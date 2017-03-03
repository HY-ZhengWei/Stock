package com.hy.stock.calculate;

import org.hy.common.Help;
import org.hy.common.xml.SerializableDef;





/**
 * 技术指标：KDJ的算法 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-19
 * @version     v1.0
 */
public final class KDJ extends SerializableDef
{
    private static final long serialVersionUID = -927982463503501838L;



    /** KDJ初始化值。当小于等于此值时，KDJ指标无效 */
    public  static final double $KDJ_InitValue = -100;
    
    
    
    /** KDJ指标：K值 */
    private double   k;
    
    /** KDJ指标：D值 */
    private double   d;
    
    /** KDJ指标：J值 */
    private double   j;
    
    
    
    public KDJ()
    {
        this($KDJ_InitValue ,$KDJ_InitValue ,$KDJ_InitValue);
    }
    
    
    
    public KDJ(double i_K ,double i_D ,double i_J)
    {
        this.k = i_K;
        this.d = i_D;
        this.j = i_J;
    }
    
    
    
    /**
     * 计算KDJ（整体返回）（返回值保留4位小数）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_NewPrice      当日收盘价
     * @param i_MinPriceNDay  N天内最低价（应对比当日的最低价）
     * @param i_MaxPriceNDay  N天内最高价（应对比当日的最高价）
     * @param i_YesterdayK    昨日K值
     * @param i_YesterdayD    昨日D值
     * @return
     */
    public static KDJ calcKDJ(double i_NewPrice 
                             ,double i_MinPriceNDay 
                             ,double i_MaxPriceNDay 
                             ,double i_YesterdayK 
                             ,double i_YesterdayD)
    {
        double v_RSV = calcRSV(i_NewPrice ,i_MinPriceNDay ,i_MaxPriceNDay);
        double v_K   = calcK(v_RSV ,i_YesterdayK);
        double v_D   = calcD(v_K   ,i_YesterdayD);
        double v_J   = calcJ(v_K   ,v_D);
        
        return new KDJ(Help.round(v_K ,4) 
                      ,Help.round(v_D ,4)
                      ,Help.round(v_J ,4));
    }
    
    
    
    /**
     * 计算RSV：未成熟随机值
     * 
     * RSV = ((当日收盘价 - N天内最低价) / (N天内最高价 - N天内最低价)) * 100
     * 
     * N天：一般指9天
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_NewPrice      当日收盘价
     * @param i_MinPriceNDay  N天内最低价（应对比当日的最低价）
     * @param i_MaxPriceNDay  N天内最高价（应对比当日的最高价）
     * @return
     */
    public static double calcRSV(double i_NewPrice ,double i_MinPriceNDay ,double i_MaxPriceNDay)
    {
        return Help.division(i_NewPrice - i_MinPriceNDay ,i_MaxPriceNDay - i_MinPriceNDay) * 100;
    }
    
    
    
    /**
     * 逆向计算N天内最低价
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_NewPrice      当日收盘价
     * @param i_MaxPriceNDay  N天内最高价
     * @param i_RSV           未成熟随机值
     * @return
     */
    public static double inverseCalcMinPriceNDay(double i_NewPrice ,double i_MaxPriceNDay ,double i_RSV)
    {
        return ((i_RSV / 100) * i_MaxPriceNDay - i_NewPrice) / ((i_RSV / 100) - 1);
    }
    
    
    
    /**
     * 逆向计算RSV
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_K           今日K值
     * @param i_YesterdayK  昨日K值
     * @return
     */
    public static double inverseCalcRSV(double i_K ,double i_YesterdayK)
    {
        return (i_K - (i_YesterdayK * 2 / 3)) * 3;
    }
    
    
    
    /**
     * 计算K值
     * 
     * 今日K值 = (昨日K值 * 2 / 3) + (今日RSV / 3)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_RSV         未成熟随机值
     * @param i_YesterdayK  昨日K值
     * @return
     */
    public static double calcK(double i_RSV ,double i_YesterdayK)
    {
        return (i_YesterdayK * 2 / 3) + (i_RSV / 3);
    }
    
    
    
    /**
     * 计算D值
     * 
     * 今日D值 = (昨日D值 * 2 / 3) + (今日K值 / 3)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_K           今日K值
     * @param i_YesterdayD  昨日D值
     * @return
     */
    public static double calcD(double i_K ,double i_YesterdayD)
    {
        return (i_YesterdayD * 2 / 3) + (i_K / 3);
    }
    
    
    
    /**
     * 计算J值
     * 
     * J = 3 * K - 2 * D
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param i_K  今日K值
     * @param i_D  今日D值
     * @return
     */
    public static double calcJ(double i_K ,double i_D)
    {
        return 3 * i_K - 2 * i_D;
    }
    
    
    
    /**
     * 获取：KDJ指标：K值
     */
    public double getK()
    {
        return k;
    }


    
    /**
     * 设置：KDJ指标：K值
     * 
     * @param k 
     */
    public void setK(double k)
    {
        this.k = k;
    }


    
    /**
     * 获取：KDJ指标：D值
     */
    public double getD()
    {
        return d;
    }


    
    /**
     * 设置：KDJ指标：D值
     * 
     * @param d 
     */
    public void setD(double d)
    {
        this.d = d;
    }


    
    /**
     * 获取：KDJ指标：J值
     */
    public double getJ()
    {
        return j;
    }


    
    /**
     * 设置：KDJ指标：J值
     * 
     * @param j 
     */
    public void setJ(double j)
    {
        this.j = j;
    }
    
}
