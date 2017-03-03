package com.hy.stock.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.thread.Task;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.LargeTrading;
import com.hy.stock.bean.StockConfig;
import com.hy.stock.dao.LargeTradingDAO;
import com.hy.stock.dao.StockConfigDAO;
import com.hy.stock.http.XHttpLargeTrading;
import com.hy.stock.mail.MailLargeTrading;





/**
 * 大单交易的服务层
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-13
 * @version     v1.0
 */
@Xjava
public class LargeTradingService
{
    
    private static final String $TaskType        = "LargeTradingTask";
    
    private static       int    $SerialNo        = 0;
    
    /** 上午开盘时间 */
    public  static       long   $Morning_Begin;
    
    /** 上午休盘时间 */
    public  static       long   $Morning_End; 
    
    /** 下午开盘时间 */
    public  static       long   $Afternoon_Begin; 
    
    /** 下午休盘时间 */
    public  static       long   $Afternoon_End; 
    
    
    
    @Xjava
    private LargeTradingDAO     largeTradingDAO;
    
    @Xjava
    private StockConfigDAO      stockConfigDAO;
    
    @Xjava
    private XHttpLargeTrading   xhttpLargeTrading;
    
    @Xjava
    private MailLargeTrading    mailLargeTrading;
    
    
    
    public LargeTradingService()
    {
        if ( $Morning_Begin <= 0 )
        {
            $Morning_Begin   = (new Date(StockService.$StandardTime + XJava.getParam("Morning_Begin")  .getValue().trim())).getTime() - (15*60*1000);
            $Morning_End     = (new Date(StockService.$StandardTime + XJava.getParam("Morning_End")    .getValue().trim())).getTime() + (15*60*1000);
            $Afternoon_Begin = (new Date(StockService.$StandardTime + XJava.getParam("Afternoon_Begin").getValue().trim())).getTime() - (15*60*1000);
            $Afternoon_End   = (new Date(StockService.$StandardTime + XJava.getParam("Afternoon_End")  .getValue().trim())).getTime() + (15*60*1000);
        }
    }
    
    
    
    /**
     * 启动请求大单交易接口，其后通过线程延时写入数据库
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-13
     * @version     v1.0
     *
     */
    public void start()
    {
        Date v_Now    = new Date();
        long v_NSTime = (new Date(StockService.$StandardTime + v_Now.getHMSMilli())).getTime();
        
        if ( ($Morning_Begin   <= v_NSTime && v_NSTime <= $Morning_End)
          || ($Afternoon_Begin <= v_NSTime && v_NSTime <= $Afternoon_End) )
        {
            System.out.print("\n" + v_Now.getFullMilli() + "  LT.");
            List<LargeTrading> v_LargeTradings    = null;
            List<LargeTrading> v_LargeTradingsSum = new ArrayList<LargeTrading>();
            
            for (Entry<String ,StockConfig> v_StockConfig : this.stockConfigDAO.queryStockConfigMaps().entrySet())
            {
                try
                {
                    if ( !"00全局指数".equals(v_StockConfig.getValue().getGroupName()) )
                    {
                        v_LargeTradings = xhttpLargeTrading.parseHttpResult(v_StockConfig.getValue() ,xhttpLargeTrading.request(v_StockConfig.getValue().getStockCode()));
                        if ( !Help.isNull(v_LargeTradings) )
                        {
                            v_LargeTradings = addLargeTradings(v_StockConfig.getValue().getStockCode() ,v_LargeTradings);
                        }
                    }
                }
                catch (Exception exce)
                {
                    exce.printStackTrace();
                }
                
                if ( !Help.isNull(v_LargeTradings) )
                {
                    v_LargeTradingsSum.addAll(v_LargeTradings);
                }
            }
            
            if ( !Help.isNull(v_LargeTradingsSum) )
            {
                mailLargeTrading.news(v_LargeTradingsSum);
            }
        }
        else
        {
            System.out.print(".");
        }
    }
    
    
    
    /**
     * 新增大单交易数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-13
     * @version     v1.0
     *
     * @param i_StockInfos
     * @return
     */
    public List<LargeTrading> addLargeTradings(String i_StockCode ,List<LargeTrading> i_LargeTradings)
    {
        return this.largeTradingDAO.addLargeTradings(i_StockCode ,i_LargeTradings);
    }
    
    
    
    public LargeTradingDAO getLargeTradingDAO()
    {
        return largeTradingDAO;
    }


    
    public void setLargeTradingDAO(LargeTradingDAO largeTradingDAO)
    {
        this.largeTradingDAO = largeTradingDAO;
    }


    
    public StockConfigDAO getStockConfigDAO()
    {
        return stockConfigDAO;
    }


    
    public void setStockConfigDAO(StockConfigDAO stockConfigDAO)
    {
        this.stockConfigDAO = stockConfigDAO;
    }


    
    public XHttpLargeTrading getXhttpLargeTrading()
    {
        return xhttpLargeTrading;
    }


    
    public void setXhttpLargeTrading(XHttpLargeTrading xhttpLargeTrading)
    {
        this.xhttpLargeTrading = xhttpLargeTrading;
    }


    
    public MailLargeTrading getMailLargeTrading()
    {
        return mailLargeTrading;
    }


    
    public void setMailLargeTrading(MailLargeTrading mailLargeTrading)
    {
        this.mailLargeTrading = mailLargeTrading;
    }





    /**
     * 解释大单交易接口的返回信息，并将其入库（暂时废弃多线程发起请求，因为大单交易的数据量本就不多）
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-13
     * @version     v1.0
     */
    class LargeTradingTask extends Task<Object>
    {
        private StockConfig stockConfig; 
        
        
        public LargeTradingTask(StockConfig i_StockConfig)
        {
            super($TaskType);
            
            this.stockConfig = i_StockConfig;
        }
        
        
        private synchronized int GetSerialNo()
        {
            return ++$SerialNo;
        }
        
        
        @Override
        public void execute()
        {
            List<LargeTrading> v_LargeTradings = null;
            
            try
            {
                v_LargeTradings = xhttpLargeTrading.parseHttpResult(this.stockConfig ,xhttpLargeTrading.request(this.stockConfig.getStockCode()));
                if ( !Help.isNull(v_LargeTradings) )
                {
                    v_LargeTradings = addLargeTradings(this.stockConfig.getStockCode() ,v_LargeTradings);
                    if ( !Help.isNull(v_LargeTradings) )
                    {
                        mailLargeTrading.news(v_LargeTradings);
                    }
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
