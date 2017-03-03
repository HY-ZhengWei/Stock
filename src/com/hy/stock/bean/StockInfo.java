package com.hy.stock.bean;

import org.hy.common.Date;

import com.hy.stock.calculate.KDJ;
import com.hy.stock.calculate.MA;
import com.hy.stock.calculate.MACD;
import com.hy.stock.calculate.WR;
import com.hy.stock.common.BaseBean;





/**
 * 股票信息
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-07
 * @version     v1.0
 */
public class StockInfo extends BaseBean
{
    private static final long serialVersionUID = 1260351103212021686L;
    
    /** 主键 */
    private String  id;
    
    /** 分组名称 */
    private String  groupName;

    /** 股票名称 */
    private String  stockName;
    
    /** 股票代码 */
    private String  stockCode;
    
    /** 昨收 */
    private Double  yesterdayClosePrice;
    
    /** 今开 */
    private Double  todayOpenPrice;
    
    /** 最新报价 */
    private Double  newPrice; 
    
    /** 今日最高 */
    private Double  maxPrice;
    
    /** 今日最低 */
    private Double  minPrice;
    
    /** 涨跌额 */
    private Double  upDownPrice;
    
    /** 涨跌幅 */
    private Double  upDownRange;
    
    /** 成交额(万元) */
    private Double  bargainSum;
    
    /** 成交量(手) */
    private Integer bargainSize;
    
    /** 换手率 */
    private Double  turnoverRate;
    
    /** 振幅 */
    private Double  swingRate;
    
    /** 外盘 */
    private Integer outSize;
    
    /** 内盘 */
    private Integer inSize;
    
    /** 市盈率 */
    private Double  priceEarningRatio;
    
    /** 市净率 */
    private Double  priceBookValueRatio;
    
    /** 交易时间 */
    private Date    time;
    
    /** 写入数据库的时间 */
    private Date    dbTime;
    
    /** KDJ指标。此值不保存在数据库中，只用于动态计算KDJ指标 */
    private KDJ     kdjValue;
    
    /** KDJ（昨天的）指标。此值不保存在数据库中，只用于动态计算KDJ指标 */
    private KDJ     kdjValueYesterday;
    
    /** MACD指标。此值不保存在数据库中，只用于动态计算MACD指标 */
    private MACD    macdValue;
    
    /** MACD指标（昨天的）。此值不保存在数据库中，只用于动态计算MACD指标 */
    private MACD    macdValueYesterday;
    
    /** WR指标。此值不保存在数据库中，只用于动态计算WR指标 */
    private WR      wrValue;
    
    /** WR指标（昨天的）。此值不保存在数据库中，只用于动态计算WR指标 */
    private WR      wrValueYesterday;
    
    /** MA移动平均线(均线)指标。此值不保存在数据库中，只用于动态计算MA指标 */
    private MA      maValue;
    
    /** MA移动平均线(均线)指标（昨天的）。此值不保存在数据库中，只用于动态计算MA指标 */
    private MA      maValueYesterday;

    
    
    public StockInfo()
    {
        
    }
    
    
    
    public StockInfo(String [] i_Datas ,int [] i_ToJavaIndexes)
    {
        for (int v_Index=0; v_Index<i_Datas.length; v_Index++)
        {
            this.setPropertyValue(i_ToJavaIndexes[v_Index] ,i_Datas[v_Index]);
        }
    }
    
    
    
    /**
     * 获取：主键
     */
    public String getId()
    {
        return id;
    }

    
    /**
     * 设置：主键
     * 
     * @param id 
     */
    public void setId(String id)
    {
        this.id = id;
    }


