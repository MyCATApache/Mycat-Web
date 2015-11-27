package org.mycat.web.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class ZKConfigInfoUtils {
	
	private final static Logger log = LoggerFactory.getLogger(ZKConfigInfoUtils.class);
	private static String zkProperties = "/zk.properties";
	private static Map<String, String> propsMap = new HashMap <String, String>();
	private static ZKConfigInfoUtils install;
	
    static{
    		InputStream is = ZKConfigInfoUtils.class.getResourceAsStream( zkProperties);
    	    Preconditions.checkNotNull(is, "zookeeper config '%s' not found", zkProperties);
    		Properties props = new Properties();
    		try {
    			 props.load(is);
    			 Enumeration<Object>  en  = props.keys();
    			 while(en.hasMoreElements()){
    				  String key = en.nextElement().toString();  
    				  String value = props.getProperty(key);  
    				  propsMap.put(key, value);  
    			 }
    		} catch (IOException e) {
    			log.error("zookeeper config {} load fail", zkProperties);
    		}finally{
    			try {
    				is.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    }
    
	private ZKConfigInfoUtils() {};

	public static ZKConfigInfoUtils getInstall() {
		if (install == null) {
			synchronized (ZKConfigInfoUtils.class) {
				if (install == null) {
					install = new ZKConfigInfoUtils();
				}
			}
		}
		return install;
	}
	
	public String getProValue(String key,String defaultVal){
	    Preconditions.checkNotNull(propsMap, "please invoke load method to load properties first");
		if(propsMap != null){
			return propsMap.get(key);
		}else{
			return defaultVal;
		}
	}

}
