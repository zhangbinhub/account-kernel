package OLink.bpm.core.workflow.notification.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class DB2NotificationDAO extends AbstractNotificationDAO implements
		NotificationDAO {

	public DB2NotificationDAO(Connection conn) throws Exception {
		super(conn);
		this.DBType = DbTypeUtil.DBTYPE_DB2;
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_DB2);
	}
}
