package com.hy.stock.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.Sum;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;
import org.hy.common.xml.plugins.XSQLGroup;
import org.hy.common.xml.plugins.XSQLGroupResult;

import com.hy.stock.bean.StockInfoDay;
import com.hy.stock.calculate.KDJ;
import com.hy.stock.calculate.MA;
import com.hy.stock.calculate.MACD;
import com.hy.stock.calculate.WR;





/**
 * 股票每天最终交易数据操作DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-12
 * @version     v1.0
 */
@Xjava
public class StockInfoDayDAO
{
    
    /**
     *  股票每天最终交易数据的高速缓存（最多只保留最近8个交易日的数据）（最新日期的排在前面）
     *  Partition.key    股票代码 + DataType_Http
     *  Partition.value  每天最终交易数据
     */
    private static PartitionMap<String ,StockInfoDay> $StockInfoDay8;
    
    
    
    /**
     * 查询每天最后交易的信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-13
     * @version     v1.0
     *
     * @return
     */
    public synchronized PartitionMap<String ,StockInfoDay> queryStockInfoDays()
    {
        if ( Help.isNull($StockInfoDay8) )
        {
            this.cacheStockInfoDays();
        }
        
        return $StockInfoDay8;
    }
    
    
    
    /**
     * 查询（所有股票）每天最后交易的信息，只获取最近X个交易日的数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @param i_DayCount         多少个交易日
     * @return  Partition.key    股票代码 + DataType_Http
     *          Partition.value  每天最终交易数据
     */
    public PartitionMap<String ,StockInfoDay> queryStockInfoDays(int i_DayCount)
    {
        return queryStockInfoDays(i_DayCount ,null);
    }
    
    
    
    /**
     * 查询（所有股票或某一只股票）每天最后交易的信息，只获取最近X个交易日的数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     *
     * @param i_DayCount         多少个交易日
     * @param i_StockCode        股票代码。可为NULL，表示查询所有股票
     * @return  Partition.key    股票代码 + DataType_Http
     *          Partition.value  每天最终交易数据
     */
    @SuppressWarnings("unchecked")
    public PartitionMap<String ,StockInfoDay> queryStockInfoDays(int i_DayCount ,String i_StockCode)
    {
        Map<String ,Object> v_XParam    = new HashMap<String ,Object>(1);
        List<StockInfoDay>  v_XWorkTime = null;
        StockInfoDay        v_Param     = new StockInfoDay();
        
        v_XParam.put("StockCode" ,i_StockCode);
        v_XParam.put("DayCount"  ,String.valueOf(i_DayCount));
        v_XWorkTime = (List<StockInfoDay>)XJava.getXSQL("XSQL_StockInfoDay_Query_XWorkMinTime").query(v_XParam);
        
        v_Param.setStockCode(i_StockCode);
        if ( Help.isNull(v_XWorkTime) )
        {
            v_Param.setStockDay(Date.getNowTime().getDateByWork(i_DayCount * -1).getFirstTimeOfDay());
        }
        else
        {
            v_Param.setStockDay(v_XWorkTime.get(0).getStockDay());
        }
        
        XSQLGroup       v_GXSQL = XJava.getXSQLGroup("GXSQL_StockInfoDay_Query");
        XSQLGroupResult v_Ret   = v_GXSQL.executes(v_Param);
        
        return (PartitionMap<String ,StockInfoDay>)v_Ret.getReturns().get("StockInfoDays");
    }
    
    
    
    /**
     * 查询每天最后交易的信息的高速缓存（最多只保留最近8个交易日的数据）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-13
     * @version     v1.0
     *
     */
    public synchronized void cacheStockInfoDays()
    {
        PartitionMap<String ,StockInfoDay> v_HistoryDatas = $StockInfoDay8;
        $StockInfoDay8 = queryStockInfoDays(8);
        if ( !Help.isNull(v_HistoryDatas) )
        {
            v_HistoryDatas.clear();
            v_HistoryDatas = null;
        }
        
        System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  高速缓存每天最后交易数据" + $StockInfoDay8.rowCount() + "条.");
    }
    
    
    
    /**
     * 更新每天最后交易数据的缓存，并将最新的交易数据放在最前面（下标为0的位置），同时将所有元素后移，删除最后一个元素
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-24
     * @version     v1.0
     *
     * @param i_StockInfoDay
     */
    public synchronized void moveFirstStockInfoDay(StockInfoDay i_StockInfoDay)
    {
        List<StockInfoDay> v_Stocks = $StockInfoDay8.get(i_StockInfoDay.getStockCode() + i_StockInfoDay.getDataType());
        
        if ( !Help.isNull(v_Stocks) )
        {
            if ( v_Stocks.size() >= 2 )
            {
                for (int v_DayIndex=v_Stocks.size()-1; v_DayIndex>=1; v_DayIndex--)
                {
                    v_Stocks.set(v_DayIndex ,v_Stocks.get(v_DayIndex - 1));
                }
            }
            
            v_Stocks.set(0 ,i_StockInfoDay);
        }
    }
    
    
    
