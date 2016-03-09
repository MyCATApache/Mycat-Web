package jrds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jrds.factories.ProbeMeta;
import jrds.probe.IndexedProbe;
import jrds.probe.UrlProbe;
import jrds.starter.HostStarter;
import jrds.starter.StarterNode;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.*;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.ArcDef;
import org.rrd4j.core.Archive;
import org.rrd4j.core.Datasource;
import org.rrd4j.core.DsDef;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.Header;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A abstract class that needs to be derived for specific probe.<br>
 * the derived class must construct a <code>ProbeDesc</code> and can overid some
 * method as needed
 * 
 * @author Fabrice Bacchella
 */
@ProbeMeta(topStarter = jrds.starter.SocketFactory.class)
public abstract class Probe<KeyType, ValueType> extends StarterNode implements
		Comparable<Probe<KeyType, ValueType>> {
	protected boolean rrdDefNeedUpdate = true;
	private static final ArcDef[] DEFAULTARC = {
			new ArcDef(ConsolFun.AVERAGE, 0.5, 1, 12 * 24 * 30 * 3),
			new ArcDef(ConsolFun.AVERAGE, 0.5, 12, 24 * 365),
			new ArcDef(ConsolFun.AVERAGE, 0.5, 288, 365 * 2) };

	private String name = null;
	protected HostInfo monitoredHost;
	private Collection<GraphNode> graphList = new ArrayList<GraphNode>();
	private ProbeDesc pd;
	private long uptime = Long.MAX_VALUE;
	private String label = null;
	private Logger namedLogger = LogManager.getLogger("jrds.Probe.EmptyProbe");
	private volatile boolean running = false;
	public ConcurrentHashMap<String, Object> latestCollectionValues = new ConcurrentHashMap<String, Object>();

	/**
	 * A special case constructor, mainly used by virtual probe
	 * 
	 * @param pd
	 */
	public Probe(ProbeDesc pd) {
		super();
		setPd(pd);
	}

	public Probe() {
		super();
	}

	public HostInfo getHost() {
		return monitoredHost;
	}

	public void setHost(HostStarter monitoredHost) {
		this.monitoredHost = monitoredHost.getHost();
		setParent(monitoredHost);
	}

	public void setPd(ProbeDesc pd) {
		this.pd = pd;
		namedLogger = LogManager.getLogger("jrds.Probe." + pd.getName());
		if (!readSpecific()) {
			throw new RuntimeException("Creation failed");
		}
	}

	public void addGraph(GraphDesc gd) {
		graphList.add(new GraphNode(this, gd));
	}

	public void addGraph(GraphNode node) {
		graphList.add(node);
	}

	/**
	 * @return Returns the graphList.
	 */
	public Collection<GraphNode> getGraphList() {
		return graphList;
	}

	public String getName() {
		// Name can be set by other means
		if (name == null && pd != null)
			name = parseTemplate(pd.getProbeName());
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRrdName() {
		String rrdName = getName().replaceAll("/", "_");
		return monitoredHost.getHostDir() + Util.getFileSeparator() + rrdName
				+ ".rrd";
	}

	private final String parseTemplate(String template) {
		Object[] arguments = { "${host}", "${index}", "${url}", "${port}",
				"${index.signature}", "${url.signature}" };
		return jrds.Util.parseOldTemplate(template, arguments, this);
	}

	protected DsDef[] getDsDefs() {
		return getPd().getDsDefs();
	}

	public RrdDef getRrdDef() {
		RrdDef def = new RrdDef(getRrdName());
		def.setVersion(2);
		def.addArchive(DEFAULTARC);
		def.addDatasource(getDsDefs());
		def.setStep(getStep());
		return def;
	}

	/**
	 * Create the probe file
	 * 
	 * @throws IOException
	 */
	protected void create() throws IOException {
		log(Level.INFO, "Need to create rrd");
		RrdDef def = getRrdDef();
		RrdDb rrdDb = new RrdDb(def);
		rrdDb.close();
	}

	private void upgrade() {
		RrdDb rrdSource = null;
		try {
			log(Level.WARN,
					"Definition is changed, the store needs to be upgraded");
			File source = new File(getRrdName());
			rrdSource = new RrdDb(source.getCanonicalPath());

			RrdDef rrdDef = getRrdDef();
			File dest = File.createTempFile("JRDS_", ".tmp",
					source.getParentFile());
			rrdDef.setPath(dest.getCanonicalPath());
			RrdDb rrdDest = new RrdDb(rrdDef);

			log(Level.TRACE, "Definition of new  found: %s\n", rrdDest
					.getRrdDef().dump());

			log(Level.DEBUG, "updating %s to %s", source, dest);

			Set<String> badDs = new HashSet<String>();
			Header header = rrdSource.getHeader();
			int dsCount = header.getDsCount();
			;
			header.copyStateTo(rrdDest.getHeader());
			for (int i = 0; i < dsCount; i++) {
				Datasource srcDs = rrdSource.getDatasource(i);
				String dsName = srcDs.getName();
				Datasource dstDS = rrdDest.getDatasource(dsName);
				if (dstDS != null) {
					try {
						srcDs.copyStateTo(dstDS);
						log(Level.TRACE, "Update %s", dsName);
					} catch (RuntimeException e) {
						badDs.add(dsName);
						log(Level.ERROR, e,
								"Datasource %s can't be upgraded: %s", dsName,
								e.getMessage());
					}
				}
			}
			int robinMigrated = 0;
			for (int i = 0; i < rrdSource.getArcCount(); i++) {
				Archive srcArchive = rrdSource.getArchive(i);
				ConsolFun consolFun = srcArchive.getConsolFun();
				int steps = srcArchive.getSteps();
				Archive dstArchive = rrdDest.getArchive(consolFun, steps);
				if (dstArchive != null) {
					if (dstArchive.getConsolFun().equals(
							srcArchive.getConsolFun())
							&& dstArchive.getSteps() == srcArchive.getSteps()) {
						for (int k = 0; k < dsCount; k++) {
							Datasource srcDs = rrdSource.getDatasource(k);
							String dsName = srcDs.getName();
							try {
								int j = rrdDest.getDsIndex(dsName);
								if (j >= 0 && !badDs.contains(dsName)) {
									log(Level.TRACE, "Upgrade of %s from %s",
											dsName, srcArchive);
									srcArchive.getArcState(k).copyStateTo(
											dstArchive.getArcState(j));
									srcArchive.getRobin(k).copyStateTo(
											dstArchive.getRobin(j));
									robinMigrated++;
								}
							} catch (IllegalArgumentException e) {
								log(Level.TRACE, "Datastore %s removed", dsName);
							}

						}
						log(Level.TRACE, "Update %s", srcArchive);
					}
				}
			}
			log(Level.DEBUG, "Robin migrated: %s", robinMigrated);
			rrdDest.close();
			rrdSource.close();
			log(Level.DEBUG, "Size difference : %d",
					(dest.length() - source.length()));
			copyFile(dest.getCanonicalPath(), source.getCanonicalPath());
		} catch (IOException e) {
			log(Level.ERROR, e, "Upgrade failed: %s", e);
		} finally {
			if (rrdSource != null)
				try {
					rrdSource.close();
				} catch (IOException e) {
				}
		}
	}

	private static void copyFile(String sourcePath, String destPath)
			throws IOException {
		File source = new File(sourcePath);
		File dest = new File(destPath);
		File destOld = new File(destPath + ".old.temp");

		if (!dest.renameTo(destOld)) {
			throw new IOException("Could not rename file " + dest + " to "
					+ destOld);
		}
		dest = new File(destPath);
		if (!source.renameTo(dest)) {
			throw new IOException("Could not rename file " + source + " to "
					+ dest);
		}
		deleteFile(destOld);
	}

	private static void deleteFile(File file) throws IOException {
		if (file.exists() && !file.delete()) {
			throw new IOException("Could not delete file: "
					+ file.getCanonicalPath());
		}
	}

	/**
	 * Check the final status of the probe. It must be called once before an
	 * probe can be used
	 * 
	 * Open the rrd backend of the probe. it's created if it's needed
	 * 
	 * @throws IOException
	 * @throws RrdException
	 */
	public void checkStore() {
		if (pd == null) {
			log(Level.ERROR, "Missing Probe description");
		}
		if (monitoredHost == null) {
			log(Level.ERROR, "Missing host");
		}
		checkStoreFile();
	}

	protected boolean checkStoreFile() {
		File rrdFile = new File(getRrdName());

		File rrdDir = monitoredHost.getHostDir();
		if (!rrdDir.isDirectory()) {
			if (!rrdDir.mkdir()) {
				try {
					log(Level.ERROR, "prode dir %s creation failed ",
							rrdDir.getCanonicalPath());
				} catch (IOException e) {
				}
				return false;
			}
		}

		boolean retValue = false;
		RrdDb rrdDb = null;
		try {
			if (rrdFile.isFile()) {
				rrdDb = new RrdDb(getRrdName());
				// old definition
				RrdDef tmpdef = rrdDb.getRrdDef();
				Date startTime = new Date();
				tmpdef.setStartTime(startTime);
				String oldDef = tmpdef.dump();
				long oldstep = tmpdef.getStep();
				log(Level.TRACE, "Definition found: %s\n", oldDef);

				// new definition
				tmpdef = getRrdDef();
				tmpdef.setStartTime(startTime);
				String newDef = tmpdef.dump();
				long newstep = tmpdef.getStep();

				if (newstep != oldstep) {
					log(Level.ERROR, "step changed, you're in trouble");
					return false;
				} else if (!newDef.equals(oldDef)) {

					rrdDb.close();
					rrdDb = null;
					upgrade();
					rrdDb = new RrdDb(getRrdName());
				}
				log(Level.TRACE, "******");
			} else
				create();
			retValue = true;
		} catch (Exception e) {
			log(Level.ERROR, e, "Store %s unusable: %s", getRrdName(), e);
		} finally {
			if (rrdDb != null)
				try {
					rrdDb.close();
				} catch (IOException e) {
				}

		}
		return retValue;
	}

	/**
	 * The method that return a map of data collected.<br>
	 * It should return return as raw as possible, they can even be opaque data
	 * tied to the probe. the key is resolved using the <code>ProbeDesc</code>.
	 * A key not associated with an existent datastore will generate a warning
	 * but will not prevent the other values to be stored.<br>
	 * 
	 * @return the map of collected object or null if the collect failed
	 */
	public abstract Map<KeyType, ValueType> getNewSampleValues();

	/**
	 * This method convert the collected object to numbers and can do post
	 * treatment
	 * 
	 * @param valuesList
	 * @return an map of value to be stored
	 */
	@SuppressWarnings("unchecked")
	public Map<KeyType, Number> filterValues(Map<KeyType, ValueType> valuesList) {
		return (Map<KeyType, Number>) valuesList;
	}

	/**
	 * This method take two unsigned 32 integers and return a signed 64 bits
	 * long The input value me be stored in a Long object
	 * 
	 * @param high
	 *            high bits of the value
	 * @param low
	 *            low bits of the value
	 * @return
	 */
	private Long joinCounter32(ValueType high, ValueType low) {
		if (high instanceof Long && low instanceof Long) {
			long highnum = ((Number) high).longValue();
			long lownum = ((Number) low).longValue();
			return (highnum << 32) + lownum;
		}
		return null;
	}

	/**
	 * The sample itself can be modified<br>
	 * 
	 * @param oneSample
	 * @param values
	 */
	public void modifySample(Sample oneSample, Map<KeyType, ValueType> values) {
		for (Map.Entry<String, ProbeDesc.Joined> e : getPd()
				.getHighlowcollectmap().entrySet()) {
			Long joined = joinCounter32(values.get(e.getValue().keyhigh),
					values.get(e.getValue().keylow));
			if (joined != null) {
				oneSample.setValue(e.getKey(), joined.doubleValue());

			}
		}
	}

	@SuppressWarnings("unchecked")
	public Map<KeyType, String> getCollectMapping() {
		Map<KeyType, String> rawMap = (Map<KeyType, String>) getPd()
				.getCollectMapping();
		Map<KeyType, String> retValues = new HashMap<KeyType, String>(
				rawMap.size());
		for (Map.Entry<KeyType, String> e : rawMap.entrySet()) {
			String value = jrds.Util.parseTemplate(e.getValue(), this);
			KeyType key = e.getKey();
			if (key instanceof String)
				key = (KeyType) jrds.Util.parseTemplate((String) key, this);
			retValues.put(key, value);
		}
		return retValues;
	}

	/**
	 * Store the values on the rrd backend.
	 * 
	 * @param oneSample
	 */
	protected boolean updateSample(Sample oneSample,
			Map<KeyType, ValueType> sampleVals) {
		if (isCollectRunning()) {
			if (sampleVals != null) {
				log(Level.TRACE, "Collected values: %s", sampleVals);
				if (getUptime() * pd.getUptimefactor() >= pd
						.getHeartBeatDefault()) {
					// Set the default values that might be defined in the probe
					// description
					for (Map.Entry<String, Double> e : getPd()
							.getDefaultValues().entrySet()) {
						oneSample.setValue(e.getKey(), e.getValue());
					}
					Map<?, String> nameMap = getCollectMapping();
					log(Level.TRACE, "Collect keys: %s", nameMap);
					Map<KeyType, Number> filteredSamples = filterValues(sampleVals);

					log(Level.TRACE, "Filtered values: %s", filteredSamples);
					for (Map.Entry<KeyType, Number> e : filteredSamples
							.entrySet()) {
						String dsName = nameMap.get(e.getKey());
						double value = e.getValue().doubleValue();
						if (dsName != null) {
							oneSample.setValue(dsName, value);
						} else {
							log(Level.TRACE, "Dropped entry: %s", e.getKey());
						}
					}
					modifySample(oneSample, sampleVals);
					return true;
				} else {
					log(Level.INFO, "uptime too low: %f",
							getUptime() * pd.getUptimefactor());
					
				}
				return true;	
			}
		}
		return false;
	}

	/**
	 * Launch an collect of values. You should not try to override it
	 */
	public void collect() {
		long start = System.currentTimeMillis();
		boolean interrupted = true;

		if (running) {
			log(Level.ERROR, "Hanged from a previous collect");
			return;
		}
		startCollect();
		// We only collect if the HostsList allow it
		if (isCollectRunning()) {
			running = true;
			log(Level.DEBUG, "launching collect");
			RrdDb rrdDb = null;
			try {
				// No collect if the thread was interrupted
				if (isCollectRunning()) {

					Map<KeyType, ValueType> newSampleVals = getNewSampleValues();
					if (rrdDefNeedUpdate) {// update rrd define
						this.checkStore();
						rrdDefNeedUpdate = false;
					}
					rrdDb = StoreOpener.getRrd(getRrdName());
					Sample onesample = rrdDb.createSample();
					onesample.getTime();
					boolean updated = updateSample(onesample, newSampleVals);
					// The collect might have been stopped
					// during the reading of samples
					if (updated && isCollectRunning()) {
						if (namedLogger.isDebugEnabled())
							log(Level.DEBUG, "sample %s", onesample.dump());
						onesample.update();
						String[] dsNames = rrdDb.getDsNames();
						for (int i = 0; i < dsNames.length; i++) {
							latestCollectionValues.put(dsNames[i], rrdDb
									.getDatasource(i).getLastValue());
						}
						latestCollectionValues.put("JRDS_LAST_COLLECT", System.currentTimeMillis()/1000);
						interrupted = false;
					}
				}
			} catch (ArithmeticException ex) {
//				ex.printStackTrace();
				log(Level.WARN, ex, "Error while storing sample: %s",
						ex.getMessage());
			} catch (Exception e) {
				Throwable rootCause = e;
				Throwable upCause;
				StringBuilder message = new StringBuilder();
				do {
					String cause = rootCause.getMessage();
					if (cause == null || "".equals(cause)) {
						message.append(": ").append(rootCause.toString());
					} else {
						message.append(": ").append(cause);
					}
					upCause = rootCause.getCause();
					if (upCause != null)
						rootCause = upCause;
				} while (upCause != null);
				log(Level.ERROR, e, "Error while collecting: %s", message);
			} finally {
				if (rrdDb != null)
					StoreOpener.releaseRrd(rrdDb);
				stopCollect();
			}
			if (interrupted) {
				long end = System.currentTimeMillis();
				float elapsed = ((float) (end - start)) / 1000;
				log(Level.DEBUG, "Interrupted after %.2fs", elapsed);
			}
			running = false;
		}
	}

	/**
	 * Return the string value of the probe as a path constitued of the host
	 * name / the probe name
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String hn = "<empty>";
		if (getHost() != null)
			hn = getHost().getName();
		return hn + "/" + getName();
	}

	/**
	 * The comparaison order of two object of the class is a case insensitive
	 * comparaison of it's string value.
	 * 
	 * @param arg0
	 *            Object
	 * @return int
	 */
	public int compareTo(Probe<KeyType, ValueType> arg0) {
		return String.CASE_INSENSITIVE_ORDER.compare(toString(),
				arg0.toString());
	}

	/**
	 * @return Returns the <code>ProbeDesc</code> of the probe.
	 */
	public ProbeDesc getPd() {
		return pd;
	}

	/**
	 * Return the date of the last update of the rrd backend
	 * 
	 * @return The date
	 */
	public Date getLastUpdate() {
		Date lastUpdate = null;
		RrdDb rrdDb = null;
		try {
			rrdDb = StoreOpener.getRrd(getRrdName());
			lastUpdate = Util.getDate(rrdDb.getLastUpdateTime());
		} catch (Exception e) {
			throw new RuntimeException("Unable to get last update date for "
					+ getQualifiedName(), e);
		} finally {
			if (rrdDb != null)
				StoreOpener.releaseRrd(rrdDb);
		}
		return lastUpdate;
	}

	public boolean dsExist(String dsName) {
		return pd.dsExist(dsName);
	}

	/**
	 * Return the probe data for the given period
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public FetchData fetchData(Date startDate, Date endDate) {
		return fetchData(startDate.getTime() / 1000, endDate.getTime() / 1000);
	}

	/**
	 * Return the probe data for the given period
	 * 
	 * @param fetchStart
	 *            Starting timestamp for fetch request.
	 * @param fetchEnd
	 *            Ending timestamp for fetch request.
	 * @return Request object that should be used to actually fetch data from
	 *         RRD
	 */
	public FetchData fetchData(long fetchStart, long fetchEnd) {
		return fetchData(ConsolFun.AVERAGE, fetchStart, fetchEnd, 1);
	}

	/**
	 * Return the probe data for the given period
	 * 
	 * @param consolFun
	 *            Consolidation function to be used in fetch request. Allowed
	 *            values are "AVERAGE", "MIN", "MAX" and "LAST" (these constants
	 *            are conveniently defined in the {@link ConsolFun} class).
	 * @param fetchStart
	 *            Starting timestamp for fetch request.
	 * @param fetchEnd
	 *            Ending timestamp for fetch request.
	 * @param resolution
	 *            Fetch resolution.
	 * @return Request object that should be used to actually fetch data from
	 *         RRD
	 */
	public FetchData fetchData(ConsolFun consolFun, long fetchStart,
			long fetchEnd, long resolution) {
		FetchData retValue = null;
		RrdDb rrdDb = null;
		try {
			rrdDb = StoreOpener.getRrd(getRrdName());
			FetchRequest fr = rrdDb.createFetchRequest(consolFun, fetchStart,
					fetchEnd, resolution);
			retValue = fr.fetchData();
		} catch (Exception e) {
			log(Level.ERROR, e, "Unable to fetch data: %s", e.getMessage());
		} finally {
			if (rrdDb != null)
				StoreOpener.releaseRrd(rrdDb);
		}
		return retValue;
	}

	public Map<String, Number> getLastValues() {
		Map<String, Number> retValues = new HashMap<String, Number>();
		RrdDb rrdDb = null;
		try {
			rrdDb = StoreOpener.getRrd(getRrdName());
			String[] dsNames = rrdDb.getDsNames();
			for (int i = 0; i < dsNames.length; i++) {
				retValues
						.put(dsNames[i], rrdDb.getDatasource(i).getLastValue());
			}
		} catch (Exception e) {
			log(Level.ERROR, e, "Unable to get last values: %s", e.getMessage());
		} finally {
			if (rrdDb != null)
				StoreOpener.releaseRrd(rrdDb);
		}
		return retValues;
	}

	/**
	 * Return a unique name for the graph
	 * 
	 * @return
	 */
	public String getQualifiedName() {
		return getHost().getName() + "/" + getName();
	}

	public int hashCode() {
		return getQualifiedName().hashCode();
	}

	public Set<String> getTags() {
		return getHost().getTags();
	}

	public abstract String getSourceType();

	/**
	 * This function it used by the probe to read all the specific it needs from
	 * the probe description It's called once during the probe initialization
	 * Every override should finish by: return super();
	 * 
	 * @return
	 */
	public boolean readSpecific() {
		return true;
	}

	/**
	 * A probe can override it to extract custom values from the properties. It
	 * will be read just after it's created and before configuration.
	 * 
	 * @param pm
	 */
	public void readProperties(PropertiesManager pm) {

	}

	/**
	 * This function should return the uptime of the probe If it's not overriden
	 * or fixed with setUptime, it will return Long.MAX_VALUE that's make it
	 * useless, as it used to make the probe pause after a restart of the probe.
	 * It's called after filterValues
	 * 
	 * @return the uptime in second
	 */
	public long getUptime() {
		return uptime;
	}

	/**
	 * Define the uptime of the probe
	 * 
	 * @param uptime
	 *            in seconds
	 */
	public void setUptime(long uptime) {
		log(Level.TRACE, "Setting probe uptime to: %d", uptime);
		this.uptime = uptime;
	}

	public Document dumpAsXml() throws ParserConfigurationException,
			IOException {
		return dumpAsXml(false);
	}

	public Document dumpAsXml(boolean sorted)
			throws ParserConfigurationException, IOException {
		String probeName = getPd().getName();
		String name = getName();
		String host = "";
		if (getHost() != null)
			host = getHost().getName();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();
		Element root = (Element) document.createElement("probe");
		document.appendChild(root);
		root.setAttribute("name", name);
		root.setAttribute("host", host);
		Element probeNameElement = document.createElement("probeName");
		probeNameElement.appendChild(document.createTextNode(probeName));

		root.appendChild(probeNameElement);
		if (this instanceof UrlProbe) {
			Element urlElement = document.createElement("url");
			String url = ((UrlProbe) this).getUrlAsString();
			urlElement.appendChild(document.createTextNode(url));
			root.appendChild(urlElement);
		}
		if (this instanceof IndexedProbe) {
			Element urlElement = document.createElement("index");
			String index = ((IndexedProbe) this).getIndexName();
			urlElement.appendChild(document.createTextNode(index));
			root.appendChild(urlElement);
		}
		Element dsElement = document.createElement("ds");
		root.appendChild(dsElement);

		Element graphs = (Element) root.appendChild(document
				.createElement("graphs"));
		for (GraphNode gn : this.graphList) {
			String qualifiedGraphName = gn.getQualifiedName();
			Element graph = (Element) graphs.appendChild(document
					.createElement("graphname"));
			graph.setTextContent(qualifiedGraphName);
			graph.setAttribute("id", String.valueOf(gn.hashCode()));
		}
		DsDef[] dss = getDsDefs();

		if (sorted)
			Arrays.sort(dss, new Comparator<DsDef>() {
				public int compare(DsDef arg0, DsDef arg1) {
					return String.CASE_INSENSITIVE_ORDER.compare(
							arg0.getDsName(), arg1.getDsName());
				}
			});

		for (DsDef ds : dss) {
			String dsName = ds.getDsName();

			Element dsNameElement = document.createElement("name");

			dsNameElement.setAttribute("pid", String.valueOf(hashCode()));
			dsNameElement.setAttribute("dsName", dsName);
			dsNameElement.appendChild(document.createTextNode(dsName));
			dsElement.appendChild(dsNameElement);
		}
		return document;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void log(Level l, Throwable e, String format, Object... elements) {
		jrds.Util.log(this, namedLogger, l, e, format, elements);
	}

	public void log(Level l, String format, Object... elements) {
		jrds.Util.log(this, namedLogger, l, null, format, elements);
	}

	/**
	 * @return the namedLogger
	 */
	public Logger getNamedLogger() {
		return namedLogger;
	}

}
