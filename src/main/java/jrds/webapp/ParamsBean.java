package jrds.webapp;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import javax.servlet.http.HttpServletRequest;

import jrds.Base64;
import jrds.Configuration;
import jrds.Filter;
import jrds.Graph;
import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.GraphTree;
import jrds.HostsList;
import jrds.Period;
import jrds.Probe;
import jrds.Tab;
import jrds.Util;
import jrds.Util.SiPrefix;

import org.apache.logging.log4j.*;
import org.json.JSONException;

/**
 * A bean to parse the request paramaters
 * 
 * @author Fabrice Bacchella
 */
public class ParamsBean implements Serializable {

	static final private Logger logger = LogManager.getLogger(ParamsBean.class);

	static public final String TREECHOICE = "tree";
	static public final String TABCHOICE = "tab";
	static public final String FILTERCHOICE = "filter";
	static public final String HOSTCHOICE = "host";

	private static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm");
		}
	};

	static private final Pattern rangePattern = Pattern
			.compile("(-?\\d+(.\\d+)?)([a-zA-Z]{0,2})");

	String contextPath = "";
	String dsName = null;
	Period period = new Period();
	Integer id = null;
	Integer gid = null;
	Integer pid = null;
	boolean sorted = false;
	boolean history = false;
	String maxArg = null;
	String minArg = null;
	Filter filter = null;
	private HostsList hostlist;
	String user = null;
	Set<String> roles = Collections.emptySet();
	private Map<String, String[]> params = Collections.emptyMap();
	private GraphTree tree = null;
	private Tab tab = null;
	private String choiceType = null;
	private String choiceValue = null;

	public ParamsBean() {

	}

	public ParamsBean(HttpServletRequest req, HostsList hl) {
		contextPath = req.getContextPath();
		params = getReqParamsMap(req);
		readAuthorization(req, hl);
		parseReq(hl);
	}

	public ParamsBean(HttpServletRequest req, HostsList hl, String... restPath) {
		contextPath = req.getContextPath();
		String probeInfo = req.getPathInfo();
		if (probeInfo != null) {
			params = new HashMap<String, String[]>(restPath.length);
			String[] path = probeInfo.trim().split("/");
			logger.trace(jrds.Util.delayedFormatString("mapping %s to %s",
					Arrays.asList(path), Arrays.asList(restPath)));
			int elem = Math.min(path.length - 1, restPath.length);
			for (int i = 0; i < elem; i++) {
				String value = path[i + 1];
				String key = restPath[i].toLowerCase();
				params.put(key, new String[] { value });
			}
			params.putAll(getReqParamsMap(req));
		} else {
			params = getReqParamsMap(req);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("params map:");
			for (String key : params.keySet()) {
				String[] value = params.get(key);
				if (value != null)
					logger.trace(key + ": " + Arrays.asList(value));
			}
		}
		readAuthorization(req, hl);
		parseReq(hl);
	}

	@SuppressWarnings("unchecked")
	// Not a really useful method, just to reduce warning
	private Map<String, String[]> getReqParamsMap(HttpServletRequest req) {
		logger.trace(jrds.Util.delayedFormatString("Parameter map for %s: %s",
				req, req.getParameterMap()));
		return new HashMap<String, String[]>(req.getParameterMap());
	}

	public String getValue(String key) {
		String[] values = params.get(key);
		if (values != null && values.length > 0)
			return values[0];
		return null;
	}

	/**
	 * Set the authentication context, without touching the requests parameters
	 * 
	 * @param req
	 * @param hl
	 */
	public void readAuthorization(HttpServletRequest req, HostsList hl) {
		user = req.getRemoteUser();
		if (user != null) {
			roles = new HashSet<String>();
			for (String role : hl.getRoles()) {
				if (req.isUserInRole(role))
					roles.add(role);
			}
		}
		logger.trace(Util.delayedFormatString("Found user %s with roles %s",
				user, roles));
	}

	private void unpack(String packed) {
		String formatedpack;
		formatedpack = JSonPack.GZIPHEADER
				+ packed.replace('!', '=').replace('$', '/').replace('*', '+');
		logger.trace(formatedpack);
		ByteArrayOutputStream outbuffer = new ByteArrayOutputStream(
				formatedpack.length());
		ByteArrayInputStream inbuffer = new ByteArrayInputStream(
				Base64.decode(formatedpack));
		try {
			byte[] copybuffer = new byte[1500];
			GZIPInputStream os = new GZIPInputStream(inbuffer);
			int realread = os.read(copybuffer);
			while (realread > 0) {
				outbuffer.write(copybuffer, 0, realread);
				realread = os.read(copybuffer);
			}
			JrdsJSONObject json = new JrdsJSONObject(outbuffer.toString());
			for (String key : json) {
				Object value = json.get(key);
				String newkey = JSonPack.JSONKEYS.get(new Integer(key));
				params.put(newkey, new String[] { value.toString() });
				logger.trace(jrds.Util.delayedFormatString("adding %s = %s",
						newkey, value));
			}
		} catch (IOException e) {
			logger.error("IOException " + e, e);
		} catch (JSONException e) {
			logger.error("JSON parsing exception " + e);
		}
		logger.trace(jrds.Util.delayedFormatString("Params unpacked: %s",
				params));
	}

	private void parseReq(HostsList hl) {
		hostlist = hl;

		String packed = getValue("p");
		if (packed != null && !"".equals(packed))
			unpack(packed);

		period = makePeriod();

		String host = getValue("host");
		String probe = getValue("probe");

		gid = jrds.Util.parseStringNumber(getValue("gid"), 0);
		// Many way to discover id (graph node id)
		String idStr = getValue("id");
		String graph = getValue("graphname");
		if (idStr != null && !"".equals(idStr))
			id = Util.parseStringNumber(idStr, 0);
		else if (host != null && !"".equals(host) && graph != null
				&& !"".equals(graph)) {
			id = (host + "/" + graph).hashCode();
		}

		String pidStr = getValue("pid");
		if (pidStr != null)
			pid = jrds.Util.parseStringNumber(pidStr, 0);
		else if (host != null && !"".equals(host) && probe != null
				&& !"".equals(probe))
			pid = (host + "/" + probe).hashCode();

		dsName = getValue("dsName");
		if ("".equals(dsName))
			dsName = null;

		String sortArg = getValue("sort");
		if (sortArg != null && "true".equals(sortArg.toLowerCase()))
			sorted = true;
		String historyArg = getValue("history");
		if ("1".equals(historyArg))
			history = true;

		// max and min should only go together
		// it's up to the gui (aka js code) to manage default value
		String minStr = getValue("min");
		String maxStr = getValue("max");
		if (minStr != null && maxStr != null && !"".equals(minStr)
				&& !"".equals(maxStr)) {
			maxArg = maxStr;
			minArg = minStr;
		}
		String paramFilterName = getValue("filter");
		String paramHostFilter = getValue("host");
		String treeName = getValue("tree");
		String tabName = getValue("tab");
		if (paramFilterName != null && !"".equals(paramFilterName)) {
			filter = hostlist.getFilter(paramFilterName);
			choiceType = FILTERCHOICE;
			choiceValue = paramFilterName;
		} else if (paramHostFilter != null && !"".equals(paramHostFilter)) {
			tree = hl.getGraphTreeByHost().getByPath(GraphTree.HOSTROOT,
					paramHostFilter);
			choiceType = HOSTCHOICE;
			choiceValue = paramHostFilter;
		} else if (treeName != null && !"".equals(treeName)) {
			tree = hl.getGraphTree(treeName);
			choiceType = TREECHOICE;
			choiceValue = treeName;
		} else if (tabName != null && !"".equals(tabName)) {
			tab = hl.getTab(tabName);
			choiceType = TABCHOICE;
			choiceValue = tabName;
		}

		// If previous steps failed
		if (choiceType == null || choiceValue == null) {
			tab = hl.getTab(choiceValue);
			choiceValue = hostlist.getFirstTab();
			choiceType = TABCHOICE;
		}
	}

	private double parseRangeArg(String rangeArg, Graph g) {
		if (rangeArg == null)
			return Double.NaN;
		Matcher m = rangePattern.matcher(rangeArg);
		jrds.GraphNode node = g.getNode();
		if (m.matches() && node != null) {
			String valueString = m.group(1);
			Number value = jrds.Util.parseStringNumber(valueString, Double.NaN);
			String suffixString = m.group(3);
			if (!"".equals(suffixString)) {
				try {
					SiPrefix suffix = SiPrefix.valueOf(suffixString);
					return suffix.evaluate(value.doubleValue(), node
							.getGraphDesc().isSiUnit());
				} catch (java.lang.IllegalArgumentException e) {
					logger.info("Illegal SI suffix " + suffixString);
				}
			} else
				return value.doubleValue();
		}
		return Double.NaN;
	}

	public Filter getFilter() {
		return filter;
	}

	public void configureGraph(jrds.Graph g) {
		g.setPeriod(period);
		double max = parseRangeArg(maxArg, g);
		double min = parseRangeArg(minArg, g);
		if (!Double.isNaN(max))
			g.setMax(max);
		if (!Double.isNaN(min))
			g.setMin(min);
	}

	public jrds.Graph getGraph(JrdsServlet caller) {
		jrds.Graph g = null;
		if (gid != null)
			g = hostlist.getRenderer().getGraph(gid);
		if (g == null) {
			logger.debug("graph cache miss");
			jrds.GraphNode node = getGraphNode(caller);
			if (node != null) {
				g = node.getGraph();
				configureGraph(g);
			}
		}
		return g;
	}

	public GraphNode getGraphNode(JrdsServlet caller) {
		GraphNode gn = null;

		if (id != null)
			gn = hostlist.getGraphById(id);
		if (this.id != null) {
			String host = getValue("host");
			String probe = getValue("probe");
			String graph = getValue("graphname");
			for (GraphNode graphNode : hostlist.getProbeByPath(host, probe)
					.getGraphList()) {
				if (graphNode.getName().startsWith(graph)) {
					return graphNode;
				}
			}
			gn = this.hostlist.getGraphById(this.id.intValue());
		}
		if (gn != null) {
			logger.debug(jrds.Util.delayedFormatString("Graph found: %s", gn));
		} else if (pid != null && pid != 0 && dsName != null) {
			if (!caller.allowed(this, hostlist.getDefaultRoles()))
				return null;
			Probe<?, ?> p = getProbe();
			if (p == null) {
				logger.error("Looking for unknown probe");
				return null;
			}
			logger.debug(jrds.Util.delayedFormatString("Probe found: %s", p));

			Graphics2D g2d = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB)
					.createGraphics();
			String graphDescName = p.getName() + "." + dsName;

			GraphDesc gd = new GraphDesc();
			gd.setName(graphDescName);
			gd.setGraphName(p.getHost().getName() + "." + p.getName() + "."
					+ dsName);
			gd.setGraphTitle(p.getName() + "." + dsName + " on ${host}");
			gd.add(dsName, GraphDesc.LINE);
			gd.initializeLimits(g2d);

			gn = new GraphNode(p, gd);
			gn.addACL(Configuration.get().getPropertiesManager().defaultACL);
		}
		return gn;
	}

	public List<GraphNode> getGraphs(JrdsServlet caller) {
		// Neither id or pid where specified, nothing can be done
		if (id == null && pid == null)
			return Collections.emptyList();

		if (id != null) {
			GraphTree node = hostlist.getNodeById(id);
			if (node != null) {
				logger.debug(jrds.Util.delayedFormatString("Tree found: %s",
						node));
				Filter filter = getFilter();
				return node.enumerateChildsGraph(filter);
			}
		}
		GraphNode gn = getGraphNode(caller);
		if (gn != null) {
			logger.debug(jrds.Util.delayedFormatString("Graph found: %s", gn));
			return Collections.singletonList(gn);
		}
		return Collections.emptyList();
	}

	public Probe<?, ?> getProbe() {
		Probe<?, ?> p = hostlist.getProbeById(pid);
		if (p == null) {
			jrds.GraphNode node = hostlist.getGraphById(pid);
			if (node != null)
				p = node.getProbe();
		}
		return p;
	}

	private void addPeriodArgs(Map<String, Object> args, boolean timeAbsolute) {
		if (!timeAbsolute && period.getScale() != 0) {
			args.put("scale", period.getScale());
		} else {
			args.put("begin", period.getBegin().getTime());
			args.put("end", period.getEnd().getTime());
		}
	}

	private void addMinMaxArgs(Map<String, Object> args) {
		if (maxArg != null)
			args.put("max", maxArg);
		if (minArg != null)
			args.put("min", minArg);
	}

	private void addFilterArgs(Map<String, Object> args) {
		if (filter instanceof jrds.FilterHost) {
			args.put("host", filter.getName());
		} else if (filter instanceof jrds.Filter) {
			args.put("filter", filter.getName());

		}
	}

	/**
	 * Construct a args list for a url's cgi arguments
	 * 
	 * @param o
	 *            The object to build the arguments for
	 * @param timeAbsolute
	 *            should the time be display as an absolute range or a relative
	 *            period
	 * @return
	 */
	public Map<String, Object> doArgsMap(Object o, boolean timeAbsolute) {
		Map<String, Object> args = new HashMap<String, Object>();
		addPeriodArgs(args, timeAbsolute);
		addMinMaxArgs(args);
		if (o instanceof jrds.FilterHost) {
			args.put("host", ((jrds.Filter) o).getName());
		} else if (o instanceof jrds.Filter) {
			args.put("filter", ((jrds.Filter) o).getName());
		} else if (o instanceof jrds.Graph) {
			// First check if it's a referenced graph
			Graph g = (jrds.Graph) o;
			args.put("gid", g.hashCode());
			if (hostlist.getGraphById(g.getNode().hashCode()) != null) {
				args.put("id", g.getNode().hashCode());
			}
			// Else let's try to keep the args that can be used
			else {
				String[] graphargs = { "pid", "dsName" };
				for (String arg : graphargs) {
					if (params.containsKey(arg)) {
						args.put(arg, params.get(arg)[0]);
					}
				}
			}
		} else if (o instanceof jrds.Probe<?, ?>) {
			args.put("pid", o.hashCode());
		} else {
			addFilterArgs(args);
			args.put("id", o.hashCode());
		}
		return args;
	}

	public String makeObjectUrl(String file, Object o, boolean timeAbsolute) {
		Map<String, Object> args = doArgsMap(o, timeAbsolute);
		logger.trace(jrds.Util.delayedFormatString("Params string:%s ", args));
		// We build the Url
		StringBuilder urlBuffer = new StringBuilder();
		urlBuffer.append(contextPath);

		if (!contextPath.endsWith("/")) {
			urlBuffer.append("/");
		}
		urlBuffer.append(file + "?");

		for (Map.Entry<String, Object> e : args.entrySet()) {
			try {
				urlBuffer.append(e.getKey() + "="
						+ URLEncoder.encode(e.getValue().toString(), "UTF-8")
						+ "&");
			} catch (UnsupportedEncodingException e1) {
			}
		}
		urlBuffer.deleteCharAt(urlBuffer.length() - 1);
		return urlBuffer.toString();
	}

	@Override
	public String toString() {
		StringBuilder parambuff = new StringBuilder();

		Map<String, Object> args = new HashMap<String, Object>();
		addFilterArgs(args);
		addPeriodArgs(args, true);
		addMinMaxArgs(args);

		parambuff.append('&');
		if (id != 0)
			parambuff.append("id=" + id + '&');
		if (gid != 0)
			parambuff.append("gid=" + gid + '&');
		for (Map.Entry<String, Object> param : args.entrySet()) {
			String key = param.getKey();
			Object value = param.getValue();
			if (value != null && !"".equals(value)) {
				parambuff.append(key);
				parambuff.append("=");
				parambuff.append(value);
				parambuff.append('&');
			} else if (value == null) {
				parambuff.append(key);
				parambuff.append('&');
			}
		}

		// Remove the extra &
		parambuff.deleteCharAt(parambuff.length() - 1);

		return parambuff.toString();
	}

	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	private Period makePeriod() {
		Period p = null;
		try {
			String scaleStr = getValue("scale");
			// Changed name for this attribute
			if (scaleStr == null)
				scaleStr = getValue("autoperiod");

			int scale = jrds.Util.parseStringNumber(scaleStr, -1);

			String end = getValue("end");
			String begin = getValue("begin");
			if (scale > 0)
				p = new Period(scale);
			else if (end != null && begin != null)
				p = new Period(begin, end);
			else
				p = new Period();
			if (params.containsKey("periodnext"))
				p = p.next();
			else if (params.containsKey("periodprevious"))
				p = p.previous();
		} catch (NumberFormatException e) {
			logger.error("Period cannot be parsed");
		} catch (ParseException e) {
			logger.error("Period cannot be parsed");
		}
		return p;
	}

	/**
	 * @return the sort
	 */
	public boolean isSorted() {
		return sorted;
	}

	/**
	 * @return the p
	 */
	public Period getPeriod() {
		if (period.getScale() != 0)
			period = new Period(period.getScale());
		return period;
	}

	/**
	 * @return Returns the begin.
	 */
	public String getStringBegin() {
		String formatted = "";
		if (period.getScale() == 0)
			formatted = df.get().format(period.getBegin());
		return formatted;
	}

	/**
	 * @return Returns the end.
	 */
	public String getStringEnd() {
		String formatted = "";
		if (period.getScale() == 0)
			formatted = df.get().format(period.getEnd());
		return formatted;
	}

	/**
	 * @return Returns the begin.
	 */
	public long getBegin() {
		if (period != null)
			return period.getBegin().getTime();
		return Long.MIN_VALUE;
	}

	/**
	 * @return Returns the end.
	 */
	public long getEnd() {
		if (period != null)
			return period.getEnd().getTime();
		return Long.MIN_VALUE;
	}

	/**
	 * @return Returns the scale.
	 */
	public int getScale() {
		return period.getScale();
	}

	public void setScale(int s) {
		period = new Period(s);
	}

	public List<String> getPeriodNames() {
		logger.trace("Knonw period names: " + Period.getPeriodNames());
		return Period.getPeriodNames();
	}

	public String getPeriodUrl() {
		StringBuilder parambuff = new StringBuilder();
		if (period != null)
			parambuff.append("begin=" + period.getBegin().getTime() + "&end="
					+ period.getEnd().getTime());
		return parambuff.toString();
	}

	public String getMaxStr() {
		return maxArg;
	}

	public String getMinStr() {
		return minArg;
	}

	/**
	 * @return the history
	 */
	public boolean isHistory() {
		return history;
	}

	public Integer getPid() {
		return pid;
	}

	public String getDsName() {
		return dsName;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the roles
	 */
	public Set<String> getRoles() {
		return roles;
	}

	/**
	 * @return the tree
	 */
	public GraphTree getTree() {
		return tree;
	}

	/**
	 * @return the choiceType
	 */
	public String getChoiceType() {
		return choiceType;
	}

	/**
	 * @return the choiceValue
	 */
	public String getChoiceValue() {
		return choiceValue;
	}

	/**
	 * @return the tab
	 */
	public Tab getTab() {
		return tab;
	}

}
