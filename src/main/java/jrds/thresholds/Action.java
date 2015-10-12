package jrds.thresholds;

import java.util.List;

import jrds.Probe;

public abstract class Action {
	public abstract void run(Threshold t, Probe<?,?> p, List<Object> args);
}
