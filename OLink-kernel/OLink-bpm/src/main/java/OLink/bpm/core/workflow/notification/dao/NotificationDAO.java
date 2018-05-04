package OLink.bpm.core.workflow.notification.dao;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import OLink.bpm.base.dao.IRuntimeDAO;

public interface NotificationDAO extends IRuntimeDAO {
	Collection<Map<String, Object>> queryOverDuePending(Date curDate,
														String applicationId) throws SQLException;
}
