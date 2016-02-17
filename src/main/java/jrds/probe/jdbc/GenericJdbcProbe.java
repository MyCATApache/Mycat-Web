package jrds.probe.jdbc;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;




import org.apache.logging.log4j.Level;

import jrds.ProbeConnected;
import jrds.ProbeDesc;
import jrds.Util;
import jrds.probe.IndexedProbe;
import jrds.probe.UrlProbe;

public class GenericJdbcProbe extends ProbeConnected<String, Number, JdbcConnection> implements UrlProbe, IndexedProbe {
    String query = null;
    String keyColumn = null;
    String index = "";
    String uptimeRow = null;
    String uptimeQuery = null;

    public GenericJdbcProbe() {
        super(JdbcConnection.class.getName());
    }

    /* (non-Javadoc)
     * @see jrds.ProbeConnected#configure()
     */
    public Boolean configure(List<? extends Object> args) {
        if(super.configure()) {
            ProbeDesc pd =  getPd();
            query = jrds.Util.parseTemplate(pd.getSpecific("query"), getHost(), args);
            keyColumn = jrds.Util.parseTemplate(pd.getSpecific("key"), getHost(), args);
            uptimeQuery = jrds.Util.parseTemplate(pd.getSpecific("uptimeQuery"), getHost(), args);
            uptimeRow = jrds.Util.parseTemplate(pd.getSpecific("uptimeRow"), getHost(), args);
            String indexTemplate = pd.getSpecific("index");
            if(indexTemplate != null && ! "".equals(indexTemplate))
                index = jrds.Util.parseTemplate(indexTemplate, getHost(), args);
            setName(jrds.Util.parseTemplate(pd.getProbeName(), args));
            return true;
        }
        return false;
    }

    public Boolean configure() {
        return configure(Collections.emptyList());
    }

    public Boolean configure(String args) {
        return configure(Collections.singletonList(args));
    }

    public Boolean configure(String... args) {
        return configure((List<? extends Object>)Arrays.asList(args));
    }

    @Override
    public Map<String, Number> getNewSampleValuesConnected(JdbcConnection cnx) {
        Map<String, Number> values = null;
        Statement stmt = cnx.getConnection();
        if(stmt != null && uptimeQuery != null && ! "".equals(uptimeQuery)) {
            if( ! doUptimeQuery(stmt))
                return null;
        }
        try {
            try {
                log(Level.DEBUG, "sql query used: %s", query);
                if(stmt != null && stmt.execute(query)) {
                    ResultSet rs = stmt.getResultSet();
                    Set<String> collectKeys = new HashSet<String>(getPd().getCollectStrings().keySet());
                    if(uptimeQuery == null && uptimeRow != null)
                        collectKeys.add(uptimeRow);
                    values = getValuesFromRS(rs, collectKeys);
                    if(uptimeRow != null && values.containsKey(uptimeRow)) {
                        setUptime(values.get(uptimeRow).longValue());
                        values.remove(uptimeRow);
                    }
                }
            }
            finally {
                stmt.close();	
            }
            return values;
        } catch (SQLException e) {
            log(Level.ERROR, e, "SQL exception while getting values: ", e.getMessage());
        }
        return null;
    }

    private boolean doUptimeQuery(Statement stmt) {
        try {
            stmt.execute(uptimeQuery);
            ResultSet rs = stmt.getResultSet();
            Map<String, Number> values = getValuesFromRS(rs, Collections.singleton(uptimeRow));
            if(uptimeRow != null && values.containsKey(uptimeRow)) {
                setUptime(values.get(uptimeRow).longValue());
                values.remove(uptimeRow);
            }
            return true;
        } catch (SQLException e) {
            log(Level.ERROR, e, "SQL exception while getting uptime: ", e.getMessage());
        }

        return false;
    }

    private Map<String, Number> getValuesFromRS(ResultSet rs, Set<String> collectKeys) {
        Map<String, Number> values = null;
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            values = new HashMap<String, Number>(columnCount);
            while(rs.next()) {
                String keyValue = "";
                if(keyColumn != null) {
                    keyValue = rs.getString(keyColumn) + ".";
                    log(Level.TRACE, "found a row with key %s", rs.getString(keyColumn));
                }

                for(int i = 1; i <= columnCount ; i++) {
                    String key = keyValue + meta.getColumnLabel(i);
                    if(! collectKeys.contains(key))
                        continue;
                    Number value = Double.NaN;
                    Object oValue = rs.getObject(i);
                    log(Level.TRACE, "type info for %s: type %d, %s = %s",  key, meta.getColumnType(i), oValue.getClass(), oValue.toString());
                    if(oValue instanceof Number) {
                        value = ((Number) oValue);
                        values.put(key, value);
                    }
                    else {
                        int type = meta.getColumnType(i);
                        value = Double.NaN;
                        switch(type) {
                        case Types.DATE: value = rs.getDate(i).getTime() / 1000; break;
                        case Types.TIME: value = rs.getTime(i).getTime() / 1000; break;
                        case Types.LONGVARCHAR:
                        case Types.VARCHAR: value = Util.parseStringNumber(rs.getString(i), Double.NaN); break;
                        case Types.TIMESTAMP: value = rs.getTimestamp(i).getTime() / 1000; break;
                        }
                        values.put(key, value);
                    }
                }
            }
        } catch (SQLException e) {
            log(Level.ERROR, e, "SQL exception while getting values: ", e.getMessage());
        }
        log(Level.TRACE, "values found: %s", values);
        return values;
    }

    @Override
    public String getSourceType() {
        return "JDBC";
    }

    public Integer getPort() {
        return 0;
    }

    public URL getUrl() {
        URL newurl = null;
        try {
            newurl = new URL(getUrlAsString());
        } catch (MalformedURLException e) {
            log(Level.ERROR, e, "Invalid jdbc url: " + getUrlAsString());
        }
        return newurl;
    }

    public String getUrlAsString() {
        return getConnection().getUrl();
    }

    public String getIndexName() {
        return index;
    }

}
