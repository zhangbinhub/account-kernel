package OLink.bpm.core.tree;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Node {
	public final static String STATE_OPEN = "open";
	public final static String STATE_CLOSED = "closed";

	private String id = ""; // 节点ID
	private String data = ""; // 节点名称
	private String state = ""; // open, closed

	private Map<String, Object> attr = new LinkedHashMap<String, Object>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		attr.put("id", id);
	}

	public void addAttr(String key, Object value) {
		attr.put(key, value);
	}

	public String toXml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<node id='" + getId() + "' name='" + getData() + "'");
		if (attr != null && !attr.isEmpty()) {
			for (Iterator<Entry<String, Object>> iterator = attr.entrySet()
					.iterator(); iterator.hasNext();) {
				Entry<String, Object> entry = iterator
						.next();
				buffer.append(" " + entry.getKey() + "='" + entry.getValue()
						+ "'");
			}
		}
		buffer.append(">");
		buffer.append(getData());
		buffer.append("</node>\n");

		return buffer.toString();
	}

	public Map<String, Object> getAttr() {
		return attr;
	}

	public void setAttr(Map<String, Object> attr) {
		this.attr = attr;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
