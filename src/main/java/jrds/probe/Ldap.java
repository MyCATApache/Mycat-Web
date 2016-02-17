package jrds.probe;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;

import jrds.ProbeConnected;

import org.apache.logging.log4j.Level;

public class Ldap extends ProbeConnected<String, Number, LdapConnection> {
    private long uptime;

    final static String[] upTimeSpecifics = {"upTimePath", "startTimePath", "currentTimePath"};
    public Ldap() {
        super(LdapConnection.class.getName());
    }

    @Override
    public Map<String, Number> getNewSampleValuesConnected(LdapConnection cnx) {
        uptime = findUptime(cnx);
        if(uptime < 0) {
            return null;
        }

        Set<String> collected = getCollectMapping().keySet();
        Map<String, Set<String>> requestInfo = buildRequestInfo(getCollectMapping().keySet());
        Map<String, Object> foundValues = doMultiSearch(cnx, requestInfo);

        Map<String, Number> retValues = new HashMap<String, Number>();
        for(Map.Entry<String, Object> e: foundValues.entrySet()) {
            if(collected.contains(e.getKey())) {
                double val = jrds.Util.parseStringNumber(e.getValue().toString(), Double.NaN).doubleValue();
                retValues.put(e.getKey(), val);
            }
        }
        return retValues;
    }

    @Override
    public String getSourceType() {
        return "LDAP";
    }

    /* This method is called with a value extracted from the connection
     * But the uptime value is manager by the probe, not the connection
     * 
     * @see jrds.Probe#setUptime(long)
     */
    @Override
    public void setUptime(long uptime) {
        if(uptime > 0) {
            this.uptime = uptime;
        }
    }

    /* (non-Javadoc)
     * @see jrds.Probe#getUptime()
     */
    @Override
    public long getUptime() {
        return uptime;
    }

    protected long findUptime(LdapConnection cnx) {
        Set<String> collectPaths = new HashSet<String>(upTimeSpecifics.length);
        for(String s: upTimeSpecifics) {
            String v = getPd().getSpecific(s);
            if(v != null && ! "".equals(v.trim()))
                collectPaths.add(v.trim());
        }

        Map<String, Set<String>> requestInfo = buildRequestInfo(collectPaths);

        Map<String, Object> retValues = doMultiSearch(cnx, requestInfo);
        log(Level.TRACE, "will search uptime in %s", retValues);
        if(retValues.containsKey("upTimePath")) {
            long uptime = jrds.Util.parseStringNumber(retValues.get("upTimePath").toString(), -1L);
            return uptime;
        }
        else {
            Object startTimePath = retValues.get(getPd().getSpecific("startTimePath"));
            Object currentTimePath = retValues.get(getPd().getSpecific("currentTimePath"));
            Object timePattern = getPd().getSpecific("timePattern");
            if(startTimePath != null && timePattern !=null) {
                DateFormat df = new SimpleDateFormat(timePattern.toString());
                try {
                    Date start = df.parse(startTimePath.toString());
                    Date current;
                    if(currentTimePath != null)
                        current = df.parse(currentTimePath.toString());
                    else
                        current = new Date();
                    long uptime = ( current.getTime() - start.getTime()) / 1000;
                    return uptime;
                } catch (ParseException e) {
                    log(Level.ERROR,"Date not parsed with pattern " + ((SimpleDateFormat) df).toPattern() + ": " + e);
                }
            }
            else {
                log(Level.ERROR, "No informations for the uptime");
                return -1;
            }
        }
        return -1;
    }


    protected Map<String, Set<String>> buildRequestInfo(Set<String> collectPaths) {
        Map<String, Set<String>> retValue = new HashMap<String, Set<String>>();
        for(String path: collectPaths) {
            String[] parsed = jrds.Util.parseTemplate(path, this).split("\\?");
            String rdn = null, field = null;
            if(parsed.length == 2) {
                rdn = parsed[0];
                field = parsed[1];
            }
            else if(parsed.length == 1) {
                rdn = ".";
                field = parsed[0];
            }
            if( ! retValue.containsKey(rdn)) {
                retValue.put(rdn, new HashSet<String>());
            }
            retValue.get(rdn).add(field);
        }
        log(Level.TRACE, "Preparing a request info: %s", retValue);

        return retValue;
    }

    protected  Map<String, Object> doMultiSearch(LdapConnection cnx, Map<String, Set<String>> requestInfo) {
        Map<String, Object> retValues = new HashMap<String, Object>();

        for(Map.Entry<String, Set<String>> e: requestInfo.entrySet()) {
            Map<String, Object> attributes = doSearchFielsEntry(cnx, e.getKey(), e.getValue());
            for(Map.Entry<String, Object> v: attributes.entrySet()) {
                retValues.put(e.getKey() + "?" + v.getKey(), v.getValue());
            }
        }

        return retValues;
    }

    protected Map<String, Object>doSearchFielsEntry(LdapConnection cnx, String base, Set<String> fields) {
        SearchControls sc = new SearchControls();
        String[] attributeFilter = fields.toArray(new String[]{});
        sc.setReturningAttributes(attributeFilter);
        sc.setSearchScope(SearchControls.OBJECT_SCOPE);
        sc.setReturningObjFlag(false);

        DirContext dctx = cnx.getConnection();

        Map<String, Object> retValues = new HashMap<String, Object>();
        try {
            Attributes attributesList = dctx.getAttributes(base, fields.toArray(new String[0]));
            for(Attribute a: jrds.Util.iterate(attributesList.getAll())) {
                log(Level.TRACE, "collect name: %s?%s", base, a.getID());
                retValues.put(a.getID(), a.get());
            }
        } catch (NamingException e) {
            log(Level.ERROR, e, e.getMessage());
        }
        return retValues;
    }

}
