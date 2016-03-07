package org.mycat.web.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class MailConfigUtils {

	private static Properties prop;
	private static InputStream inStream;
	private static final String MYCAT_PROP = "mycat.properties";
	
	
	private volatile static MailConfigUtils mailConfigUtils = null;
 

	public static MailConfigUtils getInstance() {
		if (mailConfigUtils == null) {
			synchronized (MailConfigUtils.class) {
				if (mailConfigUtils == null) {
					mailConfigUtils = new MailConfigUtils();
				}
			}
		}
		return mailConfigUtils;
	}
	
	static {
		
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
