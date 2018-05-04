package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @author Nicholas
 * 
 */
public class OracleFlowStateRTDAO extends AbstractFlowStateRTDAO implements
		FlowStateRTDAO {
	// Logger log = Logger.getLogger(OracleFlowStateRTDAO.class);

	public OracleFlowStateRTDAO(Connection conn) throws Exception {
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
		create(vo, new OracleNodeRTDAO(connection));
	}

	public void remove(String id) throws Exception {

		remove(id, new OracleActorRTDAO(connection), new OracleNodeRTDAO(
				connection));
	}

	public void update(ValueObject vo) throws Exception {

		update(vo, new OracleNodeRTDAO(connection));
	}

}
