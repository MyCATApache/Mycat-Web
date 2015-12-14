package org.mycat.web.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.fabric.xmlrpc.base.Array;

public class JitModel {

	
	private String id;
	private String name;
	private Map<String,String>  data;
	
	private List<JitModel> children;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public void setTypeData(String data) {
		if(this.data==null)
			this.data=new HashMap<String, String>();
		this.data.put("type", data);
	}

	public List<JitModel> getChildren() {
		return children;
	}

	public void setChildren(List<JitModel> children) {
		this.children = children;
	}
	public void addChildren(JitModel m) {
		if(children==null)
			children=new ArrayList<JitModel>();
		children.add(m);
	}
	public JitModel(String id, String name, Map<String, String> data,
			List<JitModel> children) {
		super();
		this.id = id;
		this.name = name;
		this.data = data;
		this.children = children;
	}

	public JitModel() {
		super();
	}

	public JitModel(String id, String name, Map<String, String> data) {
		this.id = id;
		this.name = name;
		this.data = data;
	}
	public JitModel(String id, String name, String type) {
		this.id = id;
		this.name = name;
		setTypeData(type);
	}
	
} 