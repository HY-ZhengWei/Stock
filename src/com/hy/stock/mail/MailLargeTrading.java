package com.hy.stock.mail;

import java.util.List;
import java.util.Map.Entry;

import org.hy.common.CycleList;
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
import com.hy.stock.bean.StockConfig;
import com.hy.stock.bean.StockInfoDay;
import com.hy.stock.common.BaseNews;
import com.hy.stock.dao.StockConfigDAO;
import com.hy.stock.dao.StockInfoDayDAO;





/**
 * 大单交易邮件消息
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-11
 * @version     v1.0
 */
@Xjava
public class MailLargeTrading extends BaseNews
{
    private static final long serialVersionUID = -7737174257744791239L;

    
    
    private static final String $TaskType = "NewsLargeTradingTask";
    
    private static       int    $SerialNo = 0;
    

    
    @Xjava
    private StockConfigDAO  stockConfigDAO;
    
    @Xjava
    private StockInfoDayDAO stockInfoDayDAO;
    
    
    
    /**
     * 发送消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-11
     * @version     v1.0
     *
     * @param i_LargeTradings
     */
    public void news(List<LargeTrading> i_LargeTradings)
    {
        TaskPool.putTask(new NewsLargeTradingTask(i_LargeTradings));
    }
    
    
    
    /**
     * 显示的股票信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-11
     * @version     v1.0
     */
    private String makeLargeTrading(List<LargeTrading> i_LargeTradings)
    {
        StringBuilder v_Buffer  = new StringBuilder();
        
        for (LargeTrading v_LargeTrading : i_LargeTradings)
        {
            String v_Content = this.getTemplateLargeTradingContent();
            
            for (int v_PIndex=0; v_PIndex<v_LargeTrading.gatPropertySize(); v_PIndex++)
            {
                v_Content = v_Content.replaceAll(":" + v_LargeTrading.gatPropertyShortName(v_PIndex) ,v_LargeTrading.gatPropertyValue(v_PIndex).toString());
            }
            
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


    


    /**
     * 发送消息的任务
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-12
     * @version     v1.0
     */
    class NewsLargeTradingTask extends Task<Object>
    {
        private List<LargeTrading> largeTradings;
        

        public NewsLargeTradingTask(List<LargeTrading> i_LargeTradings)
        {
            super($TaskType);
            
            this.largeTradings = i_LargeTradings;
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
                PartitionMap<String ,LargeTrading> v_Receivers = new TablePartition<String ,LargeTrading>();
                for (LargeTrading v_LargeTrading : this.largeTradings)
                {
                    StockConfig v_StockConfig = stockConfigDAO.queryStockConfigMaps().get(v_LargeTrading.getStockCode());
                    
                    if ( v_LargeTrading.getBargainSum() >= v_StockConfig.getNewsBargainSum() )
                    {
                        List<StockInfoDay> v_StockDays = stockInfoDayDAO.queryStockInfoDays().get(v_StockConfig.getStockCode() + "qfq");
                        
                        if ( !Help.isNull(v_StockDays) )
                        {
                            v_LargeTrading.setYesterdayClosePrice(v_StockDays.get(0).getNewPrice());
                        }
                                
                        for (String i_Receiver : v_StockConfig.getNewsReceivers())
                        {
                            v_Receivers.putRow(i_Receiver ,v_LargeTrading);
                        }
                    }
                }
                
                if ( !Help.isNull(v_Receivers) )
                {
                    // 向每个接收者发送消息
                    for (Entry<String, List<LargeTrading>> v_Receiver : v_Receivers.entrySet())
                    {
                        MailSendInfo v_SendInfo = new MailSendInfo();
                        
                        v_SendInfo.setEmail(v_Receiver.getKey());
                        v_SendInfo.setSubject("消息：大单交易");
                        v_SendInfo.setContent(getTemplateLargeTradingTitle().replaceAll(":Content" ,makeLargeTrading(v_Receiver.getValue())));
                        
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
                    
                    System.out.print("  News LT to " + StringHelp.toString(Help.toListKeys(v_Receivers) ,"" ,","));
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
            return "LT " + this.getTaskNo();
        }
        
    }
    
}
