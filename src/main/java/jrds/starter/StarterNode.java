package jrds.starter;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jrds.HostsList;
import jrds.PropertiesManager;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.*;

@SuppressWarnings("deprecation")
public abstract class StarterNode implements StartersSet {

    private Map<Object, Starter> allStarters = null;

    private HostsList root = null;
    private volatile boolean started = false;
    private StarterNode parent = null;
    private int timeout = -1;
    private int step = -1;

    public StarterNode() {
        if (this instanceof HostsList) {
            root = (HostsList) this;
        }
    }

    public StarterNode(StarterNode parent) {
        setParent(parent);
    }

    public void setParent(StarterNode parent) {
        root = parent.root;
        this.parent = parent;
    }

    public boolean isCollectRunning() {
        if(Thread.interrupted()) {
            started = false;
            log(Level.TRACE, "thread is stopped", this);
            return false;
        }
        if(parent != null && ! parent.isCollectRunning())
            return false;
        return started;
    }

    public boolean startCollect() {
        if(parent != null && ! parent.isCollectRunning()) {
            log(Level.TRACE, "parent of %s prevent starting", this);
            return false;
        }
        if(allStarters != null) {
            log(Level.DEBUG, "Starting %d starters for %s", allStarters.size(), this);
            for(Starter s: allStarters.values()) {
                //If collect is stopped while we're starting, drop it
                if(parent != null && ! parent.isCollectRunning())
                    return false;
                try {
                    s.doStart();
                } catch (Exception e) {
                    log(Level.ERROR, e, "starting %s failed: %s", s, e);
                }
            }
        }
        started = true;
        log(Level.DEBUG, "Starting for %s done", this);
        return isCollectRunning();
    }

    public synchronized void stopCollect() {
        started = false;
        if(allStarters != null)
            for(Starter s: allStarters.values()) {
                try {
                    s.doStop();
                } catch (Exception e) {
                    log(Level.ERROR, e, "Unable to stop timer %s: %s", s.getKey(), e);
                }
            }
    }

    /**
     * @param s the starter to register
     * @return the starter that will be used
     */
    public Starter registerStarter(Starter s) {
        Object key = s.getKey();
        if(allStarters == null)
            //Must be a linked hashed map, order of insertion might be important
            allStarters = new LinkedHashMap<Object, Starter>(2);
        if(! allStarters.containsKey(key)) {
            s.initialize(this);
            allStarters.put(key, s);
            log(Level.DEBUG, "registering %s with key %s", s.getClass().getName(), key);
            return s;
        }
        else {
            return allStarters.get(key);
        }
    }

    /**
     * Called in the host list configuration, used to finished the configuration
     * of the starters
     * @param pm the configuration
     */
    public void configureStarters(PropertiesManager pm) {
        if(allStarters == null)
            return;

        //A set with failed starters
        Set<Object> failed = new HashSet<Object>();
        for(Map.Entry<Object, Starter> me: allStarters.entrySet()) {
            try {
                me.getValue().configure(pm);
            } catch (Exception e) {
                failed.add(me.getKey());
                log(Level.ERROR, e, "Starter %s failed to configure: %s", me.getValue(), e);
            }
        }
        // Failed starter are removed
        for(Object k: failed) {
            allStarters.remove(k);
        }
    }

    public <StarterClass extends Starter> StarterClass find(Class<StarterClass> sc) {
        Object key = null;
        try {
            Method m = sc.getMethod("makeKey", StarterNode.class);
            key = m.invoke(null, this);
        } catch (NoSuchMethodException e) {
            //Not an error, the key is the the class
            key = sc.getName();
        } catch (Exception e) {
            log(Level.ERROR, e, "Error for %s with %s: %s", this, sc, e);
            return null;
        }
        return find(sc, key);
    }

    @SuppressWarnings("unchecked")
    public <StarterClass extends Starter> StarterClass find(String key) {
        return (StarterClass) find(Starter.class, key);        
    }

    /* (non-Javadoc)
     * @see jrds.starter.StartersSet#find(java.lang.Object)
     */
    @Deprecated
    public Starter find(Object key) {
        return find(Starter.class, key);
    }

    @SuppressWarnings("unchecked")
    public <StarterClass extends Starter> StarterClass find(Class<StarterClass> sc, Object key) {
        StarterClass s = null;
        if(allStarters != null)
            log(Level.TRACE, "Looking for starter %s with key %s in %s", sc, key, allStarters);
        if(allStarters != null && allStarters.containsKey(key)) {
            Starter stemp = allStarters.get(key);
            if(sc.isInstance(stemp)) {
                s = (StarterClass) stemp;
            }
            else {
                log(Level.ERROR, "Starter key error, got a %s expecting a %s", stemp.getClass(), sc);
                return null;
            }
        }
        else if(parent != null )
            s = parent.find(sc, key);
        else
            log(Level.DEBUG, "Starter class %s not found for key %s", sc.getName(), key);
        return s;
    }

    public boolean isStarted(Object key) {
        boolean s = false;
        Starter st = find(Starter.class, key);
        if(st != null)
            s = st.isStarted();
        return s;
    }

    public HostsList getHostList() {
        if(root == null && getParent() != null)
            root = getParent().getHostList();
        return root;
    }

    /**
     * @return the parent
     */
    public StarterNode getParent() {
        return parent;
    }

    //Compatibily code
    /**
     * @deprecated
     * Useless method, it return <code>this</code>
     * @return
     */
    @Deprecated
    public StartersSet getStarters() {
        return this;
    }

    /**
     * @deprecated
     * Useless method, it return <code>this</code>
     * @return
     */
    @Deprecated
    public StarterNode getLevel() {
        return this;
    }

    @Deprecated
    public void setParent(StartersSet s) {
        setParent((StarterNode) s);
    }

    /* (non-Javadoc)
     * @see jrds.starter.StartersSet#registerStarter(jrds.starter.Starter, jrds.starter.StarterNode)
     */
    @Deprecated
    public Starter registerStarter(Starter s, StarterNode parent) {
        return registerStarter(s);
    };

    /* (non-Javadoc)
     * @see jrds.starter.StartersSet#find(java.lang.Class, jrds.starter.StarterNode)
     */
    @Deprecated
    public <StarterClass extends Starter> StarterClass find(Class<StarterClass> sc, StarterNode nope) {
        return find(sc);
    }

    public void log(Level l, Throwable e, String format, Object... elements) {
        jrds.Util.log(this, LogManager.getLogger(getClass()), l, e, format, elements);
    }

    public void log(Level l, String format, Object... elements) {
        jrds.Util.log(this, LogManager.getLogger(getClass()), l, null, format, elements);
    }

    /**
     * @return the timeout
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * @param timeout the timeout to set
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /**
     * @return the step
     */
    public int getStep() {
        return step;
    }

    /**
     * @param step the step to set
     */
    public void setStep(int step) {
        this.step = step;
    }

}
