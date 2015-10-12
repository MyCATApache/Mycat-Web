package jrds.configuration;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jrds.Graph;
import jrds.GraphDesc;
import jrds.GraphNode;
import jrds.PropertiesManager;
import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;

public class GraphDescBuilder extends ConfigObjectBuilder<GraphDesc> {
    private final BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
    private final Graphics2D g2d = img.createGraphics();

    public GraphDescBuilder() {
        super(ConfigType.GRAPHDESC);
    }

    @Override
    GraphDesc build(JrdsDocument n) throws InvocationTargetException {
        try {
            return makeGraphDesc(n);
        } catch (Exception e) {
            throw new InvocationTargetException(e, GraphDescBuilder.class.getName());
        }
    }
    public GraphDesc makeGraphDesc(JrdsDocument n) throws Exception {
        JrdsElement subnode = n.getRootElement();

        GraphDesc gd = new GraphDesc();

        //Identify the optionnal custom GraphDesc class
        JrdsElement graphClass = subnode.getElementbyName("graphClass");
        if(graphClass != null) {
            String className = graphClass.getTextContent().trim();
            if(! "".equals(className)) {
                @SuppressWarnings("unchecked")
                Class<Graph> clazz = (Class<Graph>) pm.extensionClassLoader.loadClass(className);
                // Check a valid constructor
                Constructor<Graph> c = clazz.getConstructor(GraphNode.class);
                if (c !=null )
                    gd.setGraphClass(clazz);
                else
                    throw new IllegalArgumentException("Invalid constructor");
            }
            else {
                throw new IllegalArgumentException("Empty graphClass");
            }
        }

        setMethod(subnode.getElementbyName("name"), gd, "setName");
        setMethod(subnode.getElementbyName("graphName"), gd, "setGraphName");
        setMethod(subnode.getElementbyName("verticalLabel"), gd, "setVerticalLabel");
        setMethod(subnode.getElementbyName("graphTitle"), gd, "setGraphTitle");
        setMethod(subnode.getElementbyName("upperLimit"), gd, "setUpperLimit", Double.TYPE);
        setMethod(subnode.getElementbyName("lowerLimit"), gd, "setLowerLimit", Double.TYPE);
        setMethod(subnode.getElementbyName("height"), gd, "setHeight", Integer.TYPE);
        setMethod(subnode.getElementbyName("width"), gd, "setWidth", Integer.TYPE);

        doACL(gd, n, subnode);

        //Vertical label should never be empty
        if(gd.getVerticalLabel() == null)
            gd.setVerticalLabel("");

        gd.setWithLegend(subnode.getElementbyName("nolegend") == null);
        gd.setWithSummary(subnode.getElementbyName("novalues") == null);
        gd.setLogarithmic(subnode.getElementbyName("logarithmic") != null);

        JrdsElement unitElem = subnode.getElementbyName("unit");
        if(unitElem != null) {
            setMethod(unitElem.getElementbyName("base"), gd, "setUnitExponent");
            if(unitElem.getElementbyName("binary") != null)
                gd.setSiUnit(false);
            else if(unitElem.getElementbyName("SI") != null)
                gd.setSiUnit(true);
        }

        for(JrdsElement addnode: subnode.getChildElements()) {
            if(! "add".equals(addnode.getNodeName()) && ! "addpath".equals(addnode.getNodeName()) )
                continue;
            Map<String, String> elements = new HashMap<String, String>(10);
            boolean withPath = false;
            for(JrdsElement child: addnode.getChildElements()) {
                if("path".equals(child.getNodeName())) {
                    withPath = true;
                    for(JrdsElement hostchild: child.getChildElements()) {
                        String key = hostchild.getNodeName();
                        String value = hostchild.getTextContent();
                        if(value != null) {
                            value = value.trim();
                        }
                        elements.put("path" + key, value);
                    }
                }
                else {
                    String key = child.getNodeName();
                    String value = child.getTextContent();
                    if(value != null) {
                        value = value.trim();
                    }
                    elements.put(key, value);
                }
            }
            String addName = elements.get("name");
            String addgraphType = elements.get("graphType");
            String addColor = elements.get("color");
            String addLegend = elements.get("legend");
            String addrpn = elements.get("rpn");
            String consFunc = elements.get("cf");
            String reversed = elements.get("reversed");	
            String percentile = elements.get("percentile"); 
            if(elements.containsKey("percentile") && "".equals(percentile))
                percentile="95";
            String host = null;
            String probe = null;
            String dsName = null;
            if(withPath) {
                host = elements.get("pathhost");
                probe = elements.get("pathprobe");
                dsName = elements.get("pathdsName");
            }
            else 
                dsName = elements.get("dsName");

            gd.add(addName, addrpn, addgraphType, addColor, addLegend, consFunc, reversed, percentile, host, probe, dsName);
        }
        
        gd.setTree(PropertiesManager.HOSTSTAB, enumerateTree(subnode.getElementbyName("hosttree")));
        gd.setTree(PropertiesManager.VIEWSTAB, enumerateTree(subnode.getElementbyName("viewtree")));
        for(JrdsElement treenode: subnode.getChildElementsByName("tree")) {
            String treetab = treenode.getAttribute("tab");
            gd.setTree(treetab, enumerateTree(treenode));
        }

        gd.initializeLimits(g2d);
        return gd;
    }

    List<Object> enumerateTree(JrdsElement roottree) {
        if(roottree == null)
            return Collections.emptyList();
        List<JrdsElement> path = roottree.getChildElements();
        if(path.isEmpty())
            return Collections.emptyList();

        List<Object> pathString = new ArrayList<Object>(path.size());
        for(JrdsElement te: path) {
            Object value;
            if("pathelement".equals(te.getNodeName()))
                value = GraphDesc.resolvPathElement(te.getTextContent());
            else 
                value = te.getTextContent();
            pathString.add(value);
        }
        return pathString;
    }

}
