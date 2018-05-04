package OLink.bpm.core.macro.repository.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.macro.repository.ejb.RepositoryVO;

public class HibernateRepositoryDAO extends HibernateBaseDAO<RepositoryVO> implements
		RepositoryDAO {

	public HibernateRepositoryDAO(String voClassName) {
		super(voClassName);
	}

	public RepositoryVO getRepositoryByName(String name, String application)
			throws Exception {
		String hql = "from RepositoryVO rp where rp.name=" + "'" + name + "'";
                       
		if (application != null && application.length() > 0) {
			hql += (" and rp.applicationid  = '" + application + "' ");
		}
		return (RepositoryVO) this.getData(hql);
	}

	public boolean isMacroNameExist(String id,String name, String application)
			throws Exception {
		String hql = "from RepositoryVO rp where rp.name=" + "'"+name+"'";
		if(application != null && application.length()>0) {
			hql += (" and rp.applicationid = '" + application + "' ");
		}
		
		if(id != null && !id.equals("")){
			hql += (" and rp.id !='"+id+"' ");
		}

		return this.getData(hql) != null;
	}

}
