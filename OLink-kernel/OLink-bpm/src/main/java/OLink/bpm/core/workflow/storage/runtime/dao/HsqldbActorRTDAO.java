package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;

/**
 * 
 * @author Chris
 * 
 */
public class HsqldbActorRTDAO extends AbstractActorRTDAO implements ActorRTDAO {

	public HsqldbActorRTDAO(Connection conn) throws Exception {
		super(conn);
		dbTag = "HypersonicSQL: ";
		if (conn != null) {
			this.schema = "public".toUpperCase();
		}
	}

}
