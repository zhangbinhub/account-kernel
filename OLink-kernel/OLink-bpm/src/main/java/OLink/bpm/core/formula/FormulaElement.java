package OLink.bpm.core.formula;

import java.util.ArrayList;
import java.util.Collection;

public class FormulaElement {
	private String desc;

	private FormulaElement parent;

	private Collection<FormulaElement> children = new ArrayList<FormulaElement>();

	public FormulaElement(String desc) {
		setDesc(desc);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public FormulaElement getParent() {
		return parent;
	}

	public void setParent(FormulaElement parent) {
		this.parent = parent;
	}

	public Collection<FormulaElement> getChildren() {
		return children;
	}

	public void setChildren(Collection<FormulaElement> children) {
		this.children = children;
	}

	public void setChildren(FormulaElement child) {
		children.add(child);
	}

	
}
