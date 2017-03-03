package com.hy.stock.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.hy.common.CycleList;
import org.hy.common.ExpireMap;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.StringHelp;
import org.hy.common.TablePartition;
import org.hy.common.mail.MailOwnerInfo;
import org.hy.common.mail.MailSendInfo;
import org.hy.common.mail.SimpleMail;
import org.hy.common.thread.Task;
import org.hy.common.thread.TaskPool;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.LargeTrading;
import com.hy.stock.bean.MessageInfo;
import com.hy.stock.bean.StockConfig;
import com.hy.stock.bean.StockInfo;
import com.hy.stock.bean.StockInfoDay;
import com.hy.stock.common.BaseNews;
import com.hy.stock.dao.LargeTradingDAO;
import com.hy.stock.dao.MessageInfoDAO;
import com.hy.stock.dao.StockConfigDAO;
import com.hy.stock.dao.StockInfoDayDAO;
import com.hy.stock.http.XHttpStock;





/**
 * 股票邮件消息
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-11
 * @version     v1.0
 */
@Xjava
public class MailStock extends BaseNews
{
    private static final long serialVersionUID = 1673324504165829391L;

    
    
    private static final String                       $TaskType        = "NewsTask";
    
    private static       int                          $SerialNo        = 0;
    
    /** 历史消息。保证在一定时间内，不重复发送消息 */
    private final static ExpireMap<String ,StockInfo> $NewsHistory    = new ExpireMap<String ,StockInfo>();
    
    
    @Xjava
    private StockConfigDAO  stockConfigDAO;
    
    @Xjava
    private StockInfoDayDAO stockInfoDayDAO;
    
    @Xjava
    private MessageInfoDAO  messageInfoDAO;
    
    private double          news_UpDownRange;
    
    private int             news_UpDownRange_IntervalTime;
    
    
    
    public MailStock()
    {
        this.news_UpDownRange              = Double.parseDouble(XJava.getParam("News_UpDownRange")             .getValue());
        this.news_UpDownRange_IntervalTime = Integer.parseInt(  XJava.getParam("News_UpDownRange_IntervalTime").getValue());
    }
    
    
    
    /**
     * 获取预警类型的消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-31
     * @version     v1.0
     *
     * @param i_StockInfo
     * @return
     */
    private MessageInfo getMsg(StockInfo i_StockInfo)
    {
        MessageInfo v_Msg = new MessageInfo();
        
        v_Msg.init(i_StockInfo);

        v_Msg.setWeekNo(null);
        v_Msg.setMessageType(MessageInfo.$Type_UpDownRange);
        
        List<StockInfoDay> v_StockDays = this.stockInfoDayDAO.queryStockInfoDays().get(i_StockInfo.getStockCode() + "qfq");
        if ( !Help.isNull(v_StockDays) )
        {
            v_Msg.setStockDayNo(v_StockDays.get(0).getStockDayNo() + 1);
        }
        
        return v_Msg;
    }
    
    
    
    /**
     * 发送消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-11
     * @version     v1.0
     *
     * @param i_Stocks
     */
    public synchronized void news(List<StockInfo> i_Stocks)
    {
        List<StockInfo> v_StockInfos = new ArrayList<StockInfo>();
        
        for (StockInfo v_Stock : i_Stocks)
        {
            if ( v_Stock.getUpDownRange() >= this.news_UpDownRange )
            {
                if ( !$NewsHistory.containsKey(v_Stock.getStockCode()) )
                {
                    $NewsHistory.put(v_Stock.getStockCode() ,v_Stock ,this.news_UpDownRange_IntervalTime);
                    messageInfoDAO.addMessageGood(this.getMsg(v_Stock).setMessage("涨" + v_Stock.getUpDownRange() + "%"));
                    v_StockInfos.add(v_Stock);
                }
            }
            else if ( v_Stock.getUpDownRange() <= this.news_UpDownRange * -1 )
            {
                if ( !$NewsHistory.containsKey(v_Stock.getStockCode()) )
                {
                    $NewsHistory.put(v_Stock.getStockCode() ,v_Stock ,this.news_UpDownRange_IntervalTime);
                    messageInfoDAO.addMessageBad(this.getMsg(v_Stock).setMessage("跌" + v_Stock.getUpDownRange() + "%"));
                    v_StockInfos.add(v_Stock);
                }
            }
        }
        
        if ( Help.isNull(v_StockInfos) )
        {
            return;
        }
        
        TaskPool.putTask(new NewsTask(v_StockInfos));
    }
    
    
    
    /**
     * 显示的股票信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-11
     * @version     v1.0
     */
    private String makeStock(List<StockInfo> i_Stocks)
    {
        StringBuilder v_Buffer       = new StringBuilder();
        LargeTrading  v_LargeTrading = new LargeTrading();
        
        for (StockInfo v_Stock : i_Stocks)
        {
            String v_Content = this.getTemplateStockContent();
            
            for (int v_PIndex=0; v_PIndex<v_Stock.gatPropertySize(); v_PIndex++)
            {
                v_Content = v_Content.replaceAll(":" + v_Stock.gatPropertyShortName(v_PIndex) ,Help.NVL(v_Stock.gatPropertyValue(v_PIndex)).toString());
            }
            
            v_LargeTrading.setBuySum( LargeTradingDAO.getTodayTradingsSum_Buy( v_Stock.getStockCode()));
            v_LargeTrading.setSellSum(LargeTradingDAO.getTodayTradingsSum_Sell(v_Stock.getStockCode()));
            v_LargeTrading.setDifferenceSum(v_LargeTrading.getBuySum() - v_LargeTrading.getSellSum());
            
            v_Content = v_Content.replaceAll(":buySum"            ,v_LargeTrading.getBuySum() .toString());
            v_Content = v_Content.replaceAll(":sellSum"           ,v_LargeTrading.getSellSum().toString());
            v_Content = v_Content.replaceAll(":htmlDifferenceSum" ,v_LargeTrading.getHtmlDifferenceSum());
            v_Content = v_Content.replaceAll(":NewsHistory"       ,this.getNewsHistory(v_Stock.getStockCode()));
            
            v_Buffer.append(v_Content);
        }
        
        return v_Buffer.toString();
    }
    
    
    
