package org.mycat.web.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MMDataGrid {

	private int totalCount = 0;
	private List<Map<String,Object>> items = new ArrayList<Map<String,Object>>();
	private String loadingText;
	private String noDataText;
	private String loadErrorText;
	
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public List<Map<String, Object>> getItems() {
		return items;
	}
	public void setItems(List<Map<String, Object>> items) {
		this.items = items;
	}

	public String getLoadingText() {
		return loadingText;
	}

	public void setLoadingText(String loadingText) {
		this.loadingText = loadingText;
	}

	public String getNoDataText() {
		return noDataText;
	}

	public void setNoDataText(String noDataText) {
		this.noDataText = noDataText;
	}

	public String getLoadErrorText() {
		return loadErrorText;
	}

	public void setLoadErrorText(String loadErrorText) {
		this.loadErrorText = loadErrorText;
	}
}