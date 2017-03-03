package junit.com.hy.stock.junit;

import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.common.BaseJunit;
import com.hy.stock.service.StockDayService;





/**
 * 测试单元：股票每天最终交易数据的服务层
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-22
 * @version     v1.0
 */
public class JU_MakeStockHistorys extends BaseJunit
{
    
    /**
     * 补充历史每日交易的概要数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-22
     * @version     v1.0
     *
     */
    @Test
    public void makeStockHistorys()
    {
        StockDayService v_StockDayService = (StockDayService)XJava.getObject("StockDayService");
        
        v_StockDayService.makeStockHistorys();
    }
    
}
