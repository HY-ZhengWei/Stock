package com.hy.stock.calculate;

import java.util.List;





public interface SMACalc<O>
{
    
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
    public List<O> calcSMABefore(int i_DayCount);
    
    
    
    /**
     * 返回计算SMA中，某一天的计算结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @param i_DayNo    第几天的序号。最小下标为1。
     * @param i_DayData  第几天的数据对象。
     * @return
     */
    public double calcSMA(int i_DayNo ,O i_DayData);
    
}
