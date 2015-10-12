package jrds.webapp;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONWriter;

public class JrdsJSONWriter {
    private JSONWriter jw;
    private Writer w;

    public JrdsJSONWriter(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.addDateHeader("Last-Modifed", new Date().getTime());
        ServletOutputStream out = response.getOutputStream();
        w = new OutputStreamWriter(out);
        jw = new JSONWriter(w);
    }

    public JSONWriter map(Map<?, ?> map) throws JSONException {
        jw.object();
        for(Map.Entry<?, ?> e: map.entrySet()) {
            jw.key(e.getKey().toString());
            if(e.getValue() instanceof Map<?, ?>) {
                map((Map<?, ?>) e.getValue());
            } else {
                jw.value(e.getValue());
            }
        }
        return jw.endObject();
    }

    /**
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#array()
     */
    public JSONWriter array() throws JSONException {
        return jw.array();
    }

    /**
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#endArray()
     */
    public JSONWriter endArray() throws JSONException {
        return jw.endArray();
    }

    /**
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#endObject()
     */
    public JSONWriter endObject() throws JSONException {
        return jw.endObject();
    }

    /**
     * @param arg0
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#key(java.lang.String)
     */
    public JSONWriter key(String arg0) throws JSONException {
        return jw.key(arg0);
    }

    /**
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#object()
     */
    public JSONWriter object() throws JSONException {
        return jw.object();
    }
    /**
     * @param arg0
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#value(boolean)
     */
    public JSONWriter value(boolean arg0) throws JSONException {
        return jw.value(arg0);
    }

    /**
     * @param arg0
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#value(double)
     */
    public JSONWriter value(double arg0) throws JSONException {
        return jw.value(arg0);
    }

    /**
     * @param arg0
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#value(long)
     */
    public JSONWriter value(long arg0) throws JSONException {
        return jw.value(arg0);
    }

    /**
     * @param arg0
     * @return
     * @throws JSONException
     * @see org.json.JSONWriter#value(java.lang.Object)
     */
    public JSONWriter value(Object arg0) throws JSONException {
        return jw.value(arg0);
    }

    /**
     * Write a line separator. It prints both CR an LF
     * @throws IOException 
     */
    public void newLine() throws IOException {
        w.write("\r\n");
    }

    /**
     * @throws IOException
     * @see java.io.Writer#close()
     */
    public void close() throws IOException {
        w.close();
    }

    /**
     * @throws IOException
     * @see java.io.Writer#flush()
     */
    public void flush() throws IOException {
        w.flush();
    }

}
