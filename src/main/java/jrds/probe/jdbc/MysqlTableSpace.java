package jrds.probe.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * This class needs select privilege, so remember to set up something like that :
 * GRANT USAGE ON *.* TO monitor@'%' IDENTIFIED BY 'password';
 * @author Fabrice Bacchella
 *
 */
public class MysqlTableSpace extends Mysql {

    @Override
    public List<String> getQueries() {
        return Collections.singletonList("show table status from " + getDbName());
    }

    @Override
    public Map<String, Number> parseRs(ResultSet rs) throws SQLException {
        Map<String, Number> retValues = new HashMap<String, Number>(getPd().getSize());
        for(String key: getPd().getCollectStrings().keySet()) {
            retValues.put(key, 0);
        }
        for(Map<String, Object> m: parseRsHorizontaly(rs, false)) {
            for(Map.Entry<String, Object> e: m.entrySet()) {
                Number n = retValues.get(e.getKey());
                //We only keep the data in data stores list
                if(n != null) {
                    if(e.getValue() instanceof String) {
                        double d = jrds.Util.parseStringNumber((String)e.getValue(), 0.0d);
                        n = n.doubleValue() + d;
                    }
                    else if(Number.class.isAssignableFrom(e.getValue().getClass()))
                        n = n.doubleValue() + ((Number) e.getValue()).doubleValue();
                    retValues.put(e.getKey(), n);
                }

            }
        }
        return retValues;
    }
}
