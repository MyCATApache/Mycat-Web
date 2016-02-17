package jrds.probe.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import jrds.ProbeDesc;
import jrds.Util;

import org.apache.logging.log4j.Level;

public abstract class Mysql extends JdbcProbe {

    private final static int PORT = 3306;
    static {
        registerDriver(com.mysql.jdbc.Driver.class);
    }

    public Mysql() {
        super();
        this.setPort(PORT);
    }

    public Mysql(ProbeDesc pd) {
        super(pd);
        this.setPort(PORT);
    }

    public void configure(String user, String passwd) {
        super.configure(PORT, user, passwd);
    }

    public void configure(String user, String passwd, String dbName) {
        super.configure(PORT, user, passwd, dbName);
    }

    JdbcStarter setStarter() {
        return new JdbcStarter() {
            @Override
            public boolean start() {
                log(Level.TRACE, "Getting uptime for %s", this);
                boolean started = super.start();
                long uptime = 0;
                if(started) {
                    Statement stmt;
                    try {
                        stmt = getStatment();
                        if(stmt.execute("SHOW STATUS LIKE 'Uptime';")) {
                            ResultSet rs = stmt.getResultSet();
                            while(rs.next()) {
                                String key =  rs.getObject(1).toString();
                                String oValue = rs.getObject(2).toString();
                                if("Uptime".equals(key)) {
                                    uptime = Util.parseStringNumber(oValue, 0L);
                                    break;
                                }
                            }
                        }
                    } catch (SQLException e) {
                        log(Level.ERROR, "SQL exception while getting uptime for " + this);
                    } catch (NumberFormatException ex) {
                        log(Level.ERROR, "Uptime not parsable for " + this);
                    }
                }
                setUptime(uptime);
                log(Level.TRACE, "%s is started: %s", this, started);
                return started;
            }
            public String getUrlAsString() {
                return "jdbc:mysql://" + getHost().getDnsName() + ":" + getPort() + "/" + getDbName();
            }
            @Override
            public Properties getProperties() {
                Properties p = super.getProperties();
                p.put("connectTimeout", 10000);
                p.put("socketTimeout", 10000);
                return p;
            }

        };
    }
}
