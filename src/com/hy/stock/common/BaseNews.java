package com.hy.stock.common;

import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.file.FileHelp;

import com.hy.stock.bean.MessageInfo;
import com.hy.stock.config.InitConfig;
import com.hy.stock.dao.MessageInfoDAO;





/**
 * 消息基础类 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-13
 * @version     v1.0
 */
public abstract class BaseNews extends Base
{
    
    private static final long serialVersionUID = 5025644680443921830L;
    
    
    
    /** 模板信息的缓存 */
    private final static Map<String ,String>          $TemplateCaches = new Hashtable<String ,String>();
    
    
    
    /**
     * 显示昨天的值
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     * @param i_Value
     * @return
     */
    protected String getYesterdayValue(Double i_Value)
    {
        return "<br><font color='grey'>(" + i_Value.toString() + ")</font>";
    }
    
    
    
    /**
     * 获取当天的消息
     * 
     *  1. 最新的消息显示在最前面。同时，相同类型的消息在其后显示。
     *  2. 不同类型的消息分区域显示，每个区域都是最新的消息显示在最前面。
     *  3. 每个消息区域，总体也是按时间排序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-09-02
     * @version     v1.0
     *
     * @param i_StockCode  股票代码
     * @return
     */
    protected String getNewsHistory(String i_StockCode)
    {
        List<MessageInfo>          v_Msgs    = MessageInfoDAO.getMessages(i_StockCode);
        StringBuilder              v_Title   = new StringBuilder();
        Map<String ,StringBuilder> v_Buffers = null;
        String                     v_NewRow  = "&#13;&#10;";
        
        if ( !Help.isNull(v_Msgs) )
        {
            v_Buffers = new LinkedHashMap<String ,StringBuilder>();
            v_Title.append("title=\"当天消息").append(v_NewRow);
            
            for (int v_Index=v_Msgs.size()-1; v_Index>=0; v_Index--)
            {
                MessageInfo   v_Msg    = v_Msgs.get(v_Index);
                StringBuilder v_Buffer = null;
                
                if ( v_Buffers.containsKey(v_Msg.getMessageType()) )
                {
                    v_Buffer = v_Buffers.get(v_Msg.getMessageType());
                }
                else
                {
                    v_Buffer = new StringBuilder();
                    v_Buffers.put(v_Msg.getMessageType() ,v_Buffer);
                    v_Buffer.append(v_NewRow);
                }
                
                v_Buffer.append(v_Msg.getTime().getHM());
                v_Buffer.append("&nbsp;&nbsp;");
                v_Buffer.append(StringHelp.rpad(v_Msg.getMessageType() ,6 ,"&nbsp;"));
                
                if ( v_Msg.getGoodBad() >= 1 )
                {
                    v_Buffer.append(v_Msg.getMessage());
                }
                else if ( v_Msg.getGoodBad() <= -1 )
                {
                    v_Buffer.append(v_Msg.getMessage());
                }
                else
                {
                    v_Buffer.append(v_Msg.getMessage());
                }
                
                v_Buffer.append(v_NewRow);
            }
            
            for (StringBuilder v_Buffer : v_Buffers.values())
            {
                v_Title.append(v_Buffer.toString());
            }
            v_Title.append("\"");
        }
        
        return v_Title.toString();
    }
    
    
    
