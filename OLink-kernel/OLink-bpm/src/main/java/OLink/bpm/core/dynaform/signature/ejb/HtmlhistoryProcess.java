package OLink.bpm.core.dynaform.signature.ejb;

import java.util.List;

import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * 
 * @author Alex
 * 
 */
public interface HtmlhistoryProcess extends IDesignTimeProcess<Htmlhistory> {

	List<Htmlhistory> queryAll() throws Exception;

	List<Htmlhistory> queryById(String id) throws Exception;

	void createHtmlhistory(Htmlhistory htmlhistory) throws Exception;
}
