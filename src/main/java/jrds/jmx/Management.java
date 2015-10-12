package jrds.jmx;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Collection;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import jrds.Configuration;
import jrds.HostInfo;
import jrds.HostsList;
import jrds.PropertiesManager;

public class Management extends StandardMBean implements ManagementMBean {
    static public final void register(File configfile)  {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("jrds:type=Management");
            mbs.registerMBean(new Management(configfile), name);
        } catch (InstanceAlreadyExistsException e) {
            throw new RuntimeException("jrds mbean failed to register", e);
        } catch (MBeanRegistrationException e) {
            throw new RuntimeException("jrds mbean failed to register", e);
        } catch (NotCompliantMBeanException e) {
            throw new RuntimeException("jrds mbean failed to register", e);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException("jrds mbean failed to register", e);
        } catch (NullPointerException e) {
            throw new RuntimeException("jrds mbean failed to register", e);
        } 
    }

    private final File configfile;

    private Management(File configfile) {
        super(ManagementMBean.class, false);
        this.configfile = configfile;
    }

    @Override
    public void reload() {
        PropertiesManager pm = new PropertiesManager();
        if(configfile.isFile())
            pm.join(configfile);
        pm.importSystemProps();
        Configuration.switchConf(pm);
    }

    @Override
    public int getHostsCount() {
        HostsList hl = Configuration.get().getHostsList();
        Collection<HostInfo> hosts = hl.getHosts();
        return hosts.size();
    }

    @Override
    public int getProbesCount() {
        Configuration c =  Configuration.get();
        HostsList hl = c.getHostsList();
        Collection<HostInfo> hosts = hl.getHosts();
        int numProbes = 0;
        for(HostInfo h: hosts) {
            numProbes += h.getNumProbes();
        }
        return numProbes;
    }

    @Override
    public int getGeneration() {
        return Configuration.get().getHostsList().getGeneration();
    }
}
