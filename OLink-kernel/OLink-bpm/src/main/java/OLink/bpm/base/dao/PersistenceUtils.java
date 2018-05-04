package OLink.bpm.base.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.logicalcobwebs.proxool.ProxoolDataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

/**
 * The persistence utility.
 */
public class PersistenceUtils {
	private final static Log logger = LogFactory.getLog(PersistenceUtils.class);

	/**
	 * @uml.property name="sessionSignal"
	 */
	private static final ThreadLocal<SessionSignal> sessionSignal = new ThreadLocal<SessionSignal>();

	public static final ThreadLocal<Map<String, Connection>> runtimeDBConn = new ThreadLocal<Map<String, Connection>>();

	/**
	 * 表格-状态(是否存在)映射
	 */
	public static Map<Object, Object> tableStateMap = new HashMap<Object, Object>();

	/**
	 * @return the sessionSignal
	 * @uml.property name="sessionSignal"
	 */
	public static SessionSignal getSessionSignal() {
		SessionSignal sg = sessionSignal.get();
		if (sg == null) {
			sg = new SessionSignal();
			sessionSignal.set(sg);
		}
		return sg;
	}

	/**
	 * Get the current session.
	 * 
	 * @return The current session.
	 * @throws Exception
	 */
	public static Session currentSession() throws Exception {
		try {
			logger.debug("get session!");
			return HibernateBaseDAO.currentSession();
		} catch (HibernateException he) {
			throw new Exception(he);
		}
	}

	/**
	 * Close the session.
	 * 
	 * @throws Exception
	 */
	public static void closeSession() throws Exception {
		try {
			logger.debug("close session!");
			HibernateBaseDAO.closeSession();
			SessionSignal sg = sessionSignal.get();
			if (sg != null) {
				sessionSignal.set(null);
			}
		} catch (HibernateException he) {
			throw new Exception(he);
		}
	}

	/**
	 * 关闭Hibernate Session及其他数据库连接
	 * 
	 * @throws Exception
	 */
	public static void closeSessionAndConnection() throws Exception {
		PersistenceUtils.closeSession();

		Map<?, Connection> connMap = PersistenceUtils.runtimeDBConn.get();
		if (connMap != null) {
			for (Iterator<Connection> iterator = connMap.values().iterator(); iterator.hasNext();) {
				//Connection conn = (Connection) iterator.next();
				Connection conn = iterator.next();
				if (!conn.isClosed()) {
					conn.close();
				}
				conn = null;
			}
		}

		PersistenceUtils.runtimeDBConn.set(null);
	}

	/**
	 * Open the transcation.
	 * 
	 * @throws Exception
	 */
	public static void beginTransaction() throws Exception {
		try {
			HibernateBaseDAO.beginTransaction();
		} catch (HibernateException he) {
			throw new Exception(he);
		}
	}

	/**
	 * Commit the transcation.
	 * 
	 * @throws Exception
	 */
	public static void commitTransaction() throws Exception {
		try {
			HibernateBaseDAO.commitTransaction();
		} catch (HibernateException he) {
			throw new Exception(he);
		}
	}

	/**
	 * Roll back the transaction.
	 * 
	 * @throws Exception
	 */
	public static void rollbackTransaction() throws Exception {
		try {
			HibernateBaseDAO.rollbackTransaction();
		} catch (HibernateException he) {
			throw new Exception(he);
		}
	}

	public static DataSource getC3P0DataSource(String username, String password, String driver, String url,
			String poolSize, String timeout) throws Exception {

		ComboPooledDataSource ds = new ComboPooledDataSource();

		ds.setUser(username);
		ds.setPassword(password);
		ds.setDriverClass(driver);
		ds.setJdbcUrl(url);
		ds.setMaxPoolSize(Integer.parseInt(poolSize));
		ds.setMaxIdleTime(Integer.parseInt(timeout));
		// ds.setMaxAdministrativeTaskTime(5000);
		ds.setNumHelperThreads(20);
		ds.setMaxStatements(0);

		return ds;
	}

