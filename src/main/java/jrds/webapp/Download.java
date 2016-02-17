package jrds.webapp;

//----------------------------------------------------------------------------
//$Id$

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.Period;
import jrds.Probe;

import org.apache.logging.log4j.*;
import org.rrd4j.core.FetchData;
import org.rrd4j.data.DataProcessor;

/**
 * This servlet is used to download the values of a graph as an xml file
 *
 * @author Fabrice Bacchella
 * @version $Revision$
 */

public class Download extends JrdsServlet {
    static final private Logger logger = LogManager.getLogger(Download.class);
    private static final String CONTENT_TYPE = "text/csv";
    private static final SimpleDateFormat humanDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected static final ThreadLocal<DateFormat> epochFormat = 
            new ThreadLocal<DateFormat> () {
        @Override
        protected DateFormat initialValue() {
            return new DateFormat() {
                @Override
                public StringBuffer format(Date date, StringBuffer toAppendTo,
                        FieldPosition arg2) {
                    return toAppendTo.append(date.getTime() / 1000);
                }
                @Override
                public Date parse(String source, ParsePosition pos) {
                    pos.setIndex(source.length());
                    return new Date(Long.parseLong(source) * 1000);
                }
            };
        };
    };

    //    protected static final DateFormat epochFormat = new DateFormat() {
    //        @Override
    //        public StringBuffer format(Date date, StringBuffer toAppendTo,
    //                FieldPosition arg2) {
    //            return toAppendTo.append(date.getTime() / 1000);
    //        }
    //        @Override
    //        public Date parse(String source, ParsePosition pos) {
    //            pos.setIndex(source.length());
    //            return new Date(Long.parseLong(source) * 1000);
    //        }
    //        
    //    };

    public void doGet(HttpServletRequest req, HttpServletResponse res) {

        ParamsBean params;
        String cmd = "graph";

        String pi = req.getPathInfo();
        if(pi != null && pi.length() > 2) {
            String cmds[] = pi.split("/");
            if(cmds.length == 4) {
                cmd = cmds[1];
                if("probe".equals(cmd)) {
                    params = getParamsBean(req, "cmd", "host", "probe");	                
                }
                else if("graph".equals(cmd)) {
                    params = getParamsBean(req, "cmd", "host", "graphname");                   
                }
                else {
                    res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
            else {
                params = getParamsBean(req);
            }
        } else {
            params = getParamsBean(req);
        }


        DataProcessor sourceDp = null;
        String fileName = null;
        jrds.Graph graph;
        if("graph".equals(cmd)) {
            graph = params.getGraph(this);
            if(graph == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;   
            }
            if(getPropertiesManager().security) {               
                boolean allowed = graph.getACL().check(params);
                logger.trace(jrds.Util.delayedFormatString("Looking if ACL %s allow access to %s", graph.getACL(), this));
                if(! allowed) {
                    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            }

            try {
                sourceDp = graph.getDataProcessor();
                fileName = graph.getPngName().replaceFirst("\\.png",".csv");
            } catch (IOException e) {
                logger.error("Unable to process graph data");
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        else {
            Probe<?, ?> probe = params.getProbe();
            if(probe == null) {
                res.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;   
            }
            Period p = params.getPeriod();
            FetchData fd = probe.fetchData(p.getBegin(), p.getEnd());
            sourceDp = new DataProcessor(p.getBegin(), p.getEnd());
            for(String dsName: fd.getDsNames()) {
                sourceDp.addDatasource(dsName, fd);
            }
            try {
                sourceDp.processData();
                fileName = probe.getRrdName().replaceFirst("\\.rrd",".csv");
            } catch (IOException e) {
                logger.error("Unable to process probe data");
                res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        try {
            ServletOutputStream out = res.getOutputStream();
            res.setContentType(CONTENT_TYPE);
            res.addHeader("content-disposition","attachment; filename=" + fileName);
            DateFormat exportDateFormat = humanDateFormat;
            if(params.getValue("epoch") != null) {
                exportDateFormat = epochFormat.get();
            }
            writeCsv(out, sourceDp, exportDateFormat);
        } catch (IOException e) {
            logger.warn("Output socket closed");
        }

    }

    protected void writeCsv(OutputStream out, DataProcessor dp, DateFormat exportDateFormat) throws IOException {
        String sources[] = dp.getSourceNames();
        StringBuilder sourcesline = new StringBuilder();
        sourcesline.append("Date,");
        for(String name: sources) {
            if(! name.startsWith("rev_"))
                sourcesline.append(name + ",");
        }
        sourcesline.deleteCharAt(sourcesline.length() - 1);
        sourcesline.append("\r\n");
        out.write(sourcesline.toString().getBytes());
        double[][] values = dp.getValues();
        long[] ts = dp.getTimestamps();
        for(int i=0; i < ts.length; i++) {
            sourcesline.setLength(0);
            sourcesline.append(exportDateFormat.format(org.rrd4j.core.Util.getDate(ts[i])) + ",");
            for(int j = 0; j < sources.length; j++) {
                if(! sources[j].startsWith("rev_"))
                    sourcesline.append(values[j][i]+",");
            }
            sourcesline.deleteCharAt(sourcesline.length() - 1);
            sourcesline.append("\r\n");
            out.write(sourcesline.toString().getBytes());
        }
    }
}
