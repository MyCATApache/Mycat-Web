/*##########################################################################
 _##
 _##  $Id: Graph.java 236 2006-03-02 15:59:34 +0100 (jeu., 02 mars 2006) fbacchella $
 _##
 _##########################################################################*/

package jrds.webapp;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.HostInfo;
import jrds.HostsList;
import jrds.Probe;

import org.json.JSONException;

/**
 * A servlet wich show the last update values and time
 * 
 * @author Fabrice Bacchella
 * @version $Revision: 236 $
 */
public final class Last extends JrdsServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6412347162519720359L;
	static public String name = null;

	// {
	// ["host":"host1","key1":value1,"key2":value2],["host":"host2","key1":value1,"key2":value2]
	// }
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		res.setContentType("text/plain");
		res.addHeader("Cache-Control", "no-cache");
		HostsList hl = getHostsList();
		try {
			JrdsJSONWriter w = new JrdsJSONWriter(res);
			w.array();

			String host = req.getParameter("host");
			if (host != null) {
				for (HostInfo hostInfo : hl.getHosts()) {
					if (hostInfo.getName().equalsIgnoreCase(host)) {
						outputHostProbesColls(w, hostInfo);
						break;
					}
				}
			} else {// all hosts
				for (HostInfo hostInfo : hl.getHosts()) {
					outputHostProbesColls(w, hostInfo);

				}

			}

			w.endArray();
			w.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void outputHostProbesColls(JrdsJSONWriter w, HostInfo hostInfo)
			throws JSONException, IOException {
		w.object();
		w.key("host").value(hostInfo.getName());
		long lastTime = 0;
		for (Probe<?, ?> probe : hostInfo.getProbes()) {
			for (Map.Entry<String, Object> e : probe.latestCollectionValues
					.entrySet()) {
				if (e.getValue().equals(Double.POSITIVE_INFINITY)
						|| e.getValue().equals(Double.NaN)
						|| e.getValue().equals(Double.NEGATIVE_INFINITY)) {
					w.key(e.getKey()).value(-9999);
				} else {
					if (e.getKey().equals("JRDS_LAST_COLLECT")) {
						long theLastTime = ((Number) e.getValue()).longValue();
						if (lastTime < theLastTime) {
							lastTime = theLastTime;
						}

					} else {
						w.key(e.getKey()).value(e.getValue());
					}
				}

			}
		}
		w.key("JRDS_LAST_COLLECT").value(lastTime);
		w.key("JRDS_LCOLLECT_SECONDS").value(
				System.currentTimeMillis() / 1000 - lastTime);
		w.endObject();
		w.newLine();
	}

}
