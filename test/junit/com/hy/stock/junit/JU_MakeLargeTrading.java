package junit.com.hy.stock.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.hy.common.Help;
import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.bean.LargeTrading;
import com.hy.stock.bean.StockConfig;
import com.hy.stock.common.BaseJunit;
import com.hy.stock.dao.StockConfigDAO;
import com.hy.stock.http.XHttpLargeTrading;
import com.hy.stock.service.LargeTradingService;





/**
 * 补充当天大单交易数据
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-16
 * @version     v1.0
 */
public class JU_MakeLargeTrading extends BaseJunit
{
    
    /**
     * 补充当天大单交易数据
     * 
     * @author      ZhengWei(HY)
     * @createDate  2016-08-16
     * @version     v1.0
     */
    @Test
    public void makeLargeTrading()
    {
        StockConfigDAO      v_StockConfigDAO      = (StockConfigDAO)     XJava.getObject("StockConfigDAO");
        XHttpLargeTrading   v_XHttpLargeTrading   = (XHttpLargeTrading)  XJava.getObject("XHttpLargeTrading");
        LargeTradingService v_LargeTradingService = (LargeTradingService)XJava.getObject("LargeTradingService");
        
        System.out.print("-- 开始补充当天大单交易数据...\n");
        List<LargeTrading> v_LargeTradings    = null;
        List<LargeTrading> v_LargeTradingsSum = new ArrayList<LargeTrading>();
        
        for (Entry<String ,StockConfig> v_StockConfig : v_StockConfigDAO.queryStockConfigMaps().entrySet())
        {
            System.out.print("-- 补充[" + v_StockConfig.getValue().getStockCode() + v_StockConfig.getValue().getStockName() + "]当天大单交易数据...\n");
            
            try
            {
                v_LargeTradings = v_XHttpLargeTrading.parseHttpResult(v_StockConfig.getValue() ,v_XHttpLargeTrading.request(v_StockConfig.getValue().getStockCode()));
                if ( !Help.isNull(v_LargeTradings) )
                {
                    v_LargeTradings = v_LargeTradingService.addLargeTradings(v_StockConfig.getValue().getStockCode() ,v_LargeTradings);
                }
            }
            catch (Exception exce)
            {
                exce.printStackTrace();
            }
            
            if ( !Help.isNull(v_LargeTradings) )
            {
                v_LargeTradingsSum.addAll(v_LargeTradings);
            }
        }
    }
    
}
