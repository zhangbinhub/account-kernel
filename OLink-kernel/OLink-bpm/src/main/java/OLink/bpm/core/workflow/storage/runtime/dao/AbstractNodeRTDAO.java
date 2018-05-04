package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;
import org.apache.log4j.Logger;

/**
 * 
 * @author Chris
 * 
 */
public abstract class AbstractNodeRTDAO {
	Logger log = Logger.getLogger(AbstractNodeRTDAO.class);

	protected String dbTag = "Oracle: ";

	protected String schema = null;

	protected Connection connection;

	public AbstractNodeRTDAO(Connection conn) throws Exception {
		this.connection = conn;
	}

	public abstract void create(ValueObject vo) throws Exception;

	public void create(ValueObject vo, ActorRTDAO actorRTDAO) throws Exception {
		NodeRT nodert = (NodeRT) vo;
		PreparedStatement statement = null;

		String sql = "INSERT INTO "
				+ getFullTableName("T_NODERT")
				+ "(ID,NAME,FLOWID,DOCID,FLOWOPTION,STATELABEL,NODEID,FLOWSTATERT_ID, NOTIFIABLE, PASSCONDITION, PARENTNODERTID, SPLITTOKEN, DOMAINID, APPLICATIONID)";
		sql += " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//		log.info(dbTag + sql);
		try {
			if (nodert != null) {

				if (!nodert.getActorrts().isEmpty()) { // 级联create ActorRT
					for (Iterator<ActorRT> iter = nodert.getActorrts()
							.iterator(); iter.hasNext();) {
						ActorRT element = iter.next();
						element.setNodertid(nodert.getId());
						actorRTDAO.create(element);
					}
				}
				statement = connection.prepareStatement(sql);
				setValues(statement, nodert);
				statement.executeUpdate();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public ValueObject find(String id) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_NODERT")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValue(statement, 1, id);
			rs = statement.executeQuery();
			if (rs.next()) {
				NodeRT vo = new NodeRT();
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

	public NodeRT getData(String sql) throws Exception {
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			if (rs.next()) {
				NodeRT vo = new NodeRT();
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

	public Collection<NodeRT> getDatas(String sql) throws Exception {
		Collection<NodeRT> rtn = new ArrayList<NodeRT>();
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			statement = connection.prepareStatement(sql);
			rs = statement.executeQuery();
			while (rs.next()) {
				NodeRT vo = new NodeRT();
				setBaseProperties(vo, rs);

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

	public void remove(ValueObject obj) throws Exception {
		if (obj != null) {
			remove(obj.getId());
		}
	}

	public abstract void remove(String id) throws Exception;

	public void remove(String id, AbstractActorRTDAO actorRTDAO)
			throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_NODERT")
				+ " WHERE ID=?";
		PreparedStatement statement = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValue(statement, 1, id);
			// 级联删除ActorRT
			actorRTDAO.removeByForeignKey("NODERT_ID", id);

			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public void removeByForeignKey(String key, Object val) throws Exception {
		String sql = "DELETE FROM " + getFullTableName("T_NODERT") + " WHERE "
				+ key.toUpperCase() + "=?";
		PreparedStatement statement = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValue(statement, 1, val);
			statement.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	public abstract void update(ValueObject vo) throws Exception;

	public void update(ValueObject vo, AbstractActorRTDAO actorRTDAO)
			throws Exception {
		NodeRT nodert = (NodeRT) vo;
		PreparedStatement statement = null;

		String sql = "UPDATE "
				+ getFullTableName("T_NODERT")
				+ " SET ID=?,NAME=?,FLOWID=?,DOCID=?,FLOWOPTION=?,STATELABEL=?,NODEID=?,FLOWSTATERT_ID=?, NOTIFIABLE=?, PASSCONDITION=?, PARENTNODERTID=?, SPLITTOKEN=?, DOMAINID=?, APPLICATIONID=?";
		sql += " WHERE ID=?";
//		log.info(dbTag + sql);
		try {
			if (nodert != null) {
				statement = connection.prepareStatement(sql);
				int index = setValues(statement, nodert);
				setValue(statement, ++index, nodert.getId());

				actorRTDAO.removeByForeignKey("NODERT_ID", vo.getId());
				// 级联update ActorRT
				if (!nodert.getActorrts().isEmpty()) {
					for (Iterator<ActorRT> iter = nodert.getActorrts()
							.iterator(); iter.hasNext();) {
						ActorRT element = iter.next();
						element.setNodertid(nodert.getId());
						actorRTDAO.create(element);
					}
				}
				statement.executeUpdate();
			}
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeStatement(statement);
		}
	}

	/**
	 * 根据文档，文档相应流程查询，获取文档的所有运行时节点
	 * 
	 * @param docid
	 *            document id
	 * @param flowid
	 *            流程实例 id
	 * @return 文档的所有运行时节点
	 * @throws Exception
	 */
	public Collection<NodeRT> query(String docid, String flowStateId)
			throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_NODERT")
				+ " vo WHERE vo.DOCID='" + docid + "' AND vo.FLOWSTATERT_ID='" + flowStateId
				+ "'";

		return this.getDatas(sql);
	}

	/**
	 * 根据外键(FLOWSTATERT_ID)级联查找NodeRT
	 * 
	 * @param key
	 *            外键
	 * @param val
	 *            外键值
	 * @return NodeRT Collection
	 * @throws Exception
	 */
	public Collection<NodeRT> queryByForeignKey(String key, Object val)
			throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_NODERT")
				+ " WHERE " + key.toUpperCase() + "=?";
		PreparedStatement statement = null;
		ResultSet rs = null;
//		log.info(dbTag + sql);
		try {
			statement = connection.prepareStatement(sql);
			setValue(statement, 1, val);
			rs = statement.executeQuery();
			Set<NodeRT> rtn = new HashSet<NodeRT>();

			while (rs.next()) {
				NodeRT vo = new NodeRT();
				setBaseProperties(vo, rs);
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

	public Collection<NodeRT> queryNodeRTByDocidAndFlowid(String docid,
			String flowid) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_NODERT")
				+ " vo WHERE vo.DOCID='" + docid + "' AND vo.FLOWID='" + flowid
				+ "'";

		return this.getDatas(sql);
	}
	
	public Collection<NodeRT> queryNodeRTByFlowStateIdAndDocId(String instanceId, String docId) throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_NODERT")
		+ " vo WHERE vo.DOCID='" + docId + "' AND vo.FLOWSTATERT_ID='" + instanceId
		+ "'";
		
		return this.getDatas(sql);
	}

	public NodeRT findByNodeid(String docid, String flowStateId, String nodeid)
			throws Exception {
		String sql = "SELECT * FROM " + getFullTableName("T_NODERT") + " vo";
		sql += " WHERE vo.DOCID='" + docid + "'";
		sql += " AND vo.NODEID='" + nodeid + "'";
		sql += " AND vo.FLOWSTATERT_ID='" + flowStateId + "'";

		return this.getData(sql);
	}

	/**
	 * 设置参数值
	 * 
	 * @param statement
	 *            PreparedStatement
	 * @param index
	 *            索引
	 * @param obj
	 *            参数值
	 * @throws SQLException
	 */
	private void setValue(PreparedStatement statement, int index, Object obj)
			throws SQLException {
		if (obj != null) {
			statement.setObject(index, obj);
		} else {
			statement.setNull(index, Types.NULL);
		}
	}

	private void setBaseProperties(NodeRT vo, ResultSet rs) throws SQLException {
		vo.setId(rs.getString("ID"));
		vo.setName(rs.getString("NAME"));
		vo.setNodeid(rs.getString("NODEID"));
		vo.setFlowid(rs.getString("FLOWID"));
		vo.setDocid(rs.getString("DOCID"));
		vo.setFlowoption(rs.getString("FLOWOPTION"));
		vo.setStatelabel(rs.getString("STATELABEL"));
		vo.setDomainid(rs.getString("DOMAINID"));
		vo.setNotifiable(rs.getInt("NOTIFIABLE") == 1 ? true : false);
		vo.setPassCondition(rs.getInt("PASSCONDITION"));
		vo.setParentNodertid(rs.getString("PARENTNODERTID"));
		vo.setSplitToken(rs.getString("SPLITTOKEN"));
		vo.setFlowstatertid(rs.getString("FLOWSTATERT_ID"));
		vo.setApplicationid(rs.getString("APPLICATIONID"));
	}

	private int setValues(PreparedStatement statement, NodeRT nodert)
			throws SQLException {
		int i = 0;
		setValue(statement, ++i, nodert.getId());
		setValue(statement, ++i, nodert.getName());
		setValue(statement, ++i, nodert.getFlowid());
		setValue(statement, ++i, nodert.getDocid());
		setValue(statement, ++i, nodert.getFlowoption());
		setValue(statement, ++i, nodert.getStatelabel());
		setValue(statement, ++i, nodert.getNodeid());
		setValue(statement, ++i, nodert.getFlowstatertid());
		setValue(statement, ++i, nodert.isNotifiable() ? Integer.valueOf(0)
				: Integer.valueOf(1));
		setValue(statement, ++i, Integer.valueOf(nodert.getPassCondition()));
		setValue(statement, ++i, nodert.getParentNodertid());
		setValue(statement, ++i, nodert.getSplitToken());
		setValue(statement, ++i, nodert.getDomainid());
		setValue(statement, ++i, nodert.getApplicationid());

		return i;
	}

	public String getFullTableName(String tblname) {
		if (this.schema != null && !this.schema.trim().equals("")) {
			return this.schema.trim().toUpperCase() + "."
					+ tblname.trim().toUpperCase();
		}
		return tblname.trim().toUpperCase();
	}
}
