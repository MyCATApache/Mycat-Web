**功能：**

主要解决生产出现的问题，比如mycat宕机，假死，慢SQL，还可以分析出现异常的日志，
出现这些情况后 发邮件提醒


**实现方法：**

使用Log4j2中的SMTP Appender，发送HTML格式的邮件LOG，并加入了RegexFilter，满足指定条件的Log才发送邮件。


**相关配置**

pom.xml引入Java Mail和disruptor（log4j2异步用）。

 		<dependency>
			<groupId>javax.mail</groupId>
			<artifactId>mail</artifactId>
			version>1.4</version>
		</dependency>

		<dependency>
			<groupId>com.lmax</groupId>
			<artifactId>disruptor</artifactId>
			<version>3.3.4</version>
		</dependency>	

log4j2.xml中配置下列信息

		<properties>
            ......
            <property name="from">xxx@163.co</property>
            <property name="smtpHost">smtp.163.com</property>
            <property name="smtpPort">25</property>
            <property name="smtpProtocol">smtp</property>
            <property name="smtpUser">me</property>
            <property name="smtpPassword">secret</property>
        </properties>
		<appenders>
            ......
			<SMTP name="Mail" 
				subject="Error Log" 
				to="xxx@163.com,xxx@qq.com"
				cc="xxx@163.com,xxx@qq.com" 	
				from="${from}" 
				smtpUsername="${smtpUser}" 	
				smtpPassword="${smtpPassword}"
			    smtpHost="${smtpHost}" 
				smtpPort="${smtpPort}" 
				bufferSize="1" 
				smtpProtocol="${smtpProtocol}" 
				smtpDebug="false">
				<!-- 消息过滤器 -->
		     	<RegexFilter regex=".* test .*" onMatch="ACCEPT" 	onMismatch="DENY"/>
			</SMTP>
		</appenders>
		<loggers>
			......
			<Root level="error">
      			<AppenderRef ref="Mail" />
    		</Root>
    	</loggers>

**测试代码** 

	package org.mycat.web;
	
	import org.slf4j.Logger;
	import org.slf4j.LoggerFactory;
	
	
	public class Test {
	
		private static final Logger LOG = LoggerFactory.getLogger(Test.class);
		
		public static void main(String[] args) {
			LOG.error(" test ");
			LOG.error("Mail");
		}
	
	}

测试结果：
第一句Log满足条件发送。第2句不发送。


**Log4j2异步功能**
在启动时加入以下参数

-DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector



**Log4j2动态配置**

方案1：

配置monitorInterval属性，最小5秒。每隔指定时间，扫描配置文件，发生变更，则生效。

		<configuration status="OFF"  monitorInterval="1800">  

画面配置参数后，手工修改配置文件并保存。Log4j会自动更新，也可以手动更新。

手工修改配置文件，可能会比较复杂。一种是以XML方式修改。另一种是把设定参数写在property中，以文本方式修改此行数据。也可使用模板技术修改。

例：

        <property name="to">xxxx@qq.com</property>


方案2：个人推荐
使用System Property作为参数值。

        <property name="to">${sys:mailto}</property>

画面修改后，设置SystemProperty,并手工更新配置。

		System.setProperty("mailto", "xxx@qq.com");
		((LoggerContext) LogManager.getContext(false)).reconfigure();


方案3：
如需有大量的配置需要设定，维护。建议使用程序生成配置。复杂性较大，不在此次对应内。