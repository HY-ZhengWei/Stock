package com.hy.stock.http;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.StringHelp;
import org.hy.common.xml.XHttp;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.LargeTrading;
import com.hy.stock.bean.StockConfig;





/**
 * 股票大单接口
 * 
 *  http://stock.finance.qq.com/sstock/list/view/dadan.php?t=js&c=sh600887&max=100&p=1&opt=10
 *                                                              c=股票代码
 *                                                                         max=每次获取多少条数据
 *                                                                                 p=分成多少页
 *   返回结果样例：
 *             var v_dadan_data_sh600887=
 *             [1,'    分页页号
 *                交易序号   时间        成交价     成交量(手)  成交额(万元)  性质(B买盘、S卖盘、M)
 *                12820    ~14:59:36  ~18.60    ~613        ~113.993    ~B
 *              ^ 12805    ~14:59:21  ~18.60    ~749        ~139.284    ~S
 *             ']; 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-13
 * @version     v1.0
 */
@Xjava
public class XHttpLargeTrading
{
    
    public String request(String i_StockCodes)
    {
        XHttp               v_XHttp  = (XHttp)XJava.getObject("HTTP_StockLargeTrading");
        Map<String ,String> v_Params = new Hashtable<String ,String>();
        
        v_Params.put("c" ,i_StockCodes);

        Return<?> v_Response = v_XHttp.request(v_Params);
        
        return v_Response.paramStr;
    }
    
    
    
    /**
     * 解释大单交易接口返回的结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-13
     * @version     v1.0
     *
     * @param i_HttpResult
     */
    public List<LargeTrading> parseHttpResult(StockConfig i_StockConfig ,String i_HttpResult)
    {
        List<LargeTrading> v_LargeTradings = new ArrayList<LargeTrading>();
        String             v_NowYMD        = Date.getNowTime().getYMD() + " ";
        
        try
        {
            String [] v_Results = i_HttpResult.split("'")[1].split("\\^");
            for (int v_Index=0; v_Index<v_Results.length; v_Index++)
            {
                String []    v_LargeTradingArr = v_Results[v_Index].split("~");
                LargeTrading v_LargeTrading    = new LargeTrading();
                
                v_LargeTrading.setId(          StringHelp.getUUID());
                v_LargeTrading.setStockCode(   i_StockConfig.getStockCode());
                v_LargeTrading.setStockName(   i_StockConfig.getStockName());
                v_LargeTrading.setTradingNo(                       v_LargeTradingArr[0]);
                v_LargeTrading.setTime(        new Date(v_NowYMD + v_LargeTradingArr[1]));
                v_LargeTrading.setBargainPrice(Double .valueOf(    v_LargeTradingArr[2]));
                v_LargeTrading.setBargainSize( Integer.valueOf(    v_LargeTradingArr[3]));
                v_LargeTrading.setBargainSum(  Double .valueOf(    v_LargeTradingArr[4]));
                v_LargeTrading.setTradingType(                     v_LargeTradingArr[5]);
                
                v_LargeTradings.add(v_LargeTrading);
            }
            
            return v_LargeTradings;
        }
        catch (Exception exce)
        {
            // Nothing.
        }
        
        return null;
    }
    
}
