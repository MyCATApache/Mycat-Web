package jrds.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jrds.Filter;
import jrds.GraphNode;
import jrds.GraphTree;
import jrds.HostsList;
import jrds.Tab;

import org.apache.logging.log4j.*;
import org.json.JSONException;

/**
 * Servlet implementation class JSonTree
 */
public class JSonTree extends JSonData {
    static final private Logger logger = LogManager.getLogger(JSonTree.class);

    @Override
    public boolean generate(JrdsJSONWriter w, HostsList root, ParamsBean params) throws IOException, JSONException {

        if(ParamsBean.TABCHOICE.equals(params.getChoiceType() ) ) {
            Tab tab = params.getTab();
            logger.debug(jrds.Util.delayedFormatString("Tab specified: %s", params.getChoiceValue()));
            if(tab == null)
                return false;
            if(tab.isFilters()){
                Set<Filter> fset = tab.getFilters();
                if(fset != null && fset.size() !=0) {
                    logger.trace("Filters tab");
                    return dumpFilters(w, fset);
                }
            }
            else {
                GraphTree tabtree = tab.getGraphTree();
                if(tabtree != null) {
                    logger.trace("Tree tab");
                    return evaluateTree(params, w, root, tabtree);
                }
            }
        }
        else if(ParamsBean.HOSTCHOICE.equals(params.getChoiceType() ) ) {
            GraphTree tree = params.getTree();
            logger.debug(jrds.Util.delayedFormatString("Host specified: %s", params.getChoiceValue()));
            if(tree == null)
                return false;
            return evaluateTree(params, w, root, tree);
        }
        else if(ParamsBean.TREECHOICE.equals(params.getChoiceType() ) ) {
            GraphTree tree = params.getTree();
            logger.debug(jrds.Util.delayedFormatString("Tree specified: %s", params.getChoiceValue()));
            if(tree == null)
                return false;
            return evaluateTree(params, w, root, tree);
        }
        else if(ParamsBean.FILTERCHOICE.equals(params.getChoiceType() ) ) {
            Filter filter = params.getFilter();
            logger.debug(jrds.Util.delayedFormatString("Filter specified: %s", params.getChoiceValue()));
            if(filter == null)
                return false;
            return evaluateFilter(params, w, root, filter);
        }
        //Nothing requested, wrong query
        else {
            return false;
        }
        //No error, but nothing to do
        return true;
    }

    private boolean evaluateTree(ParamsBean params, JrdsJSONWriter w, HostsList root, GraphTree trytree) throws IOException, JSONException {
        for(GraphTree tree: findRoot(Collections.singleton(trytree))) {
            sub(params, w, tree, "tree", Filter.EVERYTHING, "", tree.hashCode());
        }
        return true;
    }

    private boolean evaluateFilter(ParamsBean params, JrdsJSONWriter w, HostsList root, Filter f) throws IOException, JSONException {
        Collection<GraphTree> level = root.getTrees();

        //We construct the graph tree root to use
        //The tree is parsed twice, that's not optimal
        Collection<GraphTree> rootToDo = new HashSet<GraphTree>(level.size());
        for(GraphTree tree: level) {
            GraphTree testTree = f.setRoot(tree);
            if(testTree != null && ! rootToDo.contains(testTree) && testTree.enumerateChildsGraph(f).size() > 0) {
                rootToDo.add(testTree);
            }
        }

        for(GraphTree tree: findRoot(rootToDo)) {
            sub(params, w, tree, "tree", f, "", tree.hashCode());
        }
        return true;
    }

    /**
     * Look for the first level with many childs
     * @param rootstry
     * @return
     */
    private Collection<GraphTree> findRoot(Collection<GraphTree> rootstry) {
        while(rootstry.size() == 1) {
            logger.trace(jrds.Util.delayedFormatString("Trying with graph tree roots: %s", rootstry));
            GraphTree child = rootstry.iterator().next();
            Map<String, GraphTree> childTree = child.getChildsMap();
            //Don't go in empty nodes
            if(childTree.isEmpty())
                break;
            //a graph found, stop here
            if(child.getGraphsSet().size() > 0) {
                break;
            }
            rootstry = child.getChildsMap().values();
        }
        return rootstry;
    }

    private boolean dumpFilters(JrdsJSONWriter w, Set<Filter> filterSet) throws JSONException {
        for(Filter filter: filterSet) {
            String filterName = filter.getName();
            Map<String, String> href = new HashMap<String, String>();
            href.put("filter", filterName);
            doTree(w,filterName, filter.hashCode(), "filter", null, href);
        }
        return true;
    }

    private String sub(ParamsBean params, JrdsJSONWriter w, GraphTree gt, String type, Filter f, String path, int base) throws IOException, JSONException {
        String id = null;
        String subpath = path + "/" + gt.getName();
        boolean hasChild = false;
        Map<String, GraphTree> childs = gt.getChildsMap();

        List<String> childsref = new ArrayList<String>();
        for(Map.Entry<String, GraphTree>e: childs.entrySet()) {
            String childid = sub(params, w, e.getValue(), "node", f, subpath, base);
            if(childid != null) {
                hasChild = true;
                childsref.add(childid);
            }
        }

        for(Map.Entry<String, GraphNode> leaf: gt.getGraphsSet().entrySet()) {
            GraphNode child = leaf.getValue();
            if(getPropertiesManager().security && ! child.getACL().check(params))
                continue;
            String leafName = leaf.getKey();
            if(f.acceptGraph(child, gt.getPath() + "/" + child.getName())) {
                hasChild = true;
                String graphid = base + "." + child.hashCode();
                childsref.add(graphid );
                doTree(w,leafName, graphid, "graph", null);
            }
        }

        if(hasChild) {
            id = base + "." +  gt.getPath().hashCode();
            doTree(w,gt.getName(), id, type, childsref);
        }
        return id;
    }

}
