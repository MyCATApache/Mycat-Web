package jrds.probe;

import jrds.factories.ProbeBean;


@ProbeBean({"index"})
public class LdapIndexed extends Ldap implements IndexedProbe {
	private String index;
	
	public boolean configure(String index) {
		this.index = index;
		return true;
	}
	public String getIndexName() {
		return index;
	}

	/**
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index the index to set
     */
    public void setIndex(String index) {
        this.index = index;
    }

}
