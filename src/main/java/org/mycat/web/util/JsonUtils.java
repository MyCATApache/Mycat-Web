package org.mycat.web.util;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

/**
 * JSON工具类
 * @author mycat
 * @version 1.0
 */
public abstract class JsonUtils {
	private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	public static final Map json2Map(String jsonStr) {
		if(StringUtils.isEmpty(jsonStr)) return null;
		
		try {
			return objectMapper.readValue(jsonStr, Map.class);
		} catch (Exception e) {
			logger.error("Json转换异常", e);
			return null;
		} 
	}
	
	public static String object2JSON(Object obj) {
		if(obj == null){
			return "{}";
		}
		return JSON.toJSONString(obj,SerializerFeature.WriteDateUseDateFormat);
	}
	
	public static String map2Json(Map map) {
		return object2JSON(map);
	}
	

}

