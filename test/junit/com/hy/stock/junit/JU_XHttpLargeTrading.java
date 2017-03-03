package junit.com.hy.stock.junit;

import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.common.BaseJunit;
import com.hy.stock.http.XHttpLargeTrading;





/**
 * 测试单元：股票大单接口
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-21
 * @version     v1.0
 */
public class JU_XHttpLargeTrading extends BaseJunit
{
    
    @Test
    public void request()
    {
        XHttpLargeTrading v_XHttp = (XHttpLargeTrading)XJava.getObject("XHttpLargeTrading");
        
        System.out.println(v_XHttp.request("sz000002"));
    }
    
}
