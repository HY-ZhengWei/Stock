package com.hy.stock.bean;

import org.hy.common.Date;





/**
 * 股票每天最终交易数据
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-12
 * @version     v1.0
 */
public class StockInfoDay extends StockInfo
{

    private static final long serialVersionUID = -3036774269076341651L;
    
    
    /** 每天最终交易数据的数据类型（除权、不复权、前复权、后复权） */
    private String   dataType;
    
    /** 交易日期(YYYY-MM-DD) */
    private Date     stockDay;
    
    /** 交易日期的序列号。首日上市首个交易日为1、上市第二个交易日为2，依次类推 */
    private Integer  stockDayNo;
    
    /** 星期几 */
    private Integer  weekNo;
    
    
    
    /** 最近X个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标 */
    private Double   minPriceXDay;
    
    /** 最近X个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标 */
    private Double   maxPriceXDay;
    
    /** 最近Y个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标 */
    private Double   minPriceYDay;
    
    /** 最近Y个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标 */
    private Double   maxPriceYDay;
    
    /** 最近Z个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标 */
    private Double   minPriceZDay;
    
    /** 最近Z个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标 */
    private Double   maxPriceZDay;
    
    
    
    /** KDJ指标：K值 */
    private Double   k;
    
    /** KDJ指标：D值 */
    private Double   d;
    
    /** KDJ指标：J值 */
    private Double   j;
    

    
    /** MACD指标：快速移动平均值：12日EMA */
    private Double   ema12;
    
    /** MACD指标：慢速移动平均值：26日EMA */
    private Double   ema26;
    
    /** MACD指标：快速线：差离值 */
    private Double   dif;
    
    /** MACD指标：慢速线：9日移动平均值：9日EMA */
    private Double   dea;
    
    /** MACD指标：MACD柱 */
    private Double   macd;
    
    
    
    /** 10天的WR指标 */
    private Double wr10;
    
    /** 6天的WR指标 */
    private Double wr6;
    
    
    
    /** 5日均线 */
    private Double ma5;
    
    /** 10日均线 */
    private Double ma10;
    
    /** 20日均线 */
    private Double ma20;
    
    /** 30日均线 */
    private Double ma30;
    
    /** 60日均线 */
    private Double ma60;
    
    /** 108日均线 */
    private Double ma108;
    
    
    
    public StockInfoDay()
    {
        super();
    }
    
    
    
    public StockInfoDay(Object i_InitDatas)
    {
        this.init(i_InitDatas);
    }
    
    
    
    /**
     * 获取：每天最终交易数据的数据类型（除权、不复权、前复权、后复权）
     */
    public String getDataType()
    {
        return dataType;
    }

    
    /**
     * 设置：每天最终交易数据的数据类型（除权、不复权、前复权、后复权）
     * 
     * @param dataType 
     */
    public void setDataType(String dataType)
    {
        this.dataType = dataType;
    }


    /**
     * 获取：交易日期(YYYY-MM-DD)
     */
    public Date getStockDay()
    {
        return stockDay;
    }

    
    /**
     * 设置：交易日期(YYYY-MM-DD)
     * 
     * @param stockDay 
     */
    public void setStockDay(Date stockDay)
    {
        this.stockDay = stockDay;
    }

    
    /**
     * 获取：交易日期的序列号。首日上市首个交易日为1、上市第二个交易日为2，依次类推
     */
    public Integer getStockDayNo()
    {
        return stockDayNo;
    }

    
    /**
     * 设置：交易日期的序列号。首日上市首个交易日为1、上市第二个交易日为2，依次类推
     * 
     * @param stockDayNo 
     */
    public void setStockDayNo(Integer stockDayNo)
    {
        this.stockDayNo = stockDayNo;
    }


    /**
     * 获取：星期几
     */
    public synchronized Integer getWeekNo()
    {
        if ( this.stockDay != null && this.weekNo == null )
        {
            this.weekNo = this.stockDay.getWeek();
        }
        
        return this.weekNo;
    }


    /**
     * 设置：星期几
     * 
     * @param weekNo 
     */
    public void setWeekNo(Integer weekNo)
    {
        this.weekNo = weekNo;
    }

    
    /**
     * 获取：KDJ指标：K值
     */
    public Double getK()
    {
        return k;
    }

    
    /**
     * 设置：KDJ指标：K值
     * 
     * @param k 
     */
    public void setK(Double k)
    {
        this.k = k;
    }

    
    /**
     * 获取：KDJ指标：D值
     */
    public Double getD()
    {
        return d;
    }

    
    /**
     * 设置：KDJ指标：D值
     * 
     * @param d 
     */
    public void setD(Double d)
    {
        this.d = d;
    }

    
    /**
     * 获取：KDJ指标：J值
     */
    public Double getJ()
    {
        return j;
    }

    
    /**
     * 设置：KDJ指标：J值
     * 
     * @param j 
     */
    public void setJ(Double j)
    {
        this.j = j;
    }

    
    /**
     * 获取：最近X个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     */
    public Double getMinPriceXDay()
    {
        return minPriceXDay;
    }

    
    /**
     * 设置：最近X个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     * 
     * @param minPriceXDay 
     */
    public void setMinPriceXDay(Double minPriceXDay)
    {
        this.minPriceXDay = minPriceXDay;
    }


    /**
     * 获取：最近X个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     */
    public Double getMaxPriceXDay()
    {
        return maxPriceXDay;
    }


