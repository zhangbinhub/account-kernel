package OLink.bpm.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.table.model.Column;
import OLink.bpm.core.table.model.Table;
import org.apache.log4j.Logger;

import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.dynaform.document.dql.DB2SQLFunction;
import OLink.bpm.core.dynaform.document.dql.HsqldbSQLFunction;
import OLink.bpm.core.dynaform.document.dql.MssqlSQLFunction;
import OLink.bpm.core.dynaform.document.dql.MysqlSQLFunction;
import OLink.bpm.core.dynaform.document.dql.OracleSQLFunction;
import OLink.bpm.core.dynaform.document.dql.SQLFunction;
import OLink.bpm.core.table.ddlutil.SQLBuilder;

public class DbTypeUtil {
	public final static Logger log = Logger.getLogger(DbTypeUtil.class);

	public static final String DBTYPE_ORACLE = "ORACLE";

	public static final String DBTYPE_MSSQL = "MSSQL";

	public static final String DBTYPE_MYSQL = "MYSQL";

	public static final String DBTYPE_HSQLDB = "HSQLDB";

	public static final String DBTYPE_DB2 = "DB2";

	private static HashMap<String, String> _dbTypes = new HashMap<String, String>();

	public static Collection<String> getTableNames(String applicationId) {
		Collection<String> rtn = new ArrayList<String>();

		Connection conn = null;
		ResultSet tableSet = null;
		try {
			conn = getConnection(applicationId);

			String catalog = null;
			String schemaPattern = null;

			String dbType = getDBType(applicationId);
			String schema = getSchema(conn, dbType);

			if (dbType.equals(DBTYPE_ORACLE)) {
				schemaPattern = schema;
			} else if (dbType.equals(DBTYPE_MSSQL)) {
				schemaPattern = "DBO";
			} else if (dbType.equals(DBTYPE_MYSQL)) {
				catalog = schema;
			} else if (dbType.equals(DBTYPE_HSQLDB)) {
				schemaPattern = schema;
			} else if (dbType.equals(DBTYPE_DB2)) {
				schemaPattern = schema;
			}

			DatabaseMetaData metaData = conn.getMetaData();
			tableSet = metaData.getTables(catalog, schemaPattern, null,
					new String[] { "TABLE" });

			while (tableSet.next()) {
				String tableName = tableSet.getString(3);
				rtn.add(tableName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					conn.close();
					tableSet.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return rtn;
	}

	/**
	 * 
	 * @param applicationId
	 *            应用ID
	 * @return
	 */
	public static Collection<Table> getTables(String applicationId) {
		return getTables(null, applicationId);
	}

	public static Table getTable(String tableName, String dbType,
			Connection conn) {
		Collection<Table> tables = getTables(tableName, dbType, conn);
		if (tables != null && !tables.isEmpty()) {
			return tables.iterator().next();
		}

		return null;
	}

	/**
	 * 
	 * 2.6新增
	 * 
	 * @param tableName
	 * @param dt
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	public static Table getTable(String tableName, DataSource dt)
			throws Exception {
		if (dt != null) {
			return getTable(tableName, dt.getDbTypeName(), dt.getConnection());
		}
		return null;
	}

	/**
	 * 
	 * @param tableName
	 *            数据表名称
	 * @param applicationId
	 *            应用ID
	 * @return 表集合
	 */
	public static Collection<Table> getTables(String tableName, String dbType,
			Connection conn) {
		Collection<Table> rtn = new ArrayList<Table>();

		ResultSet tableSet = null;
		try {

			String catalog = null;
			String schemaPattern = null;

			String schema = getSchema(conn, dbType);

			if (dbType.equals(DBTYPE_ORACLE)) {
				schemaPattern = schema;
			} else if (dbType.equals(DBTYPE_MSSQL)) {
				schemaPattern = "DBO";
			} else if (dbType.equals(DBTYPE_MYSQL)) {
				catalog = schema;
			} else if (dbType.equals(DBTYPE_HSQLDB)) {
				schemaPattern = schema;
			} else if (dbType.equals(DBTYPE_DB2)) {
				schemaPattern = schema;
			}

			DatabaseMetaData metaData = conn.getMetaData();
			tableSet = metaData.getTables(catalog, schemaPattern, tableName,
					new String[] { "TABLE" });

			while (tableSet.next()) {
				tableName = tableSet.getString(3);
				Table table = new Table(tableName.toUpperCase());
				ResultSet columnSet = null;
				try {
					columnSet = metaData.getColumns(catalog, schemaPattern,
							tableName, null);
					while (columnSet.next()) {
						String name = columnSet.getString(4);
						int typeCode = columnSet.getInt(5);
						Column column = new Column("", name.toUpperCase(),
								typeCode);

						table.getColumns().add(column);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (columnSet != null) {
						columnSet.close();
					}
				}

				rtn.add(table);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				tableSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return rtn;
	}

	/**
	 * 根据数据库模式获取数据库中的表格列表
	 * 
	 * @param schema
	 * @param applicationId
	 * @return
	 */
	public static Collection<Table> getTablesBySchema(String schema,
			String applicationId) {
		Collection<Table> rtn = new ArrayList<Table>();

		ResultSet tableSet = null;
		try {
			Connection conn = getConnection(applicationId);

			String catalog = null;
			String schemaPattern = null;

			String dbType = getDBType(applicationId);

			if (dbType.equals(DBTYPE_ORACLE)) {
				schemaPattern = schema;
			} else if (dbType.equals(DBTYPE_MSSQL)) {
				schemaPattern = "DBO";
			} else if (dbType.equals(DBTYPE_MYSQL)) {
				catalog = schema;
			} else if (dbType.equals(DBTYPE_HSQLDB)) {
				schemaPattern = schema;
			} else if (dbType.equals(DBTYPE_DB2)) {
				schemaPattern = schema;
			}

			DatabaseMetaData metaData = conn.getMetaData();
			tableSet = metaData.getTables(catalog, schemaPattern, null,
					new String[] { "TABLE" });

			while (tableSet.next()) {
				String tableName = tableSet.getString(3);
				Table table = new Table(tableName.toUpperCase());
				ResultSet columnSet = null;
				try {
					columnSet = metaData.getColumns(catalog, schemaPattern,
							tableName, null);
					while (columnSet.next()) {
						String name = columnSet.getString(4);
						int typeCode = columnSet.getInt(5);
						Column column = new Column("", name.toUpperCase(),
								typeCode);

						table.getColumns().add(column);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (columnSet != null) {
						columnSet.close();
					}
				}

				rtn.add(table);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				tableSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return rtn;
	}

	/**
	 * 
	 * @param tableName
	 *            数据表名称
	 * @param applicationId
	 *            应用ID
	 * @return 表集合
	 */
	public static Collection<Table> getTables(String tableName,
			String applicationId) {
		Collection<Table> rtn = new ArrayList<Table>();

		Connection conn = null;
		try {
			conn = getConnection(applicationId);

			String dbType = getDBType(applicationId);

			return getTables(tableName, dbType, conn);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return rtn;
	}

	/**
	 * 
	 * @param tableName
	 *            数据表名称
	 * @param applicationId
	 *            应用ID
	 * @return 数据库表
	 */
	public static Table getTable(String tableName, String applicationId) {
		Collection<Table> tables = getTables(tableName, applicationId);
		if (tables != null && !tables.isEmpty()) {
			return tables.iterator().next();
		}

		return null;
	}

	/**
	 * Get the connection
	 * 
	 * @return the connection
	 * @throws Exception
	 */
	protected static Connection getConnection(String applicationid)
			throws Exception {
		ApplicationProcess process = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);
		ApplicationVO appvo = (ApplicationVO) process.doView(applicationid);

		if (appvo != null) {
			return appvo.getConnection();
		}
		return null;
	}

	public int executeBatch(String sql, String applicationid) throws Exception {
		String[] commands = sql.split(SQLBuilder.SQL_DELIMITER);
		return executeBatch(commands, applicationid);
	}

	/**
	 * 2.6新增
	 * 
	 * @param sql
	 * @param dt
	 * @return
	 * @throws Exception
	 */
	public int executeBatch(String sql, DataSource dt) throws Exception {
		String[] commands = sql.split(SQLBuilder.SQL_DELIMITER);
		return executeBatch(commands, dt);
	}

	public int executeBatch(String[] commands, String applicationid)
			throws Exception {
		Connection conn = getConnection(applicationid);
		return executeBatch(commands, conn);
	}

	/**
	 * 2.6新增
	 * 
	 * @param commands
	 * @param dt
	 * @return
	 * @throws Exception
	 */
	public int executeBatch(String[] commands, DataSource dt) throws Exception {
		if (dt != null) {
			return executeBatch(commands, dt.getConnection());
		}
		return 0;
	}

	public int executeBatch(String[] commands, Connection conn)
			throws Exception {
		int errors = 0;
		int commandCount = 0;
		Statement statement = null;
		// we tokenize the SQL along the delimiters, and we also make sure that
		// only delimiters
		// at the end of a line or the end of the string are used (row mode)
		try {
			if (conn != null && commands != null) {
				conn.setAutoCommit(false);
				statement = conn.createStatement();

				for (int i = 0; i < commands.length; i++) {
					String command = commands[i];

					if (command.trim().length() == 0) {
						continue;
					}
					commandCount++;
//					log.info("executing SQL: " + command);
					int results = statement.executeUpdate(command);

//					log.info("After execution, " + results
//							+ " row(s) have been changed");
				}
				conn.commit();
//				log.info("Executed " + commandCount + " SQL command(s) with "
//						+ errors + " error(s)");
			} else if (conn != null) {
				log.debug("Exception: has no connection,nothing has done");
			} else if (commands != null) {
				log.debug("message: the commands is null,nothing has done");
			}
		} catch (SQLException ex) {
			if (conn != null) {
				conn.rollback();
			}
			throw ex;
		} catch (Exception e) {
			if (conn != null) {
				conn.rollback();
			}
			throw e;
		} finally {
			statement.close();
		}
		return errors;
	}

	public static SQLFunction getSQLFunction(String applicationId)
			throws Exception {
		String dbType = getDBType(applicationId);
		if (dbType.equals(DBTYPE_ORACLE)) {
			return new OracleSQLFunction();
		} else if (DBTYPE_MSSQL.equals(dbType)) {
			return new MssqlSQLFunction();
		} else if (DBTYPE_MYSQL.equals(dbType)) {
			return new MysqlSQLFunction();
		} else if (DBTYPE_HSQLDB.equals(dbType)) {
			return new HsqldbSQLFunction();
		} else if (DBTYPE_DB2.equals(dbType)) {
			return new DB2SQLFunction();
		}

		return null;
	}

	public static String getDBType(String applicationId) throws Exception {

		String rtn = null;
		//log.warn("--------------appid:"+(applicationId==null?"null":applicationId));
		if (applicationId != null) {
			rtn = _dbTypes.get(applicationId);
		}

		if (rtn == null) {
			ApplicationProcess process = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			ApplicationVO appvo = (ApplicationVO) process.doView(applicationId);

			DataSource ds = appvo.getDataSourceDefine();

			rtn = ds.getDbTypeName();
			//log.warn("--------------appid:"+applicationId==null?"null":applicationId+"--------rtn"+rtn==null?"rtn":rtn);
			_dbTypes.put(applicationId, rtn);
		}

		return rtn;

	}

	/**
	 * 通过datasoureceId获取数据库类型
	 * 
	 * @param datasourceId
	 * @return
	 * @throws Exception
	 */
	public static String getDBTypeByDtId(String datasourceId) throws Exception {
		String dbType = null;
		if (datasourceId != null) {
			DataSourceProcess dp = (DataSourceProcess) ProcessFactory
					.createProcess(DataSourceProcess.class);
			DataSource dt = (DataSource) dp.doView(datasourceId);
			if (dt != null) {
				dbType = dt.getDbTypeName();
			}
		}
		return dbType;
	}

	public static String getSchema(Connection conn, String dbType) {
		if (dbType.equals(DBTYPE_ORACLE) || dbType.equals(DBTYPE_DB2)) {
			try {
				return conn.getMetaData().getUserName().trim().toUpperCase();
			} catch (SQLException sqle) {
				return "";
			}
		} else if (dbType.equals(DBTYPE_MYSQL)) {
			try {
				/*
				 * String schema =
				 * conn.getMetaData().getURL().trim().toUpperCase(); if
				 * (schema.indexOf("?USE") > 0) { schema =
				 * schema.substring(schema.lastIndexOf("/") + 1,
				 * schema.indexOf("?USE")); } else { schema =
				 * schema.substring(schema.lastIndexOf("/") + 1); } return
				 * schema;
				 */
				return conn.getCatalog();
			} catch (SQLException sqle) {
				return "";
			}

		} else if (dbType.equals(DBTYPE_MSSQL)) {
			try {
				ResultSet rs = conn.getMetaData().getSchemas();
				if (rs != null) {
//					while (rs.next()){
//						String retStr= rs.getString(1).trim().toUpperCase();
//						log.warn(retStr);
//					}
					return "DBO";
						
				}
			} catch (SQLException sqle) {
				return "";
			}
			/*
			 * try { String schema =
			 * conn.getMetaData().getURL().trim().toUpperCase(); int index =
			 * schema.indexOf("/"); if (index > 0) { if(schema.substring(index -
			 * 1, index).matches("\\d")){ return
			 * schema.substring(schema.lastIndexOf("/") + 1); } } return ""; }
			 * catch (SQLException sqle) { return ""; }
			 */
		} else if (dbType.equals(DBTYPE_HSQLDB)) {
			return "public".toUpperCase();
		}
		return "";
	}

	/**
	 * 取得application中的username作为schema为单独创建sql
	 * 
	 * @param applicationId
	 * @return
	 * @throws Exception
	 */
	public static String getSchema(String applicationId) throws Exception {
		ApplicationProcess process = (ApplicationProcess) ProcessFactory
				.createProcess(ApplicationProcess.class);
		ApplicationVO appvo = (ApplicationVO) process.doView(applicationId);
		DataSource ds = appvo.getDataSourceDefine();
		String dbType = ds.getDbTypeName();
		if (dbType.equals(DBTYPE_ORACLE) || dbType.equals(DBTYPE_DB2)) {
			return ds.getUsername() != null ? ds.getUsername().toUpperCase()
					: null;
		} else if (dbType.equals(DBTYPE_MYSQL)) {
			String schema = ds.getUrl().trim().toUpperCase();
			int index = schema.lastIndexOf("/");
			if (index > 0 && schema.substring(index - 1, index).matches("\\d"))
				if (schema.indexOf("?USE") > 0) {
					return schema.substring(index + 1, schema.indexOf("?USE"));
				} else {
					return schema.substring(index + 1);
				}
			return "";
		} else if (dbType.equals(DBTYPE_MSSQL)) {
			return "DBO";
		} else if (dbType.equals(DBTYPE_HSQLDB)) {
			return "public".toUpperCase();
		}
		return "";
	}

	public static synchronized void remove(String application) {
		_dbTypes.remove(application);
	}

	public static void main(String[] args) {
		String applicationId = "11de-ef9e-c010eee1-860c-e1cadb714510";

		try {
			Collection<Table> tables = getTablesBySchema("obpm", applicationId);
			for (Iterator<Table> iterator = tables.iterator(); iterator
					.hasNext();) {
				Table table = iterator.next();
				System.out.println(table.getName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
