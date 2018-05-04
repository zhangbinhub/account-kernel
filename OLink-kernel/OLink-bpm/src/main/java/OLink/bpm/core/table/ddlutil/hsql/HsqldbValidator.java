package OLink.bpm.core.table.ddlutil.hsql;

import java.sql.Connection;

import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.core.table.ddlutil.mysql.MysqlBuilder;

/**
 * 
 * @author Chris
 *
 */
public class HsqldbValidator extends AbstractValidator {
	
	public HsqldbValidator(Connection conn) {
		super(conn, new MysqlBuilder());
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
		_builder.setSchema(schema);
	}

	protected String getCatalog() {
		return null;
	}

	protected String getSchemaPattern() {
		return schema;
	}

}
