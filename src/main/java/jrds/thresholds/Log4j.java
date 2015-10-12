package jrds.thresholds;

import java.net.URL;
import java.util.List;

import jrds.Probe;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.RootLogger;
import org.apache.log4j.xml.DOMConfigurator;

public class Log4j extends Action {
	public Log4j(URL configFile) {
		Logger hroot = new RootLogger(Level.ALL);
		Hierarchy h = new Hierarchy(hroot);

		DOMConfigurator config = new DOMConfigurator();
		config.doConfigure(configFile, h);

	}

	public Log4j(String configFile) {
		Logger hroot = new RootLogger(Level.ALL);
		Hierarchy h = new Hierarchy(hroot);

		DOMConfigurator config = new DOMConfigurator();
		config.doConfigure(configFile, h);

	}

	@Override
	public void run(Threshold t, Probe<?,?> p, List<Object> args) {		
	}

}
