package jrds.starter;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Level;

import jrds.PropertiesManager;

public class SSLStarter extends Starter {

    // Create a trust manager that does not validate certificate chains
    public static final X509TrustManager trustAllCerts= new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    };

    private final static String SSLProtocol = "SSL";

    private String[] supportedProtocols = null;
    private String[] supportedCipherSuites = null;
    private TrustManager[] trustManagers = new TrustManager[]{trustAllCerts};

    SSLContext sc = null;

    /* (non-Javadoc)
     * @see jrds.starter.Starter#configure(jrds.PropertiesManager)
     */
    @Override
    public void configure(PropertiesManager pm) {
        super.configure(pm);
    }

    @Override
    public boolean start() {
        try {
            sc = SSLContext.getInstance(SSLProtocol);
            if(! "Default".equals(sc.getProtocol())) {
                sc.init(null, trustManagers, null);                
            }
        } catch (NoSuchAlgorithmException e) {
            log(Level.ERROR, e, "failed to init ssl: %s", e);
            return false;
        } catch (KeyManagementException e) {
            log(Level.ERROR, e, "failed to init ssl: %s", e);
            return false;
        }
        return sc != null;
    }

    public SSLContext getContext() {
        return sc;
    }

    public String[] getSupportedProtocols() {
        return supportedProtocols;
    }

    public String[] getSupportedCipherSuites() {
        return supportedCipherSuites;
    }

    public Socket connect(String host, int port) throws NoSuchAlgorithmException, KeyManagementException, UnknownHostException, IOException {
        SocketFactory ss = getLevel().find(SocketFactory.class); 
        Socket s = ss.createSocket(host, port);

        SSLSocketFactory ssf = getContext().getSocketFactory();
        s = ssf.createSocket(s, host, port, true);
        log(Level.DEBUG, "done SSL handshake for %s", host);
        return s;
    }

    @Override
    public boolean isStarted() {
        return sc != null;
    }

}
