package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Table;

/**
 * @author  nicholas
 */
public class TableDataTransferChange extends ModelChange {
	private Table _sourceTable;

	private Table _targetTable;

	public TableDataTransferChange(Table _sourceTable, Table _targetTable) {
		this._sourceTable = _sourceTable;
		this._targetTable = _targetTable;
		this._table = _targetTable;
	}

	public Table getSourceTable() {
		return _sourceTable;
	}

	public Table getTargetTable() {
		return _targetTable;
	}

	public String getErrorMessage(){
		
		return null;
	}
}
