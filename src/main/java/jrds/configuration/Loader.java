package jrds.configuration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jrds.Util;
import jrds.factories.xml.EntityResolver;
import jrds.factories.xml.JrdsDocument;

import org.apache.logging.log4j.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

class Loader {

    static final private Logger logger = LogManager.getLogger(Loader.class);

    private static final FileFilter filter = new FileFilter(){
        public boolean accept(File file) {
            return (! file.isHidden()) && (file.isDirectory()) || (file.isFile() && file.getName().endsWith(".xml"));
        }
    };

    private final AtomicInteger threadCount = new AtomicInteger(0);
    private final ExecutorService tpool =  Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2, 
            new ThreadFactory() {
        public Thread newThread(Runnable r) {
            String threadName = "DomParser" + threadCount.getAndIncrement();
            Thread t = new Thread(r, threadName);
            t.setDaemon(true);
            logger.debug(Util.delayedFormatString("New thread name: %s", threadName));
            return t;
        }
    });

    private final ThreadLocal<DocumentBuilder> localDocumentBuilder = new ThreadLocal<DocumentBuilder>(){
        @Override
        protected DocumentBuilder initialValue() {
            try {
                DocumentBuilder dbuilder = instance.newDocumentBuilder();
                dbuilder.setEntityResolver(new EntityResolver());
                dbuilder.setErrorHandler(new ErrorHandler() {
                    public void error(SAXParseException exception) throws SAXException {
                        throw exception;
                    }
                    public void fatalError(SAXParseException exception) throws SAXException {
                        throw exception;
                    }
                    public void warning(SAXParseException exception) throws SAXException {
                        throw exception;
                    }
                });
                return dbuilder;
            } catch (ParserConfigurationException e) {
                throw new RuntimeException("Can't get document builder instance", e);
            }
        }
    };

    final private Map<ConfigType, Map<String, JrdsDocument>> repositories = new HashMap<ConfigType, Map<String, JrdsDocument>>(ConfigType.values().length);
    final private Map<String, ConfigType> nodesTypes = new HashMap<String, ConfigType>(ConfigType.values().length);
    private final DocumentBuilderFactory instance;

    public Loader() throws ParserConfigurationException {
        this(false);
    }

    public Loader(boolean strict) throws ParserConfigurationException {
        instance = DocumentBuilderFactory.newInstance();
        //Focus on content, not structure
        instance.setIgnoringComments(true);
        instance.setValidating(strict);
        instance.setIgnoringElementContentWhitespace(true);
        instance.setCoalescing(true);
        instance.setExpandEntityReferences(true);

        for(ConfigType t: ConfigType.values()) {
            repositories.put(t, new ConcurrentHashMap<String, JrdsDocument>());
            nodesTypes.put(t.getRootNode(), t);
        }
    }

    public Map<String, JrdsDocument> getRepository(ConfigType t) {
        return repositories.get(t);
    }

    public void setRepository(ConfigType t, Map<String, JrdsDocument> mapnodes) {
        repositories.put(t, mapnodes);
    }

    public void importUrl(URI ressourceUri) {
        URL ressourceUrl = null;
        try {
            ressourceUrl = ressourceUri.toURL();
            logger.debug("Importing " + ressourceUrl);
            String protocol = ressourceUrl.getProtocol();
            if("file".equals(protocol)) {
                String fileName = ressourceUrl.getFile();
                File imported = new File(ressourceUrl.toURI());
                if(imported.isDirectory())
                    importDir(imported);
                else if(fileName.endsWith(".jar"))
                    importJar(new JarFile(imported));
            }
            else if("jar".equals(protocol)) {
                JarURLConnection cnx = (JarURLConnection)ressourceUrl.openConnection();
                importJar(cnx.getJarFile());
            }
            else {
                logger.error("ressource " + ressourceUrl + " can't be loaded" );
            }
        } catch (IOException e) {
            logger.error("Invalid URL " + ressourceUrl + ": " + e);
        } catch (URISyntaxException e){
            logger.error("Invalid URL " + ressourceUrl + ": " + e);
        }
    }

    public void importDir(File path) {
        logger.trace("Importing directory " + path);
        if(! path.isDirectory()) {
            logger.warn(path + " is not a directory");
            return;
        }
        //listFiles can return null
        File[] foundFiles = path.listFiles(filter);
        if(foundFiles == null) {
            logger.error("Failed to import " + path);
            return;
        }
        for(File f: foundFiles) {
            if(f.isDirectory()) {
                importDir(f);
            }
            else {
                try {
                    logger.trace("Will import " + f);
                    importStream(new FileInputStream(f), f);
                } catch (IOException e) {
                    logger.error("IO error with " + f + ": " + e);
                }
            }
        }
    }

    public void importJar(JarFile jarfile) throws IOException {
        if(logger.isTraceEnabled())
            logger.trace("Importing jar " + jarfile.getName());
        for(JarEntry je: Collections.list(jarfile.entries())) {
            String name = je.getName();
            if( !je.isDirectory() && name.endsWith(".xml") && (name.startsWith("desc/") || name.startsWith("graph/") || name.startsWith("probe/"))) {
                logger.trace("Will import jar entry " + je);
                importStream(jarfile.getInputStream(je), je + " in " + jarfile);
            }
        }
    }

    /**
     * Schedule within the thread pool a dom parsing
     * @param xmlstream the xml object to parse
     * @param source a identifier for the source
     */
    void importStream(final InputStream xmlstream, final Object source) {
        Runnable importer = new Runnable() {
            @Override
            public void run() {
                try {
                    JrdsDocument d = new JrdsDocument(localDocumentBuilder.get().parse(xmlstream));

                    ConfigType t = nodesTypes.get(d.getRootElement().getNodeName());
                    if(t == null) {
                        logger.error("Invalid type " + d.getRootElement().getNodeName() + " for: " + source);
                        return;
                    }
                    String name = t.getName(d);
                    logger.trace(Util.delayedFormatString("Found a %s with name %s", t.getRootNode(), name));
                    //We check the Name
                    if(name != null && ! "".equals(name)) {
                        Map<String, JrdsDocument> rep = repositories.get(t);
                        //We warn for dual inclusion, none is loaded, as we don't know the good one
                        if(rep.containsKey(name)) {
                            logger.error("Dual definition of " + t + " with name " + name);
                            rep.remove(name);
                        }
                        else {
                            rep.put(name, d);
                        }
                    }
                    else {
                        logger.error("name not found in " + source);
                    }
                } catch (FileNotFoundException e) {
                    logger.error("File not found: " + source);
                } catch (SAXParseException e) {
                    logger.error("Invalid xml document " + source + " (line " + e.getLineNumber() + "): " + e.getMessage());
                } catch (SAXException e) {
                    logger.error("Invalid xml document " + source  + ": " + e);
                } catch (IOException e) {
                    logger.error("IO error with " + source + ": " + e);
                }
            }
        };
        tpool.execute(importer);
    }

    public void done() {
        tpool.shutdown();
        try {
            tpool.awaitTermination(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error(e);
        }
    }
}
