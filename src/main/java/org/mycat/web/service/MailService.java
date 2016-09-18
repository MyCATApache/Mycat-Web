package org.mycat.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.exception.AppException;
import org.mycat.web.util.Constant;
import org.mycat.web.util.JsonUtils;
import org.mycat.web.util.MailConfigUtils;
import org.mycat.web.util.MailUtil;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service("mailService")
public class MailService extends BaseService{
	
	public RainbowContext addMail(RainbowContext context) { 
		try{  

			JSONArray mailArray = mailJSONArray();
			if (mailArray.size()>0){

				context.setSuccess(false);
				throw new AppException("新增失败,系统只支持一个邮件设置，请删除后，再新建。");
			}
			
			
			JSONObject mailJson = mailJSON(context,mailArray); 
			mailArray.add(mailJson);
			MailConfigUtils.getInstance().setValue(Constant.MYCATY_WARN_MAIL, mailArray.toJSONString());
			MailConfigUtils.getInstance().setMailInfo(mailJson);
			MailUtil.send("hello", "这是一封测试邮件。");
			context.setMsg("新增成功!已发送测试邮件，请查看是否收到。");
			context.setSuccess(true); 
		}catch (Exception e) {
			logger.error(e.getCause());
			context.setSuccess(false);
			throw new AppException("新增失败,系统异常!case:" + e.getMessage(), e.getCause());
		}
		return context;	
	}
	
	public RainbowContext delete(RainbowContext context) {	 
		try{ 
			String guid=(String)context.getAttr("guid");
			JSONArray mailArray = mailJSONArray();
			for(int i=0;i<mailArray.size();i++){
				JSONObject mail = mailArray.getJSONObject(i);
				if(mail.getString("index").equals(guid)){
					mailArray.remove(i);
				}
			}
			MailConfigUtils.getInstance().setValue(Constant.MYCATY_WARN_MAIL, mailArray.toJSONString());
			context.setMsg("删除成功!");
			context.setSuccess(true); 
			System.clearProperty("mail_to");
		}catch (Exception e) {
			logger.error(e.getCause());
			context.setSuccess(false);
			throw new AppException("删除失败,系统异常!case:" + e.getMessage(), e.getCause());
		}				
				
		context.getAttr().clear();
		return context;
	}
	
	public RainbowContext queryAll(RainbowContext context) throws Exception { 
		List<Map<String, Object>> mailList = new ArrayList<Map<String, Object>>();
		JSONArray mailArray = mailJSONArray();
		for (int i = 0; i < mailArray.size(); i++) {
			Map<String, Object> mail = JsonUtils.json2Map(mailArray.getString(i)); 
			mailList.add(mail); 
		}
		context.addRows(mailList);
		context.setTotal(context.getRows().size()); 
		return context;
	}
	
	public JSONArray mailJSONArray(){
		JSONArray mailArray = null;
		String mailInfo = MailConfigUtils.getInstance().getValue(Constant.MYCATY_WARN_MAIL);
		if(mailInfo != null && !"".equals(mailInfo)){
			mailArray = JSONArray.parseArray(mailInfo); 
		}else{
			mailArray = new JSONArray();
		}
		return mailArray;
	}
	
	public JSONObject mailJSON(RainbowContext context, JSONArray mailArray){  
		int index = 0;
		if(mailArray == null) index = 1;
		else index = mailArray.size() + 1;
		JSONObject json = new JSONObject();
		json.put("index", index);
		json.put("smtpUser", context.getAttr("smtpUser"));
		json.put("smtpPassword", context.getAttr("smtpPassword"));
		json.put("smtpHost", context.getAttr("smtpHost"));
		json.put("smtpProtocol", context.getAttr("smtpProtocol"));
		json.put("mangerPort", context.getAttr("mangerPort"));
		json.put("to", context.getAttr("to"));
		json.put("cc", context.getAttr("cc")); 
		return json;
	}
}
