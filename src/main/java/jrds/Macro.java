package jrds;

import org.w3c.dom.DocumentFragment;

public class Macro {
	private DocumentFragment df;
	private String name;

	public Macro() {
	}

	@Override
	public String toString() {
		return "Macro$" + name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the df
	 */
	public DocumentFragment getDf() {
		return df;
	}

	/**
	 * @param df the df to set
	 */
	public void setDf(DocumentFragment df) {
		this.df = df;
	}

}
