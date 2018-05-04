package OLink.bpm.core.counter.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.counter.ejb.CounterVO;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

/**
 * 
 * @author Chris
 * 
 */
public abstract class AbstractCounterDAO {
	Logger log = Logger.getLogger(AbstractCounterDAO.class);

	protected String dbType = "Oracle: ";

	protected String schema = "";

	protected Connection connection;

	public AbstractCounterDAO(Connection conn) throws Exception {
		this.connection = conn;
	}

	public void create(ValueObject vo) throws Exception {
		CounterVO counter = (CounterVO) vo;
		PreparedStatement statement = null;

		String sql = "INSERT INTO " + getFullTableName("T_COUNTER")
				+ "(ID,NAME,COUNTER,SORTID,APPLICATIONID,DOMAINID)";
		sql += " VALUES(?,?,?,?,?,?)";
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);

			setValues(statement, 1, counter.getId());
			setValues(statement, 2, counter.getName());
			setValues(statement, 3, Integer.valueOf(counter.getCounter()));
			setValues(statement, 4, counter.getSortId());
			setValues(statement, 5, counter.getApplicationid());
			setValues(statement, 6, counter.getDomainid());

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public ValueObject find(String id) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_COUNTER")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
		ResultSet rs=null;
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValues(statement, 1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				CounterVO counter = new CounterVO();
				counter.setId(rs.getString("ID"));
				counter.setName(rs.getString("NAME"));
				counter.setCounter(rs.getInt("COUNTER"));
				counter.setSortId(rs.getString("SORTID"));
				counter.setApplicationid(rs.getString("APPLICATIONID"));
				counter.setDomainid(rs.getString("DOMAINID"));

				return counter;
			}
			return null;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	public Object getData(String sql, String domainid) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs=null;
		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		if (StringUtil.isBlank(domainid))
			sql = sqlUtil.appendCondition(sql, "DOMAINID IS NULL");
		else
			sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs.next()) {
				CounterVO counter = new CounterVO();
				counter.setId(rs.getString("ID"));
				counter.setName(rs.getString("NAME"));
				counter.setCounter(rs.getInt("COUNTER"));
				counter.setSortId(rs.getString("SORTID"));
				counter.setApplicationid(rs.getString("APPLICATIONID"));
				counter.setDomainid(rs.getString("DOMAINID"));

				return counter;
			}
			return null;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	public Collection<CounterVO> getDatas(String sql, String domainid) throws Exception {
		Collection<CounterVO>  rtn = new ArrayList<CounterVO>();
		PreparedStatement statement = null;
		ResultSet rs=null;
		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		if (StringUtil.isBlank(domainid))
			sql = sqlUtil.appendCondition(sql, "DOMAINID IS NULL");
		else
			sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs.next()) {
				CounterVO counter = new CounterVO();
				counter.setId(rs.getString("ID"));
				counter.setName(rs.getString("NAME"));
				counter.setCounter(rs.getInt("COUNTER"));
				counter.setSortId(rs.getString("SORTID"));
				counter.setApplicationid(rs.getString("APPLICATIONID"));
				counter.setDomainid(rs.getString("DOMAINID"));
				rtn.add(counter);
			}
			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void removeById(String id) throws Exception {
		String sql = "DELETE " + getFullTableName("T_COUNTER")
				+ " WHERE ID = '" + id + "'";
		execSQL(sql);
	}

	public void update(ValueObject vo) throws Exception {
		CounterVO counter = (CounterVO) vo;

		PreparedStatement statement = null;

		String sql = "UPDATE "
				+ getFullTableName("T_COUNTER")
				+ " SET ID=?,NAME=?,COUNTER=?,SORTID=?,APPLICATIONID=?,DOMAINID=?";
		sql += " WHERE ID=?";
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);

			setValues(statement, 1, counter.getId());
			setValues(statement, 2, counter.getName());
			setValues(statement, 3, Integer.valueOf(counter.getCounter()));
			setValues(statement, 4, counter.getSortId());
			setValues(statement, 5, counter.getApplicationid());
			setValues(statement, 7, counter.getId());
			setValues(statement, 6, counter.getDomainid());

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public CounterVO findByName(String name, String application, String domainid)
			throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_COUNTER")
				+ " WHERE NAME = '" + name + "' AND  APPLICATIONID = '"
				+ application + "'";
		return (CounterVO) getData(sql, domainid);
	}

	public void removeByName(String name, String application, String domainid)
			throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_COUNTER")
				+ " WHERE NAME = '" + name + "' AND APPLICATIONID = '"
				+ application + "' AND DOMAINID='" + domainid + "'";
		execSQL(sql);
	}

	/**
	 * Execute the sql statement.
	 * 
	 * @param sql
	 *            The sql statement.
	 * @throws Exception
	 */
	protected void execSQL(String sql) throws Exception {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			statement.execute();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}

	}

	/**
	 * Get the current session.
	 * 
	 * @return The current session.
	 * @throws Exception
	 */
	private void setValues(PreparedStatement statement, int index, Object obj)
			throws SQLException {
		if (obj != null) {
			statement.setObject(index, obj);
		} else {
			statement.setNull(index, Types.NULL);
		}
	}

	public void remove(String pk) throws Exception {
		String sql = "DELETE " + getFullTableName("T_COUNTER") + " WHERE ID=?";
		PreparedStatement statement = null;
//		log.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValues(statement, 1, pk);

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public String getFullTableName(String tblname) {
		if (this.schema != null && !this.schema.trim().equals("")) {
			return this.schema.trim().toUpperCase() + "."
					+ tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
	}
}
