package jrds.webapp;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

public class JrdsJSONObject extends JSONObject implements Iterable<String> {

	public JrdsJSONObject() {
		super();
	}

	public JrdsJSONObject(String arg0) throws JSONException {
		super(arg0);
	}

	@SuppressWarnings("unchecked")
	public Iterator<String> iterator() {
		return keys();
	}

}
