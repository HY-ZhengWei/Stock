package com.hy.stock.calculate;

import org.hy.common.xml.SerializableDef;





/**
 * 技术指标：MA移动平均线(均线)
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-24
 * @version     v1.0
 */
public class MA extends SerializableDef
{
    
    private static final long serialVersionUID = -7766662859553788169L;
    
    
    
    /** MA初始化值。当小于等于此值时，MA指标无效 */
    public  static final double $MA_InitValue = 0;
    
    

    /** 5日均线 */
    private double ma5;
    
    /** 10日均线 */
    private double ma10;
    
    /** 20日均线 */
    private double ma20;
    
    /** 30日均线 */
    private double ma30;
    
    /** 60日均线 */
    private double ma60;
    
    /** 108日均线 */
    private double ma108;

    
    
    public MA()
    {
        this($MA_InitValue ,$MA_InitValue ,$MA_InitValue ,$MA_InitValue ,$MA_InitValue ,$MA_InitValue);
    }
    
    
    
    public MA(double i_MA5 ,double i_MA10 ,double i_MA20 ,double i_MA30 ,double i_MA60 ,double i_MA108)
    {
        this.ma5   = i_MA5;
        this.ma10  = i_MA10;
        this.ma20  = i_MA20;
        this.ma30  = i_MA30;
        this.ma60  = i_MA60;
        this.ma108 = i_MA108;
    }
    
    
    
    /**
     * 获取：5日均线
     */
    public double getMa5()
    {
        return ma5;
    }

    
    /**
     * 设置：5日均线
     * 
     * @param ma5 
     */
    public void setMa5(double ma5)
    {
        this.ma5 = ma5;
    }

    
    /**
     * 获取：10日均线
     */
    public double getMa10()
    {
        return ma10;
    }

    
    /**
     * 设置：10日均线
     * 
     * @param ma10 
     */
    public void setMa10(double ma10)
    {
        this.ma10 = ma10;
    }

    
    /**
     * 获取：20日均线
     */
    public double getMa20()
    {
        return ma20;
    }

    
    /**
     * 设置：20日均线
     * 
     * @param ma20 
     */
    public void setMa20(double ma20)
    {
        this.ma20 = ma20;
    }

    
    /**
     * 获取：30日均线
     */
    public double getMa30()
    {
        return ma30;
    }

    
    /**
     * 设置：30日均线
     * 
     * @param ma30 
     */
    public void setMa30(double ma30)
    {
        this.ma30 = ma30;
    }

    
    /**
     * 获取：60日均线
     */
    public double getMa60()
    {
        return ma60;
    }

    
    /**
     * 设置：60日均线
     * 
     * @param ma60 
     */
    public void setMa60(double ma60)
    {
        this.ma60 = ma60;
    }

    
    /**
     * 获取：108日均线
     */
    public double getMa108()
    {
        return ma108;
    }

    
    /**
     * 设置：108日均线
     * 
     * @param ma108 
     */
    public void setMa108(double ma108)
    {
        this.ma108 = ma108;
    }
    
}