    /**
     * 获取：分组名称
     */
    public String getGroupName()
    {
        return groupName;
    }

    
    /**
     * 设置：分组名称
     * 
     * @param groupName 
     */
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }


    /**
     * 获取：股票名称
     */
    public String getStockName()
    {
        return stockName;
    }

    
    /**
     * 设置：股票名称
     * 
     * @param stockName 
     */
    public void setStockName(String stockName)
    {
        this.stockName = stockName;
    }

    
    /**
     * 获取：股票代码
     */
    public String getStockCode()
    {
        return stockCode;
    }

    
    /**
     * 设置：股票代码
     * 
     * @param stockCode 
     */
    public void setStockCode(String stockCode)
    {
        this.stockCode = stockCode;
    }

    
    /**
     * 获取：昨收
     */
    public Double getYesterdayClosePrice()
    {
        return yesterdayClosePrice;
    }

    
    /**
     * 设置：昨收
     * 
     * @param yesterdayClosePrice 
     */
    public void setYesterdayClosePrice(Double yesterdayClosePrice)
    {
        this.yesterdayClosePrice = yesterdayClosePrice;
    }

    
    /**
     * 获取：今开
     */
    public Double getTodayOpenPrice()
    {
        return todayOpenPrice;
    }

    
    /**
     * 设置：今开
     * 
     * @param todayOpenPrice 
     */
    public void setTodayOpenPrice(Double todayOpenPrice)
    {
        this.todayOpenPrice = todayOpenPrice;
    }

    
    /**
     * 获取：最新报价
     */
    public Double getNewPrice()
    {
        return newPrice;
    }

    
    /**
     * 设置：最新报价
     * 
     * @param newPrice 
     */
    public void setNewPrice(Double newPrice)
    {
        this.newPrice = newPrice;
    }
    
    
    /**
     * 获取：最新报价
     */
    public String getHtmlNewPrice()
    {
        if ( null == this.newPrice 
          || null == this.yesterdayClosePrice )
        {
            return "";
        }
        
        if ( this.newPrice > this.yesterdayClosePrice )
        {
            return this.fontColorRed(this.newPrice);
        }
        else if ( this.newPrice < this.yesterdayClosePrice )
        {
            return this.fontColorGreen(this.newPrice);
        }
        else
        {
            return this.newPrice.toString();
        }
    }

    
    /**
     * 设置：最新报价
     * 
     * @param newPrice 
     */
    public void setHtmlNewPrice(String htmlNewPrice)
    {
        // Nothing.
    }

    
    /**
     * 获取：今日最高
     */
    public Double getMaxPrice()
    {
        return maxPrice;
    }

    
    /**
     * 设置：今日最高
     * 
     * @param maxPrice 
     */
    public void setMaxPrice(Double maxPrice)
    {
        this.maxPrice = maxPrice;
    }

    
    /**
     * 获取：今日最低
     */
    public Double getMinPrice()
    {
        return minPrice;
    }

    
    /**
     * 设置：今日最低
     * 
     * @param minPrice 
     */
    public void setMinPrice(Double minPrice)
    {
        this.minPrice = minPrice;
    }

    
    /**
     * 获取：涨跌额
     */
    public Double getUpDownPrice()
    {
        return upDownPrice;
    }

    
    /**
     * 设置：涨跌额
     * 
     * @param upDownprice 
     */
    public void setUpDownPrice(Double upDownPrice)
    {
        this.upDownPrice = upDownPrice;
    }

    
    /**
     * 获取：涨跌幅
     */
    public Double getUpDownRange()
    {
        return upDownRange;
    }

    
    /**
     * 设置：涨跌幅
     * 
     * @param upDownRange 
     */
    public void setUpDownRange(Double upDownRange)
    {
        this.upDownRange = upDownRange;
    }
    
    
    /**
     * 获取：涨跌幅
     */
    public String getHtmlUpDownRange()
    {
        if ( null == this.upDownRange )
        {
            return "";
        }
        
        if ( this.upDownRange > 0 )
        {
            return this.fontColorRed(this.upDownRange + "%");
        }
        else if ( this.upDownRange < 0 )
        {
            return this.fontColorGreen(this.upDownRange + "%");
        }
        else
        {
            return this.upDownRange.toString();
        }
    }

    
    /**
     * 设置：涨跌幅
     * 
     * @param newPrice 
     */
    public void setHtmlUpDownRange(String htmlUpDownRange)
    {
        // Nothing.
    }


    
    /**
     * 获取：成交额(万元)
     */
    public Double getBargainSum()
    {
        return bargainSum;
    }

    
    /**
     * 设置：成交额(万元)
     * 
     * @param bargainSum
     */
    public void setBargainSum(Double bargainSum)
    {
        this.bargainSum = bargainSum;
    }

    
    /**
     * 获取：成交量(手)
     */
    public Integer getBargainSize()
    {
        return bargainSize;
    }

    
    /**
     * 设置：成交量(手)
     * 
     * @param bargainSize 
     */
    public void setBargainSize(Integer bargainSize)
    {
        this.bargainSize = bargainSize;
    }

    
    /**
     * 获取：换手率
     */
    public Double getTurnoverRate()
    {
        return turnoverRate;
    }

    
    /**
     * 设置：换手率
     * 
     * @param turnoverRate 
     */
    public void setTurnoverRate(Double turnoverRate)
    {
        this.turnoverRate = turnoverRate;
    }

    
    /**
     * 获取：振幅
     */
    public Double getSwingRate()
    {
        return swingRate;
    }

    
    /**
     * 设置：振幅
     * 
     * @param swingRate 
     */
    public void setSwingRate(Double swingRate)
    {
        this.swingRate = swingRate;
    }

    
    /**
     * 获取：外盘
     */
    public Integer getOutSize()
    {
        return outSize;
    }

    
    /**
     * 设置：外盘
     * 
     * @param outSize 
     */
    public void setOutSize(Integer outSize)
    {
        this.outSize = outSize;
    }

    
    /**
     * 获取：内盘
     */
    public Integer getInSize()
    {
        return inSize;
    }

    
    /**
     * 设置：内盘
     * 
     * @param inSize 
     */
    public void setInSize(Integer inSize)
    {
        this.inSize = inSize;
    }

    
    /**
     * 获取：市盈率
     */
    public Double getPriceEarningRatio()
    {
        return priceEarningRatio;
    }

    
    /**
     * 设置：市盈率
     * 
     * @param priceEarningRatio 
     */
    public void setPriceEarningRatio(Double priceEarningRatio)
    {
        this.priceEarningRatio = priceEarningRatio;
    }

    
    /**
     * 获取：市净率
     */
    public Double getPriceBookValueRatio()
    {
        return priceBookValueRatio;
    }

    
    /**
     * 设置：市净率
     * 
     * @param priceBookValueRatio 
     */
    public void setPriceBookValueRatio(Double priceBookValueRatio)
    {
        this.priceBookValueRatio = priceBookValueRatio;
    }

    
    /**
     * 获取：交易时间
     */
    public Date getTime()
    {
        return time;
    }

    
    /**
     * 设置：交易时间
     * 
     * @param time 
     */
    public void setTime(Date time)
    {
        this.time = time;
    }
    
    
    /**
     * 获取：写入数据库的时间
     */
    public Date getDbTime()
    {
        return dbTime;
    }


    /**
     * 设置：写入数据库的时间
     * 
     * @param dbTime 
     */
    public void setDbTime(Date dbTime)
    {
        this.dbTime = dbTime;
    }

    
    /**
     * 获取：KDJ指标。此值不保存在数据库中，只用于动态计算KDJ指标
     */
    public KDJ getKdjValue()
    {
        return kdjValue;
    }

    
    /**
     * 设置：KDJ指标。此值不保存在数据库中，只用于动态计算KDJ指标
     * 
     * @param kdjValue 
     */
    public void setKdjValue(KDJ kdjValue)
    {
        this.kdjValue = kdjValue;
    }

    
    /**
     * 获取：MACD指标。此值不保存在数据库中，只用于动态计算MACD指标
     */
    public MACD getMacdValue()
    {
        return macdValue;
    }

    
    /**
     * 设置：MACD指标。此值不保存在数据库中，只用于动态计算MACD指标
     * 
     * @param macdValue 
     */
    public void setMacdValue(MACD macdValue)
    {
        this.macdValue = macdValue;
    }

    
    /**
     * 获取：WR指标。此值不保存在数据库中，只用于动态计算WR指标
     */
    public WR getWrValue()
    {
        return wrValue;
    }

    
    /**
     * 设置：WR指标。此值不保存在数据库中，只用于动态计算WR指标
     * 
     * @param wr 
     */
    public void setWrValue(WR wrValue)
    {
        this.wrValue = wrValue;
    }

    
    /**
     * 获取：KDJ（昨天的）指标。此值不保存在数据库中，只用于动态计算KDJ指标
     */
    public KDJ getKdjValueYesterday()
    {
        return kdjValueYesterday;
    }

    
    /**
     * 设置：KDJ（昨天的）指标。此值不保存在数据库中，只用于动态计算KDJ指标
     * 
     * @param kdjValueYesterday 
     */
    public void setKdjValueYesterday(KDJ kdjValueYesterday)
    {
        this.kdjValueYesterday = kdjValueYesterday;
    }

    
    /**
     * 获取：MACD指标（昨天的）。此值不保存在数据库中，只用于动态计算MACD指标
     */
    public MACD getMacdValueYesterday()
    {
        return macdValueYesterday;
    }

    
    /**
     * 设置：MACD指标（昨天的）。此值不保存在数据库中，只用于动态计算MACD指标
     * 
     * @param macdValueYesterday 
     */
    public void setMacdValueYesterday(MACD macdValueYesterday)
    {
        this.macdValueYesterday = macdValueYesterday;
    }

    
    /**
     * 获取：WR指标（昨天的）。此值不保存在数据库中，只用于动态计算WR指标
     */
    public WR getWrValueYesterday()
    {
        return wrValueYesterday;
    }

    
    /**
     * 设置：WR指标（昨天的）。此值不保存在数据库中，只用于动态计算WR指标
     * 
     * @param wrValueYesterday 
     */
    public void setWrValueYesterday(WR wrValueYesterday)
    {
        this.wrValueYesterday = wrValueYesterday;
    }

    
    /**
     * 获取：MA移动平均线(均线)指标。此值不保存在数据库中，只用于动态计算MA指标
     */
    public MA getMaValue()
    {
        return maValue;
    }

    
    /**
     * 设置：MA移动平均线(均线)指标。此值不保存在数据库中，只用于动态计算MA指标
     * 
     * @param maValue 
     */
    public void setMaValue(MA maValue)
    {
        this.maValue = maValue;
    }

    
    /**
     * 获取：MA移动平均线(均线)指标（昨天的）。此值不保存在数据库中，只用于动态计算MA指标
     */
    public MA getMaValueYesterday()
    {
        return maValueYesterday;
    }


    /**
     * 设置：MA移动平均线(均线)指标（昨天的）。此值不保存在数据库中，只用于动态计算MA指标
     * 
     * @param maValueYesterday 
     */
    public void setMaValueYesterday(MA maValueYesterday)
    {
        this.maValueYesterday = maValueYesterday;
    }
    
}
