package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @author Chris
 * 
 */
public class HsqldbNodeRTDAO extends AbstractNodeRTDAO implements NodeRTDAO {

	public HsqldbNodeRTDAO(Connection conn) throws Exception {
		super(conn);
		dbTag = "HypersonicSQL: ";
		if (conn != null) {
			this.schema = "public".toUpperCase();
		}
	}

	public void create(ValueObject vo) throws Exception {
		create(vo, new HsqldbActorRTDAO(connection));
	}

	public void remove(ValueObject obj) throws Exception {
		if (obj != null) {
			remove(obj.getId());
		}
	}

	public void remove(String pk) throws Exception {

		remove(pk, new HsqldbActorRTDAO(connection));
	}

	public void update(ValueObject vo) throws Exception {

		update(vo, new HsqldbActorRTDAO(connection));
	}
}
