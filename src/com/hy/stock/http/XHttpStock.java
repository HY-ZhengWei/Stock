package com.hy.stock.http;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hy.common.Help;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.xml.XHttp;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.StockInfo;





/**
 * 股票接口
 * 
 *  http://sqt.gtimg.cn/utf8/q=股票代码01,股票代码02&offset=1,2,3,4,31,32,33,38
 *                                                offset返回结果字段的索引号
 *   返回结果样例：v_sh600887="1
 *            2            ~伊利股份                     股票名称
 *            3            ~600887                      股票代码
 *            4            ~16.32                       最新报价
 *            5            ~16.31                       昨收
 *            6            ~16.31                       今开
 *            7            ~180204                      成交量(手)。除以100后单位为：万股
 *            8            ~94954                       外盘
 *            9            ~85250                       内盘
 *           10            ~16.31                       五档盘口:买01:元
 *           11            ~33                          五档盘口:买01:手
 *           12            ~16.30                       五档盘口:买02:元
 *           13            ~912                         五档盘口:买02:手
 *           14            ~16.29                       五档盘口:买03:元
 *           15            ~264                         五档盘口:买03:手
 *           16            ~16.28                       五档盘口:买04:元
 *           17            ~591                         五档盘口:买04:手
 *           18            ~16.27                       五档盘口:买05:元
 *           19            ~194                         五档盘口:买05:手
 *           20            ~16.32                       五档盘口:卖01:元
 *           21            ~793                         五档盘口:卖01:手
 *           22            ~16.33                       五档盘口:卖02:元
 *           23            ~1976                        五档盘口:卖02:手
 *           24            ~16.34                       五档盘口:卖03:元
 *           25            ~662                         五档盘口:卖03:手
 *           26            ~16.35                       五档盘口:卖04:元
 *           27            ~1217                        五档盘口:卖04:手
 *           28            ~16.36                       五档盘口:卖05:元
 *           29            ~461                         五档盘口:卖05:手
 *           30            ~15:00:02/16.32/63/B/102774/13108|14:59:56/16.32/60/B/97890/13103|14:59:47/16.32/98/B/159893/13095|14:59:47/16.32/148/B/241481/13092|14:59:41/16.32/162/B/264330/13087|14:59:37/16.32/139/B/226781/13084
 *           31            ~20160622150541              时间
 *           32            ~0.01                        涨跌额（单位：元）
 *           33            ~0.06                        涨跌幅 %
 *           34            ~16.42                       今日最高
 *           35            ~16.16                       今日最低
 *           36            ~16.32/180141/293078740
 *           37            ~180204                      成交量(手)。除以100后单位为：万股
 *           38            ~29318                       成交额（单位：万元）
 *           39            ~0.30                        换手率 %
 *           40            ~20.27                       市盈率
 *           41            ~
 *           42            ~16.42
 *           43            ~16.16
 *           44            ~1.59                        振幅 %
 *           45            ~984.65                      流通市值（单位：亿元）
 *           46            ~989.78                      总市值（单位：亿元）
 *           47            ~4.59                        市净率
 *           48            ~17.94
 *           49            ~14.68
 *           50            ~";
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-06-22
 * @version     v1.0
 */
@Xjava
public class XHttpStock
{
    
    /** 
     * 参数Offset，返回结果字段
     * 数组二维的第0下标： 股票接口offset参数值
     * 数组二维的第1下标： 股票接口offset参数值的注释说明
     * 数组二维的第2下标： 股票接口返回结果集对应Java属性名称
     */
    public  static final String [][] $StockResults = {{"31" ,"时间"    ,"time"}
                                                     ,{"2"  ,"股票名称" ,"stockName"}
                                                     ,{"3"  ,"股票代码" ,"stockCode"}
                                                     ,{"5"  ,"昨收"    ,"yesterdayClosePrice"}
                                                     ,{"6"  ,"今开"    ,"todayOpenPrice"}
                                                     ,{"4"  ,"最新报价" ,"newPrice"}
                                                     ,{"34" ,"今日最高" ,"maxPrice"}
                                                     ,{"35" ,"今日最低" ,"minPrice"}
                                                     ,{"32" ,"涨跌额"   ,"upDownPrice"}
                                                     ,{"33" ,"涨跌幅"   ,"upDownRange"}
                                                     ,{"37" ,"成交量"   ,"bargainSum"}
                                                     ,{"38" ,"成交额"   ,"bargainSize"}
                                                     ,{"39" ,"换手率"   ,"turnoverRate"}
                                                     ,{"44" ,"振幅"    ,"swingRate"}
                                                     ,{"8"  ,"外盘"    ,"outSize"}
                                                     ,{"9"  ,"内盘"    ,"inSize"}
                                                     ,{"40" ,"市盈率"   ,"priceEarningRatio"}
                                                     ,{"47" ,"市净率"   ,"priceBookValueRatio"}};
    
    /** 股票接口offset参数 */
    public static final String $Offsets;
    
    /** 股票接口offset参数对应Java属性的属性索引号 */
    public static final int [] $ToJavaIndexes;
    
    
    
    static
    {
        // 生成股票接口offset参数
        StringBuilder v_Offsets = new StringBuilder();
        v_Offsets.append($StockResults[0][0]);
        for (int v_Index=1; v_Index<$StockResults.length; v_Index++)
        {
            v_Offsets.append(",").append($StockResults[v_Index][0]);
        }
        $Offsets = v_Offsets.toString();
        
        
        // 生成股票接口offset参数对应Java属性的属性索引号
        $ToJavaIndexes     = new int[$StockResults.length];
        StockInfo v_Sample = new StockInfo();
        for (int v_Index=0; v_Index<$StockResults.length; v_Index++)
        {
            $ToJavaIndexes[v_Index] = Help.gatPropertyIndex(v_Sample ,$StockResults[v_Index][2]);
        }
    }
    
    
    
    public String request(String i_StockCodes)
    {
        XHttp               v_XHttp  = (XHttp)XJava.getObject("HTTP_Stock");
        Map<String ,String> v_Params = new Hashtable<String ,String>();
        
        v_Params.put("q"      ,i_StockCodes);
        v_Params.put("offset" ,$Offsets);

        Return<?> v_Response = v_XHttp.request(v_Params);
        
        return v_Response.paramStr;
    }
    
    
    
    /**
     * 解释股票接口返回的结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-08
     * @version     v1.0
     *
     * @param i_StocksSubGroupName  股票分组名称
     * @param i_HttpResult
     */
    public List<StockInfo> parseHttpResult(String i_StocksSubGroupName ,String i_HttpResult)
    {
        try
        {
            List<StockInfo> v_Stocks       = new ArrayList<StockInfo>();
            String []       v_ResultStocks = i_HttpResult.split(";");
            for (int v_Index=0; v_Index<v_ResultStocks.length; v_Index++)
            {
                String [] v_ChinaStock = v_ResultStocks[v_Index].split("=")[1].replaceAll("\"" ,"").split("~");
                StockInfo v_Stock      = new StockInfo(v_ChinaStock ,XHttpStock.$ToJavaIndexes);
                
                v_Stock.setId(StringHelp.getUUID());
                v_Stock.setGroupName(i_StocksSubGroupName);
                v_Stock.setStockCode(v_ResultStocks[v_Index].split("=")[0].replaceAll("v_" ,"").toLowerCase().trim());
                
                v_Stocks.add(v_Stock);
            }
            
            return v_Stocks;
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        return null;
    }
    
}
