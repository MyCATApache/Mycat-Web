package org.mycat.web.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.CreateMode;
import org.hx.rainbow.common.context.RainbowContext;

import com.alibaba.fastjson.JSON;

import org.mycat.web.util.JsonUtils;

public class ZookeeperService {
	private  static  ZookeeperService  zookeeperService;
	private final String zooKey="zookeeper";
	
	private final String mycat_eye="/mycat-eye";
	private final String mycats=mycat_eye+"/mycat";
	private final String mycat_jmx=mycat_eye+"/mycat_jmx";
	private final String mycat_snmp=mycat_eye+"/mycat_snmp";
	private final String mycat_processor=mycat_eye+"/mycat_processor";	
	
	private  String zookeeper;
	private static CuratorFramework framework;
	
	private ZookeeperService(){
		Properties properties = new Properties();
		try {
			properties.load(ZookeeperService.class.getClassLoader().getResourceAsStream("mycat.properties"));
			zookeeper=properties.getProperty(zooKey);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ZookeeperService getInstance(){ 
		if(zookeeperService == null){
			synchronized (ZookeeperService.class) {
				if(zookeeperService == null){
					zookeeperService = new ZookeeperService();
				}
			}
		}
		return zookeeperService;	   
	}
	
    private static CuratorFramework createConnection(String url) {
        CuratorFramework curatorFramework = CuratorFrameworkFactory
                .newClient(url, new ExponentialBackoffRetry(100, 6));
        //start connection
        curatorFramework.start();
        //wait 3 second to establish connect
        try {
            curatorFramework.blockUntilConnected(3, TimeUnit.SECONDS);
            if (curatorFramework.getZookeeperClient().isConnected()) {
                return curatorFramework;
            }
        } catch (InterruptedException e) {
        }
   
        //fail situation
        curatorFramework.close();
        //throw new RuntimeException("failed to connect to zookeeper service : " + url);
       System.out.println("failed to connect to zookeeper service : " + url);
       return null;
    }
    
	public boolean Connected(){
		return Connected(zookeeper);
	}
	
	public boolean Connected(String value){
		framework = createConnection(value);
		if (framework!=null){
			zookeeper=value;
			createMainPath();
			return true;
		}
		else {
			return false;
		}
	}	
	public void UpdateZkConfig(){
		Properties properties = new Properties();
		try {
			properties.load(ZookeeperService.class.getClassLoader().getResourceAsStream("mycat.properties"));
			properties.setProperty(zooKey, zookeeper);
			
			String realPath = ZookeeperService.class.getClassLoader().getResource("mycat.properties").getPath();
			OutputStream out = new FileOutputStream(realPath);
			System.out.println("realPath : " + realPath);
			properties.store(out, "###ZK CONFIG");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public boolean createMainPath() {
		if (!isExitPath(mycat_eye)) {
			createPath(mycat_eye,"mycat eye");
			createPath(mycats,"mycat node");
			createPath(mycat_jmx,"jmx");
			createPath(mycat_snmp,"snmp");
			createPath(mycat_processor,"processor");
		}
		return true;
	}
	
	private boolean isExitPath(String mainPath){
        Stat nodeStat= null;
		try {
			nodeStat = framework.checkExists().forPath(mainPath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return nodeStat!=null;
	}
	private boolean createPath(String mainPath,String value) {		
		boolean isCreate=true;
      //  if (!isExitPath(mainPath)) {
        	try {
				framework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(mainPath,value.getBytes());				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isCreate=false;
			}
     //   }
        return isCreate;
	}
	
	private int getChildNum(String aPath) {		        
		try {
			List<String> children=null;
			children = framework.getChildren().forPath(aPath);
			if (children!=null) {
			  return children.size();
			}
			else {
				return 0;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}       
	} 
	
	private boolean processLeafNode(String childPath,Object innerMap) {
        try {
            Stat restNodeStat = framework.checkExists().forPath(childPath);
            if (restNodeStat == null) {
                framework.create().creatingParentsIfNeeded().forPath(childPath);
            }
              framework.setData().forPath(childPath, JSON.toJSONString(innerMap).getBytes());
               return true;
        } catch (Exception e) {
          //  LOGGER.error("create node error: {} ", e.getMessage(), e);
          //  throw new RuntimeException(e);
        	return false;
        }
    }	
	
	private boolean insert(String ParentPath,String guid,Object innerMap) {
		 return processLeafNode(ParentPath+"/"+guid,innerMap);
	}
	
	private boolean insert(String ParentPath,Object innerMap) {
		 int index=getChildNum(ParentPath)+1;
		 return insert(ParentPath+"/"+String.valueOf(index),innerMap);
	}
	

	//删除
	private boolean del(String ParentPath,String guid) {
		try {
			framework.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(ParentPath+"/"+guid);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
    public Map<String, Object> readNode(String aPath) {
        //读取节点
        Stat stat = new Stat();
        byte[] nodeData;
		try {
			nodeData = framework.getData().storingStatIn(stat).forPath(aPath);
			return JsonUtils.json2Map(new String(nodeData));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        
    }   
    
	private List<Map<String, Object>> getPath(String ParentPath){
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		try {
			List<String> children=null;
			children = framework.getChildren().forPath(ParentPath);
			if (children!=null) {
		          for(int i = 0; i < children.size(); i++)  {
		        	  rows.add( readNode(ParentPath+"/"+children.get(i)));
		            }
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		
		}     		
		return rows;
	}
	//增加修改节点
	public boolean insertMycat(String guid,Object innerMap) {
		return insert(mycats,guid,innerMap);
	}	
	//获取节点
	public Map<String, Object> getMycatNode(String Key) {
		return readNode(mycats+"/"+Key);
	}		
	//获取全部节点
	public List<Map<String, Object>> getMycat() {
		return getPath(mycats);
	}	
	//删除节点
	public boolean delMycat(String guid){
		return del(mycats,guid);
	}	
	
	//增加修改节点
	public boolean insertJmx(String guid,Object innerMap) {
		return insert(mycat_jmx,guid,innerMap);
	}	
	//获取节点
	public Map<String, Object> getJmxNode(String Key) {
		return readNode(mycat_jmx+"/"+Key);
	}		
	//获取全部节点
	public List<Map<String, Object>> getJmx() {
		return getPath(mycat_jmx);
	}	
	//删除节点
	public boolean delJmx(String guid){
		return del(mycat_jmx,guid);
	}		
	
	//增加修改节点
	public boolean insertSnmp(String guid,Object innerMap) {
		return insert(mycat_snmp,guid,innerMap);
	}	
	//获取节点
	public Map<String, Object> getSnmpNode(String Key) {
		return readNode(mycat_snmp+"/"+Key);
	}		
	//获取全部节点
	public List<Map<String, Object>> getSnmp() {
		return getPath(mycat_snmp);
	}	
	//删除节点
	public boolean delSnmp(String guid){
		return del(mycat_snmp,guid);
	}		
}
