package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;

/**
 * 
 * @author Chris
 * 
 */
public class HsqldbRelationHISDAO extends AbstractRelationHISDAO implements
		RelationHISDAO {

	public HsqldbRelationHISDAO(Connection conn) throws Exception {
		super(conn);
		dbTag = "HypersonicSQL: ";
		if (conn != null) {
			this.schema = "public".toUpperCase();
		}
	}

}
