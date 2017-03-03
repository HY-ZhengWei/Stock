package com.hy.stock.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.StringHelp;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.XSQLGroup;
import org.hy.common.xml.plugins.XSQLGroupResult;

import com.hy.stock.bean.StockInfoDay;
import com.hy.stock.dao.StockInfoDayDAO;
import com.hy.stock.http.XHttpStockHistory;





/**
 * 股票每天最终交易数据的服务层
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-22
 * @version     v1.0
 */
@Xjava
public class StockDayService
{
    /** 
     * 减少重复访问 "股票历史交易的概要数据接口"，临时缓存一下
     * Map.key  为stockCode + DataType_Http
     */
    private static Map<String ,List<StockInfoDay>> $ValidationStockDays;
    
    
    
    @Xjava
    private StockInfoDayDAO   stockInfoDayDAO;
    
    @Xjava
    private XHttpStockHistory xhttpStockHistory;
    
    
    
    /**
     * 通过每日交易的概要接口（this.makeStockHistorys()），效验本程序自己日结的每日交易数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-24
     * @version     v1.0
     *
     */
    public void validationStockDays()
    {
        $ValidationStockDays = new HashMap<String ,List<StockInfoDay>>();
        
        System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  通过每日交易的概要接口，效验本程序自己日结的每日交易数据，开始。。。");
        
        XSQLGroup       v_GXSQL = XJava.getXSQLGroup("GXSQL_StockInfoDay_Validations");
        XSQLGroupResult v_Ret   = v_GXSQL.executes();
        
        if ( !v_Ret.isSuccess() )
        {
            v_GXSQL.logReturn(v_Ret);
        }
        else
        {
            System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  通过每日交易的概要接口，效验本程序自己日结的每日交易数据，完成。共修正 " + v_Ret.getExecSumCount().getSumValue() + "条。");
        }
    }
    
    
    
    public boolean validationStockDays_Compare(Map<String ,Object> io_Params ,Map<String ,Object> io_Returns)
    {
        String             v_DataType_Http = io_Params.get("DataType_Http").toString();
        StockInfoDay       v_RowStock      = new StockInfoDay(io_Params);
        List<StockInfoDay> v_StockDays     = $ValidationStockDays.get(v_RowStock.getStockCode() + v_DataType_Http);
        
        if ( Help.isNull(v_StockDays) )
        {
            // 同时获取今年和去年，两年内的数据，防止跨年因起数据错误
            v_StockDays = this.xhttpStockHistory.parseHttpResult(v_RowStock.getStockCode() ,this.xhttpStockHistory.request(v_RowStock.getStockCode() ,v_RowStock.getStockDay().getYear() - 1 ,v_DataType_Http));
            if ( Help.isNull(v_StockDays) )
            {
                v_StockDays = new ArrayList<StockInfoDay>();
            }
            v_StockDays.addAll(this.xhttpStockHistory.parseHttpResult(v_RowStock.getStockCode() ,this.xhttpStockHistory.request(v_RowStock.getStockCode() ,v_RowStock.getStockDay().getYear() ,v_DataType_Http)));
            
            for (int v_Index=1; v_Index<v_StockDays.size(); v_Index++)
            {
                StockInfoDay v_Yesterday = v_StockDays.get(v_Index - 1);
                StockInfoDay v_Today     = v_StockDays.get(v_Index);
                
                v_Today.setYesterdayClosePrice(v_Yesterday.getNewPrice());
                v_Today.setStockName(          v_RowStock.getStockName());
            }
            
            $ValidationStockDays.put(v_RowStock.getStockCode() + v_DataType_Http ,v_StockDays);
        }
        
        if ( Help.isNull(v_StockDays) )
        {
            io_Params.put("IsUpdate" ,0);
            return true;
        }
        
        // 找到对比的日期的交易数据对象
        StockInfoDay v_RigthData = null;
        for (int v_DayIndex=v_StockDays.size()-1; v_DayIndex>=0; v_DayIndex--)
        {
            StockInfoDay v_Item = v_StockDays.get(v_DayIndex);
            
            if ( v_RowStock.getStockDay().equalsYMD(v_Item.getStockDay()) )
            {
                v_RigthData = v_Item;
                break;
            }
        }
        
        if ( v_RigthData == null )
        {
            io_Params.put("IsUpdate" ,0);
            System.out.print(".");
            return true;
        }
        
        if ( v_RigthData.getYesterdayClosePrice().equals(v_RowStock.getYesterdayClosePrice()) 
          && v_RigthData.getTodayOpenPrice()     .equals(v_RowStock.getTodayOpenPrice())
          && v_RigthData.getNewPrice()           .equals(v_RowStock.getNewPrice())
          && v_RigthData.getMaxPrice()           .equals(v_RowStock.getMaxPrice())
          && v_RigthData.getMinPrice()           .equals(v_RowStock.getMinPrice())
          && v_RigthData.getBargainSum()         .equals(v_RowStock.getBargainSum()) )
        {
            io_Params.put("IsUpdate" ,0);
        }
        else
        {
            System.out.println("\n-- 对比出日结错误的数据。其正确数据应为：" + v_RigthData.getStockCode() + " " + v_RigthData.getStockName() + "." + v_DataType_Http + "   " + v_RigthData.getStockDay().getYMD());
            System.out.println("   Right: " + v_RigthData.getYesterdayClosePrice() + "\t\t" 
                                            + v_RigthData.getTodayOpenPrice()      + "\t"
                                            + v_RigthData.getNewPrice()            + "\t" 
                                            + v_RigthData.getMaxPrice()            + "\t" 
                                            + v_RigthData.getMinPrice()            + "\t" 
                                            + v_RigthData.getBargainSum()          + "\t" 
                              );
            System.out.println("   Error: " + v_RowStock.getYesterdayClosePrice()  + "\t\t" 
                                            + v_RowStock.getTodayOpenPrice()       + "\t"
                                            + v_RowStock.getNewPrice()             + "\t" 
                                            + v_RowStock.getMaxPrice()             + "\t" 
                                            + v_RowStock.getMinPrice()             + "\t" 
                                            + v_RowStock.getBargainSum()           + "\t" 
                              );
            
            
            io_Params.put("yesterdayClosePrice" ,v_RigthData.getYesterdayClosePrice());
            io_Params.put("todayOpenPrice"      ,v_RigthData.getTodayOpenPrice());
            io_Params.put("newPrice"            ,v_RigthData.getNewPrice());
            io_Params.put("maxPrice"            ,v_RigthData.getMaxPrice());
            io_Params.put("minPrice"            ,v_RigthData.getMinPrice());
            io_Params.put("bargainSum"          ,v_RigthData.getBargainSum());
            io_Params.put("IsUpdate"            ,1);
        }
        
        return true;
    }
    
    
    
