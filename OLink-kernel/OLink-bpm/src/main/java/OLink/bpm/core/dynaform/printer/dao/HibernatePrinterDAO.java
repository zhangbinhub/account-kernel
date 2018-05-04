package OLink.bpm.core.dynaform.printer.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.dynaform.printer.ejb.Printer;

/**
 * @author Happy
 *
 */
public class HibernatePrinterDAO extends HibernateBaseDAO<Printer> implements PrinterDAO {

	public HibernatePrinterDAO(String valueObjectName) {
		super(valueObjectName);
	}

	public Printer findByFormId(String formid) throws Exception{
		String hql="FROM " + _voClazzName + " vo where vo.formid ='"+formid+"'";
		return (Printer)this.getData(hql);
	}

	public Collection<Printer> getPrinterByModule(String moduleid) throws Exception {
		String hql = "FROM " + _voClazzName + " vo WHERE vo.module='"+ moduleid + "'";
		return this.getDatas(hql, null);
	}

}
