package jrds.webapp;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.HostsList;
import jrds.Probe;
import jrds.Renderer;

import org.apache.logging.log4j.*;
import org.json.JSONException;

/**
 * Servlet implementation class JSonGraph
 */
public class JSonGraph extends JSonData {
    static final private Logger logger = LogManager.getLogger(JSonGraph.class);
    private static final long serialVersionUID = 1L;
    private int periodHistory[] = {7, 9, 11, 16};

    @Override
    public boolean generate(JrdsJSONWriter w, HostsList root,
            ParamsBean params) throws IOException, JSONException {

        if(params.getPeriod() == null) {
            return false;
        }

        List<GraphNode> graphs = params.getGraphs(this);
        if(params.isSorted() && graphs.size() > 1) {
            Collections.sort(graphs, new Comparator<GraphNode>() {
                public int compare(GraphNode g1, GraphNode g2) {
                    int order = String.CASE_INSENSITIVE_ORDER.compare(g1.getName(), g2.getName());
                    if(order == 0)
                        order = String.CASE_INSENSITIVE_ORDER.compare(g1.getProbe().getHost().getName(), g2.getProbe().getHost().getName());
                    return order;
                }
            });
        }
        logger.debug(jrds.Util.delayedFormatString("Graphs returned: %s", graphs));
        if( ! graphs.isEmpty()) {
            Renderer r = root.getRenderer();
            for(GraphNode gn: graphs) {
                if(! gn.getACL().check(params))
                    continue;
                if(params.isHistory()) {
                    for(int p: periodHistory) {
                        params.setScale(p);
                        doGraph(gn, r, params, w);
                    }
                }
                else {
                    doGraph(gn, r, params, w);
                }
            }
        }
        return true;
    }

    private void doGraph(GraphNode gn, Renderer r, ParamsBean params, JrdsJSONWriter w) throws IOException, JSONException {
        jrds.Graph graph = gn.getGraph();
        params.configureGraph(graph);

        Map<String, Object> imgProps = new HashMap<String, Object>();
        r.render(graph);
        Probe<?,?> p = gn.getProbe();
        imgProps.put("probename", p.getName());
        imgProps.put("qualifiedname", graph.getQualifiedName());

        imgProps.put("qualifiedname", graph.getQualifiedName());
        GraphDesc gd = gn.getGraphDesc();
        if(gd !=null && gd.getDimension() != null) {
            imgProps.put("height", gd.getDimension().height);
            imgProps.put("width", gd.getDimension().width);
        }
        imgProps.put("graph",params.doArgsMap(graph, true));
        imgProps.put("history",params.doArgsMap(graph, false));
        imgProps.put("probe",params.doArgsMap(p, true));
        imgProps.put("graphnode",params.doArgsMap(gn, true));
        doTree(w, graph.getQualifiedName(), gn.hashCode(), "graph", null, imgProps);
    }

}
