package org.mycat.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.hx.rainbow.common.context.RainbowContext;

public class ZkTestReadConfig {
    public static void main(String[] args) throws Exception {
    	RainbowContext context=new RainbowContext();
    	//readZkinfo(context,"127.0.0.1:2181","/mycat-zone");
    	readZkinfo(context,"127.0.0.1:2181","/mycat");
    	 //System.out.println("Children: " + context); 
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
    public static void readZkinfo(RainbowContext context,String zk,String path) throws Exception {  
    	readZkinfo(context,zk,path,false);
    }
	 public static void readZkinfo(RainbowContext context,String zk,String path,boolean isDel) throws Exception {    
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
         if (isDel) {
           client.delete().guaranteed().deletingChildrenIfNeeded().withVersion(-1).forPath(mainPath);
         }
         client.close();
     }      
}
