package OLink.bpm.core.workflow.notification.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class MysqlNotificationDAO extends AbstractNotificationDAO implements
		NotificationDAO {

	public MysqlNotificationDAO(Connection conn) throws Exception {
		super(conn);
		this.DBType = DbTypeUtil.DBTYPE_MYSQL;
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_MYSQL);
	}
}
