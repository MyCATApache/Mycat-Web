package org.mycat.web.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.mycat.web.listen.PublishServiceStartupListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MailConfigUtils {

    private final Logger log = LoggerFactory.getLogger(MailConfigUtils.class);
	private static Properties prop;
	private static InputStream inStream;
	private static final String MYCAT_PROP = "mycat.properties";
	
	
	private volatile static MailConfigUtils mailConfigUtils = null;
 

	public static MailConfigUtils getInstance() {
		if (mailConfigUtils == null) {
			synchronized (MailConfigUtils.class) {
				if (mailConfigUtils == null) {
 					mailConfigUtils = new MailConfigUtils();
					String mailInfo = mailConfigUtils.getValue(Constant.MYCATY_WARN_MAIL);
					if(mailInfo != null && !"".equals(mailInfo)){
						JSONArray mailArray = JSONArray.parseArray(mailInfo); 
						if (mailArray.size()>0)
							mailConfigUtils.setMailInfo( (JSONObject)mailArray.get(0));
						
					}
				}
			}
		}
		return mailConfigUtils;
	}
	
	static {
		
	}
	
	public void setMailInfo(JSONObject json){
		System.out.println("Loading Mail Setting....");
		System.setProperty("mail_to", (String)json.get("to"));
		System.setProperty("mail_cc", (String)json.get("cc"));
		System.setProperty("mail_from", (String)json.get("smtpUser"));
		System.setProperty("mail_user", (String)json.get("smtpUser"));
		System.setProperty("mail_password", (String)json.get("smtpPassword"));
		System.setProperty("mail_server", (String)json.get("smtpHost"));
		System.setProperty("mail_protocal", (String)json.get("smtpProtocol"));
		System.setProperty("mail_port", (String)json.get("mangerPort"));
		((LoggerContext) LogManager.getContext(false)).reconfigure();
	}

	public String getValue(String key){
		try {
		prop = Properties.class.newInstance();   
		inStream = new FileInputStream(MailConfigUtils.class.getClassLoader().getResource(MYCAT_PROP).getPath()); 
		prop.load(inStream); 
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		try {
			inStream.close();
		} catch (IOException e) { 
			e.printStackTrace();
		}
	}
		if(prop.containsKey(key)){
			return prop.getProperty(key);
		}else{
			return null;
		}
	}
	
	public void setValue(String key,String value){
		try {
		   OutputStream fos = new FileOutputStream(MailConfigUtils.class.getClassLoader().getResource(MYCAT_PROP).getPath());
		   prop.setProperty(key, value); 
		   prop.store(fos, "Update '" + key + "' value"); 
		   fos.flush();
		   fos.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	} 

	public static void main(String[] args) { 
		System.out.println(MailConfigUtils.getInstance().getValue(Constant.MYCATY_WARN_MAIL)); 
	}
}
