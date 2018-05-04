package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * 
 * @author Nicholas
 * 
 */
public class OracleRelationHISDAO extends AbstractRelationHISDAO implements
		RelationHISDAO {
	Logger log = Logger.getLogger(OracleRelationHISDAO.class);

	public OracleRelationHISDAO(Connection conn) throws Exception {
		super(conn);
		if (conn != null) {
			try {
				this.schema = conn.getMetaData().getUserName().trim()
						.toUpperCase();
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	// private Connection connection;
	//
	// public OracleRelationHISDAO(Connection conn) throws Exception {
	// this.connection = conn;
	// }
	//
	// /**
	// * 获取一条历史记录
	// *
	// * @param docid
	// * Document id
	// * @param flowid
	// * 流程 id
	// * @return 一条历史记录
	// * @throws Exception
	// */
	// public RelationHIS find(String docid, String flowid) throws Exception {
	// String hql = "SELECT * FROM T_RELATIONHIS vo WHERE " + "vo.docid='"
	// + docid + "' AND vo.flowid='" + flowid + "'";
	//
	// Object data = this.getData(hql);
	// if (data != null && data instanceof RelationHIS) {
	// return (RelationHIS) data;
	// }
	// return null;
	// }
	//
	// public RelationHIS findRelHISByCondition(String docid, String
	// startnodeid,
	// String endnodeid, boolean ispassed) throws Exception {
	// String passed = null;
	// if (ispassed) {
	// passed = "1";
	// } else {
	// passed = "0";
	// }
	//
	// String hql = "SELECT * FROM T_RELATIONHIS vo WHERE " + "vo.docid='"
	// + docid + "' AND vo.startnodeid='" + startnodeid
	// + "' AND vo.endnodeid='" + endnodeid + "' AND vo.ispassed="
	// + passed;
	//
	// Object data = this.getData(hql);
	// if (data != null && data instanceof RelationHIS) {
	// return (RelationHIS) data;
	// }
	// return null;
	// }
	//
	// public Collection query(String docid, String flowid) throws Exception {
	// String sql = "SELECT * FROM T_RELATIONHIS vo WHERE " + "vo.docid='"
	// + docid + "' AND vo.flowid='" + flowid + "'"
	// + " ORDER BY vo.actiontime";
	// return this.getDatas(sql);
	// }
	//
	// public Collection queryRelationHIS(String docid, String flowid,
	// String endnodeid) throws Exception {
	// String sql = "SELECT * FROM T_RELATIONHIS vo WHERE " + "vo.docid='"
	// + docid + "' AND vo.flowid='" + flowid + "' AND vo.endnodeid='"
	// + endnodeid + "' ORDER BY vo.actiontime DESC";
	//
	// return this.getDatas(sql);
	// }
	//
	// public Collection queryStartNodeHis(String docid, String flowid,
	// String endnodeid) throws Exception {
	// String sql = "SELECT DISTINCT vo.startnodeid FROM T_RELATIONHIS"
	// + " vo WHERE " + "vo.docid='" + docid + "' AND vo.flowid='"
	// + flowid + "' AND vo.endnodeid='" + endnodeid
	// + "' ORDER BY vo.actiontime DESC";
	//
	// PreparedStatement statement = null;
	// try {
	// statement = connection.prepareStatement(sql);
	//
	// ResultSet rs = statement.executeQuery();
	// Collection rtn = new ArrayList();
	//
	// while (rs.next()) {
	// rtn.add(rs.getString("STARTNODEID"));
	// }
	// return rtn;
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// PersistenceUtils.closeStatement(statement);
	// }
	// }
	//
	// public void create(ValueObject vo) throws Exception {
	// RelationHIS relhis = (RelationHIS) vo;
	// PreparedStatement statement = null;
	//
	// String sql =
	// "INSERT INTO T_RELATIONHIS(ID,ACTIONTIME,FLOWID,DOCID,ATTITUDE,ENDNODEID,ENDNODENAME,STARTNODEID,STARTNODENAME,ISPASSED)";
	// sql += " VALUES(?,?,?,?,?,?,?,?,?,?)";
	// log.info("Oracle: " + sql);
	// try {
	// if (relhis != null) {
	// statement = connection.prepareStatement(sql);
	//
	// Timestamp actiontime = null;
	// if (relhis.getActiontime() != null) {
	// actiontime = new Timestamp(relhis.getActiontime().getTime());
	// }
	//
	// setValues(statement, 1, relhis.getId());
	// setValues(statement, 2, actiontime);
	// setValues(statement, 3, relhis.getFlowid());
	// setValues(statement, 4, relhis.getDocid());
	// setValues(statement, 5, relhis.getAttitude());
	// setValues(statement, 6, relhis.getEndnodeid());
	// setValues(statement, 7, relhis.getEndnodename());
	// setValues(statement, 8, relhis.getStartnodeid());
	// setValues(statement, 9, relhis.getStartnodename());
	// setValues(statement, 10, Integer
	// .valueOf(relhis.getIspassed() ? 1 : 0));
	// // setValues(statement, 11, relhis.getSortId());
	// statement.executeUpdate();
	//
	// // 级联Create ActorHIS
	// if (!relhis.getActorhiss().isEmpty()) {
	// String sql2 = "INSERT INTO T_ACTORHIS(ID,NAME,ACTORID,TYPE,NODEHIS_ID)";
	// sql2 += " VALUES(?,?,?,?,?)";
	// log.info("Oracle: " + sql2);
	// statement = connection.prepareStatement(sql2);
	// for (Iterator iter = relhis.getActorhiss().iterator(); iter
	// .hasNext();) {
	// ActorHIS acthis = (ActorHIS) iter.next();
	//
	// setValues(statement, 1, acthis.getId());
	// setValues(statement, 2, acthis.getName());
	// setValues(statement, 3, acthis.getActorid());
	// setValues(statement, 4, Integer.valueOf(acthis
	// .getType()));
	// setValues(statement, 5, relhis.getId());
	// statement.addBatch();
	// }
	// statement.executeBatch();
	// }
	// }
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// PersistenceUtils.closeStatement(statement);
	// }
	// }
	//
	// public ValueObject find(String id) throws Exception {
	// String sql = "SELECT * FROM T_RELATIONHIS WHERE ID=?";
	// PreparedStatement statement = null;
	// log.info("Oracle: " + sql);
	// try {
	// statement = connection.prepareStatement(sql);
	// setValues(statement, 1, id);
	//
	// ResultSet rs = statement.executeQuery();
	// if (rs.next()) {
	// RelationHIS vo = new RelationHIS();
	// vo.setId(rs.getString("ID"));
	// vo.setActiontime(rs.getTimestamp("ACTIONTIME"));
	// vo.setAttitude(rs.getString("ATTITUDE"));
	// vo.setFlowid(rs.getString("FLOWID"));
	// vo.setDocid(rs.getString("DOCID"));
	// vo.setEndnodeid(rs.getString("ENDNODEID"));
	// vo.setEndnodename(rs.getString("ENDNODENAME"));
	// vo.setStartnodeid(rs.getString("STARTNODEID"));
	// vo.setStartnodename(rs.getString("STARTNODENAME"));
	// vo.setIspassed(rs.getInt("ISPASSED") == 1 ? true : false);
	//
	// // 级联Query ActorHIS
	// String sql2 = "SELECT * FROM T_ACTORHIS WHERE NODEHIS_ID=?";
	// log.info("Oracle: " + sql2);
	// statement = connection.prepareStatement(sql2);
	// setValues(statement, 1, id);
	// ResultSet rs2 = statement.executeQuery();
	// while (rs2.next()) {
	// ActorHIS acthis = new ActorHIS();
	// acthis.setId(rs2.getString("ID"));
	// acthis.setActorid(rs2.getString("ACTORID"));
	// acthis.setName(rs2.getString("NAME"));
	// acthis.setType(rs2.getInt("TYPE"));
	// vo.getActorhiss().add(acthis);
	// }
	// return vo;
	// }
	// return null;
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// PersistenceUtils.closeStatement(statement);
	// }
	// }
	//
	// public Object getData(String sql) throws Exception {
	// PreparedStatement statement = null;
	// log.info("Oracle: " + sql);
	// try {
	// statement = connection.prepareStatement(sql);
	//
	// ResultSet rs = statement.executeQuery();
	// if (rs.next()) {
	// RelationHIS vo = new RelationHIS();
	// vo.setId(rs.getString("ID"));
	// vo.setActiontime(rs.getTimestamp("ACTIONTIME"));
	// vo.setAttitude(rs.getString("ATTITUDE"));
	// vo.setFlowid(rs.getString("FLOWID"));
	// vo.setDocid(rs.getString("DOCID"));
	// vo.setEndnodeid(rs.getString("ENDNODEID"));
	// vo.setEndnodename(rs.getString("ENDNODENAME"));
	// vo.setStartnodeid(rs.getString("STARTNODEID"));
	// vo.setStartnodename(rs.getString("STARTNODENAME"));
	// vo.setIspassed(rs.getInt("ISPASSED") == 1 ? true : false);
	//
	// // 级联Query ActorHIS
	// String sql2 = "SELECT * FROM T_ACTORHIS WHERE NODEHIS_ID=?";
	// log.info("Oracle: " + sql2);
	// statement = connection.prepareStatement(sql2);
	// setValues(statement, 1, vo.getId());
	// ResultSet rs2 = statement.executeQuery();
	// while (rs2.next()) {
	// ActorHIS acthis = new ActorHIS();
	// acthis.setId(rs2.getString("ID"));
	// acthis.setActorid(rs2.getString("ACTORID"));
	// acthis.setName(rs2.getString("NAME"));
	// acthis.setType(rs2.getInt("TYPE"));
	// vo.getActorhiss().add(acthis);
	// }
	// return vo;
	// }
	// return null;
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// PersistenceUtils.closeStatement(statement);
	// }
	// }
	//
	// public Collection getDatas(String sql) throws Exception {
	// PreparedStatement statement = null;
	// log.info("Oracle: " + sql);
	// try {
	// statement = connection.prepareStatement(sql);
	//
	// ResultSet rs = statement.executeQuery();
	// Collection rtn = new ArrayList();
	//
	// while (rs.next()) {
	// RelationHIS vo = new RelationHIS();
	// vo.setId(rs.getString("ID"));
	// vo.setActiontime(rs.getTimestamp("ACTIONTIME"));
	// vo.setAttitude(rs.getString("ATTITUDE"));
	// vo.setFlowid(rs.getString("FLOWID"));
	// vo.setDocid(rs.getString("DOCID"));
	// vo.setEndnodeid(rs.getString("ENDNODEID"));
	// vo.setEndnodename(rs.getString("ENDNODENAME"));
	// vo.setStartnodeid(rs.getString("STARTNODEID"));
	// vo.setStartnodename(rs.getString("STARTNODENAME"));
	// vo.setIspassed(rs.getInt("ISPASSED") == 1 ? true : false);
	//
	// // 级联Query ActorHIS
	// String sql2 = "SELECT * FROM T_ACTORHIS WHERE NODEHIS_ID=?";
	// log.info("Oracle: " + sql2);
	// statement = connection.prepareStatement(sql2);
	// setValues(statement, 1, vo.getId());
	// ResultSet rs2 = statement.executeQuery();
	// while (rs2.next()) {
	// ActorHIS acthis = new ActorHIS();
	// acthis.setId(rs2.getString("ID"));
	// acthis.setActorid(rs2.getString("ACTORID"));
	// acthis.setName(rs2.getString("NAME"));
	// acthis.setType(rs2.getInt("TYPE"));
	// vo.getActorhiss().add(acthis);
	// }
	//
	// rtn.add(vo);
	// }
	// return rtn;
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// PersistenceUtils.closeStatement(statement);
	// }
	// }
	//
	// public void remove(ValueObject obj) throws Exception {
	// if (obj != null) {
	// remove(obj.getId());
	// }
	// }
	//
	// public void remove(String id) throws Exception {
	// String sql = "DELETE T_RELATIONHIS WHERE ID=?";
	// PreparedStatement statement = null;
	// log.info("Oracle: " + sql);
	// try {
	// statement = connection.prepareStatement(sql);
	// setValues(statement, 1, id);
	//
	// // 级联Remove ActorHIS
	// String sql2 = "DELETE T_ACTORHIS WHERE NODEHIS_ID=?";
	// log.info("Oracle: " + sql2);
	// PreparedStatement statement2 = connection.prepareStatement(sql2);
	// setValues(statement2, 1, id);
	//
	// statement2.executeUpdate();
	//
	// statement.executeUpdate();
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// PersistenceUtils.closeStatement(statement);
	// }
	// }
	//
	// public void update(ValueObject vo) throws Exception {
	// RelationHIS relhis = (RelationHIS) vo;
	// PreparedStatement statement = null;
	//
	// String sql =
	// "UPDATE T_RELATIONHIS SET ID=?,ACTIONTIME=?,FLOWID=?,DOCID=?,ATTITUDE=?,ENDNODEID=?,ENDNODENAME=?,STARTNODEID=?,STARTNODENAME=?,ISPASSED=?";
	// sql += " WHERE ID=?";
	// log.info("Oracle: " + sql);
	// try {
	// if (relhis != null) {
	// statement = connection.prepareStatement(sql);
	//
	// Timestamp actiontime = null;
	// if (relhis.getActiontime() != null) {
	// actiontime = new Timestamp(relhis.getActiontime().getTime());
	// }
	//
	// setValues(statement, 1, relhis.getId());
	// setValues(statement, 2, actiontime);
	// setValues(statement, 3, relhis.getFlowid());
	// setValues(statement, 4, relhis.getDocid());
	// setValues(statement, 5, relhis.getAttitude());
	// setValues(statement, 6, relhis.getEndnodeid());
	// setValues(statement, 7, relhis.getEndnodename());
	// setValues(statement, 8, relhis.getStartnodeid());
	// setValues(statement, 9, relhis.getStartnodename());
	// setValues(statement, 10, Integer
	// .valueOf(relhis.getIspassed() ? 1 : 0));
	// setValues(statement, 11, relhis.getId());
	//
	// statement.executeUpdate();
	//
	// // 级联update ActorHIS
	// String sql2 =
	// "UPDATE T_ACTORHIS SET ID=?,NAME=?,ACTORID=?,TYPE=?,NODEHIS_ID=?";
	// sql2 += " WHERE ID=?";
	// log.info("Oracle: " + sql2);
	// statement = connection.prepareStatement(sql2);
	// if (!relhis.getActorhiss().isEmpty()) {
	// for (Iterator iter = relhis.getActorhiss().iterator(); iter
	// .hasNext();) {
	// ActorHIS actorhis = (ActorHIS) iter.next();
	// setValues(statement, 1, actorhis.getId());
	// setValues(statement, 2, actorhis.getName());
	// setValues(statement, 3, actorhis.getActorid());
	// setValues(statement, 4, Integer.valueOf(actorhis
	// .getType()));
	// setValues(statement, 5, relhis.getId());
	// setValues(statement, 6, actorhis.getId());
	// statement.addBatch();
	// }
	// statement.executeBatch();
	// }
	// }
	// } catch (Exception e) {
	// throw e;
	// } finally {
	// PersistenceUtils.closeStatement(statement);
	// }
	// }
	//
	// private void setValues(PreparedStatement statement, int index, Object
	// obj)
	// throws SQLException {
	// if (obj != null) {
	// statement.setObject(index, obj);
	// } else {
	// statement.setNull(index, Types.NULL);
	// }
	// }

}
