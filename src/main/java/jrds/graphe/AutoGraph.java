package jrds.graphe;

import java.util.Arrays;

import jrds.Graph;
import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.Probe;
import jrds.PropertiesManager;

import org.apache.logging.log4j.*;

public class AutoGraph extends GraphNode {
    public enum Operation {
        SUM, MIN, MAX, AVERAGE
    };

    static final private Logger logger = LogManager.getLogger(AutoGraph.class);
    static int i;
    Operation op;

    public AutoGraph(Probe<?,?> theStore, Operation op) {
        super(theStore, new GraphDesc() {
            String name = "autograph" + i++;
            /* (non-Javadoc)
             * @see jrds.GraphDesc#getGraphName()
             */
            @Override
            public String getGraphName() {
                return name;
            }


        });
        GraphDesc gd = this.getGraphDesc();
        gd.setGraphName(theStore.getName());
        gd.setGraphTitle(theStore.getName());
        gd.setName(theStore.getName());
        gd.setTree(PropertiesManager.HOSTSTAB, Arrays.asList(new Object[]{GraphDesc.TITLE}));
        logger.debug(this.getQualifiedName());
        this.op = op;

    }

    /* (non-Javadoc)
     * @see jrds.GraphNode#getGraph()
     */
    @Override
    public Graph getGraph() {
        logger.debug("Wants to graph a AutoGraph");
        return null;
    }
}
