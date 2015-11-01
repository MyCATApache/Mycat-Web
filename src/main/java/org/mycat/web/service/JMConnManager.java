package org.mycat.web.service;

import java.io.IOException;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.hx.rainbow.common.core.SpringApplicationContext;
import org.hx.rainbow.common.dao.Dao;
import org.mycat.web.jmonitor.JMConnBean;
import org.mycat.web.jmonitor.JMEevntCenter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.management.OperatingSystemMXBean;

/**
 * 
 * @author @author code_czp@126.com-2015年5月12日
 */
@SuppressWarnings("restriction")
public class JMConnManager implements NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(JMConnManager.class);
    public static final String GCCMS = "java.lang:type=GarbageCollector,name=ConcurrentMarkSweep";
    public static final String GCMSC = "java.lang:type=GarbageCollector,name=MarkSweepCompact";
    public static final String GCSCAV = "java.lang:type=GarbageCollector,name=PS Scavenge";
    public static final String GCMS = "java.lang:type=GarbageCollector,name=PS MarkSweep";
    public static final String GCPARNEW = "java.lang:type=GarbageCollector,name=ParNew";
    public static final String HOTSPOTDUMP = "com.sun.management:type=HotSpotDiagnostic";
    public static final String HEAP_ITEM = "PS Survivor Space,PS Eden Space,PS Old Gen";
    public static final String GCCOPY = "java.lang:type=GarbageCollector,name=Copy";
    public static final String OSBEANNAME = "java.lang:type=OperatingSystem";
    public static final String POOLNAME = "java.lang:type=MemoryPool";
    public static final String RUNTIMNAME = "java.lang:type=Runtime";
    public static final String NONHEAP_ITEM = "Code Cache, Perm Gen";
    private static final String CLSBEANAME = "java.lang:type=ClassLoading";
    public static final String THREAD_BEAN_NAME = "java.lang:type=Threading";
    public static final String MEMORYNAME = "java.lang:type=Memory";
    public static final String JMONITOR = "JMonitor";
    private static ConcurrentHashMap<String, JMConnBean> conns = new ConcurrentHashMap<String, JMConnBean>();
    private static final NotificationListener INSTANCE = new JMConnManager();

    public static void addConnInfo(JMConnBean bean) {
        conns.putIfAbsent(bean.getName(), bean);
        JSONObject quit = new JSONObject();
        quit.put("type", "add");
        quit.put("app", bean.getName());
        JMEevntCenter.getInstance().send(quit);

    }

    public static Map<String, JMConnBean> getApps() {
    	init();
        return conns;
    }

    public static void init() {
    	conns.clear();
    	Dao dao = (Dao)SpringApplicationContext.getBean("daoMybatis");
    	List<Map<String, Object>> dataList = dao.query("SYSJMX", "query");
    	for(Map<String, Object> data : dataList){
    		JMConnBean bean = new JMConnBean();
    		bean.setName((String)data.get("name"));
    		bean.setPort((Integer)data.get("port"));
    		bean.setHost((String)data.get("ip"));
    		addConnInfo(bean);
    	}
    }

    public static <T> T getServer(String app, String beanBane, Class<T> cls) throws IOException {
        T ser = ManagementFactory.newPlatformMXBeanProxy(getConn(app), beanBane, cls);
        return ser;
    }

    public static ThreadMXBean getThreadMBean(String app) throws IOException {
        return getServer(app, THREAD_BEAN_NAME, ThreadMXBean.class);
    }

    public static RuntimeMXBean getRuntimeMBean(String app) throws IOException {
        return getServer(app, RUNTIMNAME, RuntimeMXBean.class);
    }

    public static MemoryMXBean getMemoryMBean(String app) throws IOException {
        return getServer(app, MEMORYNAME, MemoryMXBean.class);
    }

    public static OperatingSystemMXBean getOSMbean(String app) throws IOException {
        return getServer(app, OSBEANNAME, OperatingSystemMXBean.class);
    }

    public static ClassLoadingMXBean getClassMbean(String app) throws IOException {
        return getServer(app, CLSBEANAME, ClassLoadingMXBean.class);
    }

    public static MBeanServerConnection getConn(String app) throws IOException {
        if (app.equals(JMONITOR))
            return ManagementFactory.getPlatformMBeanServer();

        JMConnBean bean = conns.get(app);
        if (bean == null)
            throw new RuntimeException(app + ":disconnected");

        if (bean.getConnector() == null) {
            synchronized (JMConnManager.class) {
                if (bean.getConnector() == null) {
                    bean.setConnector(getConnection(bean));
                }
            }
        }
        return bean.getConnector().getMBeanServerConnection();
    }

    public static boolean isLocalHost(String ip) {
        return "127.0.0.1".equals(ip) || "localhost".equals(ip);
    }

    public static void close() {
        Collection<JMConnBean> values = conns.values();
        for (JMConnBean bean : values) {
            try {
                JMXConnector conn = bean.getConnector();
                conn.close();
            } catch (IOException e) {
                log.error("close error:" + e);
            }
        }
    }

    private static JMXConnector getConnection(JMConnBean conn) {
        try {
            Map<String, String[]> map = new HashMap<String, String[]>();
            if (conn.getUser() != null && conn.getPwd() != null) {
                map.put(JMXConnector.CREDENTIALS, new String[] { conn.getUser(), conn.getPwd() });
            }
            String jmxURL = "service:jmx:rmi:///jndi/rmi://" + conn.getHost() + ":" + conn.getPort() + "/jmxrmi";
            JMXConnector connector = JMXConnectorFactory.newJMXConnector(new JMXServiceURL(jmxURL), map);
            connector.addConnectionNotificationListener(INSTANCE, null, conn.getName());
            connector.connect();
            return connector;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static HotSpotDiagnosticMXBean getHotspotBean(String app) throws IOException {
        return getServer(app, HOTSPOTDUMP, HotSpotDiagnosticMXBean.class);
    }


    public void handleNotification(Notification notification, Object handback) {
        JMXConnectionNotification noti = (JMXConnectionNotification) notification;
        if (noti.getType().equals(JMXConnectionNotification.CLOSED)) {
            disconnect(String.valueOf(handback));
        } else if (noti.getType().equals(JMXConnectionNotification.FAILED)) {
            disconnect(String.valueOf(handback));
        } else if (noti.getType().equals(JMXConnectionNotification.NOTIFS_LOST)) {
            disconnect(String.valueOf(handback));
        }
    }

    private static void disconnect(String app) {
        try {
            JMConnBean jmConnBean = conns.remove(app);
            JSONObject quit = new JSONObject();
            quit.put("type", "quit");
            quit.put("app", app);
            JMEevntCenter.getInstance().send(quit);
            JMXConnector conn = jmConnBean.getConnector();
            conn.removeConnectionNotificationListener(INSTANCE);
            conn.close();
        } catch (Exception e) {
            log.error("disconnect error:" + e);
        }
    }
}
