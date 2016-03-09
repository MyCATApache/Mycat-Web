package jrds.probe;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

import jrds.starter.Connection;

import org.apache.logging.log4j.Level;

public class LdapConnection extends Connection<DirContext> {
	private String binddn;
	private String password;
	private int port = 389;
	DirContext dctx = null;
	
	long uptime;

	public LdapConnection() {
		super();
	}

	public LdapConnection(Integer port) {
		super();
		this.port = port;
	}
	public LdapConnection(Integer port, String binddn, String password) {
		super();
		this.binddn = binddn;
		this.password = password;
		this.port = port;
	}

	public LdapConnection(String binddn, String password) {
		super();
		this.binddn = binddn;
		this.password = password;
	}

	@Override
	public DirContext getConnection() {
		return dctx;
	}

	/* (non-Javadoc)
	 * @see jrds.Starter#start()
	 */
	@Override
	public boolean startConnection() {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + getHostName() +  ":" + port);
		if(binddn != null && password !=null) {
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, binddn);
			env.put(Context.SECURITY_CREDENTIALS, password);
		}
		env.put("com.sun.jndi.ldap.connect.timeout", "" + getTimeout() * 1000);

		try {
			dctx = new InitialDirContext(env);
		} catch (NamingException e) {
			log(Level.ERROR, e, "Cannot connect to %s, cause: ", getLevel(), e.getCause());
			return false;
		}

		log(Level.DEBUG, "Binding to: %s with dn: %s", env.get(Context.PROVIDER_URL), binddn);
		return dctx != null;
	}

	/* (non-Javadoc)
	 * @see jrds.Starter#stop()
	 */
	@Override
	public void stopConnection() {
		uptime = 1;
		if(dctx != null)
			try {
				dctx.close();
			} catch (NamingException e) {
				log(Level.ERROR, e, "Error close to %s, cause: %s", getLevel(), e.getCause());
			}
			dctx = null;
	}

	@Override
	public long setUptime() {
		return uptime;
	}

}
