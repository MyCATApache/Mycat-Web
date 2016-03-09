package jrds.webapp;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import jrds.HostsList;
import jrds.PropertiesManager;
import jrds.StoreOpener;

import org.apache.logging.log4j.*;

/**
 * @author Fabrice Bacchella
 */
public final class WhichLibs extends JrdsServlet {
    static final private Logger logger = LogManager.getLogger(WhichLibs.class);
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HostsList hl = getHostsList();

        ParamsBean params = new ParamsBean(req, hl);
        if(! allowed(params, getPropertiesManager().adminACL, req, res))
            return;

        try {
            ServletOutputStream out = res.getOutputStream();
            res.setContentType("text/plain");
            res.addHeader("Cache-Control", "no-cache");

            ServletContext ctxt = getServletContext();

            out.println("Server info: ");
            out.println("    Servlet API: " + ctxt.getMajorVersion() + "." + ctxt.getMinorVersion());
            out.println("    Server info: " + ctxt.getServerInfo());
            out.println();

            String[] openned = StoreOpener.getInstance().getOpenFiles();
            out.println("" + StoreOpener.getInstance().getOpenFileCount() + " opened rrd: ");
            for(String rrdPath: openned) {
                out.println("   " + rrdPath + ": " + StoreOpener.getOpenCount(rrdPath));
            }
            out.println();

            PropertiesManager pm = getPropertiesManager();
            out.println("Temp dir: " + pm.tmpdir);
            out.println("current directory: " + new File(".").getCanonicalPath());
            out.println("Probes descriptions found in: ");
            for(URI descuri: getPropertiesManager().libspath ) {
                String file = descuri.toString().replace("jar:", "").replace("file:", "").replace("!/desc", "");
                out.println("    " + file);
            }
            out.println();
            out.println(resolv("String", String.class));
            out.println(resolv("jrds", WhichLibs.class));
            String transformerFactory = System.getProperties().getProperty("javax.xml.transform.TransformerFactory");
            try {
                out.print(resolv("Xml Transformer", javax.xml.transform.TransformerFactory.newInstance()));
            } catch (TransformerFactoryConfigurationError e) {
                out.print("no xml transformer factory ");
            }
            if(transformerFactory != null) {
                out.println("Set by sytem property javax.xml.transform.TransformerFactory: " + transformerFactory);
            }
            else {
                out.println();
            }
            try {
                out.println(resolv("DOM implementation",  DocumentBuilderFactory.newInstance().newDocumentBuilder().getDOMImplementation()));
            } catch (ParserConfigurationException e) {
                out.println("Invalid DOM parser configuration");
            }
            out.println(resolv("Servlet API", javax.servlet.ServletContext.class));
            out.println(resolv("SNMP4J", "org.snmp4j.transport.DefaultUdpTransportMapping"));
            out.println(resolv("Jrds Agent", "jrds.probe.RMI"));
            out.println(resolv("Log4j",logger.getClass()));
            out.println("Generation:" + hl.getGeneration());
        } catch (RuntimeException e) {
            logger.error(e, e);
        }                           
    }

    private String resolv(String name, Object o) {
        String retValue = "";
        if(o != null)
            retValue = resolv(name, o.getClass());
        else
            retValue = name + " not found";
        return retValue;
    }


    private String resolv(String name, Class<?> c) {
        String retValue = "";
        try {
            retValue = name + " found in " + locateJar(c);
        } catch (RuntimeException e) {
            retValue = "Problem with " + c + ": " + e.getMessage();
        }
        return retValue.replaceFirst("!.*", "").replaceFirst("file:", "");
    }

    private String resolv(String name, String className) {
        Class<?> c;
        try {
            c = getPropertiesManager().extensionClassLoader.loadClass(className);
            return resolv(name, c);
        } catch (ClassNotFoundException e1) {
            return name + " not found";
        }
    }

    private String locateJar(Class<?> c ) {
        String retValue="Not found";
        String cName = c.getName();
        int lastDot = cName.lastIndexOf('.');
        if(lastDot > 1) {
            String scn = cName.substring(lastDot + 1);
            URL jarUrl = c.getResource(scn + ".class");
            if(jarUrl != null)
                retValue = jarUrl.getPath();
            else
                retValue = scn + " not found";
        }
        return retValue;
    }
}
