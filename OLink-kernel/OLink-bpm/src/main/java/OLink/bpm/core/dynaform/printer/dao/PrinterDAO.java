package OLink.bpm.core.dynaform.printer.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.printer.ejb.Printer;

/**
 * @author Happy
 *
 */
public interface PrinterDAO extends IDesignTimeDAO<Printer> {
	
	Printer findByFormId(String formid) throws Exception;
	
	Collection<Printer> getPrinterByModule(String moduleid) throws Exception;

}
