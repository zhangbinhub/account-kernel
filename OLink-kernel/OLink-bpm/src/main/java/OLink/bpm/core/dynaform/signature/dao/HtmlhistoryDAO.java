package OLink.bpm.core.dynaform.signature.dao;

import java.util.List;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.signature.ejb.Htmlhistory;

public interface HtmlhistoryDAO extends IDesignTimeDAO<Htmlhistory> {

	List<Htmlhistory> queryAll() throws Exception;
	
	List<Htmlhistory> queryById(String id) throws Exception;
	
	void createHtmlhistory(Htmlhistory htmlhistory) throws Exception;
}
