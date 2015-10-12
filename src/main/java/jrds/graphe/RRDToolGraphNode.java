package jrds.graphe;

import java.io.File;
import java.io.IOException;

import org.rrd4j.core.jrrd.ConsolidationFunctionType;
import org.rrd4j.core.jrrd.DataChunk;
import org.rrd4j.core.jrrd.RRDatabase;

import jrds.Graph;
import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.PlottableMap;
import jrds.probe.RRDToolProbe;

public class RRDToolGraphNode extends GraphNode {
    private final File rrdpath;

    public RRDToolGraphNode(RRDToolProbe theStore, GraphDesc gd, File rrdpath) {
        super(theStore, gd);
        this.rrdpath = rrdpath;
    }

    /* (non-Javadoc)
     * @see jrds.GraphNode#getGraph()
     */
    @Override
    public Graph getGraph() {
        PlottableMap pp = new PlottableMap() {
            @Override
            public void configure(long start, long end, long step) {
                try {
                    RRDatabase db = new RRDatabase(rrdpath);
                    DataChunk chunck = db.getData(ConsolidationFunctionType.AVERAGE, start, end, step);
                    for(String name: db.getDataSourcesName()) {
                        put(name, chunck.toPlottable(name));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Unable to create ProxyPlottableMap", e);
                }

            }
        };
        setCustomData(pp);
        return super.getGraph();
    }

}