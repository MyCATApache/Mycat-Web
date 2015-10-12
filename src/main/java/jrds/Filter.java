package jrds;

import jrds.graphe.Sum;
import jrds.probe.ContainerProbe;
import jrds.webapp.ACL;
import jrds.webapp.WithACL;


public abstract class Filter implements WithACL {
	static final public Filter SUM = new Filter() {
		@Override
		public boolean acceptGraph(GraphNode graph, String path) {
			return (graph instanceof Sum);
		}
		@Override
		public String getName() {
			return "All sums";
		}
		@Override
		public GraphTree setRoot(GraphTree gt) {
			return gt.getByPath(GraphTree.SUMROOT);
		}
	};
	static final public Filter CUSTOM = new Filter() {
		@Override
		public boolean acceptGraph(GraphNode graph, String path) {
			return (graph.getProbe() instanceof ContainerProbe);
		}
		@Override
		public String getName() {
			return "All customs graph";
		}
	};
	static final public Filter EVERYTHING = new Filter() {
		@Override
		public boolean acceptGraph(GraphNode graph, String path) {
			return true;
		}
		@Override
		public String getName() {
			return "Everything";
		}
	};
	static final public Filter ALLHOSTS = new Filter() {
		@Override
		public boolean acceptGraph(GraphNode graph, String path) {
		    HostInfo host = graph.getProbe().getHost();
			return (! host.isHidden()) && path.startsWith("/" + GraphTree.HOSTROOT + "/");
		}
		@Override
		public String getName() {
			return "All hosts";
		}
	};
	static final public Filter ALLVIEWS = new Filter() {
		@Override
		public boolean acceptGraph(GraphNode graph, String path) {
			return path.startsWith("/" + GraphTree.VIEWROOT + "/");
		}
		@Override
		public String getName() {
			return "All views";
		}
	};
	static final public Filter ALLSERVICES = new Filter() {
		@Override
		public boolean acceptGraph(GraphNode graph, String path) {
			return path.startsWith("/" + GraphTree.VIEWROOT + "/Services");
		}
		@Override
		public String getName() {
			return "All Services";
		}
		@Override
		public GraphTree setRoot(GraphTree gt) {
			return gt.getByPath(GraphTree.VIEWROOT, "Services");
		}
	};
	public abstract boolean acceptGraph(GraphNode graph, String path);
	public abstract String getName();
	public GraphTree setRoot(GraphTree gt) {
		return gt;
	}
	
	private ACL acl = ACL.ALLOWEDACL;
	
	/* (non-Javadoc)
	 * @see jrds.webapp.WithACL#getACL()
	 */
	public ACL getACL() {
		return acl;
	}
	/* (non-Javadoc)
	 * @see jrds.webapp.WithACL#addACL()
	 */
	public void addACL(ACL acl) {
		this.acl = this.acl.join(acl); 
	}

}
