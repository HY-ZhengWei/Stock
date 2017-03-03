package junit.com.hy.stock.junit;

import org.hy.common.CycleList;
import org.hy.common.Date;
import org.hy.common.mail.MailOwnerInfo;
import org.hy.common.mail.MailSendInfo;
import org.hy.common.mail.SimpleMail;
import org.hy.common.xml.XJava;
import org.junit.Test;

import com.hy.stock.common.BaseJunit;





public class JU_Mail extends BaseJunit
{
 
    @Test
    @SuppressWarnings("unchecked")
    public void testSendMail()
    {
        MailSendInfo v_SendInfo = new MailSendInfo();
        
        v_SendInfo.setEmail("HY.ZhengWei@qq.com");
        v_SendInfo.setSubject(Date.getNowTime().getFull());
        v_SendInfo.setContent("T");
        
        CycleList<MailOwnerInfo> v_MailOwners = (CycleList<MailOwnerInfo>)XJava.getObject("CycleMails");
        
        for (MailOwnerInfo v_MailOwner : v_MailOwners)
        {
            System.out.println(v_MailOwner.getEmail());
            SimpleMail.sendHtmlMail(v_MailOwner ,v_SendInfo);
        }
    }
    
}
