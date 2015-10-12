package jrds.jmx;

public interface ManagementMBean {
    public void reload();
    public int getHostsCount();
    public int getProbesCount();
    public int getGeneration();
}
