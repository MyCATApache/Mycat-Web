package jrds.starter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jrds.HostInfo;
import jrds.PropertiesManager;

import org.apache.logging.log4j.Level;

public class Timer extends StarterNode {

    public final static String DEFAULTNAME = "_default";

    public static final class Stats implements Cloneable {
        Stats() {
            lastCollect = new Date(0);
        }
        public long runtime = 0;
        public Date lastCollect;
        /* (non-Javadoc)
         * @see java.lang.Object#clone()
         */
        @Override
        public Object clone() throws CloneNotSupportedException {
            Stats newstates = new Stats();
            synchronized(this) {
                newstates.runtime = runtime;
                newstates.lastCollect = new Date(lastCollect.getTime());
            }
            return newstates;
        }
    }

    private final Map<String, HostStarter> hostList = new HashMap<String, HostStarter>();
    private Semaphore collectMutex = new Semaphore(1);
    private final Stats stats = new Stats();
    private final int numCollectors;
    private final String name;
    private TimerTask collector;

    public Timer(String name, PropertiesManager.TimerInfo ti) {
        super();
        this.name = name;
        setTimeout(ti.timeout);
        setStep(ti.step);
        this.numCollectors = ti.numCollectors;
    }

    public HostStarter getHost(HostInfo info) {
        String hostName = info.getName();
        HostStarter starter = hostList.get(hostName);
        if(starter == null) {
            starter = new HostStarter(info);
            hostList.put(hostName, starter);
            starter.setTimeout(getTimeout());
            starter.setStep(getStep());
            starter.setParent(this);
        }
        return starter;
    }

    public Iterable<HostStarter> getAllHosts() {
        return hostList.values();
    }

    public void startTimer(java.util.Timer collectTimer) {
        collector = new TimerTask () {
            public void run() {
                Thread subcollector = new Thread("Collector/" + Timer.this.name) {
                    @Override
                    public void run() {
                        try {
                            Timer.this.collectAll();
                        } catch (RuntimeException e) {
                            Timer.this.log(Level.FATAL, e, "A fatal error occured during collect: %s", e.getMessage());
                        }
                    }
                };
                subcollector.start();
            }
        };
        collectTimer.scheduleAtFixedRate(collector, getTimeout() * 1000L, getStep() * 1000L);
    }

    public void collectAll() {
        log(Level.DEBUG, "One collect is launched");
        Date start = new Date();
        try {
            if( ! collectMutex.tryAcquire(getTimeout(), TimeUnit.SECONDS)) {
                log(Level.FATAL, "A collect failed because a start time out");
                return;
            }
        } catch (InterruptedException e) {
            log(Level.FATAL, "A collect start was interrupted");
            return;
        }
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            ExecutorService tpool =  Executors.newFixedThreadPool(numCollectors, 
                    new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, Timer.this.name  + "/CollectorThread" + counter.getAndIncrement());
                    t.setDaemon(true);
                    log(Level.DEBUG, "New thread name:" + t.getName());
                    return t;
                }
            }
                    );
            startCollect();
            for(final HostStarter host: hostList.values()) {
                if( ! isCollectRunning())
                    break;
                Runnable runCollect = new Runnable() {
                    public void run() {
                        log(Level.DEBUG, "Collect all stats for host " + host.getName());
                        String threadName = Timer.this.name  + "/" + "JrdsCollect-" + host.getName();
                        Thread.currentThread().setName(threadName);
                        host.collectAll();
                        Thread.currentThread().setName(threadName + ":finished");
                    }
                    @Override
                    public String toString() {
                        return Thread.currentThread().toString();
                    }
                };
                try {
                    tpool.execute(runCollect);
                }
                catch(RejectedExecutionException ex) {
                    log(Level.DEBUG, "collector thread dropped for host " + host.getName());
                }
            }
            tpool.shutdown();
            try {
                tpool.awaitTermination(getStep() - getTimeout() * 2 , TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log(Level.WARN, "Collect interrupted");
            }
            stopCollect();
            if( ! tpool.isTerminated()) {
                //Second chance, we wait for the time out
                boolean emergencystop = false;
                try {
                    emergencystop = tpool.awaitTermination(getTimeout(), TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    log(Level.WARN, "Collect interrupted in last chance");
                }
                if(! emergencystop) {
                    log(Level.WARN, "Some task still alive, needs to be killed");
                    //Last chance to commit results
                    List<Runnable> timedOut = tpool.shutdownNow();
                    if(! timedOut.isEmpty()) {
                        log(Level.WARN, "Still " + timedOut.size() + " waiting probes: ");
                        for(Runnable r: timedOut) {
                            log(Level.WARN, r.toString());
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            log(Level.ERROR, "problem while collecting data: ", e);
        }
        finally {
            collectMutex.release();             
        }
        Date end = new Date();
        long duration = end.getTime() - start.getTime();
        synchronized(stats) {
            stats.lastCollect = start;
            stats.runtime = duration;
        }
        System.gc();
        log(Level.INFO, "Collect started at "  + start + " ran for " + duration + "ms");
    }

    public void lockCollect() throws InterruptedException {
        collectMutex.acquire();
    }

    public void releaseCollect() {
        collectMutex.release();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "timer:" + name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the stats
     */
    public Stats getStats() {
        return stats;
    }

}
