package OLink.bpm.webservice;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import OLink.bpm.util.ObjectUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.webservice.fault.DomainServiceFault;
import OLink.bpm.webservice.model.SimpleDomain;
import OLink.bpm.core.domain.ejb.DomainProcess;

public class DomainService {
	/**
	 * 根据域管理员ID查找所管理的域列表
	 * 
	 * @param domainAdminId
	 *            管理员ID
	 * @return domain list 域列表
	 * @throws DomainServiceFault
	 */
	public Collection<SimpleDomain> searchDomainsByDomainAdmin(
			String domainAdminId) throws DomainServiceFault {
		try {
			SuperUserProcess userProcess = (SuperUserProcess) ProcessFactory
					.createProcess(SuperUserProcess.class);
			SuperUserVO superUserVO = (SuperUserVO) userProcess
					.doView(domainAdminId);
			Collection<?> domains = superUserVO.getDomains();
			return convertToSimple(domains);
		} catch (Exception e) {
			throw new DomainServiceFault(e.getMessage());
		}
	}

	/**
	 * 根据名称查找域
	 * 
	 * @param name
	 *            　名称
	 * @return　应用
	 * @throws DomainServiceFault
	 */
	public SimpleDomain searchDomainByName(String name)
			throws DomainServiceFault {
		try {
			DomainProcess domainProcess = (DomainProcess) ProcessFactory
					.createProcess(DomainProcess.class);
			ParamsTable params = new ParamsTable();
			params.setParameter("t_name", name);
			Collection<?> domainList = domainProcess.doSimpleQuery(params);
			if (domainList != null && !domainList.isEmpty()) {
				DomainVO domain = (DomainVO) domainList.iterator().next();
				return convertToSimple(domain);
			}
			return null;
		} catch (Exception e) {
			throw new DomainServiceFault(e.getMessage());
		}
	}

	/**
	 * 转换为简单域对象列表
	 * 
	 * @param domainList
	 *            企业域列表
	 * @return
	 */
	private Collection<SimpleDomain> convertToSimple(Collection<?> domainList) {
		Collection<SimpleDomain> sDomainList = new ArrayList<SimpleDomain>();
		for (Iterator<?> iterator = domainList.iterator(); iterator.hasNext();) {
			DomainVO domain = (DomainVO) iterator.next();
			sDomainList.add(convertToSimple(domain));
		}

		return sDomainList;
	}

	/**
	 * 转换为简单域对象
	 * 
	 * @param domain
	 *            企业域
	 * @return
	 */
	private SimpleDomain convertToSimple(DomainVO domain) {
		if (domain != null) {
			SimpleDomain sDomain = new SimpleDomain();

			try {
				ObjectUtil.copyProperties(sDomain, domain);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return sDomain;
		}
		return null;
	}
}
