package OLink.bpm.core.workcalendar.standard.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.workcalendar.standard.ejb.StandardDayVO;

public class HibernateStandardDayDAO extends HibernateBaseDAO<StandardDayVO>
		implements StandardDayDAO {

	public HibernateStandardDayDAO(String valueObjectName){
		super(valueObjectName);
	}

}
