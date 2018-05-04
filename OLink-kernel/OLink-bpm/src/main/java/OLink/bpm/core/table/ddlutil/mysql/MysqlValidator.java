package OLink.bpm.core.table.ddlutil.mysql;

import java.sql.Connection;

import OLink.bpm.core.dynaform.form.dao.AbstractFormTableDAO;
import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.util.DbTypeUtil;
import org.apache.log4j.Logger;

/**
 * 
 * @author Chris
 * 
 */
public class MysqlValidator extends AbstractValidator {
	protected static Logger LOG = Logger.getLogger(AbstractFormTableDAO.class);

	public MysqlValidator(Connection conn) {
		super(conn, new MysqlBuilder());
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
		_builder.setSchema(schema);
	}

	/**
	 * 对于不同的数据库会有所不同
	 */
	protected String getCatalog() {
		return schema;
	}

	protected String getSchemaPattern() {
		return null;
	}
}
