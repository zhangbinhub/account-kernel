package OLink.bpm.core.formula;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class FormulaNode {
	private String id;

	private String text;

	private Collection<FormulaNode> children = new ArrayList<FormulaNode>();

	private FormulaNode parent;

	private String valuetype;
	
	public String getValuetype() {
		return valuetype;
	}

	public void setValuetype(String valuetype) {
		this.valuetype = valuetype;
	}

	public FormulaNode getParent() {
		return parent;
	}

	public void setParent(FormulaNode parent) {
		this.parent = parent;
	}

	public Collection<FormulaNode> getChildren() {
		return children;
	}

	public void setChildren(Collection<FormulaNode> thechildren) {
		if (thechildren!=null) {
			this.children.clear();
			for (Iterator<FormulaNode> iter = thechildren.iterator(); iter.hasNext();) {
				//Object element = (Object) iter.next();
				FormulaNode element = iter.next();
				this.children.add(element);
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void addChild(FormulaNode child) {
		children.add(child);
	}

	public void delChild(FormulaNode child) {
		children.remove(child);
	}
}
