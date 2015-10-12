package jrds.probe.jdbc;

import java.math.BigDecimal;
import java.util.Map;

public class BestMysqlStatus extends GenericJdbcProbe {
	@Override
	public Map<String, Number> getNewSampleValuesConnected(JdbcConnection cnx) {
		Map<String, Number> retValues = super.getNewSampleValuesConnected(cnx);
		Number qCacheHits = retValues.get("Qcache_hits");
		Number qcache_inserts = retValues.get("Qcache_inserts");
		Number qcache_not_cached = retValues.get("Qcache_not_cached");
		// Qcache_hits*100.0/(Qcache_hits+Qcache_inserts+Qcache_not_cached)
		double result = 0;
		if (qCacheHits != null && qcache_inserts != null
				&& qcache_not_cached != null && qCacheHits.longValue() != 0) {
			result = (qCacheHits.longValue() * 100.0)
					/ (qCacheHits.longValue() + qcache_inserts.longValue() + qcache_not_cached
							.longValue());
			BigDecimal b = new BigDecimal(result);
			result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		}
		retValues.put("Qcache_Usage", result);
		// System.out.println("Qcache_Usage :" + result);
		return retValues;

	}
}
