package jrds.probe;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;

import jrds.Probe;
import jrds.Util;
import jrds.factories.ProbeBean;
import jrds.factories.ProbeMeta;
import jrds.starter.SocketFactory;
import jrds.starter.XmlProvider;

import org.apache.logging.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@ProbeMeta(
        topStarter=jrds.starter.XmlProvider.class
        )
@ProbeBean({"user", "password", "iloHost"})
public class Ribcl extends Probe<String, Number> {
	private String user;
	private String passwd;
	private String iloHost;
	private Integer port = 443;

	static final private String encoding = "ISO-8859-1";
	static final private String eol = "\r\n";
	static final private String xmlHeader = "<?xml version=\"1.0\" ?>" + eol;

	public void configure(String iloHost, int port, String user, String passwd) {
		this.iloHost = iloHost;
		this.user = user;
		this.passwd = passwd;
		this.port = port;
		
	}

	public void configure(String iloHost, String user, String passwd) {
		this.iloHost = iloHost;
		this.user = user;
		this.passwd = passwd;
	}

	@Override
	public Map<String, Number> getNewSampleValues() {
		Map<String, Number> vars = new HashMap<String, Number>();
		Socket s = null;
		try {
			s = connect();
		} catch (Exception e) {
			log(Level.ERROR, e, "SSL connect error %s", e);
			return null;
		}

		try {
			XmlProvider xmlstarter  = find(XmlProvider.class);
			if(xmlstarter == null) {
				log(Level.ERROR, "XML Provider not found");
				return null;
			}
			
			if(! isCollectRunning())
				return null;

			OutputStream outputSocket = s.getOutputStream();
			InputStream inputSocket = s.getInputStream();

			outputSocket.write(xmlHeader.getBytes(encoding));
			buildQuery(outputSocket, xmlstarter);

			byte[] buffer = new byte[4096];
			int n;
			StringBuffer message = null;
			while((n = inputSocket.read(buffer)) > 0) {
				String messageBuffer = new String(buffer, 0, n, encoding);
				if(messageBuffer.startsWith("<?xml version=\"1.0\"?>")) {
					if(message != null)
						parse(message.toString(), vars, xmlstarter);
					message = new StringBuffer(messageBuffer);
				}
				else {
					message.append(messageBuffer);
				}
			}
			s.close();
		} catch (IOException e) {
			log(Level.ERROR, e, "SSL socket error %s", e);
		}

		return vars;
	}

	@Override
	public String getSourceType() {
		return "RIBCL";
	}

	private void buildQuery(OutputStream out, XmlProvider xmlstarter) {
		Document ribclQ = xmlstarter.getDocument();
		Element LOCFG = ribclQ.createElement("LOCFG");
		LOCFG.setAttribute("version", "2.21");
		ribclQ.appendChild(LOCFG);
		Element RIBCL = ribclQ.createElement("RIBCL");
		LOCFG.appendChild(RIBCL);
		RIBCL.setAttribute("VERSION", "2.0");
		Element LOGIN = ribclQ.createElement("LOGIN");
		LOGIN.setAttribute("USER_LOGIN", user);
		LOGIN.setAttribute("PASSWORD", passwd);
		RIBCL.appendChild(LOGIN);
		Element command = ribclQ.createElement(getPd().getSpecific("command"));
		command.setAttribute("MODE", "read");
		LOGIN.appendChild(command);
		Element subcommand = ribclQ.createElement(getPd().getSpecific("subcommand"));
		command.appendChild(subcommand);

		Map<String, String> properties = new HashMap<String, String>();
		properties.put(OutputKeys.INDENT, "no");
		properties.put(OutputKeys.OMIT_XML_DECLARATION, "yes");
		try {
			Util.serialize(ribclQ, out, null, properties);
		} catch (TransformerException e) {
		} catch (IOException e) {
			log(Level.FATAL, e, "Unable to serialize in memory");
			throw new Error(e);
		}
	}

	private Socket connect() throws NoSuchAlgorithmException, KeyManagementException, UnknownHostException, IOException {
		SocketFactory ss = find(SocketFactory.class); 
		Socket s = ss.createSocket(iloHost, port);
		if (s == null)
			return s;
		
		if(port == 23) {
			return 	s;
		}		
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[]{
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() {
						return null;
					}
					public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
					public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
					}
				}
		};

		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());

		SSLSocketFactory ssf = sc.getSocketFactory();
		if(! isCollectRunning())
			return null;
		s = ssf.createSocket(s, iloHost, port, true);
		log(Level.DEBUG, "done SSL handshake for %s", iloHost);
		return s;
	}

	public void parse(String message, Map<String, Number> vars, XmlProvider xmlstarter) {
		if(message == null ||  "".equals(message))
			return;
		log(Level.TRACE,"new message to parse: ");
		log(Level.TRACE, message);
		//The XML returned from an iLO is buggy, up to ilO2 1.50 
		message = message.replaceAll("<RIBCL VERSION=\"[0-9\\.]+\"/>", "<RIBCL >");
		Document d = xmlstarter.getDocument(new StringReader(message));
		xmlstarter.fileFromXpaths(d, getPd().getCollectStrings().keySet(), vars);
		return;
	}

	/* (non-Javadoc)
	 * @see jrds.starter.StarterNode#isStarted(java.lang.Object)
	 */
	@Override
	public boolean isStarted(Object key) {
		return super.isStarted(key) && find(XmlProvider.class).isStarted() && find(SocketFactory.class).isStarted();
	}

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the passwd
     */
    public String getPassword() {
        return passwd;
    }

    /**
     * @param passwd the passwd to set
     */
    public void setPassword(String passwd) {
        this.passwd = passwd;
    }

    /**
     * @return the iloHost
     */
    public String getIloHost() {
        return iloHost;
    }

    /**
     * @param iloHost the iloHost to set
     */
    public void setIloHost(String iloHost) {
        this.iloHost = iloHost;
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
