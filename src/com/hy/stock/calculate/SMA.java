package com.hy.stock.calculate;

import java.util.List;





public class SMA
{
    
    /**
     * [M*X+(N-M)*Y')]/N
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @param i_SMACalc    X:获取某一天数值的接口
     * @param i_DayCount   N:计算多少天的移动平均值
     * @param i_Weight     M:权重
     * @return
     */
    public static <O> double calcSMA(SMACalc<O> i_SMACalc ,int i_DayCount ,double i_Weight)
    {
        double  v_SMA          = 0D;
        double  v_YesterdaySMA = 0D;
        List<O> v_Datas        = i_SMACalc.calcSMABefore(i_DayCount);
        
        for (int v_DayNo=Math.min(i_DayCount ,v_Datas.size()); v_DayNo>=1; v_DayNo--)
        {
            double v_X = i_SMACalc.calcSMA(v_DayNo ,v_Datas.get(v_DayNo-1));
            
            v_SMA = (i_Weight * v_X + (i_DayCount - i_Weight) * v_YesterdaySMA) / i_DayCount;
            v_YesterdaySMA = v_SMA;
        }
        
        return v_SMA;
    }
    
}
