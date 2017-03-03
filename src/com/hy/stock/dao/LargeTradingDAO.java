package com.hy.stock.dao;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.Sum;
import org.hy.common.TablePartitionRID;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.LargeTrading;





/**
 * 大单交易数据的操作DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-13
 * @version     v1.0
 */
@Xjava
public class LargeTradingDAO
{
    
    /** 
     * 今天的大单交易数据的高速缓存
     * Partition.key        按股票代码分区
     * Partition.Map.key    为交易序号
     * Partition.Map.value  为大单交易数据
     */
    private final static TablePartitionRID<String ,LargeTrading> $TodayLargeTradings = new TablePartitionRID<String ,LargeTrading>();
    
    /**
     * 今天的大单交易数据的成交额(万元)分类统计
     * Map.key              股票代码 + 交易类型(B买盘、S卖盘)
     * Sum.key              交易序号            
     */
    private final static Map<String ,Sum<String>>                $TodayTradingsSum   = new Hashtable<String ,Sum<String>>();
    
    
    
    /**
     * 获取某只股票：大单累计买入(万元)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     * @param i_StockCode
     * @return
     */
    public static double getTodayTradingsSum_Buy(String i_StockCode)
    {
        Sum<String> v_Sum = $TodayTradingsSum.get(i_StockCode + "B");
        if ( v_Sum != null )
        {
            return v_Sum.getSumValue();
        }
        else
        {
            return 0;
        }
    }
    
    
    
    /**
     * 获取某只股票：大单累计卖出(万元)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     * @param i_StockCode
     * @return
     */
    public static double getTodayTradingsSum_Sell(String i_StockCode)
    {
        Sum<String> v_Sum = $TodayTradingsSum.get(i_StockCode + "S");
        if ( v_Sum != null )
        {
            return v_Sum.getSumValue();
        }
        else
        {
            return 0;
        }
    }
    
    
    
    /**
     * 获取某只股票：大单累计增减仓(万元)
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-24
     * @version     v1.0
     *
     * @param i_StockCode
     * @return
     */
    public static double getTodayTradingsSum_Diff(String i_StockCode)
    {
        return getTodayTradingsSum_Buy(i_StockCode) - getTodayTradingsSum_Sell(i_StockCode);
    }
    
    
    
    /**
     * 新增大单交易数据
     * 
     * 当缓存中有时，不再向数据库写值
     *
     * @author      ZhengWei(HY)
     * @createDate  2016-07-13
     * @version     v1.0
     *
     * @param i_StockInfos
     * @return
     */
    public synchronized List<LargeTrading> addLargeTradings(String i_StockCode ,List<LargeTrading> i_LargeTradings)
    {
        List<LargeTrading> v_LargeTradings = new ArrayList<LargeTrading>();
        
        if ( $TodayLargeTradings.containsKey(i_StockCode) )
        {
            Map<String ,LargeTrading> v_MyLargeTrading = $TodayLargeTradings.get(i_StockCode);
            
            for (int v_Index=0; v_Index<i_LargeTradings.size(); v_Index++)
            {
                LargeTrading v_LargeTrading = i_LargeTradings.get(v_Index);
                
                if ( v_MyLargeTrading.containsKey(v_LargeTrading.getTradingNo()) )
                {
                    break;
                }
                
                $TodayLargeTradings.putRow(i_StockCode ,v_LargeTrading.getTradingNo() ,v_LargeTrading);
                $TodayTradingsSum.get(i_StockCode + v_LargeTrading.getTradingType()).set(v_LargeTrading.getTradingNo() ,v_LargeTrading.getBargainSum());
                v_LargeTradings.add(v_LargeTrading);
            }
        }
        else
        {
            $TodayTradingsSum.put(i_StockCode + "B" ,new Sum<String>());
            $TodayTradingsSum.put(i_StockCode + "S" ,new Sum<String>());
            $TodayTradingsSum.put(i_StockCode + "M" ,new Sum<String>());
            
            for (int v_Index=0; v_Index<i_LargeTradings.size(); v_Index++)
            {
                LargeTrading v_LargeTrading = i_LargeTradings.get(v_Index);
                
                $TodayLargeTradings.putRow(i_StockCode ,v_LargeTrading.getTradingNo() ,v_LargeTrading);
                $TodayTradingsSum.get(i_StockCode + v_LargeTrading.getTradingType()).set(v_LargeTrading.getTradingNo() ,v_LargeTrading.getBargainSum());
                v_LargeTradings.add(v_LargeTrading);
            }
        }
        
        if ( Help.isNull(v_LargeTradings) )
        {
            return v_LargeTradings;
        }
        
        
        // 统计累计买入、累计卖出、主力增减仓
        for (LargeTrading v_LargeTrading : v_LargeTradings)
        {
            v_LargeTrading.setBuySum ($TodayTradingsSum.get(i_StockCode + "B").getSumValue());
            v_LargeTrading.setSellSum($TodayTradingsSum.get(i_StockCode + "S").getSumValue());
            v_LargeTrading.setDifferenceSum(v_LargeTrading.getBuySum() - v_LargeTrading.getSellSum());
        }
        
        try
        {
            XJava.getXSQL("XSQL_LargeTrading_Add").executeUpdates(v_LargeTradings);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return v_LargeTradings;
    }
    
    
    
    /**
     * 查询大单交易数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-14
     * @version     v1.0
     *
     * @param i_LargeTrading
     * @return
     */
    @SuppressWarnings("unchecked")
    public TablePartitionRID<String ,LargeTrading> queryLargeTradings(LargeTrading i_LargeTrading)
    {
        return (TablePartitionRID<String ,LargeTrading>)XJava.getXSQL("XSQL_LargeTrading_Query").query(i_LargeTrading);
    }
    
    
    
    /**
     * 缓存当天已保存在数据库中的大单交易数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-14
     * @version     v1.0
     *
     */
    public synchronized void cacheLargeTradings()
    {
        LargeTrading v_Param = new LargeTrading();
        
        v_Param.setTime(Date.getNowTime().getFirstTimeOfDay());
        
        TablePartitionRID<String ,LargeTrading> v_TodayDatas = this.queryLargeTradings(v_Param);
        
        $TodayLargeTradings.clear();
        $TodayTradingsSum  .clear();
        
        if ( !Help.isNull(v_TodayDatas) )
        {
            $TodayLargeTradings.putAll(v_TodayDatas);
            
            for (Entry<String ,Map<String ,LargeTrading>> v_Item : $TodayLargeTradings.entrySet())
            {
                $TodayTradingsSum.put(v_Item.getKey() + "B" ,new Sum<String>());
                $TodayTradingsSum.put(v_Item.getKey() + "S" ,new Sum<String>());
                $TodayTradingsSum.put(v_Item.getKey() + "M" ,new Sum<String>());
                
                for (LargeTrading v_LargeTrading : v_Item.getValue().values())
                {
                    $TodayTradingsSum.get(v_LargeTrading.getStockCode() + v_LargeTrading.getTradingType()).set(v_LargeTrading.getTradingNo() ,v_LargeTrading.getBargainSum());
                }
            }
        }
        
        System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  高速缓存当天的大单交易数据 " + $TodayLargeTradings.rowCount() + "笔.");
    }
    
}
