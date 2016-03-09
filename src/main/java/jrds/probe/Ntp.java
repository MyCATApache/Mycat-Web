package jrds.probe;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jrds.Probe;
import jrds.factories.ProbeBean;
import jrds.starter.Resolver;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.NtpV3Packet;
import org.apache.commons.net.ntp.TimeInfo;
import org.apache.logging.log4j.Level;

@ProbeBean({"port"})
public class Ntp extends Probe<String, Number> {
	static final NTPUDPClient client = new NTPUDPClient();
	int port = NTPUDPClient.DEFAULT_PORT;

	public Boolean configure() {
		return true;
	}
	
	public Boolean configure(Integer port) {
		this.port = port;
		return configure();
	}

	/* (non-Javadoc)
	 * @see jrds.Probe#setTimeout(int)
	 */
	@Override
	public void setTimeout(int timeout) {
		super.setTimeout(timeout);
		client.setDefaultTimeout(timeout);
	}

	@Override
	public Map<String, Number> getNewSampleValues() {
		Resolver resolv = find(Resolver.class); 
		if(! resolv.isStarted())
			return null;
		try {
			TimeInfo ti = client.getTime(resolv.getInetAddress(), port);
			ti.computeDetails();
			NtpV3Packet pkct = ti.getMessage();
			Map<String, Number> retValues = new HashMap<String, Number>(4);
			retValues.put("RootDelay", pkct.getRootDelayInMillisDouble());
			retValues.put("RootDispersion", pkct.getRootDispersionInMillisDouble());
			retValues.put("Offset", ti.getOffset());
			retValues.put("Delay", ti.getDelay());
			return retValues;
		} catch (IOException e) {
			log(Level.ERROR, e, "NTP IO exception %s", e);
		}
		return null;
	}

	@Override
	public String getSourceType() {
		return "NTP";
	}
	
	/* (non-Javadoc)
	 * @see jrds.Probe#getUptime()
	 */
	@Override
	public long getUptime() {
		return Long.MAX_VALUE;
	}

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

}
