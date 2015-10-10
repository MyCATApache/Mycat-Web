package org.mycat.web.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mycat.web.jmonitor.JMConnBean;
import org.mycat.web.service.JMConnManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.management.OperatingSystemMXBean;

/***
 * 
 * @author coder_czp@126.com
 *
 */
@SuppressWarnings("restriction")
@Controller
@RequestMapping("/jm")
public class JMServer {

	@RequestMapping("/deadlockCheck")
	@ResponseBody
    public JSONObject doDeadlockCheck(HttpServletRequest request) {
        try {
            String app = request.getParameter("app");
            ThreadMXBean tBean = JMConnManager.getThreadMBean(app);
            JSONObject json = new JSONObject();
            long[] dTh = tBean.findDeadlockedThreads();
            if (dTh != null) {
                ThreadInfo[] threadInfo = tBean.getThreadInfo(dTh, Integer.MAX_VALUE);
                StringBuffer sb = new StringBuffer();
                for (ThreadInfo info : threadInfo) {
                    sb.append("\n").append(info);
                }
                json.put("hasdeadlock", true);
                json.put("info", sb);
                return json;
            }
            json.put("hasdeadlock", false);
            return json;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/loadThreadInfo")
	@ResponseBody
    public JSONObject doLoadThreadInfo(HttpServletRequest request) {
        try {
        	String app = request.getParameter("app");
            ThreadMXBean tBean = JMConnManager.getThreadMBean(app);
            ThreadInfo[] allThreads = tBean.dumpAllThreads(false, false);

            JSONObject root = new JSONObject();
            JSONArray detail = new JSONArray();
            HashMap<State, Integer> state = new HashMap<Thread.State, Integer>();
            for (ThreadInfo info : allThreads) {
                JSONObject th = new JSONObject();
                long threadId = info.getThreadId();
                long cpu = tBean.getThreadCpuTime(threadId);
                State tState = info.getThreadState();

                th.put("id", threadId);
                th.put("state", tState);
                th.put("name", info.getThreadName());
                th.put("cpu", TimeUnit.NANOSECONDS.toMillis(cpu));
                detail.add(th);

                Integer vl = state.get(tState);
                if (vl == null) {
                    state.put(tState, 0);
                } else {
                    state.put(tState, vl + 1);
                }
            }

            root.put("state", state);
            root.put("detail", detail);
            root.put("total", tBean.getThreadCount());
            root.put("time", System.currentTimeMillis());
            root.put("deamon", tBean.getDaemonThreadCount());

            return root;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/dumpThead")
	@ResponseBody
    public void doDumpThread(HttpServletRequest request, HttpServletResponse response) {
        try {
            String app = request.getParameter("app");
            String threadId = request.getParameter("threadId");
            ThreadMXBean tBean = JMConnManager.getThreadMBean(app);
            JSONObject data = new JSONObject();
            if (threadId != null) {
                Long id = Long.valueOf(threadId);
                ThreadInfo threadInfo = tBean.getThreadInfo(id, Integer.MAX_VALUE);
                data.put("info", threadInfo.toString());
            } else {
                ThreadInfo[] dumpAllThreads = tBean.dumpAllThreads(false, false);
                StringBuffer info = new StringBuffer();
                for (ThreadInfo threadInfo : dumpAllThreads) {
                    info.append("\n").append(threadInfo);
                }
                data.put("info", info);
            }
            writeFile(request, response, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/loadRuntimeInfo")
	@ResponseBody
    public JSONObject doLoadRuntimeInfo(HttpServletRequest request) {
        try {
            String app = request.getParameter("app");
            RuntimeMXBean mBean = JMConnManager.getRuntimeMBean(app);
            ClassLoadingMXBean cBean = JMConnManager.getClassMbean(app);
            Map<String, String> props = mBean.getSystemProperties();
            DateFormat format = DateFormat.getInstance();
            List<String> input = mBean.getInputArguments();
            Date date = new Date(mBean.getStartTime());

            TreeMap<String, Object> data = new TreeMap<String, Object>();

            data.put("apppid", mBean.getName());
            data.put("startparam", input.toString());
            data.put("starttime", format.format(date));
            data.put("classLoadedNow", cBean.getLoadedClassCount());
            data.put("classUnloadedAll", cBean.getUnloadedClassCount());
            data.put("classLoadedAll", cBean.getTotalLoadedClassCount());
            data.putAll(props);

            JSONObject json = new JSONObject(true);
            json.putAll(data);
            return json;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/doGC")
	@ResponseBody
    public String doVMGC(HttpServletRequest request) {
        try {
            String app = request.getParameter("app");
            JMConnManager.getMemoryMBean(app).gc();
            return "success";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/doHeapDump")
	@ResponseBody
    public JSONObject doHeapDump(HttpServletRequest request) {
        try {
            String app = request.getParameter("app");
            JMConnBean bean = JMConnManager.getApps().get(app);
            String host = bean.getHost();
            boolean islocal = JMConnManager.isLocalHost(host);
            DateFormat fmt = DateFormat.getDateTimeInstance();
            String date = fmt.format(new Date()).replaceAll("\\D", "_");
            if (islocal) {
                return doLocalDump(request, app, date);
            }
            return doRemoteDump(app, date, host);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject doRemoteDump(String app, String date, String host) throws IOException {
        RuntimeMXBean mBean = JMConnManager.getRuntimeMBean(app);
        String dir = mBean.getSystemProperties().get("user.dir");
        String dumpFile = String.format("%s/%s_%s_heap.hprof", dir, app, date);
        JMConnManager.getHotspotBean(app).dumpHeap(dumpFile, false);

        JSONObject res = new JSONObject();
        res.put("file", host + ":" + dumpFile);
        res.put("local", false);
        return res;
    }

    @SuppressWarnings("deprecation")
	@ResponseBody
    private JSONObject doLocalDump(HttpServletRequest req, String app, String date) throws IOException {
        File root = new File(req.getRealPath(req.getRequestURI()));
        String dir = root.getParentFile().getParent();
        File file = new File(String.format("%s/dump/%s_%s_heap.hprof", dir, app, date));
        file.getParentFile().mkdirs();
        String dumpFile = file.getAbsolutePath();
        JMConnManager.getHotspotBean(app).dumpHeap(dumpFile, false);

        JSONObject res = new JSONObject();
        res.put("local", true);
        res.put("file", String.format("./dump/%s", file.getName()));

        return res;
    }

    @RequestMapping("/loadMonitorData")
	@ResponseBody
    public JSONObject doLoadMonitorData(HttpServletRequest request) {
        try {
            String app = request.getParameter("app");
            long now = System.currentTimeMillis();
            JSONObject data = new JSONObject();

            JSONObject gc = geGCInfo(app);
            JSONObject cpu = findCpuInfo(app);
            JSONObject memory = loadMemoryInfo(app);

            data.put("gc", gc);
            data.put("cpu", cpu);
            data.put("time", now);
            data.put("memory", memory);

            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @RequestMapping("/loadCluster")
	@ResponseBody
    public String doRequestLoadCluster(HttpServletRequest request) {
        Map<String, JMConnBean> apps = JMConnManager.getApps();
        JSONArray tree = new JSONArray();
        for (JMConnBean bean : apps.values()) {
            JSONObject node = new JSONObject();
            node.put("host", bean.getHost());
            node.put("port", bean.getPort());
            node.put("text", bean.getName());
            node.put("cluster", "test");
            tree.add(node);
        }
        return tree.toJSONString();

    }


    public static JSONObject loadMemoryInfo(String app) {
        try {
            MemoryMXBean mBean = JMConnManager.getMemoryMBean(app);
            MemoryUsage nonHeap = mBean.getNonHeapMemoryUsage();
            MemoryUsage heap = mBean.getHeapMemoryUsage();

            JSONObject map = new JSONObject(true);
            buildMemoryJSon(heap, "heap", map);
            buildMemoryJSon(nonHeap, "nonheap", map);

            JSONObject heapChild = new JSONObject();
            JSONObject nonheapChild = new JSONObject();

            JSONObject heapUsed = new JSONObject();
            JSONObject heapMax = new JSONObject();
            heapUsed.put("used", heap.getUsed());
            heapMax.put("used", heap.getCommitted());
            heapChild.put("HeapUsed", heapUsed);
            heapChild.put("HeapCommit", heapMax);

            JSONObject nonheapUsed = new JSONObject();
            JSONObject noheapMax = new JSONObject();
            nonheapUsed.put("used", nonHeap.getUsed());
            noheapMax.put("used", nonHeap.getCommitted());

            nonheapChild.put("NonheapUsed", nonheapUsed);
            nonheapChild.put("NonheapCommit", noheapMax);

            ObjectName obj = new ObjectName("java.lang:type=MemoryPool,*");
            MBeanServerConnection conn = JMConnManager.getConn(app);
            Set<ObjectInstance> MBeanset = conn.queryMBeans(obj, null);
            for (ObjectInstance objx : MBeanset) {
                String name = objx.getObjectName().getCanonicalName();
                String keyName = objx.getObjectName().getKeyProperty("name");
                MemoryPoolMXBean bean = JMConnManager.getServer(app, name, MemoryPoolMXBean.class);
                JSONObject item = toJson(bean.getUsage());
                if (JMConnManager.HEAP_ITEM.contains(keyName)) {
                    heapChild.put(keyName, item);
                } else {
                    nonheapChild.put(keyName, item);
                }
            }
            map.getJSONObject("heap").put("childs", heapChild);
            map.getJSONObject("nonheap").put("childs", nonheapChild);

            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject findCpuInfo(String app) {
        try {
            JSONObject map = new JSONObject(true);
            OperatingSystemMXBean os = JMConnManager.getOSMbean(app);
            map.put("os", (long) (os.getSystemCpuLoad() * 100));
            map.put("vm", (long) (os.getProcessCpuLoad() * 100));
            map.put("cores", (long) (os.getAvailableProcessors()));
            map.put("freememory", os.getFreePhysicalMemorySize());
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject geGCInfo(String app) throws Exception {
        ObjectName obj = new ObjectName("java.lang:type=GarbageCollector,*");
        MBeanServer conn = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectInstance> MBeanset = conn.queryMBeans(obj, null);
        Class<GarbageCollectorMXBean> cls = GarbageCollectorMXBean.class;
        JSONObject data = new JSONObject();
        for (ObjectInstance objx : MBeanset) {
            String name = objx.getObjectName().getCanonicalName();
            String keyName = objx.getObjectName().getKeyProperty("name");
            GarbageCollectorMXBean gc = ManagementFactory.newPlatformMXBeanProxy(conn, name, cls);
            data.put(keyName + "-time", gc.getCollectionTime() / 1000.0);
            data.put(keyName + "-count", gc.getCollectionCount());
        }
        return data;
    }

    private static void buildMemoryJSon(MemoryUsage useage, String name, JSONObject map) {
        JSONObject item = toJson(useage);
        map.put(name, item);
    }

    private static JSONObject toJson(MemoryUsage useage) {
        JSONObject item = new JSONObject();
        item.put("commit", useage.getCommitted());
        item.put("used", useage.getUsed());
        item.put("init", useage.getInit());
        item.put("max", useage.getMax());
        return item;
    }

    private void writeFile(HttpServletRequest req, HttpServletResponse resp, JSONObject rtInfo) {
        try {
            String javaApp = req.getParameter("app");
            DateFormat fmt = DateFormat.getDateTimeInstance();
            String dateStr = fmt.format(new Date()).replaceAll("\\D", "_");
            String fileName = String.format("%s-%s.threaddump", javaApp, dateStr);
            resp.setHeader("content-disposition", "attachment; filename=" + fileName);
            PrintWriter out = resp.getWriter();
            out.print(rtInfo.get("info"));
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
