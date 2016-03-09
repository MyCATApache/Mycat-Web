/*##########################################################################
 _##
 _##  $Id$
 _##
 _##########################################################################*/

package jrds.webapp;

import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.GraphNode;
import jrds.HostInfo;
import jrds.HostsList;
import jrds.Probe;

import org.apache.logging.log4j.*;
import org.hx.rainbow.common.util.JsonUtil;

/**
 * A servlet wich generate a png for a graph
 * 
 * @author Fabrice Bacchella
 * @version $Revision$
 */
public final class Graph extends JrdsServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2438020478080172748L;
	
	static final private Logger logger = LogManager.getLogger(Graph.class);

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		try {
			String pathInf = req.getPathInfo();
			if (pathInf.startsWith("/showMycatGraphs")) {
				showMycatGraphs(req, res);
				return;
			}
			if (pathInf.startsWith("/hostMycatList")) {
				hostMycatList(req, res);
				return;
			}
			Date start = new Date();
			HostsList hl = getHostsList();

			ParamsBean p = new ParamsBean(req, hl, "host", "graphname");

			// Let have a little cache control
			boolean cache = true;
			String cachecontrol = req.getHeader("Cache-Control");
			if (cachecontrol != null
					&& "no-cache".equals(cachecontrol.toLowerCase().trim()))
				cache = false;

			jrds.Graph graph = p.getGraph(this);

			if (graph == null) {
				res.sendError(HttpServletResponse.SC_NOT_FOUND,
						"Invalid graph id");
				return;
			}

			// If the requested end is in the future, the graph should not be
			// cached.
			if (graph.getEnd().after(new Date()))
				cache = false;

			if (getPropertiesManager().security) {
				boolean allowed = graph.getACL().check(p);
				logger.trace(jrds.Util.delayedFormatString(
						"Looking if ACL %s allow access to %s", graph.getACL(),
						this));
				if (!allowed) {
					res.sendError(HttpServletResponse.SC_FORBIDDEN,
							"Invalid role access");
					return;
				}
			}

			Date middle = new Date();
			if (!hl.getRenderer().isReady(graph)) {
				logger.warn("One graph not ready, synchronous rendering");
			}
			res.setContentType("image/png");
			// No caching, the date might be in the future, a period is
			// requested
			// So the image have short lifetime, just one step
			if (p.period.getScale() != 0 || !cache) {
				res.addDateHeader("Expires", new Date().getTime()
						+ getPropertiesManager().step * 1000);
			}
			res.addDateHeader("Last-Modified", graph.getEnd().getTime());
			res.addHeader("content-disposition",
					"inline; filename=" + graph.getPngName());
			res.addHeader(
					"ETag",
					jrds.Base64.encodeString(getServletName()
							+ graph.hashCode()));
			ServletOutputStream out = res.getOutputStream();
			FileChannel indata = hl.getRenderer().sendInfo(graph);
			// If a cache file exist, try to be smart, but only if caching is
			// allowed
			if (indata != null && cache) {
				logger.debug(jrds.Util.delayedFormatString(
						"graph %s is cached", graph));
				if (indata.size() < Integer.MAX_VALUE)
					res.setContentLength((int) indata.size());
				WritableByteChannel outC = Channels.newChannel(out);
				indata.transferTo(0, indata.size(), outC);
				indata.close();
			} else {
				logger.debug(jrds.Util.delayedFormatString(
						"graph %s not found in cache", graph));
				graph.writePng(out);
			}

			if (logger.isTraceEnabled()) {
				Date finish = new Date();
				long duration1 = middle.getTime() - start.getTime();
				long duration2 = finish.getTime() - middle.getTime();
				logger.trace("Graph " + graph + " rendering, started at "
						+ start + ", ran for " + duration1 + ":" + duration2
						+ "ms");
			}
		} catch (RuntimeException e) {
			if (logger.isDebugEnabled())
				logger.error(e, e);
			else
				logger.error(e);

			res.sendError(HttpServletResponse.SC_BAD_REQUEST,
					"Invalid graph request");
		}
	}

	private void hostMycatList(HttpServletRequest req, HttpServletResponse res) {
		HostsList hl = this.getHostsList();
		String hostprefix = req.getParameter("hostprefix");
		List<String> hostList = new ArrayList<String>();
		
		for (HostInfo hostInf : hl.getHosts()) {
			String hostName = hostInf.getName();
			if(hostName.startsWith(hostprefix)){
				hostList.add(hostInf.getName());
			}
		}
		
		try {
			Writer out = res.getWriter();
			out.write(JsonUtil.getInstance().object2JSON(hostList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void showMycatGraphs(HttpServletRequest req, HttpServletResponse res) {
		HostsList hl = this.getHostsList();
		String hostName = req.getParameter("hostName");
		List<Map<String, Object>> hostList = new ArrayList<Map<String, Object>>();
		if(hostName == null || hostName.isEmpty()){
			return;
		}
		for (HostInfo hostInf : hl.getHosts()) {
			Map<String,Object> hostMap = new LinkedHashMap<String, Object>();
			String hostInfName = hostInf.getName();
			if(!hostName.equals(hostInfName)){
				continue;
			}
			hostMap.put("host", hostInf.getName());
			Iterator<Probe<?, ?>> itors = hostInf.getProbes().iterator();
			
			List<Map<String, Object>> probeList = new ArrayList<Map<String, Object>>();
			while (itors.hasNext()) {
				Probe curProbe = itors.next();
				String probeName = curProbe.getName();
				Map<String,Object> probeMap = new LinkedHashMap<String, Object>();
				probeMap.put("name", probeName);
				
				Collection<GraphNode> allGraphs = curProbe.getGraphList();
				List<Map<String, Object>> nameList = new ArrayList<Map<String, Object>>();
				for (GraphNode gn : allGraphs) {
					Map<String,Object> nameMap = new LinkedHashMap<String, Object>();
					String gNname = gn.getGraphDesc().getName();
					nameMap.put("name", gNname);
					String url = req.getContextPath()
							+ "/graph/" + hostInf.getName();
					nameMap.put("url",  url + "/" + gn.getName() + "?probe=" + probeName);
					nameList.add(nameMap);
				}
				probeMap.put("graph", nameList);
				probeList.add(probeMap);
			}
			hostMap.put("probe", probeList);
			hostList.add(hostMap);
			break;
		}
		
		try {
			Writer out = res.getWriter();
			out.write(JsonUtil.getInstance().object2JSON(hostList));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
