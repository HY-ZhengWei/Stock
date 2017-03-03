package junit.com.hy.stock.junit;

import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.common.BaseJunit;
import com.hy.stock.dao.StockInfoDayDAO;





/**
 * 测试单元：WR指标 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-23
 * @version     v1.0
 */
public class JU_WR extends BaseJunit
{
    
    /**
     * 所有股票都有上市首个交易日开始，计算WR指标
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-23
     * @version     v1.0
     *
     */
    @Test
    public void calcWR_FirstDayToToday()
    {
        StockInfoDayDAO v_StockInfoDayDAO = (StockInfoDayDAO)XJava.getObject("StockInfoDayDAO");
        
        v_StockInfoDayDAO.calcWR_FirstDayToToday();
    }
    
}
