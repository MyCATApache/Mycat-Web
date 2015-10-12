package jrds.probe.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jrds.Util;

/**
 * 
 * This class needs select privilege, so remember to set up something like that
 * : GRANT USAGE ON *.* TO monitor@'%' IDENTIFIED BY 'password';
 * 
 * @author Fabrice Bacchella
 * 
 */
public class MysqlStatus extends Mysql {
	public void configure(int port, String user, String passwd) {
		super.configure(port, user, passwd);
	}

	public void configure(String user, String passwd) {
		super.configure(user, passwd);
	}

	@Override
	public List<String> getQueries() {
		return Collections.singletonList("SHOW /*!50002 GLOBAL */ STATUS");
	}

	@Override
	public Map<String, Number> parseRs(ResultSet rs) throws SQLException {
		Map<String, Number> retValues = new HashMap<String, Number>(getPd()
				.getSize());
		Set<String> toCollect = getPd().getCollectStrings().keySet();
		for (Map<String, Object> m : parseRsVerticaly(rs, false)) {
			for (Map.Entry<String, Object> e : m.entrySet()) {
				Double d = Double.NaN;
				// We only keep the data in data stores list
				if (toCollect.contains(e.getKey())) {
					if (e.getValue() instanceof String)
						d = Util.parseStringNumber((String) e.getValue(),
								Double.NaN).doubleValue();
					retValues.put(e.getKey(), d);
				}
			}
		}
		Number qCacheHits = retValues.get("Qcache_hits");
		Number qcache_inserts = retValues.get("Qcache_inserts");
		double result = 0;
		if (qCacheHits != null && qcache_inserts != null
				&& qcache_inserts.longValue() != 0) {
			result = ((qCacheHits.longValue() + 0.0) / qcache_inserts
					.longValue()) * 100;

		}
		retValues.put("Qcache_Usage", result);
		System.out.println("Qcache_Usage :" + result);
		return retValues;
	}
}
