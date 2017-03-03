package junit.com.hy.stock.junit;

import org.junit.Test;

import com.hy.stock.calculate.RSI;
import com.hy.stock.common.BaseJunit;





public class JU_RSI extends BaseJunit
{
    
    @Test
    public void test_RSI()
    {
        System.out.println(RSI.calcRSI6 ("sz000002"));
        System.out.println(RSI.calcRSI12("sz000002"));
        System.out.println(RSI.calcRSI24("sz000002"));
    }
    
}
