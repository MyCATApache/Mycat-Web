package jrds;

import java.util.HashMap;

import org.rrd4j.data.Plottable;

public abstract class PlottableMap extends HashMap<String, Plottable>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static class ProxyPlottable extends Plottable {
		Plottable real = new Plottable() {};
		@Override
		public double getValue(long timestamp) {
			return real.getValue(timestamp);
		}
		public void setReal(Plottable real) {
			this.real = real;
		}
	}
	/**
	 * Fill the map with the appropriate Plottable, for the given time span specification
	 * @param start the start time, in second
	 * @param end the end time, in second
	 * @param step the step, in second
	 */
	public abstract void configure(long start, long end, long step);
}
