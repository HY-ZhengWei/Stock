package com.hy.stock.calculate;

import java.util.List;

import org.hy.common.PartitionMap;
import org.hy.common.xml.SerializableDef;
import org.hy.common.xml.XJava;

import com.hy.stock.bean.StockInfoDay;
import com.hy.stock.dao.StockInfoDayDAO;





/**
 * 技术指标：RSI的算法 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-11
 * @version     v1.0
 */
public class RSI extends SerializableDef
{

    private static final long serialVersionUID = 4733777772736388897L;
    
    
    
    /** RSI初始化值。当小于等于此值时，RSI指标无效 */
    public  static final double $RSI_InitValue = -100;
    
    
    
    /** 6天的RSI指标 */
    private double   rsi6;
    
    /** 12天的RSI指标 */
    private double   rsi12;
    
    /** 24天的RSI指标 */
    private double   rsi24;
    
    
    
    public RSI()
    {
        this($RSI_InitValue ,$RSI_InitValue ,$RSI_InitValue);
    }
    
    
    
    public RSI(double i_RSI6 ,double i_RSI12 ,double i_RSI24)
    {
        this.rsi6  = i_RSI6;
        this.rsi12 = i_RSI12;
        this.rsi24 = i_RSI24;
    }
    
    
    
    public static double calcRSI6(String i_StockCode)
    {
        return calcRSIx(i_StockCode ,6);
    }
    
    
    
    public static double calcRSI12(String i_StockCode)
    {
        return calcRSIx(i_StockCode ,12);
    }
    
    
    
    public static double calcRSI24(String i_StockCode)
    {
        return calcRSIx(i_StockCode ,24);
    }
    
    
    
    public static double calcRSIx(String i_StockCode ,int i_DayCount)
    {
        double v_RSISMA_Strong     = SMA.calcSMA(new RSISMA_Strong(i_StockCode)     ,i_DayCount ,1);
        double v_RSISMA_StrongWeak = SMA.calcSMA(new RSISMA_StrongWeak(i_StockCode) ,i_DayCount ,1);
        
        return v_RSISMA_Strong / v_RSISMA_StrongWeak * 100;
    }
    
    
    
    /**
     * 获取：6天的RSI指标
     */
    public double getRsi6()
    {
        return rsi6;
    }


    
    /**
     * 设置：6天的RSI指标
     * 
     * @param rsi6 
     */
    public void setRsi6(double rsi6)
    {
        this.rsi6 = rsi6;
    }


    
    /**
     * 获取：12天的RSI指标
     */
    public double getRsi12()
    {
        return rsi12;
    }


    
    /**
     * 设置：12天的RSI指标
     * 
     * @param rsi12 
     */
    public void setRsi12(double rsi12)
    {
        this.rsi12 = rsi12;
    }


    
    /**
     * 获取：24天的RSI指标
     */
    public double getRsi24()
    {
        return rsi24;
    }


    
    /**
     * 设置：24天的RSI指标
     * 
     * @param rsi24 
     */
    public void setRsi24(double rsi24)
    {
        this.rsi24 = rsi24;
    }
    
}




abstract class RSISMA implements SMACalc<StockInfoDay>
{
    
    /** 股票代码 */
    private String stockCode;
    
    
    
    public RSISMA(String i_StockCode)
    {
        this.stockCode = i_StockCode;
    }
    
    
    
    /**
     * 在计算开始前，统一获取要被计算的数天数据。
     *   注：只执行一次 
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @param  i_DayCount  获取多少天的数据（一般从今天向后取数据）
     * @return
     */
    public List<StockInfoDay> calcSMABefore(int i_DayCount)
    {
        StockInfoDayDAO                    v_DAO   = (StockInfoDayDAO)XJava.getObject("StockInfoDayDAO");
        PartitionMap<String ,StockInfoDay> v_Datas = v_DAO.queryStockInfoDays(i_DayCount ,this.stockCode);
        
        return v_Datas.get(this.stockCode + "qfq");
    }
    
}





class RSISMA_Strong extends RSISMA
{
    
    public RSISMA_Strong(String i_StockCode)
    {
        super(i_StockCode);
    }
    
    

    /**
     * 返回计算SMA中，某一天的计算结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @param i_DayNo  第几天的序号。最小下标为1。
     * @return
     */
    public double calcSMA(int i_DayNo ,StockInfoDay i_DayData)
    {
        return Math.max(i_DayData.getNewPrice() - i_DayData.getYesterdayClosePrice() ,0);
    }
    
}





class RSISMA_StrongWeak extends RSISMA
{
    
    public RSISMA_StrongWeak(String i_StockCode)
    {
        super(i_StockCode);
    }
    
    

    /**
     * 返回计算SMA中，某一天的计算结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @param i_DayNo  第几天的序号。最小下标为1。
     * @return
     */
    public double calcSMA(int i_DayNo ,StockInfoDay i_DayData)
    {
        return Math.abs(i_DayData.getNewPrice() - i_DayData.getYesterdayClosePrice());
    }
    
}
