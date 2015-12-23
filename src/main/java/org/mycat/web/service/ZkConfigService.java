package org.mycat.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
//import org.mycat.web.ZkTestReadConfig;
import org.mycat.web.model.Menu;
import org.mycat.web.util.Constant;
import org.mycat.web.util.DataSourceUtils;
import org.mycat.web.util.MycatPathConstant;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Lazy
@Service("zkConfigService")
public class ZkConfigService  extends BaseService {
	private static final String NAMESPACE = "SYSZOOKEEPER";
	
	private static final String MENU_TYPE_ZONE = "1";
	private static final String MENU_TYPE_CLUSTER_GROUP = "2";
	private static final String MENU_TYPE_CLUSTER_NODE = "3";
	private static final String MENU_TYPE_HOST_GROUP = "4";	
	private static final String MENU_TYPE_HOST_NODE = "5";
	private static final String MENU_TYPE_PROJECT_GROUP = "6";
	private static final String MENU_TYPE_PROJECT_NODE = "7";
	private static final String MENU_TYPE_NODE = "8";
	
	
	
	public RainbowContext query(RainbowContext context) {
		//super.query(context, NAMESPACE);
	    String zkpath=(String)context.getAttr("zkpath");
	    String zkid=(String)context.getAttr("zkid");	
	    String config=(String)context.getAttr("config");	
	    List<String> configid=ZookeeperService.getInstance().getChilds("/"+zkpath+"/"+zkid+"/"+config);
	    for(int i = 0; i < configid.size(); i++)  { 
	        Map<String, Object> attr = new HashMap<String, Object>();
	        attr.put("id", i);
	        attr.put("child", configid.get(i));
	    	context.getRows().add(attr);
	    }
		context.setMsg("OK!");
		context.setSuccess(true);	
		return context;
	}
	private List<Map<String, Object>> getMmgrid(List<Map<String, Object>> mapList,String child){
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		for (int i=0;i<mapList.size();i++){			
			//Iterator it = mapList.get(i).keySet().iterator();  
			int l=1;			
			for(Map.Entry<String, Object> entry:mapList.get(i).entrySet()) {
				Map<String, Object> map=new HashMap<>();
	             map.put("name", l++);
	             map.put("param", entry.getKey());
	             map.put("value", entry.getValue());	
	             rows.add(map);
			}            
		}
		return rows;
	 }
	public RainbowContext queryDetail(RainbowContext context) throws Exception {
	    String zkpath=(String)context.getAttr("zkpath");
	    String zkid=(String)context.getAttr("zkid");
	    String config=(String)context.getAttr("config");	
	    String ds=(String)context.getAttr("ds");
		if (!(ds ==  null || ds.isEmpty())){
			ds="/"+ds;
		}
	    String childPath =ZKPaths.makePath("/"+zkpath+"/"+zkid+"/"+config,ds);
	    Map<String, Object> readNode = ZookeeperService.getInstance().readNode(childPath);
	    if(readNode==null)
	    	return context;
	    ArrayList<Map<String,Object>> list = new ArrayList<Map<String, Object>>();
	    list.add(readNode);
	    context.addRows(getMmgrid(list,ds));
	    context.setTotal(context.getRows().size());	
		context.setMsg("OK!");
		context.setSuccess(true);	
		return context;
	}
	
	public RainbowContext queryChilds(RainbowContext context) throws Exception {
	    String zkpath=(String)context.getAttr("zkpath");
	    String zkid=(String)context.getAttr("zkid");
	    String config=(String)context.getAttr("config");	
	    String ds=(String)context.getAttr("ds");
		if (!(ds ==  null || ds.isEmpty())){
			ds="/"+ds;
		}
		
	    String childPath =ZKPaths.makePath("/"+zkpath,"/"+zkid);
	    context.addRows(getMmgrid(ZookeeperService.getInstance().getNodeOrChildNodes(childPath,config,ds),ds));
	    //context.addRows(getMmgrid(ZookeeperService.getInstance().getNodeOrChildNodes(childPath+ds),ds));
	    context.setTotal(context.getRows().size());	
		context.setMsg("OK!");
		context.setSuccess(true);	
		return context;
	}

