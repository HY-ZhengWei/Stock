package com.hy.stock.dao;

import java.util.List;

import org.hy.common.Date;
import org.hy.common.PartitionMap;
import org.hy.common.TablePartition;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.MessageInfo;





/**
 * 消息的操作DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-31
 * @version     v1.0
 */
@Xjava
public class MessageInfoDAO
{
    
    /**
     * 消息的高速缓存
     * PartitionMap.分区：股票代码
     */
    private static PartitionMap<String ,MessageInfo> $MessageCaches       = new TablePartition<String ,MessageInfo>();
    
    /** 消息的高速缓存是否初始化过 */
    private static boolean                           $MessageCachesIsInit = false;
    
    
    /**
     * 添加利好消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-31
     * @version     v1.0
     *
     * @param io_MessageInfo
     * @return
     */
    public boolean addMessageGood(MessageInfo io_MessageInfo)
    {
        io_MessageInfo.setGoodBad(1);
        return this.addMessage(io_MessageInfo);
    }
    
    
    
    /**
     * 添加利空消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-31
     * @version     v1.0
     *
     * @param io_MessageInfo
     * @return
     */
    public boolean addMessageBad(MessageInfo io_MessageInfo)
    {
        io_MessageInfo.setGoodBad(-1);
        return this.addMessage(io_MessageInfo);
    }
    
    
    
    /**
     * 添加中立消息，即不利空，也不利好
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-31
     * @version     v1.0
     *
     * @param io_MessageInfo
     * @return
     */
    public boolean addMessageNormal(MessageInfo io_MessageInfo)
    {
        io_MessageInfo.setGoodBad(0);
        return this.addMessage(io_MessageInfo);
    }
    
    
    
    /**
     * 新增消息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-31
     * @version     v1.0
     *
     * @param i_HistoryStockDays
     * @return
     */
    private synchronized boolean addMessage(MessageInfo i_MessageInfo)
    {
        $MessageCaches.putRow(i_MessageInfo.getStockCode() ,i_MessageInfo);
        
        return XJava.getXSQL("XSQL_MessageLog_Add").executeUpdate(i_MessageInfo) == 1;
    }
    
    
    
    /**
     * 获取某一只股票当天所有的消息（有缓存）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-31
     * @version     v1.0
     *
     * @param i_StockCode
     * @return
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<MessageInfo> getMessages(String i_StockCode)
    {
        if ( !$MessageCachesIsInit )
        {
            MessageInfo v_Param = new MessageInfo();
            
            v_Param.setStockDay(Date.getNowTime().getFirstTimeOfDay());
            
            $MessageCaches = (PartitionMap<String ,MessageInfo>)XJava.getXSQL("XSQL_MessageLog_Query").query(v_Param);
            $MessageCachesIsInit = true;
        }
        
        return $MessageCaches.get(i_StockCode);
    }
    
}
