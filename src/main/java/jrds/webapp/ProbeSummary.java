/*##########################################################################
 _##
 _##  $Id: Graph.java 236 2006-03-02 15:59:34 +0100 (jeu., 02 mars 2006) fbacchella $
 _##
 _##########################################################################*/

package jrds.webapp;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.Probe;

import org.apache.logging.log4j.*;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;

/**
 * A servlet wich show the last update values and time
 * @author Fabrice Bacchella
 * @version $Revision: 236 $
 */
public final class ProbeSummary extends JrdsServlet {
	static final private Logger logger = LogManager.getLogger(ProbeSummary.class);
	
	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
	throws ServletException, IOException {
		res.setContentType("text/plain");
		res.addHeader("Cache-Control", "no-cache");
		ServletOutputStream out = res.getOutputStream();

		ParamsBean params = getParamsBean(req);

		Probe<?,?> probe = params.getProbe();
		if(probe != null) {
			Date begin = params.getPeriod().getBegin();
			Date end = params.getPeriod().getEnd();
			FetchData fetched = probe.fetchData(begin, end);
			String names[] = fetched.getDsNames();
			for(int i= 0; i< names.length ; i++) {
				String dsName = names[i];
				try {
					out.print(dsName + " ");
					out.print(fetched.getAggregate(dsName, ConsolFun.AVERAGE) + " ");
					out.print(fetched.getAggregate(dsName, ConsolFun.MIN) + " ");
					out.println(fetched.getAggregate(dsName, ConsolFun.MAX));
				} catch (IOException e) {
					logger.error("Probe file " + probe.getRrdName() + "unusable: " + e);
				}
			}
		}
		else {
			logger.error("Probe id provided " + params.getId() + " invalid");
		}
	}
	
}
