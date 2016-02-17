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

import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.ProbeConnected;
import jrds.ProbeDesc;
import jrds.Util;
import jrds.probe.UrlProbe;

import org.apache.logging.log4j.Level;

public class PowerJdbcProbe extends
		ProbeConnected<String, Number, JdbcConnection> implements UrlProbe {
	String query = null;
	String uptimeRow = null;
	String uptimeQuery = null;
	DsNameValuesInf dsNameValuesInf;

	public PowerJdbcProbe() {
		super(JdbcConnection.class.getName());
		rrdDefNeedUpdate = false;
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
			String keyAndValuePairs = pd.getSpecific("keyAndValuePairs");
			String[] pairs = keyAndValuePairs.split(";");
			dsNameValuesInf = new DsNameValuesInf();
			dsNameValuesInf.keyName = pairs[0];
			for (int i = 1; i < pairs.length; i++) {
				dsNameValuesInf.voluesNameSet.add(pairs[i]);

			}
			uptimeQuery = jrds.Util.parseTemplate(
					pd.getSpecific("uptimeQuery"), getHost(), args);
			uptimeRow = jrds.Util.parseTemplate(pd.getSpecific("uptimeRow"),
					getHost(), args);
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
					values = getValuesFromRS(rs, collectKeys);

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

	private Map<String, Number> getValuesFromRS(ResultSet rs,
			Set<String> collectKeys) {
		Map<String, Number> values = null;
		try {
			ResultSetMetaData meta = rs.getMetaData();
			int columnCount = meta.getColumnCount();
			values = new HashMap<String, Number>(columnCount);
			while (rs.next()) {
				String keyValue = rs.getString(dsNameValuesInf.keyName);
				log(Level.TRACE, "found a row with key %s", keyValue);

				for (int i = 1; i <= columnCount; i++) {
					String colLabel = meta.getColumnLabel(i);
					if (!dsNameValuesInf.containsValueColum(colLabel))
						continue;

					Number value = Double.NaN;
					Object oValue = rs.getObject(i);
					String key = keyValue + "." + colLabel;
					try {
						createDsAndGrahpDescIfNeeded(key, keyValue,
								dsNameValuesInf.voluesNameSet, colLabel);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (oValue instanceof Number) {
						value = ((Number) oValue);
						values.put(key, value);
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
							value = Util.parseStringNumber(rs.getString(i),
									Double.NaN);
							break;
						case Types.TIMESTAMP:
							value = rs.getTimestamp(i).getTime() / 1000;
							break;
						}
						values.put(key, value);
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

	private void createDsAndGrahpDescIfNeeded(String dsName,
			String rowKeyValue, Set<String> dsValueColumns,
			String curdsValueColumn) throws Exception {
		ProbeDesc pd = getPd();
		if (!pd.dsExist(dsName)) {
			if (rrdDefNeedUpdate==false) {
				rrdDefNeedUpdate = true;
			}
			log(Level.INFO, " add ds " + dsName);
			ProbeDesc.DsDesc templateDsDesc = pd.getDsMap().get(
					curdsValueColumn);
			// 复制一个模板
			ProbeDesc.DsDesc cpDsDef = (ProbeDesc.DsDesc) templateDsDesc
					.clone();
			cpDsDef.collectKey = dsName;
			pd.getDsMap().put(dsName, cpDsDef);
			// 对每一个GraphDefine，判断是否需要生成对应的实例
			for (String graphCls : pd.getGraphClasses()) {
				boolean foundGraphInst = false;
				String newGraphName = graphCls + "_" + dsName;
				GraphDesc templateGD = null;
				GraphDesc newgd = null;
				for (GraphNode gn : this.getGraphList()) {
					String curClassName = gn.getGraphDesc().getName();
					if (curClassName.equals(graphCls)) {
						templateGD = gn.getGraphDesc();
					}

					if (gn.getName().equals(newGraphName)) {
						foundGraphInst = true;
						break;
					}
				}
				if (!foundGraphInst) {
					boolean foundDS = false;
					// 若此模板中存在 当前 curdsValueColumn的 相关定义，则需要实例化此图
					for (GraphDesc.DsDesc dsDesc : templateGD.getAllds()) {
						if (dsDesc.getDsName().equalsIgnoreCase(
								curdsValueColumn)) {
							foundDS = true;
							break;
						}
					}
					if (!foundDS) {
						continue;
					}
					try {

						newgd = (GraphDesc) templateGD.clone();
						newgd.setName(newGraphName);
						newgd.setGraphName(newGraphName);
						newgd.setGraphTitle(newGraphName);
						for (GraphDesc.DsDesc dsDesc : newgd.getAllds()) {
							if (dsValueColumns.contains(dsDesc.getDsName())) {
								dsDesc.chagneDsName(rowKeyValue + "."
										+ dsDesc.getDsName());
							}
						}
						GraphNode newGn = new GraphNode(this, newgd);
						this.addGraph(newGn);
						log(Level.INFO, "addgraph instance for dsname %s",
								dsName + ",graph " + newGn);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}

				}

			}

		}

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

class DsNameValuesInf {
	public String keyName;
	public Set<String> voluesNameSet = new HashSet<String>();

	public boolean containsValueColum(String colum) {
		return voluesNameSet.contains(colum);
	}

}
