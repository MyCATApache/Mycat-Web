package org.mycat.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.core.service.SoaManager;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;

public class DataSourceUtils {

	// bean id 由{变量}+NAME_SIFFIX组成
	public static final String NAME_SUFFIX = "dataSource";

	public static String register(Map<String, Object> jdbc, String dbName) throws Exception {

		String ip = (String) jdbc.get("ip");
		String port = (String) jdbc.get("port");
		String url = DialectUtils.getMySQLURL(ip, port, (String) jdbc.get("dbName"));
		jdbc.put("url", url);

		String beanName = dbName + NAME_SUFFIX;
		ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) SpringApplicationContext.getApplicationContext();
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
		boolean flag = beanFactory.isBeanNameInUse(beanName);
		if (!flag) {
			try {
				beanFactory.registerBeanDefinition(beanName, getDefinition(jdbc));
				BasicDataSource dbSource = (BasicDataSource) SpringApplicationContext.getBean(beanName);

				try {
					dbSource.getConnection();
				} catch (Exception e) {
					throw new Exception("连接数据库失败,请检查参数");
				}

				if (dbSource != null) {
					beanFactory.registerBeanDefinition(dbName + "sqlSessionFactory", getSqlSessionFactoryDef(dbSource));
					Object sqlSessionFactory = SpringApplicationContext.getBean(dbName + "sqlSessionFactory");
					beanFactory.registerBeanDefinition(dbName + "sqlSessionTemplate", getSqlSessionTemplateDef(sqlSessionFactory));
				}
			} catch (Exception e1) {
				remove(dbName);
				throw e1;
			}
		}

		return dbName;

	}

	public static String register(String dbName) throws Exception {
		RainbowContext context = new RainbowContext("mycatService", "query");
		context.addAttr("mycatName", dbName);
		context = SoaManager.getInstance().invoke(context);
		if (context.getRows() == null || context.getRows().size() == 0) { throw new Exception("数据源不存在"); }
		Map<String, Object> row = context.getRow(0);

		Map<String, Object> params = new HashMap<String, Object>();
		params.putAll(row);
		params.put("driverClassName", row.get("driverClass"));
		register(params, dbName);
		return dbName;
	}

	public static void remove(String mycatName) {
		SpringApplicationContext.removeBeans(mycatName + NAME_SUFFIX, mycatName + "sqlSessionFactory", mycatName + "sqlSessionTemplate", mycatName
				+ "transactionManager");
	}

	private static GenericBeanDefinition getDefinition(Map<String, Object> jdbc) {
		GenericBeanDefinition messageSourceDefinition = new GenericBeanDefinition();
		Map<String, Object> original = new HashMap<String, Object>();
		String driverClassName = (String) jdbc.get("driverClassName");
		original.put("driverClassName", StringUtils.isBlank(driverClassName) ? DialectUtils.DEFAULT_MYSQL_DRIVER_CLASS : driverClassName);
		jdbc.put("driverClass", driverClassName);
		original.put("url", jdbc.get("url"));
		original.put("username", jdbc.get("username"));
		original.put("password", jdbc.get("password"));
		messageSourceDefinition.setBeanClass(BasicDataSource.class);
		messageSourceDefinition.setDestroyMethodName("close");
		messageSourceDefinition.setPropertyValues(new MutablePropertyValues(original));
		return messageSourceDefinition;
	}

	private static GenericBeanDefinition getSqlSessionFactoryDef(Object dbSource) {
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

	private static GenericBeanDefinition getSqlSessionTemplateDef(Object sqlSessionFacotry) {
		GenericBeanDefinition sqlSessionTemplateDef = new GenericBeanDefinition();
		ConstructorArgumentValues values = new ConstructorArgumentValues();
		values.addIndexedArgumentValue(0, sqlSessionFacotry);
		sqlSessionTemplateDef.setConstructorArgumentValues(values);
		sqlSessionTemplateDef.setBeanClass(SqlSessionTemplate.class);
		return sqlSessionTemplateDef;
	}
}
