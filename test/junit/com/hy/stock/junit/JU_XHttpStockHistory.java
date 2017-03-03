package junit.com.hy.stock.junit;

import java.util.List;

import org.hy.common.Help;
import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.bean.StockInfoDay;
import com.hy.stock.common.BaseJunit;
import com.hy.stock.http.XHttpStockHistory;





/**
 * 测试单元：股票历史交易的概要数据接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-21
 * @version     v1.0
 */
public class JU_XHttpStockHistory extends BaseJunit
{
    
    @Test
    public void request()
    {
        XHttpStockHistory v_XHttp     = (XHttpStockHistory)XJava.getObject("XHttpStockHistory");
        String            v_StockCode = "sz000002";
        
        List<StockInfoDay> v_Stocks = v_XHttp.parseHttpResult(v_StockCode ,v_XHttp.request(v_StockCode ,2015 ,XHttpStockHistory.$DataType_CQ));
        
        Help.print(v_Stocks);
    }
    
}
