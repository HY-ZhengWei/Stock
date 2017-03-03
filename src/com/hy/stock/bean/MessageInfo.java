package com.hy.stock.bean;

import org.hy.common.Date;





/**
 * 消息 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-31
 * @version     v1.0
 */
public class MessageInfo extends StockInfoDay
{

    private static final long serialVersionUID = -994018383680606726L;
    
    
    
    /** 消息类型：涨跌幅 */
    public static final String $Type_UpDownRange = "UD";
    
    /** 消息类型：KDJ */
    public static final String $Type_KDJ         = "KDJ";
    
    /** 消息类型：MACD */
    public static final String $Type_MACD        = "MACD";
    
    /** 消息类型：WR */
    public static final String $Type_WR          = "WR";
    
    /** 消息类型：MA */
    public static final String $Type_MA          = "MA";
    
    
    
    /** 消息类型 */
    private String  messageType;
    
    /** 利好?还是利空?（1:利好；-1:利空；0:中立） */
    private Integer goodBad;
    
    /** 消息信息 */
    private String  message;
    
    /** 消息发送状态（0:等待发送； 1:发送成功； -1:发送异常）。默认为：0 */
    private Integer sendStatus;
    
    /** 发送次数。每发送一次次数加1。默认为：0 */
    private Integer sendCount;
    
    /** 发送时间。异常时，也记录时间。默认为：null */
    private Date    sendTime;
    
    
    
    public MessageInfo()
    {
        this.sendStatus = 0;
        this.sendCount  = 0;
        this.sendTime   = null;
    }
    
    
    
    /**
     * 获取：消息类型
     */
    public String getMessageType()
    {
        return messageType;
    }

    
    /**
     * 设置：消息类型
     * 
     * @param messageType 
     */
    public void setMessageType(String messageType)
    {
        this.messageType = messageType;
    }

    
    /**
     * 获取：消息信息
     */
    public String getMessage()
    {
        return message;
    }

    
    /**
     * 设置：消息信息
     * 
     * @param message 
     */
    public MessageInfo setMessage(String message)
    {
        this.message = message;
        return this;
    }

    
    /**
     * 获取：利好?还是利空?（1:利好；-1:利空；0:中立）
     */
    public Integer getGoodBad()
    {
        return goodBad;
    }

    
    /**
     * 设置：利好?还是利空?（1:利好；-1:利空；0:中立）
     * 
     * @param goodBad 
     */
    public void setGoodBad(Integer goodBad)
    {
        this.goodBad = goodBad;
    }

    
    /**
     * 获取：消息发送状态（0:等待发送； 1:发送成功； -1:发送异常）。默认为：0
     */
    public Integer getSendStatus()
    {
        return sendStatus;
    }

    
    /**
     * 设置：消息发送状态（0:等待发送； 1:发送成功； -1:发送异常）。默认为：0
     * 
     * @param sendStatus 
     */
    public void setSendStatus(Integer sendStatus)
    {
        this.sendStatus = sendStatus;
    }

    
    /**
     * 获取：发送次数。每发送一次次数加1。默认为：0
     */
    public Integer getSendCount()
    {
        return sendCount;
    }

    
    /**
     * 设置：发送次数。每发送一次次数加1。默认为：0
     * 
     * @param sendCount 
     */
    public void setSendCount(Integer sendCount)
    {
        this.sendCount = sendCount;
    }

    
    /**
     * 获取：发送时间。异常时，也记录时间。默认为：null
     */
    public Date getSendTime()
    {
        return sendTime;
    }

    
    /**
     * 设置：发送时间。异常时，也记录时间。默认为：null
     * 
     * @param sendTime 
     */
    public void setSendTime(Date sendTime)
    {
        this.sendTime = sendTime;
    }
    
}