	public static DataSource getDBCPDataSource(String username, String password, String driver, String url,
			String poolSize, String timeout) {

		BasicDataSource ds = new BasicDataSource();

		ds.setUsername(username);
		ds.setPassword(password);
		ds.setDriverClassName(driver);
		ds.setUrl(url);
		// ds.setPoolPreparedStatements(true);
		// ds.setMaxOpenPreparedStatements(10);
		// ds.setInitialSize(10);
		ds.setMaxIdle(5);
		ds.setMaxActive(Integer.parseInt(poolSize));

		ds.setMaxWait(Integer.parseInt(timeout));
		ds.setDefaultAutoCommit(true);

		return ds;
	}

	public static DataSource getProxoolDataSource(String username, String password, String driver, String url,
			String poolSize, String timeout) {


		ProxoolDataSource ds = new ProxoolDataSource();
		
		ds.setAlias("Runtime:"+url);

		ds.setUser(username);
		ds.setPassword(password);
		ds.setDriver(driver);
		ds.setDriverUrl(url);

		//jdbc-0.proxool.maximum-connection-count=50
		ds.setMaximumConnectionCount(Integer.parseInt(poolSize));
		
		//jdbc-0.proxool.minimum-connection-count=10
		ds.setMinimumConnectionCount(5);
		
		//jdbc-0.proxool.prototype-count=4
		ds.setPrototypeCount(4);
		
		//jdbc-0.proxool.house-keeping-test-SQL=SELECTCURRENT_DATE
		ds.setHouseKeepingTestSql("SELECTCURRENT_DATE");
		
		//jdbc-0.proxool.verbose=true
		ds.setVerbose(false);
		
		//jdbc-0.proxool.statistics=10s,1m,1d
		ds.setStatistics("10s,1m,1d");
		
		//jdbc-0.proxool.statistics-log-level=DEBUG
//		ds.setStatisticsLogLevel("DEBUG");
		
		//jdbc-0.proxool.house-keeping-sleep-time=30000
		ds.setHouseKeepingSleepTime(30000);
		
		//jdbc-0.proxool.maximum-connection-lifetime=60000
		ds.setMaximumConnectionLifetime(60000);
		
		//jdbc-0.proxool.simultaneous-build-throttle=20
		ds.setSimultaneousBuildThrottle(20);
		
		//jdbc-0.proxool.overload-wIThout-refusal-lifetime=10000
		ds.setOverloadWithoutRefusalLifetime(10000);
		
		//jdbc-0.proxool.maximum-active-time=50000
		ds.setMaximumActiveTime(Integer.parseInt(timeout));
		
		//jdbc-0.proxool.trace=true
		ds.setTrace(false);
		
		//jdbc-0.proxool.fatal-SQL-exception=ORA-1234
//		ds.setFatalSqlExceptionsAsString("");

//		ds.setDefaultAutoCommit(true);

		return ds;
	}
	
	public static Connection getDBConnection() throws Exception {
		Session s = PersistenceUtils.currentSession();
		if (s != null) {
			return s.connection();
		}
		return null;
	}

	public static void closeConnection(Connection dbConnection) throws DAOException {
		try {
			if (dbConnection != null && !dbConnection.isClosed()) {
				dbConnection.close();
			}
		} catch (SQLException se) {
			throw new DAOException("SQL Exception while closing " + "DB connection : \n" + se);
		}
	}

	public static void closeResultSet(ResultSet result) throws DAOException {
		try {
			if (result != null) {
				result.close();
			}
		} catch (SQLException se) {
			throw new DAOException("SQL Exception while closing " + "Result Set : \n" + se);
		}
	}

	public static void closeStatement(Statement stmt) throws DAOException {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException se) {
			throw new DAOException("SQL Exception while closing " + "Statement : \n" + se);
		}
	}

	public static Map<Object, Object> getTableStateMap() {
		return tableStateMap;
	}
}
