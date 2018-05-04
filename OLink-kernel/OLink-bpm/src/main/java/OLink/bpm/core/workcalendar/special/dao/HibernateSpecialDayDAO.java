package OLink.bpm.core.workcalendar.special.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.workcalendar.special.ejb.SpecialDayVO;

public class HibernateSpecialDayDAO extends HibernateBaseDAO<SpecialDayVO> implements SpecialDayDAO {

	public HibernateSpecialDayDAO(String valueObjectName){
		super(valueObjectName);
	}
	
}
