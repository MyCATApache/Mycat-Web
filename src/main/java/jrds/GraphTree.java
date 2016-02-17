/*##########################################################################
_##
_##  $Id$
_##
_##########################################################################*/

package jrds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.*;

/**
 *
 * @author Fabrice Bacchella
 * @version $Revision$ $Date$
 */
public class GraphTree {

	static final private Logger logger = LogManager.getLogger(GraphTree.class);

	public static final String HOSTROOT = "Sorted by host";
	public static final String VIEWROOT = "Sorted by view";
	public static final String SUMROOT = "Sums";
	public static final String TAGSROOT = "All tags";

	static public final int LEAF_GRAPHTITLE = 1;
	static public final int LEAF_HOSTNAME = 2;

	private GraphTree parent;
	private Map<Integer, GraphTree> pathsMap;
	//The node's name
	private String name;
	//The childs
	private Map<String, GraphTree> childsMap;
	//The graphs in this node
	private Map<String, GraphNode> graphsSet;

	/**
	 *  Private constructor, no one can generate an graph on the fly
	 *  
	 */
	private GraphTree(String name) {
		graphsSet = new TreeMap<String, GraphNode>(jrds.Util.nodeComparator);
		childsMap = new TreeMap<String, GraphTree>(jrds.Util.nodeComparator);
		this.name = name;
	}

	/**
	 * The only way to get a new graph
	 * @param root the graph's root name
	 * @return
	 */
	public static GraphTree makeGraph(String root) {
		GraphTree rootNode = new GraphTree(root);
		rootNode.pathsMap = new HashMap<Integer, GraphTree>();
		rootNode.pathsMap.put(rootNode.getPath().hashCode(), rootNode);
		return rootNode;
	}

	public GraphTree getByPath(String... path) {
		logger.trace(Arrays.asList(path) + " match " + name);
		logger.trace("childs: "+ childsMap);
		if(! path[0].equals(this.name))
			return null;
		if(path.length == 1)
			return this;
		GraphTree child = childsMap.get(path[1]);
		if(child != null)
			return child.getByPath(Arrays.copyOfRange(path, 1, path.length));

		return null;
	}

	public GraphTree getById(int id) {
		return pathsMap.get(id);
	}

	synchronized private void addChild(String childName) {
		if( ! childsMap.containsKey(childName)) {
			GraphTree newChild = new GraphTree(childName);
			childsMap.put(childName, newChild);
			newChild.parent = this;
			newChild.pathsMap = pathsMap;
			pathsMap.put(newChild.getPath().hashCode(), newChild);
		}
	}

	private void _addGraphByPath(LinkedList<String> path, GraphNode nodesGraph) {
		if(path.size() == 1 && nodesGraph != null ) {
				graphsSet.put(path.getLast(), nodesGraph);
		}
		else if(! path.isEmpty()){
			String pathElem = path.removeFirst();
			addChild(pathElem);
			getChildbyName(pathElem)._addGraphByPath(path, nodesGraph);
		}
	}

	/**
	 * Add a graph node provided it's full path, the last element will be the graph
	 * node name
	 * @param path
	 * @param nodesGraph
	 */
	public void addGraphByPath(List<String> path, GraphNode nodesGraph) {
		if(path.size() < 1) {
			logger.error("Path is empty : " + path + " for graph " + nodesGraph.getGraphTitle());
		}
		else
			_addGraphByPath(new LinkedList<String>(path), nodesGraph);
	}

	/**
	 * Create an empty path in a tree
	 * @param path
	 */
	public void addPath(String... path) {
		if(path.length < 1) {
			logger.error("Path is empty");
		}
		else
			_addGraphByPath(new LinkedList<String>(Arrays.asList(path)), null);
	}

	public GraphTree getChildbyName(String name) {
		return childsMap.get(name);
	}

	public String getName() {
		return name;
	}

	/**
	 * @return Returns the childsMap.
	 */
	public Map<String, GraphTree> getChildsMap() {
		return childsMap;
	}
	
	/**
	 * @return Returns the graphsSet.
	 */
	public Map<String, GraphNode>  getGraphsSet() {
		return graphsSet;
	}

	/**
	 * @param Filter, can be null
	 * @return
	 */
	public List<GraphNode> enumerateChildsGraph(Filter f) {
		List<GraphNode> retValue  = new ArrayList<GraphNode>();
		if(graphsSet != null) {
			if(f == null)
				retValue.addAll((Collection<GraphNode>) graphsSet.values());
			else {
				for(GraphNode g: graphsSet.values()) {
					String path = this.getPath() + "/" + g.getName();
					if(f.acceptGraph(g, path))
						retValue.add(g);
				}
			}
		}
		if(childsMap != null) {
			for(GraphTree child: childsMap.values()) {
				retValue.addAll(child.enumerateChildsGraph(f));
			}
		}	
		return retValue;
	}
	
	public List<GraphNode> enumerateChildsGraph() {
		return enumerateChildsGraph(null);
	}

	public String toString() {
		return name;
	}

	private StringBuilder _getPath() {
		StringBuilder retValue = null;
		if(parent == null)
			retValue = new StringBuilder();
		else
			retValue = parent._getPath();
		retValue.append("/");
		retValue.append(name);
		return retValue;
	}

	public String getPath() {
		return _getPath().toString();
	}
}