    /**
     * 显示的股票信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-11
     * @version     v1.0
     */
    @SuppressWarnings("unused")
    private String makeStockInfo(List<StockInfo> i_Stocks)
    {
        StringBuilder v_Buffer = new StringBuilder();
        
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[0][1]  ,21 ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[1][1]  ,6  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[2][1]  ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[3][1]  ,6  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[4][1]  ,6  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[5][1]  ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[6][1]  ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[7][1]  ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[8][1]  ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[9][1]  ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[10][1] ,10 ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[11][1] ,10 ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[12][1] ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[13][1] ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[14][1] ,10 ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[15][1] ,10 ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[16][1] ,7  ," "));
        v_Buffer.append(StringHelp.rpad(XHttpStock.$StockResults[17][1] ,7  ," "));
        v_Buffer.append("\n");
        
        for (StockInfo v_Stock : i_Stocks)
        {
            v_Buffer.append(StringHelp.rpad(v_Stock.getTime().getFull()      ,22 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getStockName()           ,6  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getStockCode()           ,10 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getYesterdayClosePrice() ,7  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getTodayOpenPrice()      ,7  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getNewPrice()            ,10 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getMaxPrice()            ,10 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getMinPrice()            ,10 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getUpDownPrice()         ,9  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getUpDownRange()         ,9  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getBargainSum()          ,12 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getBargainSize()         ,12 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getTurnoverRate()        ,9  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getSwingRate()           ,8  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getOutSize()             ,11 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getInSize()              ,12 ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getPriceEarningRatio()   ,9  ," "));
            v_Buffer.append(StringHelp.rpad(v_Stock.getPriceBookValueRatio() ,7  ," "));
            v_Buffer.append("\n");
        }
        
        return v_Buffer.toString();
    }
    
    
    
    public StockConfigDAO getStockConfigDAO()
    {
        return stockConfigDAO;
    }


    
    public void setStockConfigDAO(StockConfigDAO stockConfigDAO)
    {
        this.stockConfigDAO = stockConfigDAO;
    }
    
    
    
    public StockInfoDayDAO getStockInfoDayDAO()
    {
        return stockInfoDayDAO;
    }


    
    public void setStockInfoDayDAO(StockInfoDayDAO stockInfoDayDAO)
    {
        this.stockInfoDayDAO = stockInfoDayDAO;
    }
    
    
    
    public MessageInfoDAO getMessageInfoDAO()
    {
        return messageInfoDAO;
    }

    
    
    public void setMessageInfoDAO(MessageInfoDAO messageInfoDAO)
    {
        this.messageInfoDAO = messageInfoDAO;
    }
    
    
    
    
    
    /**
     * 发送消息的任务
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-12
     * @version     v1.0
     */
    class NewsTask extends Task<Object>
    {
        private List<StockInfo> stocks;
        

        public NewsTask(List<StockInfo> i_Stocks)
        {
            super($TaskType);
            
            this.stocks = i_Stocks;
        }
        
        
        private synchronized int GetSerialNo()
        {
            return ++$SerialNo;
        }
        
        
        @SuppressWarnings("unchecked")
        @Override
        public void execute()
        {
            try
            {
                // 为每个接收者，生成需要发送的股票信息
                PartitionMap<String ,StockInfo> v_Receivers = new TablePartition<String ,StockInfo>();
                for (StockInfo v_Stock : this.stocks)
                {
                    StockConfig v_StockConfig = stockConfigDAO.queryStockConfigMaps().get(v_Stock.getStockCode());
                    
                    for (String i_Receiver : v_StockConfig.getNewsReceivers())
                    {
                        v_Receivers.putRow(i_Receiver ,v_Stock);
                    }
                }
                
                if ( !Help.isNull(v_Receivers) )
                {
                    // 向每个接收者发送消息
                    for (Entry<String, List<StockInfo>> v_Receiver : v_Receivers.entrySet())
                    {
                        MailSendInfo v_SendInfo = new MailSendInfo();
                        
                        v_SendInfo.setEmail(v_Receiver.getKey());
                        v_SendInfo.setSubject("消息：涨跌幅超过 ±" + news_UpDownRange + "%");
                        v_SendInfo.setContent(getTemplateStockTitle().replaceAll(":Content" ,makeStock(v_Receiver.getValue())));
                        
                        SimpleMail.sendHtmlMail(((CycleList<MailOwnerInfo>)XJava.getObject("CycleMails")).next() ,v_SendInfo);
                        
                        try
                        {
                            Thread.sleep(5000);
                        }
                        catch (Exception exce)
                        {
                            // Nothing.
                        }
                    }
                    
                    System.out.print("  News to " + StringHelp.toString(Help.toListKeys(v_Receivers) ,"" ,","));
                }
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            this.finishTask();
        }
        

        @Override
        public int getSerialNo()
        {
            return GetSerialNo();
        }
        

        @Override
        public String getTaskDesc()
        {
            return "" + this.getTaskNo();
        }
        
    }
    
}
