package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Table;
import OLink.bpm.core.table.model.Column;

/**
 * @author  nicholas
 */
public class ColumnDataTransferChange extends ModelChange {

	/**
	 * data source column
	 */
	private Column _sourceColumn;

	public ColumnDataTransferChange(Table _changedTable, Column _sourceColumn,
									Column _targetColumn) {
		this._table = _changedTable;
		this._sourceColumn = _sourceColumn;
		this._targetColumn = _targetColumn;
	}

	public Column getSourceColumn() {
		return _sourceColumn;
	}

	public String getErrorMessage(){
		return null;
	}
}
