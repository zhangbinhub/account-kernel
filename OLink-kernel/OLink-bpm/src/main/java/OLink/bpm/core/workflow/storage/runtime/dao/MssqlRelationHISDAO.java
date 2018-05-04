package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;

/**
 * 
 * @author Chris
 * 
 */
public class MssqlRelationHISDAO extends AbstractRelationHISDAO implements
		RelationHISDAO {

	public MssqlRelationHISDAO(Connection conn) throws Exception {
		super(conn);
		dbTag = "MS SQL SERVER: ";
	}

}
