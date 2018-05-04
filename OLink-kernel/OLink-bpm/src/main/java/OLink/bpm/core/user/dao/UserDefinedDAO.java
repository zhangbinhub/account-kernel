package OLink.bpm.core.user.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.user.ejb.UserDefined;

public interface UserDefinedDAO extends IDesignTimeDAO<UserDefined> {

	Collection<UserDefined> findByApplication(String applicationId) throws Exception;

	int queryCountByName(String name, String applicationid) throws Exception;
//	public abstract UserDefined login(String loginno, String domain)
//	throws Exception;

	UserDefined login(String name) throws Exception;
}
