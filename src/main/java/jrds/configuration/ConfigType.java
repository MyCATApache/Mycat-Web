package jrds.configuration;

import jrds.factories.xml.JrdsDocument;
import jrds.factories.xml.JrdsElement;

public enum ConfigType {
    FILTER {
        public String getName(JrdsDocument d) {
            return getNameByElement(d);
        }
        @Override
        public String getRootNode() {
            return "filter";
        }
    },
    HOSTS {
        public String getName(JrdsDocument d) {
            return getNameByAttribute(d);
        }
        @Override
        public String getRootNode() {
            return "host";
        }
    },
    SUM {
        public String getName(JrdsDocument d) {
            return getNameByAttribute(d);
        }
        @Override
        public String getRootNode() {
            return "sum";
        }
    },
    TAB {
        public String getName(JrdsDocument d) {
            return getNameByAttribute(d);
        }
        @Override
        public String getRootNode() {
            return "tab";
        }
    },
    MACRODEF {
        public String getName(JrdsDocument d) {
            return getNameByAttribute(d);
        }
        @Override
        public String getRootNode() {
            return "macrodef";
        }
    },
    GRAPH {
        public String getName(JrdsDocument d) {
            return getNameByElement(d);
        }
        @Override
        public String getRootNode() {
            return "graph";
        }
    },
    GRAPHDESC {
        public String getName(JrdsDocument d) {
            return getNameByElement(d);
        }
        @Override
        public String getRootNode() {
            return "graphdesc";
        }
    },
    PROBEDESC {
        public String getName(JrdsDocument d) {
            return getNameByElement(d);
        }
        @Override
        public String getRootNode() {
            return "probedesc";
        }
    },
    LISTENER {
        public String getName(JrdsDocument d) {
            String name = getNameByAttribute(d);
            if (name == null || name.isEmpty()) {
                name  = d.getRootElement().getAttribute("class");
            }
            return name != null ? name.trim() : null;
        }
        @Override
        public String getRootNode() {
            return "listener";
        }
    };

    public abstract String getName(JrdsDocument d);
    public abstract String getRootNode();

    private static String getNameByAttribute(JrdsDocument d) {
        String name = d.getRootElement().getAttribute("name");
        if(name != null) {
            return name.trim();
        }
        return null;
    }

    private static String getNameByElement(JrdsDocument d) {
        JrdsElement nameElement = d.getRootElement().getElementbyName("name");
        if(nameElement != null) {
            return nameElement.getTextContent() != null ? nameElement.getTextContent().trim() : null;
        }
        return null;
    }

}
