package jrds.webapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jrds.HostsList;
import jrds.Tab;
import jrds.Util;

import org.apache.logging.log4j.*;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Servlet implementation class JSonQueryParams
 */
public class JSonQueryParams extends JrdsServlet {
    private static final long serialVersionUID = 1L;
    static final private Logger logger = LogManager.getLogger(JSonQueryParams.class);

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ParamsBean params = getParamsBean(request);
        HostsList root = getHostsList();

        JrdsJSONWriter w = new JrdsJSONWriter(response);
        try {
            w.object();
            doVariable(w, "pid", params.getValue("pid"));
            doVariable(w, "id", params.getValue("id"));
            doVariable(w, "gid", params.getValue("gid"));
            doVariable(w, "sort", params.getValue("sort"));
            String pathString = params.getValue("path");
            if(pathString != null && ! "".equals(pathString)) {
                doVariable(w, "path", new JSONArray(pathString));
            }
            String choiceType = params.getChoiceType();
            String choiceValue = params.getChoiceValue();
            if(choiceType != null && choiceValue != null)
                doVariable(w, choiceType, choiceValue);

            doVariable(w, "min", params.getMinStr());
            doVariable(w, "max", params.getMaxStr());
            doVariable(w, "dsName", params.getValue("dsName"));
            doVariable(w, "begin", params.getBegin());
            doVariable(w, "end", params.getEnd());
            doVariable(w, "autoperiod", params.getScale());

            //Add the list of tabs
            w.key("tabslist");
            w.object();
            for(String id: root.getTabsId()) {
                Tab tab = root.getTab(id);
                w.key(id);
                w.object();
                w.key("id").value(id);
                w.key("label").value(tab.getName());
                w.key("isFilters").value(tab.isFilters());
                w.key("callback").value(tab.getJSCallback());
                w.endObject();
            }
            w.endObject();

            w.endObject();
            w.newLine();
            w.flush();
        } catch (JSONException e) {
            logger.fatal(e, e);
        }
    }

    private final void doVariable(JrdsJSONWriter w, String key, Object value) throws JSONException {
        logger.trace(Util.delayedFormatString("resolving %s with %s", key, value));
        if(value == null) 
            return;
        if(value instanceof String && "".equals(value.toString().trim())) {
            return;
        }
        //		if(value != null && ! "".equals(value)) {
        //			value = value.replace("'", " ");//.replace("\"", " ");
        //		}
        w.key(key).value(value);
    }

}
