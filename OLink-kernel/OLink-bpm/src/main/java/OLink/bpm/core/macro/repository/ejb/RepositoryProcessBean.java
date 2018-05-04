package OLink.bpm.core.macro.repository.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.macro.repository.dao.RepositoryDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.dynaform.form.action.ImpropriateException;
import org.apache.commons.beanutils.PropertyUtils;

public class RepositoryProcessBean extends AbstractDesignTimeProcessBean<RepositoryVO> implements
		RepositoryProcess {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8429701819151113696L;

	/**
	 * @SuppressWarnings getDefaultDAO得到的process不定
	 */
	@SuppressWarnings("unchecked")
	protected IDesignTimeDAO<RepositoryVO> getDAO() throws Exception {
		IDesignTimeDAO<RepositoryVO> dao = (IDesignTimeDAO<RepositoryVO>) DAOFactory.getDefaultDAO(RepositoryVO.class.getName());
		return dao;
	}
	
	public RepositoryVO getRepositoryByName(String name,String application) throws Exception
	{
		return ((RepositoryDAO)getDAO()).getRepositoryByName(name, application);
	
}
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			ValueObject po = getDAO().find(vo.getId());
			if(po != null && vo.getVersion()!= po.getVersion())
				throw new ImpropriateException("{*[core.util.cannotsave]*}");
			vo.setVersion(vo.getVersion()+1);
			if (po != null) {
				PropertyUtils.copyProperties(po, vo);
				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}

			PersistenceUtils.commitTransaction();
		}catch(ImpropriateException e)
		{
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
		catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

	public boolean isMacroNameExist(String id, String name, String application)
			throws Exception {
		return ((RepositoryDAO)getDAO()).isMacroNameExist(id, name, application);
	}
}
