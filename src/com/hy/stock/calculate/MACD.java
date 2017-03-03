package com.hy.stock.calculate;

import java.util.ArrayList;
import java.util.List;

import org.hy.common.Help;
import org.hy.common.xml.SerializableDef;





/**
 * 技术指标：MACD的算法  
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-19
 * @version     v1.0
 */
public final class MACD extends SerializableDef
{
    private static final long serialVersionUID = -7127588502383791298L;

    /** MACD初始化值。当小于等于此值时，MACD指标无效 */
    public  static final double $MACD_InitValue = -999999;
    
    
    
    /** 快速移动平均值：12日EMA */
    private double   ema12;
    
    /** 慢速移动平均值：26日EMA */
    private double   ema26;
    
    /** 快速线：差离值(DIF) */
    private double   dif;
    
    /** 慢速线：离差平均值：9日移动平均值：9日EMA */
    private double   dea;
    
    /** MACD柱 */
    private double   bar;
    
    /** this为昨天的MACD指标，superMACD为今天的MACD指标 */
    private MACD     superMACD;
    
    
    
    public MACD()
    {
        this($MACD_InitValue ,$MACD_InitValue ,$MACD_InitValue ,$MACD_InitValue ,$MACD_InitValue);
    }
    
    
    
    public MACD(double i_EMA12 ,double i_EMA26 ,double i_DIF ,double i_DEA ,double i_Bar)
    {
        this.ema12 = i_EMA12;
        this.ema26 = i_EMA26;
        this.dif   = i_DIF;
        this.dea   = i_DEA;
        this.bar   = i_Bar;
    }
    
    
    
    /**
     * 计算MACD（整体返回）（返回值保留四位小数）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_NewPrice         今日收盘价
     * @param i_YesterdayEMA12   前一日快速移动平均值EMA(12)
     * @param i_YesterdayEMA26   前一日慢速移动平均值EMA(26)
     * @param i_YesterdayDEA     前一日离差平均值DEA
     * @return
     */
    public static MACD calcMACD(double i_NewPrice 
                               ,double i_YesterdayEMA12 
                               ,double i_YesterdayEMA26
                               ,double i_YesterdayDEA)
    {
        double v_EMA12 = calcEMA12(i_NewPrice ,i_YesterdayEMA12);
        double v_EMA26 = calcEMA26(i_NewPrice ,i_YesterdayEMA26);
        double v_DIF   = calcDIF(  v_EMA12    ,v_EMA26);
        double v_DEA   = calcDEA(  v_DIF      ,i_YesterdayDEA);
        double v_Bar   = calcBar(  v_DIF ,v_DEA);
        
        return new MACD(Help.round(v_EMA12 ,4) 
                       ,Help.round(v_EMA26 ,4)
                       ,Help.round(v_DIF   ,4)
                       ,Help.round(v_DEA   ,4)
                       ,Help.round(v_Bar   ,4));
    }
    
    
    
    public static void main(String [] i_Args)
    {
        double v_EMA12 = MACD.$MACD_InitValue;
        double v_EMA26 = MACD.$MACD_InitValue;
        double v_DIF   = MACD.$MACD_InitValue;
        double v_DEA   = 0.17;                  // 前一天的DEA
        
        
        // 2016-07-19
        v_EMA12 = 10.5565;                      // 通过inverseCalcEMA_Trend返回的"下标0：今天价格" 对应的EMA12、EMA26
        v_EMA26 = 10.3587;
        v_DIF   = Help.round(calcDIF(v_EMA12 ,v_EMA26) ,4);
        v_DEA   = Help.round(calcDEA(v_DIF   ,v_DEA)   ,4);
        
        // sh601766 2016-07-15 向后的数据
        // inverseCalcEMA_Trend(new double[]{9.35 ,9.37 ,9.43 ,9.37 ,9.23} ,new double[]{0.01 ,0.00 ,-0.01 ,-0.03 ,-0.04});
        
        // sz002267 2016-07-19 ~ 2016-06-24 之间的交易数据
        inverseCalcEMA_Trend(new double[]{10.68 ,10.72 ,10.64 ,10.70 ,10.74 ,10.73 ,10.50 ,10.47 ,10.66 ,10.34 ,10.26 ,10.31 ,10.19 ,10.30 ,10.20 ,10.15 ,10.07 ,9.91} 
                            ,new double[]{0.20  ,0.20  ,0.20  ,0.20  ,0.19  ,0.18  ,0.16  ,0.15  ,0.14  ,0.11  ,0.10  ,0.10  ,0.09  ,0.08  ,0.06  ,0.05  ,0.04  ,0.03});
    }
    
    
    
