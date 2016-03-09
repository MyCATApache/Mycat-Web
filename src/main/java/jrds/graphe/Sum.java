package jrds.graphe;

import java.util.ArrayList;

import jrds.AutonomousGraphNode;
import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.HostsList;
import jrds.PlottableMap;
import jrds.Util;

import org.apache.logging.log4j.*;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.data.LinearInterpolator;
import org.rrd4j.data.Plottable;

public class Sum extends AutonomousGraphNode {
    static final private Logger logger = LogManager.getLogger(Sum.class);

    private final ArrayList<String> graphList;
    private HostsList hl;

    public Sum(String name, ArrayList<String> graphList) {
        super(name);
        this.graphList = graphList;
        GraphDesc gd = new GraphDesc();
        gd.setGraphName(name);
        gd.setGraphTitle(name);
        gd.setName(name);
        setGraphDesc(gd);
        getProbe().addGraph(this);
    };

    public void configure(HostsList hl) {
        super.configure(hl);
        this.hl = hl;

        GraphNode g = null;
        //Check the sum consistency
        for(String graphname: graphList) {
            g = hl.getGraphById(graphname.hashCode());
            if(g == null) {
                logger.warn(Util.delayedFormatString("graph %s not found for sum %s", graphname, getName()));
            }
        }
        //The last graph found is used to clone the graphdesc and use it
        if(g != null){
            try {
                GraphDesc oldgd = g.getGraphDesc();
                GraphDesc newgd  = (GraphDesc) oldgd.clone();
                newgd.setGraphTitle(getName());
                setGraphDesc(newgd);
                logger.debug(Util.delayedFormatString("Adding sum called %s", getQualifiedName()));       
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(String.format("GraphDesc is supposed to be clonnable, what happened with %s ?", getName()));
            }
        }
        else {
            throw new RuntimeException(String.format("Not graph found in %s definition, unusable sum", getName()));
        }
    }

    /* (non-Javadoc)
     * @see jrds.GraphNode#getCustomData()
     */
    @Override
    public PlottableMap getCustomData() {
        PlottableMap sumdata = new PlottableMap() {
            @Override
            public void configure(long start, long end, long step) {
                logger.debug(Util.delayedFormatString("Configuring the sum %s from %d to %d, step %d", Sum.this.getName(), start, end, step));
                //Used to kept the last fetched data and analyse the
                FetchData fd = null;

                double[][] allvalues = null;
                for(String name : graphList) {
                    GraphNode g = hl.getGraphById(name.hashCode());
                    logger.trace("Looking for " + name + " in graph base, and found " + g);
                    if(g != null) {
                        fd = g.getProbe().fetchData(ConsolFun.AVERAGE, start, end, step);

                        //First pass, no data to use
                        if(allvalues == null) {
                            allvalues = (double[][]) fd.getValues().clone();
                        }
                        //Next step, sum previous values
                        else {
                            double[][] tempallvalues = fd.getValues();
                            for(int c = 0 ; c < tempallvalues.length ; c++) {
                                for(int r = 0 ; r < tempallvalues[c].length; r++) {
                                    double v = tempallvalues[c][r];
                                    if ( ! Double.isNaN(v) ) {
                                        if(! Double.isNaN(allvalues[c][r]))
                                            allvalues[c][r] += v;
                                        else    
                                            allvalues[c][r] = v;
                                    }
                                }
                            }
                        }
                    }
                    else {
                        logger.error("Graph not found: " + name);
                    }
                }
                if(fd != null) {
                    long[] ts = fd.getTimestamps();
                    String[] dsNames = fd.getDsNames();
                    for(int i= 0; i < fd.getColumnCount(); i++) {
                        Plottable pl = new LinearInterpolator(ts, allvalues[i]);
                        put(dsNames[i], pl);
                        logger.trace(Util.delayedFormatString("Added %s to sum plottables", dsNames[i]));
                    }
                }
            }
        };
        return sumdata;
    }

}
