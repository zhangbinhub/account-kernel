package OLink.bpm.core.domain.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.constans.Web;
import OLink.bpm.core.department.ejb.DepartmentProcess;
import OLink.bpm.core.department.ejb.DepartmentVO;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.domain.dao.DomainDAO;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.base.dao.DAOFactory;
import eWAP.core.Tools;

public class DomainProcessBean extends AbstractDesignTimeProcessBean<DomainVO> implements DomainProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4350448235990331151L;

	public void doCreate(ValueObject vo) throws Exception {

		super.doCreate(vo);
		// 创建一个部门
		DomainVO domain = (DomainVO) vo;
		DepartmentProcess process = (DepartmentProcess) ProcessFactory.createProcess(DepartmentProcess.class);
		DepartmentVO depVO = new DepartmentVO();
		depVO.setId(Tools.getSequence());
		depVO.setName(domain.getName());
		depVO.setApplicationid(domain.getApplicationid());
		depVO.setCode("00");// 为默认的code的00
		depVO.setDomain(domain);
		depVO.setLevel(0);
		depVO.setDomainid(domain.getId());
		depVO.setIndexCode(depVO.getId());
		process.doCreate(depVO);

		PermissionPackage.clearCache();
	}

	public void doRemove(String pk) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			// 检查是否有下属部门
			DomainVO tempDomain = (DomainVO) getDAO().find(pk);
			if (tempDomain != null && tempDomain.getDepartments().size() > 0) {
				throw new DomainException("{*[core.domain.department.hassub]*}");
			}
			if (tempDomain != null && tempDomain.getApplications().size() > 0) {
				throw new DomainException("{*[core.domain.hasapp]*}");
			}

			if (tempDomain != null) {
				UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
				Collection<UserVO> cols = process.queryByDomain(tempDomain.getId(), 1, 10);
				if (cols != null && cols.size() > 0) {
					throw new DomainException("{*[core.domain.user.hassub]*}");
				}
				tempDomain.setUsers(null);
				tempDomain.setApplications(null);
				// Update(tempDomain);
				super.doRemove(pk);
				PersistenceUtils.commitTransaction();
				PermissionPackage.clearCache();
			}
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}

	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcess#doUpdate(ValueObject)
	 */
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			DomainVO domain = (DomainVO) vo;
			DomainVO doo = (DomainVO) getDAO().find(vo.getId());
			String smswd = domain.getSmsMemberPwd();
			if (smswd != null && !smswd.trim().equals(doo.getSmsMemberPwd())
					&& !smswd.trim().equals(Web.DEFAULT_SHOWPASSWORD)) {
				domain.setSmsMemberPwd(smswd);
			} else {
				domain.setSmsMemberPwd(doo.getSmsMemberPwd());
			}

			getDAO().update(vo);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}
	
	protected IDesignTimeDAO<DomainVO> getDAO() throws Exception {
		return (DomainDAO) DAOFactory.getDefaultDAO(DomainVO.class.getName());
	}

	public DomainVO getDomainByName(String tempname) throws Exception {
		return ((DomainDAO) getDAO()).getDomainByName(tempname);
	}

	public Collection<DomainVO> queryDomains(String userid, int page, int line) throws Exception {
		return ((DomainDAO) getDAO()).queryDomains(userid, page, line);
	}

	public DataPackage<DomainVO> queryDomainsByManager(String manager, int page, int line) throws Exception {
		return ((DomainDAO) getDAO()).queryDomainsByManager(manager, page, line);
	}

	public Collection<DomainVO> getAllDomain() throws Exception {
		return ((DomainDAO) getDAO()).getAllDomain();
	}

	public DataPackage<DomainVO> queryDomainsByManagerAndName(String managerName, String name, int page, int line)
			throws Exception {

		return ((DomainDAO) getDAO()).queryDomainsbyManagerAndName(managerName, name, page, line);
	}

	public DataPackage<DomainVO> queryDomainsByName(String name, int page, int line) throws Exception {

		return ((DomainDAO) getDAO()).queryDomainsByName(name, page, line);
	}

	public DataPackage<DomainVO> queryDomainsbyManagerLoginnoAndName(String manager, String name, int page, int line)
			throws Exception {
		return ((DomainDAO) getDAO()).queryDomainsbyManagerLoginnoAndName(manager, name, page, line);
	}

	public DomainVO getDomainByDomainName(String name) throws Exception {
		return ((DomainDAO) getDAO()).getDomainByName(name);
	}

}
