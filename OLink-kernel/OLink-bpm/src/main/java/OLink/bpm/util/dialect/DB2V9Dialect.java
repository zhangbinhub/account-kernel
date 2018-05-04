package OLink.bpm.util.dialect;

import java.sql.Types;

import org.hibernate.dialect.DB2Dialect;

public class DB2V9Dialect extends DB2Dialect {

	public DB2V9Dialect(){
		super();
		registerColumnType( Types.BLOB, "blob" );
		registerColumnType( Types.CLOB, "clob" );
	}
}
