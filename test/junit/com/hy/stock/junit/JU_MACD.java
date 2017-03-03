package junit.com.hy.stock.junit;

import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.common.BaseJunit;
import com.hy.stock.dao.StockInfoDayDAO;





/**
 * 测试单元：计算MACD指标
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-21
 * @version     v1.0
 */
public class JU_MACD extends BaseJunit
{
    
    /**
     * 所有股票都有上市首个交易日开始，计算MACD指标
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     */
    @Test
    public void calcMACD_FirstDayToToday()
    {
        StockInfoDayDAO v_StockInfoDayDAO = (StockInfoDayDAO)XJava.getObject("StockInfoDayDAO");
        
        v_StockInfoDayDAO.calcMACD_FirstDayToToday();
    }
    
}
