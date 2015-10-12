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

import jrds.HostsList;
import jrds.Probe;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;

/**
 * A servlet wich return datastore values from a probe.
 * It can be used in many way :
 * The simplest way is by using a URL of the form :
 * http://<it>server</it>/values/<it>host</it>/<it>probe.</it>
 * It will return all datastores values for this probe. By adding a /<it>datastore</i>, one can choose only 
 * one data store.<p>
 * It's possible to refine the query with some arguments, using REST syntax.<p>
 * The argument can be:
 * <ul>
 * <li>dsName: the datastore name</li>
 * <li>period: the time interval in seconds, default to the step value.</li>
 * <li>cf: the consolidated function used.</li>
 * </ul>
 * If there is only one value generated, it's displayed as is. Else the name is also shown as well as the last update value
 * in the form <code>datastore: value</code>
 * @author Fabrice Bacchella
 * @version $Revision: 236 $
 */
public final class CheckValues extends JrdsServlet {

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HostsList hl = getHostsList();

        ParamsBean params = new ParamsBean(req, hl, "host", "probe", "dsname", "period", "cf");

        int period = jrds.Util.parseStringNumber(params.getValue("period"), hl.getStep()).intValue();
        String cfName = params.getValue("cf");
        if(cfName == null || "".equals(cfName.trim()))
            cfName = "AVERAGE";
        ConsolFun cf = ConsolFun.valueOf(cfName.trim().toUpperCase());
        Probe<?,?> p = params.getProbe();

        if(p != null) {
            res.setContentType("text/plain");
            res.addHeader("Cache-Control", "no-cache");
            ServletOutputStream out = res.getOutputStream();

            Date lastupdate = p.getLastUpdate();
            long age = (new Date().getTime() - lastupdate.getTime()) / 1000;
            //It the last update is too old, it fails
            if( age > p.getStep() * 2 ) {
                out.println("Probe too old: " +  age);
                return;
            }
            Date paste = new Date(lastupdate.getTime() - period * 1000);
            FetchData fd = p.fetchData(paste, lastupdate);

            String ds = params.getValue("dsname");
            if(ds != null && !  "".equals(ds.trim())) {
                out.print(fd.getAggregate(ds.trim(), cf));
            }
            else {
                for(String dsName: fd.getDsNames()) {
                    double val = fd.getAggregate(dsName, cf);
                    out.println(dsName + ": " + val);
                }
                out.println("Last update: " + p.getLastUpdate());
                out.println("Last update age (ms): " + (new Date().getTime() - p.getLastUpdate().getTime()));
            }
        }
        else {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, "No matching probe");
        }
    }

}
