package jrds.probe;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import jrds.HostInfo;
import jrds.Probe;
import jrds.ProbeDesc;

import org.apache.logging.log4j.Level;

public class ContainerProbe extends Probe<Object, Number> {
    static private final HostInfo ContainerHost = new HostInfo("ContainerHost");

    static final ProbeDesc pd = new ProbeDesc(0) {
        @Override
        public String getName() {
            return "ContainerProbeDesc";
        }
        @Override
        public Class<? extends Probe<?,?>> getProbeClass() {
            return (Class<? extends Probe<?, ?>>) ContainerProbe.class;
        }
        public String getProbeName() {
            return "ContainerProbeDesc";
        }	
    };

    public ContainerProbe(String name) {
        super(pd);
        setName(name);
        this.monitoredHost = ContainerHost;
    }

    //An array list is needed, the introspection is picky
    public void configure(String name, ArrayList<String> graphList) {
        log(Level.DEBUG, "new container: %s", name);
        setName(name);
    }

    @Override
    public Date getLastUpdate() {
        return new Date();
    }

    @Override
    public String getSourceType() {
        return "container";
    }

    public Map<Object, Number> getNewSampleValues() {
        return java.util.Collections.emptyMap();
    }

   
    /**
     * This method does nothing for a virtual probe
     * @see jrds.Probe#collect()
     */
    public void collect() {
    }

    /* (non-Javadoc)
     * @see jrds.Probe#getRrdName()
     */
    @Override
    public String getRrdName() {
        return getName();
    }

    /* (non-Javadoc)
     * @see jrds.Probe#getQualifiedName()
     */
    @Override
    public String getQualifiedName() {
        return "/"  + getName();
    }

}
