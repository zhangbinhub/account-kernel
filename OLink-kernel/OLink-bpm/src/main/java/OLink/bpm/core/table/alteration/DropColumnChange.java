package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.model.Column;

/**
 * @author  nicholas
 */
public class DropColumnChange extends ModelChange {
	private Column _sourceColumn;
	
	public DropColumnChange(Table _changedTable, Column _sourceColumn) {
		this._table = _changedTable;
		this._sourceColumn = _sourceColumn;
	}

	public Column getSourceColumn() {
		return _sourceColumn;
	}

	public String getErrorMessage(){
		
		return "{*[core.form.alteration.column.hasdata]*}"+_sourceColumn;
	}
}
