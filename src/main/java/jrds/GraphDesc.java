package jrds;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jrds.Util.SiPrefix;
import jrds.probe.IndexedProbe;
import jrds.probe.UrlProbe;
import jrds.probe.jdbc.JdbcProbe;
import jrds.webapp.ACL;
import jrds.webapp.WithACL;

import org.apache.logging.log4j.*;
import org.rrd4j.ConsolFun;
import org.rrd4j.data.DataProcessor;
import org.rrd4j.data.Plottable;
import org.rrd4j.graph.RrdGraphConstants;
import org.rrd4j.graph.RrdGraphDef;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A classed used to store the static description of a graph
 * @author Fabrice Bacchella
 */
public class GraphDesc
implements Cloneable, WithACL {
    static final private Logger logger = LogManager.getLogger(GraphDesc.class);

    static public final ConsolFun DEFAULTCF = ConsolFun.AVERAGE;

    //  static final private String manySpace = "123456798ABCDEF0123465798ABCDEF0123456798ABCDEF0123465798ABCDEF0123456798ABCDEF0123465798ABCDEF0";
    static final private String MANYSPACE = "                                                                      ";

    public enum GraphType {
        NONE  {
            public String toString() {
                return "none";
            }
            public boolean datasource() {
                return true;
            }
            public boolean toPlot() {
                return false;
            }
            public boolean legend() {
                return false;
            }
        },
        PERCENTILE  {
            public String toString() {
                return "percentile";
            }
            public boolean datasource() {
                return true;
            }
            public boolean toPlot() {
                return false;
            }
            public boolean legend() {
                return false;
            }
        },
        LEGEND {
            public String toString() {
                return "legend";
            }
            public boolean datasource() {
                return true;
            }
            public boolean toPlot() {
                return false;
            }
            public boolean legend() {
                return true;
            }
        },
        PERCENTILELEGEND {
            @Override
            public String toString() {
                return "percentile legend";
            };
            public boolean datasource() {
                return false;
            }
            public boolean toPlot() {
                return false;
            };
            public boolean legend() {
                return true;
            };
        },
        COMMENT {
            public String toString() {
                return "comment";
            }
            public boolean datasource() {
                return false;
            }
            public boolean toPlot() {
                return false;
            }
            public boolean legend() {
                return true;
            }
        },
        LINE {
            public void draw(RrdGraphDef rgd, String sn, Color color, String legend) {
                rgd.line(sn, color, legend);
            };
            @Override
            public String toString() {
                return "line";
            };
            public boolean datasource() {
                return true;
            }
            public boolean toPlot() {
                return true;
            };
            public boolean legend() {
                return true;
            };
        },
        AREA {
            public void draw(RrdGraphDef rgd, String sn, Color color, String legend) {
                rgd.area(sn, color, legend);
            };
            @Override
            public String toString() {
                return "area";
            };
            public boolean datasource() {
                return true;
            }
            public boolean toPlot() {
                return true;
            };
            public boolean legend() {
                return true;
            };
        },
        STACK {
            public void draw(RrdGraphDef rgd, String sn, Color color, String legend) {
                rgd.stack(sn, color, legend);
            };
            @Override
            public String toString() {
                return "stack";
            };
            public boolean datasource() {
                return true;
            }
            public boolean toPlot() {
                return true;
            };
            public boolean legend() {
                return true;
            };
        };

        public void draw(RrdGraphDef rgd, String sn, Color color, String legend) {};

        /**
         * To check if it will generate a plot, for color calculation
         * @return
         */
        public abstract boolean toPlot();
        public abstract boolean datasource();

        /**
         * To check if it will generate a line in the legend block
         * @return
         */
        public abstract boolean legend();
    };

    //Old name kept
    static final public GraphType NONE = GraphType.NONE;
    static final public GraphType DATASOURCE = GraphType.NONE;
    static final public GraphType LINE = GraphType.LINE;
    static final public GraphType AREA = GraphType.AREA;
    static final public GraphType STACK = GraphType.STACK;
    static final public GraphType COMMENT = GraphType.COMMENT;

    private enum PathElement {
        HOST {
            public String resolve(GraphNode graph) {
                return graph.getProbe().getHost().getName();
            }
        },
        TITLE {
            public String resolve(GraphNode graph) {
                return graph.getGraphTitle();
            }
        },
        INDEX {
            public String resolve(GraphNode graph) {
                StringBuffer retValue = new StringBuffer("empty");
                if(graph.getProbe() instanceof IndexedProbe) {
                    retValue.setLength(0);
                    IndexedProbe ip = (IndexedProbe) graph.getProbe();
                    retValue.append(ip.getIndexName());
                    //Check to see if a label is defined and needed to add
                    String label = graph.getProbe().getLabel();
                    if(label != null) {
                        retValue.append(" (" + label + ")");
                    }
                }
                else {
                    logger.debug("Bad graph definition for " + graph);
                }
                return retValue.toString();
            }
        },
        URL {
            public String resolve(GraphNode graph) {
                String url = "";
                Probe<?,?> probe = graph.getProbe();
                if( probe instanceof UrlProbe) {
                    url =((UrlProbe) probe).getUrlAsString();
                }
                return url;
            }
        },
        JDBC {
            public String resolve(GraphNode graph) {
                return ( (JdbcProbe) graph.getProbe()).getUrlAsString();
            }
        },
        DISK {
            public String resolve(GraphNode graph) {
                return "Disk";
            }
        },
        NETWORK {
            public String resolve(GraphNode graph) {
                return "Network";
            }
        },
        TCP {
            public String resolve(GraphNode graph) {
                return "TCP";
            }
        },
        SERVICES {
            public String resolve(GraphNode graph) {
                return "Services";
            }
        },
        SYSTEM {
            public String resolve(GraphNode graph) {
                return "System";
            }
        },
        LOAD {
            public String resolve(GraphNode graph) {
                return "Load";
            }
        },
        DISKACTIVITY {
            public String resolve(GraphNode graph) {
                return "Disk activity";
            }
        },
        WEB {
            public String resolve(GraphNode graph) {
                return "Web";
            }
        },
        INTERFACES {
            public String resolve(GraphNode graph) {
                return "Interfaces";
            }
        },
        IP {
            public String resolve(GraphNode graph) {
                return "IP";
            }
        },
        MEMORY {
            public String resolve(GraphNode graph) {
                return "Memory";
            }
        },
        DATABASE{
            public String resolve(GraphNode graph) {
                return "Databases";
            }
        },
        DBINSTANCE {
            public String resolve(GraphNode graph) {
                JdbcProbe dbprobe = (JdbcProbe) graph.getProbe();
                return dbprobe.getUrlAsString();
            }
        };
        public abstract String resolve(GraphNode graph);
    }

    static final public PathElement HOST = PathElement.HOST;
    static final public PathElement SERVICES = PathElement.SERVICES;
    static final public PathElement NETWORK = PathElement.NETWORK;
    static final public PathElement IP = PathElement.IP;
    static final public PathElement TITLE = PathElement.TITLE;
    static final public PathElement INDEX = PathElement.INDEX;
    static final public PathElement URL = PathElement.URL;
    static final public PathElement JDBC = PathElement.JDBC;
    static final public PathElement WEB = PathElement.WEB;
    static final public PathElement SYSTEM = PathElement.SYSTEM;
    static final public PathElement DISK = PathElement.DISK;
    static final public PathElement DISKACTIVITY = PathElement.DISKACTIVITY;
    static final public PathElement MEMORY = PathElement.MEMORY;
    static final public PathElement TCP = PathElement.TCP;
    static final public PathElement LOAD = PathElement.LOAD;
    static final public PathElement INTERFACES = PathElement.INTERFACES;
    static final public PathElement DATABASE = PathElement.DATABASE;

    public enum Colors {
        //240°
        BLUE {
            @Override
            public Color getColor() {
                return Color.BLUE;
            }
        },
        //120°
        GREEN {
            @Override
            public Color getColor() {
                return Color.GREEN;
            }
        },
        //0°
        RED {
            @Override
            public Color getColor() {
                return Color.RED;
            }
        },
        //180°
        CYAN {
            @Override
            public Color getColor() {
                return Color.CYAN;
            }
        },
        // 47°
        ORANGE {
            @Override
            public Color getColor() {
                return Color.ORANGE;
            }
        },
        //180°
        TEAL {
            @Override
            public Color getColor() {
                return new Color(0,128,128);
            }
        },
        //60°
        YELLOW {
            @Override
            public Color getColor() {
                return Color.YELLOW;
            }
        },
        //300°
        MAGENTA {
            @Override
            public Color getColor() {
                return Color.MAGENTA;
            }
        },
        //0°
        PINK {
            @Override
            public Color getColor() {
                return Color.PINK;
            }
        },
        //0°
        BLACK {
            @Override
            public Color getColor() {
                return Color.BLACK;
            }
        },
        NAVY {
            @Override
            public Color getColor() {
                return new Color(0,0,128);
            }
        },
        //0°
        GRAY {
            @Override
            public Color getColor() {
                return Color.GRAY;
            }
        },
        //0°
        LIGHT_GRAY {
            @Override
            public Color getColor() {
                return Color.LIGHT_GRAY;
            }
        },
        DARK_GRAY {
            @Override
            public Color getColor() {
                return Color.DARK_GRAY;
            }
        },
        FUCHSIA {
            @Override
            public Color getColor() {
                return new Color(255,0,255);
            }
        },
        //Netscape alias for cyan
        AQUA {
            @Override
            public Color getColor() {
                return Color.CYAN;
            }
        },
        LIME {
            @Override
            public Color getColor() {
                return new Color(204,255,0);
            }
        },
        MAROON {
            @Override
            public Color getColor() {
                return new Color(128,0,0);
            }
        },
        OLIVE {
            @Override
            public Color getColor() {
                return new Color(128,128,0);
            }
        },
        PURPLE {
            @Override
            public Color getColor() {
                return new Color(128,0,128);
            }
        },
        SILVER {
            @Override
            public Color getColor() {
                return new Color(192,192,192);
            }
        },
        WHITE {
            @Override
            public Color getColor() {
                return Color.WHITE;
            }
        };

        public abstract Color getColor();
        public static final int length = Colors.values().length;
        public static final Color resolveIndex(int i) {
            return Colors.values()[ i % Colors.length].getColor();
        }
    };

    public static  final class DsDesc implements Cloneable {
        String name;
        String dsName;
        final String rpn;
        final GraphType graphType;
        final Color color;
        final String legend;
        final ConsolFun cf;
        final Integer percentile;
        static final class DsPath  {
            public final String host;
            public final String probe;
            DsPath(String host, String probe) {
                this.host = host;
                this.probe = probe;
            }
        };
        
        public String getDsName() {
			return dsName;
		}
		final DsPath dspath;
        DsDesc(String name, String dsName, String rpn,
                GraphType graphType, Color color, String legend,
                ConsolFun cf, String host, String probe) {
            this.name = name;
            this.dsName = dsName;
            this.rpn = rpn;
            this.percentile = null;
            this.graphType = graphType;
            this.color = color;
            this.legend = legend;
            this.cf = cf;
            if(host != null && probe != null) {
                this.dspath = new DsPath(host, probe);
            }
            else {
                this.dspath = null;
            }
        }
        public void chagneDsName(String newDsName)
        {
        	this.name=newDsName;
        	this.dsName=newDsName;
        }
        public DsDesc(String name, String rpn,
                GraphType graphType, Color color, String legend) {
            this.name = name;
            this.rpn = rpn;
            this.graphType = graphType;
            this.color = color;
            this.legend = legend;
            this.dsName = null;
            this.cf = null;
            this.percentile = null;
            this.dspath = null;
        }
        public DsDesc(String name, String dsName,
                Integer percentile,
                GraphType graphType, Color color) {
            this.name = name;
            this.dsName = dsName;
            this.percentile = percentile;
            this.graphType = graphType;
            this.color = color;
            this.rpn = null;
            this.legend = null;
            this.cf = null;
            this.dspath = null;
        }
        public DsDesc(String dsName, GraphType graphType, String legend, ConsolFun cf) {
            this.name = dsName;
            this.dsName = dsName;
            this.graphType = graphType;
            this.legend = legend;
            this.cf = cf;
            this.rpn = null;
            this.color = null;
            this.percentile = null;
            this.dspath = null;
        }
        public Object clone() throws CloneNotSupportedException {
        	DsDesc other=(DsDesc) super.clone();
        	return other;
        }
        public String toString() {
            return "DsDesc(" + name + "," + dsName + ",\"" + (rpn == null ? "" : rpn) + "\"," + graphType + "," + color + ",\"" + (legend == null ? "" : legend) + "\"," + cf + ")";
        }
    }

    private List<DsDesc> allds;
    private int width = 928;
    private int height = 206;
    private double upperLimit = Double.NaN;
    private double lowerLimit = 0;
    private String verticalLabel = null;
    private int lastColor = 0;
    private Map<String, List<?>> trees = new HashMap<String, List<?>>(2);
    private String graphName;
    private String name;
    private String graphTitle ="${graphdesc.name} on ${host}";
    private int maxLengthLegend = 0;
    private boolean siUnit = true;
    private boolean logarithmic = false;
    private Integer unitExponent = null;
    private boolean withLegend = true;  // To show the values block under the graph
    private boolean withSummary = true; // To show the summary with last update, period, etc. information block
    private ACL acl = ACL.ALLOWEDACL;
    private Class<Graph> graphClass = Graph.class;

    public static final class Dimension implements Cloneable{
        public int width = 0;
        public int height = 0;
        public Object clone() throws CloneNotSupportedException{
       return super.clone();
   
		 }
    };
    private Dimension dimension = null;


    /**
     * A constructor wich pre allocate the desired size
     * @param size the estimated number of graph that will be created
     */
    public GraphDesc(int size) {
        allds = new ArrayList<DsDesc>(size);
    }

    public GraphDesc() {
        allds = new ArrayList<DsDesc>();
    }

    public void add(String name, GraphType graphType) {
        add(name, name, null, graphType,
                Colors.resolveIndex(lastColor), name,
                DEFAULTCF, false, null, null, null);
        if(graphType.toPlot())
            lastColor++;
    }

    /**
     * Add a datastore that will not generate a graph
     *
     * @param name String
     */
    public void add(String name) {
        add(name, name, null, GraphType.NONE, null, null, DEFAULTCF, false, null, null, null);
    }

    /**
     * Add a plot, but only uses String as parameters, for the GraphFactory
     * @param name Name of the plot
     * @param dsName the datastore to use
     * @param rpn The RPN, used instead of the datastore
     * @param graphType
     * @param color
     * @param legend
     * @param consFunc
     * @param reversed
     * @param host
     * @param probe
     * @param subDsName
     */
    public void add(String name, String rpn,
            String graphType, String color, String legend,
            String consFunc, String reversed, String percentile,
            //The path to an external datastore
            String host, String probe, String dsName) {
        if(logger.isTraceEnabled())
            logger.trace("Adding " + name + ", " + rpn + ", " + graphType + ", " + color + ", " + legend + ", " + consFunc + ", " + reversed + ", " + host + ", " + probe);
        GraphType gt = null;
        if(graphType == null || "".equals(graphType)) {
            if(legend != null)
                gt = GraphType.COMMENT;
            else
                gt = GraphType.NONE;
        }
        else
            gt = GraphType.valueOf(graphType.toUpperCase());

        ConsolFun cf  = null;
        if(gt != GraphType.COMMENT) {
            cf = DEFAULTCF;
            if (consFunc != null && ! "".equals(consFunc))
                cf = ConsolFun.valueOf(consFunc.toUpperCase());
        }

        Color c = null;
        if(gt.toPlot()) {
            c = Color.WHITE;
            if(color != null && color.toUpperCase().matches("^#[0-9A-F]{6}")) {
                int r = Integer.parseInt(color.substring(1, 3), 16);
                int g = Integer.parseInt(color.substring(3, 5), 16);
                int b = Integer.parseInt(color.substring(5, 7), 16);
                c = new Color(r,g,b);
            }
            else if (color != null && ! "".equals(color)) {
                c = Colors.valueOf(color.toUpperCase()).getColor();
                if( c == null)
                    c = Color.getColor(color);
                if (c == null) {
                    logger.error("Cannot read color " + color);
                    c = Color.white;
                }
            }
            else {
                c = Colors.resolveIndex(lastColor);
                if(gt.toPlot())
                    lastColor++;

            }
        }
        if(name != null) {
            // If not a rpn, it must be a datastore
            if(gt.datasource() && rpn == null && dsName == null) {
                dsName = name;
            }
        }
        //If the name is missing, generate one ?
        else {
            name = Integer.toHexString((int)(Math.random() * Integer.MAX_VALUE));
        }
        //Auto generated legend
        if(legend == null && name != null && gt.legend())
            legend = name;

        Integer valPercentile = null;
        if(percentile != null && ! "".equals(percentile)) {
            valPercentile = jrds.Util.parseStringNumber(percentile, Integer.valueOf(0));
        }
        add(name, dsName, rpn, gt, c, legend, cf, reversed != null, valPercentile, host, probe);
    }

    public void add(String name, String dsName, String rpn,
            GraphType graphType, Color color, String legend,
            ConsolFun cf, boolean reversed, Integer percentile,
            //The path to an external datastore
            String host, String probe) {
        if(reversed) {
            String revRpn = "0, " + name + ", -";
            allds.add(
                    new DsDesc(name, dsName, rpn, GraphType.NONE, null, null, cf, host, probe));
            allds.add(
                    new DsDesc("rev_" + name, revRpn, graphType, color, null));
            allds.add(new DsDesc(name, GraphType.LEGEND, legend, cf));
        }
        else {
            allds.add(
                    new DsDesc(name, dsName, rpn, graphType, color, legend, cf, host, probe));
        }
        if(percentile != null) {
            String percentileName = "percentile" + percentile + "_" + name;
            String percentileLegend = percentile + "th percentile";
            Color percentilColor = color.darker();
            if(!reversed) {
                allds.add(
                        new DsDesc(percentileName, name, percentile, GraphType.LINE, percentilColor));
            }
            else {
                String revPercentilRpn = "0, " + percentileName + ", -";
                allds.add(
                        new DsDesc(percentileName, name, percentile, GraphType.NONE, null));
                allds.add(
                        new DsDesc("rev_" + percentileName, revPercentilRpn, GraphType.LINE, percentilColor, null));

            }
            allds.add(new DsDesc(percentileName, GraphType.PERCENTILELEGEND, percentileLegend, cf));
            maxLengthLegend = Math.max(maxLengthLegend, percentileLegend.length());
        }
        if(legend != null) {
            maxLengthLegend = Math.max(maxLengthLegend, legend.length());
        }
    }

    /**
     * return the RrdGraphDef for this graph, used the indicated probe
     *
     * @param probe Probe
     * @return RrdGraphDef
     * @throws IOException
     * @throws RrdException
     */
    public RrdGraphDef getGraphDef(Probe<?,?> probe) throws IOException {
        return getGraphDef(probe, null);
    }

    public RrdGraphDef getEmptyGraphDef() {
        RrdGraphDef retValue = new RrdGraphDef();
        if( ! Double.isNaN(lowerLimit))
            retValue.setMinValue(lowerLimit);
        if( ! Double.isNaN(upperLimit))
            retValue.setMaxValue(upperLimit);
        if (verticalLabel != null)
            retValue.setVerticalLabel(verticalLabel);
        if(this.siUnit)
            retValue.setBase(1000);
        else    
            retValue.setBase(1024);
        if(unitExponent != null) {
            retValue.setUnitsExponent(unitExponent);
        }
        retValue.setLogarithmic(logarithmic);
        retValue.setPoolUsed(true);
        retValue.setAntiAliasing(true);
        retValue.setTextAntiAliasing(true);
        retValue.setImageFormat("PNG");
        retValue.setWidth(getWidth());
        retValue.setHeight(getHeight());
        return retValue;
    }

    /**
     * Fill a GraphDef with values as defined by the graph desc
     * @param graphDef the GraphDef to configure
     * @param defProbe The probe to get values from
     * @param customData some custom data, they override existing values in the associated probe
     */
    public void fillGraphDef(RrdGraphDef graphDef, Probe<?, ?> defProbe,
            Map<String, ? extends Plottable> customData) {
        HostsList hl = defProbe.getHostList();
        List<DsDesc> toDo = new ArrayList<DsDesc>();
        //The datasources already found
        Set<String> datasources = new HashSet<String>();

        for(DsDesc ds: allds) {
            boolean complete = false;
            // not a data source, don't try to add it in datasources
            if(! ds.graphType.datasource()) {
                complete = true;
            }
            //The graph is a percentile
            else if(ds.percentile != null) {
                complete = true;
                graphDef.percentile(ds.name, ds.dsName, ds.percentile);
                datasources.add(ds.name);
            }
            //A rpn datasource
            else if (ds.rpn != null) {
                complete = true;
                if(! datasources.contains(ds.name)) {
                    graphDef.datasource(ds.name, ds.rpn);
                    datasources.add(ds.name);
                }
            }
            else if(ds.graphType == GraphType.LEGEND) {
                complete = true;                
            }
            //Does the datas existe in the provided values
            //It override existing values in the probe
            else if(customData != null && customData.containsKey(ds.dsName)) {
                complete = true;
                if( ! datasources.contains(ds.name)) {
                    graphDef.datasource(ds.name, customData.get(ds.dsName));
                    datasources.add(ds.name);
                    logger.trace(Util.delayedFormatString("custom data found for %s", ds.dsName));
                }
            }
            //Last but common case, datasource refers to a rrd
            //Or they might be on the associated rrd
            else {
                Probe<?,?> probe = defProbe;
                if(ds.dspath != null) {
                    if(logger.isTraceEnabled())
                        logger.trace("External probe path: " + ds.dspath.host + "/" + ds.dspath.probe + "/" + ds.dsName);
                    probe = hl.getProbeByPath(ds.dspath.host, ds.dspath.probe);
                    if(probe == null) {
                        logger.error("Invalide probe: " + ds.dspath.host + "/" + ds.dspath.probe);
                        continue;
                    }
                }
                if(! probe.dsExist(ds.dsName)) {
                    logger.error("Invalide datasource "  + ds.dsName + ", not found in " + probe);
                    continue;
                }

                complete = true;
                if( ! datasources.contains(ds.name)) {
                    String rrdName = probe.getRrdName();
                    graphDef.datasource(ds.name, rrdName, ds.dsName, ds.cf);                
                    datasources.add(ds.name);
                }
                else {
                    logger.error("Datasource '" + ds.name + "' defined twice in " + name + ", for found: " + ds);
                }
            }
            if (complete) {
                toDo.add(ds);
            }
            else {
                logger.debug("Error for " + ds);
                logger.error("No way to plot " + ds.name + " in " + name + " found");
            }
        }
        // The title line, only if values block is required
        if( withSummary) {
            graphDef.comment(""); //We simulate the color box
            graphDef.comment(MANYSPACE.substring(0, Math.min(maxLengthLegend, MANYSPACE.length()) + 2));
            graphDef.comment("Current");
            graphDef.comment("  Average");
            graphDef.comment("  Minimum");
            graphDef.comment("  Maximum");
            graphDef.comment("\\l");
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Datasource: " + datasources);
            logger.trace("Todo: " + toDo);
        }

        String shortLegend = withSummary ? " \\g": null;
        for(DsDesc ds: toDo) {
            ds.graphType.draw(graphDef, ds.name, ds.color, shortLegend);
            if(withSummary && ds.graphType.legend())
                addLegend(graphDef, ds.name, ds.graphType, ds.legend);
        }
    }

    /**
     * return the RrdGraphDef for this graph, used the indicated probe
     * any data can be overined of a provided map of Plottable
     * @param probe
     * @param ownData data used to overied probe's own values
     * @return
     * @throws IOException
     * @throws RrdException
     */
    public RrdGraphDef getGraphDef(Probe<?,?> defProbe, Map<String, ? extends Plottable> ownData) throws IOException {
        RrdGraphDef retValue = getEmptyGraphDef();
        fillGraphDef(retValue, defProbe, ownData);
        return retValue;
    }

    /**
     * return the RrdGraphDef for this graph, used the indicated probe
     * any data can be overridden of a provided map of Plottable
     * @param probe
     * @param ownData data used to override probe's own values
     * @return
     * @throws IOException
     * @throws RrdException
     */
    public DataProcessor getPlottedDatas(Probe<?,?> probe, Map<?, ?> ownData, long start, long end) throws IOException {
        DataProcessor retValue = new DataProcessor(start, end);
        String rrdName = probe.getRrdName();

        String lastName = null;
        for(DsDesc ds: allds) {
            boolean stack = ds.graphType == GraphType.STACK;
            boolean plotted = stack || ds.graphType == GraphType.LINE  || ds.graphType == GraphType.AREA;
            if (ds.rpn == null && ds.dsName != null) {
                //Does the datas existe in the provided values
                if(ownData != null && ownData.containsKey(ds.dsName) && ds.graphType == GraphType.LINE) {
                    retValue.addDatasource(ds.name, (Plottable) ownData.get(ds.dsName));
                }
                //Or they might be on the associated rrd
                else if(probe.dsExist(ds.dsName)) {
                    retValue.addDatasource(ds.name, rrdName, ds.dsName, ds.cf);                             
                }
            }
            else if(ds.rpn != null){
                retValue.addDatasource(ds.name, ds.rpn);
            }
            if(plotted && stack) {
                retValue.addDatasource("Plotted" + ds.name, lastName + ", " +  ds.name + ", +");
            }
            else if(plotted) {
                retValue.addDatasource("Plotted" + ds.name, ds.name);
            }
            lastName = ds.name; 
        }
        if(logger.isTraceEnabled()) {
            logger.trace("Datastore for " + getName());
            for(String s: retValue.getSourceNames())
                logger.trace("\t" + s);
        }
        return retValue;
    }

    protected void addLegend(RrdGraphDef def, String ds, GraphType gt, String legend) {
        if(legend == null)
            return;
        if(gt == GraphType.PERCENTILELEGEND) {
            def.comment(legend + "\\g");
            int missingLength = Math.min(maxLengthLegend - legend.length(), MANYSPACE.length()) + 2;
            if(missingLength > 0)
                def.comment(MANYSPACE.substring(0, missingLength));
            def.gprint(ds, ConsolFun.MAX, "%6.2f%s");
            def.comment("\\l");
        }
        else if(gt == GraphType.COMMENT) {
            def.comment(legend + "\\l");
        }
        else if(gt != GraphType.NONE) {
            def.comment(legend + "\\g");
            int missingLength = Math.min(maxLengthLegend - legend.length(), MANYSPACE.length()) + 2;
            if(missingLength > 0)
                def.comment(MANYSPACE.substring(0, missingLength));
            def.gprint(ds, ConsolFun.LAST, "%6.2f%s");
            def.gprint(ds, ConsolFun.AVERAGE, "%8.2f%s");
            def.gprint(ds, ConsolFun.MIN, "%8.2f%s");
            def.gprint(ds, ConsolFun.MAX, "%8.2f%s");
            def.comment("\\l");
        }
    }

    /**
     * @return Returns the graphTitle.
     */
    public String getGraphName() {
        return graphName;
    }

    /**
     * @param graphTitle The graphTitle to set.
     */
    public void setGraphName(String graphTitle) {
        this.graphName = graphTitle;
    }

    /**
     * @return Returns the height of the graphic zone.
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height The height of the graphic zone to set.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return Returns the width of the graphic zone.
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width The width of the graphic zone to set.
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return Returns the lowerLimit.
     */
    public double getLowerLimit() {
        return lowerLimit;
    }

    /**
     * @param lowerLimit The lowerLimit to set.
     */
    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    /**
     * @return Returns the upperLimit.
     */
    public double getUpperLimit() {
        return upperLimit;
    }

    /**
     * @param upperLimit The upperLimit to set.
     */
    public void setUpperLimit(double upperLimit) {
        this.upperLimit = upperLimit;
    }

    /**
     * @return Returns the verticalLabel.
     */
    public String getVerticalLabel() {
        return verticalLabel;
    }

    /**
     * @param verticalLabel The verticalLabel to set.
     */
    public void setVerticalLabel(String verticalLabel) {
        this.verticalLabel = verticalLabel;
    }

    public void colorsReset() {
        lastColor = 0;
    }

    /**
     * @return Returns the viewTree.
     */
    public LinkedList<String> getViewTree(GraphNode graph) {
        return getTree(graph, PropertiesManager.VIEWSTAB);
    }

    /**
     * @return Returns the hostTree.
     */
    public LinkedList<String> getHostTree(GraphNode graph) {
        return getTree(graph, PropertiesManager.HOSTSTAB);
    }

    public LinkedList<String> getTree(GraphNode graph, String tabname) {
        List<?> elementsTree = trees.get(tabname);
        LinkedList<String> tree = new LinkedList<String>();
        if(elementsTree == null)
            return tree;
        for (Object o: elementsTree) {
            if (o instanceof String) {
                String pathElem = jrds.Util.parseTemplate((String) o, graph.getProbe(), this, graph.getProbe().getHost());
                tree.add(pathElem);
            }
            else if (o instanceof PathElement)
                tree.add( ( (PathElement) o).resolve(graph));
        }
        return tree;
    }

    
    public List<DsDesc> getAllds() {
		return allds;
	}

	public void addTree(String tab, List<?> tree) {
        trees.put(tab, tree);
        logger.trace(jrds.Util.delayedFormatString("Adding tree %s to tab %s", tree, tab));
    }

    public void setTree(String tab, List<?> tree) {        
        addTree(tab, tree);
    }

    /**
     * @return Returns the graphTitle.
     */
    public String getGraphTitle() {
        return graphTitle;
    }
    /**
     * @param graphTitle The graphTitle to set.
     */
    public void setGraphTitle(String graphTitle) {
        this.graphTitle = graphTitle;
    }

    public static final PathElement resolvPathElement(String name) {
        return PathElement.valueOf(name.toUpperCase());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSiUnit() {
        return siUnit;
    }

    public void setSiUnit(boolean siUnit) {
        this.siUnit = siUnit;
    }

    public void setUnitExponent(String exponent) {
        if("".equals(exponent))
            exponent = SiPrefix.FIXED.name();
        try {
            unitExponent = SiPrefix.valueOf(exponent).getExponent();
        } catch (IllegalArgumentException e1) {
        }
        if(unitExponent == null) {
            try {
                unitExponent = new Integer(exponent);
            } catch (NumberFormatException e) {
                logger.debug("Base unit not identified: " + exponent);
            }
        }
    }

    public Integer getUnitExponent() {
        return unitExponent;
    }

    /**
     * @return the dimension of the graphic object
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * @param dimension the dimension of the graphic object to set
     */
    public void setDimension(int height, int width) {
        dimension = new Dimension();
        dimension.height = height;
        dimension.width = width;
    }

    public int getLegendLines() {
        int numlegend = 0;
        for(DsDesc dd: allds) {
            if (dd.graphType.legend() && dd.legend != null && withSummary)
                numlegend++;
        }
        return numlegend;
    }

    private static final class ImageParameters {
        int xsize;
        int ysize;
        int unitslength;
        int xorigin;
        int yorigin;
        int xgif, ygif;
    }

    static private final double LEGEND_LEADING_SMALL = 0.7; // chars
    static private final int PADDING_LEFT = 10; // pix
    static private final int PADDING_TOP = 12; // pix
    static private final int PADDING_TITLE = 6; // pix
    static private final int PADDING_RIGHT = 16; // pix
    static private final int PADDING_PLOT = 2; //chars
    static private final int PADDING_BOTTOM = 6; //pix

    static private final int DEFAULT_UNITS_LENGTH = 9;

    private static final String DUMMY_TEXT = "Dummy";
    private static final Font smallFont = RrdGraphConstants.DEFAULT_SMALL_FONT; // ok
    private static final Font largeFont = RrdGraphConstants.DEFAULT_LARGE_FONT; // ok

    private double getFontHeight(FontRenderContext frc, Font font) {
        LineMetrics lm = font.getLineMetrics(DUMMY_TEXT, frc);
        return lm.getAscent() + lm.getDescent();
    }

    private double getSmallFontHeight(FontRenderContext frc) {
        return getFontHeight(frc, smallFont);
    }

    private double getLargeFontHeight(FontRenderContext frc) {
        return getFontHeight(frc, largeFont);
    }

    private double getStringWidth(Font font, FontRenderContext frc) {
        return font.getStringBounds("a", 0, 1, frc).getBounds().getWidth();
    }

    private double getSmallFontCharWidth(FontRenderContext frc) {
        return getStringWidth(smallFont, frc);
    }

    private double getSmallLeading(FontRenderContext frc) {
        return getSmallFontHeight(frc) * LEGEND_LEADING_SMALL;
    }

    public void initializeLimits(Graphics2D g2d) {
        FontRenderContext frc  = g2d.getFontRenderContext();
        ImageParameters im = new ImageParameters();
        int summaryLines =  withSummary ?5:0;

        im.xsize = getWidth();
        im.ysize = getHeight();
        im.unitslength = DEFAULT_UNITS_LENGTH;
        im.xorigin = (int) (PADDING_LEFT + im.unitslength * getSmallFontCharWidth(frc));
        im.xorigin += getSmallFontHeight(frc);
        im.yorigin = PADDING_TOP + im.ysize;
        if(graphTitle != null && ! "".equals(graphTitle))
            im.yorigin += getLargeFontHeight(frc) + PADDING_TITLE;
        im.xgif = PADDING_RIGHT + im.xsize + im.xorigin;
        im.ygif = im.yorigin + (int) (PADDING_PLOT * getSmallFontHeight(frc));
        im.ygif += ( (int) getSmallLeading(frc) + summaryLines ) * ( getLegendLines() + summaryLines);
        im.ygif += PADDING_BOTTOM;
        setDimension(im.ygif, im.xgif);
    }

    public void addACL(ACL acl) {
        this.acl = this.acl.join(acl);
    }

    public ACL getACL() {
        return acl;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        GraphDesc newgd =  (GraphDesc) super.clone();
        newgd.allds=new ArrayList<DsDesc>(this.allds.size());
        for(DsDesc dsDesc:this.allds)
        {
        	newgd.allds.add((DsDesc) dsDesc.clone());
        }
        
        return newgd;
    }

    public Document dumpAsXml() throws ParserConfigurationException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element root = 
                (Element) document.createElement("graphdesc"); 
        document.appendChild(root);
        root.appendChild(document.createElement("name")).setTextContent(name);
        if(graphName != null)
            root.appendChild(document.createElement("graphName")).setTextContent(graphName);
        if(graphClass != null) {
            root.appendChild(document.createElement("graphClass")).setTextContent(graphClass.getCanonicalName());
        }
        if(graphTitle != null)
            root.appendChild(document.createElement("graphTitle")).setTextContent(graphTitle);
        Element unit = document.createElement("unit");
        if(siUnit) {
            root.appendChild(unit).appendChild(document.createElement("SI"));
        }
        if(unitExponent != null) {
            for(SiPrefix unity: SiPrefix.values()) {
                if(unitExponent == unity.getExponent()) {
                    String suffix = unity.toString();
                    if (unity == SiPrefix.FIXED) {
                        suffix = "";
                    }
                    root.appendChild(unit).appendChild(document.createElement("base")).setTextContent(suffix);
                    break;
                }
            }
        }
        if(verticalLabel != null)
            root.appendChild(document.createElement("verticalLabel")).setTextContent(verticalLabel);
        if(! (lowerLimit == 0))
            root.appendChild(document.createElement("lowerLimit")).setTextContent(Double.toString(lowerLimit));
        if(! Double.isNaN(upperLimit))
            root.appendChild(document.createElement("upperLimit")).setTextContent(Double.toString(upperLimit));
        if(logarithmic)
            root.appendChild(document.createElement("logarithmic"));
        int i=0;
        //it will contain the number of dsdesc to skip
        int skip=0;
        for(DsDesc curs: allds) {
            DsDesc e = curs;
            if(skip-- > 0) {
                i++;
                continue;
            }
            boolean reversed = false;
            if(i + 2 <= allds.size() && allds.get(i+1).name.startsWith("rev_")) {
                reversed = true;
                skip = 2;
                DsDesc rev = allds.get(i+1);
                DsDesc leg = allds.get(i+2);
                e = new DsDesc(curs.name, curs.dsName, curs.rpn, rev.graphType, rev.color, leg.legend, curs.cf, null, null);
            }
            Element specElement = (Element) root.appendChild(document.createElement("add"));
            specElement.appendChild(document.createElement("name")).setTextContent(e.name);
            if(! e.name.equals(e.dsName) && e.dsName != null)
                specElement.appendChild(document.createElement("dsName")).setTextContent(e.dsName);
            if(e.rpn != null) {
                specElement.appendChild(document.createElement("rpn")).setTextContent(e.rpn);
            }
            if(reversed) {
                specElement.appendChild(document.createElement("reversed"));
            }
            specElement.appendChild(document.createElement("graphType")).setTextContent(e.graphType.toString());
            if(e.legend != null)
                specElement.appendChild(document.createElement("legend")).setTextContent(e.legend);
            i++;
        }
        for(Map.Entry<String, List<?>> e: trees.entrySet()) {
            Element hostTreeElement =  (Element) root.appendChild(document.createElement("tree"));
            hostTreeElement.setAttribute("tab", e.getKey());
            for(Object o: e.getValue()) {
                Element pe = document.createElement("pathstring");
                pe.setTextContent(o.toString());
                hostTreeElement.appendChild(pe);
            }            
        }
        return document;
    }

    /**
     * @return the logarithmic
     */
    public boolean isLogarithmic() {
        return logarithmic;
    }

    /**
     * @param logarithmic the logarithmic to set
     */
    public void setLogarithmic(boolean logarithmic) {
        this.logarithmic = logarithmic;
    }

    /**
     * @return the withLegend
     */
    public boolean withLegend() {
        return withLegend;
    }

    /**
     * @param withLegend the withLegend to set
     */
    public void setWithLegend(boolean withLegend) {
        this.withLegend = withLegend;
    }

    /**
     * @return the withValues
     */
    public boolean withSummary() {
        return withSummary;
    }

    /**
     * @param withValues the withValues to set
     */
    public void setWithSummary(boolean withSummary) {
        this.withSummary = withSummary;
    }

    /**
     * @return the graphClass
     */
    public Class<Graph> getGraphClass() {
        return graphClass;
    }

    /**
     * @param graphClass the graphClass to set
     */
    public void setGraphClass(Class<Graph> graphClass) {
        this.graphClass = graphClass;
    }


}

