package OLink.bpm.core.dynaform.form.dao;

import java.sql.Connection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.core.table.ddlutil.oracle.OracleTableDefinition;
import OLink.bpm.core.table.ddlutil.oracle.OracleValidator;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;

public class OracleFormTableDAO extends AbstractFormTableDAO implements IRuntimeDAO, FormTableDAO {

	public OracleFormTableDAO(Connection conn) {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
	}

	public AbstractValidator getValidator() {
		return new OracleValidator(conn);
	}

	public AbstractTableDefinition getTableDefinition() {
		return new OracleTableDefinition(conn);
	}
}
