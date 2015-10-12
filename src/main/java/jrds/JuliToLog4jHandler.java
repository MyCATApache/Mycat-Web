package jrds;

import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * A JULI (java.util.logging) handler that redirects java.util.logging messages to Log4J
 * http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 * <br>
 * User: josh
 * Date: Jun 4, 2008
 * Time: 3:31:21 PM
 */
public class JuliToLog4jHandler extends Handler {

    public JuliToLog4jHandler() {
        super();
    }

    public void publish(LogRecord record) {
        org.apache.log4j.Logger log4j = Logger.getLogger(record.getLoggerName());
        Priority priority = toLog4j(record.getLevel());
        log4j.log(priority, toLog4jMessage(record), record.getThrown());
    }

    private String toLog4jMessage(LogRecord record) {
        String message = record.getMessage();
        // Format message
        Object parameters[] = record.getParameters();
        if (parameters != null && parameters.length != 0) {
            // Check for the first few parameters ?
            if (message.indexOf("{0}") >= 0 ||
                    message.indexOf("{1}") >= 0 ||
                    message.indexOf("{2}") >= 0 ||
                    message.indexOf("{3}") >= 0) {
                message = MessageFormat.format(message, parameters);
            }
        }
        return message;
    }

    private org.apache.log4j.Level toLog4j(Level level) {
        if(Level.ALL.equals(level))
            return org.apache.log4j.Level.ALL;
        else if(Level.SEVERE.equals(level))
            return org.apache.log4j.Level.ERROR;
        else if(Level.WARNING.equals(level))
            return org.apache.log4j.Level.WARN;
        else if(Level.INFO.equals(level))
            return org.apache.log4j.Level.INFO;
        else if(Level.CONFIG.equals(level))
            return org.apache.log4j.Level.INFO;
        else if(Level.FINE.equals(level))
            return org.apache.log4j.Level.DEBUG;
        else if(Level.FINER.equals(level))
            return org.apache.log4j.Level.DEBUG;
        else if(Level.FINEST.equals(level))
            return org.apache.log4j.Level.TRACE;
        else if(Level.OFF.equals(level))
            return org.apache.log4j.Level.OFF;
        return null;
    }

    @Override
    public void flush() {
        // nothing to do
    }

    @Override
    public void close() {
        // nothing to do
    }
}