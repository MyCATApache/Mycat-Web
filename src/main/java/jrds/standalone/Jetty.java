package jrds.standalone;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServer;

import jrds.PropertiesManager;
import jrds.jmx.Management;

import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.config.Configurator;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.Authenticator;
import org.mortbay.jetty.security.BasicAuthenticator;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.security.UserRealm;
import org.mortbay.jetty.webapp.WebAppContext;

public class Jetty extends CommandStarterImpl {

    static private final Logger logger = LogManager.getLogger(Jetty.class);

    int port = 8080;
    String propFileName = "jrds.properties";
    String webRoot = ".";

    public Jetty()  {
    }

    public void configure(Properties configuration) {
        logger.debug("Configuration: " + configuration);

        port = jrds.Util.parseStringNumber((String) configuration.getProperty("jetty.port"), port).intValue();
        propFileName =  configuration.getProperty("propertiesFile", propFileName);
        webRoot = configuration.getProperty("webRoot", webRoot);
    }

    public void start(String args[]) {
        PropertiesManager pm = new PropertiesManager();
        File propFile = new File(propFileName);
        if(propFile.isFile())
            pm.join(propFile);
        pm.importSystemProps();
        pm.update();

        if(pm.withjmx) {
            doJmx(pm);
            Management.register(propFile);
        }

        System.setProperty("org.mortbay.log.class", jrds.standalone.JettyLogger.class.getName());

        final Server server = new Server();
        Connector connector=new SelectChannelConnector();
        connector.setPort(port);

        //Let's try to start the connector before the application
        try {
            connector.open();
        } catch (IOException e) {
            throw new RuntimeException("Jetty server failed to start", e);
        }
        server.setConnectors(new Connector[]{connector});

        final WebAppContext webapp = new WebAppContext(webRoot, "/");
        webapp.setClassLoader(getClass().getClassLoader());
        Map<String, Object> initParams = new HashMap<String, Object>();
        initParams.put("propertiesFile", propFileName);
        webapp.setInitParams(initParams);

        ResourceHandler staticFiles=new ResourceHandler();
        staticFiles.setWelcomeFiles(new String[]{"index.html"});
        staticFiles.setResourceBase(webRoot);

        if(pm.security) {
            try {
                UserRealm myrealm = new HashUserRealm("jrds",pm.userfile);
                server.setUserRealms(new UserRealm[] {myrealm});

                Authenticator auth = new BasicAuthenticator();
                Constraint constraint = new Constraint();
                constraint.setName("jrds");;
                constraint.setRoles(new String[]{Constraint.ANY_ROLE});
                constraint.setAuthenticate(true);
                constraint.setDataConstraint(Constraint.DC_NONE);

                ConstraintMapping cm = new ConstraintMapping();
                cm.setConstraint(constraint);
                cm.setPathSpec("/*");

                SecurityHandler sh = new SecurityHandler();
                sh.setUserRealm(myrealm);
                sh.setConstraintMappings(new ConstraintMapping[]{cm});
                sh.setAuthenticator(auth);
                webapp.setSecurityHandler(sh);
            } catch (IOException e) {
                throw new RuntimeException("Jetty server failed to configure authentication", e);
            }
        }

        HandlerCollection handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{staticFiles, webapp});
        server.setHandler(handlers);

        if(pm.withjmx) {
            MBeanServer mbs = java.lang.management.ManagementFactory.getPlatformMBeanServer();
            server.getContainer().addEventListener(new org.mortbay.management.MBeanContainer(mbs));
            handlers.addHandler(new org.mortbay.jetty.handler.StatisticsHandler());    
        }

        //Properties are not needed any more
        pm = null;

        Thread finish = new Thread() {
            public void run() {
                try {
                    server.stop();
                } catch (Exception e) {
                    throw new RuntimeException("Jetty server failed to stop", e);
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(finish);

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new RuntimeException("Jetty server failed to start", e);
        }
    }

    /* (non-Javadoc)
     * @see jrds.standalone.CommandStarterImpl#help()
     */
    @Override
    public void help() {
        System.out.println("Run an embedded web server, using jetty");
        System.out.print("The default listening port is " + port);
        System.out.println(". It can be specified using the property jetty.port");
        System.out.println("The jrds configuration file is specified using the property propertiesFile");
    }

}
