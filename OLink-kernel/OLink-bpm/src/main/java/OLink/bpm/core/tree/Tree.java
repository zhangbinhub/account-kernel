package OLink.bpm.core.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.util.json.JsonUtil;

public abstract class Tree<E> {
	protected Collection<Node> childNodes = new ArrayList<Node>();

	protected Collection<String> searchNodes = new ArrayList<String>();

	public Collection<Node> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(Collection<Node> childNodes) {
		this.childNodes = childNodes;
	}

	public abstract void parse(Collection<E> datas);

	public abstract void search();

	public String toXml() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<nodes>\n");
		if (childNodes != null && !childNodes.isEmpty()) {
			for (Iterator<Node> iterator = childNodes.iterator(); iterator
					.hasNext();) {
				Node node = iterator.next();
				buffer.append(node.toXml());
			}
		}
		buffer.append("</nodes>");
		return buffer.toString();
	}

	public String toJSON() {
		return JsonUtil.collection2Json(childNodes);
	}

	public String toSearchJSON() {
		return JsonUtil.collection2Json(searchNodes);
	}
}
