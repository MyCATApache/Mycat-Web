package jrds.probe;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jrds.Probe;
import jrds.factories.ProbeBean;
import jrds.starter.SocketFactory;

import org.apache.logging.log4j.Level;

@ProbeBean({"port", "welcome"})
public class Varnish extends Probe<String, Number> implements IndexedProbe {

    static final private Pattern statlinepattern = Pattern.compile("^\\s+(\\d+)\\s+(.*)$");

    private int port = 6081;
    private Boolean welcome = false;

    public void configure(Integer port) {
        this.port = port;
    }

    public void configure(Integer port, Boolean welcome) {
        this.port = port;
        this.welcome = welcome;
    }

    public void configure(Boolean welcome) {
        this.welcome = welcome;
    }

    @Override
    public Map<String, Number> getNewSampleValues() {
        Socket s = null;
        try {
            SocketFactory ss = find(SocketFactory.class); 
            if(! ss.isStarted())
                return null;
            s = ss.createSocket(this, port);
        } catch (Exception e) {
            log(Level.ERROR, e, "Connect error %s", e);
            return null;
        }

        if(s == null)
            return null;

        try {
            PrintWriter outputSocket =  new PrintWriter(s.getOutputStream());
            BufferedReader inputSocket = new BufferedReader(new InputStreamReader(s.getInputStream()));

            if(welcome) {
                log(Level.TRACE, "Welcome screen dropped");
                getAnswer(inputSocket, outputSocket, null);
            }

            BufferedReader statsbuffer = getAnswer(inputSocket, outputSocket, getPd().getSpecific("command"));

            Map<String, Number> vars = new HashMap<String, Number>();
            while(statsbuffer.ready()) {
                String statsline = statsbuffer.readLine();
                Matcher m = statlinepattern.matcher(statsline);
                if(m.matches()) {
                    Number value = jrds.Util.parseStringNumber(m.group(1), -1L);
                    String key = m.group(2);
                    vars.put(key, value);
                }
                else {
                    log(Level.DEBUG,"Invalid line: %s", statsline);
                }
            }

            getAnswer(inputSocket, outputSocket, "quit");

            s.close();

            Number uptime = vars.remove(getPd().getSpecific("uptime"));
            if (uptime != null) {
                setUptime(uptime.longValue());
            }
            return vars;
        } catch (IOException e) {
            log(Level.ERROR, e, "Socket error %s", e);
        }

        return null;
    }

    private BufferedReader getAnswer(BufferedReader in, PrintWriter out, String command) throws IOException {
        if(command != null && ! "".equals(command)) {
            log(Level.TRACE, "Send command '%s'", command);
            out.println(command);
            out.flush();
        }

        //We read a possible status line
        //The format is : 'status size \n'
        String statusline  = in.readLine().trim();
        log(Level.TRACE, "Read status line '%s'", statusline);
        String[] statusinfo = statusline.split(" ");
        if(statusinfo.length != 2) {
            return new BufferedReader(new CharArrayReader(new char[0]));
        }
        int statuscode = jrds.Util.parseStringNumber(statusinfo[0], -1).intValue();
        int size =  jrds.Util.parseStringNumber(statusinfo[1], -1).intValue();
        log(Level.TRACE, "status code: %d", statuscode);
        if((statuscode != 200 && statuscode != 500) || size < 1) {
            log(Level.ERROR, "communication error, code: %d, byte expected: %d", statuscode, size);
            return new BufferedReader(new CharArrayReader(new char[0]));
        }

        //We read the data
        char[] cbuf= new char[size];
        int readchar = in.read(cbuf);
        if( readchar != size ) {
            log(Level.ERROR, "read failed, not enough byte, got %d expected %d", readchar, size);
            return new BufferedReader(new CharArrayReader(new char[0]));
        };
        in.readLine();

        BufferedReader answerbuffer = new BufferedReader(new CharArrayReader(cbuf));

        return answerbuffer;
    }

    @Override
    public String getSourceType() {
        return "Varnish";
    }

    public String getIndexName() {
        return Integer.toString(port);
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the welcome
     */
    public Boolean getWelcome() {
        return welcome;
    }

    /**
     * @param welcome the welcome to set
     */
    public void setWelcome(Boolean welcome) {
        this.welcome = welcome;
    }

}
