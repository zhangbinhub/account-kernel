package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Column;
import OLink.bpm.core.table.model.Table;

/**
 * @author  nicholas
 */
public class ColumnRenameChange extends ModelChange {
	private Column _sourceColumn;

	public ColumnRenameChange(Table _changedColumn, Column _sourceColumn,
							  Column _targetColumn) {
		this._table = _changedColumn;
		this._sourceColumn = _sourceColumn;
		this._targetColumn = _targetColumn;
	}

	public Column getSourceColumn() {
		return _sourceColumn;
	}

	public String getErrorMessage() {
		
		return "{*[core.form.alteration.column.name.exist]*}" + _targetColumn;
	}
}
