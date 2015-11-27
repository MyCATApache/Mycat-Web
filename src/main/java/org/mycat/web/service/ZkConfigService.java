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
import org.mycat.web.model.Menu;
import org.mycat.web.util.DataSourceUtils;
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
	
	private static final String CONFIG_MYCAT_ZONE= "/mycat-zone";
	
	
	public RainbowContext query(RainbowContext context) {
		//super.query(context, NAMESPACE);
	    String zkpath=(String)context.getAttr("zkpath");
	    String zkid=(String)context.getAttr("zkid");	
	    String config=(String)context.getAttr("config");	
	    List<String> configid=ZookeeperService.getInstance().getChilds(CONFIG_MYCAT_ZONE+"/"+zkpath+"/"+zkid+"/"+config+"-config");
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
	
	
	public RainbowContext queryChilds(RainbowContext context) throws Exception {
	    String zkpath=(String)context.getAttr("zkpath");
	    String zkid=(String)context.getAttr("zkid");
	    String config=(String)context.getAttr("config");	
	    String ds=(String)context.getAttr("ds");
		if (!(ds ==  null || ds.isEmpty())){
			ds="/"+ds;
		}
		
	    String childPath =ZKPaths.makePath(CONFIG_MYCAT_ZONE+"/"+zkpath,"/"+zkid);
	    context.addRows(getMmgrid(ZookeeperService.getInstance().getNodeOrChildNodes(childPath,config+"-config",ds),ds));
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
		Menu mycatMenuSub3= new Menu("1-3","mycat系统参数","page/manger/sysparam.html",MENU_TYPE_NODE);
		Menu mycatMenuSub4= new Menu("1-4","mycat日志管理","page/manger/syslog.html",MENU_TYPE_NODE);
		Menu mycatMenuSub5= new Menu("1-5","Zookeeper信息","page/manger/zkread.html",MENU_TYPE_NODE);		 
		mycatMenu.getSubMenus().add(mycatMenuSub1);
		mycatMenu.getSubMenus().add(mycatMenuSub2);
		mycatMenu.getSubMenus().add(mycatMenuSub3);
		mycatMenu.getSubMenus().add(mycatMenuSub4);
		mycatMenu.getSubMenus().add(mycatMenuSub5);
		menus.add(mycatMenu);
		
		Menu monitorMenu= new Menu("2","Mycat-监控","",MENU_TYPE_PROJECT_GROUP);
		Menu monitorMenuSub1= new Menu("2-1","mycat性能监控","page/monitor/jrds.html",MENU_TYPE_NODE);
		Menu monitorMenuSub2= new Menu("2-2","mycat物理节点","page/monitor/datahostinfo.html",MENU_TYPE_NODE);
		Menu monitorMenuSub3= new Menu("2-3","主从同步监控","page/monitor/masterslaveinfo.html",MENU_TYPE_NODE);		
		//Menu monitorMenuSub4= new Menu("2-4","节点负载监控","page/monitor/datahostinfo.html",MENU_TYPE_NODE);
		//Menu monitorMenuSub5= new Menu("2-5","数据节点监控","page/monitor/masterslaveinfo.html",MENU_TYPE_NODE);				
		monitorMenu.getSubMenus().add(monitorMenuSub1);
		monitorMenu.getSubMenus().add(monitorMenuSub2);
		monitorMenu.getSubMenus().add(monitorMenuSub3);
		menus.add(monitorMenu);		
		
		//菜单4
		Menu firstMenu4 = new Menu("4","SQL-监控","",MENU_TYPE_PROJECT_GROUP);
		Menu firstMenu4Sub = new Menu("4_1","SQL统计","page/sql/sqltj.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub2 = new Menu("4_2","SQL监控","page/sql/sql.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub3 = new Menu("4_3","慢SQL统计","page/sql/sqlslow.html",MENU_TYPE_NODE);
		Menu firstMenu4Sub4 = new Menu("4_4","SQL解析","page/sql/sqlparse.html",MENU_TYPE_NODE);
		firstMenu4.getSubMenus().add(firstMenu4Sub);
		firstMenu4.getSubMenus().add(firstMenu4Sub2);
		firstMenu4.getSubMenus().add(firstMenu4Sub3);
		firstMenu4.getSubMenus().add(firstMenu4Sub4);
		menus.add(firstMenu4);
		
		
		Menu firstMenu5 = new Menu("5","MSQL","",MENU_TYPE_PROJECT_GROUP);
		Menu firstMenu5Sub1 = new Menu("5-1","MySQL Group","page/manger/myrep.html",MENU_TYPE_NODE);
		Menu firstMenu5Sub2 = new Menu("5-2","MySQL Servers","page/manger/mysql.html",MENU_TYPE_NODE);
		firstMenu5.getSubMenus().add(firstMenu5Sub1);
		firstMenu5.getSubMenus().add(firstMenu5Sub2);
		menus.add(firstMenu5);
		/*
		//菜单1
		Menu firstMenus1 = new Menu("5","Mycat Zone","",MENU_TYPE_ZONE);
		//菜单1 第二级 submenu
		Menu firstMenuSub1 = new Menu("5_1","Mycat Cluster","",MENU_TYPE_CLUSTER_GROUP);
		//菜单1 第三级 菜单即第二级的子菜单
		Menu firstMenuSsuba = new Menu("5_1_1","Mycat Server1","page/sql/sqltj.html",MENU_TYPE_CLUSTER_NODE);
		firstMenuSub1.getSubMenus().add(firstMenuSsuba);
		Menu firstMenuSsubb = new Menu("5_1_1","Mycat Server2","page/sql/sqltj.html",MENU_TYPE_CLUSTER_NODE);
		firstMenuSub1.getSubMenus().add(firstMenuSsubb);
		firstMenus1.getSubMenus().add(firstMenuSub1);
		
		Menu firstMenuSub2 = new Menu("5_2","MySQL Group","",MENU_TYPE_HOST_GROUP);
		Menu firstMenuSub2_1 = new Menu("5_2_1","MySQL Host1","page/sql/sqltj.html",MENU_TYPE_HOST_NODE);
		Menu firstMenuSub2_2 = new Menu("5_2_2","MySQL Host2","page/sql/sqltj.html",MENU_TYPE_HOST_NODE);
		firstMenuSub2.getSubMenus().add(firstMenuSub2_1);
		firstMenuSub2.getSubMenus().add(firstMenuSub2_2);
		firstMenus1.getSubMenus().add(firstMenuSub2);
		
		Menu firstMenuSub3 = new Menu("5_3","Mycat LB","",MENU_TYPE_HOST_GROUP);
		Menu firstMenuSub3_1 = new Menu("5_3_1","LB Host1","page/sql/sqltj.html",MENU_TYPE_HOST_NODE);
		Menu firstMenuSub3_2 = new Menu("5_3_2","LB Host2","page/sql/sqltj.html",MENU_TYPE_HOST_NODE);
		firstMenuSub3.getSubMenus().add(firstMenuSub3_1);
		firstMenuSub3.getSubMenus().add(firstMenuSub3_2);
		firstMenus1.getSubMenus().add(firstMenuSub3);	
		*/
		menus.add(getMycatZoneMenu());		
		//context.addAttr("menu",menus);  
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("menu", menus);
		context.addRow(attr);
    	return context;
    }
    private Menu getMycatZoneMenu(){
      Menu mycatZone = new Menu("5","Mycat Zone","",MENU_TYPE_ZONE);
      List<String> cluster=ZookeeperService.getInstance().getChilds(CONFIG_MYCAT_ZONE);
      if (cluster!=null){
    	  for(int i = 0; i < cluster.size(); i++)  { 
    		  Menu clusterMenu = new Menu("5."+i,"Mycat "+cluster.get(i),"",MENU_TYPE_CLUSTER_GROUP);
    		  List<String> mycatid=ZookeeperService.getInstance().getChilds(CONFIG_MYCAT_ZONE+"/"+cluster.get(i));
    		  if (mycatid!=null){
    			  for(int j = 0; j < mycatid.size(); j++)  {  
    				  Menu mycatMenu = new Menu("5."+i+j,"Mycat"+mycatid.get(i),"page/zk/zkread.html?zkpath="+cluster.get(i)+"&zkid="+mycatid.get(i),MENU_TYPE_CLUSTER_NODE); 
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
     	readZkinfo(context,ZookeeperService.getInstance().getZookeeper(),"/"+cluster);
     	return context;
	}
	
	 public static void readZkinfo(RainbowContext context,String zk,String path) throws Exception {    
         CuratorFramework client = CuratorFrameworkFactory.builder()
       		  .connectString(zk)  // 服务器列表
                 .sessionTimeoutMs(5000) // 会话超时时间，单位毫秒
                 .connectionTimeoutMs(3000)// 连接创建超时时间，单位毫秒
                 .retryPolicy(new ExponentialBackoffRetry(1000, 3))// 重试策略
                 .build();
         client.start();
        String mainPath=path;
         Stat systemPropertiesNodeStat = client.checkExists().forPath(mainPath);
         if (systemPropertiesNodeStat == null) {
         	//client.create().creatingParentsIfNeeded().forPath(mainPath);
           //}     
           //创建节点
           //client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(mainPath, "xxxxx".getBytes());
         }     
         readNode(context,client,mainPath);
         //删除
         //client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(mainPath);
         client.close();
     }   
   
   public static void readNode(RainbowContext context,CuratorFramework client,String aPath) throws Exception {
       //读取节点
       Stat stat = new Stat();
       byte[] nodeData = client.getData().storingStatIn(stat).forPath(aPath);
     
       Map<String, Object> attr = new HashMap<String, Object>();
       attr.put("Path", aPath);
       attr.put("Data", new String(nodeData));
       System.out.println("path: " +attr.get("Path")); 
       System.out.println("Data: " +attr.get("Data"));    
     //  attr.put("Stat", stat);
       context.addRow(attr);        
       //更新节点
      // client.setData().withVersion(-1).forPath(mainPath, "Data".getBytes());     
       //获取子节点列表
       List<String> children = client.getChildren().forPath(aPath);
       if (children.size()>0) {
         System.out.println("Children: " +aPath+"-----"+ children);        
         System.out.println("---------------------------"); 
         for(int i = 0; i < children.size(); i++)  {
     	    readNode(context,client,aPath+"/"+children.get(i));
         }
       }
   }    
    public static void main(String[] args) throws Exception {
    	RainbowContext context=new RainbowContext();
    	readZkinfo(context,"127.0.0.1:2181","/mycat-zone");
    	//readZkinfo(context,"127.0.0.1:2181","/mycat-eye");
    	 //System.out.println("Children: " + context); 
    }
}
