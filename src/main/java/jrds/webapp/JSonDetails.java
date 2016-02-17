package jrds.webapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.GraphNode;
import jrds.Probe;
import jrds.probe.IndexedProbe;

import org.apache.logging.log4j.*;

public class JSonDetails extends JrdsServlet {

	static final private Logger logger = LogManager.getLogger(JSonDetails.class);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			ParamsBean params = getParamsBean(request);
			Probe<?, ?> p = params.getProbe();

			JrdsJSONWriter w = new JrdsJSONWriter(response);
			w.object();
			w.key("probequalifiedname").value(p.getQualifiedName());
			w.key("probeinstancename").value(p.getName());
			w.key("probename").value(p.getPd().getName());
			w.key("hostname").value(p.getHost().getName());
			w.key("pid").value(params.getPid());
			if (p instanceof IndexedProbe) {
				w.key("index").value(((IndexedProbe) p).getIndexName());
			}
			w.key("datastores");
			w.array();
			for (String datasource : p.getPd().getDs()) {
				w.object();
				w.key("id").value(datasource.hashCode());
				w.key("name").value(datasource);
				w.endObject();
			}
			w.endArray();
			w.key("graphs");
			w.array();
			for (GraphNode gn : p.getGraphList()) {
				w.object();
				w.key("id").value(gn.getQualifiedName().hashCode());
				w.key("name").value(gn.getQualifiedName());
				w.endObject();
			}
			w.endArray();
			w.endObject();
			w.flush();
		} catch (Exception e) {
			logger.warn("Failed request: " + request.getRequestURI() + "?"
					+ request.getQueryString() + ": " + e, e);
		}
	}

}
