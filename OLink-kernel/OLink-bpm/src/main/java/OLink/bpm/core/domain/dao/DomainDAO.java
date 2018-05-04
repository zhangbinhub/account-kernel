package OLink.bpm.core.domain.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.domain.ejb.DomainVO;

public interface DomainDAO extends IDesignTimeDAO<DomainVO> {

	DomainVO getDomainByName(String tempname) throws Exception;
	
	Collection<DomainVO> queryDomains(String userid, int page, int line) throws Exception;

	DataPackage<DomainVO> queryDomainsByManager(String manager, int page, int line) throws Exception;
	
	DataPackage<DomainVO> queryDomainsByName(String name, int page, int line) throws Exception;
	
	DataPackage<DomainVO> queryDomainsbyManagerAndName(String manager, String name, int page, int line) throws Exception;
	
	Collection<DomainVO> getAllDomain() throws Exception;
	
	DataPackage<DomainVO> queryDomainsbyManagerLoginnoAndName(String manager,
															  String name, int page, int line) throws Exception;
}
