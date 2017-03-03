package com.hy.stock.service;

import java.util.List;
import java.util.Map.Entry;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.thread.Task;
import org.hy.common.thread.TaskPool;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.StockInfo;
import com.hy.stock.dao.StockConfigDAO;
import com.hy.stock.dao.StockInfoDAO;
import com.hy.stock.http.XHttpStock;
import com.hy.stock.mail.MailStock;
import com.hy.stock.mail.MailStockKDJ;
import com.hy.stock.mail.MailStockMA;
import com.hy.stock.mail.MailStockMACD;
import com.hy.stock.mail.MailStockWR;





/**
 * 股票信息的服务层
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-08
 * @version     v1.0
 */
@Xjava
public class StockService
{
    private static final String    $TaskType     = "StockTask";
    
    private static       int       $SerialNo     = 0;
    
    /** 标准时间 */
    public  static final String    $StandardTime = "2012-07-07 ";
    
    /** 上午开盘时间 */
    public  static       long      $Morning_Begin;
    
    /** 上午休盘时间 */
    public  static       long      $Morning_End; 
    
    /** 下午开盘时间 */
    public  static       long      $Afternoon_Begin; 
    
    /** 下午休盘时间 */
    public  static       long      $Afternoon_End; 
    
    
    
    @Xjava
    private StockInfoDAO    stockInfoDAO;
    
    @Xjava
    private StockConfigDAO  stockConfigDAO;
    
    @Xjava
    private XHttpStock      xhttpStock;
    
    @Xjava
    private MailStock       mailStock;
    
    @Xjava
    private MailStockKDJ    mailStockKDJ;
    
    @Xjava
    private MailStockMACD   mailStockMACD;
    
    @Xjava
    private MailStockWR     mailStockWR;
    
    @Xjava
    private MailStockMA     mailStockMA;
    
    
    
    public StockService()
    {
        if ( $Morning_Begin <= 0 )
        {
            $Morning_Begin   = (new Date($StandardTime + XJava.getParam("Morning_Begin")  .getValue().trim())).getTime() - (5*60*1000);
            $Morning_End     = (new Date($StandardTime + XJava.getParam("Morning_End")    .getValue().trim())).getTime() + (5*60*1000);
            $Afternoon_Begin = (new Date($StandardTime + XJava.getParam("Afternoon_Begin").getValue().trim())).getTime() - (5*60*1000);
            $Afternoon_End   = (new Date($StandardTime + XJava.getParam("Afternoon_End")  .getValue().trim())).getTime() + (5*60*1000);
        }
    }
    
    
    
    /**
     * 启动请求股票接口，其后通过线程延时写入数据库
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-08
     * @version     v1.0
     *
     */
    public void start()
    {
        Date v_Now    = new Date();
        long v_NSTime = (new Date($StandardTime + v_Now.getHMSMilli())).getTime();
        
        if ( ($Morning_Begin   <= v_NSTime && v_NSTime <= $Morning_End)
          || ($Afternoon_Begin <= v_NSTime && v_NSTime <= $Afternoon_End) )
        {
            System.out.print("\n" + v_Now.getFullMilli());
            
            // 分段发起请求
            for (Entry<String ,String> v_StockConfigSubGroups : this.stockConfigDAO.queryStockConfigs().entrySet())
            {
                TaskPool.putTask(new StockTask(v_StockConfigSubGroups.getKey() ,v_StockConfigSubGroups.getValue()));
            }
        }
        else
        {
            System.out.print(".");
        }
    }
    
    
    
    /**
     * 新增股票信息
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-08
     * @version     v1.0
     *
     * @param i_StockInfos
     * @return
     */
    public boolean addStocks(List<StockInfo> i_StockInfos)
    {
        return this.stockInfoDAO.addStocks(i_StockInfos);
    }
    
    
    
    /**
     * 解释分组
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-08
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    public String [] parseSplit(String i_Value)
    {
        String v_Value = i_Value.trim();
        
        v_Value = v_Value.replaceAll("\\n" ,"");
        v_Value = v_Value.replaceAll("\\r" ,"");
        v_Value = v_Value.replaceAll("\\t" ,"");
        v_Value = v_Value.replaceAll(" "   ,"");
        
        return v_Value.split(";");
    }
    
    
    
    public StockInfoDAO getStockInfoDAO()
    {
        return stockInfoDAO;
    }

    

    public void setStockInfoDAO(StockInfoDAO stockInfoDAO)
    {
        this.stockInfoDAO = stockInfoDAO;
    }


    
    public StockConfigDAO getStockConfigDAO()
    {
        return stockConfigDAO;
    }


    
    public void setStockConfigDAO(StockConfigDAO stockConfigDAO)
    {
        this.stockConfigDAO = stockConfigDAO;
    }


    
    public XHttpStock getXhttpStock()
    {
        return xhttpStock;
    }


    
    public void setXhttpStock(XHttpStock xhttpStock)
    {
        this.xhttpStock = xhttpStock;
    }

    
    
    public MailStock getMailStock()
    {
        return mailStock;
    }

    
    
    public void setMailStock(MailStock mailStock)
    {
        this.mailStock = mailStock;
    }



    public MailStockKDJ getMailStockKDJ()
    {
        return mailStockKDJ;
    }



    public void setMailStockKDJ(MailStockKDJ mailStockKDJ)
    {
        this.mailStockKDJ = mailStockKDJ;
    }



    public MailStockMACD getMailStockMACD()
    {
        return mailStockMACD;
    }
    
    
    
    public void setMailStockMACD(MailStockMACD mailStockMACD)
    {
        this.mailStockMACD = mailStockMACD;
    }


    
    public MailStockWR getMailStockWR()
    {
        return mailStockWR;
    }


    
    public void setMailStockWR(MailStockWR mailStockWR)
    {
        this.mailStockWR = mailStockWR;
    }

    
    
    public MailStockMA getMailStockMA()
    {
        return mailStockMA;
    }


    
    public void setMailStockMA(MailStockMA mailStockMA)
    {
        this.mailStockMA = mailStockMA;
    }





    /**
     * 解释股票接口的返回信息，并将其入库 
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-08
     * @version     v1.0
     */
    class StockTask extends Task<Object>
    {
        /** 分组名称 */
        private String stocksSubGroupName;
        
        /** 求获取的股票代码的分组信息。用,逗号分隔。用;分号分组（此处只包含,逗号了） */
        private String stocksSubGroup;
        

        public StockTask(String i_StocksSubGroupName ,String i_StocksSubGroup)
        {
            super($TaskType);
            
            this.stocksSubGroupName = i_StocksSubGroupName;
            this.stocksSubGroup     = i_StocksSubGroup;
        }
        
        
        private synchronized int GetSerialNo()
        {
            return ++$SerialNo;
        }
        
        
        @Override
        public void execute()
        {
            try
            {
                List<StockInfo> v_Stocks = xhttpStock.parseHttpResult(this.stocksSubGroupName ,xhttpStock.request(this.stocksSubGroup));
                if ( !Help.isNull(v_Stocks) )
                {
                    if ( Date.getNowTime().getTime() - v_Stocks.get(0).getTime().getTime() <= 1000 * 60 * 15 )
                    {
                        addStocks(         v_Stocks);
                        mailStock    .news(v_Stocks);
                        mailStockKDJ .news(v_Stocks);
                        mailStockMACD.news(v_Stocks);
                        mailStockWR  .news(v_Stocks);
                        mailStockMA  .news(v_Stocks);
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
