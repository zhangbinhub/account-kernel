package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.ejb.Circulator;
import org.apache.log4j.Logger;

/**
 * @author happy
 * 
 */
public abstract class AbstractCirculatorDAO {

	Logger log = Logger.getLogger(AbstractCirculatorDAO.class);

	protected String dbTag = "Oracle: ";

	protected String schema = "";

	protected Connection connection;

	public AbstractCirculatorDAO(Connection conn) throws Exception {
		this.connection = conn;
	}

	public void create(ValueObject vo) throws Exception {
		Circulator circulator = (Circulator) vo;
		PreparedStatement statement = null;

		String sql = "INSERT INTO "
				+ getFullTableName("T_CIRCULATOR")
				+ "(ID,NAME,USERID,DOC_ID,NODERT_ID,FLOWSTATERT_ID,CCTIME,READTIME,DEADLINE,ISREAD,DOMAINID,APPLICATIONID)";
		sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setParameters(statement, circulator);

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public ValueObject find(String id) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_CIRCULATOR")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				Circulator vo = new Circulator();
				setProperties(rs, vo);
				return vo;
			}
			return null;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}
	}

	public int setParameters(PreparedStatement statement, Circulator circulator)
			throws SQLException {
		int index = 1;
		statement.setObject(index++, circulator.getId());
		statement.setObject(index++, circulator.getName());
		statement.setObject(index++, circulator.getUserId());
		statement.setObject(index++, circulator.getDocId());
		statement.setObject(index++, circulator.getNodertId());
		statement.setObject(index++, circulator.getFlowstatertId());
		statement.setObject(index++,
				circulator.getCcTime() != null ? new Timestamp(circulator
						.getCcTime().getTime()) : null);
		statement.setObject(index++,
				circulator.getReadTime() != null ? new Timestamp(circulator
						.getReadTime().getTime()) : null);
		statement.setObject(index++,
				circulator.getDeadline() != null ? new Timestamp(circulator
						.getDeadline().getTime()) : null);
		statement.setObject(index++, circulator.isRead() ? Integer.valueOf(1)
				: Integer.valueOf(0));// 是否已阅
		statement.setObject(index++, circulator.getDomainid());
		statement.setObject(index++, circulator.getApplicationid());
		return index;
	}

	public void setProperties(ResultSet rs, Circulator vo) throws SQLException {
		vo.setId(rs.getString("ID"));
		vo.setName(rs.getString("NAME"));
		vo.setDocId(rs.getString("DOC_ID"));
		vo.setUserId(rs.getString("USERID"));
		vo.setNodertId(rs.getString("NODERT_ID"));
		vo.setFlowstatertId(rs.getString("FLOWSTATERT_ID"));
		vo.setCcTime(rs.getTimestamp("CCTIME"));
		vo.setReadTime(rs.getTimestamp("READTIME"));
		vo.setDeadline(rs.getTimestamp("DEADLINE"));
		vo.setRead(rs.getInt("ISREAD") == 1 ? true : false);// 是否已阅
		vo.setDomainid(rs.getString("DOMAINID"));
		vo.setApplicationid(rs.getString("APPLICATIONID"));

	}

	/**
	 * 根据外键(DOCID、NODERT_ID、FLOWSTATERT_ID)级联查找Circulator
	 * 
	 * @param key
	 *            外键
	 * @param val
	 *            外键值
	 * @return 级联查找ActorRT
	 * @throws Exception
	 */
	public Collection<Circulator> queryByForeignKey(String key, Object val)
			throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_CIRCULATOR")
				+ " WHERE " + key.toUpperCase() + "=? ORDER BY ID";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setObject(1, val);
			rs = statement.executeQuery();
			Collection<Circulator> rtn = new ArrayList<Circulator>();
			while (rs.next()) {
				Circulator vo = new Circulator();
				setProperties(rs, vo);
				rtn.add(vo);
			}
			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 根据外键(DOCID、NODERT_ID、FLOWSTATERT_ID)删除Circulator
	 * 
	 * @param key
	 *            外键字段名
	 * @param val
	 *            外键值
	 * @throws Exception
	 */
	public void removeByForeignKey(String key, Object val) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_CIRCULATOR")
				+ " WHERE " + key.toUpperCase() + "=?";
		PreparedStatement statement = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setObject(1, val);
			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void update(ValueObject vo) throws Exception {
		Circulator circulator = (Circulator) vo;

		PreparedStatement statement = null;

		String sql = "UPDATE "
				+ getFullTableName("T_CIRCULATOR")
				+ " SET ID=?,NAME=?,USERID=?,DOC_ID=?,NODERT_ID=?,FLOWSTATERT_ID=?,CCTIME=?,READTIME=?,DEADLINE=?,ISREAD=?,DOMAINID=?,APPLICATIONID=?";
		sql += " WHERE ID=?";
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			int index = setParameters(statement, circulator);
			statement.setString(index, circulator.getId());

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 根据主键删除
	 * 
	 * @param pk
	 * @throws Exception
	 */
	public void remove(String pk) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_CIRCULATOR")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, pk);

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

	public DataPackage<Circulator> queryPendingByUser(ParamsTable params,
													  WebUser user) throws Exception {
		Long _currpage = params.getParameterAsLong("_currpage");
		Long _pagelines = params.getParameterAsLong("_pagelines");
		int currpage = _currpage != null ? _currpage.intValue() : 1;
		int pagelines = _pagelines != null ? _pagelines.intValue() : 10;

		// String _orderby = params.getParameterAsString("_orderby");

		StringBuffer sqlBuilder = new StringBuffer();
		String sql = createPendingSQL(params, user);

		sqlBuilder.append(sql);
		// sqlBuilder.append(" ORDER BY ").append(
		// StringUtil.isBlank(_orderby) ? "ID" : _orderby.toUpperCase());

		return queryBySQLPage(sqlBuilder.toString(), currpage, pagelines,
				user.getDomainid());
	}

	public DataPackage<Circulator> queryWorksByUser(ParamsTable params,
			WebUser user) throws Exception {
		Long _currpage = params.getParameterAsLong("_currpage");
		Long _pagelines = params.getParameterAsLong("_pagelines");
		int currpage = _currpage != null && _currpage>0 ? _currpage.intValue() : 1;
		int pagelines = _pagelines != null && _pagelines>0 ? _pagelines.intValue() : 10;
		StringBuffer sqlBuilder = new StringBuffer();
		String sql = createWorkSQL(params, user);

		sqlBuilder.append(sql);

		return queryBySQLPage(sqlBuilder.toString(), currpage, pagelines,
				user.getDomainid());
	}

	protected String createPendingSQL(ParamsTable params, WebUser user)
			throws Exception {

		StringBuffer sqlBuilder = new StringBuffer();
		String formId = params.getParameterAsString("formid");
		sqlBuilder
				.append("SELECT c.ID ,c.NAME ,c.USERID ,c.DOC_ID ,c.NODERT_ID ,c.FLOWSTATERT_ID ,c.CCTIME ,c.READTIME ,c.DEADLINE ,c.ISREAD ,c.DOMAINID ,c.APPLICATIONID,p.SUMMARY,p.FORMID FROM "
						+ getFullTableName("T_CIRCULATOR")
						+ " c,"
						+ getFullTableName("T_PENDING")
						+ " p WHERE c.DOC_ID = p.ID AND c.ISREAD = 0 AND p.FORMID = '"
						+ formId + "'");
		sqlBuilder.append(" AND c.USERID IN('" + user.getId() + "')");

		return sqlBuilder.toString();
	}

	protected String createWorkSQL(ParamsTable params, WebUser user)
			throws Exception {
		StringBuffer sqlBuilder = new StringBuffer();
		String isRead = params.getParameterAsString("_isRead");
		sqlBuilder
				.append("SELECT c.ID ,c.NAME ,c.USERID ,c.DOC_ID ,c.NODERT_ID ,c.FLOWSTATERT_ID ,c.CCTIME ,c.READTIME ,c.DEADLINE ,c.ISREAD ,c.DOMAINID ,c.APPLICATIONID,p.SUMMARY,p.FORMID FROM "
						+ getFullTableName("T_CIRCULATOR")
						+ " c,"
						+ getFullTableName("T_PENDING")
						+ " p WHERE c.DOC_ID = p.ID AND c.ISREAD = "+isRead);
		sqlBuilder.append(" AND c.USERID IN('" + user.getId() + "')");

		return sqlBuilder.toString();
	}

	public DataPackage<Circulator> queryBySQLPage(String sql, int page,
			int lines, String domainid) throws Exception {

		// HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		/**
		 * 生成分页SQL
		 */
		String limitSQL = buildLimitString(sql, page, lines);
//		log.info(dbTag + limitSQL);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			DataPackage<Circulator> dpg = new DataPackage<Circulator>();
			statement = connection.prepareStatement(limitSQL);
			rs = statement.executeQuery();
			Collection<Circulator> datas = new ArrayList<Circulator>();

			for (int i = 0; i < lines && rs.next(); i++) {
				Circulator vo = new Circulator();
				setProperties(rs, vo);
				vo.setSummary(rs.getString("SUMMARY"));
				vo.setFormId(rs.getString("FORMID"));
				datas.add(vo);
			}

			dpg.datas = datas;
			dpg.rowCount = (int) countBySQL(sql, domainid);
			dpg.linesPerPage = lines;
			dpg.pageNo = page;

			return dpg;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}
	}

	public abstract String buildLimitString(String sql, int page, int lines);

	public long countBySQL(String sql, String domainid) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

//		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		// sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");
		String countSQL = "SELECT COUNT(*) FROM (" + sql + ") TB";
//		log.info(dbTag + countSQL);
		try {
			statement = connection.prepareStatement(countSQL);
			rs = statement.executeQuery();

			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}

		return 0;
	}
	

	/**
	 * 根据当前文档的信息查找
	 * @return
	 */
	public Circulator findByCurrDoc(String docId ,String flowStateId,boolean isRead,WebUser user) throws Exception{
		int _isRead = 0;
		if(isRead){
			_isRead = 1;
		}
		String sql = "SELECT * FROM "+ getFullTableName("T_CIRCULATOR")+" c WHERE c.DOC_ID =? AND c.FLOWSTATERT_ID =? AND c.USERID =? AND c.ISREAD =?";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setObject(1, docId);
			statement.setObject(2, flowStateId);
			statement.setObject(3, user.getId());
			statement.setObject(4, _isRead);
			rs = statement.executeQuery();
			if (rs.next()) {
				Circulator vo = new Circulator();
				setProperties(rs, vo);
				return vo;
			}
			return null;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
		
	}
}
