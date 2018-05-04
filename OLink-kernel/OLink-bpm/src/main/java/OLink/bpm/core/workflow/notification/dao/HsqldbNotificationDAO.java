package OLink.bpm.core.workflow.notification.dao;

import java.sql.Connection;

import OLink.bpm.util.DbTypeUtil;

public class HsqldbNotificationDAO extends AbstractNotificationDAO implements
		NotificationDAO {

	public HsqldbNotificationDAO(Connection conn) throws Exception {
		super(conn);
		this.DBType = DbTypeUtil.DBTYPE_HSQLDB;
		this.schema = DbTypeUtil.getSchema(conn, DbTypeUtil.DBTYPE_HSQLDB);
	}
}
