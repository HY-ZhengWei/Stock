package com.hy.stock.http;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hy.common.Date;
import org.hy.common.Return;
import org.hy.common.xml.XHttp;
import org.hy.common.xml.XJSON;
import org.hy.common.xml.XJava;
import org.hy.common.xml.annotation.Xjava;

import com.hy.stock.bean.StockInfoDay;





/**
 * 股票历史交易的概要数据接口
 * 
 *  http://web.ifzq.gtimg.cn/appstock/app/fqkline/get?param=sz002267,day,2008-01-01,2008-12-31,320,qfq
 *                                                    param=股票代码
 *                                                                   day=按日交易
 *                                                                        开始时间    结束时间
 *                                                                                            多少交易日
 *                                                                                                  qfq:前复权
 *                                                                                                  hfq:后复权
 *                                                                                                  bfq:不复权
 *                                                                                                  cq:除权
 *   返回结果样例：
 *  {
 *    "code": 0,
 *    "msg": "",
 *    "data": {
 *      "sz002267": {
 *        "qfqday": [
 *          [
 *            "2008-08-13",
 *            "5.94",     开盘
 *            "7.04",     收盘
 *            "7.13",     最高
 *            "5.94",     最低
 *            "708382.72" 成交额
 *          ],
 *          [
 *            "2008-08-14",
 *            "6.42",
 *            "7.04",
 *            "7.56",
 *            "6.40",
 *            "330049.80"
 *          ],
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-21
 * @version     v1.0
 */
@Xjava
public class XHttpStockHistory
{
    
    /** 数据类型：前复权 */
    public static final String $DataType_QFQ = "qfq";
    
    /** 数据类型：后复权 */
    public static final String $DataType_HFQ = "hfq";
    
    /** 数据类型：不复权 */
    public static final String $DataType_BFQ = "bfq";
    
    /** 数据类型：除权 */
    public static final String $DataType_CQ  = "cq";
    
    
    
    public String request(String i_StockCodes ,int i_Year ,String i_DataType)
    {
        XHttp               v_XHttp  = (XHttp)XJava.getObject("HTTP_StockHistory");
        Map<String ,String> v_Params = new Hashtable<String ,String>();
        
        v_Params.put("param" ,i_StockCodes + ",day," + i_Year + "-01-01," + i_Year + "-12-31,320," + i_DataType);

        Return<?> v_Response = v_XHttp.request(v_Params);
        
        return v_Response.paramStr;
    }
    
    
    
    /**
     * 解释接口返回的结果
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-21
     * @version     v1.0
     *
     * @param i_HttpResult
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<StockInfoDay> parseHttpResult(String i_StockCode ,String i_HttpResult)
    {
        List<StockInfoDay> v_Ret = new ArrayList<StockInfoDay>();
        
        if ( i_HttpResult.indexOf("\"qfqday\"") < 0
          && i_HttpResult.indexOf("\"hfqday\"") < 0
          && i_HttpResult.indexOf("\"day\"")    < 0 )
        {
            return v_Ret;
        }
        
        String v_DataType = "day";
        if ( i_HttpResult.indexOf("\"qfqday\"") >= 0 )
        {
            v_DataType = "qfqday";
        }
        else if ( i_HttpResult.indexOf("\"hfqday\"") >= 0 )
        {
            v_DataType = "hfqday";
        }
        
        try
        {
            String             v_HttpResult = "{" + i_HttpResult.substring(i_HttpResult.indexOf("\"" + v_DataType + "\"") ,i_HttpResult.indexOf("\"qt\"") - 1) + "}";
            XJSON              v_XJSON      = new XJSON();
            List<List<String>> v_Datas      = (List<List<String>>)v_XJSON.parser(v_HttpResult ,v_DataType ,ArrayList.class);
            
            for (List<String> v_Item : v_Datas)
            {
                StockInfoDay v_StockInfoDay = new StockInfoDay();
                
                v_StockInfoDay.setStockCode(i_StockCode);
                v_StockInfoDay.setStockDay(            new Date(v_Item.get(0)));
                v_StockInfoDay.setTodayOpenPrice(Double.valueOf(v_Item.get(1)));
                v_StockInfoDay.setNewPrice(      Double.valueOf(v_Item.get(2)));
                v_StockInfoDay.setMaxPrice(      Double.valueOf(v_Item.get(3)));
                v_StockInfoDay.setMinPrice(      Double.valueOf(v_Item.get(4)));
                v_StockInfoDay.setBargainSum(    Double.valueOf(v_Item.get(5)));
                
                v_Ret.add(v_StockInfoDay);
            }
        }
        catch (Exception exce)
        {
            exce.printStackTrace();   
        }
        
        return v_Ret;
    }
    
}
