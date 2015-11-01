package org.mycat.web.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.hx.rainbow.common.context.RainbowContext;
import org.hx.rainbow.common.core.service.BaseService;
import org.hx.rainbow.common.util.ObjectId;
import org.mycat.web.util.DataSourceUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("zkConfigService")
public class ZkConfigService  extends BaseService {
	private static final String NAMESPACE = "SYSZOOKEEPER";

	public RainbowContext query(RainbowContext context) {
		super.query(context, NAMESPACE);
		return context;
	}


	public RainbowContext queryByPage(RainbowContext context) {
		super.queryByPage(context, NAMESPACE);
		return context;
	}

	public synchronized RainbowContext insert(RainbowContext context) throws Exception {

		RainbowContext query = new RainbowContext();
		query.addAttr("name", context.getAttr("name"));
		query = super.query(query, NAMESPACE);

		if (query.getRows() != null && query.getRows().size() > 0) {
			context.setMsg("名称已存在");
			context.setSuccess(false);
			return context;
		}

		context.addAttr("zkid", 1);
		//context.addAttr("createTime", new Date());
		super.insert(context, NAMESPACE);
		return context;
	}
	
	public synchronized RainbowContext insertCluster(RainbowContext context) throws Exception {

		RainbowContext query = new RainbowContext();
		query.addAttr("name", context.getAttr("name"));
		query = super.query(query, NAMESPACE);

		if (query.getRows() != null && query.getRows().size() > 0) {
			context.setMsg("名称已存在");
			context.setSuccess(false);
			return context;
		}

		context.addAttr("zkid", 2);
		//context.addAttr("createTime", new Date());
		super.insert(context, NAMESPACE);
		return context;
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
     	query.addAttr("cluster", cluster);
     	Map<String, Object> data = super.getDao().get(NAMESPACE, "load", query.getAttr());
     	System.out.println(data.toString());
     	String ip = (String)data.get("ip");
     	String port= data.get("port").toString();
     	context.getAttr().clear();
     	readZkinfo(context,ip+":"+port,"/"+cluster);
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
      //  attr.put("Stat", stat);
        context.addRow(attr);        
        //更新节点
       // client.setData().withVersion(-1).forPath(mainPath, "Data".getBytes());     
        //获取子节点列表
        List<String> children = client.getChildren().forPath(aPath);
        if (children.size()>0) {
         // System.out.println("Children: " + children);        
          for(int i = 0; i < children.size(); i++)  {
      	    readNode(context,client,aPath+"/"+children.get(i));
          }
        }
    }
    //public static void main(String[] args) throws Exception {
    	//readZkinfo("127.0.0.1:2181","/cluster1");
   // }
}
