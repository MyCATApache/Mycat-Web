package jrds;

//----------------------------------------------------------------------------
//$Id$

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

import jrds.webapp.ACL;
import jrds.webapp.WithACL;

import org.rrd4j.graph.RrdGraphDef;

/**
 * @author bacchell
 * @version $Revision$
 * TODO
 */
public class GraphNode implements Comparable<GraphNode>, WithACL {

    protected Probe<?,?> probe;
    private String viewPath = null;
    private GraphDesc gd;
    private String name = null;
    private String graphTitle = null;
    private ACL acl = ACL.ALLOWEDACL;
    private PlottableMap customData = null;

    /**
     *
     */
    public GraphNode(Probe<?,?> theStore, GraphDesc gd) {
        this.probe = theStore;
        this.gd = gd;
        this.acl = gd.getACL();
    }

    /**
     * A protected constructor
     * child are allowed to build themselves in a strange way
     * 
     */
    protected GraphNode() {
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return getQualifiedName().hashCode();
    }

    /**
     * @return Returns the theStore.
     */
    public Probe<?,?> getProbe() {
        return probe;
    }

    /**
     * To be called if the probe was not provided in the initial creation
     * This should be called as soon as possible
     * @param probe a custom generated probe
     */
    protected void setProbe(Probe<?,?> probe) {
        this.probe = probe;
    }

    public LinkedList<String> getTreePathByHost() {
        return gd.getHostTree(this);
    }

    public LinkedList<String> getTreePathByView() {
        return gd.getViewTree(this);
    }

    private final String parseTemplate(String template) {
        Object[] arguments = {
                "${graphdesc.name}",
                "${host}",
                "${index}",
                "${url}",
                "${probename}",
                "${index.signature}",
                "${url.signature}"
        };
        return jrds.Util.parseOldTemplate(template, arguments, probe, gd);
    }

    public String getGraphTitle() {
        if(graphTitle == null) {
            graphTitle = parseTemplate(gd.getGraphTitle());
        }
        return graphTitle;
    }

    public String getName() {
        if(name == null) {
            name = parseTemplate(gd.getGraphName());
        }
        return name;
    }

    /**
     * Return a uniq name for the graph
     * @return
     */
    public String getQualifiedName() {
        if (probe.getHost() != null) {
            return probe.getHost().getName() + "/"  + getName();
        } else {
            return "/"  + getName();
        }
    }

    public GraphDesc getGraphDesc() {
        return gd;
    }

    /**
     * To be called if the graphdesc was not provided in the initial creation
     * This should be called as soon as possible
     * @param gd A custom generated GraphDesc
     */
    protected void setGraphDesc(GraphDesc gd) {
        this.gd = gd;
        this.acl = gd.getACL();
    }

    /**
     * Provide a RrdGraphDef with template resolved for the node
     * @return a RrdGraphDef with some default values
     * @throws IOException
     */
    public RrdGraphDef getEmptyGraphDef() {
        RrdGraphDef retValue = getGraphDesc().getEmptyGraphDef();
        retValue.setTitle(getGraphTitle());
        return retValue;
    }

    public Graph getGraph() {
        Class<Graph>  gclass = gd.getGraphClass();
        
        //Exceptions can't happen, it was checked at configuration time
        try {
            return gclass.getConstructor(GraphNode.class).newInstance(this);
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(GraphNode arg0) {
        if (viewPath == null)
            viewPath = this.getTreePathByView().toString();

        String otherPath = arg0.getTreePathByView().toString();

        return String.CASE_INSENSITIVE_ORDER.compare(viewPath, otherPath);
    }

    @Override
    public String toString() {
        return probe.toString() + "/" + getName();
    }

    public void addACL(ACL acl) {
        this.acl = this.acl.join(acl);
    }

    public ACL getACL() {
        return acl;
    }

    /**
     * @return the customData
     */
    public PlottableMap getCustomData() {
        return customData;
    }

    /**
     * @param customData the customData to set
     */
    public void setCustomData(PlottableMap customData) {
        this.customData = customData;
    }

}
