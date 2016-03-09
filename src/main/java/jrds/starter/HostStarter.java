package jrds.starter;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;

import jrds.HostInfo;
import jrds.Probe;

public class HostStarter extends StarterNode {
    private HostInfo host;
    private final Set<Probe<?,?>> allProbes = new TreeSet<Probe<?,?>>();

    public HostStarter(HostInfo host) {
        super();
        this.host = host;
        registerStarter(new Resolver(host.getDnsName()));
    }
    
    public void addProbe(Probe<?,?> p){
        host.addProbe(p);
        allProbes.add(p);
    }
    
    public Iterable<Probe<?,?>> getAllProbes() {
        return allProbes;
    }
    
    public void collectAll() {
        log(Level.DEBUG, "Starting collect");
        long start = System.currentTimeMillis();
        startCollect();
        for(Probe<?,?> probe: allProbes) {
            if(! isCollectRunning() )
                break;
            long duration = (System.currentTimeMillis() - start) / 1000 ;
            if(duration > (probe.getStep() / 2 )) {
                log(Level.ERROR, "Collect too slow: %ds", duration);
                break;
            }
            log(Level.TRACE, "Starting collect for %s", probe);
            probe.collect();
        }
        stopCollect();
        long end = System.currentTimeMillis();
        float elapsed = (end - start)/1000f;
        log(Level.DEBUG, "Collect time for %s: %fs", host.getName(), elapsed);
    }

    public String toString() {
        return host.toString();
    }

    public int compareTo(HostStarter arg0) {
        return String.CASE_INSENSITIVE_ORDER.compare(host.getName(),arg0.getHost().getName() );
    }

    /**
     * @return the host
     */
    public HostInfo getHost() {
        return host;
    }

    public File getHostDir() {
        return host.getHostDir();
    }

    public String getName() {
        return host.getName();
    }

    public Set<String> getTags() {
        return host.getTags();
    }

    public String getDnsName() {
        return host.getDnsName();
    }

    public boolean isHidden() {
        return host.isHidden();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof HostStarter))
            return false;
        HostStarter other = (HostStarter) obj;
        boolean parentEquals;
        if(getParent() != null)
            parentEquals = getParent().equals(other.getParent());
        else
            parentEquals = other.getParent() == null;
        return host.equals(other.getHost()) && parentEquals;
    }

}
