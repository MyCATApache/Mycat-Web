package jrds.probe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Map;

import jrds.Probe;
import jrds.Util;
import jrds.factories.ProbeBean;
import jrds.starter.Resolver;
import jrds.starter.Starter;

import org.apache.logging.log4j.Level;

/**
 * A generic probe to collect an HTTP service
 * default generic : 
 * port to provide a default port to collect
 * file to provide a specific file to collect
 * 
 * Implementation should implement the parseStream method
 *
 * @author Fabrice Bacchella 
 */
@ProbeBean({"port",  "file", "url", "urlhost"})
public abstract class HttpProbe extends Probe<String, Number> implements UrlProbe {
    protected URL url = null;
    protected String urlhost = null;
    protected int port = 80;
    protected String file = "/";
    Starter resolver = null;

    public Boolean configure(URL url) {
        this.url = url;
        return finishConfigure(null);
    }

    public Boolean configure(Integer port, String file) {
        this.port = port;
        this.file = file;
        return finishConfigure(null);
    }

    public Boolean configure(Integer port) {
        this.port = port;
        return finishConfigure(null);
    }

    public Boolean configure(String file) {
        this.file = file;
        return finishConfigure(null);
    }

    public Boolean configure(List<Object> argslist) {
        return finishConfigure(argslist);
    }

    public Boolean configure(String file, List<Object> argslist) {
        this.file = file;
        return finishConfigure(argslist);
    }

    public Boolean configure(Integer port, List<Object> argslist) {
        this.port = port;
        return finishConfigure(argslist);
    }

    public Boolean configure(URL url, List<Object> argslist) {
        this.url = url;
        return finishConfigure(argslist);
    }

    public Boolean configure(Integer port, String file, List<Object> argslist) {
        this.port = port;
        this.file = file;
        return finishConfigure(argslist);
    }

    public Boolean configure() {
        return finishConfigure(null);
    }

    private boolean finishConfigure(List<Object> argslist) {
        if(url == null) {
            try {
                if(urlhost == null)
                    urlhost = getHost().getDnsName();
                if(argslist != null) {
                    try {
                        String urlString = String.format("http://" + urlhost + ":" + port + file, argslist.toArray());
                        url = new URL(Util.parseTemplate(urlString, getHost(), argslist));
                    } catch (IllegalFormatConversionException e) {
                        log(Level.ERROR, "Illegal format string: http://%s:%d%s, args %d", urlhost, port, file, argslist.size());
                        return false;
                    }
                }
                else {
                    url = new URL("http", urlhost, port, file);
                }
            } catch (MalformedURLException e) {
                log(Level.ERROR, e, "URL 'http://%s:%s%s' is invalid", urlhost, port, file);
                return false;
            }
        }
        if("http".equals(url.getProtocol())) {
            resolver = getParent().registerStarter(new Resolver(url.getHost()));
        }
        log(Level.DEBUG, "URL to collect is %s", getUrl());
        return true;
    }

    /* (non-Javadoc)
     * @see jrds.Probe#isCollectRunning()
     */
    @Override
    public boolean isCollectRunning() {
        if (resolver == null || ! resolver.isStarted())
            return false;
        return super.isCollectRunning();
    }

    /**
     * @param A stream collected from the http source
     * @return a map of collected value
     */
    protected abstract Map<String, Number> parseStream(InputStream stream);

    /**
     * A utility method that transform the input stream to a List of lines
     * @param stream
     * @return
     */
    public List<String> parseStreamToLines(InputStream stream) {
        List<String> lines = java.util.Collections.emptyList();
        log(Level.DEBUG, "Getting %s", getUrl());
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            lines = new ArrayList<String>();
            String lastLine;
            while((lastLine = in.readLine()) != null)
                lines.add(lastLine);
            in.close();
        } catch (IOException e) {
            log(Level.ERROR, e, "Unable to read url %s because: %s", getUrl(), e.getMessage());
        }
        return lines;
    }

    /* (non-Javadoc)
     * @see com.aol.jrds.Probe#getNewSampleValues()
     */
    public Map<String, Number> getNewSampleValues() {
        log(Level.DEBUG, "Getting %s", getUrl());
        URLConnection cnx = null;
        try {
            cnx = getUrl().openConnection();
            cnx.setConnectTimeout(getTimeout() * 1000);
            cnx.setReadTimeout(getTimeout() * 1000);
            cnx.connect();
        } catch (IOException e) {
            log(Level.ERROR, e, "Connection to %s failed: %s", getUrl(), e.getMessage());
            return null;
        }
        try {
            InputStream is = cnx.getInputStream();
            Map<String, Number> vars = parseStream(is);
            is.close();
            return vars;
        } catch(ConnectException e) {
            log(Level.ERROR, e, "Connection refused to %s", getUrl());
        } catch (IOException e) {
            //Clean http connection error management
            //see http://java.sun.com/j2se/1.5.0/docs/guide/net/http-keepalive.html
            try {
                byte[] buffer = new byte[4096];
                int respCode = ((HttpURLConnection)cnx).getResponseCode();
                log(Level.ERROR, e, "Unable to read url %s because: %s, http error code: %d", getUrl(), e.getMessage(), respCode);
                InputStream es = ((HttpURLConnection)cnx).getErrorStream();
                // read the response body
                while (es.read(buffer) > 0) {}
                // close the error stream
                es.close();
            } catch(IOException ex) {
                log(Level.ERROR, ex, "Unable to recover from error in url %s because %s", getUrl(), ex.getMessage());
            }
        }

        return null;
    }

    /**
     * @return Returns the url.
     */
    public String getUrlAsString() {
        return getUrl().toString();
    }

    public Integer getPort() {
        return port;
    }

    /**
     * @return Returns the url.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * @param url The url to set.
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public String getSourceType() {
        return "HTTP";
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the path
     */
    public String getFile() {
        return file;
    }

    /**
     * @param path the path to set
     */
    public void setFile(String path) {
        this.file = path;
    }

    /**
     * @return the urlhost
     */
    public String getUrlhost() {
        return urlhost;
    }

    /**
     * @param urlhost the urlhost to set
     */
    public void setUrlhost(String urlhost) {
        this.urlhost = urlhost;
    }

}
