package OLink.bpm.core.workflow.notification.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class OracleNotificationDAO extends AbstractNotificationDAO implements
		NotificationDAO {

	public OracleNotificationDAO(Connection conn) throws Exception {
		super(conn);
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_ORACLE);
	}
}
