package jrds.starter;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.logging.log4j.Level;

import jrds.Probe;

public abstract class Connection<ConnectedType> extends Starter {

    private String name;
    private long uptime;

    public abstract ConnectedType getConnection();

    Socket makeSocket(String host, int port) throws UnknownHostException, IOException {
        SocketFactory sf = getLevel().find(SocketFactory.class);
        return sf.createSocket(host, port);
    }

    /* (non-Javadoc)
     * @see jrds.Starter#getKey()
     */
    @Override
    public Object getKey() {
        if(name !=null)
            return name;
        else
            return getClass().getName();
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
     * Return the host name associated
     * @return
     */
    public String getHostName() {
        StarterNode level = getLevel();
        if( level instanceof HostStarter) {
            return ((HostStarter) level).getDnsName();
        }
        if(level instanceof Probe<?, ?>) {
            return ((Probe<?,?>)level).getHost().getDnsName();
        }
        return null;
    }

    public Resolver getResolver() {
        String hostName = getHostName();
        Resolver r = getLevel().find(Resolver.class);
        if(r == null) {
            r = new Resolver(hostName);
            getLevel().registerStarter(r);
        }
        return r;
    }

    /**
     * To get the default time out
     * @return the connection timeout in second
     */
    public int getTimeout() {
        SocketFactory sf = getLevel().find(SocketFactory.class);
        return sf.getTimeout();
    }

    /* (non-Javadoc)
     * @see jrds.Starter#start()
     */
    @Override
    public boolean start() {
        if(! getResolver().isStarted())
            return false;
        boolean started =  startConnection();
        if(started) {
            uptime = setUptime();
            log(Level.DEBUG, "Uptime for %s = %ds", this, uptime);
        }
        return started;
    }

    /* (non-Javadoc)
     * @see jrds.Starter#stop()
     */
    @Override
    public void stop() {
        stopConnection();
    }

    public abstract boolean startConnection();
    public abstract void stopConnection();
    /**
     * Return the uptime of the end point of the connexion
     * it's called once after the connexion start
     * It should be in seconds
     * @return
     */
    public abstract long setUptime();

    /**
     * @return the uptime
     */
    public long getUptime() {
        return uptime;
    }

    /* (non-Javadoc)
     * @see jrds.Starter#toString()
     */
    @Override
    public String toString() {
        return getKey() + "@" + getHostName();
    }

}
