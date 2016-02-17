package jrds.standalone;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.mortbay.log.Logger;

public class JettyLogger implements Logger {
	org.apache.logging.log4j.Logger logger = LogManager.getLogger(JettyLogger.class);
	public JettyLogger() {
	}

	public JettyLogger(String name) {
		logger = LogManager.getLogger(name);
	}

	public void debug(String message, Throwable arg1) {
		doLog(Level.DEBUG, message, arg1);
	}

	public void debug(String message, Object arg1, Object arg2) {
		doLog(Level.DEBUG, message, null, arg1, arg2);
	}

	public Logger getLogger(String arg0) {
		return new JettyLogger(arg0);
	}

	public void info(String message, Object arg1, Object arg2) {
		doLog(Level.INFO, message, null, arg1, arg2);
	}

	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	public void setDebugEnabled(boolean arg0) {
	}

	public void warn(String message, Throwable error) {
		doLog(Level.WARN, message, error);
	}

	public void warn(String message, Object arg1, Object arg2) {
		doLog(Level.WARN, message, null, arg1, arg2);
	}
	
	private void doLog(Level l, String message, Throwable error, Object... args) {
		if(logger.isEnabled(l)) {
			logger.log(l, String.format(message.replaceAll("\\{\\}", "\\%s"), args));
			if(error != null) {
				Writer w = new CharArrayWriter(error.getStackTrace().length + 20);
				error.printStackTrace(new PrintWriter(w));
				logger.log(l, "Error stack: ");
				logger.log(l, w);
			}
		}
	}
}
