package com.hy.stock.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.hy.common.CycleList;
import org.hy.common.ExpireMap;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.Return;
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
import com.hy.stock.calculate.MA;
import com.hy.stock.common.BaseNews;
import com.hy.stock.dao.LargeTradingDAO;
import com.hy.stock.dao.MessageInfoDAO;
import com.hy.stock.dao.StockConfigDAO;
import com.hy.stock.dao.StockInfoDayDAO;





/**
 * 股票MA移动平均线(均线)邮件消息
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-28
 * @version     v1.0
 */
@Xjava
public class MailStockMA extends BaseNews
{
    private static final long serialVersionUID = 2217715800265149530L;

    
    
    private static final String                       $TaskType        = "NewsTask_MA";
    
    private static       int                          $SerialNo        = 0;
    
    /** 历史消息。保证在一定时间内，不重复发送消息 */
    private final static ExpireMap<String ,StockInfo> $NewsHistory    = new ExpireMap<String ,StockInfo>();
    
    
    @Xjava
    private StockConfigDAO  stockConfigDAO;
    
    @Xjava
    private StockInfoDayDAO stockInfoDayDAO;
    
    @Xjava
    private MessageInfoDAO  messageInfoDAO;
    
    private int             news_MA_IntervalTime;
    
    
    
    public MailStockMA()
    {
        this.news_MA_IntervalTime = Integer.parseInt(XJava.getParam("News_MA_IntervalTime").getValue());
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
        v_Msg.setMessageType(MessageInfo.$Type_MA);
        
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
     * @createDate  2016-08-28
     * @version     v1.0
     *
     * @param i_Stocks
     */
    public synchronized void news(List<StockInfo> i_Stocks)
    {
        List<Return<StockInfo>> v_StockInfos = new ArrayList<Return<StockInfo>>();
        String                  v_Msg        = null;
        
        for (StockInfo v_Stock : i_Stocks)
        {
            List<StockInfoDay> v_StockDays = this.stockInfoDayDAO.queryStockInfoDays().get(v_Stock.getStockCode() + "qfq");
            
            if ( !Help.isNull(v_StockDays) )
            {
                StockInfoDay v_Yesterday = v_StockDays.get(0);
                
                if ( null != v_Yesterday.getMa5()  && v_Yesterday.getMa5()  > MA.$MA_InitValue
                  && null != v_Yesterday.getMa10() && v_Yesterday.getMa10() > MA.$MA_InitValue
                  && v_Stock.getNewPrice() > 0 
                  && v_Stock.getTodayOpenPrice() > 0 )
                {
                    if ( $NewsHistory.containsKey(v_Stock.getStockCode()) )
                    {
                        continue;
                    }
                    
                    Return<StockInfo> v_Item = new Return<StockInfo>();
                    
                    v_Item.paramStr = "";
                    // v_Stock.setMaValue(v_MA);
                    v_Stock.setMaValueYesterday(new MA(v_Yesterday.getMa5() 
                                                      ,v_Yesterday.getMa10()
                                                      ,v_Yesterday.getMa20()
                                                      ,v_Yesterday.getMa30()
                                                      ,v_Yesterday.getMa60()
                                                      ,v_Yesterday.getMa108()));
                    
                    if ( v_Yesterday.getNewPrice() >= v_Yesterday.getMa5() 
                      && v_Stock    .getNewPrice() <  v_Yesterday.getMa5() )
                    {
                        v_Msg = "跌破5日均线；";
                        messageInfoDAO.addMessageBad(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorGreen(v_Msg);
                    }
                    else if ( v_Yesterday.getNewPrice() <  v_Yesterday.getMa5() 
                           && v_Stock    .getNewPrice() >= v_Yesterday.getMa5() )
                    {
                        v_Msg = "突破5日均线；";
                        messageInfoDAO.addMessageGood(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorRed(v_Msg);
                    }
                    
                    if ( v_Yesterday.getNewPrice() >= v_Yesterday.getMa10() 
                      && v_Stock    .getNewPrice() <  v_Yesterday.getMa10() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "跌破10日均线；";
                        messageInfoDAO.addMessageBad(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorGreen(v_Msg);
                    }
                    else if ( v_Yesterday.getNewPrice() <  v_Yesterday.getMa10() 
                           && v_Stock    .getNewPrice() >= v_Yesterday.getMa10() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "突破10日均线；";
                        messageInfoDAO.addMessageGood(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorRed(v_Msg);
                    }
                    
                    if ( v_Yesterday.getNewPrice() >= v_Yesterday.getMa20() 
                      && v_Stock    .getNewPrice() <  v_Yesterday.getMa20() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "跌破20日均线；";
                        messageInfoDAO.addMessageBad(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorGreen(v_Msg);
                    }
                    else if ( v_Yesterday.getNewPrice() <  v_Yesterday.getMa20() 
                           && v_Stock    .getNewPrice() >= v_Yesterday.getMa20() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "突破20日均线；";
                        messageInfoDAO.addMessageGood(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorRed(v_Msg);
                    }
                    
                    if ( v_Yesterday.getNewPrice() >= v_Yesterday.getMa30() 
                      && v_Stock    .getNewPrice() <  v_Yesterday.getMa30() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "跌破30日均线；";
                        messageInfoDAO.addMessageBad(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorGreen(v_Msg);
                    }
                    else if ( v_Yesterday.getNewPrice() <  v_Yesterday.getMa30() 
                           && v_Stock    .getNewPrice() >= v_Yesterday.getMa30() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "突破30日均线；";
                        messageInfoDAO.addMessageGood(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorRed(v_Msg);
                    }
                    
                    if ( v_Yesterday.getNewPrice() >= v_Yesterday.getMa60() 
                      && v_Stock    .getNewPrice() <  v_Yesterday.getMa60() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "跌破60日均线；";
                        messageInfoDAO.addMessageBad(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorGreen(v_Msg);
                    }
                    else if ( v_Yesterday.getNewPrice() <  v_Yesterday.getMa60() 
                           && v_Stock    .getNewPrice() >= v_Yesterday.getMa60() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "突破60日均线；";
                        messageInfoDAO.addMessageGood(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorRed(v_Msg);
                    }
                    
                    if ( v_Yesterday.getNewPrice() >= v_Yesterday.getMa108() 
                      && v_Stock    .getNewPrice() <  v_Yesterday.getMa108() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "跌破108日均线；";
                        messageInfoDAO.addMessageBad(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorGreen(v_Msg);
                    }
                    else if ( v_Yesterday.getNewPrice() <  v_Yesterday.getMa108() 
                           && v_Stock    .getNewPrice() >= v_Yesterday.getMa108() )
                    {
                        if ( !Help.isNull(v_Item.paramStr) )
                        {
                            v_Item.paramStr += "<br>";
                        }
                        v_Msg = "突破108日均线；";
                        messageInfoDAO.addMessageGood(this.getMsg(v_Stock).setMessage(v_Msg));
                        v_Item.paramStr += this.fontColorRed(v_Msg);
                    }
                    
                    if ( !Help.isNull(v_Item.paramStr) )
                    {
                        $NewsHistory.put(v_Stock.getStockCode() ,v_Stock ,this.news_MA_IntervalTime);
                        v_Item.paramObj = v_Stock;
                        v_StockInfos.add(v_Item);
                    }
                }
            }
        }
        
        if ( Help.isNull(v_StockInfos) )
        {
            return;
        }
        
        TaskPool.putTask(new NewsWRTask(v_StockInfos));
    }
    
    
    
    /**
     * 显示的股票信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-28
     * @version     v1.0
     */
    private String makeStockMA(List<Return<StockInfo>> i_Stocks)
    {
        StringBuilder v_Buffer       = new StringBuilder();
        LargeTrading  v_LargeTrading = new LargeTrading();
        
        for (Return<StockInfo> v_Item : i_Stocks)
        {
            String v_Content = this.getTemplateStockMAContent();
            
            for (int v_PIndex=0; v_PIndex<v_Item.paramObj.gatPropertySize(); v_PIndex++)
            {
                v_Content = v_Content.replaceAll(":" + v_Item.paramObj.gatPropertyShortName(v_PIndex) ,Help.NVL(v_Item.paramObj.gatPropertyValue(v_PIndex)).toString());
            }
            
            v_LargeTrading.setBuySum( LargeTradingDAO.getTodayTradingsSum_Buy( v_Item.paramObj.getStockCode()));
            v_LargeTrading.setSellSum(LargeTradingDAO.getTodayTradingsSum_Sell(v_Item.paramObj.getStockCode()));
            v_LargeTrading.setDifferenceSum(v_LargeTrading.getBuySum() - v_LargeTrading.getSellSum());
            
            v_Content = v_Content.replaceAll(":buySum"            ,v_LargeTrading.getBuySum() .toString());
            v_Content = v_Content.replaceAll(":sellSum"           ,v_LargeTrading.getSellSum().toString());
            v_Content = v_Content.replaceAll(":htmlDifferenceSum" ,v_LargeTrading.getHtmlDifferenceSum());
            
            v_Content = v_Content.replaceAll(":ma5"               ,"-" + this.getYesterdayValue(v_Item.paramObj.getMaValueYesterday().getMa5 ()));
            v_Content = v_Content.replaceAll(":ma10"              ,"-" + this.getYesterdayValue(v_Item.paramObj.getMaValueYesterday().getMa10()));
            v_Content = v_Content.replaceAll(":ma20"              ,"-" + this.getYesterdayValue(v_Item.paramObj.getMaValueYesterday().getMa20()));
            v_Content = v_Content.replaceAll(":ma30"              ,"-" + this.getYesterdayValue(v_Item.paramObj.getMaValueYesterday().getMa30()));
            v_Content = v_Content.replaceAll(":ma60"              ,"-" + this.getYesterdayValue(v_Item.paramObj.getMaValueYesterday().getMa60()));
            v_Content = v_Content.replaceAll(":ma801"             ,"-" + this.getYesterdayValue(v_Item.paramObj.getMaValueYesterday().getMa108()));
            v_Content = v_Content.replaceAll(":NewsMA"            ,v_Item.paramStr);
            v_Content = v_Content.replaceAll(":NewsHistory"       ,this.getNewsHistory(v_Item.paramObj.getStockCode()));
            
            v_Buffer.append(v_Content);
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
     * @createDate  2016-08-28
     * @version     v1.0
     */
    class NewsWRTask extends Task<Object>
    {
        private List<Return<StockInfo>> stocks;
        

        public NewsWRTask(List<Return<StockInfo>> i_Stocks)
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
                PartitionMap<String ,Return<StockInfo>> v_Receivers = new TablePartition<String ,Return<StockInfo>>();
                for (Return<StockInfo> v_Item : this.stocks)
                {
                    StockConfig v_StockConfig = stockConfigDAO.queryStockConfigMaps().get(v_Item.paramObj.getStockCode());
                    
                    for (String i_Receiver : v_StockConfig.getNewsReceivers())
                    {
                        v_Receivers.putRow(i_Receiver ,v_Item);
                    }
                }
                
                if ( !Help.isNull(v_Receivers) )
                {
                    // 向每个接收者发送消息
                    for (Entry<String, List<Return<StockInfo>>> v_Receiver : v_Receivers.entrySet())
                    {
                        MailSendInfo v_SendInfo = new MailSendInfo();
                        
                        v_SendInfo.setEmail(v_Receiver.getKey());
                        v_SendInfo.setSubject("消息：MA预警：黄金均线");
                        v_SendInfo.setContent(getTemplateStockMATitle().replaceAll(":Content" ,makeStockMA(v_Receiver.getValue())));
                        
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
                    
                    System.out.print("  News MA to " + StringHelp.toString(Help.toListKeys(v_Receivers) ,"" ,","));
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
            return "MA " + this.getTaskNo();
        }
        
    }
    
}
