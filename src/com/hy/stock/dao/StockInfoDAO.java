package com.hy.stock.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.StockInfo;





/**
 * 股票操作DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-08
 * @version     v1.0
 */
@Xjava
public class StockInfoDAO
{
    
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
        try
        {
            return XJava.getXSQL("XSQL_StockInfo_Add").executeUpdates(i_StockInfos) == i_StockInfos.size();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return false;
    }
    
    
    
    /**
     * 删除重复数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-11
     * @version     v1.0
     *
     */
    public void delRepeating()
    {
        try
        {
            XJava.getXSQL("XSQL_StockInfo_DelRepeating").executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * 删除无效数据(每天开盘前的数据)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-12
     * @version     v1.0
     *
     */
    public void delInvalidData()
    {
        this.delInvalid(Date.getNowTime().getFirstTimeOfDay().getFull() 
                       ,Date.getNowTime().getYMD() + " " + XJava.getParam("Morning_Begin")  .getValue().trim());
        
        this.delInvalid(Date.getNowTime().getYMD() + " " + XJava.getParam("Morning_End")    .getValue().trim()
                       ,Date.getNowTime().getYMD() + " " + XJava.getParam("Afternoon_Begin").getValue().trim());
    }
    
    
    
    /**
     * 删除无效数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-12
     * @version     v1.0
     *
     */
    public void delInvalid(String i_BeginTime ,String i_EndTime)
    {
        Map<String ,String> v_Params = new HashMap<String ,String>(2);
        
        v_Params.put("BeginTime" ,i_BeginTime);
        v_Params.put("EndTime"   ,i_EndTime);
        
        try
        {
            XJava.getXSQL("XSQL_StockInfo_DelInvalid").executeUpdate(v_Params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
