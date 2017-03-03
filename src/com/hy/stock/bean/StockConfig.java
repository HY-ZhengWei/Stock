package com.hy.stock.bean;

import org.hy.common.Help;
import org.hy.common.xml.XJava;





/**
 * 准备抓取的股票代码配置信息 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-11
 * @version     v1.0
 */
public class StockConfig
{
    
    /** 股票代码 */
    private String       stockCode;
    
    /** 股票名称 */
    private String       stockName;
    
    /** 分组名称 */
    private String       groupName;
    
    /** 消息的接收者。用;分号分隔 */
    private String       newsReceiver;
    
    /** 消息的接收者。已分隔成数组的 */
    private String []    newsReceivers;
    
    /** 大单交易中成交额(万元)超过多少发送消息 */
    private Double       newsBargainSum;

    
    
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
     * 获取：消息的接收者
     */
    public String getNewsReceiver()
    {
        return newsReceiver;
    }

    
    /**
     * 设置：消息的接收者
     * 
     * @param newReceiver 
     */
    public void setNewsReceiver(String newsReceiver)
    {
        this.newsReceiver = newsReceiver;
    }

    
    public synchronized String [] getNewsReceivers()
    {
        if ( Help.isNull(this.newsReceiver) )
        {
            return new String[0];
        }
        
        this.newsReceivers = this.newsReceiver.split(";");
        
        return this.newsReceivers;
    }


    
    /**
     * 获取：大单交易中成交额(万元)超过多少发送消息
     */
    public synchronized Double getNewsBargainSum()
    {
        if ( this.newsBargainSum == null || this.newsBargainSum <= 0 )
        {
            this.newsBargainSum = Double.valueOf(XJava.getParam("News_BargainSum").getValue());
        }
        
        return this.newsBargainSum;
    }


    
    /**
     * 设置：大单交易中成交额(万元)超过多少发送消息
     * 
     * @param newsBargainSum 
     */
    public synchronized void setNewsBargainSum(Double newsBargainSum)
    {
        this.newsBargainSum = newsBargainSum;
    }

}
