package org.mycat.web.util;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.hamcrest.core.IsEqual;
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

	public synchronized void connect(String host, String nameSpace) {
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
				boolean blockUntilConnected = client.blockUntilConnected(5000,
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

	public void createEphemeralNode(String path, String data) throws Exception {
		Preconditions.checkNotNull(client, errorWithNullClient);
		data = data == null ? "" : data;
		client.create().creatingParentsIfNeeded()
				.withMode(CreateMode.EPHEMERAL)
				.forPath(path, data.getBytes(Constant.CHARSET));
	}

	public String createSeqNode(String path, String data)  {
		Preconditions.checkNotNull(client, errorWithNullClient);
		String rePath=null;
		try {
			rePath = client.create().creatingParentsIfNeeded()
					.withMode(CreateMode.PERSISTENT_SEQUENTIAL)
					.forPath(path, data.getBytes(Constant.CHARSET));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	public String getNodeData(String path) {
		Preconditions.checkNotNull(client, errorWithNullClient);
		String rep=null;
		try {
			byte[] byteData = client.getData().forPath(path);
			rep=new String(byteData, Constant.CHARSET);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rep;
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
			return getChildNodeData(path, entity, 0,null,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public List<String> getChildNode(String path){
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
			return forPath;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public <T> Map<String, Object> getChildNodeData(String path,Class<T> entity,Integer begin,Integer size,Map<String, Object> attr){
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
	
	public <T> T getBeanData(String path,Class<T> claz) {
		Preconditions.checkNotNull(client, errorWithNullClient);
		String rep=null;
		try {
			byte[] byteData = client.getData().forPath(path);
			rep=new String(byteData, Constant.CHARSET);
			
		    T t = JSON.parseObject(rep, claz);
		    return t;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public <T> Map<String, Object> getChildNodeDataByPage(String path,Class<T> entity,Integer limit,Integer offset,Map<String, Object> attr) throws Exception{
		Preconditions.checkNotNull(client, errorWithNullClient);
		Map<String, Object> reMap=new HashMap<String, Object>();
		Stat stat = client.checkExists().forPath(path);
		if(stat==null)
			return null;
		List<String> forPath=new ArrayList<String>();
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
	
	
	
	public boolean createMainPath() throws Exception {
		if (!existsNode(Constant.MYCAT_EYE)) {
			createNode(Constant.MYCAT_EYE,"mycat eye");
			createNode(Constant.mycats,"mycat node");
			createNode(Constant.mycat_jmx,"jmx");
			createNode(Constant.MYCAT_MYSQL,"mysql");
			createNode(Constant.mycat_snmp,"snmp");
			createNode(Constant.mycat_processor,"processor");
		}
		return true;
	}
	
	
}
