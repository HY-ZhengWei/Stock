package com.hy.stock.bean;

import org.hy.common.Date;
import org.hy.common.Help;

import com.hy.stock.common.BaseBean;





/**
 * 大单交易信息 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-13
 * @version     v1.0
 */
public class LargeTrading extends BaseBean
{
    
    private static final long serialVersionUID = -1734101065221455937L;

    
    /** 主键 */
    private String  id;
    
    /** 股票代码 */
    private String  stockCode;
    
    /** 股票名称 */
    private String  stockName;
    
    /** 交易序号 */
    private String  tradingNo;
    
    /** 成交价 */
    private Double  bargainPrice;
    
    /** 成交额(万元) */
    private Double  bargainSum;
    
    /** 成交量(手) */
    private Integer bargainSize;
    
    /** 交易类型(B买盘、S卖盘、M) */
    private String  tradingType;
    
    /** 交易时间 */
    private Date    time;
    
    /** 累计买入(万元) */
    private Double  buySum;
    
    /** 累计卖出(万元) */
    private Double  sellSum;
    
    /** 主力增减仓(万元) = 累计买入 - 累计卖出 */
    private Double  differenceSum;
    
    /** 昨收（不保存在数据库中，只做内存数据处理用） */
    private Double  yesterdayClosePrice;
    
    
    
    public LargeTrading()
    {
        this.buySum              = 0D;
        this.sellSum             = 0D;
        this.differenceSum       = 0D;
        this.yesterdayClosePrice = null;
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
     * 获取：交易序号
     */
    public String getTradingNo()
    {
        return tradingNo;
    }

    
    /**
     * 设置：交易序号
     * 
     * @param tradingNo 
     */
    public void setTradingNo(String tradingNo)
    {
        this.tradingNo = tradingNo;
    }


    /**
     * 获取：成交价
     */
    public Double getBargainPrice()
    {
        return bargainPrice;
    }

    
    /**
     * 设置：成交价
     * 
     * @param bargainPrice 
     */
    public void setBargainPrice(Double bargainPrice)
    {
        this.bargainPrice = bargainPrice;
    }
    
    
    /**
     * 获取：成交价
     */
    public String getHtmlBargainPrice()
    {
        if ( this.yesterdayClosePrice != null )
        {
            if ( this.bargainPrice > this.yesterdayClosePrice )
            {
                return this.fontColorRed(this.bargainPrice);
            }
            else if ( this.bargainPrice < this.yesterdayClosePrice )
            {
                return this.fontColorGreen(this.bargainPrice);
            }
            else
            {
                return this.bargainPrice + "";
            }
        }
        else
        {
            return this.bargainPrice + "";
        }
    }

    
    /**
     * 设置：成交价
     * 
     * @param bargainPrice 
     */
    public void setHtmlBargainPrice(String bargainPrice)
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
     * 获取：交易类型(B买盘、S卖盘、M)
     */
    public String getTradingType()
    {
        return tradingType;
    }

    
    /**
     * 设置：交易类型(B买盘、S卖盘、M)
     * 
     * @param tradingType 
     */
    public void setTradingType(String tradingType)
    {
        this.tradingType = tradingType;
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
     * 获取：累计买入(万元)
     */
    public Double getBuySum()
    {
        return Help.round(buySum ,3);
    }


    
    /**
     * 设置：累计买入(万元)
     * 
     * @param buySum 
     */
    public void setBuySum(Double buySum)
    {
        this.buySum = buySum;
    }


    
    /**
     * 获取：累计卖出(万元)
     */
    public Double getSellSum()
    {
        return Help.round(sellSum ,3);
    }


    
    /**
     * 设置：累计卖出(万元)
     * 
     * @param sellSum 
     */
    public void setSellSum(Double sellSum)
    {
        this.sellSum = sellSum;
    }


    
    /**
     * 获取：主力增减仓(万元) = 累计买入 - 累计卖出
     */
    public Double getDifferenceSum()
    {
        return differenceSum;
    }


    
    /**
     * 设置：主力增减仓(万元) = 累计买入 - 累计卖出
     * 
     * @param differenceSum 
     */
    public void setDifferenceSum(Double differenceSum)
    {
        this.differenceSum = differenceSum;
    }
    
    
    /**
     * 获取：交易类型(B买盘、S卖盘、M)
     */
    public String getHtmlTradingType()
    {
        if ( "B".equalsIgnoreCase(this.tradingType) )
        {
            return "<font color='#FF4444'>买入</font>";
        }
        else if ( "S".equalsIgnoreCase(this.tradingType) )
        {
            return "<font color='#669900'>卖出</font>";
        }
        else
        {
            return this.tradingType;
        }
    }

    
    /**
     * 设置：交易类型(B买盘、S卖盘、M)
     * 
     * @param tradingType 
     */
    public void setHtmlTradingType(String htmlTradingType)
    {
        // Nothing.
    }
    
    
    /**
     * 获取：主力增减仓(万元) = 累计买入 - 累计卖出
     */
    public String getHtmlDifferenceSum()
    {
        if ( this.differenceSum > 0 )
        {
            return this.fontColorRed(Help.round(Help.NVL(this.differenceSum) ,3));
        }
        else if ( this.differenceSum < 0 )
        {
            return this.fontColorGreen(Help.round(Help.NVL(this.differenceSum) ,3));
        }
        else
        {
            return Help.round(Help.NVL(this.differenceSum) ,3) + "";
        }
    }

    
    /**
     * 设置：主力增减仓(万元) = 累计买入 - 累计卖出
     * 
     * @param tradingType 
     */
    public void setHtmlDifferenceSum(String htmlTradingType)
    {
        // Nothing.
    }


    
    /**
     * 获取：昨收（不保存在数据库中，只做内存数据处理用）
     */
    public Double getYesterdayClosePrice()
    {
        return yesterdayClosePrice;
    }


    
    /**
     * 设置：昨收（不保存在数据库中，只做内存数据处理用）
     * 
     * @param yesterdayClosePrice 
     */
    public void setYesterdayClosePrice(Double yesterdayClosePrice)
    {
        this.yesterdayClosePrice = yesterdayClosePrice;
    }
    
}
