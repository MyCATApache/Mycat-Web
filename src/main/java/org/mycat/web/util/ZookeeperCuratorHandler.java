package org.mycat.web.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.base.Preconditions;

/**
 * zookeeper公共操作的curator框架实现，单例
 */
public final class ZookeeperCuratorHandler {
	private static final Logger LOG = LoggerFactory
			.getLogger(ZookeeperCuratorHandler.class);
	private CuratorFramework client = null;
	private StateListener listener = new StateListener();
	private String errorWithNullClient = "zookeeper CuratorFramework is null, please invoke connect method first";
//	private  String zookeeper;
	private final String zooKey="zookeeper";
	
	private static class SingletonHolder {
		private static ZookeeperCuratorHandler instance = new ZookeeperCuratorHandler();
	}

	private ZookeeperCuratorHandler() {
	}

	public static ZookeeperCuratorHandler getInstance() {
		return SingletonHolder.instance;
	}

	public List<String> getChildrenName(String parentNodePath) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		return client.getChildren().forPath(parentNodePath);
	}

	public synchronized boolean connect(String host, String nameSpace) {
		boolean blockUntilConnected = false;
		if (client == null
				|| client.getState() != CuratorFrameworkState.STARTED) {
			Preconditions.checkArgument(StringUtils.isNotBlank(host),
					"zk host cannot be empty");
			nameSpace = (nameSpace == null ? "" : nameSpace);
			try {
				LOG.info("start to connect zookeeper[{}] with namespace[{}]",
						host, nameSpace);
				CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory
						.builder();
				client = builder.connectString(host).sessionTimeoutMs(60000)
						.connectionTimeoutMs(3000).canBeReadOnly(false)
						.defaultData("".getBytes("UTF-8"))
						.retryPolicy(new ExponentialBackoffRetry(1000, 3))
						.namespace(nameSpace).build();
				listener = new StateListener();
				client.getConnectionStateListenable().addListener(listener);
				client.start();
				blockUntilConnected = client.blockUntilConnected(5000,
						TimeUnit.MILLISECONDS);
				if (blockUntilConnected) {
					LOG.info(
							"connect zookeeper[{}] with namespace[{}] successful",
							host, nameSpace);
					createMainPath();
				} else {
					disconnect();
					throw new Exception("fail to connect zookeeper server["
							+ host + "] with namespace[" + nameSpace + "]");
				}
			} catch (Exception e) {
				LOG.error("fail to connect zookeeper server[" + host
						+ "] with namespace[" + nameSpace + "]", e);
			}
		}
		return blockUntilConnected;
	}

	public boolean isConnected() {
		return client != null
				&& client.getState() == CuratorFrameworkState.STARTED;
	}

	private static class StateListener implements ConnectionStateListener {

		public void stateChanged(CuratorFramework client,
				ConnectionState newState) {
			switch (newState) {
			case LOST: // 一旦丢失链接,就意味着zk server端已经删除了锁数据 lockedThread.clear();
				LOG.info("ZK-LOST.....");
				break;
			case SUSPENDED:
				LOG.info("ZK-SUSPENDED.....");
				break;
			case CONNECTED: // 一旦丢失链接,就意味着zk server端已经删除了锁数据
							// lockedThread.clear();
				LOG.info("ZK-CONNETED.....");
				break;
			case RECONNECTED: // 一旦丢失链接,就意味着zk server端已经删除了锁数据
								// lockedThread.clear();
				LOG.info("ZK-RECONNECTED.....");
				break;
			case READ_ONLY:
				LOG.info("ZK-READ_ONLY.....");
				break;
			default:
				LOG.info("ZK-" + newState.toString());
				break;
			}
		}
	}

	public synchronized void disconnect() {
		if (client != null) {
			try {
				client.getConnectionStateListenable().removeListener(listener);
				client.close();
			} catch (Exception e) {
			}
		}
	}

	public void UpdateZkConfig(String zkinfo) throws Exception {
		Properties properties = new Properties();
		properties.load(ZookeeperCuratorHandler.class.getClassLoader()
				.getResourceAsStream("mycat.properties"));
		properties.setProperty(zooKey, zkinfo);
		String realPath = ZookeeperCuratorHandler.class.getClassLoader()
				.getResource("mycat.properties").getPath();
		OutputStream out = new FileOutputStream(realPath);
		System.out.println("realPath : " + realPath);
		properties.store(out, "###ZK CONFIG");

	}
	public void createEphemeralNode(String path, String data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		data = data == null ? "" : data;
		client.create().creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.forPath(path, data.getBytes(Constant.CHARSET));
	}

	public String createSeqNode(String path, String data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		String	rePath = client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT_SEQUENTIAL)
					.forPath(path, data.getBytes(Constant.CHARSET));
		return rePath;
	}

	public void createEphemeralSeqNode(String path, String data)
			throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		data = data == null ? "" : data;
		client.create().creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
				.forPath(path, data.getBytes(Constant.CHARSET));
	}

	public boolean existsNode(String path) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		Stat stat = client.checkExists().forPath(path);
		if (stat == null) {
			return false;
		} else {
			return true;
		}
	}

	public void createEphemeralNode(String path) throws Exception {
		createEphemeralNode(path, null);
	}

	public void deleteNode(String path) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
	}

	public void deleteChildrenNodes(String path) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		List<String> childrenName = getChildrenName(path);
		for (String childrenNode : childrenName) {
			client.delete().guaranteed().deletingChildrenIfNeeded()
					.forPath(path + "/" + childrenNode);
		}
	}

	public String getNodeData (String path) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		byte[] byteData = client.getData().forPath(path);
		String rep = new String(byteData, Constant.CHARSET);
		return rep;
	}
	
	public Map<String,Object> getNodeDataForMap(String path) throws Exception{
		Preconditions.checkNotNull(client, errorWithNullClient);
		Stat stat = new Stat();
		byte[] nodeData = client.getData().storingStatIn(stat).forPath(path);
		String dataNode = new String(nodeData);
		return JsonUtils.json2Map(dataNode);
	}

	public String getNodeData(String path, Stat stat) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		byte[] byteData = client.getData().storingStatIn(stat).forPath(path);
		return new String(byteData, Constant.CHARSET);
	}

	public void setNodeData(String path, String data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		client.setData().forPath(path, data.getBytes(Constant.CHARSET));

	}

	public void setNodeDataWithVersion(String path, String data, int version)
			throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		client.setData().withVersion(version)
				.forPath(path, data.getBytes(Constant.CHARSET));
	}

	public void updateNodeData(String path, String data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		Stat stat = client.checkExists().forPath(path);
		if (stat == null) {
			client.create().creatingParentsIfNeeded()
					.forPath(path, data.getBytes(Constant.CHARSET));
		} else {
			client.setData().forPath(path, data.getBytes(Constant.CHARSET));
		}
	}

	public void updateNodeDataWithVersion(String path, String data, int version)
			throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		Stat stat = client.checkExists().forPath(path);
		if (stat == null) {
			client.create().creatingParentsIfNeeded()
					.forPath(path, data.getBytes(Constant.CHARSET));
		} else {
			client.setData().withVersion(version)
					.forPath(path, data.getBytes(Constant.CHARSET));
		}
	}

	public void createNode(String path, String data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		data = (data == null ? "" : data);
		client.create().creatingParentsIfNeeded()
				.forPath(path, data.getBytes(Constant.CHARSET));
	}

	public void createNodeNx(String path, String data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		Stat stat = client.checkExists().forPath(path);
		if (stat == null) {
			createNode(path, data);
		}
	}

	public int getNodeVersion(String path) throws Exception {
		Stat stat = new Stat();
		getNodeData(path, stat);
		return stat.getVersion();
	}

	public void createNode(String path) throws Exception {
		createNode(path, null);
	}

	public void createNodeNx(String path) throws Exception {
		createNodeNx(path, null);
	}

	public void copyNodeWithChildren(String srcPath, String distPath)
			throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		updateNodeData(distPath, getNodeData(srcPath));
		List<String> childrenName = getChildrenName(srcPath);
		for (String child : childrenName) {
			updateNodeData(distPath + "/" + child, getNodeData(srcPath + "/"
					+ child));
		}
	}

	public void setNodeData(String path, byte[] data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		client.setData().forPath(path, data);

	}

	public void updateNodeData(String path, byte[] data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		Stat stat = client.checkExists().forPath(path);
		if (stat == null) {
			client.create().creatingParentsIfNeeded().forPath(path, data);
		} else {
			client.setData().forPath(path, data);
		}
	}
	public <T> Map<String, Object> getChildNodeData(String path,Class<T> entity){
		try {
			return getChildNodeData(path, entity, 0,0,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	public List<Map<String,Object>> getChildNodeData(String path) throws Exception{
		Preconditions.checkNotNull(client, errorWithNullClient);
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		List<String> children = null;
		children = client.getChildren().forPath(path);
		if ((children != null) && children.size() > 0) {
			for (int i = 0; i < children.size(); i++) {
				Map<String,Object> node=readNode(path + "/" + children.get(i));
				if (node!=null) {
				  rows.add(node);
				}
			}
		} else {
			Map<String,Object> node=readNode(path);
			if (node!=null) {
				rows.add(node);
			}
		}
		return rows;
	}
    public Map<String, Object> readNode(String aPath) {
        //读取节点
        Stat stat = new Stat();
        byte[] nodeData;
		try {
			nodeData = client.getData().storingStatIn(stat).forPath(aPath);
			String dataNode = new String(nodeData).trim();
			//System.out.println("zk node:"+dataNode);
			if (dataNode.indexOf("{")>=0) {
				return JsonUtils.json2Map(dataNode);	
			}
			else
			  return null;		
		} catch (Exception e) {
			// TODO Auto-generated catch block			
			return null;
		}        
    } 	
	
	public List<String> getChildNode(String path) throws Exception{
		Preconditions.checkNotNull(client, errorWithNullClient);
		Stat stat = client.checkExists().forPath(path);
		if (stat == null) {
			return null;
		}
		List<String> forPath = new ArrayList<String>();
		return forPath;
	}
	public <T> Map<String, Object> getChildNodeData(String path,Class<T> entity,Integer begin,Integer size,Map<String, Object> attr) throws Exception{
		Preconditions.checkNotNull(client, errorWithNullClient);
		Map<String, Object> reMap=new HashMap<String, Object>();
		Stat stat=null;
		try {
			stat = client.checkExists().forPath(path);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(stat==null)
			return null;
		List<String> forPath=new ArrayList<String>();
		try {
			forPath = client.getChildren().forPath(path);
			List<String> remove=new ArrayList<String>();
			if (attr!=null&&attr.size()>=1&&attr.get("name")!=null) {
				String name=String.valueOf(attr.get("name"));
				if(StringUtils.isNotEmpty(name)){
					for (String s : forPath) {
						if(s.indexOf(name)<0)
							remove.add(s);
					}
				}
			}
			forPath.removeAll(remove);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(size==null){
			size=0;
		}
		int offsize=begin*size;
		if(size==0||offsize>forPath.size()){
			offsize=forPath.size();
		}
		List<Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
		for (int i = (begin-1)*size; i < offsize; i++) {
			String s=forPath.get(i);
			String nodeData = getNodeData(path+"/"+s);
			if(StringUtils.isEmpty(nodeData))
				continue;
			T t = JSONArray.parseObject(nodeData, entity);
			rows.add(JavaBeanToMapUtil.beanToMap(t));
		}
		reMap.put("rows", rows);
		reMap.put("total", forPath.size());
		return reMap;
	}
	
	public <T> T getBeanData(String path,Class<T> claz) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		String rep = null;
		byte[] byteData = client.getData().forPath(path);
		rep = new String(byteData, Constant.CHARSET);
		T t = JSON.parseObject(rep, claz);
		return t;
	}
	
	public <T> Map<String, Object> getChildNodeDataByPage(String path,Class<T> entity,String searchPath,Integer limit,Integer offset) throws Exception{
		Preconditions.checkNotNull(client, errorWithNullClient);
		Map<String, Object> reMap=new HashMap<String, Object>();
		Stat stat = client.checkExists().forPath(path);
		if(stat==null)
			return null;
		List<String> forPath = new ArrayList<String>();
		forPath = client.getChildren().forPath(path);
		List<String>mathPath = new ArrayList<String>();
		//根据zk路径 字符串匹配
		if(searchPath != null && !searchPath.equals("")){
			for (String p : forPath) {
				if(p.indexOf(searchPath) != -1){
					mathPath.add(p);
				}
			}
			forPath = mathPath;
		}
		//根据路径名称排序
		Collections.sort(forPath, new Comparator<String>() {
			public int compare(String o1, String o2) {
				return compareSort(o1, o2);
			}
		});
		
		if(offset == null){
			offset = 0;
		}
		int endset = offset+limit;
		if(endset > forPath.size()) {
			endset = forPath.size();
		}
		List<Map<String, Object>> rows=new ArrayList<Map<String, Object>>();
		for (int i = offset; i <= endset-1; i++) {
			String s=forPath.get(i);
			String nodeData = getNodeData(path+"/"+s);
			if(StringUtils.isEmpty(nodeData))
				continue;
			T t = JSONArray.parseObject(nodeData, entity);
			rows.add(JavaBeanToMapUtil.beanToMap(t));
		}
		reMap.put("rows", rows);
		reMap.put("total", forPath.size());
		return reMap;
	}
	
	public int compareSort(String o1, String o2) {

		String s1 = (String) o1;
		String s2 = (String) o2;
		int len1 = s1.length();
		int len2 = s2.length();
		int n = Math.min(len1, len2);
		char v1[] = s1.toCharArray();
		char v2[] = s2.toCharArray();
		int pos = 0;

		while (n-- != 0) {
			char c1 = v1[pos];
			char c2 = v2[pos];
			if (c1 != c2) {
				return c1 - c2;
			}
			pos++;
		}
		return len1 - len2;
	}

	public boolean createMainPath() throws Exception {
		if (!existsNode(Constant.MYCAT_EYE)) {
			createNode(Constant.MYCAT_EYE,"mycat eye");
			createNode(Constant.MYCATS,"mycat node");
			createNode(Constant.MYCAT_JMX,"jmx");
			createNode(Constant.MYCAT_MYSQL,"mysql");
			createNode(Constant.MYCAT_SNMP,"snmp");
			createNode(Constant.MYCAT_PROCESSOR,"processor");
		}
		return true;
	}
	
}
