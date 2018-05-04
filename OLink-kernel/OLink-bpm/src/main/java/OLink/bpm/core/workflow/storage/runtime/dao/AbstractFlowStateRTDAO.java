package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.runtime.ejb.FlowStateRT;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

/**
 * 
 * @author Chris
 * 
 */
public abstract class AbstractFlowStateRTDAO {
	Logger log = Logger.getLogger(AbstractFlowStateRTDAO.class);

	protected String dbTag = "Oracle: ";

	protected String schema = "";

	protected Connection connection;

	public AbstractFlowStateRTDAO(Connection conn) throws Exception {
		this.connection = conn;
	}

	public abstract void create(ValueObject vo) throws Exception;

	public void create(ValueObject vo, AbstractNodeRTDAO nodeRTDAO)
			throws Exception {
		FlowStateRT flowstatert = (FlowStateRT) vo;
		PreparedStatement statement = null;

		String sql = "INSERT INTO "
				+ getFullTableName("T_FLOWSTATERT")
				+ "(ID,FLOWID,DOCID,STATE,APPLICATIONID,PARENT,FLOWXML,FLOWNAME,LASTMODIFIERID,LASTMODIFIED,SUBFLOWNODEID,COMPLETE,CALLBACK,TOKEN,STATELABEL,INITIATOR,AUDITUSER,AUDITORNAMES,AUDITORLIST,LASTFLOWOPERATION,AUDITDATE,SUB_POSITION)";
		sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		log.info(dbTag + sql);
		try {
			if (flowstatert != null) {
				statement = connection.prepareStatement(sql);

				int index = 0;
				setValues(statement, ++index, flowstatert.getId());
				setValues(statement, ++index, flowstatert.getFlowid());
				setValues(statement, ++index, flowstatert.getDocid());
				setValues(statement, ++index,
						Integer.valueOf(flowstatert.getState()));
				setValues(statement, ++index, flowstatert.getApplicationid());
				if (flowstatert.getParent() != null) {
					setValues(statement, ++index, flowstatert.getParent()
							.getId());
				} else {
					setValues(statement, ++index, null);
				}
				// 流程定义字段
				setValues(statement, ++index, flowstatert.getFlowXML());
				setValues(statement, ++index, flowstatert.getFlowName());
				setValues(statement, ++index, flowstatert.getLastModifierId());
				setValues(statement, ++index, new Date(flowstatert.getLastModified().getTime()));
				setValues(statement, ++index, flowstatert.getSubFlowNodeId());
				if (flowstatert.isComplete()) {
					setValues(statement, ++index, 1);
				} else {
					setValues(statement, ++index, 0);
				}
				if (flowstatert.isCallback()) {
					setValues(statement, ++index, 1);
				} else {
					setValues(statement, ++index, 0);
				}
				setValues(statement, ++index, flowstatert.getToken());
				setValues(statement, ++index, flowstatert.getStateLabel());
				setValues(statement, ++index, flowstatert.getInitiator());
				setValues(statement, ++index, flowstatert.getAudituser());
				setValues(statement, ++index, flowstatert.getAuditorNames());
				setValues(statement, ++index, flowstatert.getAuditorList());
				setValues(statement, ++index, flowstatert.getLastFlowOperation());
				setValues(statement, ++index, new Date(flowstatert.getAuditdate().getTime()));
				setValues(statement, ++index, flowstatert.getPosition());
				statement.executeUpdate();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public ValueObject find(String id) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_FLOWSTATERT")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValues(statement, 1, id);
			rs = statement.executeQuery();

			if (rs.next()) {
				FlowStateRT vo = new FlowStateRT();
				setBaseProperties(vo, rs);

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

	public void remove(ValueObject obj) throws Exception {
		if (obj != null) {
			remove(obj.getId());
		}
	}

	public abstract void remove(String id) throws Exception;

	public void remove(String id, AbstractActorRTDAO actorRTDAO,
			AbstractNodeRTDAO nodeRTDAO) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_FLOWSTATERT")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValues(statement, 1, id);

			// 级联删除ActorRT
			// OracleActorRTDAO actorRTDAO = new OracleActorRTDAO(connection);
			// actorRTDAO.removeByForeignKey("FLOWSTATERT_ID", id);

			// 级联删除NodeRT
			// OracleNodeRTDAO nodeRTDAO = new OracleNodeRTDAO(connection);
			nodeRTDAO.removeByForeignKey("FLOWSTATERT_ID", id);

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public abstract void update(ValueObject vo) throws Exception;

	public void update(ValueObject vo, AbstractNodeRTDAO nodeRTDAO)
			throws Exception {
		FlowStateRT flowstatert = (FlowStateRT) vo;
		PreparedStatement statement = null;

		String sql = "UPDATE "
				+ getFullTableName("T_FLOWSTATERT")
				+ " SET ID=?,FLOWID=?,DOCID=?,STATE=?,APPLICATIONID=?,PARENT=?,FLOWXML=?,FLOWNAME=?,LASTMODIFIERID=?,LASTMODIFIED=?,SUBFLOWNODEID=?,COMPLETE=?,CALLBACK=?,TOKEN=?,STATELABEL=?,INITIATOR=?,AUDITUSER=?,AUDITORNAMES=?,AUDITORLIST=?,LASTFLOWOPERATION=?,AUDITDATE=?,SUB_POSITION=?";
		sql += " WHERE ID=?";

//		log.info(dbTag + sql);
		try {
			if (flowstatert != null) {
				statement = connection.prepareStatement(sql);
				int index = 0;
				setValues(statement, ++index, flowstatert.getId());
				setValues(statement, ++index, flowstatert.getFlowid());
				setValues(statement, ++index, flowstatert.getDocid());
				setValues(statement, ++index,
						Integer.valueOf(flowstatert.getState()));
				setValues(statement, ++index, flowstatert.getApplicationid());
				if (flowstatert.getParent() != null) {
					setValues(statement, ++index, flowstatert.getParent()
							.getId());
				} else {
					setValues(statement, ++index, null);
				}
				setValues(statement, ++index, flowstatert.getFlowXML());
				setValues(statement, ++index, flowstatert.getFlowName());
				setValues(statement, ++index, flowstatert.getLastModifierId());
				if(flowstatert.getLastModified() == null){//兼容旧数据
					flowstatert.setLastModified(new java.util.Date());
				}
				setValues(statement, ++index, new Date(flowstatert.getLastModified().getTime()));
				setValues(statement, ++index, flowstatert.getSubFlowNodeId());
				if (flowstatert.isComplete()) {
					setValues(statement, ++index, 1);
				} else {
					setValues(statement, ++index, 0);
				}
				if (flowstatert.isCallback()) {
					setValues(statement, ++index, 1);
				} else {
					setValues(statement, ++index, 0);
				}
				setValues(statement, ++index, flowstatert.getToken());
				setValues(statement, ++index, flowstatert.getStateLabel());
				setValues(statement, ++index, flowstatert.getInitiator());
				setValues(statement, ++index, flowstatert.getAudituser());
				setValues(statement, ++index, flowstatert.getAuditorNames());
				setValues(statement, ++index, flowstatert.getAuditorList());
				setValues(statement, ++index, flowstatert.getLastFlowOperation());
				setValues(statement, ++index, new Date(flowstatert.getAuditdate().getTime()));
				setValues(statement, ++index, flowstatert.getPosition());
				setValues(statement, ++index, flowstatert.getId());
				statement.executeUpdate();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 获取当前文档流程状态
	 * 
	 * @param docid
	 *            Document id
	 * @param flowid
	 *            文档流程 id
	 * @return 当前文档流程状态
	 * @throws Exception
	 */
	public FlowStateRT findFlowStateRTByDocidAndFlowid(String docid,
			String flowid) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM " + getFullTableName("T_FLOWSTATERT")
				+ " vo WHERE vo.DOCID=? AND vo.FLOWID=?";

//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, docid);
			statement.setString(2, flowid);

			rs = statement.executeQuery();
			if (rs.next()) {
				FlowStateRT vo = new FlowStateRT();
				setBaseProperties(vo, rs);

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

	private void setValues(PreparedStatement statement, int index, Object obj)
			throws SQLException {
		if (obj != null) {
			statement.setObject(index, obj);
		} else {
			statement.setNull(index, Types.NULL);
		}
	}

	private void setBaseProperties(FlowStateRT vo, ResultSet rs)
			throws Exception {
		vo.setId(rs.getString("ID"));
		vo.setFlowid(rs.getString("FLOWID"));
		vo.setDocid(rs.getString("DOCID"));
		vo.setState(rs.getInt("STATE"));
		if (!StringUtil.isBlank(rs.getString("PARENT"))) {
			FlowStateRT parent = (FlowStateRT) find(rs.getString("PARENT"));
			vo.setParent(parent);
		}
		vo.setLastModifierId(rs.getString("LASTMODIFIERID"));
		vo.setFlowName(rs.getString("FLOWNAME"));
		vo.setFlowXML(rs.getString("FLOWXML"));
		vo.setLastModified(rs.getDate("LASTMODIFIED"));
		vo.setApplicationid(rs.getString("APPLICATIONID"));
		vo.setSubFlowNodeId(rs.getString("SUBFLOWNODEID"));
		if (rs.getInt("COMPLETE") == 1) {
			vo.setComplete(true);
		} else {
			vo.setComplete(false);
		}
		if (rs.getInt("CALLBACK") == 1) {
			vo.setCallback(true);
		} else {
			vo.setCallback(false);
		}
		vo.setToken(rs.getString("TOKEN"));
		vo.setStateLabel(rs.getString("STATELABEL"));
		vo.setInitiator(rs.getString("INITIATOR"));
		vo.setAudituser(rs.getString("AUDITUSER"));
		vo.setAuditorNames(rs.getString("AUDITORNAMES"));
		vo.setAuditorList(rs.getString("AUDITORLIST"));
		vo.setLastFlowOperation(rs.getString("LASTFLOWOPERATION"));
		vo.setAuditdate(rs.getDate("AUDITDATE"));
		vo.setPosition(rs.getInt("SUB_POSITION"));
	}

	public String getFullTableName(String tblname) {
		if (this.schema != null && !this.schema.trim().equals("")) {
			return this.schema.trim().toUpperCase() + "."
					+ tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
	}

	/**
	 * 根据父状态ID获取子状态
	 * 
	 * @param parent
	 *            父状态ID
	 * @return
	 * @throws Exception
	 */
	public Collection<FlowStateRT> queryByParent(String parent)
			throws Exception {
		Collection<FlowStateRT> rtn = new ArrayList<FlowStateRT>();
		PreparedStatement statement = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM " + getFullTableName("T_FLOWSTATERT")
				+ " vo WHERE vo.PARENT=?";
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, parent);

			rs = statement.executeQuery();
			while (rs.next()) {
				FlowStateRT vo = new FlowStateRT();
				setBaseProperties(vo, rs);

				rtn.add(vo);
			}

			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
	}

	public FlowStateRT getCurrFlowStateRT(Document doc, WebUser user,
										  String currFlowStateId) throws Exception {
		String w = "";
		if (user != null) {
			w += " AND actor.ACTORID IN ('" + user.getId() + "') ";
		}
		if (!StringUtil.isBlank(currFlowStateId)) {
			w += " AND vo.ID='" + currFlowStateId + "' ";
		}
		String sql = "SELECT vo.ID,vo.FLOWID,vo.DOCID,vo.STATE,vo.APPLICATIONID,vo.PARENT,vo.FLOWXML,vo.FLOWNAME,vo.LASTMODIFIERID,vo.LASTMODIFIED,vo.SUBFLOWNODEID,vo.COMPLETE,vo.CALLBACK,vo.TOKEN,vo.STATELABEL,vo.INITIATOR,vo.AUDITUSER,vo.AUDITORNAMES,vo.AUDITORLIST,vo.LASTFLOWOPERATION,vo.AUDITDATE,vo.SUB_POSITION "
				+ "FROM "
				+ getFullTableName("T_FLOWSTATERT")
				+ " vo, "
				+ getFullTableName("T_NODERT")
				+ " node, "
				+ getFullTableName("T_ACTORRT")
				+ " actor "
				+ "WHERE vo.ID =node.FLOWSTATERT_ID AND  node.ID = actor.NODERT_ID "
				+ "AND vo.DOCID='" + doc.getId() + "' " + w;

		if (queryBySQL(sql).isEmpty()) {
			return null;
		}
		return (FlowStateRT) queryBySQL(sql).toArray()[0];
	}

	public Collection<FlowStateRT> queryBySQL(String sql) throws Exception {
		Collection<FlowStateRT> rtn = new ArrayList<FlowStateRT>();
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			while (rs.next()) {
				FlowStateRT vo = new FlowStateRT();
				setBaseProperties(vo, rs);
				rtn.add(vo);
			}
			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
	}

	public long countBySQL(String sql) throws Exception {
		if (StringUtil.isBlank(sql))
			return 0;
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);

		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();

			if (rs.next()) {
				return rs.getLong(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs)	;
			PersistenceUtils.closeStatement(statement);
		}

		return 0;
	}

	/**
	 * 根据文档ID获取相关流程实例集合
	 * 
	 * @param docId
	 * @return
	 * @throws Exception
	 */
	public Collection<FlowStateRT> getFlowStateRTsByDocId(String docId)
			throws Exception {
		Collection<FlowStateRT> rtn = new ArrayList<FlowStateRT>();
		PreparedStatement statement = null;
		ResultSet rs = null;

		String sql = "SELECT * FROM " + getFullTableName("T_FLOWSTATERT")
				+ " vo WHERE vo.DOCID=?";
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			statement.setString(1, docId);

			rs = statement.executeQuery();
			while (rs.next()) {
				FlowStateRT vo = new FlowStateRT();
				setBaseProperties(vo, rs);

				rtn.add(vo);
			}

			return rtn;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 根据文档ID删除关联的流程实例
	 * 
	 * @param docId
	 * @throws Exception
	 */
	public void doRemoveByDocId(String docId) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_FLOWSTATERT")
				+ " WHERE DOCID=?";
		PreparedStatement statement = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValues(statement, 1, docId);

			// 级联删除ActorRT
			// OracleActorRTDAO actorRTDAO = new OracleActorRTDAO(connection);
			// actorRTDAO.removeByForeignKey("FLOWSTATERT_ID", id);

			// 级联删除NodeRT
			// OracleNodeRTDAO nodeRTDAO = new OracleNodeRTDAO(connection);
//			nodeRTDAO.removeByForeignKey("FLOWSTATERT_ID", id);

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public boolean isMultiFlowState(Document doc, WebUser user)
			throws Exception {

		String w = "";
		if (user != null) {
			w += " AND actor.ACTORID IN ('" + user.getId() + "') ";
		}

		String sql = "SELECT COUNT(0) "
				+ "FROM "
				+ getFullTableName("T_FLOWSTATERT")
				+ " vo, "
				+ getFullTableName("T_NODERT")
				+ " node, "
				+ getFullTableName("T_ACTORRT")
				+ " actor "
				+ "WHERE vo.ID =node.FLOWSTATERT_ID AND  node.ID = actor.NODERT_ID "
				+ "AND vo.DOCID='" + doc.getId() + "' AND vo.COMPLETE=0 " + w;

		return countBySQL(sql) > 1;
	}

	public boolean isMultiFlowState(Document doc) throws Exception {

		String sql = "SELECT COUNT(0) " + "FROM "
				+ getFullTableName("T_FLOWSTATERT") + " vo, "
				+ getFullTableName("T_NODERT") + " node "
				+ "WHERE vo.ID =node.FLOWSTATERT_ID " + "AND vo.DOCID='"
				+ doc.getId() + "' AND vo.COMPLETE=0 ";

		return countBySQL(sql) > 0;
	}

	public boolean isAllSubFlowStateRTComplete(FlowStateRT subFlowInstance)
			throws Exception {

		String sql = "SELECT COUNT(0) " + "FROM "
				+ getFullTableName("T_FLOWSTATERT")
				+ " vo WHERE vo.SUBFLOWNODEID='"
				+ subFlowInstance.getSubFlowNodeId() + "' AND vo.COMPLETE=0 "
				+
				 "AND vo.TOKEN='"+ subFlowInstance.getToken() + "' " +
				"AND vo.PARENT='" + subFlowInstance.getParent().getId() + "' "
				+ "AND vo.ID !='" + subFlowInstance.getId() + "'";

		return countBySQL(sql) == 0;
	}
	
}