    /**
     * 逆向计算：快速移动平均值：12日EMA 和 慢速移动平均值：26日EMA
     * 
     * 此方法不能十分精确的逆向计算，而是采取的穷举模式+趋向模式
     * 
     * 返回 "下标0：今天价格" 对应的EMA12、EMA26
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-20
     * @version     v1.0
     *
     * @param i_NewPrices  下标0：今天价格
     *                     下标1：昨天价格
     *                     下标2：前天价格
     *                     
     * @param i_DIFs       下标0：今天DIF
     *                     下标1：昨天DIF
     *                     下标2：前天DIF
     */
    public static void inverseCalcEMA_Trend(double [] i_NewPrices ,double [] i_DIFs)
    {
        int        v_Day   = 0;
        List<MACD> v_MACDs = inverseCalcEMA_Exhaustivity(i_NewPrices[v_Day] ,i_DIFs[v_Day] ,i_DIFs[v_Day + 1]);
        
        for (v_Day=1; v_Day<i_NewPrices.length-1; v_Day++)
        {
            for (int v_Index=v_MACDs.size()-1; v_Index>=0; v_Index--)
            {
                MACD v_MACD = v_MACDs.get(v_Index);
                
                double v_ThreeDaysAgoEMA12 = Help.round(inverseCalcYesterdayEMA12(i_NewPrices[v_Day] ,v_MACD.getEma12()) ,4);
                double v_ThreeDaysAgoEMA26 = Help.round(inverseCalcYesterdayEMA26(i_NewPrices[v_Day] ,v_MACD.getEma26()) ,4);
                double v_ThreeDaysAgoDIF   = Help.round(calcDIF(v_ThreeDaysAgoEMA12 ,v_ThreeDaysAgoEMA26) ,2);
                
                if ( v_ThreeDaysAgoDIF != i_DIFs[v_Day + 1] )
                {
                    v_MACDs.remove(v_Index);
                }
                else
                {
                    MACD v_ThreeDaysAgoMACD = new MACD(v_ThreeDaysAgoEMA12 ,v_ThreeDaysAgoEMA26 ,0 ,0 ,0);
                    v_ThreeDaysAgoMACD.setSuperMACD(v_MACD);
                    v_MACDs.remove(v_Index);
                    v_MACDs.add(v_ThreeDaysAgoMACD);
                }
            }
        }
        
        
        if ( v_MACDs.size() == 1 )
        {
            System.out.println("\n\n--");
            
            MACD v_MACD = v_MACDs.get(0);
            while ( v_MACD.getSuperMACD() != null )
            {
                System.out.println("-- " + v_MACD.toString());
                v_MACD = v_MACD.getSuperMACD();
            }
            
            System.out.println("-- " + v_MACD.toString());
        }
        else
        {
            Help.print(v_MACDs);
        }
    }
    
    
    
    /**
     * 逆向计算：快速移动平均值：12日EMA 和 慢速移动平均值：26日EMA
     * 
     * 此方法不能十分精确的逆向计算，而是采取的穷举模式
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-20
     * @version     v1.0
     *
     * @param i_NewPrice
     * @param i_DIF
     * @param i_YesterdayDIF
     * @return
     */
    public static List<MACD> inverseCalcEMA_Exhaustivity(double i_NewPrice ,double i_DIF ,double i_YesterdayDIF)
    {
        List<MACD> v_MACDs      = new ArrayList<MACD>();
        double     v_Step       = 0.001D;  // 步数大小：穷举精度
        double     v_PriceScope = 1;       // 穷举的价格范围幅度
        
        for (double v_YesterdayEMA12=i_NewPrice+v_PriceScope; v_YesterdayEMA12>=i_NewPrice-v_PriceScope; v_YesterdayEMA12=Help.round(v_YesterdayEMA12-v_Step ,3))
        {
            double v_EMA12 = calcEMA12(i_NewPrice ,v_YesterdayEMA12);
            
            for (double v_YesterdayEMA26=i_NewPrice+v_PriceScope; v_YesterdayEMA26>=i_NewPrice-v_PriceScope; v_YesterdayEMA26=Help.round(v_YesterdayEMA26-v_Step ,3))
            {
                double v_EMA26        = calcEMA26(i_NewPrice     ,v_YesterdayEMA26);
                double v_DIF          = calcDIF(v_EMA12          ,v_EMA26) ;
                double v_YesterdayDIF = calcDIF(v_YesterdayEMA12 ,v_YesterdayEMA26);
                
                if ( i_DIF == Help.round(v_DIF ,2) )
                {
                    if ( i_YesterdayDIF == Help.round(v_YesterdayDIF ,2) )
                    {
                        MACD v_YesterdayMACD = new MACD(v_YesterdayEMA12 ,v_YesterdayEMA26 ,0 ,0 ,0);
                        v_YesterdayMACD.setSuperMACD(new MACD(v_EMA12 ,v_EMA26 ,Help.round(v_DIF ,4) ,0 ,0));
                        
                        v_MACDs.add(v_YesterdayMACD);
                        System.out.println("-- YesterdayEMA12 = " + v_YesterdayEMA12 + "    YesterdayEMA26 = " + v_YesterdayEMA26);
                    }
                }
            }
        }
        
        return v_MACDs;
    }
    
    
    
