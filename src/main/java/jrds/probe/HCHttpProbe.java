package jrds.probe;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;

import jrds.factories.ProbeMeta;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.logging.log4j.Level;

/**

 * A generic probe to collect an HTTP service. It uses <a href="http://hc.apache.org/httpclient-3.x/">Apache's Commons HttpClient</a>s to provide a better isolation with other web app
 * in the same container. So it should used in preference to HttpProbe and deprecate it.
 * default generic : 
 * port to provide a default port to collect
 * file to provide a specific file to collect
 * 
 * Implementation should implement the parseStream method
 *
 * @author Fabrice Bacchella 
 */
@ProbeMeta(
        timerStarter=jrds.probe.HttpClientStarter.class
        )
public abstract class HCHttpProbe extends HttpProbe {

    @Override
    public Map<String, Number> getNewSampleValues() {
        log(Level.DEBUG, "Getting %s", getUrl());
        HttpClientStarter httpstarter = find(HttpClientStarter.class);
        HttpClient cnx = httpstarter.getHttpClient();
        try {
            HttpGet hg = new HttpGet(getUrl().toURI());
            HttpResponse response = cnx.execute(hg);
            if(response.getStatusLine().getStatusCode() != 200) {
                log(Level.ERROR, "Connection to %s fail with %s", getUrl(), response.getStatusLine().getReasonPhrase());
                return null;
            }
            HttpEntity entity = response.getEntity();
            if(entity == null) {
                log(Level.ERROR, "Not response body to %s",getUrl());
                return null;
            }
            InputStream is = entity.getContent();;
            Map<String, Number> vars = parseStream(is);
            is.close();
            return vars;
        } catch (ClientProtocolException e) {
            log(Level.ERROR, e, "Unable to read %s because: %s", getUrl(), e.getMessage());
        } catch (IllegalStateException e) {
            log(Level.ERROR, e, "Unable to read %s because: %s", getUrl(), e.getMessage());
        } catch (IOException e) {
            log(Level.ERROR, e, "Unable to read %s because: %s", getUrl(), e.getMessage());
        } catch (URISyntaxException e) {
            log(Level.ERROR, "unable to parse %s", getUrl());
        }

        return null;
    }

}