    /**
     * 获取当天的消息
     * 
     *  1. 最新的消息显示在最前面。同时，相同类型的消息在其后显示。
     *  2. 不同类型的消息分区域显示，每个区域都是最新的消息显示在最前面。
     *  3. 每个消息区域，总体也是按时间排序
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-09-01
     * @version     v1.0
     *
     * @param i_StockCode  股票代码
     * @return
     */
    protected String getNewsHistoryByJQuery(String i_StockCode)
    {
        List<MessageInfo>          v_Msgs    = MessageInfoDAO.getMessages(i_StockCode);
        StringBuilder              v_Title   = new StringBuilder();
        Map<String ,StringBuilder> v_Buffers = null;
        String                     v_NewRow  = "|";
        
        if ( !Help.isNull(v_Msgs) )
        {
            v_Buffers = new LinkedHashMap<String ,StringBuilder>();
            v_Title.append("title=\"当天消息").append(v_NewRow);
            
            for (int v_Index=v_Msgs.size()-1; v_Index>=0; v_Index--)
            {
                MessageInfo   v_Msg    = v_Msgs.get(v_Index);
                StringBuilder v_Buffer = null;
                
                if ( v_Buffers.containsKey(v_Msg.getMessageType()) )
                {
                    v_Buffer = v_Buffers.get(v_Msg.getMessageType());
                }
                else
                {
                    v_Buffer = new StringBuilder();
                    v_Buffers.put(v_Msg.getMessageType() ,v_Buffer);
                    v_Buffer.append(v_NewRow);
                }
                
                v_Buffer.append(v_Msg.getTime().getHM());
                v_Buffer.append("&nbsp;&nbsp;");
                v_Buffer.append(StringHelp.rpad(v_Msg.getMessageType() ,6 ,"&nbsp;"));
                
                if ( v_Msg.getGoodBad() >= 1 )
                {
                    v_Buffer.append(this.fontColorRed(v_Msg.getMessage()));
                }
                else if ( v_Msg.getGoodBad() <= -1 )
                {
                    v_Buffer.append(this.fontColorGreen(v_Msg.getMessage()));
                }
                else
                {
                    v_Buffer.append(v_Msg.getMessage());
                }
                
                v_Buffer.append(v_NewRow);
            }
            
            for (StringBuilder v_Buffer : v_Buffers.values())
            {
                v_Title.append(v_Buffer.toString());
            }
            v_Title.append("\"");
        }
        
        return v_Title.toString();
    }
    
    
    
    protected String getTemplateStockTitle()
    {
        return this.getTemplateContent("template.StockTitle.html");
    }
    
    
    
    protected String getTemplateStockContent()
    {
        return this.getTemplateContent("template.StockContent.html");
    }
    
    
    
    protected String getTemplateStockKDJTitle()
    {
        return this.getTemplateContent("template.KDJTitle.html");
    }
    
    
    
    protected String getTemplateStockKDJContent()
    {
        return this.getTemplateContent("template.KDJContent.html");
    }
    
    
    
    protected String getTemplateStockMACDTitle()
    {
        return this.getTemplateContent("template.MACDTitle.html");
    }
    
    
    
    protected String getTemplateStockMACDContent()
    {
        return this.getTemplateContent("template.MACDContent.html");
    }
    
    
    
    protected String getTemplateStockWRTitle()
    {
        return this.getTemplateContent("template.WRTitle.html");
    }
    
    
    
    protected String getTemplateStockWRContent()
    {
        return this.getTemplateContent("template.WRContent.html");
    }
    
    
    
    protected String getTemplateStockMATitle()
    {
        return this.getTemplateContent("template.MATitle.html");
    }
    
    
    
    protected String getTemplateStockMAContent()
    {
        return this.getTemplateContent("template.MAContent.html");
    }
    

    
    protected String getTemplateLargeTradingTitle()
    {
        return this.getTemplateContent("template.LargeTradingTitle.html");
    }
    
    
    
    protected String getTemplateLargeTradingContent()
    {
        return this.getTemplateContent("template.LargeTradingContent.html");
    }
    
    
    
    /**
     * 获取模板内容（有缓存机制）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2014-11-19
     * @version     v1.0
     *
     * @param i_TemplateName  模板名称
     * @return
     */
    protected synchronized String getTemplateContent(String i_TemplateName)
    {
        if ( $TemplateCaches.containsKey(i_TemplateName) )
        {
            return $TemplateCaches.get(i_TemplateName);
        }
        
        String v_Content = "";
        
        try
        {
            v_Content = this.getFileContent("template/" + i_TemplateName);
            $TemplateCaches.put(i_TemplateName ,v_Content);
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return v_Content;
    }
    
    
    
    /**
     * 获取文件内容
     * 
     * @author      ZhengWei(HY)
     * @createDate  2014-11-28
     * @version     v1.0
     *
     * @param i_FileName  文件名称(无须文件路径)。
     * @return
     * @throws Exception
     */
    protected String getFileContent(String i_FileName) throws Exception
    {
        return new FileHelp().getContent(InitConfig.class.getResourceAsStream(i_FileName) ,"UTF-8");
    }
    
}
