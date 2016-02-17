package jrds.starter;

import java.util.Date;

import jrds.PropertiesManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.*;

public abstract class Starter {
	long uptime = Long.MAX_VALUE;

	private StarterNode level;
	private Logger namedLogger = null;
	volatile private boolean started = false;

	public Starter() {
		String[] classElements = getClass().getName().split("\\.");
		namedLogger = LogManager.getLogger("jrds.Starter."
				+ classElements[classElements.length - 1].replaceAll("\\$",
						".\\$"));
	}

	/**
	 * This method is called when the started is really registred
	 * <p/>
	 * It can be overriden to contains delayed initialization but it must begin
	 * with a call to super.initialize(parent)
	 * 
	 * @param level
	 */
	public void initialize(StarterNode level) {
		this.level = level;
	}

	/**
	 * It's called after the starter registration but in host list configuration
	 * A starter can uses it to tweaks it's configuration It can be overriden to
	 * contains delayed initialization but it must begin with a call to
	 * super.configuration(pm)
	 * 
	 * @param pm
	 */
	public void configure(PropertiesManager pm) {
		log(Level.DEBUG, "registred to %s", getLevel());
	}

	public final void doStart() {
		log(Level.TRACE, "Starting");
		try {
			long begin = new Date().getTime();
			started = start();
			long end = new Date().getTime();
			log(Level.DEBUG, "Starting connection took %d ms", end - begin);
		} catch (Exception e) {
			log(Level.ERROR, e, "Error while starting: %s", e.getMessage());
		} catch (NoClassDefFoundError e) {
			log(Level.ERROR, e, "Class %s not found",
					e.getMessage().replace('/', '.'));
		}
	}

	public final void doStop() {
		if (started) {
			log(Level.TRACE, "Stopping");
			started = false;
			stop();
		}
	}

	public boolean start() {
		return true;
	}

	public void stop() {
	}

	public Object getKey() {
		// System.out.println("key class:"+getClass());
		String name = getClass().getName();
		if (name == null) {
			name = getClass().toString();
		}
		return name;
	}

	public StarterNode getLevel() {
		return level;
	}

	public boolean isStarted() {
		return started;
	}

	@Override
	public String toString() {
		String levelString = "''";
		String keyString = "";
		if (level != null)
			levelString = level.toString();
		Object key = getKey();
		if (key instanceof Class<?>) {
			keyString = ((Class<?>) key).getName();
		} else if (key == null) {

			keyString = this.getClass().toString();
			System.out.println("key is null :" + keyString);
		} else {

			keyString = keyString.toString();
		}

		return keyString + "@" + levelString;
	}

	/**
	 * Return uptime for the starter, default value is max value
	 * 
	 * @return
	 */
	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	/**
	 * @deprecated Use getLevel instead.
	 * @return the StarterNode for this starter
	 */
	@Deprecated
	public StartersSet getParent() {
		return level;
	}

	public void log(Level l, Throwable e, String format, Object... elements) {
		jrds.Util.log(this, namedLogger, l, e, format, elements);
	}

	public void log(Level l, String format, Object... elements) {
		jrds.Util.log(this, namedLogger, l, null, format, elements);
	}

}
