package OLink.bpm.core.dynaform.document.ejb;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author nicholas
 */
public class CurrentDocument extends Document {

	private static final long serialVersionUID = -5009405103987121519L;

	private Document parent;

	/**
	 * @uml.property name="childs"
	 */
	private Collection<Document> childs;

	/**
	 * @return the parent
	 * @uml.property name="parent"
	 */
	public Document getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 * @uml.property name="parent"
	 */
	public void setParent(Document parent) {
		this.parent = parent;
	}

	/**
	 * @return the childs
	 * @uml.property name="childs"
	 */
	public Collection<Document> getChilds() {
		if (childs != null) {
			return childs;
		} else {
			return new HashSet<Document>();
		}

	}

	/**
	 * @param childs
	 *            the childs to set
	 * @uml.property name="childs"
	 */
	public void setChilds(Collection<Document> childs) {
		this.childs = childs;
	}

}
