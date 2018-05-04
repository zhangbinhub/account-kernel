package OLink.bpm.core.table.alteration;

import OLink.bpm.core.table.model.Table;

/**
 * 
 * @author nicholas
 * 
 */
public class AddTableChange extends ModelChange {

	public AddTableChange(Table _newTable) {
		this._table = _newTable;
	}

	public String getErrorMessage(){
		return "{*[core.form.alteration.table.name.exist]*}" + this._table.getName();
	}
	
}