	public synchronized RainbowContext insert(RainbowContext context) throws Exception {
       String ip=(String)context.getAttr("ip");
       String port=(String)context.getAttr("port");
       if (ZookeeperService.getInstance().Connected(ip+":"+port)){
    	   ZookeeperService.getInstance().UpdateZkConfig();
		  context.setMsg("注册中心配置成功!");
		  context.setSuccess(true);
          return context;
       }
       else {
 		  context.setMsg("连接注册中心失败，请检查!");
 		  context.setSuccess(true);
           return context;    	   
       }
	}	

	
	public RainbowContext update(RainbowContext context) {
		super.update(context, NAMESPACE);
		context.getAttr().clear();
		return context;
	}	
	
	public RainbowContext delete(RainbowContext context) {		
	    super.delete(context, NAMESPACE);
	    context.getAttr().clear();
	    return context;
   }
	  
    public  RainbowContext allZone(RainbowContext context){
    	 if (ZookeeperService.getInstance().Connected()){
    		 return zkConnectOK(context);
    	 }
    	 else {
    		 return zkConnectFail(context);
    	 }
    }
    private RainbowContext zkConnectFail(RainbowContext context){
		List<Menu> menus =new ArrayList<Menu>();
		//菜单1
		Menu firstMenu4 = new Menu("1","注册中心","",MENU_TYPE_PROJECT_GROUP);
		Menu firstMenu4Sub = new Menu("1_1","配置","page/zk/zkconfig.html",MENU_TYPE_NODE);
		firstMenu4.getSubMenus().add(firstMenu4Sub);
		menus.add(firstMenu4);
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("menu", menus);
		context.addRow(attr);
    	return context;		
    }
    private RainbowContext zkConnectOK(RainbowContext context){
		List<Menu> menus =new ArrayList<Menu>();
		Menu mycatMenu= new Menu("1","Mycat-配置","",MENU_TYPE_PROJECT_GROUP);
		Menu mycatMenuSub1= new Menu("1-1","mycat服务管理","page/manger/mycat.html",MENU_TYPE_NODE);
		Menu mycatMenuSub2= new Menu("1-2","mycat-VM管理","page/manger/jmx.html",MENU_TYPE_NODE);
		Menu mycatMenuSub3= new Menu("1-3","mysql管理","page/manger/mysqlmonitor.html",MENU_TYPE_NODE);
		Menu mycatMenuSub4= new Menu("1-4","mycat系统参数","page/manger/sysparam.html",MENU_TYPE_NODE);
		Menu mycatMenuSub5= new Menu("1-5","mycat日志管理","page/manger/syslog.html",MENU_TYPE_NODE);
		Menu mycatMenuSub6= new Menu("1-6","网络拓扑图","page/manger/topol.html",MENU_TYPE_NODE);		 
		mycatMenu.getSubMenus().add(mycatMenuSub1);
		mycatMenu.getSubMenus().add(mycatMenuSub2);
		mycatMenu.getSubMenus().add(mycatMenuSub3);
		mycatMenu.getSubMenus().add(mycatMenuSub4);
		mycatMenu.getSubMenus().add(mycatMenuSub5);
		mycatMenu.getSubMenus().add(mycatMenuSub6);
		menus.add(mycatMenu);
		
		Menu monitorMenu= new Menu("2","Mycat-监控","",MENU_TYPE_PROJECT_GROUP);
		Menu monitorMenuSub1= new Menu("2-1","mycat性能监控","page/monitor/jrds.html",MENU_TYPE_NODE);
		Menu monitorMenuSub2= new Menu("2-2","mycatJVM性能监控","page/monitor/jrdsjvm.html",MENU_TYPE_NODE);		
		Menu monitorMenuSub3= new Menu("2-3","mysql性能监控","page/monitor/jrdsmysql.html",MENU_TYPE_NODE);		
		Menu monitorMenuSub4= new Menu("2-4","mycat物理节点","page/monitor/datahostinfo.html",MENU_TYPE_NODE);
		Menu monitorMenuSub5= new Menu("2-5","主从同步监控","page/monitor/masterslaveinfo.html",MENU_TYPE_NODE);		
		//Menu monitorMenuSub4= new Menu("2-4","节点负载监控","page/monitor/datahostinfo.html",MENU_TYPE_NODE);
		//Menu monitorMenuSub5= new Menu("2-5","数据节点监控","page/monitor/masterslaveinfo.html",MENU_TYPE_NODE);				
		monitorMenu.getSubMenus().add(monitorMenuSub1);
		monitorMenu.getSubMenus().add(monitorMenuSub2);
		monitorMenu.getSubMenus().add(monitorMenuSub3);
		monitorMenu.getSubMenus().add(monitorMenuSub4);
		monitorMenu.getSubMenus().add(monitorMenuSub5);
		menus.add(monitorMenu);		
		
		//菜单4
		Menu firstMenu4 = new Menu("4","SQL-监控","",MENU_TYPE_PROJECT_GROUP);
		Menu firstMenu4Sub = new Menu("4_1","SQL统计","page/sql/sqltj.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub2 = new Menu("4_2","SQL表分析","page/sql/sqltable.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub3 = new Menu("4_3","SQL监控","page/sql/sql.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub4 = new Menu("4_4","高频SQL","page/sql/sqlhigh.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub5= new Menu("4_5","慢SQL统计","page/sql/sqlslow.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub6 = new Menu("4_6","SQL解析","page/sql/sqlparse.html",MENU_TYPE_NODE);
		firstMenu4.getSubMenus().add(firstMenu4Sub);
		firstMenu4.getSubMenus().add(firstMenu4Sub2);
		firstMenu4.getSubMenus().add(firstMenu4Sub3);
		firstMenu4.getSubMenus().add(firstMenu4Sub4);
		firstMenu4.getSubMenus().add(firstMenu4Sub5);
		firstMenu4.getSubMenus().add(firstMenu4Sub6);
		menus.add(firstMenu4);
		
		
        //屏蔽 2015-12-12 sohudo  
		Menu mycatzone=getMycatZoneMenu();
		
		/*先屏蔽 2015-12-3 sohudo
		Menu firstMenu5 = new Menu("5","MySQL Group1","",MENU_TYPE_PROJECT_GROUP);
		Menu firstMenu5Sub1 = new Menu("5-1","MySQLGroup管理","page/manger/myrep.html",MENU_TYPE_NODE);
		Menu firstMenu5Sub2 = new Menu("5-2","MySQL Server1","page/manger/mysql.html",MENU_TYPE_NODE);
		firstMenu5.getSubMenus().add(firstMenu5Sub1);
		firstMenu5.getSubMenus().add(firstMenu5Sub2);
		//menus.add(firstMenu5);		
		mycatzone.getSubMenus().add(firstMenu5);
			
		
		Menu firstMenu6 = new Menu("6","ZONE","",MENU_TYPE_PROJECT_GROUP);
		Menu firstMenuSsubb1 = new Menu("6_1_1","Server","page/cluster/mycat_server_list.html",MENU_TYPE_CLUSTER_NODE);
		Menu firstMenuSsuba1 = new Menu("6_1_1","cluster","page/cluster/mycat_cluster_list.html",MENU_TYPE_CLUSTER_NODE);
		Menu firstMenuSsubb2 = new Menu("6_1_1","zone","page/cluster/mycat_zone_list.html",MENU_TYPE_CLUSTER_NODE);
		firstMenu6.getSubMenus().add(firstMenuSsuba1);
		firstMenu6.getSubMenus().add(firstMenuSsubb2);
		firstMenu6.getSubMenus().add(firstMenuSsubb1);
		menus.add(firstMenu6);		
		*/	
		
		/*先屏蔽 2015-12-3 sohudo
		Menu firstMenuSub3 = new Menu("5_3","Mycat LB","",MENU_TYPE_HOST_GROUP);
		Menu firstMenuSub3_1 = new Menu("5_3_1","LB Host1","",MENU_TYPE_HOST_NODE);
		Menu firstMenuSub3_2 = new Menu("5_3_2","LB Host2","",MENU_TYPE_HOST_NODE);
		firstMenuSub3.getSubMenus().add(firstMenuSub3_1);
		firstMenuSub3.getSubMenus().add(firstMenuSub3_2);		
		mycatzone.getSubMenus().add(firstMenuSub3);			
		*/
		
		menus.add(mycatzone);		
		//context.addAttr("menu",menus);  
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("menu", menus);
		context.addRow(attr);
    	return context;
    }
    private Menu getMycatZoneMenu(){
      Menu mycatZone = new Menu("5","Mycat Zone","",MENU_TYPE_ZONE);
      List<String> cluster=ZookeeperService.getInstance().getChilds("/");
      if (cluster!=null){
    	  for(int i = 0; i < cluster.size(); i++)  { 
    		if (!cluster.get(i).equals("mycat-eye")){ 
    			String path="page/zk/zkread.html";
    			
        		switch (cluster.get(i)) {
        		case Constant.MYCAT_ZONE_KEY:
    				path="page/mycat/zone_detail.html";
    				break;
    			case Constant.MYCAT_CLUSTER_KEY:
    				path="page/cluster/mycat_cluster_detail.html";
    				break;
    			case Constant.MYCAT_HOST_KEY:
    				path="page/mycat/host_detail.html";
    				break;
    			case Constant.MYCAT_NODES_KEY:
    				path="page/mycat/node_detail.html";
    				break;
    			case Constant.MYCAT_MYSQL_GROUP_KEY:
    				path="page/mycat/mysqlgroup_detail.html";
    				break;
    			case Constant.MYCAT_MYSQLS_KEY:
    				path="page/mycat/mysql_detail.html";
    				break;
    			default:
    				break;
    			}
    		  Menu clusterMenu = new Menu("5."+i,cluster.get(i),"",MENU_TYPE_CLUSTER_GROUP);
    		  List<String> mycatid=ZookeeperService.getInstance().getChilds("/"+cluster.get(i));
    		  if (mycatid!=null){
    			  for(int j = 0; j < mycatid.size(); j++)  {  
   				    Menu mycatMenu = new Menu("5."+i+j,mycatid.get(j),path+"?zkpath="+cluster.get(i)+"&zkid="+mycatid.get(j),MENU_TYPE_NODE); 
      				  /*
    				  List<String> configid=ZookeeperService.getInstance().getChilds(CONFIG_MYCAT_ZONE+"/"+cluster.get(i)+"/"+mycatid.get(i));
    				  if (configid!=null){
    					  for(int m = 0; m < configid.size(); m++)  { 
    						  Menu configMenu = new Menu("5."+i+j+m,"Mycat"+configid.get(m),"page/zk/zkread.html?zkpath="+cluster.get(i)+"&zkid="+mycatid.get(i),MENU_TYPE_CLUSTER_NODE); 
    						  mycatMenu.getSubMenus().add(configMenu);
    					  }
    					  
    				  }
    				  */
    				  clusterMenu.getSubMenus().add(mycatMenu);    				 
    			  }
    		  }
    		  mycatZone.getSubMenus().add(clusterMenu);    	
    		  }  
    	  }
      }
      return mycatZone;	
    }
	public RainbowContext getZkconfig(RainbowContext context) throws Exception {	
     	String cluster = (String)context.getAttr("ds");
		try {
			if(cluster==null){
				context.setSuccess(false);
				context.setMsg("请选择注册中心!");
				return context;
			}
		} catch (Exception e) {
			
		}     	
     	RainbowContext query = new RainbowContext();
     	context.getAttr().clear();
     	//ZkTestReadConfig.readZkinfo(context,ZookeeperService.getInstance().getZookeeper(),"/"+cluster);
     	return context;
	}
	
 
   
 

}