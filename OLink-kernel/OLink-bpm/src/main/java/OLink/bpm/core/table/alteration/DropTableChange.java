package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Table;

/**
 * 
 * @author nicholas
 * 
 */
public class DropTableChange extends ModelChange {

	public DropTableChange(Table _sourceTable) {
		this._table = _sourceTable;
	}

	public String getErrorMessage() {
		return "{*[core.form.alteration.table.hasdata]*}"+_table.getName();
	}
}
