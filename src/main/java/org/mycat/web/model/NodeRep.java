package org.mycat.web.model;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;


public class NodeRep {
	
	private String name;
	
	private List<NodeRep> childnode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<NodeRep> getChildnode() {
		return childnode;
	}

	public void setChildnode(List<NodeRep> childnode) {
		this.childnode = childnode;
	}
	public void addChildnode(NodeRep...nodes) {
		if(childnode==null)
			setChildnode(new ArrayList<NodeRep>());
		for (NodeRep node : nodes) {
			childnode.add(node);
		}
	}
	public NodeRep() {
		super();
	}

	public NodeRep(String name) {
		super();
		this.name = name;
	}
	
	public static void main(String[] args) {
		NodeRep TESTDB = new NodeRep("TESTDB");
		
		NodeRep offer = new NodeRep("offer");
		NodeRep hotnews = new NodeRep("hotnews");
		NodeRep customer = new NodeRep("customer");
		NodeRep customer_addr = new NodeRep("customer_addr");
		NodeRep orders = new NodeRep("orders");
		NodeRep order_items = new NodeRep("order_items");
		
		orders.addChildnode(order_items);
		customer.addChildnode(customer_addr,orders);
		customer.addChildnode();
		TESTDB.addChildnode(offer,hotnews,customer);
		
		String jsonString = JSONArray.toJSONString(TESTDB);
		System.out.println(jsonString);
	}

}
