package jrds;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.*;

public abstract class Tab {
    public static final class Filters extends Tab {
        private final Set<String> filters = new TreeSet<String>(jrds.Util.nodeComparator);

        public Filters(String name) {
            super(name);
        }
        public Filters(String name, String id) {
            super(name, id);
        }
        public void add(String filter) {
            filters.add(filter);
        }
        public Set<jrds.Filter> getFilters() {
            Set<jrds.Filter> filtersset = new LinkedHashSet<jrds.Filter>(filters.size());
            for(String filtername: filters) {
                jrds.Filter f = hostlist.getFilter(filtername);
                if(f != null)
                    filtersset.add(f);
            }
            return filtersset;
        }
        public boolean isFilters() {
            return true;
        }
    }
    public static final class StaticTree extends Tab {
        private final GraphTree gt;
        public StaticTree(String name, GraphTree gt) {
            super(name);
            this.gt = gt;
        }
        public StaticTree(String name, String id, GraphTree gt) {
            super(name, id);
            this.gt = gt;
        }
        public GraphTree getGraphTree() {
            return gt;
        }
    }
    public static final class DynamicTree extends Tab {
        private final Map<String, List<String>> paths = new TreeMap<String, List<String>>(jrds.Util.nodeComparator);
        public DynamicTree(String name) {
            super(name);
        }
        public DynamicTree(String name, String id) {
            super(name, id);
        }
        public void add(String id, List<String> path) {
            paths.put(id, path);
        }
        public GraphTree getGraphTree() {
            GraphTree gt = GraphTree.makeGraph(name); 
            for(Map.Entry<String , List<String>> e: paths.entrySet()) {
                String id = e.getKey();
                List<String> path = e.getValue();
                GraphNode gn = hostlist.getGraphById(id.hashCode());
                if(gn == null) {
                    logger.warn(jrds.Util.delayedFormatString("Graph not found for %s: %s", name, id));
                    continue;
                }
                gt.addGraphByPath(path, gn);
            }
            return gt;
        }
    }

    static protected final Logger logger = LogManager.getLogger(Tab.class);

    protected String name;
    protected String id;
    protected HostsList hostlist;

    protected Tab(String name) {
        this.name = name;
        this.id = name;
    }

    protected Tab(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public void add(String filter) {
        throw new RuntimeException("Not implemented");
    }

    public void add(String id, String... path) {
        add(id, Arrays.asList(path));
    }

    public void add(String id, List<String> path) {
        throw new RuntimeException("Not implemented");
    }

    public GraphTree getGraphTree() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @param hostlist the hostlist to set
     */
    public void setHostlist(HostsList hostlist) {
        this.hostlist = hostlist;
    }

    public Set<jrds.Filter> getFilters() {
        throw new RuntimeException("Not implemented");
    }

    /* (non-Javadoc)
     * @see jrds.Probe#toString()
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public boolean isFilters() {
        return false;
    }

    public String getJSCallback() {
        return "treeTabCallBack";
    }

    /**
     * @return the id
     */
    String getId() {
        return id;
    }
}
