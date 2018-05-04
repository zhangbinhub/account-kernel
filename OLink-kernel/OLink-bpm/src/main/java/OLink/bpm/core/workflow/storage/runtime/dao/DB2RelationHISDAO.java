package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class DB2RelationHISDAO extends AbstractRelationHISDAO implements
		RelationHISDAO {

	public DB2RelationHISDAO(Connection conn) throws Exception {
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
