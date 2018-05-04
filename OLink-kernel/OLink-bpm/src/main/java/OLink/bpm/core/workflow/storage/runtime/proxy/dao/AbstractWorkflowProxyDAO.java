package OLink.bpm.core.workflow.storage.runtime.proxy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.workflow.storage.runtime.proxy.ejb.WorkflowProxyVO;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.user.action.WebUser;

/**
 * @author Happy
 *
 */
public abstract class AbstractWorkflowProxyDAO {

	Logger LOG = Logger.getLogger(AbstractWorkflowProxyDAO.class);

	protected Connection connection;

	protected String schema;

	protected String dbType = "Oracle: ";


	public AbstractWorkflowProxyDAO(Connection connection) {
		this.connection = connection;
	}

	public void create(ValueObject obj) throws Exception {
		WorkflowProxyVO vo = (WorkflowProxyVO) obj;
		PreparedStatement statement = null;

		String sql = "INSERT INTO "
				+ getFullTableName("T_FLOW_PROXY")
				+ "(ID,FLOWNAME,FLOWID, DESCRIPTION, STATE, AGENTS,AGENTSNAME, OWNER, APPLICATIONID, DOMAINID, VERSION)";
		sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?)";
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

	private int setParameters(PreparedStatement statement, WorkflowProxyVO vo)
			throws SQLException {
		int paramterIndex = 0;
		statement.setObject(++paramterIndex, vo.getId());
		statement.setObject(++paramterIndex, vo.getFlowName());
		statement.setObject(++paramterIndex, vo.getFlowId());
		statement.setObject(++paramterIndex, vo.getDescription());
		statement.setObject(++paramterIndex, vo.getState());
		statement.setObject(++paramterIndex, vo.getAgents());
		statement.setObject(++paramterIndex, vo.getAgentsName());
		statement.setObject(++paramterIndex, vo.getOwner());
		statement.setObject(++paramterIndex, vo.getApplicationid());
		statement.setObject(++paramterIndex, vo.getDomainid());
		statement.setObject(++paramterIndex, vo.getVersion());
		return paramterIndex;
	}

	public void remove(String pk) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_FLOW_PROXY")
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
		WorkflowProxyVO vo = (WorkflowProxyVO) obj;
		String sql = "UPDATE "
				+ getFullTableName("T_FLOW_PROXY")
				+ " SET ID=?,FLOWNAME=?,FLOWID=?, DESCRIPTION=?, STATE=?, AGENTS=?,AGENTSNAME=?, OWNER=?, APPLICATIONID=?, DOMAINID=?, VERSION=? WHERE ID=?";
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
		String sql = "SELECT * FROM " + getFullTableName("T_FLOW_PROXY")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		LOG.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				WorkflowProxyVO vo = new WorkflowProxyVO();
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

	public void setProperties(ResultSet rs, WorkflowProxyVO vo) throws Exception {
		vo.setId(rs.getString("ID"));
		vo.setFlowName(rs.getString("FLOWNAME"));
		vo.setFlowId(rs.getString("FLOWID"));
		vo.setDescription(rs.getString("DESCRIPTION"));
		vo.setAgents(rs.getString("AGENTS"));
		vo.setAgentsName(rs.getString("AGENTSNAME"));
		vo.setState(rs.getString("STATE"));
		vo.setOwner(rs.getString("OWNER"));
		vo.setApplicationid(rs.getString("APPLICATIONID"));
		vo.setDomainid(rs.getString("DOMAINID"));
		vo.setVersion(rs.getInt("VERSION"));
	}
	
	
	
	/**
	 * 根据用户获取用户的流程代理信息
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public DataPackage<WorkflowProxyVO> queryByAgent(WebUser user, String applicationid) throws Exception {
		
		String sql = "SELECT * FROM "+getFullTableName("T_FLOW_PROXY")+" WHERE AGENTS like '%"+user.getId()+"%' AND APPLICATIONID='"+applicationid+"' AND DOMAINID ='"+user.getDomainid()+"' AND STATE ='1'";
		
		return queryBySQL(sql, user.getDomainid());
	}
	
	
	

	public long countByFilter(ParamsTable params, WebUser user) throws Exception {
		return countBySQL(createWorkflowProxySQL(params, user), user.getDomainid());
	}

	public DataPackage<WorkflowProxyVO> queryByFilter(ParamsTable params, WebUser user) throws Exception {
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");
		int currpage = _currpage != null && !"0".equals(_currpage) ? Integer.parseInt(_currpage) : 1;
		int pagelines = _pagelines != null && !"0".equals(_pagelines) ? Integer.parseInt(_pagelines) : 10;
		
		String _orderby = params.getParameterAsString("_orderby");

		StringBuffer sqlBuilder = new StringBuffer();
		String sql = createWorkflowProxySQL(params, user);

		sqlBuilder.append(sql);
		sqlBuilder.append(" ORDER BY ").append(
				StringUtil.isBlank(_orderby) ? "ID" : _orderby.toUpperCase());

		return queryBySQLPage(sqlBuilder.toString(), currpage, pagelines,user.getDomainid());
	}

	protected String createWorkflowProxySQL(ParamsTable params, WebUser user)
			throws Exception {
		StringBuffer sql = new StringBuffer();
		String whereBlock = buildWhereBlock(params);
		
		sql.append("SELECT * FROM "+getFullTableName("T_FLOW_PROXY")+" WHERE 1=1 ");
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
		
		if(_flowName !=null && _flowName.trim().length()>0){
			whereBlock.append(" AND FLOWNAME like'%"+_flowName+"%' ");
		}
		
		return whereBlock.toString();
	}

	public DataPackage<WorkflowProxyVO> queryBySQL(String sql, String domainid) throws Exception {
		return queryBySQLPage(sql, 1, Integer.MAX_VALUE, domainid);
	}

	public DataPackage<WorkflowProxyVO> queryBySQLPage(String sql, int page, int lines,
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
			DataPackage<WorkflowProxyVO> dpg = new DataPackage<WorkflowProxyVO>();
			statement = connection.prepareStatement(limitSQL);
			rs = statement.executeQuery();
			Collection<WorkflowProxyVO> datas = new ArrayList<WorkflowProxyVO>();
			
			for (int i = 0; i < lines && rs.next(); i++) {
				WorkflowProxyVO vo = new WorkflowProxyVO();
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

		LOG.debug(dbType + countSQL);
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
	
	
	public long countByFlowAndOwner(WorkflowProxyVO vo) throws Exception{
		String sql ="SELECT COUNT(*) FROM "+getFullTableName("T_FLOW_PROXY")+" WHERE FLOWID='"+vo.getFlowId()+"' AND OWNER='"+vo.getOwner()+"'";
		PreparedStatement statement = null;
		ResultSet rs = null;

//		LOG.info(dbType + sql);
		try {
			statement = connection.prepareStatement(sql);
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
	
	public DataPackage<WorkflowProxyVO> queryByOwners(Collection<BaseUser> owners) throws Exception{
		StringBuffer w =new StringBuffer();
		String domainid =null;
		for(BaseUser user : owners){
			w.append("'").append(user.getId()).append("',");
			if(domainid ==null) domainid = user.getDomainid();
		}
		if(w.toString().endsWith(",")){
			w.setLength(w.length()-1);
		}
		StringBuffer sql = new StringBuffer("SELECT * FROM "+getFullTableName("T_FLOW_PROXY")+" WHERE STATE='1' AND OWNER in (").append(w).append(")");
		
		return queryBySQL(sql.toString(),domainid);
		
	}
}
