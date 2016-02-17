package jrds.starter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jrds.probe.PassiveProbe;

import org.apache.logging.log4j.Level;

public abstract class Listener<Message, KeyType> extends Starter {

    Thread listenerThread = null;
    private final Map<String, Map<String,PassiveProbe<KeyType>>> probes = new HashMap<String, Map<String,PassiveProbe<KeyType>>>();

    public boolean start() {
        listenerThread = new Thread() {
            @Override
            public void run() {
                long lastsleep = 500;
                while (Listener.this.isStarted() && ! isInterrupted()) {
                    Date startListen = new Date();
                    try {
                        Listener.this.listen();
                    } catch (InterruptedException e) {
                        // Normal exception, just exit
                        break;
                    } catch (Exception e) {
                        Date failedListen = new Date();
                        if ( (failedListen.getTime() - startListen.getTime() < lastsleep))
                            lastsleep *= 2;
                        else 
                            lastsleep = 500;
                        try {
                            Thread.sleep(lastsleep);
                        } catch (InterruptedException e1) {
                            break;
                        }
                        log(Level.ERROR, e, "Listener thread failed: %s", e.getMessage());
                    }
                }
            }
        };
        listenerThread.setDaemon(true);
        listenerThread.setName(String.format("Listener/%s", this));
        listenerThread.start();
        return true;
    }

    public void stop() {
        if(listenerThread != null) {
            listenerThread.interrupt();
            listenerThread = null;
        }
    }

    public void register(PassiveProbe<KeyType> p) {
        log(Level.DEBUG, "adding %s", p);
        String hostname = getHost(p);
        if(! probes.containsKey(p.getHost().getDnsName())) {
            probes.put(hostname, new HashMap<String, PassiveProbe<KeyType>>());
        }
        probes.get(hostname).put(p.getName(), p);        
    }

    /**
     * This method should be call to identify the probe used to store a received external input. It uses result from identifyHost and identifyProbe to find it.
     * @param message the external input
     * @return the probe where values will be stored
     */
    protected PassiveProbe<KeyType> findProbe(Message message) {
        String hostname = identifyHost(message);
        String probename = identifyProbe(message);
        log(Level.DEBUG, "looking for %s in %s", message, probes);
        if(! probes.containsKey(hostname)) {
            log(Level.WARN, "unregistered sender: %s", hostname);
            return null;
        }
        PassiveProbe<KeyType> pp = probes.get(hostname).get(probename);
        if( pp == null) {
            log(Level.WARN, "unregistered probe: %s", probename);
            return null;
        }
        return pp;
    }

    /**
     * The method used to wait on external input and feed back the result to the probe, using jrds.probe.PassiveProbe.store(Date, Map<KeyType, Number>).
     * There is no need to manage exceptions and interruption in it. This method is called again in case of failure, with adaptative sleep between each call.
     * @throws Exception
     */
    protected abstract void listen() throws Exception;

    /**
     * Extract the string to identify the host from a message
     * @param message
     * @return
     */
    protected abstract String identifyHost(Message message);

    /**
     * Extract the string to identify the probe from a message
     * @param message
     * @return
     */
    protected abstract String identifyProbe(Message message);

    /**
     * Extract the string that will identity the host from the host.
     * This method is the inversion of  identifyHost(Message message)
     * @param pp
     * @return
     */
    protected abstract String getHost(PassiveProbe<KeyType> pp);

    /**
     * @return A nice string that will be displayed in the graph
     */
    public abstract String getSourceType();

}
