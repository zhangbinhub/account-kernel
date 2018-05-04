package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

/**
 * 
 * @author Nicholas
 * 
 */
public class OracleActorRTDAO extends AbstractActorRTDAO implements ActorRTDAO {
	Logger log = Logger.getLogger(OracleActorRTDAO.class);

	public OracleActorRTDAO(Connection conn) throws Exception {
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
