package jrds.standalone;

import java.io.File;
import java.util.Properties;

import jrds.HostsList;
import jrds.PropertiesManager;
import jrds.StoreOpener;
import jrds.starter.Timer;

import org.apache.logging.log4j.*;


/**
 * @author Fabrice Bacchella
 *
 */
public class Collector extends CommandStarterImpl {
    static final private Logger logger = LogManager.getLogger(Collector.class);

    String propFile = "jrds.properties";

    public void configure(Properties configuration) {
        logger.debug("Configuration: " + configuration);

        propFile = configuration.getProperty("propertiesFile", propFile);
    }

    public void start(String[] args) throws Exception {

        PropertiesManager pm = new PropertiesManager(new File(propFile));
        //jrds.JrdsLoggerConfiguration.configure(pm);

        System.getProperties().setProperty("java.awt.headless","true");
        System.getProperties().putAll(pm);
        StoreOpener.prepare(pm.rrdbackend, pm.dbPoolSize );

        HostsList hl = new HostsList(pm);

        logger.debug("Scanning dir");

        for(Timer t: hl.getTimers()) {
            t.collectAll();
        }
        StoreOpener.stop();
    }

}

