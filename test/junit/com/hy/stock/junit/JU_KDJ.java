package junit.com.hy.stock.junit;

import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.common.BaseJunit;
import com.hy.stock.dao.StockInfoDayDAO;





/**
 * 测试单元：KDJ指标 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-22
 * @version     v1.0
 */
public class JU_KDJ extends BaseJunit
{
    
    /**
     * 所有股票都有上市首个交易日开始，计算KDJ指标
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-31
     * @version     v1.0
     *
     */
    @Test
    public void calcKDJ_FirstDayToToday()
    {
        StockInfoDayDAO v_StockInfoDayDAO = (StockInfoDayDAO)XJava.getObject("StockInfoDayDAO");
        
        // v_StockInfoDayDAO.calcKDJ_DayToTody(Date.getNowTime().getDate(-180));
        v_StockInfoDayDAO.calcKDJ_FirstDayToToday();
    }
    
}
