package org.mycat.web.model.cluster;

public class Sequence {
	
	private int currentValue;
	private int increament;
	
	
	public int getCurrentValue() {
		return currentValue;
	}
	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}
	public int getIncreament() {
		return increament;
	}
	public void setIncreament(int increament) {
		this.increament = increament;
	}

}
