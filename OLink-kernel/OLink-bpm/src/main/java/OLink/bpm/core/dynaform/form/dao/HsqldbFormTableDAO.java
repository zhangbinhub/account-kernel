package OLink.bpm.core.dynaform.form.dao;

import java.sql.Connection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.core.table.ddlutil.hsql.HsqldbTableDefinition;
import OLink.bpm.core.table.ddlutil.hsql.HsqldbValidator;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;

/**
 * 
 * @author Chris
 * 
 */
public class HsqldbFormTableDAO extends AbstractFormTableDAO implements IRuntimeDAO, FormTableDAO {

	public HsqldbFormTableDAO(Connection conn) {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
	}

	public AbstractValidator getValidator() {
		return new HsqldbValidator(conn);
	}

	public AbstractTableDefinition getTableDefinition() {
		return new HsqldbTableDefinition(conn);
	}
}
