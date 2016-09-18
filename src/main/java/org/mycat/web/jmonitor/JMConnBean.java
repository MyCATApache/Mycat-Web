package org.mycat.web.jmonitor;

import javax.management.remote.JMXConnector;

public class JMConnBean {

    private JMXConnector connector;
    private String name;
    private String host;
    private String user;
    private String pwd;
    private int port;

    public JMXConnector getConnector() {
        return connector;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConnector(JMXConnector connector) {
        this.connector = connector;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
