package org.mycat.web.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException; 
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.mycat.web.task.common.TaskManger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MailUtil {


	private static final Logger LOG = LoggerFactory.getLogger(MailUtil.class);
	
	
	public static Properties props;
	private static String mangerPort;
	private static String smtpProtocol;
	private static String smtpUser;
	private static String smtpPassword;
	private static String smtpHost;
	private static String to;
	private static String cc;
	private static Session mailSession;
	private static Transport transport;
	 
	
	public static Authenticator getAuthenticator(){
		return new Authenticator() {
	        @Override
	        protected PasswordAuthentication getPasswordAuthentication() {
	            // 用户名、密码
	            String userName = props.getProperty("mail.user");
	            String password = props.getProperty("mail.password");
	            return new PasswordAuthentication(userName, password);
	        }
	    };
	}
	
	@SuppressWarnings("static-access")
	public static void send(String subject, String content) throws MessagingException{
		if (true){
			if (System.getProperty("mail_to")==null)
				LOG.warn(content);
			else
				LOG.error(content);
			return;
		}
		
		
		
		JSONArray jsonArray = JSONArray.parseArray(MailConfigUtils.getInstance().getValue(Constant.MYCATY_WARN_MAIL)); 
		for(int i=0;i<jsonArray.size();i++){
			props = new Properties();
			JSONObject json = jsonArray.getJSONObject(i); 
			mangerPort = json.getString("mangerPort");
			smtpProtocol = json.getString("smtpProtocol");
			smtpUser = json.getString("smtpUser");
			smtpPassword = json.getString("smtpPassword");
			smtpHost = json.getString("smtpHost");
			to = json.getString("to");
			cc = json.getString("cc"); 
			props.put("mail.smtp.auth", "true");    
			props.put("mail.smtp.host", smtpProtocol); 
			props.put("mail.smtp.port", mangerPort); 
			
			Authenticator authenticator = new Authenticator() {
		        @Override
		        protected PasswordAuthentication getPasswordAuthentication() { 
		            return new PasswordAuthentication(smtpUser, smtpPassword);
		        }
			};
		    mailSession = Session.getInstance(props, authenticator);
		    MimeMessage message = new MimeMessage(mailSession);
		    // 设置发件人
		    InternetAddress fromMail; 
			fromMail = new InternetAddress(smtpHost); 
			message.setFrom(fromMail);
		    InternetAddress toMail = new InternetAddress(to);
		    message.setRecipient(RecipientType.TO, toMail);
		    String[] ccs = cc.split(";");
		    InternetAddress[] ccAddress = new InternetAddress[ccs.length] ;
		    for(int j=0;j<ccs.length;j++){
		    	ccAddress[j] = new InternetAddress(ccs[j]);
		    }
		    message.setRecipients(RecipientType.CC, ccAddress);
		    
		    message.setSubject(subject);

		    // 设置邮件的内容体
		    message.setContent(content, "text/html;charset=UTF-8"); 
		    String smtp[] = smtpProtocol.split("\\.");
		    transport = mailSession.getTransport(smtp[0]);
		    transport.connect(smtpProtocol, smtpUser, smtpPassword);   
		    transport.send(message); 
		}
		
	   

	}
	
   public static void main(String[] args) {
	   try {
		MailUtil.send("hello", "错误log日志信息abc");
	} catch (MessagingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }
}
