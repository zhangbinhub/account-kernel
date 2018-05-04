package OLink.bpm.core.dynaform.dts.export2.sql;

import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;

public class SqlFactory {
	
	public static BaseSql createDb(int dbType)throws Exception
	{
		BaseSql db = null; 
		
		if(dbType == DataSource.DB_ORACLE)
			db = new Oracle();
		else if(dbType == DataSource.DB_SQLSERVER)
			db = new SqlServer();
		
		return db;
		
	}
	
	
}