    /**
     * 逆向计算：快速移动平均值（昨天的）：12日EMA
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-20
     * @version     v1.0
     *
     * @param i_NewPrice
     * @param i_EMA12
     * @return
     */
    public static double inverseCalcYesterdayEMA12(double i_NewPrice ,double i_EMA12)
    {
        return (i_EMA12 * 13 - i_NewPrice * 2) / 11;
    }
    
    
    
    /**
     * 逆向计算：慢速移动平均值（昨天的）：26日EMA
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-20
     * @version     v1.0
     *
     * @param i_NewPrice
     * @param i_EMA26
     * @return
     */
    public static double inverseCalcYesterdayEMA26(double i_NewPrice ,double i_EMA26)
    {
        return (i_EMA26 * 27 - i_NewPrice * 2) / 25;
    }
    
    
    
    /**
     * 快速移动平均值：12日EMA的计算
     * 
     * EMA(12) = 前一日EMA(12) * 11 / 13 + 今日收盘价 * 2 / 13
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_NewPrice        今日收盘价
     * @param i_YesterdayEMA12  前一日EMA(12)
     * @return
     */
    public static double calcEMA12(double i_NewPrice ,double i_YesterdayEMA12)
    {
        return (i_YesterdayEMA12 * 11 + i_NewPrice * 2) / 13;
    }
    
    
    
    /**
     * 慢速移动平均值：26日EMA的计算
     * 
     * EMA(26) = 前一日EMA(26) * 25 / 27 + 今日收盘价 * 2 / 27
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_NewPrice        今日收盘价
     * @param i_YesterdayEMA26  前一日EMA(26)
     * @return
     */
    public static double calcEMA26(double i_NewPrice ,double i_YesterdayEMA26)
    {
        return (i_YesterdayEMA26 * 25 + i_NewPrice * 2) / 27;
    }
    
    
    
    /**
     * 快速线：差离值(DIF)的计算
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_EMA12   12日EMA
     * @param i_EMA26   26日EMA
     * @return
     */
    public static double calcDIF(double i_EMA12 ,double i_EMA26)
    {
        return i_EMA12 - i_EMA26;
    }
    
    
    
    /**
     * 慢速线：9日移动平均值：9日EMA的计算
     * 
     * 据差离值计算其9日的EMA，即离差平均值。为了不与指标原名相混淆，此值又名DEA或DEM。
     * 
     * 今日DEA = 前一日DEA * 8 / 10 + 今日DIF * 2 / 10
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_DIF            今日DIF
     * @param i_YesterdayDEA   前一日DEA
     * @return
     */
    public static double calcDEA(double i_DIF ,double i_YesterdayDEA)
    {
        return i_YesterdayDEA * 8 / 10 + i_DIF * 2 / 10;
    }
    
    
    
    /**
     * 计算MACD柱状图(线)
     * 
     * (DIF - DEA) * 2
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_DIF   差离值
     * @param i_DEA   离差平均值
     * @return
     */
    public static double calcBar(double i_DIF ,double i_DEA)
    {
        return (i_DIF - i_DEA) * 2;
    }


    
    /**
     * 获取：快速移动平均值：12日EMA
     */
    public double getEma12()
    {
        return ema12;
    }


    
    /**
     * 设置：快速移动平均值：12日EMA
     * 
     * @param ema12 
     */
    public void setEma12(double ema12)
    {
        this.ema12 = ema12;
    }


    
    /**
     * 获取：慢速移动平均值：26日EMA
     */
    public double getEma26()
    {
        return ema26;
    }


    
    /**
     * 设置：慢速移动平均值：26日EMA
     * 
     * @param ema26 
     */
    public void setEma26(double ema26)
    {
        this.ema26 = ema26;
    }


    
    /**
     * 获取：快速线：差离值(DIF)
     */
    public double getDif()
    {
        return dif;
    }



    /**
     * 设置：差离值(DIF)
     * 
     * @param dif 
     */
    public void setDif(double dif)
    {
        this.dif = dif;
    }


    
    /**
     * 获取：慢速线：离差平均值：9日移动平均值：9日EMA
     */
    public double getDea()
    {
        return dea;
    }


    
    /**
     * 设置：离差平均值：9日移动平均值：9日EMA
     * 
     * @param dea 
     */
    public void setDea(double dea)
    {
        this.dea = dea;
    }


    
    /**
     * 获取：MACD柱
     */
    public double getBar()
    {
        return bar;
    }


    
    /**
     * 设置：MACD柱
     * 
     * @param bar 
     */
    public void setBar(double bar)
    {
        this.bar = bar;
    }



    /**
     * 获取：this为昨天的MACD指标，superMACD为今天的MACD指标
     */
    public MACD getSuperMACD()
    {
        return superMACD;
    }


    
    /**
     * 设置：this为昨天的MACD指标，superMACD为今天的MACD指标
     * 
     * @param superMACD 
     */
    public void setSuperMACD(MACD superMACD)
    {
        this.superMACD = superMACD;
    }
    
}
