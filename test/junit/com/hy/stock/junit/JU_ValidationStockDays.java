package junit.com.hy.stock.junit;

import org.hy.common.Date;
import org.hy.common.thread.ThreadPool;
import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.common.BaseJunit;
import com.hy.stock.dao.StockInfoDayDAO;
import com.hy.stock.service.StockDayService;





/**
 * 测试单元：股票每天最终交易数据的服务层
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-07-22
 * @version     v1.0
 */
public class JU_ValidationStockDays extends BaseJunit
{
    
    /**
     * 通过每日交易的概要接口（this.makeStockHistorys()），效验本程序自己日结的每日交易数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-07-24
     * @version     v1.0
     */
    @Test
    public void validationStockDays()
    {
        ThreadPool.setMaxThread(10);
        ThreadPool.setMinThread(1);
        ThreadPool.setMinIdleThread(1);
        ThreadPool.setIntervalTime(100);
        ThreadPool.setIdleTimeKill(60);
        ThreadPool.setWatch(true);
        
        
        StockDayService v_StockDayService = (StockDayService)XJava.getObject("StockDayService");
        StockInfoDayDAO v_StockInfoDayDAO = (StockInfoDayDAO)XJava.getObject("StockInfoDayDAO");
        
        v_StockDayService.validationStockDays();
        
        Date v_BeginTime = new Date();
        v_StockInfoDayDAO.calcKDJ_DayToTody (Date.getNowTime().getDateByWork(-90).getFirstTimeOfDay());
        System.out.println("共用时长为：" + Date.toTimeLen(Date.getNowTime().getTime() - v_BeginTime.getTime()) + "\n");
        
        
        v_BeginTime = new Date();
        v_StockInfoDayDAO.calcMACD_DayToTody(Date.getNowTime().getDateByWork(-90).getFirstTimeOfDay());
        System.out.println("共用时长为：" + Date.toTimeLen(Date.getNowTime().getTime() - v_BeginTime.getTime()) + "\n");
        
        
        v_BeginTime = new Date();
        v_StockInfoDayDAO.calcWR_DayToTody(  Date.getNowTime().getDateByWork(-90).getFirstTimeOfDay());
        System.out.println("共用时长为：" + Date.toTimeLen(Date.getNowTime().getTime() - v_BeginTime.getTime()) + "\n");
        
        
        v_BeginTime = new Date();
        v_StockInfoDayDAO.calcMA_DayToTody(  Date.getNowTime().getDateByWork(-90).getFirstTimeOfDay());
        System.out.println("共用时长为：" + Date.toTimeLen(Date.getNowTime().getTime() - v_BeginTime.getTime()) + "\n");
    }
    
}
