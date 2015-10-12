package jrds.probe;

import jrds.factories.ProbeBean;

@ProbeBean({"node"})
public class ExternalCmdIndexed extends ExternalCmdProbe implements IndexedProbe {
    private String node;
    
    /**
     * @return the host
     */
    public String getNode() {
        return node;
    }

    /**
     * @param host the host to set
     */
    public void setNode(String node) {
        this.node = node;
    }

    @Override
    public String getIndexName() {
        return getNode();
    }

}
