package OLink.bpm.core.domain.level.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.domain.level.dao.DomainLevelDAO;

public class DomainLevelProcessBean extends AbstractDesignTimeProcessBean<DomainLevelVO> implements
		DomainLevelProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7290465529108799324L;

	public void doCreate(ValueObject vo) throws Exception {
		super.doCreate(vo);
		PermissionPackage.clearCache();
	}

	public void doRemove(String pk) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			// 检查是否有下属部门
			DomainLevelVO tmpRate = (DomainLevelVO) getDAO().find(pk);
			if (tmpRate != null && tmpRate.getDomains().size() > 0) {
				throw new DomainLevelException("{*[core.department.hassub]*}");
			}
			super.doRemove(pk);
			PersistenceUtils.commitTransaction();
			PermissionPackage.clearCache();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}

	}

	protected IDesignTimeDAO<DomainLevelVO> getDAO() throws Exception {
		return (DomainLevelDAO) DAOFactory.getDefaultDAO(DomainLevelVO.class.getName());
	}

	public DomainLevelVO getRateByName(String tempname) throws Exception {
		return ((DomainLevelDAO) getDAO()).getRateByName(tempname);
	}
}
