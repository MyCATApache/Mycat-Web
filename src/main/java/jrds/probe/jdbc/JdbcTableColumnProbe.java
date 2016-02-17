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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jrds.ProbeConnected;
import jrds.ProbeDesc;
import jrds.ProbeDesc.DsDesc;
import jrds.Util;
import jrds.probe.UrlProbe;

import org.apache.logging.log4j.Level;

public class JdbcTableColumnProbe extends
		ProbeConnected<String, Number, JdbcConnection> implements UrlProbe {
	String query = null;
	String uptimeRow = null;
	String uptimeQuery = null;
	private boolean sumRows;
	// private Set<String> columnAgg;
	private int columnAggSize = 10;

	public JdbcTableColumnProbe() {
		super(JdbcConnection.class.getName());
		rrdDefNeedUpdate = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jrds.ProbeConnected#configure()
	 */
	public Boolean configure(List<? extends Object> args) {
		if (super.configure()) {
			ProbeDesc pd = getPd();
			query = pd.getSpecific("query");
			uptimeQuery = jrds.Util.parseTemplate(
					pd.getSpecific("uptimeQuery"), getHost(), args);
			uptimeRow = jrds.Util.parseTemplate(pd.getSpecific("uptimeRow"),
					getHost(), args);
			setName(jrds.Util.parseTemplate(pd.getProbeName(), args));
			sumRows = "true".equalsIgnoreCase(pd.getSpecific("sumrows"));
			// String columnAggStr = pd.getSpecific("columnsAgg");
			// if (columnAggStr != null && !columnAggStr.isEmpty()) {
			// String[] items = columnAggStr.split(",");
			// columnAgg = new HashSet<String>(items.length);
			// for (String item : items) {
			// columnAgg.add(item);
			// }
			// } else {
			// columnAgg = new HashSet<String>(1);
			// }
			String columnAggSizeStr = pd.getSpecific("columnsAggSize");
			if (columnAggSizeStr != null && !columnAggSizeStr.isEmpty()) {
				columnAggSize = Integer.valueOf(columnAggSizeStr);
			}
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
		return configure((List<? extends Object>) Arrays.asList(args));
	}

	@Override
	public Map<String, Number> getNewSampleValuesConnected(JdbcConnection cnx) {
		Map<String, Number> values = null;
		Statement stmt = cnx.getConnection();
		if (stmt != null && uptimeQuery != null && !"".equals(uptimeQuery)) {
			if (!doUptimeQuery(stmt))
				return null;
		}
		try {
			try {
				log(Level.DEBUG, "sql query used: %s", query);
				if (stmt != null && stmt.execute(query)) {
					ResultSet rs = stmt.getResultSet();
					Set<String> collectKeys = new HashSet<String>(getPd()
							.getCollectStrings().keySet());
					if (uptimeQuery == null && uptimeRow != null)
						collectKeys.add(uptimeRow);
					if (sumRows) {
						values = getValuesFromRSWtihRowSum(rs, collectKeys);
					} else {
						values = getValuesFromRS(rs, collectKeys);
					}

					if (uptimeRow != null && values.containsKey(uptimeRow)) {
						setUptime(values.get(uptimeRow).longValue());
						values.remove(uptimeRow);
					}
				}
			} finally {
				stmt.close();
			}

			return values;
		} catch (SQLException e) {
			log(Level.ERROR, e, "SQL exception while getting values: ",
					e.getMessage());
		}
		return null;
	}

	private boolean doUptimeQuery(Statement stmt) {
		try {
			stmt.execute(uptimeQuery);
			ResultSet rs = stmt.getResultSet();
			Map<String, Number> values = getValuesFromRS(rs,
					Collections.singleton(uptimeRow));
			if (uptimeRow != null && values.containsKey(uptimeRow)) {
				setUptime(values.get(uptimeRow).longValue());
				values.remove(uptimeRow);
			}
			return true;
		} catch (SQLException e) {
			log(Level.ERROR, e, "SQL exception while getting uptime: ",
					e.getMessage());
		}

		return false;
	}

	private Object getValueFromResultSet(ResultSet rs, ResultSetMetaData meta,
			DsDesc dsDesc, int i) throws SQLException {
		Number value = Double.NaN;
		Object oValue = rs.getObject(i);

		if (oValue instanceof Number) {
			value = ((Number) oValue);
		} else {
			int type = meta.getColumnType(i);
			value = Double.NaN;
			switch (type) {
			case Types.DATE:
				value = rs.getDate(i).getTime() / 1000;
				break;
			case Types.TIME:
				value = rs.getTime(i).getTime() / 1000;
				break;
			case Types.LONGNVARCHAR:
			case Types.VARCHAR:
				if (dsDesc.dsType == null) {
					return rs.getString(i);
				} else {
					value = Util.parseStringNumber(rs.getString(i), Double.NaN);
				}

				break;
			case Types.TIMESTAMP:
				value = rs.getTimestamp(i).getTime() / 1000;
				break;
			default:
				if (dsDesc.dsType == null) {
					return rs.getString(i);
				} else {
					value = Util.parseStringNumber(rs.getString(i), Double.NaN);
				}

			}
		}
		return value;
	}

	// private Object getAggValue()
	/**
	 * 
	 * 
	 * @param rs
	 * @param collectKeys
	 * @return
	 */
	private Map<String, Number> getValuesFromRSWtihRowSum(ResultSet rs,
			Set<String> collectKeys) {
		Map<String, Number> values = null;
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			Map<String, DsDesc> dsMap = this.getPd().getDsMap();
			values = new HashMap<String, Number>(dsMap.size());
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					String colLabel = meta.getColumnLabel(i);
					String key = colLabel;
					DsDesc dsDesc = dsMap.get(key);
					if (dsDesc == null) {
						continue;
					}
					Object value = getValueFromResultSet(rs, meta, dsDesc, i);
					if (dsDesc.dsType == null) {
						Map<Object, Integer> theMap = (Map<Object, Integer>) latestCollectionValues
								.get(key);

						if (theMap != null) {
							Integer count = theMap.get(value);
							if (count != null) {
								theMap.put(value, ++count);
							} else {
								theMap.put(value, 1);
							}

						} else {// no map
							theMap = new HashMap<Object, Integer>(
									columnAggSize * 2);
							theMap.put(value, 1);

						}
						enSureMapSize(theMap);
						this.latestCollectionValues.put(key, theMap);
					} else {
						Number curVale = (Number) value;
						Number oldVale = values.get(key);
						if (oldVale == null || oldVale.equals(Double.NaN)
								|| oldVale.equals(Double.NEGATIVE_INFINITY)
								|| oldVale.equals(Double.POSITIVE_INFINITY)) {
							values.put(key, curVale);
						} else if (!curVale.equals(Double.NaN)) {
							double value2 = oldVale.doubleValue()
									+ curVale.doubleValue();
							values.put(key, value2);
						}

					}

				}
			}

		} catch (SQLException e) {
			log(Level.ERROR, e, "SQL exception while getting values: ",
					e.getMessage());
		}
		log(Level.TRACE, "values found: %s", values);
		return values;
	}

	private void enSureMapSize(Map<Object, Integer> theMap) {
		if (theMap.size() <= this.columnAggSize) {
			return;
		}
		TreeSet<SortItem> sortSet = new TreeSet<SortItem>();
		for (Map.Entry<Object, Integer> entry : theMap.entrySet()) {
			sortSet.add(new SortItem(entry.getKey(), entry.getValue()));
		}
		theMap.clear();
		Iterator<SortItem> itor = sortSet.descendingIterator();
		for (int i = 0; i < this.columnAggSize; i++) {
			SortItem item = itor.next();
			theMap.put(item.key, item.times);
		}

	}

	private Map<String, Number> getValuesFromRS(ResultSet rs,
			Set<String> collectKeys) {
		Map<String, Number> values = null;
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			Map<String, DsDesc> dsMap = this.getPd().getDsMap();
			values = new HashMap<String, Number>(dsMap.size());

			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					String colLabel = meta.getColumnLabel(i);
					String key = colLabel;

					DsDesc dsDesc = dsMap.get(key);
					if (dsDesc == null) {
						continue;
					}
					Object value = getValueFromResultSet(rs, meta, dsDesc, i);
					if (dsDesc.dsType == null) {
						this.latestCollectionValues.put(key, value);
						continue;
					} else {
						values.put(key, (Number) value);
					}
				}
			}
		} catch (SQLException e) {
			log(Level.ERROR, e, "SQL exception while getting values: ",
					e.getMessage());
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

}

class SortItem implements Comparable<SortItem> {
	public Object key;
	public Integer times;

	public SortItem(Object key, Integer times) {
		super();
		this.key = key;
		this.times = times;
	}

	@Override
	public int compareTo(SortItem o) {
		return times.compareTo(o.times);
	}

}
