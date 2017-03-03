package com.hy.stock.common;

import com.hy.stock.config.InitConfig;





/**
 * 基础测试类
 * 
 * @author      ZhengWei(HY)
 * @createDate  2016-07-20
 * @version     v1.0
 */
public class BaseJunit
{
    private static boolean $IsInit = false;
    
    
    
    public BaseJunit()
    {
        synchronized ( this )
        {
            if ( !$IsInit )
            {
                $IsInit = true;
                new InitConfig(false);
            }
        }
    }
    
}