    /**
     * 设置：最近X个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     * 
     * @param maxPriceXDay 
     */
    public void setMaxPriceXDay(Double maxPriceXDay)
    {
        this.maxPriceXDay = maxPriceXDay;
    }

    
    /**
     * 获取：最近Y个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     */
    public Double getMinPriceYDay()
    {
        return minPriceYDay;
    }

    
    /**
     * 设置：最近Y个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     * 
     * @param minPriceYDay 
     */
    public void setMinPriceYDay(Double minPriceYDay)
    {
        this.minPriceYDay = minPriceYDay;
    }

    
    /**
     * 获取：最近Y个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     */
    public Double getMaxPriceYDay()
    {
        return maxPriceYDay;
    }


    /**
     * 设置：最近Y个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     * 
     * @param maxPriceYDay 
     */
    public void setMaxPriceYDay(Double maxPriceYDay)
    {
        this.maxPriceYDay = maxPriceYDay;
    }

    
    /**
     * 获取：最近Z个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     */
    public Double getMinPriceZDay()
    {
        return minPriceZDay;
    }

    
    /**
     * 设置：最近Z个交易日的最小价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     * 
     * @param minPriceZDay 
     */
    public void setMinPriceZDay(Double minPriceZDay)
    {
        this.minPriceZDay = minPriceZDay;
    }

    
    /**
     * 获取：最近Z个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     */
    public Double getMaxPriceZDay()
    {
        return maxPriceZDay;
    }

    
    /**
     * 设置：最近Z个交易日的最大价格。此值不保存在数据库中，只用于动态计算KDJ、WR指标
     * 
     * @param maxPriceZDay 
     */
    public void setMaxPriceZDay(Double maxPriceZDay)
    {
        this.maxPriceZDay = maxPriceZDay;
    }


    /**
     * 获取：MACD指标：快速移动平均值：12日EMA
     */
    public Double getEma12()
    {
        return ema12;
    }

    
    /**
     * 设置：MACD指标：快速移动平均值：12日EMA
     * 
     * @param ema12 
     */
    public void setEma12(Double ema12)
    {
        this.ema12 = ema12;
    }

    
    /**
     * 获取：MACD指标：慢速移动平均值：26日EMA
     */
    public Double getEma26()
    {
        return ema26;
    }

    
    /**
     * 设置：MACD指标：慢速移动平均值：26日EMA
     * 
     * @param ema26 
     */
    public void setEma26(Double ema26)
    {
        this.ema26 = ema26;
    }

    
    /**
     * 获取：MACD指标：快速线：差离值
     */
    public Double getDif()
    {
        return dif;
    }

    
    /**
     * 设置：MACD指标：快速线：差离值
     * 
     * @param dif 
     */
    public void setDif(Double dif)
    {
        this.dif = dif;
    }

    
    /**
     * 获取：MACD指标：慢速线：9日移动平均值：9日EMA
     */
    public Double getDea()
    {
        return dea;
    }

    
    /**
     * 设置：MACD指标：慢速线：9日移动平均值：9日EMA
     * 
     * @param dea 
     */
    public void setDea(Double dea)
    {
        this.dea = dea;
    }


    
    /**
     * 获取：MACD指标：MACD柱
     */
    public Double getMacd()
    {
        return macd;
    }


    
    /**
     * 设置：MACD指标：MACD柱
     * 
     * @param macd 
     */
    public void setMacd(Double macd)
    {
        this.macd = macd;
    }



    /**
     * 获取：10天的WR指标
     */
    public Double getWr10()
    {
        return wr10;
    }


    
    /**
     * 设置：10天的WR指标
     * 
     * @param wr10 
     */
    public void setWr10(Double wr10)
    {
        this.wr10 = wr10;
    }


    
    /**
     * 获取：6天的WR指标
     */
    public Double getWr6()
    {
        return wr6;
    }


    
    /**
     * 设置：6天的WR指标
     * 
     * @param wr6 
     */
    public void setWr6(Double wr6)
    {
        this.wr6 = wr6;
    }


    
    /**
     * 获取：5日均线
     */
    public Double getMa5()
    {
        return ma5;
    }


    
    /**
     * 设置：5日均线
     * 
     * @param ma5 
     */
    public void setMa5(Double ma5)
    {
        this.ma5 = ma5;
    }


    
    /**
     * 获取：10日均线
     */
    public Double getMa10()
    {
        return ma10;
    }


    
    /**
     * 设置：10日均线
     * 
     * @param ma10 
     */
    public void setMa10(Double ma10)
    {
        this.ma10 = ma10;
    }


    
    /**
     * 获取：20日均线
     */
    public Double getMa20()
    {
        return ma20;
    }


    
    /**
     * 设置：20日均线
     * 
     * @param ma20 
     */
    public void setMa20(Double ma20)
    {
        this.ma20 = ma20;
    }


    
    /**
     * 获取：30日均线
     */
    public Double getMa30()
    {
        return ma30;
    }


    
    /**
     * 设置：30日均线
     * 
     * @param ma30 
     */
    public void setMa30(Double ma30)
    {
        this.ma30 = ma30;
    }


    
    /**
     * 获取：60日均线
     */
    public Double getMa60()
    {
        return ma60;
    }


    
    /**
     * 设置：60日均线
     * 
     * @param ma60 
     */
    public void setMa60(Double ma60)
    {
        this.ma60 = ma60;
    }


    
    /**
     * 获取：108日均线
     */
    public Double getMa108()
    {
        return ma108;
    }


    
    /**
     * 设置：108日均线
     * 
     * @param ma108 
     */
    public void setMa108(Double ma108)
    {
        this.ma108 = ma108;
    }
    
}
