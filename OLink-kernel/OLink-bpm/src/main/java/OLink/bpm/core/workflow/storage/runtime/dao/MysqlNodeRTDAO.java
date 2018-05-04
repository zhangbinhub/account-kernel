package OLink.bpm.core.workflow.storage.runtime.dao;

import java.sql.Connection;
import java.sql.SQLException;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @author Chris
 * 
 */
public class MysqlNodeRTDAO extends AbstractNodeRTDAO implements NodeRTDAO {

	public MysqlNodeRTDAO(Connection conn) throws Exception {
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
		create(vo, new MysqlActorRTDAO(connection));
	}

	public void remove(ValueObject obj) throws Exception {
		if (obj != null) {
			remove(obj.getId());
		}
	}

	public void remove(String pk) throws Exception {

		remove(pk, new MysqlActorRTDAO(connection));
	}

	public void update(ValueObject vo) throws Exception {

		update(vo, new MysqlActorRTDAO(connection));
	}
}
