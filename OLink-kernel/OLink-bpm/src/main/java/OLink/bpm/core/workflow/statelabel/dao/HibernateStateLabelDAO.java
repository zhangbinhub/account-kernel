package OLink.bpm.core.workflow.statelabel.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.workflow.statelabel.ejb.StateLabel;

public class HibernateStateLabelDAO extends HibernateBaseDAO<StateLabel>
		implements StateLabelDAO {

	public HibernateStateLabelDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<StateLabel> queryName(String application)
			throws Exception {
		String hql = "select name  from " + this._voClazzName
				+ " vo where applicationid='" + application + "'";
		return getDatas(hql);
	}

	public Collection<StateLabel> queryByName(String name, String application)
			throws Exception {
		String hql = "from " + _voClazzName + " vo where vo.name='" + name
				+ "' and vo.applicationid = '" + application
				+ "' order by vo.orderNo";

		return getDatas(hql);

	}

	public Collection<StateLabel> queryStates(String application)
			throws Exception {
		String hql = "from " + this._voClazzName
				+ " vo where applicationid='" + application
				+ "' order by orderNo";
		return this.getDatas(hql);
	}

}
