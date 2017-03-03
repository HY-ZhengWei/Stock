package com.hy.stock.calculate;

import org.hy.common.Help;
import org.hy.common.xml.SerializableDef;





/**
 * 技术指标：威廉W&R的算法 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-23
 * @version     v1.0
 */
public class WR extends SerializableDef
{

    private static final long serialVersionUID = 3509975521504466070L;
    
    
    
    /** WR初始化值。当小于等于此值时，WR指标无效 */
    public  static final double $WR_InitValue = -100;
    
    
    
    /** 6天的WR指标 */
    private double wr6;
    
    /** 10天的WR指标 */
    private double wr10;
    
    
    
    public WR()
    {
        this($WR_InitValue ,$WR_InitValue);
    }
    
    
    
    public WR(double i_WR6 ,double i_WR10)
    {
        this.wr6  = i_WR6;
        this.wr10 = i_WR10;
    }
    
    
    
    /**
     * 计算WR（返回值保留4位小数）
     * 
     * WR = (Hn - C) / (Hn - Ln) * 100
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     * @param i_NewPrice      当日收盘价
     * @param i_MinPriceNDay  N天内最低价（应对比当日的最低价）
     * @param i_MaxPriceNDay  N天内最高价（应对比当日的最高价）
     * @return
     */
    public static double calcWR(double i_NewPrice 
                               ,double i_MinPriceNDay 
                               ,double i_MaxPriceNDay)
    {
        return Help.round(Help.division((i_MaxPriceNDay - i_NewPrice) ,(i_MaxPriceNDay - i_MinPriceNDay)) * 100 ,4);
    }
    
    
    
    /**
     * 获取：10天的WR指标
     */
    public double getWr10()
    {
        return wr10;
    }

    
    /**
     * 设置：10天的WR指标
     * 
     * @param wr10 
     */
    public void setWr10(double wr10)
    {
        this.wr10 = wr10;
    }

    
    /**
     * 获取：6天的WR指标
     */
    public double getWr6()
    {
        return wr6;
    }

    
    /**
     * 设置：6天的WR指标
     * 
     * @param wr6 
     */
    public void setWr6(double wr6)
    {
        this.wr6 = wr6;
    }
    
}
