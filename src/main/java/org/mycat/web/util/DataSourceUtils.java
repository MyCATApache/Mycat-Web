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
import org.mycat.web.task.server.SyncClearData;
import org.mycat.web.task.server.SyncSysSql;
import org.mycat.web.task.server.SyncSysSqlhigh;
import org.mycat.web.task.server.SyncSysSqlslow;
import org.mycat.web.task.server.SyncSysSqlsum;
import org.mycat.web.task.server.SyncSysSqtable;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;


public class DataSourceUtils {

	private static final Logger logger = LogManager
			.getLogger(DataSourceUtils.class);
	private static final String DEFULAT_MYCAT_MANGER = "9066";
	
	public static final String DEFAULT_MYSQL_DRIVER_CLASS = "com.mysql.jdbc.Driver";

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
	
	
	public  boolean register(Map<String, Object> jdbc, String dbName, String mycatType) throws Exception {
		Connection conn = null;
		try {
			dbName = dbName + mycatType;
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
			updateTask(dbName);
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
	
	private void updateTask(String dbName){
		TaskManger taskManger = TaskManger.getInstance();
		taskManger.addDBName(dbName);
		taskManger.cancelTask("SyncSysSql", "SyncSysSqlhigh", "SyncSysSqlslow", "SyncSysSqtable", "SyncSysSqlsum");
		taskManger.addTask(new SyncSysSql(), 60 * 1000, "SyncSysSql");//1分钟
		taskManger.addTask(new SyncSysSqlhigh(), 60 * 1000*2, "SyncSysSqlhigh");//2分钟
		taskManger.addTask(new SyncSysSqlslow(), 60 * 1000*2, "SyncSysSqlslow");//2分钟
		taskManger.addTask(new SyncSysSqtable(), 60 * 1000*3, "SyncSysSqtable");//3分钟
		taskManger.addTask(new SyncSysSqlsum(), 60 * 1000*3, "SyncSysSqlsum");//3分钟
		taskManger.addTask(new SyncClearData(),60 *1000*60*10, "SyncClearData");//10小时
	}
	
	public boolean register(String dbName, String mycatType) throws Exception {
		if(!SpringApplicationContext.getApplicationContext().containsBean(dbName + mycatType + NAME_SUFFIX)){
			RainbowContext context = new RainbowContext("mycatService", "query");
			context.addAttr("mycatName", dbName);
			context = SoaManager.getInstance().invokeNoTx(context);
			if (context.getRows() == null || context.getRows().size() == 0) {
				return false;
			}
			Map<String, Object> row = context.getRow(0);
			row.put("port", mycatType);
			return register(row, dbName, mycatType);
		}
		return true;
	}

	public  boolean register(Map<String, Object> jdbc, String dbName) throws Exception {
		return register(jdbc, dbName, DEFULAT_MYCAT_MANGER);
	}
	
	public boolean register(String dbName) throws Exception {
		return register(dbName, DEFULAT_MYCAT_MANGER);
	}
	public String getDbName(String dbName)  {
		int n_pos = dbName.indexOf(DEFULAT_MYCAT_MANGER);
		if (n_pos>0) {
		   return dbName.substring(0,n_pos);
		}
		else {
		   return dbName;
		}
	}
	public  void remove(String dbName) {
		SpringApplicationContext.removeBeans(dbName + NAME_SUFFIX, dbName + "sqlSessionFactory", dbName + "sqlSessionTemplate", dbName
				+ "transactionManager");
	}

	private  GenericBeanDefinition getDefinition(Map<String, Object> jdbc) {
		GenericBeanDefinition messageSourceDefinition = new GenericBeanDefinition();
		Map<String, Object> original = new HashMap<String, Object>();
		original.put("driverClassName", DEFAULT_MYSQL_DRIVER_CLASS);
		original.put("url", getMySQLURL((String)jdbc.get("ip"), (String)jdbc.get("port"), (String)jdbc.get("dbName")));
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
	
	private String getMySQLURL(String ip, String port, String server) {
		return "jdbc:mysql://" + ip + ":" + port + "/" + server + "?characterEncoding=utf8";
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
