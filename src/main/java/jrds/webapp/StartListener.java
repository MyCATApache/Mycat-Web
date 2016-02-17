package jrds.webapp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jrds.StoreOpener;

import org.apache.logging.log4j.*;

/**
 * Used to start the application.<p>
 * Jrds search his configuration in different places, using the following order :
 * <ol>
 * <li>A file named <code>jrds.properties</code> in the <code>/WEB-INF</code> directory.
 * <li>The init parameters of the web app.
 * <li>A file whose path given by the init parameter <code>propertiesFile</code>.
 * <li>A file whose path is given by system property named <code>jrds.propertiesFile</code>.
 * <li>Any system property whose name start with <code>jrds.</code> .
 * </ol>
 * @author Fabrice Bacchella 
 * @version $Revision$,  $Date$
 */
public class StartListener implements ServletContextListener {
	static private final Logger logger = LogManager.getLogger(StartListener.class);
	static private boolean started = false;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		//Resin and some others launch the listener twice !
		if( ! started ) {
			System.setProperty("java.awt.headless","true");

			ServletContext ctxt = arg0.getServletContext();
			ctxt.setAttribute(StartListener.class.getName(), this);
            configure(ctxt);
			started = true;
			logger.info("Application jrds started");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		if(started) {
			logger.info("Application jrds will stop");
			started = false;
			jrds.Configuration.get().stop();
			StoreOpener.stop();
			logger.info("Application jrds stopped");
		}
	}

    public void configure(ServletContext ctxt) {
        Properties p = new Properties();

        InputStream propStream = ctxt.getResourceAsStream("/WEB-INF/classes/jrds.properties");
        if(propStream != null) {
            try {
                p.load(propStream);
            } catch (IOException ex) {
                logger.warn("Invalid properties stream " + propStream + ": " + ex);
            }
        }
        
		String webroot = System.getProperty("webapp.root");
		p.setProperty("configdir", webroot + "/WEB-INF/jrdsconf/hosts");
		p.setProperty("rrddir", webroot + "/WEB-INF/jrdsconf/rrddir");

		Enumeration<String> params = (Enumeration<String>)ctxt.getInitParameterNames();
        for(String attr: jrds.Util.iterate(params)) {
            String value = ctxt.getInitParameter(attr);
            if(value != null)
                p.setProperty(attr, value);
        }

        String localPropFile = ctxt.getInitParameter("propertiesFile");
        if(localPropFile != null)
            try {
                p.load(new FileReader(localPropFile));
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        jrds.Configuration.configure(p);
    }

}
