package jrds;

import java.util.Set;

public class FilterTag extends Filter {
    private String tag;

    public FilterTag(String tag) {
        super();
        this.tag = tag;
    }

    @Override
    public boolean acceptGraph(GraphNode graph, String path) {
        Probe<?,?> p = graph.getProbe();
        if(p == null)
            return false;
        HostInfo host = p.getHost();
        if(host == null)
            return false;
        Set<String> hostTags = graph.getProbe().getHost().getTags();
        if (hostTags ==null) {
            return false;
        }
        for(String oneTag: hostTags) {
            if(tag.equals(oneTag))
                return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return tag;
    }

}
