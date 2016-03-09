package jrds.configuration;

import jrds.Macro;
import jrds.Util;
import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;

import org.apache.logging.log4j.*;
import org.w3c.dom.DocumentFragment;

public class MacroBuilder extends ConfigObjectBuilder<Macro> {
    static final private Logger logger = LogManager.getLogger(MacroBuilder.class);

    public MacroBuilder() {
        super(ConfigType.MACRODEF);
    }

    @Override
    Macro build(JrdsDocument n) {
        return makeMacro(n);
    }

    public Macro makeMacro(JrdsDocument n) {
        Macro m = new Macro();
        String name =  n.getRootElement().getAttribute("name");
        logger.debug(Util.delayedFormatString("Building macro %s", name));
        if(name != null && ! "".equals(name)) {
            m.setName(name);
        }

        JrdsElement macrodefnode = n.getRootElement();
        DocumentFragment df = n.createDocumentFragment();
        df.appendChild(macrodefnode.getParentNode().removeChild(macrodefnode.getParent()));
        m.setDf(df);

        return m;
    }

}
