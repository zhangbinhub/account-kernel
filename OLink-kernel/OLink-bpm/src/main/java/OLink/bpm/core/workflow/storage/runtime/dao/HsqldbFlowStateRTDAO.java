package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @author Chris
 * 
 */
public class HsqldbFlowStateRTDAO extends AbstractFlowStateRTDAO implements
		FlowStateRTDAO {

	public HsqldbFlowStateRTDAO(Connection conn) throws Exception {
		super(conn);
		dbTag = "HypersonicSQL: ";
		if (conn != null) {
			this.schema = "public".toUpperCase();
		}
	}

	public void create(ValueObject vo) throws Exception {
		create(vo, new HsqldbNodeRTDAO(connection));
	}

	public void remove(String id) throws Exception {

		remove(id, new HsqldbActorRTDAO(connection), new HsqldbNodeRTDAO(
				connection));
	}

	public void update(ValueObject vo) throws Exception {

		update(vo, new HsqldbNodeRTDAO(connection));
	}

}
