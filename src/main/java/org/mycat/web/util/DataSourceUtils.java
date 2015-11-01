package org.mycat.web.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mycat.web.task.common.TaskManger;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;


public class DataSourceUtils {

	private static final Logger logger = LogManager
			.getLogger(DataSourceUtils.class);
	
	private volatile static DataSourceUtils dataSourceUtils = null;
	private DataSourceUtils(){};
	
	public static DataSourceUtils getInstance(){
		if(dataSourceUtils == null){
			synchronized (DataSourceUtils.class) {
				if(dataSourceUtils == null){
					dataSourceUtils = new DataSourceUtils();
				}
			}
		}
		return dataSourceUtils;
	}
	
	private static final String NAME_SUFFIX = "dataSource";

	public  boolean register(Map<String, Object> jdbc, String dbName) throws Exception {
		Connection conn = null;
		try {
			String beanName = dbName + NAME_SUFFIX;
			remove(dbName);
			ConfigurableApplicationContext applicationContext = 
					(ConfigurableApplicationContext) SpringApplicationContext.getApplicationContext();
			DefaultListableBeanFactory beanFactory = 
					(DefaultListableBeanFactory) applicationContext.getBeanFactory();
				
			beanFactory.registerBeanDefinition(beanName, getDefinition(jdbc));
			
			BasicDataSource dbSource = (BasicDataSource)SpringApplicationContext.getBean(beanName);
			
			conn = dbSource.getConnection();
			
			beanFactory.registerBeanDefinition(dbName + "sqlSessionFactory", getSqlSessionFactoryDef(dbSource));
			Object sqlSessionFactory = SpringApplicationContext.getBean(dbName + "sqlSessionFactory");
			beanFactory.registerBeanDefinition(dbName + "sqlSessionTemplate", getSqlSessionTemplateDef(sqlSessionFactory));
			TaskManger.getInstance().addDBName(dbName);
			return true;
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e.getCause());
			remove(dbName);
			return false;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	
	public boolean register(String dbName) throws Exception {
		if(!SpringApplicationContext.getApplicationContext().containsBean(dbName + "NAME_SUFFIX")){
			RainbowContext context = new RainbowContext("mycatService", "query");
			context.addAttr("mycatName", dbName);
			context = SoaManager.getInstance().invoke(context);
			if (context.getRows() == null || context.getRows().size() == 0) {
				return false;
			}
			Map<String, Object> row = context.getRow(0);
			return register(row, dbName);
		}
		return true;
	}

	public  void remove(String dbName) {
		SpringApplicationContext.removeBeans(dbName + NAME_SUFFIX, dbName + "sqlSessionFactory", dbName + "sqlSessionTemplate", dbName
				+ "transactionManager");
	}

	private  GenericBeanDefinition getDefinition(Map<String, Object> jdbc) {
		GenericBeanDefinition messageSourceDefinition = new GenericBeanDefinition();
		Map<String, Object> original = new HashMap<String, Object>();
		original.put("driverClassName", "com.mysql.jdbc.Driver");
		original.put("url", DialectUtils.getMySQLURL((String)jdbc.get("ip"), (String)jdbc.get("port"), (String)jdbc.get("dbName")));
		original.put("username", jdbc.get("username"));
		original.put("password", jdbc.get("password"));
		
		original.put("maxActive", 20);
		original.put("initialSize", 5);
		original.put("maxWait", 60000);
		original.put("minIdle", 5);

		messageSourceDefinition.setBeanClass(BasicDataSource.class);
		messageSourceDefinition.setDestroyMethodName("close");
		messageSourceDefinition.setPropertyValues(new MutablePropertyValues(original));
		return messageSourceDefinition;
	}

	private  GenericBeanDefinition getSqlSessionFactoryDef(Object dbSource) {
		GenericBeanDefinition sessionFactoryDef = new GenericBeanDefinition();
		Map<String, Object> paramData = new HashMap<String, Object>();
		paramData.put("dataSource", dbSource);
		List<String> list = new ArrayList<String>();
		list.add("classpath:mybatis/**/*Mapper.xml");
		paramData.put("mapperLocations", list);
		paramData.put("typeAliasesPackage", "org.hx.rainbow.common.dao.handler");
		sessionFactoryDef.setBeanClass(SqlSessionFactoryBean.class);
		sessionFactoryDef.setPropertyValues(new MutablePropertyValues(paramData));
		return sessionFactoryDef;
	}

	private  GenericBeanDefinition getSqlSessionTemplateDef(Object sqlSessionFacotry) {
		GenericBeanDefinition sqlSessionTemplateDef = new GenericBeanDefinition();
		ConstructorArgumentValues values = new ConstructorArgumentValues();
		values.addIndexedArgumentValue(0, sqlSessionFacotry);
		sqlSessionTemplateDef.setConstructorArgumentValues(values);
		sqlSessionTemplateDef.setBeanClass(SqlSessionTemplate.class);
		return sqlSessionTemplateDef;
	}
	
}
