package org.mycat.web;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class TestMailLog {

    private static final Logger LOG = LoggerFactory.getLogger(TestMailLog.class);
    
    
	public static void main(String[] args) throws InterruptedException {

		
		
		
		//修改配置
		System.setProperty("mail.subject", "邮件主题");
		//通知Log4j2更新配置
		((LoggerContext) LogManager.getContext(false)).reconfigure();

		 
        LOG.error(" test ");
        System.out.println("test-------------");
        LOG.error("Mail");
	}

}