    /**
     * 查询每天最终交易数据的数据类型（除权、不复权、前复权、后复权）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-25
     * @version     v1.0
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String ,Object>> queryDataTypes()
    {
        return (List<Map<String ,Object>>)XJava.getXSQL("XSQL_StockInfoDay_Query_DataType").query();
    }
    
    
    
    /**
     * 查询每只股票的最小交易时间
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-21
     * @version     v1.0
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<StockInfoDay> queryMinStockDays(String i_DataType)
    {
        Map<String ,Object> v_Params = new HashMap<String ,Object>();
        
        v_Params.put("DataType" ,i_DataType);
        
        return (List<StockInfoDay>)XJava.getXSQL("XSQL_StockInfoDay_Query_MinStockDay").query(v_Params);
    }
    
    
    
    /**
     * 新增股票每天最终交易数据（历史的概要数据）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     *
     * @param i_HistoryStockDays
     * @return
     */
    public boolean addHistorys(List<StockInfoDay> i_HistoryStockDays)
    {
        return XJava.getXSQL("XSQL_StockInfoDay_AddHistory").executeUpdates(i_HistoryStockDays) == i_HistoryStockDays.size();
    }
    
    
    
    
    
    
    
    /**
     * 生成股票每天最终交易数据（默认对最近一周的数据进行日结）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-25
     * @version     v1.0
     *
     */
    public void makeStockInfoDays()
    {
        this.makeStockInfoDays(Date.getNowTime().getPreviousWeek() ,Date.getNowTime());
    }
    
    
    
