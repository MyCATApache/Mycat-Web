package jrds.thresholds;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.net.SMTPAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.TriggeringEventEvaluator;

import jrds.Probe;

public class Mail extends Action {
	static final Logger mailloger = Logger.getLogger("maillogger");

	static final public  SMTPAppender mailappender = new org.apache.log4j.net.SMTPAppender( new TriggeringEventEvaluator() {
		public boolean isTriggeringEvent(LoggingEvent arg0) {
			return true;
		}
	});
	static {
		mailappender.setSubject("Threshold reached");
		mailappender.setName("Jrds Thresold logger");
		mailappender.setLayout(new PatternLayout() );
		mailappender.setSMTPDebug(false);
		mailappender.setThreshold(Level.TRACE);
		mailappender.activateOptions();
		mailloger.addAppender(mailappender);
	}

	public Mail(String actionMailFrom, String  actionMailTo, String actionMailHost ) {
		mailappender.setFrom(actionMailFrom);
		mailappender.setTo(actionMailTo);
		mailappender.setSMTPHost(actionMailHost);
		mailappender.activateOptions();
	}
	
	@Override
	public void run(Threshold t, Probe<?,?> p, List<Object> args) {
		mailloger.log(Level.INFO, "Threshold reached for " + t.name + " on "+ p);
	}

}
