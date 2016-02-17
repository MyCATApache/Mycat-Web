package jrds.webapp;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;
import jrds.webapp.Discover.ProbeDescSummary;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public abstract class DiscoverAgent {
    protected enum DojoType {
        ToggleButton {
            @Override
            public void doNode(JrdsElement parent, FieldInfo fi) {
                Element button = parent.getOwnerDocument().createElement("button");
                button.setAttribute("id", fi.id);
                button.setAttribute("name", fi.id);
                button.setAttribute("value", fi.value );

                button.setAttribute("iconClass", "dijitCheckBoxIcon");
                button.setAttribute("dojoType", "dijit.form.ToggleButton");
                button.setTextContent(fi.label);
                parent.appendChild(button);
            }
        },
        TextBox {
            @Override
            public void doNode(JrdsElement parent, FieldInfo fi) {
                Element label = parent.getOwnerDocument().createElement("label");
                label.setAttribute("for", fi.id);
                label.setTextContent(fi.label);
                parent.appendChild(label);

                Element input = parent.getOwnerDocument().createElement("input");
                input.setAttribute("dojoType", "dijit.form.TextBox");
                input.setAttribute("trim", "true");
                input.setAttribute("id", fi.id);
                input.setAttribute("name", fi.id);
                input.setAttribute("value", fi.value);
                parent.appendChild(input);
            }
        };
        public abstract void  doNode(JrdsElement parent, FieldInfo fi);
    };

    public static final class FieldInfo {
        public String id;
        public String label;
        public String value = "";
        public DojoType dojoType;
    };

    private final Logger namedLogger;
    final Set<Class<?>> validClasses;

    protected DiscoverAgent(String name, Class<?>... validClasses) {
        namedLogger = LogManager.getLogger("jrds.DiscoverAgent." + name);
        this.validClasses = new HashSet<Class<?>>(validClasses.length);
        for(Class<?> c: validClasses) {
            this.validClasses.add(c);
        }
    }

    /**
     * Do some specific discover that can't be done on enumerated probes
     * @param hostname
     * @param hostElement
     * @param probdescs
     * @param request
     */
    public void discoverPre(String hostname, JrdsElement hostEleme,
            Map<String, JrdsDocument> probdescs, HttpServletRequest request) {
    }

    public void discoverPost(String hostname, JrdsElement hostEleme,
            Map<String, JrdsDocument> probdescs, HttpServletRequest request) {
    }

    /**
     * Try to add the indicated probe
     * @param hostElement
     * @param summary
     * @param request
     * @return
     */
    public abstract void addProbe(JrdsElement hostElement, ProbeDescSummary summary, HttpServletRequest request);

    public abstract List<FieldInfo> getFields();

    /**
     * Return try if we should try to discover the probes for this host using this agent
     * @param hostname
     * @param request
     * @return
     */
    public abstract boolean exist(String hostname, HttpServletRequest request);

    /**
     * Add the connection for this agent to the host if needed
     * @param hostElement
     * @param request
     */
    public abstract void addConnection(JrdsElement hostElement, HttpServletRequest request);

    public abstract boolean isGoodProbeDesc(ProbeDescSummary summary);

    public void doHtmlDiscoverFields(JrdsDocument document) {
        try {
            List<FieldInfo> fields = getFields();
            log(Level.DEBUG, "Fields: %s", fields);

            JrdsElement localRoot = document.getRootElement().addElement("div");
            for(FieldInfo f: fields) {
                f.dojoType.doNode(localRoot, f);
            }
        } catch (DOMException e) {
            log(Level.ERROR, e, "Invalid DOM: %s", e);
        }
    }

    /**
     * This method add a probe to the current host document
     * @param hostDom the host document
     * @param probe the Name of the probe
     * @param label the label of the probe
     * @param argsTypes a list of type for the argument
     * @param argsValues a list of value for the argument
     * @return the generated element for this probe
     */
    public JrdsElement addProbe(JrdsElement hostElement, String probe, String label, List<String> argsTypes, List<String> argsValues, Map<String, String> beans) {
        JrdsElement probeElement = hostElement.addElement("probe");
        probeElement.setAttribute("type", probe);
        addArgsList(probeElement, argsTypes, argsValues, beans);
        if(label != null) {
            probeElement.setAttribute("label", label);
        }
        return probeElement;
    }

    /**
     * This method add a probe to the current host document
     * @param hostDom the host document
     * @param probe the Name of the probe
     * @param argsTypes a list of type for the argument
     * @param argsValues a list of value for the argument
     * @return the generated element for this probe
     */
    public JrdsElement addProbe(JrdsElement hostElement, String probe, List<String> argsTypes, List<String> argsValues, Map<String, String> beans) {
        return addProbe(hostElement, probe, null, argsTypes, argsValues, beans);
    }

    protected Element addConnexion(JrdsElement hostElem, String connexionClass, List<String> argsTypes, List<String> argsValues, Map<String, String> beans) {
        JrdsElement cnxElement = hostElem.addElement("type", String.format("type=%s", connexionClass));
        addArgsList(cnxElement, argsTypes, argsValues, beans);
        return cnxElement;
    }

    private void addArgsList(JrdsElement element, List<String> argsTypes, List<String> argsValues, Map<String, String> beans) {
        if(beans != null && ! beans.isEmpty()) {
            for(Map.Entry<String, String> bean: beans.entrySet()) {
                JrdsElement arg = element.addElement("attr");
                arg.setAttribute("name", bean.getKey());
                arg.setTextContent(bean.getValue());

            }
        }
        if(argsTypes != null && argsTypes.size() > 0 && argsTypes.size() == argsValues.size()) {
            for(int i=0; i < argsTypes.size(); i++) {
                Element arg = element.addElement("arg");
                arg.setAttribute("type", argsTypes.get(i));
                arg.setAttribute("value", argsValues.get(i));
                element.appendChild(arg);
            }
        }
    }

    protected void log(Level l, Throwable e, String format, Object... elements) {
        jrds.Util.log(this, namedLogger, l, e, format, elements);
    }

    protected void log(Level l, String format, Object... elements) {
        jrds.Util.log(this, namedLogger, l, null, format, elements);
    }


}
