package OLink.bpm.core.task.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.task.ejb.Task;

public class HibernateTaskDAO extends HibernateBaseDAO<Task> implements TaskDAO {

	public HibernateTaskDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<Task> getTaskByModule(String application, String module)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module.id='"
				+ module + "'";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	public Collection<Task> query(String application) throws Exception {
		String hql = "FROM " + _voClazzName + " vo ORDER BY vo.firstTime";

		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

}
