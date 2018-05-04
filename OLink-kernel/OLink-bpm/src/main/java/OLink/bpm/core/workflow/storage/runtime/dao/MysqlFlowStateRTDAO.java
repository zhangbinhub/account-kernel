package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @author Chris
 * 
 */
public class MysqlFlowStateRTDAO extends AbstractFlowStateRTDAO implements
		FlowStateRTDAO {

	public MysqlFlowStateRTDAO(Connection conn) throws Exception {
		super(conn);
		dbTag = "MY SQL: ";
		if (conn != null) {
			try {
				this.schema = conn.getMetaData().getURL().trim().toUpperCase();
				if (this.schema.indexOf("?USE") > 0) {
					this.schema = this.schema.substring(this.schema
							.lastIndexOf("/") + 1, this.schema.indexOf("?USE"));
				} else {
					this.schema = this.schema.substring(this.schema
							.lastIndexOf("/") + 1);
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}

	public void create(ValueObject vo) throws Exception {
		create(vo, new MysqlNodeRTDAO(connection));
	}

	public void remove(String id) throws Exception {
		remove(id, new MysqlActorRTDAO(connection), new MysqlNodeRTDAO(
				connection));
	}

	public void update(ValueObject vo) throws Exception {
		update(vo, new MysqlNodeRTDAO(connection));
	}
}
