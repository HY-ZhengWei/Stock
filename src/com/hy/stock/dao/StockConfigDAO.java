package com.hy.stock.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.PartitionMap;
import org.hy.common.StringHelp;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.StockConfig;





/**
 * 准备抓取的股票代码配置信息的操作DAO
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-11
 * @version     v1.0
 */
@Xjava
public class StockConfigDAO
{
    
    /**
     *  准备抓取的股票代码配置信息的高速缓存
     *  Map.key    股票代码
     *  Map.value  股票对象
     */
    private static Map<String ,StockConfig>          $StockConfigMaps;
    
    /**
     *  准备抓取的股票代码配置信息的高速缓存
     *  Partition.key    分组名称
     *  Partition.value  分组所属的所有股票对象
     */
    private static PartitionMap<String ,StockConfig> $StockConfigPartitions;
    
    /**
     *  准备抓取的股票代码配置信息的高速缓存
     *  Map.key    分组名称
     *  Map.value  分组所属的所有股票代码。用,逗号分隔
     */
    private static Map<String ,String>               $StockConfigs;
    
    
    
    public Map<String ,StockConfig> queryStockConfigMaps()
    {
        this.initStockConfigs();
        return $StockConfigMaps;
    }
    
    
    
    public PartitionMap<String ,StockConfig> queryStockConfigPartitions()
    {
        this.initStockConfigs();
        return $StockConfigPartitions;
    }
    
    
    
    public synchronized Map<String ,String> queryStockConfigs()
    {
        this.initStockConfigs();
        return $StockConfigs;
    }
    
    
    
    private synchronized void initStockConfigs()
    {
        if ( Help.isNull($StockConfigs) )
        {
            cacheStockConfigs();
        }
    }
    
    
    
    /**
     * 高速缓存股票配置信息
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-11
     * @version     v1.0
     */
    @SuppressWarnings("unchecked")
    public synchronized void cacheStockConfigs()
    {
        $StockConfigPartitions = (PartitionMap<String ,StockConfig>)XJava.getXSQL("XSQL_StockConfig_Query").query();
        Map<String ,String>      v_StockConfigMap = new HashMap<String ,String>     ($StockConfigPartitions.rowCount()); 
        Map<String ,StockConfig> v_StockCMap      = new HashMap<String ,StockConfig>($StockConfigPartitions.rowCount());
        
        for (Map.Entry<String ,List<StockConfig>> v_StockConfigSubGroups :$StockConfigPartitions.entrySet())
        {
            v_StockConfigMap.put(v_StockConfigSubGroups.getKey() ,StringHelp.toString(Help.toList(v_StockConfigSubGroups.getValue() ,"stockCode") ,"" ,","));
            
            for (StockConfig v_StockConfig : v_StockConfigSubGroups.getValue())
            {
                v_StockCMap.put(v_StockConfig.getStockCode() ,v_StockConfig);
            }
        }
        
        $StockConfigMaps = v_StockCMap;
        $StockConfigs    = v_StockConfigMap;
        
        System.out.println("\n-- " + Date.getNowTime().getFullMilli() + "  高速缓存" + $StockConfigMaps.size() + "只股票配置信息.");
    }
    
}
