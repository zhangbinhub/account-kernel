package OLink.bpm.core.validate.repository.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.validate.repository.dao.ValidateRepositoryDAO;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.StaleObjectStateException;

public class ValidateRepositoryProcessBean extends
		AbstractDesignTimeProcessBean<ValidateRepositoryVO> implements ValidateRepositoryProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6055979077980725609L;

	protected IDesignTimeDAO<ValidateRepositoryVO> getDAO() throws Exception {
		return (ValidateRepositoryDAO) DAOFactory
				.getDefaultDAO(ValidateRepositoryVO.class.getName());
	}

	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			ValueObject po = getDAO().find(vo.getId());
			if (po != null && vo.getVersion() != po
					.getVersion())
				throw new StaleObjectStateException(null, null);
			vo.setVersion(vo
					.getVersion() + 1);
			if (po != null) {
				PropertyUtils.copyProperties(po, vo);
				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}

			PersistenceUtils.commitTransaction();
		} catch (StaleObjectStateException e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

	/**
	 * 取该模块下的所有的校验脚本
	 */
	public Collection<ValidateRepositoryVO> get_validate(String applicationid) throws Exception {
		return ((ValidateRepositoryDAO) getDAO())
				.getValidateRepositoryByApplication(applicationid);
	}

	public ValueObject doView(String pk) throws Exception {
		return getDAO().find(pk);
	}

	/**
	 * 检验名称是否唯一
	 */
	public boolean isValidateNameExist(String id, String name, String application)
			throws Exception {
		return ((ValidateRepositoryDAO)getDAO()).isValidateNameExist(id, name, application);
	}
}