    /**
     * 补充历史每日交易的概要数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     */
    public void makeStockHistorys()
    {
        List<Map<String ,Object>> v_DataTypes    = this.stockInfoDayDAO.queryDataTypes();
        List<StockInfoDay>        v_MinStockDays = null;
        int                       v_NowYear      = Date.getNowTime().getYear();
        
        for (Map<String ,Object> v_DataTypeItem : v_DataTypes)
        {
            String v_DataType_Http = v_DataTypeItem.get("DataType_Http").toString();
            String v_DataType      = v_DataTypeItem.get("DataType")     .toString();
            
            v_MinStockDays = this.stockInfoDayDAO.queryMinStockDays(v_DataType);
            
            for (StockInfoDay v_Item : v_MinStockDays)
            {
                if ( v_Item.getStockDay().getYear() != v_NowYear )
                {
                    continue;
                }
                
                System.out.println("\n\n-- " + Date.getNowTime().getFullMilli() + "  开始补充[" + v_Item.getStockName() + "." + v_DataType_Http + "]的每日交易历史数据...");
                
                Double             v_YesterdayClosePrice = 0D;
                int                v_StockDayNo          = 0;
                List<StockInfoDay> v_StockDays           = null;
                boolean            v_IsAdd               = false;
                for (int v_Year=1990; v_Year<=v_NowYear; v_Year++)
                {
                    v_StockDays = this.xhttpStockHistory.parseHttpResult(v_Item.getStockCode() ,this.xhttpStockHistory.request(v_Item.getStockCode() ,v_Year ,v_DataType_Http));
                    
                    for (int v_Index=v_StockDays.size()-1; v_Index>=0; v_Index--)
                    {
                        StockInfoDay v_StockDay = v_StockDays.get(v_Index);
                        
                        if ( v_StockDay.getStockDay().compareTo(v_Item.getStockDay()) >=0 )
                        {
                            v_StockDays.remove(v_Index);
                        }
                        else
                        {
                            v_StockDay.setDataType( v_DataType);
                            v_StockDay.setId(       StringHelp.getUUID());
                            v_StockDay.setStockName(v_Item.getStockName());
                            v_StockDay.setTime(     v_StockDay.getStockDay());
                        }
                    }
                    
                    if ( !Help.isNull(v_StockDays) )
                    {
                        // 设置今天的"昨收"
                        v_StockDays.get(0).setYesterdayClosePrice(v_YesterdayClosePrice);
                        v_StockDays.get(0).setStockDayNo(++v_StockDayNo);
                        for (int v_Index=1; v_Index<v_StockDays.size(); v_Index++)
                        {
                            StockInfoDay v_Yesterday = v_StockDays.get(v_Index - 1);
                            StockInfoDay v_StockDay  = v_StockDays.get(v_Index);
                            
                            v_StockDay.setYesterdayClosePrice(v_Yesterday.getNewPrice());
                            v_StockDay.setStockDayNo(++v_StockDayNo);
                        }
                        
                        v_YesterdayClosePrice = v_StockDays.get(v_StockDays.size() - 1).getNewPrice();
                        this.stockInfoDayDAO.addHistorys(v_StockDays);
                        v_IsAdd = true;
                        System.out.print(v_Year + " ");
                    }
                }
                
                if ( v_IsAdd )
                {
                    Map<String ,Object> v_Params = new HashMap<String ,Object>(3);
                    XSQLGroup           v_GXSQL  = XJava.getXSQLGroup("GXSQL_StockInfoDay_History_StockDayNo");
                    
                    v_Params.put("DataType"       ,v_DataType);
                    v_Params.put("stockCode"      ,v_Item.getStockCode());
                    v_Params.put("minStockDay"    ,v_Item.getStockDay().getYMD());
                    v_Params.put("baseStockDayNo" ,++v_StockDayNo);
                    
                    XSQLGroupResult v_Ret = v_GXSQL.executes(v_Params);
                    if ( !v_Ret.isSuccess() )
                    {
                        v_GXSQL.logReturn(v_Ret);
                    }
                }
            }
        }
    }


    
    public StockInfoDayDAO getStockInfoDayDAO()
    {
        return stockInfoDayDAO;
    }


    
    public void setStockInfoDayDAO(StockInfoDayDAO stockInfoDayDAO)
    {
        this.stockInfoDayDAO = stockInfoDayDAO;
    }


    
    public XHttpStockHistory getXhttpStockHistory()
    {
        return xhttpStockHistory;
    }


    
    public void setXhttpStockHistory(XHttpStockHistory xhttpStockHistory)
    {
        this.xhttpStockHistory = xhttpStockHistory;
    }
    
}
