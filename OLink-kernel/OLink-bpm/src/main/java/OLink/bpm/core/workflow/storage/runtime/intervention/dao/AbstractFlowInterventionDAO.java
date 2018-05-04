package OLink.bpm.core.workflow.storage.runtime.intervention.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.intervention.ejb.FlowInterventionVO;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

/**
 * @author Happy
 *
 */
public abstract class AbstractFlowInterventionDAO {

	Logger LOG = Logger.getLogger(AbstractFlowInterventionDAO.class);

	protected Connection connection;

	protected String schema;

	protected String dbType = "Oracle: ";


	public AbstractFlowInterventionDAO(Connection connection) {
		this.connection = connection;
	}

	public void create(ValueObject obj) throws Exception {
		FlowInterventionVO vo = (FlowInterventionVO) obj;
		PreparedStatement statement = null;

		String sql = "INSERT INTO "
				+ getFullTableName("T_FLOW_INTERVENTION")
				+ "(ID,SUMMARY,FLOWNAME, STATELABEL, INITIATOR, LASTAUDITOR, FIRSTPROCESSTIME, LASTPROCESSTIME ,FLOWID, FORMID, DOCID, APPLICATIONID, DOMAINID, VERSION)";
		sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		LOG.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			setParameters(statement, vo);
			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	private int setParameters(PreparedStatement statement, FlowInterventionVO vo)
			throws SQLException {
		int paramterIndex = 0;
		statement.setObject(++paramterIndex, vo.getId());
		statement.setObject(++paramterIndex, vo.getSummary());
		statement.setObject(++paramterIndex, vo.getFlowName());
		statement.setObject(++paramterIndex, vo.getStateLabel());
		statement.setObject(++paramterIndex, vo.getInitiator());
		statement.setObject(++paramterIndex, vo.getLastAuditor());
		//为了兼容sql server数据库
		if(vo.getFirstProcessTime()!=null){
			statement.setObject(++paramterIndex, new Date(vo.getFirstProcessTime().getTime()));
		} else {
			statement.setObject(++paramterIndex,null);
		}
		if(vo.getLastProcessTime()!=null){
			statement.setObject(++paramterIndex, new Date(vo.getLastProcessTime().getTime()));
		} else {
			statement.setObject(++paramterIndex,null);
		}
		statement.setObject(++paramterIndex, vo.getFlowId());
		statement.setObject(++paramterIndex, vo.getFormId());
		statement.setObject(++paramterIndex, vo.getDocId());
		statement.setObject(++paramterIndex, vo.getApplicationid());
		statement.setObject(++paramterIndex, vo.getDomainid());
		statement.setObject(++paramterIndex, vo.getVersion());

		return paramterIndex;
	}

	public void remove(String pk) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_FLOW_INTERVENTION")
				+ " WHERE ID = '" + pk + "'";
//		LOG.info(dbType + sql);
		Statement statement = connection.createStatement();
		try {
			statement.addBatch(sql);
			statement.executeBatch();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void update(ValueObject obj) throws Exception {
		FlowInterventionVO vo = (FlowInterventionVO) obj;
		String sql = "UPDATE "
				+ getFullTableName("T_FLOW_INTERVENTION")
				+ " SET ID=?,SUMMARY=?,FLOWNAME=?, STATELABEL=?, INITIATOR=?, LASTAUDITOR=?, FIRSTPROCESSTIME=?, LASTPROCESSTIME=? ,FLOWID=?, FORMID=?, DOCID=?, APPLICATIONID=?, DOMAINID=?, VERSION=? WHERE ID=?";
//		LOG.info(dbType + sql);
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);
			int parameterIndex = setParameters(statement, vo);
			statement.setString(++parameterIndex, vo.getId());
			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public ValueObject find(String id) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_FLOW_INTERVENTION")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		LOG.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				FlowInterventionVO vo = new FlowInterventionVO();
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

	public void setProperties(ResultSet rs, FlowInterventionVO vo)
			throws Exception {
		vo.setId(rs.getString("ID"));
		vo.setSummary(rs.getString("SUMMARY"));
		vo.setFlowName(rs.getString("FLOWNAME"));
		vo.setStateLabel(rs.getString("STATELABEL"));
		vo.setInitiator(rs.getString("INITIATOR"));
		vo.setLastAuditor(rs.getString("LASTAUDITOR"));
		vo.setFirstProcessTime(rs.getTimestamp("FIRSTPROCESSTIME"));
		vo.setLastProcessTime(rs.getTimestamp("LASTPROCESSTIME"));
		vo.setFlowId(rs.getString("FLOWID"));
		vo.setFormId(rs.getString("FORMID"));
		vo.setDocId(rs.getString("DOCID"));
		vo.setApplicationid(rs.getString("APPLICATIONID"));
		vo.setDomainid(rs.getString("DOMAINID"));
		vo.setVersion(rs.getInt("VERSION"));
	}

	public long countByFilter(ParamsTable params, WebUser user)
			throws Exception {
		return countBySQL(createFlowInterventionSQL(params, user), params.getParameterAsString("domain"));
	}

	public DataPackage<FlowInterventionVO> queryByFilter(ParamsTable params, WebUser user)
			throws Exception {
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");
		int currpage = _currpage != null && !"0".equals(_currpage) ? Integer.parseInt(_currpage) : 1;
		int pagelines = _pagelines != null && !"0".equals(_pagelines) ? Integer.parseInt(_pagelines) : 10;

		String _orderby = params.getParameterAsString("_orderby");

		StringBuffer sqlBuilder = new StringBuffer();
		String sql = createFlowInterventionSQL(params, user);

		sqlBuilder.append(sql);
		sqlBuilder.append(" ORDER BY ").append(
				StringUtil.isBlank(_orderby) ? "ID" : _orderby.toUpperCase());

		return queryBySQLPage(sqlBuilder.toString(), currpage, pagelines, params.getParameterAsString("domain"));
	}

	protected String createFlowInterventionSQL(ParamsTable params, WebUser user)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		String whereBlock = buildWhereBlock(params);
		
		sql.append("SELECT * FROM "+getFullTableName("T_FLOW_INTERVENTION")+" WHERE 1=1 ");
		sql.append(whereBlock);
		return sql.toString();
	}
	
	/**
	 * 拼接查询条件
	 * @param params
	 * @return
	 */
	public String buildWhereBlock(ParamsTable params) {
		StringBuffer whereBlock = new StringBuffer();
		
		String _flowName = params.getParameterAsString("_flowName");
		String _steteLabel = params.getParameterAsString("_stateLabel");
		String _inintator = params.getParameterAsString("_initiator");
		String _lastAuditor = params.getParameterAsString("_lastAuditor");
		String _firstProcessTime = params.getParameterAsString("_firstProcessTime");
		String _lastProcessTime = params.getParameterAsString("_lastProcessTime");
		String _summary = params.getParameterAsString("_summary");
		
		if(_flowName !=null && _flowName.trim().length()>0){
			whereBlock.append(" AND FLOWNAME ='"+_flowName+"' ");
		}
		if(_steteLabel !=null && _steteLabel.trim().length()>0){
			whereBlock.append(" AND STATELABEL ='"+_steteLabel+"' ");
		}
		if(_inintator !=null && _inintator.trim().length()>0){
			whereBlock.append(" AND INITIATOR ='"+_inintator+"' ");
		}
		if(_lastAuditor !=null && _lastAuditor.trim().length()>0){
			whereBlock.append(" AND LASTAUDITOR ='"+_lastAuditor+"' ");
		}
		if(_firstProcessTime !=null && _firstProcessTime.trim().length()>0){
			whereBlock.append(" AND FIRSTPROCESSTIME like '"+_firstProcessTime+"%' ");
		}
		if(_lastProcessTime !=null && _lastProcessTime.trim().length()>0){
			whereBlock.append(" AND LASTPROCESSTIME like '"+_lastProcessTime+"%' ");
		}
		if(_summary !=null && _summary.trim().length()>0){
			whereBlock.append(" AND SUMMARY like '%"+_summary+"%' ");
		}
		
		return whereBlock.toString();
	}

	public DataPackage<FlowInterventionVO> queryBySQL(String sql, String domainid) throws Exception {
		return queryBySQLPage(sql, 1, Integer.MAX_VALUE, domainid);
	}

	public DataPackage<FlowInterventionVO> queryBySQLPage(String sql, int page, int lines,
			String domainid) throws Exception {

		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");
		/**
		 * 生成分页SQL
		 */
		String limitSQL = buildLimitString(sql, page, lines);
//		LOG.info(dbType + limitSQL);
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			DataPackage<FlowInterventionVO> dpg = new DataPackage<FlowInterventionVO>();
			statement = connection.prepareStatement(limitSQL);
			rs = statement.executeQuery();
			Collection<FlowInterventionVO> datas = new ArrayList<FlowInterventionVO>();
			
			for (int i = 0; i < lines && rs.next(); i++) {
				FlowInterventionVO vo = new FlowInterventionVO();
				setProperties(rs, vo);
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
			PersistenceUtils.closeStatement(statement);
			PersistenceUtils.closeResultSet(rs);
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


	public void remove(ValueObject vo) throws Exception {
		remove(vo.getId());
	}
}
