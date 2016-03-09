package jrds.probe;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.Sample;

import jrds.Probe;
import jrds.StoreOpener;
import jrds.starter.Listener;
import jrds.starter.StarterNode;

public class PassiveProbe<KeyType> extends Probe<KeyType, Number> {

    private Listener<?, KeyType> listener = null;

    public void configure() {
        setName(jrds.Util.parseTemplate(getPd().getProbeName(), this));
        if(listener != null) {
            listener.register(this);                
        }
    }

    /**
     * Used by the listener starter to store value
     * @param rawValues
     */
    public void store(Date time, Map<KeyType, Number> rawValues) {
        try {
            RrdDb rrdDb = StoreOpener.getRrd(getRrdName());
            Sample sample = rrdDb.createSample();
            sample.setTime(time.getTime() / 1000);
            updateSample(sample, rawValues);
            StoreOpener.releaseRrd(rrdDb);
        } catch (IOException e) {
            log(Level.ERROR, e, "Failed to store sample: %s", e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see jrds.Probe#collect()
     */
    @Override
    public void collect() {
    }

    @Override
    public Map<KeyType, Number> getNewSampleValues() {
        return Collections.emptyMap();
    }

    @Override
    public String getSourceType() {
        return listener.getSourceType();
    }

    /* (non-Javadoc)
     * @see jrds.starter.StarterNode#setParent(jrds.starter.StarterNode)
     */
    @Override
    public void setParent(StarterNode parent) {
        super.setParent(parent);
        if(listener == null) {
            String listenerClassName = this.getPd().getSpecific("listener");
            try {
                @SuppressWarnings("unchecked")
                Class<Listener<?, KeyType>> listenerClass = (Class<Listener<?, KeyType>>) this.getClass().getClassLoader().loadClass(listenerClassName);
                listener = parent.find(listenerClass);
            } catch (ClassNotFoundException e) {
                log(Level.ERROR, e, "Can't find listener class: %s", e.getMessage());
            }            
        }
    }

    /**
     * @return the listener
     */
    public Listener<?, KeyType> getListener() {
        return listener;
    }

    /**
     * @param l the listener to set
     */
    @SuppressWarnings("unchecked")
    public void setListener(Listener<?, ?> l) {
        this.listener = (Listener<?, KeyType>) l;
    }

}
