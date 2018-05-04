package OLink.bpm.core.deploy.module.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;

public class HibernateModuleDAO extends HibernateBaseDAO<ModuleVO> implements ModuleDAO {
	public HibernateModuleDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<ModuleVO> getModuleByApplication(String application)
			throws Exception {
		String sql = "FROM " + _voClazzName + " where application='"
				+ application + "'";
		return getDatas(sql);
	}

	public ModuleVO findByName(String name, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " where name= '" + name
				+ "' and application='" + application + "'";
		return (ModuleVO) getData(hql);
	}

}
