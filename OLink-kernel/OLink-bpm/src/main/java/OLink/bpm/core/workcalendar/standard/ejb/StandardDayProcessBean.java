package OLink.bpm.core.workcalendar.standard.ejb;

import java.util.Date;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import org.apache.commons.beanutils.PropertyUtils;

import OLink.bpm.core.workcalendar.standard.dao.StandardDayDAO;

public class StandardDayProcessBean extends AbstractDesignTimeProcessBean<StandardDayVO> implements StandardDayProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3983462796565055563L;

	protected IDesignTimeDAO<StandardDayVO> getDAO() throws Exception {
		IDesignTimeDAO<StandardDayVO> dao = (StandardDayDAO) DAOFactory.getDefaultDAO(StandardDayVO.class.getName());
		return dao;
	}

	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			StandardDayVO po = (StandardDayVO) getDAO().find(vo.getId());
			if (po != null) {
				PropertyUtils.copyProperties(po, vo);
				po.setLastModifyDate(new Date());
				getDAO().update(po);
			}
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
		PermissionPackage.clearCache();
	}
}
