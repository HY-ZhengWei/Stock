package junit.com.hy.stock.junit;

import java.util.List;

import org.hy.common.Date;
import org.hy.common.Help;
import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.bean.StockInfoDay;
import com.hy.stock.common.BaseJunit;
import com.hy.stock.dao.StockInfoDayDAO;





/**
 * 测试单元：股票每天最终交易数据操作DAO 
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-25
 * @version     v1.0
 */
public class JU_MakeStockInfoDays extends BaseJunit
{
    
    /**
     * 生成 "当天" 的股票每天最终交易数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-25
     * @version     v1.0
     *
     */
    @Test
    public void makeStockInfoDays()
    {
        StockInfoDayDAO v_StockInfoDayDAO = (StockInfoDayDAO)XJava.getObject("StockInfoDayDAO");
        
        v_StockInfoDayDAO.makeStockInfoDays(Date.getNowTime().getDate(-10) ,Date.getNowTime().getDate(1));
    }
    
    
    
    public void queryStockInfoDays() throws InterruptedException
    {
        Thread.sleep(10 * 1000);
        
        StockInfoDayDAO v_StockInfoDayDAO = (StockInfoDayDAO)XJava.getObject("StockInfoDayDAO");
        
        for (int v_Index=0; v_Index<1000000000; v_Index++)
        {
            System.out.println(v_Index);
            List<StockInfoDay> v_Item = v_StockInfoDayDAO.queryStockInfoDays().get("sz000002bfq");
            Help.toSort(v_Item ,"stockDay asc");
            Help.toSort(v_Item ,"stockDay desc");
        }
        
        Thread.sleep(10 * 60 * 1000);
    }
    
}
