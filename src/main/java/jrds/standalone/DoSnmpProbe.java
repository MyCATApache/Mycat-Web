package jrds.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.OutputKeys;

import jrds.Probe;
import jrds.ProbeDesc;
import jrds.probe.snmp.RdsIndexedSnmpRrd;
import jrds.probe.snmp.RdsSnmpSimple;

import org.apache.logging.log4j.*;
import org.rrd4j.DsType;
import org.snmp4j.smi.OID;

public class DoSnmpProbe  extends CommandStarterImpl {
    static final private Logger logger = LogManager.getLogger(DoSnmpProbe.class);
    static final Pattern oidPattern = Pattern.compile("^(.\\d+)+$");
    static final Pattern namePattern = Pattern.compile("^(.+)\\s+OBJECT-TYPE$");
    static final Pattern syntaxPattern = Pattern.compile(".*SYNTAX\\s+([a-zA-Z0-9]+).*");

    private static final class OidInfo {
        OID oid;
        String name;
        DsType type;
    }

    static final Map<String, Method> argstomethod = new HashMap<String, Method>();
    static final Map<String, Method> typeMapper = new HashMap<String, Method>();
    static {
        try {
            argstomethod.put("name", ProbeDesc.class.getMethod("setName", String.class));
            argstomethod.put("probename", ProbeDesc.class.getMethod("setProbeName", String.class));
            argstomethod.put("uptimefactor", ProbeDesc.class.getMethod("setUptimefactor", float.class));

            typeMapper.put("uptimefactor", Float.class.getMethod("valueOf", String.class));
            typeMapper.put("uniqindex", Boolean.class.getMethod("valueOf", String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void configure(Properties configuration) {
        logger.debug("Configuration: " + configuration);
    }

    private OidInfo translate(String oidstring) throws IOException {
        OidInfo info = new OidInfo();

        Process p = Runtime.getRuntime().exec(new String[] {"snmptranslate", "-Td", "-On", oidstring});
        InputStreamReader isr = new InputStreamReader(p.getInputStream());
        BufferedReader r = new BufferedReader(isr);
        String line = r.readLine();
        while (line != null) {
            Matcher nameMatcher = namePattern.matcher(line);
            Matcher syntaxMatcher = syntaxPattern.matcher(line);
            if(oidPattern.matcher(line.trim()).matches()) {
                String oidString = line.substring(1);
                info.oid = new OID(oidString);
            }
            else if(nameMatcher.matches()) {
                info.name = nameMatcher.group(1);
            }
            else if(syntaxMatcher.matches()) {
                String syntax = syntaxMatcher.group(1);
                if("counter".matches(syntax.toLowerCase()))
                    info.type = DsType.COUNTER;
                else if("integer".matches(syntax.toLowerCase()))
                    info.type = DsType.GAUGE;
                else if("gauge32".matches(syntax.toLowerCase()))
                    info.type = DsType.GAUGE;
            }
            line = r.readLine();
        }
        r.close();
        return info;
    }

    @SuppressWarnings("unchecked")
    public void start(String[] args) throws Exception {
        ProbeDesc pd = new ProbeDesc();
        pd.setProbeClass(jrds.probe.snmp.RdsSnmpSimple.class);
        boolean indexed = false;
        for(int i=0; i < args.length ; i++) {
            String cmd = args[i];
            if("--specific".equals(cmd.toLowerCase())) {
                for(String specargs: args[++i].split(",")) {
                    String[] specinfo = specargs.split("=");
                    pd.addSpecific(specinfo[0], specinfo[1]);
                }
            }
            else if("--index".equals(cmd.toLowerCase())) { 
                OidInfo info = translate(args[++i]);

                pd.setProbeClass(jrds.probe.snmp.RdsIndexedSnmpRrd.class);
                pd.addSpecific(RdsIndexedSnmpRrd.INDEXOIDNAME, info.oid.toString());
                indexed = true;
            }
            else if("--probeclass".equals(cmd.toLowerCase())) {
                Class<?> c = Class.forName(args[++i]);
                pd.setProbeClass((Class<? extends Probe<?, ?>>) c);
            }
            else if("--graphs".equals(cmd.toLowerCase())) {
                String graphsList = args[++i];
                for(String g: graphsList.split(","))  {
                    pd.addGraph(g.trim());
                }
            }
            else if("--collect".equals(cmd.toLowerCase())) {
                for(String collectarg: args[++i].split(",")) {
                    OidInfo info = translate(collectarg);
                    pd.add(info.name, info.type, info.oid);
                }
            }
            else if(cmd.startsWith("--") ) {
                String key = cmd.replace("--", "").toLowerCase();
                Object arg = args[++i];
                Method m = argstomethod.get(key);
                Method parser = typeMapper.get(key);
                if(parser != null)
                    arg = parser.invoke(null, arg);
                m.invoke(pd, arg);
            }
        }
        if( ! indexed)
            pd.addSpecific(RdsSnmpSimple.REQUESTERNAME, "simple");

        Map<String, String> prop = new HashMap<String, String>();
        prop.put(OutputKeys.INDENT, "yes");
        prop.put(OutputKeys.DOCTYPE_PUBLIC, "-//jrds//DTD Probe Description//EN");
        prop.put(OutputKeys.DOCTYPE_SYSTEM, "urn:jrds:probedesc");
        prop.put(OutputKeys.INDENT, "yes");
        prop.put("{http://xml.apache.org/xslt}indent-amount", "4");
        jrds.Util.serialize(pd.dumpAsXml(), System.out, null, prop);
        System.out.println();
    }
}
