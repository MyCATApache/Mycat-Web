package jrds.standalone;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import jrds.HostInfo;
import jrds.HostsList;
import jrds.Probe;
import jrds.PropertiesManager;
import jrds.StoreOpener;

import org.apache.logging.log4j.*;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;

public class Updater {
	static final private Logger logger = LogManager.getLogger(Updater.class);

	public static void main(String[] args) throws IOException {
//		jrds.JrdsLoggerConfiguration.initLog4J();

		PropertiesManager pm = new PropertiesManager(new File("jrds.properties"));
		//jrds.log.JrdsLoggerFactory.setOutputFile(pm.logfile);

		System.getProperties().setProperty("java.awt.headless","true");
		System.getProperties().putAll(pm);
		StoreOpener.prepare(pm.rrdbackend, pm.dbPoolSize);
        HostsList hl =  new HostsList(pm);

		ExecutorService tpool =  Executors.newFixedThreadPool(3);

		for(HostInfo host: hl.getHosts()) {
			for(final Probe<?,?> p: host.getProbes()) {
				final Runnable runUpgrade = new Runnable() {
					private Probe<?,?> lp = p;
					
					public void run() {
						try {
							RrdDef rrdDef = lp.getRrdDef();
							File source = new File(lp.getRrdName());
							File dest = File.createTempFile("JRDS_", ".tmp", source.getParentFile());
							logger.debug("updating " +  source  + " to "  + dest);
							RrdDb rrdSource = StoreOpener.getRrd(source.getCanonicalPath());
							rrdDef.setPath(dest.getCanonicalPath());
							RrdDb rrdDest = new RrdDb(rrdDef);
							rrdSource.copyStateTo(rrdDest);
							rrdDest.close();
							StoreOpener.releaseRrd(rrdSource);
							logger.debug("Size difference : " + (dest.length() - source.length()));
							copyFile(dest.getCanonicalPath(), source.getCanonicalPath());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
				try {
					tpool.execute(runUpgrade);
				}
				catch(RejectedExecutionException ex) {
					logger.debug("collector thread dropped for probe " + p.getName());
				}


			}
		}
		tpool.shutdown();
		try {
			tpool.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.info("Collect interrupted");
		}
		StoreOpener.stop();
	}
	private static void copyFile(String sourcePath, String destPath)
	throws IOException {
		File source = new File(sourcePath);
		File dest = new File(destPath);
		deleteFile(dest);
		if (!source.renameTo(dest)) {
			throw new IOException("Could not create file " + destPath + " from " + sourcePath);
		}
	}
	private static void deleteFile(File file) throws IOException {
		if (file.exists() && !file.delete()) {
			throw new IOException("Could not delete file: " + file.getCanonicalPath());
		}
	}




}