    /**
     * 生成股票每天最终交易数据
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-12
     * @version     v1.0
     *
     * @param i_StockInfos
     * @return
     */
    public void makeStockInfoDays(Date i_BeginTime ,Date i_EndTime)
    {
        Map<String ,String> v_Params = new HashMap<String ,String>();
        
        v_Params.put("BeginTime" ,i_BeginTime.getFirstTimeOfDay().getFull());
        v_Params.put("EndTime"   ,i_EndTime  .getFirstTimeOfDay().getFull());
        
        try
        {
            XSQLGroup       v_GXSQL = ((XSQLGroup)XJava.getObject("GXSQL_StockInfoDay_ADD"));
            XSQLGroupResult v_Ret   = v_GXSQL.executes(v_Params);
            
            if ( !v_Ret.isSuccess() )
            {
                v_GXSQL.logReturn(v_Ret);
            }
            else
            {
                System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  新增" + v_Ret.getExecSumCount().getSumValue() + "只股票每天最终交易数据。");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        this.cacheStockInfoDays();
    }
    
    
    
    /**
     * 1. 生成股票每天最终交易数据的过程中，生成KDJ指标
     * 2. 生成股票每天最终交易数据的过程中，生成MACD指标
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-19
     * @version     v1.0
     *
     * @param io_Params
     * @param io_Returns
     * @return
     */
    public boolean makeStockInfoDays_Calc(Map<String ,Object> io_Params ,Map<String ,Object> io_Returns)
    {
        StockInfoDay       v_RowStock      = new StockInfoDay(io_Params);
        String             v_DataType_Http = io_Params.get("DataType_Http").toString();
        List<StockInfoDay> v_StockDays     = this.queryStockInfoDays().get(v_RowStock.getStockCode() + v_DataType_Http);
        KDJ                v_KDJ           = new KDJ();
        MACD               v_MACD          = new MACD();
        WR                 v_WR            = new WR();
        Double             v_MaxPriceX     = 0D;
        Double             v_MinPriceX     = 0D;
        Double             v_MaxPriceY     = 0D;
        Double             v_MinPriceY     = 0D;
        Double             v_MaxPriceZ     = 0D;
        Double             v_MinPriceZ     = 0D;
        
        v_RowStock.setStockDayNo(0);
        
        if ( !Help.isNull(v_StockDays) )
        {
            StockInfoDay v_Yesterday = v_StockDays.get(0);
            
            if ( v_Yesterday.getStockDayNo() != null && v_Yesterday.getStockDayNo() >= 1 )
            {
                v_RowStock.setStockDayNo(v_Yesterday.getStockDayNo() + 1);
            }
            
            // 计算KDJ
            if ( null != v_Yesterday.getK() && v_Yesterday.getK() > KDJ.$KDJ_InitValue
              && null != v_Yesterday.getD() && v_Yesterday.getD() > KDJ.$KDJ_InitValue
              && v_StockDays.size() >= 8 )
            {
                v_MaxPriceX = Math.max(v_RowStock.getMaxPrice() ,v_Yesterday.getMaxPriceXDay());
                v_MinPriceX = Math.min(v_RowStock.getMinPrice() ,v_Yesterday.getMinPriceXDay());
                
                v_KDJ = KDJ.calcKDJ(v_RowStock.getNewPrice()
                                   ,v_MinPriceX
                                   ,v_MaxPriceX
                                   ,v_Yesterday.getK()
                                   ,v_Yesterday.getD());
            }
            
            
            // 计算MACD
            if ( null != v_Yesterday.getEma12() && v_Yesterday.getEma12() > MACD.$MACD_InitValue
              && null != v_Yesterday.getEma26() && v_Yesterday.getEma26() > MACD.$MACD_InitValue
              && null != v_Yesterday.getDea()   && v_Yesterday.getDea()   > MACD.$MACD_InitValue )
            {
                v_MACD = MACD.calcMACD(v_RowStock.getNewPrice() 
                                      ,v_Yesterday.getEma12() 
                                      ,v_Yesterday.getEma26()
                                      ,v_Yesterday.getDea());
            }
            
            if ( null != v_Yesterday.getWr6 () && v_Yesterday.getWr6 () > WR.$WR_InitValue
              && null != v_Yesterday.getWr10() && v_Yesterday.getWr10() > WR.$WR_InitValue )
            {
                v_MaxPriceY = Math.max(v_RowStock.getMaxPrice() ,v_Yesterday.getMaxPriceYDay());
                v_MinPriceY = Math.min(v_RowStock.getMinPrice() ,v_Yesterday.getMinPriceYDay());
                v_MaxPriceZ = Math.max(v_RowStock.getMaxPrice() ,v_Yesterday.getMaxPriceZDay());
                v_MinPriceZ = Math.min(v_RowStock.getMinPrice() ,v_Yesterday.getMinPriceZDay());
                
                v_WR.setWr6 (WR.calcWR(v_RowStock.getNewPrice() 
                                      ,v_MinPriceY
                                      ,v_MaxPriceY));
                v_WR.setWr10(WR.calcWR(v_RowStock.getNewPrice() 
                                      ,v_MinPriceZ
                                      ,v_MaxPriceZ));
            }
        }
        
        io_Params.put("k"          ,v_KDJ.getK());
        io_Params.put("d"          ,v_KDJ.getD());
        io_Params.put("j"          ,v_KDJ.getJ());
        
        io_Params.put("ema12"      ,v_MACD.getEma12());
        io_Params.put("ema26"      ,v_MACD.getEma26());
        io_Params.put("dif"        ,v_MACD.getDif());
        io_Params.put("dea"        ,v_MACD.getDea());
        io_Params.put("macd"       ,v_MACD.getBar());
        
        io_Params.put("wr6"        ,v_WR.getWr6 ());
        io_Params.put("wr10"       ,v_WR.getWr10());
        
        io_Params.put("stockDayNo" ,v_RowStock.getStockDayNo());
        
        
        v_RowStock.setK(           v_KDJ.getK());
        v_RowStock.setD(           v_KDJ.getD());
        v_RowStock.setJ(           v_KDJ.getJ());
        
        v_RowStock.setEma12(       v_MACD.getEma12());
        v_RowStock.setEma26(       v_MACD.getEma26());
        v_RowStock.setDif(         v_MACD.getDif());
        v_RowStock.setDea(         v_MACD.getDea());
        v_RowStock.setMacd(        v_MACD.getBar());
        
        v_RowStock.setK(           v_WR.getWr6());
        v_RowStock.setD(           v_WR.getWr10());
        
        v_RowStock.setMaxPriceXDay(v_MaxPriceX);
        v_RowStock.setMinPriceXDay(v_MinPriceX);
        v_RowStock.setMaxPriceYDay(v_MaxPriceY);
        v_RowStock.setMinPriceYDay(v_MinPriceY);
        v_RowStock.setMaxPriceZDay(v_MaxPriceZ);
        v_RowStock.setMinPriceZDay(v_MinPriceZ);
        
        v_RowStock.setDataType(    v_DataType_Http);
        
        this.moveFirstStockInfoDay(v_RowStock);
        
        return true;
    }
    
    
    
    /**
     * 计算KDJ。对所有股票计算，从每只股票的上市第一天，首个交易日开始计算起，一直计算到今天。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     */
    public void calcKDJ_FirstDayToToday()
    {
        this.calcKDJ_DayToTody(null);
    }

    
    
    /**
     * 计算KDJ。对所有股票计算，从每只股票的某一天开始计算KDJ，一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算KDJ有一个前提：指定时期的当天，必须已有KDJ指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有KDJ值。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcKDJ_DayToTody(Date i_Day)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockDay(i_Day);
        
        calcKDJ_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算KDJ。从某一天开始计算KDJ，一直到计算到今天。
     * 
     *   1. 当指定股票代码(stockCode)时，只计算此股票的KDJ指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcKDJ_CodeToTody(String i_StockCode)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockCode(i_StockCode);
        
        calcKDJ_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算KDJ。一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算KDJ有一个前提：指定时期的当天，必须已有KDJ指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有KDJ值。
     *   3. 当指定股票代码(stockCode)时，只计算此股票的KDJ指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     * @param i_StockInfoDay  可选参数如下：
     *                        1. stockDay  股票交易日期
     *                        2. stockCode 股票代码
     *                        3. stockDay + stockCode 的组合
     */
    public synchronized void calcKDJ_ToTody(StockInfoDay i_StockInfoDay)
    {
        try
        {
            Map<String ,Object> v_Params = new HashMap<String ,Object>();
            v_Params.put("QueryStockDay"  ,i_StockInfoDay.getStockDay());
            v_Params.put("QueryStockCode" ,i_StockInfoDay.getStockCode());
            
            XSQLGroup       v_GXSQL  = XJava.getXSQLGroup("XSQL_StockInfoDay_CalcKDJ");
            XSQLGroupResult v_Ret    = v_GXSQL.executes(v_Params); 
            
            if ( !v_Ret.isSuccess() )
            {
                v_GXSQL.logReturn(v_Ret);
            }
            else
            {
                System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  更新计算KDJ " + v_Ret.getExecSumCount().getSumValue() + "条每天最终交易数据。");
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
    
    
    /**
     * 计算KDJ。一直到计算到今天。（XSQLNode节点）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-30
     * @version     v1.0
     *
     * @param io_Params
     * @param io_Returns
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean calcKDJ_ToToday_XSQLNode(Map<String ,Object> io_Params ,Map<String ,Object> io_Returns)
    {
        try
        {
            StockInfoDay v_RowStock  = new StockInfoDay(io_Params);
            StockInfoDay v_Yesterday = null;
            KDJ          v_KDJ       = new KDJ();
            boolean      v_IsFirst   = false;
            
            if ( v_RowStock.getStockDayNo() == 9 )
            {
                // 首条股票的首条记录，并且此记录为上市后第9个交易日
                v_KDJ.setK(0);
                v_KDJ.setD(0);
                v_KDJ.setJ(KDJ.calcJ(v_KDJ.getK() ,v_KDJ.getD()));
                v_IsFirst = true;
            }
            else if ( io_Params.get(XSQLGroup.$Param_RowPrevious) == null )
            {
                // 首条股票的首条记录
                v_KDJ.setK(Help.NVL(v_RowStock.getK() ,0).doubleValue());
                v_KDJ.setD(Help.NVL(v_RowStock.getD() ,0).doubleValue());
                v_KDJ.setJ(KDJ.calcJ(v_KDJ.getK() ,v_KDJ.getD()));
                v_IsFirst = true;
            }
            else
            {
                v_Yesterday = new StockInfoDay((Map<String ,Object>)io_Params.get(XSQLGroup.$Param_RowPrevious));
                    
                if ( v_Yesterday.getStockCode().equals(v_RowStock.getStockCode()) )
                {
                    v_KDJ = KDJ.calcKDJ(v_RowStock.getNewPrice() 
                                       ,v_RowStock.getMinPriceXDay() 
                                       ,v_RowStock.getMaxPriceXDay() 
                                       ,v_Yesterday.getK() 
                                       ,v_Yesterday.getD());
                }
                else
                {
                    // 每股票的首条记录
                    v_KDJ.setK(Help.NVL(v_RowStock.getK() ,0).doubleValue());
                    v_KDJ.setD(Help.NVL(v_RowStock.getD() ,0).doubleValue());
                    v_KDJ.setJ(KDJ.calcJ(v_KDJ.getK() ,v_KDJ.getD()));
                    v_IsFirst = true;
                }
            }
            
            if ( v_IsFirst )
            {
                System.out.println("-- " + Date.getNowTime().getFullMilli() + "  KDJ：" + v_RowStock.getStockName() + " " + v_RowStock.getStockCode() + " " + v_RowStock.getStockDay().getYMD());
            }
            
            io_Params.put("k"        ,v_KDJ.getK());
            io_Params.put("d"        ,v_KDJ.getD());
            io_Params.put("j"        ,v_KDJ.getJ());
            io_Params.put("IsUpdate" ,1);
            
            return true;
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return false;
    }
    
    
    
    /**
     * 从股票的上市第一天，首个交易日开始计算起，一直计算到今天。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-21
     * @version     v1.0
     */
    public void calcMACD_FirstDayToToday()
    {
        calcMACD_DayToTody(null);
    }
    
    
    
    /**
     * 计算MACD。对所有股票计算，从每只股票的某一天开始计算MACD，一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算MACD有一个前提：指定时期的当天，必须已有MACD指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有MACD值。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcMACD_DayToTody(Date i_Day)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockDay(i_Day);
        
        calcMACD_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算MACD。从某一天开始计算MACD，一直到计算到今天。
     * 
     *   1. 当指定股票代码(stockCode)时，只计算此股票的MACD指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcMACD_CodeToTody(String i_StockCode)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockCode(i_StockCode);
        
        calcMACD_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算MACD。一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算MACD有一个前提：指定时期的当天，必须已有MACD指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有MACD值。
     *   3. 当指定股票代码(stockCode)时，只计算此股票的MACD指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-21
     * @version     v1.0
     *
     * @param i_StockInfoDay  可选参数如下：
     *                        1. stockDay  股票交易日期
     *                        2. stockCode 股票代码
     *                        3. stockDay + stockCode 的组合
     */
    public synchronized void calcMACD_ToTody(StockInfoDay i_StockInfoDay)
    {
        try
        {
            Map<String ,Object> v_Params = new HashMap<String ,Object>();
            v_Params.put("QueryStockDay"  ,i_StockInfoDay.getStockDay());
            v_Params.put("QueryStockCode" ,i_StockInfoDay.getStockCode());
            
            XSQLGroup       v_GXSQL = ((XSQLGroup)XJava.getObject("GXSQL_StockInfoDay_CalcMACD"));
            XSQLGroupResult v_Ret   = v_GXSQL.executes(i_StockInfoDay);
            
            if ( !v_Ret.isSuccess() )
            {
                v_GXSQL.logReturn(v_Ret);
            }
            else
            {
                System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  更新计算MACD " + v_Ret.getExecSumCount().getSumValue() + "条每天最终交易数据。");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    
    
    /**
     * 从股票的上市第一天，首个交易日开始计算起，一直计算到今天。（只计算前复权价格下的MACD）（XSQLNode节点）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-21
     * @version     v1.0
     *
     * @param io_Params
     * @param io_Returns
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean calcMACD_ToToday_XSQLNode(Map<String ,Object> io_Params ,Map<String ,Object> io_Returns)
    {
        try
        {
            StockInfoDay v_RowStock  = new StockInfoDay(io_Params);
            StockInfoDay v_Yesterday = null;
            MACD         v_MACD      = new MACD();
            boolean      v_IsFirst   = false;
            
            if ( v_RowStock.getStockDayNo() == 1 )
            {
                // 首条股票的首条记录，并且此记录为上市的首个交易日
                v_MACD.setEma12(0);
                v_MACD.setEma26(0);
                v_MACD.setDif(  0);
                v_MACD.setDea(  0);
                v_MACD.setBar(  0);
                v_IsFirst = true;
            }
            else if ( io_Params.get(XSQLGroup.$Param_RowPrevious) == null )
            {
                // 首条股票的首条记录
                v_MACD.setEma12(Help.NVL(v_RowStock.getEma12() ,0).doubleValue());
                v_MACD.setEma26(Help.NVL(v_RowStock.getEma26() ,0).doubleValue());
                v_MACD.setDif(  Help.NVL(v_RowStock.getDif()   ,0).doubleValue());
                v_MACD.setDea(  Help.NVL(v_RowStock.getDea()   ,0).doubleValue());
                v_MACD.setBar(  Help.NVL(v_RowStock.getMacd()  ,0).doubleValue());
                v_IsFirst = true;
            }
            else
            {
                v_Yesterday = new StockInfoDay((Map<String ,Object>)io_Params.get(XSQLGroup.$Param_RowPrevious));
                
                if ( v_Yesterday.getStockCode().equals(v_RowStock.getStockCode()) )
                {
                    v_MACD = MACD.calcMACD(v_RowStock.getNewPrice()
                                          ,v_Yesterday.getEma12()
                                          ,v_Yesterday.getEma26()
                                          ,v_Yesterday.getDea());
                }
                else
                {
                    // 每股票的首条记录
                    v_MACD.setEma12(Help.NVL(v_RowStock.getEma12() ,0).doubleValue());
                    v_MACD.setEma26(Help.NVL(v_RowStock.getEma26() ,0).doubleValue());
                    v_MACD.setDif(  Help.NVL(v_RowStock.getDif()   ,0).doubleValue());
                    v_MACD.setDea(  Help.NVL(v_RowStock.getDea()   ,0).doubleValue());
                    v_MACD.setBar(  Help.NVL(v_RowStock.getMacd()  ,0).doubleValue());
                    v_IsFirst = true;
                }
            }
            
            if ( v_IsFirst )
            {
                System.out.println("-- " + Date.getNowTime().getFullMilli() + "  MACD：" + v_RowStock.getStockName() + " " + v_RowStock.getStockCode() + " " + v_RowStock.getStockDay().getYMD());
            }
            
            io_Params.put("ema12"    ,v_MACD.getEma12());
            io_Params.put("ema26"    ,v_MACD.getEma26());
            io_Params.put("dif"      ,v_MACD.getDif());
            io_Params.put("dea"      ,v_MACD.getDea());
            io_Params.put("macd"     ,v_MACD.getBar());
            io_Params.put("IsUpdate" ,1);
            
            return true;
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return false;
    }
    
    
    
    /**
     * 计算WR。对所有股票计算，从每只股票的上市第一天，首个交易日开始计算起，一直计算到今天。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     */
    public void calcWR_FirstDayToToday()
    {
        this.calcWR_DayToTody(null);
    }

    
    
    /**
     * 计算WR。对所有股票计算，从每只股票的某一天开始计算WR，一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算WR有一个前提：指定时期的当天，必须已有WR指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有WR值。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcWR_DayToTody(Date i_Day)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockDay(i_Day);
        
        calcWR_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算WR。从某一天开始计算WR，一直到计算到今天。
     * 
     *   1. 当指定股票代码(stockCode)时，只计算此股票的WR指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcWR_CodeToTody(String i_StockCode)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockCode(i_StockCode);
        
        calcWR_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算WR。一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算WR有一个前提：指定时期的当天，必须已有WR指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有WR值。
     *   3. 当指定股票代码(stockCode)时，只计算此股票的WR指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     * @param i_StockInfoDay  可选参数如下：
     *                        1. stockDay  股票交易日期
     *                        2. stockCode 股票代码
     *                        3. stockDay + stockCode 的组合
     */
    public synchronized void calcWR_ToTody(StockInfoDay i_StockInfoDay)
    {
        try
        {
            Map<String ,Object> v_Params = new HashMap<String ,Object>();
            v_Params.put("QueryStockDay"  ,i_StockInfoDay.getStockDay());
            v_Params.put("QueryStockCode" ,i_StockInfoDay.getStockCode());
            
            XSQLGroup       v_GXSQL  = XJava.getXSQLGroup("XSQL_StockInfoDay_CalcWR");
            XSQLGroupResult v_Ret    = v_GXSQL.executes(v_Params); 
            
            if ( !v_Ret.isSuccess() )
            {
                v_GXSQL.logReturn(v_Ret);
            }
            else
            {
                System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  更新计算WR " + v_Ret.getExecSumCount().getSumValue() + "条每天最终交易数据。");
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
    
    
    /**
     * 计算WR。一直到计算到今天。（XSQLNode节点）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     * @param io_Params
     * @param io_Returns
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean calcWR_ToToday_XSQLNode(Map<String ,Object> io_Params ,Map<String ,Object> io_Returns)
    {
        try
        {
            StockInfoDay v_RowStock  = new StockInfoDay(io_Params);
            StockInfoDay v_Yesterday = null;
            WR           v_WR        = new WR();
            boolean      v_IsFirst   = false;
            
            if ( v_RowStock.getStockDayNo() == 10 )
            {
                // 首条股票的首条记录，并且此记录为上市后第10个交易日
                v_WR.setWr6 (0);
                v_WR.setWr10(0);
                v_IsFirst = true;
            }
            else if ( io_Params.get(XSQLGroup.$Param_RowPrevious) == null )
            {
                // 首条股票的首条记录
                v_WR.setWr6 (Help.NVL(v_RowStock.getWr6 () ,0).doubleValue());
                v_WR.setWr10(Help.NVL(v_RowStock.getWr10() ,0).doubleValue());
                v_IsFirst = true;
            }
            else
            {
                v_Yesterday = new StockInfoDay((Map<String ,Object>)io_Params.get(XSQLGroup.$Param_RowPrevious));
                    
                if ( v_Yesterday.getStockCode().equals(v_RowStock.getStockCode()) )
                {
                    v_WR.setWr6 (WR.calcWR(v_RowStock.getNewPrice() 
                                          ,v_RowStock.getMinPriceXDay() 
                                          ,v_RowStock.getMaxPriceXDay()));
                    v_WR.setWr10(WR.calcWR(v_RowStock.getNewPrice() 
                                          ,v_RowStock.getMinPriceYDay()  
                                          ,v_RowStock.getMaxPriceYDay()));
                }
                else
                {
                    // 每股票的首条记录
                    v_WR.setWr6 (Help.NVL(v_RowStock.getWr6 () ,0).doubleValue());
                    v_WR.setWr10(Help.NVL(v_RowStock.getWr10() ,0).doubleValue());
                    v_IsFirst = true;
                }
            }
            
            if ( v_IsFirst )
            {
                System.out.println("-- " + Date.getNowTime().getFullMilli() + "  WR：" + v_RowStock.getStockName() + " " + v_RowStock.getStockCode() + " " + v_RowStock.getStockDay().getYMD());
            }
            
            io_Params.put("wr6"      ,v_WR.getWr6 ());
            io_Params.put("wr10"     ,v_WR.getWr10());
            io_Params.put("IsUpdate" ,1);
            
            return true;
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return false;
    }
    
    
    
    /**
     * 计算MA移动平均线(均线)。对所有股票计算，从每只股票的上市第一天，首个交易日开始计算起，一直计算到今天。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     */
    public void calcMA_FirstDayToToday()
    {
        this.calcMA_DayToTody(null);
    }

    
    
    /**
     * 计算MA移动平均线(均线)。对所有股票计算，从每只股票的某一天开始计算MA，一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算WR有一个前提：指定时期的当天，必须已有WR指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有WR值。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcMA_DayToTody(Date i_Day)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockDay(i_Day);
        
        calcMA_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算MA移动平均线(均线)。从某一天开始计算MA，一直到计算到今天。
     * 
     *   1. 当指定股票代码(stockCode)时，只计算此股票的WR指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     * @param i_Day
     */
    public void calcMA_CodeToTody(String i_StockCode)
    {
        StockInfoDay v_Params = new StockInfoDay();
        v_Params.setStockCode(i_StockCode);
        
        calcMA_ToTody(v_Params);
    }
    
    
    
    /**
     * 计算MA移动平均线(均线)。一直到计算到今天。
     * 
     *   1. 当指定交易日期(stockDay)时，计算MA有一个前提：指定时期的当天，必须已有MA指标。
     *   2. 当指定交易日期(stockDay)为空时，从股票的上市第一天开始计算，并且无须有MA值。
     *   3. 当指定股票代码(stockCode)时，只计算此股票的MA指标。从上市第一天，首个交易日开始计算起。
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     * @param i_StockInfoDay  可选参数如下：
     *                        1. stockDay  股票交易日期
     *                        2. stockCode 股票代码
     *                        3. stockDay + stockCode 的组合
     */
    public synchronized void calcMA_ToTody(StockInfoDay i_StockInfoDay)
    {
        try
        {
            Map<String ,Object> v_Params = new HashMap<String ,Object>();
            v_Params.put("QueryStockDay"  ,i_StockInfoDay.getStockDay());
            v_Params.put("QueryStockCode" ,i_StockInfoDay.getStockCode());
            
            XSQLGroup       v_GXSQL  = XJava.getXSQLGroup("XSQL_StockInfoDay_CalcMA");
            XSQLGroupResult v_Ret    = v_GXSQL.executes(v_Params); 
            
            if ( !v_Ret.isSuccess() )
            {
                v_GXSQL.logReturn(v_Ret);
            }
            else
            {
                System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  更新计算MA " + v_Ret.getExecSumCount().getSumValue() + "条每天最终交易数据。");
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
    }
    
    
    
    /**
     * 计算MA移动平均线(均线)。一直到计算到今天。（XSQLNode节点）
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     * @param io_Params
     * @param io_Returns
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean calcMA_ToToday_XSQLNode(Map<String ,Object> io_Params ,Map<String ,Object> io_Returns)
    {
        try
        {
            StockInfoDay v_RowStock  = new StockInfoDay(io_Params);
            StockInfoDay v_Yesterday = null;
            MA           v_MA        = new MA();
            Sum<String>  v_SumMA5    = null;
            Sum<String>  v_SumMA10   = null;
            Sum<String>  v_SumMA20   = null;
            Sum<String>  v_SumMA30   = null;
            Sum<String>  v_SumMA60   = null;
            Sum<String>  v_SumMA108  = null;
            boolean      v_IsFirst   = false;
            
            if ( v_RowStock.getStockDayNo() == 1 )
            {
                // 首条股票的首条记录，并且此记录为上市后第1个交易日
                v_SumMA5    = new Sum<String>();
                v_SumMA10   = new Sum<String>();
                v_SumMA20   = new Sum<String>();
                v_SumMA30   = new Sum<String>();
                v_SumMA60   = new Sum<String>();
                v_SumMA108  = new Sum<String>();
                
                v_SumMA5  .setMaxSize(5);
                v_SumMA10 .setMaxSize(10);
                v_SumMA20 .setMaxSize(20);
                v_SumMA30 .setMaxSize(30);
                v_SumMA60 .setMaxSize(60);
                v_SumMA108.setMaxSize(108);
                
                v_SumMA5  .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA10 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA20 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA30 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA60 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA108.put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                
                io_Returns.put("ma5"   ,v_SumMA5);
                io_Returns.put("ma10"  ,v_SumMA10);
                io_Returns.put("ma20"  ,v_SumMA20);
                io_Returns.put("ma30"  ,v_SumMA30);
                io_Returns.put("ma60"  ,v_SumMA60);
                io_Returns.put("ma108" ,v_SumMA108);
                
                v_MA.setMa5  (v_SumMA5  .getAvgValue());
                v_MA.setMa10 (v_SumMA10 .getAvgValue());
                v_MA.setMa20 (v_SumMA20 .getAvgValue());
                v_MA.setMa30 (v_SumMA30 .getAvgValue());
                v_MA.setMa60 (v_SumMA60 .getAvgValue());
                v_MA.setMa108(v_SumMA108.getAvgValue());
                v_IsFirst = true;
            }
            else if ( io_Params.get(XSQLGroup.$Param_RowPrevious) == null )
            {
                // 首条股票的首条记录
                v_SumMA5    = new Sum<String>();
                v_SumMA10   = new Sum<String>();
                v_SumMA20   = new Sum<String>();
                v_SumMA30   = new Sum<String>();
                v_SumMA60   = new Sum<String>();
                v_SumMA108  = new Sum<String>();
                
                v_SumMA5  .setMaxSize(5);
                v_SumMA10 .setMaxSize(10);
                v_SumMA20 .setMaxSize(20);
                v_SumMA30 .setMaxSize(30);
                v_SumMA60 .setMaxSize(60);
                v_SumMA108.setMaxSize(108);
                
                v_SumMA5  .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA10 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA20 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA30 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA60 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                v_SumMA108.put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                
                io_Returns.put("ma5"   ,v_SumMA5);
                io_Returns.put("ma10"  ,v_SumMA10);
                io_Returns.put("ma20"  ,v_SumMA20);
                io_Returns.put("ma30"  ,v_SumMA30);
                io_Returns.put("ma60"  ,v_SumMA60);
                io_Returns.put("ma108" ,v_SumMA108);
                
                v_MA.setMa5  (v_SumMA5  .getAvgValue());
                v_MA.setMa10 (v_SumMA10 .getAvgValue());
                v_MA.setMa20 (v_SumMA20 .getAvgValue());
                v_MA.setMa30 (v_SumMA30 .getAvgValue());
                v_MA.setMa60 (v_SumMA60 .getAvgValue());
                v_MA.setMa108(v_SumMA108.getAvgValue());
                v_IsFirst = true;
            }
            else
            {
                v_Yesterday = new StockInfoDay((Map<String ,Object>)io_Params.get(XSQLGroup.$Param_RowPrevious));
                    
                if ( v_Yesterday.getStockCode().equals(v_RowStock.getStockCode()) )
                {
                    v_SumMA5    = (Sum<String>)io_Returns.get("ma5");
                    v_SumMA10   = (Sum<String>)io_Returns.get("ma10");
                    v_SumMA20   = (Sum<String>)io_Returns.get("ma20");
                    v_SumMA30   = (Sum<String>)io_Returns.get("ma30");
                    v_SumMA60   = (Sum<String>)io_Returns.get("ma60");
                    v_SumMA108  = (Sum<String>)io_Returns.get("ma108");
                    
                    v_SumMA5  .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA10 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA20 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA30 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA60 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA108.put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    
                    v_MA.setMa5  (v_SumMA5  .getAvgValue());
                    v_MA.setMa10 (v_SumMA10 .getAvgValue());
                    v_MA.setMa20 (v_SumMA20 .getAvgValue());
                    v_MA.setMa30 (v_SumMA30 .getAvgValue());
                    v_MA.setMa60 (v_SumMA60 .getAvgValue());
                    v_MA.setMa108(v_SumMA108.getAvgValue());
                }
                else
                {
                    // 每股票的首条记录
                    v_SumMA5    = new Sum<String>();
                    v_SumMA10   = new Sum<String>();
                    v_SumMA20   = new Sum<String>();
                    v_SumMA30   = new Sum<String>();
                    v_SumMA60   = new Sum<String>();
                    v_SumMA108  = new Sum<String>();
                    
                    v_SumMA5  .setMaxSize(5);
                    v_SumMA10 .setMaxSize(10);
                    v_SumMA20 .setMaxSize(20);
                    v_SumMA30 .setMaxSize(30);
                    v_SumMA60 .setMaxSize(60);
                    v_SumMA108.setMaxSize(108);
                    
                    v_SumMA5  .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA10 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA20 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA30 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA60 .put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    v_SumMA108.put(v_RowStock.getStockDayNo().toString() ,v_RowStock.getNewPrice());
                    
                    io_Returns.put("ma5"   ,v_SumMA5);
                    io_Returns.put("ma10"  ,v_SumMA10);
                    io_Returns.put("ma20"  ,v_SumMA20);
                    io_Returns.put("ma30"  ,v_SumMA30);
                    io_Returns.put("ma60"  ,v_SumMA60);
                    io_Returns.put("ma108" ,v_SumMA108);
                    
                    v_MA.setMa5  (v_SumMA5  .getAvgValue());
                    v_MA.setMa10 (v_SumMA10 .getAvgValue());
                    v_MA.setMa20 (v_SumMA20 .getAvgValue());
                    v_MA.setMa30 (v_SumMA30 .getAvgValue());
                    v_MA.setMa60 (v_SumMA60 .getAvgValue());
                    v_MA.setMa108(v_SumMA108.getAvgValue());
                    v_IsFirst = true;
                }
            }
            
            if ( v_IsFirst )
            {
                System.out.println("-- " + Date.getNowTime().getFullMilli() + "  MA：" + v_RowStock.getStockName() + " " + v_RowStock.getStockCode() + " " + v_RowStock.getStockDay().getYMD());
            }
            
            io_Params.put("ma5"      ,Help.round(v_MA.getMa5  () ,2));
            io_Params.put("ma10"     ,Help.round(v_MA.getMa10 () ,2));
            io_Params.put("ma20"     ,Help.round(v_MA.getMa20 () ,2));
            io_Params.put("ma30"     ,Help.round(v_MA.getMa30 () ,2));
            io_Params.put("ma60"     ,Help.round(v_MA.getMa60 () ,2));
            io_Params.put("ma108"    ,Help.round(v_MA.getMa108() ,2));
            io_Params.put("IsUpdate" ,1);
            
            return true;
        }
        catch (Exception exce)
        {
            exce.printStackTrace();
        }
        
        return false;
    }
    
}
