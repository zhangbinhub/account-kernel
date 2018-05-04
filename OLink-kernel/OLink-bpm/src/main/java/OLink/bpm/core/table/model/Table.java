package OLink.bpm.core.table.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @author nicholas
 * 
 */
public class Table {

	private String name;

	private String formName;

	private Collection<Column> columns = new ArrayList<Column>();

	public Table(String name) {
		this.name = name;
	}

	public Collection<Column> getColumns() {
		return columns;
	}

	public void setColumns(Collection<Column> columns) {
		this.columns = columns;
	}

	public void addColumn(Column column) {
		columns.add(column);
	}

	public Column findColumn(String name) throws DuplicateException {
		ArrayList<Column> tmp = new ArrayList<Column>();

		for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
			Column column = iter.next();
			if (column.getName().equalsIgnoreCase(name)) {
				tmp.add(column);
			}
		}

		if (tmp.size() > 1) {
			Column column = tmp.get(0);
			throw new DuplicateException("(" + column.getFieldName() + ") {*[core.field.name.was.duplicate]*}");
		}

		if (tmp.size() > 0) {
			return tmp.get(0);
		}

		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public boolean equals(Object obj) {
		if (obj instanceof Table) {
			Table anTable = (Table) obj;
			if (this.getName().equalsIgnoreCase(anTable.getName())) {
				for (Iterator<Column> iterator = getColumns().iterator(); iterator.hasNext();) {
					try {
						Column column = iterator.next();
						Column anColumn = anTable.findColumn(column.getName());
						if (!column.equals(anColumn)) {
							return false;
						}
					} catch (Exception e) {
						return false;
					}
				}
				return true;
			}
		}

		return super.equals(obj);
	}

	public int hashCode() {
		return super.hashCode();
	}

}
