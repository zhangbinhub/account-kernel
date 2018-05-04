package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DB2ActorRTDAO extends AbstractActorRTDAO implements ActorRTDAO {

	public DB2ActorRTDAO(Connection conn) throws Exception {
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

}
