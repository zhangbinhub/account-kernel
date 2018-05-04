package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import OLink.bpm.base.dao.ValueObject;

public class DB2NodeRTDAO extends AbstractNodeRTDAO implements NodeRTDAO {

	public DB2NodeRTDAO(Connection conn) throws Exception {
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
		create(vo, new DB2ActorRTDAO(connection));

	}

	public void remove(ValueObject obj) throws Exception {
		if (obj != null) {
			remove(obj.getId());
		}
	}

	public void remove(String id) throws Exception {
		remove(id, new DB2ActorRTDAO(connection));

	}

	public void update(ValueObject vo) throws Exception {
		update(vo, new DB2ActorRTDAO(connection));

	}

}
