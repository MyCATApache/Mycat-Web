package jrds;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import jrds.webapp.ACL;
import jrds.webapp.WithACL;

import org.apache.logging.log4j.*;
import org.rrd4j.data.DataProcessor;
import org.rrd4j.data.Plottable;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;

public class Graph implements WithACL {
    static final private Logger logger = LogManager.getLogger(Graph.class);

    static final private SimpleDateFormat lastUpdateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private final GraphNode node;
    private Date start;
    private Date end;
    private double max = Double.NaN;
    private double min = Double.NaN;
    private ACL acl = ACL.ALLOWEDACL;

    public Graph(GraphNode node) {
        this.node = node;
        addACL(node.getACL());
    }

    public Graph(GraphNode node, Date begin, Date start) {
        this.node = node;
        addACL(node.getACL());
        setStart(start);
        setEnd(end);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        //result = PRIME * result + ((end == null) ? 0 : end.hashCode());
        //result = PRIME * result + ((start == null) ? 0 : start.hashCode());
        result = PRIME * result + end.hashCode();
        result = PRIME * result + start.hashCode();
        long temp;
        temp = Double.doubleToLongBits(max);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(min);
        result = PRIME * result + (int) (temp ^ (temp >>> 32));
        result = PRIME * result + ((node == null) ? 0 : node.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Graph other = (Graph) obj;
        if (end == null) {
            if (other.end != null)
                return false;
        } else if (!end.equals(other.end))
            return false;
        if (Double.doubleToLongBits(max) != Double.doubleToLongBits(other.max))
            return false;
        if (Double.doubleToLongBits(min) != Double.doubleToLongBits(other.min))
            return false;
        if (node == null) {
            if (other.node != null)
                return false;
        } else if (!node.equals(other.node))
            return false;
        if (start == null) {
            if (other.start != null)
                return false;
        } else if (!start.equals(other.start))
            return false;
        return true;
    }

    protected void addlegend(RrdGraphDef graphDef) {
        Date lastUpdate = node.getProbe().getLastUpdate();
        graphDef.comment("\\l");
        graphDef.comment("\\l");
        graphDef.comment("Last update: " + 
                lastUpdateFormat.format(lastUpdate) + "\\L");
        String unit = "SI";
        if(! node.getGraphDesc().isSiUnit()) 
            unit = "binary";
        graphDef.comment("Unit type: " + unit + "\\r");
        graphDef.comment("Period from " + lastUpdateFormat.format(start) +
                " to " + lastUpdateFormat.format(end) + "\\L");
        graphDef.comment("Source type: " + node.getProbe().getSourceType() + "\\r");
    }

    protected void fillGraphDef(RrdGraphDef graphDef) {
        GraphDesc gd = node.getGraphDesc();
        try {
            long startsec = getStartSec();
            long endsec = getEndSec();
            graphDef.setStartTime(startsec);
            graphDef.setEndTime(endsec);
            PlottableMap customData = node.getCustomData();
            if(customData != null) {
                long step = Math.max((endsec - startsec) / gd.getWidth(), 1);
                customData.configure(startsec, endsec, step);
            }
            setGraphDefData(graphDef, node.getProbe(), customData);
            if(gd.withLegend())
                addlegend(graphDef);
        } catch (IllegalArgumentException e) {
            logger.error("Impossible to create graph definition, invalid date definition from " + start + " to " + end + " : " + e);
        }
    }
    
    protected void setGraphDefData(RrdGraphDef graphDef, Probe<?, ?> defProbe,
            Map<String, ? extends Plottable> customData) {
        GraphDesc gd = node.getGraphDesc();
        gd.fillGraphDef(graphDef, node.getProbe(), customData);        
    }

    protected long getStartSec() {
        return start.getTime()/1000;
    }

    protected long getEndSec() {
        return Util.endDate(node.getProbe(), end).getTime()/1000;
    }

    protected void finishGraphDef(RrdGraphDef graphDef) {
        if( ! Double.isNaN(max) && ! Double.isNaN(min) ) {
            graphDef.setMaxValue(max);
            graphDef.setMinValue(min);
            graphDef.setRigid(true);
        }
        graphDef.setFilename("-");
    }

    public RrdGraph getRrdGraph() throws IOException {
        return new RrdGraph(getRrdGraphDef());
    }

    public RrdGraphDef getRrdGraphDef() throws IOException {
        RrdGraphDef graphDef = node.getEmptyGraphDef();
        fillGraphDef(graphDef);
        finishGraphDef(graphDef);

        return graphDef;
    }

    public void writePng(OutputStream out) throws IOException {
        byte[] buffer = getRrdGraph().getRrdGraphInfo().getBytes();
        out.write(buffer);
    }

    /**
     * Return the RRD4J's DataProcessor object for this graph
     * 
     * @return an already processed data processor
     * @throws IOException
     */
    public DataProcessor getDataProcessor() throws IOException {
        DataProcessor dp = node.getGraphDesc().getPlottedDatas(node.getProbe(), null, start.getTime() / 1000, end.getTime() / 1000);
        dp.processData();
        return dp;
    }

    public String getPngName() {
        return node.getName().replaceAll("/","_").replaceAll(" ","_") + ".png";
    }

    /**
     * Return a uniq name for the graph
     * @return
     */
    public String getQualifiedName() {
        return node.getQualifiedName();
    }

    /**
     * @return the node
     */
    public GraphNode getNode() {
        return node;
    }

    /**
     * @return the max
     */
    public double getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * @return the start
     */
    public Date getStart() {
        return new Date(start.getTime());
    }

    /**
     * @param start the start to set
     */
    public void setStart(Date start) {
        long step = getNode().getProbe().getStep();
        this.start = new Date(org.rrd4j.core.Util.normalize(start.getTime() / 1000L, step) * 1000L);
    }

    /**
     * @return the end
     */
    public Date getEnd() {
        return new Date(end.getTime());
    }

    /**
     * @param end the end to set
     */
    public void setEnd(Date end) {
        //We normalize the last update time, it can't be used directly
        this.end = Util.endDate(node.getProbe(), end);
    }

    public void setPeriod(Period p) {
        logger.trace(jrds.Util.delayedFormatString("Period for graph: %s", p));
        setStart(p.getBegin());
        setEnd(p.getEnd());
    }

    public void addACL(ACL acl) {
        this.acl = this.acl.join(acl);
    }

    public ACL getACL() {
        return acl;
    }
}
