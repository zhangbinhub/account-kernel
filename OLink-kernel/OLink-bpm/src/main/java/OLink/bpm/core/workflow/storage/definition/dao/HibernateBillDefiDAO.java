package OLink.bpm.core.workflow.storage.definition.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;

public class HibernateBillDefiDAO extends HibernateBaseDAO<BillDefiVO>
		implements BillDefiDAO {
	public HibernateBillDefiDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<BillDefiVO> getBillDefiByModule(String moduleid)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module='"
				+ moduleid + "'";
		return this.getDatas(hql, null);
	}

	public BillDefiVO findBySubject(String name, String applicationId)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.subject = '" + name
				+ "' and vo.applicationid='" + applicationId + "'";
		return (BillDefiVO) getData(hql);
	}
}
