package OLink.bpm.core.deploy.application.dao;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;

public interface ApplicationDAO extends IDesignTimeDAO<ApplicationVO> {
	ApplicationVO getApplicationByDomainName(String domainName)
			throws Exception;

	Map<String, ApplicationVO> getAppDomain_Cache() throws Exception;

	Collection<ApplicationVO> getAllApplication() throws Exception;

	boolean isEmpty() throws Exception;

	Collection<ApplicationVO> queryAppsByDomain(String domainId, int page, int line) throws Exception;

	Collection<ApplicationVO> queryApplications(String suserid, int page, int line) throws Exception;

	ApplicationVO findBySIPAppKey(String appKey) throws Exception;

	ApplicationVO findByName(String name) throws Exception;

}
