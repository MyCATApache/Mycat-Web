package org.mycat.web.jmonitor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class JMEevntCenter implements Runnable {

    private CopyOnWriteArrayList<JMEvevntListener> listeners = new CopyOnWriteArrayList<JMEvevntListener>();
    private static final Logger log = LoggerFactory.getLogger(JMEevntCenter.class);
    private BlockingQueue<JSONObject> events = new LinkedBlockingQueue<JSONObject>();
    private ExecutorService dispatchServer = Executors.newSingleThreadExecutor();
    private static JMEevntCenter INSTANCE;

    private JMEevntCenter() {
        dispatchServer.execute(this);
    }

    public static JMEevntCenter getInstance() {
        if (INSTANCE == null) {
            synchronized (JMEevntCenter.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JMEevntCenter();
                }
            }
        }
        return INSTANCE;
    }

    public void run() {
        while (!Thread.interrupted()) {
            try {
                JSONObject event = events.take();
                for (JMEvevntListener listener : listeners) {
                    listener.handle(event);
                }
            } catch (Exception e) {
                log.error("dispatch event error", e);
            }
        }
    }

    public void addListener(JMEvevntListener listener) {
        listeners.add(listener);
    }

    public void send(JSONObject event) {
        events.add(event);
    }

    public void close() {
        events.clear();
        listeners.clear();
        dispatchServer.shutdownNow();
    }

}
