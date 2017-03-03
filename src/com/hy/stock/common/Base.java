package com.hy.stock.common;

import org.hy.common.xml.SerializableDef;





/**
 * 最高级的基础类
 *
 * @author      ZhengWei(HY)
 * @createDate  2016-08-28
 * @version     v1.0
 */
public class Base extends SerializableDef
{
    
    private static final long serialVersionUID = 6541683447351445761L;



    public String fontColorRed(Double i_Value)
    {
        return "<font color='#FF4444'>" + i_Value + "</font>";
    }
    
    
    
    public String fontColorRed(String i_Value)
    {
        return "<font color='#FF4444'>" + i_Value + "</font>";
    }
    
    
    
    public String fontColorGreen(Double i_Value)
    {
        return "<font color='#669900'>" + i_Value + "</font>";
    }
    
    
    
    public String fontColorGreen(String i_Value)
    {
        return "<font color='#669900'>" + i_Value + "</font>";
    }
    
}
