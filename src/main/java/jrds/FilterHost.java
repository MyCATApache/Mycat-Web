package jrds;

public class FilterHost extends Filter {
	String hostname = "";
	public FilterHost(String hostname) {
		super();
		this.hostname = hostname;
	}

	@Override
	public boolean acceptGraph(GraphNode graph, String path) {
		return graph.getProbe().getHost().getName().equals(hostname) && path.startsWith("/" + GraphTree.HOSTROOT + "/");
	}

	@Override
	public String getName() {
		return hostname;
	}

	@Override
	public GraphTree setRoot(GraphTree gt) {
		return gt.getByPath(GraphTree.HOSTROOT, hostname);
	}

}
