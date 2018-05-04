package OLink.bpm.core.dynaform.component.dao;

import OLink.bpm.core.dynaform.form.dao.HibernateFormDAO;
import OLink.bpm.core.dynaform.component.ejb.Component;

public class HibernateComponentDAO extends HibernateFormDAO<Component> implements
		ComponentDAO {

	public HibernateComponentDAO(String voClassName) {
		super(voClassName);
	}

}
