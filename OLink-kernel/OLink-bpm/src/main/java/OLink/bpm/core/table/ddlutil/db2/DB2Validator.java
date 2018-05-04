package OLink.bpm.core.table.ddlutil.db2;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.ejb.Confirm;
import OLink.bpm.core.table.alteration.ColumnDataTypeChange;
import OLink.bpm.core.table.constants.ConfirmConstant;
import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.util.DbTypeUtil;
import eWAP.core.Tools;


/**
 * 
 * @author Chris
 * 
 */
public class DB2Validator extends AbstractValidator {

	public DB2Validator(Connection conn) {
		super(conn, new DB2Builder());
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
		_builder.setSchema(schema);
	}

	public void checkChange(ColumnDataTypeChange change) throws Exception {
		Confirm confirm = new Confirm(change.getTable().getFormName(), ConfirmConstant.FIELD_TYPE_INCOMPATIBLE);
		confirm.setId(Tools.getSequence());
		confirm.setNewFieldId(change.getTargetColumn().getId());
		confirm.setOldFieldId(change.getSourceColumn().getId());
		confirm.setFieldName(change.getSourceColumn().getFieldName());
		confirms.add(confirm);
	}

	protected String getCatalog() {
		return null;
	}

	protected String getSchemaPattern() {
		return schema;
	}
}
