package OLink.bpm.core.table.ddlutil.oracle;

import java.sql.Connection;

import OLink.bpm.core.table.ddlutil.AbstractValidator;
import OLink.bpm.util.DbTypeUtil;

/**
 * 
 * @author Chris
 * 
 */
public class OracleValidator extends AbstractValidator {

	public OracleValidator(Connection conn) {
		super(conn, new OracleBuilder());
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
		_builder.setSchema(schema);
	}

	protected String getCatalog() {
		return null;
	}

	protected String getSchemaPattern() {
		return schema;
	}

}
