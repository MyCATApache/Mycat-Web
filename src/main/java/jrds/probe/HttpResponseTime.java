package jrds.probe;

import java.net.URL;

import jrds.factories.ProbeBean;

/**
 * @author Fabrice Bacchella
 */
@ProbeBean({"url"})
public final class HttpResponseTime extends ExternalCmdProbe implements UrlProbe {
	private URL url;

	public void configure(URL url)
    {
        this.url = url;
    }

	/**
     * @return the url
     */
    public URL getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(URL url) {
        this.url = url;
    }

    /* (non-Javadoc)
	 * @see jrds.probe.UrlProbe#getUrlAsString()
	 */
	public String getUrlAsString() {
		return getUrl().toString();
	}

	/* (non-Javadoc)
	 * @see jrds.probe.UrlProbe#getPort()
	 */
	public Integer getPort() {
		return getUrl().getPort();
	}
}
