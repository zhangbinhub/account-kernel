package OLink.bpm.core.dynaform.form.dao;

import java.sql.Connection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;
import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.core.table.ddlutil.mssql.MssqlTableDefinition;
import OLink.bpm.core.table.ddlutil.mssql.MssqlValidator;
import OLink.bpm.util.DbTypeUtil;

/**
 * 
 * @author Chris
 * 
 */
public class MssqlFormTableDAO extends AbstractFormTableDAO implements IRuntimeDAO, FormTableDAO {

	public MssqlFormTableDAO(Connection conn) {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MSSQL);
	}

	public AbstractValidator getValidator() {
		return new MssqlValidator(conn);
	}

	public AbstractTableDefinition getTableDefinition() {
		return new MssqlTableDefinition(conn);
	}
}
