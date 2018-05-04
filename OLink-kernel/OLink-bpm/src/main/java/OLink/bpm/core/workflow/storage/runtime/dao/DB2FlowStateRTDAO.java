package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import OLink.bpm.base.dao.ValueObject;

public class DB2FlowStateRTDAO extends AbstractFlowStateRTDAO implements
		FlowStateRTDAO {

	public DB2FlowStateRTDAO(Connection conn) throws Exception {
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

	public void create(ValueObject vo) throws Exception {
		create(vo, new DB2NodeRTDAO(connection));
	}

	public void remove(String id) throws Exception {

		remove(id, new DB2ActorRTDAO(connection), new DB2NodeRTDAO(connection));
	}

	public void update(ValueObject vo) throws Exception {

		update(vo, new DB2NodeRTDAO(connection));
	}

}
