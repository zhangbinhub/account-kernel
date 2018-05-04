package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.model.Column;

/**
 * 
 * @author nicholas
 * 
 */
public class AddColumnChange extends ModelChange {
	
	public AddColumnChange(Table _changedTable, Column _targetColumn) {
		this._table = _changedTable;
		this._targetColumn = _targetColumn;
	}

	public String getErrorMessage()  {
		 return "{*[core.form.alteration.column.name.exist]*}"+_targetColumn.getName();
	}
}
