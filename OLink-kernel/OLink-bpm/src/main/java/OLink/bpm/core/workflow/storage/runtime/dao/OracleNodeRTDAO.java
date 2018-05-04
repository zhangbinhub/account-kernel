package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import OLink.bpm.base.dao.ValueObject;
import org.apache.log4j.Logger;

/**
 * 
 * @author Nicholas
 * 
 */
public class OracleNodeRTDAO extends AbstractNodeRTDAO implements NodeRTDAO {
	Logger log = Logger.getLogger(OracleNodeRTDAO.class);

	public OracleNodeRTDAO(Connection conn) throws Exception {
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
		create(vo, new OracleActorRTDAO(connection));

	}

	public void remove(ValueObject obj) throws Exception {
		if (obj != null) {
			remove(obj.getId());
		}
	}

	public void remove(String id) throws Exception {
		remove(id, new OracleActorRTDAO(connection));

	}

	public void update(ValueObject vo) throws Exception {
		update(vo, new OracleActorRTDAO(connection));

	}

}
