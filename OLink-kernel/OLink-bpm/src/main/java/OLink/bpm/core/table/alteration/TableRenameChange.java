package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Table;

/**
 * @author  nicholas
 */
public class TableRenameChange extends ModelChange {
	private Table _changedTable;

	private Table _targetTable;

	public TableRenameChange(Table _changedTable, Table _targetTable) {
		this._table = _targetTable;
		this._changedTable = _changedTable;
		this._targetTable = _targetTable;
	}

	public Table getChangedTable() {
		return _changedTable;
	}

	public Table getTargetTable() {
		return _targetTable;
	}

	public String getErrorMessage(){
		
		return "{*[core.form.alteration.table.name.exist]*}"+_targetTable.getName();
	}
}
