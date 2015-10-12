/*##########################################################################
 _##
 _##  $Id: ApacheStatus.java 475 2009-05-15 20:04:03Z fbacchella $
 _##
 _##########################################################################*/

package jrds.probe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jrds.Util;

/**
 * A class to probe the apache status from the /server-status URL
 * @author Fabrice Bacchella 
 */
public class ApacheStatusDetails extends ApacheStatus {

    //"_" Waiting for Connection, "S" Starting up, "R" Reading Request,
    //"W" Sending Reply, "K" Keepalive (read), "D" DNS Lookup,
    //"C" Closing connection, "L" Logging, "G" Gracefully finishing,
    //"I" Idle cleanup of worker, "." Open slot with no current process
    //Can't be within the enum, it's defined after the first call toll add
    static private final Map<Character, WorkerStat> map = new HashMap<Character, WorkerStat>();

    static enum WorkerStat {
        WAITING('_'),
        STARTING('S'),
        READING('R'),
        SENDING('W'),
        KEEPALIVE('K'),
        DNS('D'),
        CLOSING('C'),
        LOGGING('L'),
        GRACEFULLY('G'),
        IDLE('I'),
        OPEN('.');

        private final static WorkerStat resolv(char key) {
            return map.get(key);
        }
        private final static synchronized void add(char key, WorkerStat value) {
            map.put(key, value);
        }
        private WorkerStat(char key) {
            WorkerStat.add(key, this);
        }
    }

    /* (non-Javadoc)
     * @see com.aol.jrds.HttpProbe#parseLines(java.util.List)
     */
    protected Map<String, Number> parseLines(List<String> lines) {
        Map<String, Number> retValue = new HashMap<String, Number>(lines.size());
        for(String l: lines) {
            String[] kvp = l.split(":");
            if(kvp.length !=2)
                continue;
            if("Scoreboard".equals(kvp[0].trim())) {
                parseScoreboard(kvp[1].trim(), retValue);
            }
            else {
                Double value = Util.parseStringNumber(kvp[1].trim(), Double.NaN);
                retValue.put(kvp[0].trim(), value);
            }
        }
        Number uptimeNumber = retValue.remove("Uptime");
        if(uptimeNumber != null)
            setUptime(uptimeNumber.longValue());
        return retValue;
    }

    void parseScoreboard(String scoreboard, Map<String, Number> retValue) {
        int workers[] = new int[WorkerStat.values().length];
        for(char c: scoreboard.toCharArray()) {
            WorkerStat worker = WorkerStat.resolv(c);
            if(worker != null)
                workers[worker.ordinal()]++;
        }
        for(WorkerStat worker: WorkerStat.values()) {
            retValue.put(worker.toString(), workers[worker.ordinal()]);
        }
    }
    
}
