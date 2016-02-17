package jrds.probe;

import java.io.IOException;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import jrds.factories.ProbeBean;
import jrds.starter.Connection;

import org.apache.logging.log4j.Level;

@ProbeBean({"url", "protocol", "port", "path", "user", "password"})
public class JMXConnection extends Connection<MBeanServerConnection> {
    private static enum PROTOCOL {
        rmi {
            @Override
            public JMXServiceURL getURL(JMXConnection cnx) throws MalformedURLException {
                return new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + cnx.getHostName() + ":" + cnx.port + cnx.path);
            }
        },
        iiop {
            @Override
            public JMXServiceURL getURL(JMXConnection cnx) throws MalformedURLException {
                return new JMXServiceURL("service:jmx:iiop:///jndi/iiop://" + cnx.getHostName() + ":" + cnx.port + cnx.path);
            }
        },
        jmxmp {
            @Override
            public JMXServiceURL getURL(JMXConnection cnx) throws MalformedURLException {
                return new JMXServiceURL("service:jmx:jmxmp://" + cnx.getHostName() + ":" + cnx.port);
            }

        };
        abstract public JMXServiceURL getURL(JMXConnection cnx)  throws MalformedURLException ;
    };

    final static String startTimeObjectName = "java.lang:type=Runtime";
    final static String startTimeAttribue = "Uptime";

    private JMXServiceURL url = null;
    private PROTOCOL protocol  = PROTOCOL.rmi;
    private int port;
    private String path = "/jmxrmi";
    private String user = null;
    private String password = null;

    private JMXConnector connector;
    private MBeanServerConnection connection;

    public JMXConnection() {
        super();
    }

    public JMXConnection(Integer port) {
        super();
        this.port = port;
    }

    public JMXConnection(Integer port, String user, String password) {
        super();
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    public MBeanServerConnection getConnection() {
        return connection;
    }
    
    /**
     * Resolve a mbean interface, given the interface and it's name
     * @param name
     * @param interfaceClass
     * @return
     */
    public <T> T getMBean(String name,  Class<T> interfaceClass) {
        MBeanServerConnection mbsc = getConnection();
        try {
            ObjectName mbeanName = new ObjectName(name);
            return javax.management.JMX.newMBeanProxy(mbsc, mbeanName, 
                    interfaceClass, true);        
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("wrong mbean name: " + name, e);
        }
    }

    @Override
    public long setUptime() {
        try {
            RuntimeMXBean mxbean = getMBean(startTimeObjectName, RuntimeMXBean.class);
            if (mxbean != null)
                return mxbean.getUptime() /1000;
        } catch (Exception e) {
            log(Level.ERROR, e, "Uptime error for %s: %s", this, e);
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see jrds.Starter#start()
     */
    @Override
    public boolean startConnection() {
        try {
            if (url == null) {
                url = protocol.getURL(this);
            }
            log(Level.TRACE, "connecting to %s", url);
            Map<String, Object> attributes = null;
            if(user != null && password != null ) {
                String[] credentials = new String[]{user, password};
                attributes = new HashMap<String, Object>();
                attributes.put("jmx.remote.credentials", credentials);
            }
            connector = JMXConnectorFactory.connect(url, attributes);
            connection = connector.getMBeanServerConnection();
            log(Level.DEBUG, "connected to %s", connection);
            return true;
        } catch (MalformedURLException e) {
            log(Level.ERROR, e, "Invalid jmx URL %s: %s", protocol.toString(), e);
        } catch (IOException e) {
            log(Level.ERROR, e, "Communication error with %s: %s", protocol.toString(), e);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see jrds.Starter#stop()
     */
    @Override
    public void stopConnection() {
        try {
            connector.close();
        } catch (IOException e) {
            log(Level.ERROR, e, "JMXConnector to %s close failed because of: %s", this, e );
        }
        connection = null;
    }

    /* (non-Javadoc)
     * @see jrds.Starter#toString()
     */
    @Override
    public String toString() {
        if (url == null) {
            try {
                return protocol.getURL(this).toString();
            } catch (MalformedURLException e) {
                return "";
            }
        }
        else {
            return url.toString();
        }
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol.name();
    }

    /**
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = PROTOCOL.valueOf(protocol.trim().toLowerCase());
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url.toString();
    }

    /**
     * @param url the url to set
     * @throws MalformedURLException 
     */
    public void setUrl(String url) throws MalformedURLException {
        this.url = new JMXServiceURL(url);
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

}
