package jrds.snmp;

import java.util.Collection;
import java.util.Iterator;

import org.snmp4j.Target;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TableEvent;
import org.snmp4j.util.TableUtils;

public class TabularIterator implements Iterator<SnmpVars>, Iterable<SnmpVars> {
    Iterator<TableEvent> tabIterator;

    public TabularIterator(SnmpConnection starter, Collection<OID> oids) {
        if(starter != null && starter.isStarted()) {
            Target snmpTarget = starter.getConnection();
            if(snmpTarget != null) {
                DefaultPDUFactory localfactory = new DefaultPDUFactory();
                TableUtils tableRet = new TableUtils(starter.getSnmp(), localfactory);
                tableRet.setMaxNumColumnsPerPDU(30);
                OID[] oidTab= new OID[oids.size()];
                oids.toArray(oidTab);
                tabIterator = tableRet.getTable(snmpTarget, oidTab, null, null).iterator();
            }
        }
    }

    public boolean hasNext() {
        return tabIterator.hasNext();
    }

    public SnmpVars next() {
        TableEvent te =  tabIterator.next();
        VariableBinding[] columns = te.getColumns();
        SnmpVars vars;
        if(columns != null)
            vars = new SnmpVars(columns);
        else
            vars = new SnmpVars();
        return vars;
    }

    public void remove() {
        throw new UnsupportedOperationException("Cannot remove in a TabularIterator");
    }

    public Iterator<SnmpVars> iterator() {
        return this;
    }
}