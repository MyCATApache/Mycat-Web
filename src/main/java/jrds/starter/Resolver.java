package jrds.starter;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jrds.HostInfo;
import jrds.Probe;

import org.apache.logging.log4j.Level;

public class Resolver extends Starter {
	String hostname = "";
	InetAddress address = null;

	public Resolver(String hostname) {
		this.hostname = hostname;
        log(Level.DEBUG, "New dns resolver");
	}

	@Override
	public boolean start() {
		boolean started = false;
		try {
			address = InetAddress.getByName(hostname);
			started = true;
		} catch (UnknownHostException e) {
			log(Level.ERROR, e,  "DNS host name %s can't be found", hostname);
		}
		return started;
	}
	
	@Override
	public void stop() {
		address = null;
	}

	public InetAddress getInetAddress() {
		return address;
	}
	
	@Override
	public Object getKey() {
		return "resolver:" + hostname;
	}

	public static Object makeKey(StarterNode node) {
		HostInfo host = null;
		if(node instanceof HostStarter)
			host = ((HostStarter)node).getHost();
		else if(node instanceof Probe<?,?>) {
			Probe<?,?> p = (Probe<?,?>) node;
			host = p.getHost();
		}
		else {
			return null;
		}
		return "resolver:" + host.getDnsName();
	}

	@Deprecated
	public static Object makeKey(String hostname) {
		return "resolver:" + hostname;
	}
}
