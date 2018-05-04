package OLink.bpm.core.dynaform.form.dao;

import java.sql.Connection;

import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.core.table.ddlutil.mysql.MysqlValidator;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.core.table.ddlutil.AbstractTableDefinition;
import OLink.bpm.core.table.ddlutil.mysql.MysqlTableDefinition;

/**
 * 
 * @author Chris
 * 
 */
public class MysqlFormTableDAO extends AbstractFormTableDAO implements IRuntimeDAO, FormTableDAO {

	public MysqlFormTableDAO(Connection conn) {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
	}

	public AbstractValidator getValidator() {
		return new MysqlValidator(conn);
	}

	public AbstractTableDefinition getTableDefinition() {
		return new MysqlTableDefinition(conn);
	}
}
