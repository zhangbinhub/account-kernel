package OLink.bpm.core.dynaform.pending.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.user.dao.UserDAO;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workflow.storage.runtime.dao.FlowStateRTDAO;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.PersistenceUtils;

public abstract class AbstractPendingDAO {

	Logger LOG = Logger.getLogger(AbstractPendingDAO.class);

	protected Connection connection;

	protected String schema;

	protected String dbType = "Oracle: ";

	protected UserDAO userDAO;

	public FlowStateRTDAO stateRTDAO;

	public AbstractPendingDAO(Connection connection) {
		this.connection = connection;
		try {
			userDAO = (UserDAO) DAOFactory
					.getDefaultDAO(UserVO.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void create(ValueObject vo) throws Exception {
		PendingVO pendingVO = (PendingVO) vo;
		PreparedStatement statement = null;
		PreparedStatement statement2 = null;

		String sql = "INSERT INTO "
				+ getFullTableName("T_PENDING")
				+ "(ID,FORMID,FORMNAME, FLOWID, CREATED, LASTMODIFIED, AUDITDATE, AUTHOR ,AUDITUSER, STATELABEL, STATE, AUDITORNAMES, LASTFLOWOPERATION, LASTMODIFIER, SUMMARY, APPLICATIONID,DOMAINID)";
		sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		LOG.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			setParameters(statement, pendingVO);

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
			PersistenceUtils.closeStatement(statement2);
		}
	}

	private int setParameters(PreparedStatement statement, PendingVO pendingVO)
			throws SQLException {
		int paramterIndex = 0;
		statement.setObject(++paramterIndex, pendingVO.getId());
		statement.setObject(++paramterIndex, pendingVO.getFormid());
		statement.setObject(++paramterIndex, pendingVO.getFormname());
		statement.setObject(++paramterIndex, pendingVO.getFlowid());

		if (pendingVO.getCreated() != null) {
			statement.setTimestamp(++paramterIndex, new Timestamp(pendingVO
					.getCreated().getTime()));
		} else {
			statement.setTimestamp(++paramterIndex, null);
		}
		if (pendingVO.getLastmodified() != null) {
			statement.setTimestamp(++paramterIndex, new Timestamp(pendingVO
					.getLastmodified().getTime()));
		} else {
			statement.setTimestamp(++paramterIndex, null);
		}
		if (pendingVO.getAuditdate() != null) {
			statement.setTimestamp(++paramterIndex, new Timestamp(pendingVO
					.getAuditdate().getTime()));
		} else {
			statement.setTimestamp(++paramterIndex, null);
		}

		if (pendingVO.getAuthor() != null) {
			statement.setObject(++paramterIndex, pendingVO.getAuthor().getId());
		} else {
			statement.setObject(++paramterIndex, null);
		}
		statement.setObject(++paramterIndex, pendingVO.getAudituser());
		statement.setObject(++paramterIndex, pendingVO.getStateLabel());

		if (pendingVO.getState() != null) {
			statement.setObject(++paramterIndex, pendingVO.getState().getId());
		} else {
			statement.setObject(++paramterIndex, null);
		}

		statement.setObject(++paramterIndex, pendingVO.getAuditorNames());
		statement.setObject(++paramterIndex, pendingVO.getLastFlowOperation());
		statement.setObject(++paramterIndex, pendingVO.getLastmodifier());
		statement.setObject(++paramterIndex, pendingVO.getSummary());
		statement.setObject(++paramterIndex, pendingVO.getApplicationid());
		statement.setObject(++paramterIndex, pendingVO.getDomainid());

		return paramterIndex;
	}

	public void remove(String pk) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_PENDING")
				+ " WHERE ID = '" + pk + "'";
		String sql2 = "DELETE FROM " + getFullTableName("T_PENDING_ACTOR_SET")
				+ " WHERE DOCID = '" + pk + "'";
//		LOG.info(dbType + sql);
//		LOG.info(dbType + sql2);
		Statement statement = connection.createStatement();
		try {
			statement.addBatch(sql);
			statement.addBatch(sql2);
			statement.executeBatch();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void update(ValueObject vo) throws Exception {
		PendingVO pendingVO = (PendingVO) vo;
		String sql = "UPDATE "
				+ getFullTableName("T_PENDING")
				+ " SET ID=?,FORMID=?,FORMNAME=?, FLOWID=?, CREATED=?, LASTMODIFIED=?, AUDITDATE=?, AUTHOR=? ,AUDITUSER=?, STATELABEL=?, STATE=?, AUDITORNAMES=?, LASTFLOWOPERATION=?, LASTMODIFIER=?, SUMMARY=?, APPLICATIONID=?,DOMAINID=? WHERE ID=?";
//		LOG.info(dbType + sql);
		PreparedStatement statement = null;
		Statement statement2 = null;
		try {
			statement = connection.prepareStatement(sql);
			int parameterIndex = setParameters(statement, pendingVO);
			statement.setString(++parameterIndex, pendingVO.getId());

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
			PersistenceUtils.closeStatement(statement2);
		}
	}

	public ValueObject find(String id) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_PENDING")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
		ResultSet rs=null;
//		LOG.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				PendingVO pendingVO = new PendingVO();
				setProperties(rs, pendingVO);
				return pendingVO;
			}
			return null;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void setProperties(ResultSet rs, PendingVO pendingVO)
			throws Exception {
		pendingVO.setId(rs.getString("ID"));
		pendingVO.setFormid(rs.getString("FORMID"));
		pendingVO.setFormname(rs.getString("FORMNAME"));
		pendingVO.setFlowid(rs.getString("FLOWID"));
		pendingVO.setApplicationid(rs.getString("APPLICATIONID"));
		pendingVO.setAuditdate(rs.getDate("AUDITDATE"));
		pendingVO.setAuditorNames(rs.getString("AUDITORNAMES"));
		pendingVO.setAudituser(rs.getString("AUDITUSER"));
		String authorId = rs.getString("AUTHOR");
		if (!StringUtil.isBlank(authorId)) {
			UserVO author = (UserVO) userDAO.find(authorId);
			pendingVO.setAuthor(author);
		}
		pendingVO.setCreated(rs.getDate("CREATED"));
		pendingVO.setLastFlowOperation(rs.getString("LASTFLOWOPERATION"));
		pendingVO.setLastmodifier(rs.getString("LASTMODIFIER"));
		//lr check:there is an problem
		String stateId = rs.getString("STATE");
		if (!StringUtil.isBlank(stateId)) {
			FlowStateRT stateRT = (FlowStateRT) stateRTDAO.find(stateId);
			pendingVO.setState(stateRT);
		}
		pendingVO.setStateLabel(rs.getString("STATELABEL"));
		pendingVO.setSummary(rs.getString("SUMMARY"));
		pendingVO.setDomainid(rs.getString("DOMAINID"));
	}

	public long countByFilter(ParamsTable params, WebUser user)
			throws Exception {
		return countBySQL(createPendingSQL(params, user), user.getDomainid());
	}

	public DataPackage<PendingVO> queryByFilter(ParamsTable params, WebUser user)
			throws Exception {
		Long _currpage = params.getParameterAsLong("_currpage");
		Long _pagelines = params.getParameterAsLong("_pagelines");
		int currpage = _currpage != null ? _currpage.intValue() : 1;
		int pagelines = _pagelines != null ? _pagelines.intValue() : 10;

		String _orderby = params.getParameterAsString("_orderby");

		StringBuffer sqlBuilder = new StringBuffer();
		String sql = createPendingSQL(params, user);

		sqlBuilder.append(sql);
		sqlBuilder.append(" ORDER BY ").append(
				StringUtil.isBlank(_orderby) ? "ID" : _orderby.toUpperCase());

		return queryBySQLPage(sqlBuilder.toString(), currpage, pagelines, user
				.getDomainid());
	}

	protected String createPendingSQL(ParamsTable params, WebUser user)
			throws Exception {
		FormProcess process = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		
		StringBuffer sqlBuilder = new StringBuffer();
		String formId = params.getParameterAsString("formid");
		Form form = (Form) process.doView(formId);

		sqlBuilder.append("SELECT * FROM T_PENDING WHERE STATE IN");
		sqlBuilder
				.append(" (SELECT FLOWSTATERT_ID FROM T_ACTORRT WHERE ACTORID IN("
						+ user.getActorListString(form.getApplicationid()) + ") AND PENDING=1)");
		sqlBuilder.append(" AND FORMID='" + formId).append("'");
		sqlBuilder.append(" AND SUMMARY IS NOT NULL");

		return sqlBuilder.toString();
	}

	public DataPackage<PendingVO> queryBySQL(String sql, String domainid) throws Exception {
		return queryBySQLPage(sql, 1, Integer.MAX_VALUE, domainid);
	}

	public DataPackage<PendingVO> queryBySQLPage(String sql, int page, int lines,
			String domainid) throws Exception {

		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");
		/**
		 * 生成分页SQL
		 */
		String limitSQL = buildLimitString(sql, page, lines);
//		LOG.info(dbType + limitSQL);
		PreparedStatement statement = null;
		ResultSet rs=null;
		try {
			DataPackage<PendingVO> dpg = new DataPackage<PendingVO>();
			statement = connection.prepareStatement(limitSQL);
			rs = statement.executeQuery();
			Collection<PendingVO> datas = new ArrayList<PendingVO>();
			
			for (int i = 0; i < lines && rs.next(); i++) {
				PendingVO pendingVO = new PendingVO();
				setProperties(rs, pendingVO);
				datas.add(pendingVO);
			}

			dpg.datas = datas;
			dpg.rowCount = (int) countBySQL(sql, domainid);
			dpg.linesPerPage = lines;
			dpg.pageNo = page;

			return dpg;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);//Add By XGY 20130218
			PersistenceUtils.closeStatement(statement);
		}
	}

	public abstract String buildLimitString(String sql, int page, int lines);

	public long countBySQL(String sql, String domainid) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");
		String countSQL = "SELECT COUNT(*) FROM (" + sql + ") TB";

//		LOG.info(dbType + countSQL);
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

	public String getFullTableName(String tblname) {
		if (this.schema != null && !this.schema.trim().equals("")) {
			return this.schema.trim().toUpperCase() + "."
					+ tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
	}

	public FlowStateRTDAO getStateRTDAO() {
		return stateRTDAO;
	}

	public void setStateRTDAO(FlowStateRTDAO stateRTDAO) {
		this.stateRTDAO = stateRTDAO;
	}

	public void remove(ValueObject vo) throws Exception {
		remove(vo.getId());
	}
}
