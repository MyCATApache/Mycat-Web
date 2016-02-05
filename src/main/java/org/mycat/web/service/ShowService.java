package org.mycat.web.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.mycat.web.task.server.ShowMycatProcessor;
import org.mycat.web.util.DataSourceUtils;
import org.mycat.web.util.DataSourceUtils.MycatPortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Lazy
@Service
public class ShowService extends BaseService { 
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowService.class);
	private static final String SYSPARAM_NAMESPACE = "SYSSHOW";  
	private static final String SYSSQL_NAMESPACE = "SYSSQL";  
	private static final String SYSSQLHIGH_NAMESPACE = "SYSSQLHIGH";
	private static final String SYSSQLSLOW_NAMESPACE = "SYSSQLSLOW";   
	private static final String SYSSQLTABLE_NAMESPACE = "SYSSQLTABLE";   
	private static final String SYSSQLSUM_NAMESPACE = "SYSSQLSUM";   

	public RainbowContext base(RainbowContext context,String cmd) {
		String datasource = (String)context.getAttr("ds");
		if(datasource ==  null || datasource.isEmpty()){
			return context;
		}
		try {
			if(!DataSourceUtils.getInstance().register(datasource)){
				context.setSuccess(false);
				context.setMsg("数据源["+datasource+"]连接失败!");
				return context;
			}
		} catch (Exception e) {
			
		}
		LOGGER.info("数据源["+datasource+"]");
		context.setDs(datasource + MycatPortType.MYCAT_MANGER);
		if (cmd.equals("sqlslow")){
			String threshold = (String)context.getAttr("threshold");
			if (!(threshold ==  null || threshold.isEmpty())){
				super.query(context, SYSPARAM_NAMESPACE, "setsqlslow");
			}			
		} 
		
		super.query(context, SYSPARAM_NAMESPACE, cmd); 
		return context;
	}
	
	
	public RainbowContext baseQuery(RainbowContext context,String namespace ,String cmd) {  		
				String datasource = (String)context.getAttr("ds");		
		 		if(!(datasource ==  null || datasource.isEmpty())){		
		 			context.addAttr("DB_NAME", datasource);		
		 		}
		super.queryByPage(context, namespace, cmd, cmd+"Count"); 
		return context;
	}
	
	public RainbowContext baseQueryAll(RainbowContext context,String namespace ,String cmd) {  		
		String datasource = (String)context.getAttr("ds");		
 		if(!(datasource ==  null || datasource.isEmpty())){		
 			context.addAttr("DB_NAME", datasource);		
 		}
     super.query(context, namespace, cmd); 
     return context;
   }	
	
	public RainbowContext sysparam(RainbowContext context) {
		return base(context,"sysparam");
	}
	
	public RainbowContext sql(RainbowContext context) {
		return baseQuery(context, SYSSQL_NAMESPACE, "sql");
	}
	

	public RainbowContext sqlInfo(RainbowContext context) {
		return baseQuery(context, SYSSQL_NAMESPACE, "sqlInfo");
	}
	
	public RainbowContext sqlonline(RainbowContext context,String command) {
		String tmpdir=System.getProperty("java.io.tmpdir");
		String clientip=(String)context.getAttr("ip");
		String fileindex="";
		String filename;
		String remotefilename;
		Session session = null;
		Channel channel = null;
		JSch jsch = new JSch();
		
		if(clientip.indexOf(".")>0)
		{
			
			fileindex=clientip.substring(clientip.lastIndexOf(".")+1);
			
			
		}
		else if (clientip.indexOf(":")>0)
		{
			fileindex=clientip.substring(clientip.lastIndexOf(":")+1);
		}
		filename=tmpdir+"/sqlcheck"+fileindex+".sql";
		remotefilename="/tmp/sqlcheck"+fileindex+".sql";
		command="sqlwatch <"+remotefilename+" 2>&1|grep "+command+"|grep -v Aborting$|uniq";
		try
		{
			  File file =new File(filename);
		      //if file doesnt exists, then create it
		      if(file.exists()){
		    	  file.delete();  
		       file.createNewFile();
		      }
		      else
		      {
		    	  file.createNewFile();
		      }
		      FileWriter fileWritter = new FileWriter(filename);
		  
		      fileWritter.write((String)context.getAttr("sql"));
		      fileWritter.close();
        
		      
		      session = jsch.getSession(ShowMycatProcessor.ShowMycatSqlonlineUser(),ShowMycatProcessor.ShowMycatSqlonlineServer(),22);
				session.setPassword(ShowMycatProcessor.ShowMycatSqlonlinePasswd());
				session.setConfig("StrictHostKeyChecking", "no");
				//设置登陆超时时间   
			    session.connect(30000);
			    
			    channel = session.openChannel("sftp");
			    channel.connect();
			    ChannelSftp csftp = (ChannelSftp) channel;
			    csftp.put(filename,remotefilename);
			    csftp.disconnect();
			    
			    channel = (Channel) session.openChannel("exec");
			    ((ChannelExec) channel).setCommand(command);  
			    channel.connect();
			    
			    InputStream instream = channel.getInputStream();
		        
		        StringBuffer sb = new StringBuffer(4096);
		        
		        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));  
		        String buf = null;  
		        while ((buf = reader.readLine()) != null)  
		        {  
		        	sb.append(buf);
		        	sb.append("\n;");
		        }  
		        
		        reader.close();  
		        
		        channel.disconnect();  
		        session.disconnect(); 
		        
		        if(sb.toString().indexOf("ERROR")>-1)
		        {
		        	context.setMsg(sb.toString());
		        }
		        else if(sb.toString().indexOf("dbtablename ")>-1)
		        {
		        	context.setMsg(sb.toString().replace("dbtablename", "mysqldump"));
		        }
		        else
		        {
		        	context.setMsg("check sql ok!");
		        }
		        System.out.println(context.getMsg());
			    
		}catch ( Exception e)
		{
			
			  context.setMsg(e.getMessage());
		}
		
		
		
		
		
		System.out.println("tmpdir:"+tmpdir);
		
		
		return context;
	}
	public RainbowContext sqlcheck(RainbowContext context) {
		return sqlonline(context,"ERROR");
	
	}
	
	public RainbowContext sqlback(RainbowContext context) {
		return sqlonline(context,"dbtablename");
	
	}
	
	public RainbowContext sqlslow(RainbowContext context) { 
		return baseQuery(context, SYSSQLSLOW_NAMESPACE, "sqlslow"); 
	}
	public RainbowContext sqlhigh(RainbowContext context) {
		return baseQuery(context, SYSSQLHIGH_NAMESPACE, "sqlhigh"); 
	}	
	public RainbowContext sqlhighInfo(RainbowContext context) {
		return baseQuery(context, SYSSQLHIGH_NAMESPACE, "sqlhighInfo"); 
	}		
	public RainbowContext sqlsum(RainbowContext context) { 
		return baseQuery(context, SYSSQLSUM_NAMESPACE, "sqlsum"); 
	}
	public RainbowContext sqlsumtable(RainbowContext context) {
		return baseQueryAll(context, SYSSQLTABLE_NAMESPACE, "sqlsumtable");  
	}	
	public RainbowContext sqlsumtableInfo(RainbowContext context) {
		return baseQuery(context, SYSSQLTABLE_NAMESPACE, "sqlsumtableInfo");  
	}		
	public RainbowContext syslog(RainbowContext context) {
		return base(context,"syslog");
	}	
	public RainbowContext heartbeat(RainbowContext context) {
		context = base(context,"heartbeat");
		System.out.println(context.getRows().toString());
		return context;
	}
	
	public RainbowContext heartbeatDetail(RainbowContext context) {
		return base(context,"heartbeatDetail");
	}
	
	public RainbowContext dataSouceSynstatus(RainbowContext context) {
		return base(context,"dataSouceSynstatus");
	}
	
	public RainbowContext dataSouceDetail(RainbowContext context) {
		return base(context,"dataSouceDetail");
	}
	public RainbowContext whitehost(RainbowContext context) {
		return base(context,"whitehost");
	}
	public RainbowContext addWhitehost(RainbowContext context) {
		System.out.println(context.getAttr().toString());
		return base(context,"addwhitehost");
	}
}
