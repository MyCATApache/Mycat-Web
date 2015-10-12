package jrds.probe;

import java.net.URL;

public interface UrlProbe {
	public String getUrlAsString();
	public URL getUrl();
	public Integer getPort();
}
