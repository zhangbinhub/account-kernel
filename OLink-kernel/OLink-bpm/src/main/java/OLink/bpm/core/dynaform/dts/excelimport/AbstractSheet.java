package OLink.bpm.core.dynaform.dts.excelimport;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public abstract class AbstractSheet extends Node {
	
	private static final long serialVersionUID = 7104332579182516716L;
	public String formName;

	public AbstractSheet(ExcelMappingDiagram owner) {
		super(owner);
	}

	public Collection<? extends Column> getColumns() {
		Vector<Column> rtn = new Vector<Column>();
		Iterator<?> iter = _owner.getAllElements().iterator();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (obj instanceof Relation) {
				Relation r = (Relation) obj;

				Node node = r.getAnotherEndNode(this);
				if (node instanceof Column) {
					rtn.add((Column)node);
				}
			}
		}
		return rtn;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

}
