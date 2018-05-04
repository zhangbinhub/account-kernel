package OLink.bpm.core.dynaform.form.dao;

import java.sql.Connection;

import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;
import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.core.table.ddlutil.db2.DB2TableDefinition;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.table.ddlutil.db2.DB2Validator;
import OLink.bpm.util.DbTypeUtil;

public class DB2FormTableDAO extends AbstractFormTableDAO implements IRuntimeDAO, FormTableDAO {

	public DB2FormTableDAO(Connection conn) {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
	}

	public AbstractValidator getValidator() {
		return new DB2Validator(conn);
	}

	public AbstractTableDefinition getTableDefinition() {
		return new DB2TableDefinition(conn);
	}
}
