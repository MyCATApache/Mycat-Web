/*##########################################################################
 _##
 _##  $Id$
 _##
 _##########################################################################*/

package jrds.probe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jrds.Util;

import org.apache.logging.log4j.Level;

/**
 * A class to probe the apache status from the mod_status
 * @author Fabrice Bacchella 
 */
public class ApacheStatus extends HCHttpProbe implements IndexedProbe {

    public ApacheStatus() {
        super();
    }

    public Boolean configure() {

        if(this.url == null) {
            file = file + "?auto"; 
        }
        return super.configure();
    }

    /**
     * @return Returns the url.
     */
    public String getUrlAsString() {
        String retValue = "";
        try {
            URL tempUrl = new URL("http", getUrl().getHost(), getUrl().getPort(), "/");
            retValue = tempUrl.toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException("MalformedURLException",e);
        }
        return retValue;
    }

    public String getIndexName() {
        int port = getUrl().getPort();
        if(port <= 0)
            port = 80;
        return Integer.toString(port);
    }

    /* (non-Javadoc)
     * @see jrds.probe.HttpProbe#parseStream(java.io.InputStream)
     */
    @Override
    protected Map<String, Number> parseStream(InputStream stream) {
        Map<String, Number> vars = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            List<String> lines = new ArrayList<String>();
            String lastLine;
            while((lastLine = in.readLine()) != null)
                lines.add(lastLine);
            in.close();
            vars = parseLines(lines);
        } catch (IOException e) {
            log(Level.ERROR,e ,  "Unable to read url %s because %s", getUrl(), e.getMessage());
        }
        return vars;
    }

    /* (non-Javadoc)
     * @see com.aol.jrds.HttpProbe#parseLines(java.util.List)
     */
    protected Map<String, Number> parseLines(List<String> lines) {
        Map<String, Number> retValue = new HashMap<String, Number>(lines.size());
        for(String l: lines) {
            String[] kvp = l.split(":");
            if(kvp.length !=2)
                continue;
            Double value = Util.parseStringNumber(kvp[1].trim(), Double.NaN);
            retValue.put(kvp[0].trim(), value);
        }
        Number uptimeNumber = retValue.remove("Uptime");
        if(uptimeNumber != null)
            setUptime(uptimeNumber.longValue());
        return retValue;
    }

}
