package OLink.bpm.util.dialect;

import java.sql.Types;

import org.hibernate.dialect.SQLServerDialect;

public class SQLServerUnicodeDialect extends SQLServerDialect {

	public SQLServerUnicodeDialect() {
		registerColumnType(Types.CHAR, "nchar(1)");
		registerColumnType(Types.VARCHAR, "nvarchar($l)");
		registerColumnType(Types.CLOB, "ntext");
	}
}
