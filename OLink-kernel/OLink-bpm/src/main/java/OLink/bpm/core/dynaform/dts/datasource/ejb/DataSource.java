package OLink.bpm.core.dynaform.dts.datasource.ejb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.util.DbTypeUtil;
import eWAP.core.ResourcePool;

/**
 * @hibernate.class table="T_DATASOURCE" lazy="false"
 * @author nicholas
 */
public class DataSource extends ValueObject {
	
	private static final long serialVersionUID = 7641787810321822255L;

	private String id;

	private String name;

	private String driverClass;

	private String url;

	private String username;

	private String password;

	private transient Connection connection;

	private int dbType;

	private String poolsize;

	private String timeout;

	public static final int DB_ORACLE = 1;

	public static final int DB_SQLSERVER = 2;

	public static final int DB_DB2 = 3;

	public static final int DB_MYSQL = 4;

	public static final int DB_HSQL = 5;

	public static Map<Integer, String> dbType2NameMap = new HashMap<Integer, String>();

	static {
		dbType2NameMap.put(DB_ORACLE, DbTypeUtil.DBTYPE_ORACLE);
		dbType2NameMap.put(DB_SQLSERVER, DbTypeUtil.DBTYPE_MSSQL);
		dbType2NameMap.put(DB_DB2, DbTypeUtil.DBTYPE_DB2);
		dbType2NameMap.put(DB_MYSQL, DbTypeUtil.DBTYPE_MYSQL);
		dbType2NameMap.put(DB_HSQL, DbTypeUtil.DBTYPE_HSQLDB);
	}

	public static Integer getDbTypeByName(String typeName) {
		for (Iterator<Entry<Integer, String>> iterator = dbType2NameMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, String> entry = iterator.next();
			if ((entry.getValue()).equalsIgnoreCase(typeName)) {
				return entry.getKey();
			}
		}
		return 0;
	}

	/**
	 * @hibernate.property column="DBTYPE"
	 * @return
	 */

	public int getDbType() {
		return dbType;
	}

	/**
	 * @param dbType
	 *            the dbType to set
	 * @uml.property name="dbType"
	 */
	public void setDbType(int dbType) {
		this.dbType = dbType;
	}

	/**
	 * @hibernate.property column="DRIVERCLASS"
	 * @return
	 * @uml.property name="driverClass"
	 */
	public String getDriverClass() {
		return driverClass;
	}

	/**
	 * @param driverClass
	 *            the driverClass to set
	 * @uml.property name="driverClass"
	 */
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 * @hibernate.property column="NAME"
	 * @return
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.property column="URL"
	 * @return
	 * @uml.property name="url"
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 * @uml.property name="url"
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 * @uml.property name="id"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 * @uml.property name="id"
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property column="PASSWORD"
	 * @return
	 * @uml.property name="password"
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 * @uml.property name="password"
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @hibernate.property column="USERNAME"
	 * @return
	 * @uml.property name="username"
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username
	 *            the username to set
	 * @uml.property name="username"
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public String getPoolsize() {
		return poolsize;
	}

	public void setPoolsize(String poolsize) {
		this.poolsize = poolsize;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getDbTypeName() {
		String typeName = dbType2NameMap.get(getDbType());
		return typeName;
	}

	/**
	 * @return the connection
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SQLException
	 * @uml.property name="connection"
	 */
	public Connection getConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (connection == null || connection.isClosed()) {
			if(driverClass!=null)
			{
				Class.forName(getDriverClass()).newInstance();
				connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
			}
			//增加 by XGY
			else
			{
				connection=ResourcePool.getDataSource(id).getConnection();
			}
		}

		return connection;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		buffer.append("DriverClass: '" + getDriverClass() + "', ");
		buffer.append("DBType: '" + dbType2NameMap.get(getDbType()) + "', ");
		buffer.append("DBurl: '" + getUrl() + "',");
		buffer.append("UserName: '" + getUsername() + "', ");
		buffer.append("Password: '" + getPassword() + "', ");
		buffer.append("Poolsize: '" + getPoolsize() + "', ");
		buffer.append("Timeout: '" + getTimeout() + "'");
		buffer.append("}");

		return buffer.toString();
	}
}
