package jrds.webapp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.HostsList;

import org.apache.logging.log4j.*;
import org.json.JSONException;

public abstract class JSonData extends JrdsServlet {
    static final private Logger logger = LogManager.getLogger(JSonData.class);

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            ParamsBean params = getParamsBean(request);
            HostsList root = getHostsList();
            JrdsJSONWriter w = new JrdsJSONWriter(response);
            w.object();
            w.key("identifier").value("id");
            w.key("label").value("name");
            w.key("items");
            w.array();
            w.newLine();
            if (! generate(w, root, params)) {
                logger.warn("Invalid request received: " + request.getRequestURI() + "?" + request.getQueryString());
            }
            w.endArray();
            w.endObject();
            w.newLine();
            w.flush();
        } catch (Exception e) {
            logger.warn("Failed request: " + request.getRequestURI() + "?" + request.getQueryString() +": " + e, e);
        }
    }

    public abstract boolean generate(JrdsJSONWriter w, HostsList root, ParamsBean params) throws IOException, JSONException;

    public JrdsJSONWriter doTree(JrdsJSONWriter w, String name, int id, String type, List<String> childsref) throws JSONException {
        return doTree(w, name, Integer.toString(id), type, childsref, null);
    }

    public JrdsJSONWriter doTree(JrdsJSONWriter w,String name, int id, String type, List<String> childsref, Map<String, ?> attributes) throws JSONException {
        return doTree(w, name, Integer.toString(id), type, childsref, attributes);
    }

    public JrdsJSONWriter doTree(JrdsJSONWriter w, String name, String id, String type, List<String> childsref) throws JSONException {
        return doTree(w, name, id, type, childsref, null);
    }

    public JrdsJSONWriter doTree(JrdsJSONWriter w, String name, String id, String type, List<String> childsref, Map<String, ?> attributes) throws JSONException {
        name = name.replace("'", " ").replace("\"", " ");
        w.object();
        w.key("name").value(name);
        w.key("type").value(type);
        w.key("id").value(id);

        if(attributes != null && attributes.size() > 0) {
            for(Map.Entry<String, ?> e: attributes.entrySet()) {
                w.key(e.getKey());
                if(e.getValue() instanceof String) {
                    String value = (String) e.getValue();
                    w.value(value.replace("'", " "));
                } else if(e.getValue() instanceof Map<?,?>) {
                    w.map((Map<?,?>)e.getValue());
                } else {
                    w.value(e.getValue());
                }
            }
        }
        if(childsref != null && childsref.size() >0 ) {
            w.key("children").array();
            for(String child: childsref) {
                w.object().key("_reference").value(child).endObject();
            }
            w.endArray();
        }
        w.endObject();

        return w;
    }

}
