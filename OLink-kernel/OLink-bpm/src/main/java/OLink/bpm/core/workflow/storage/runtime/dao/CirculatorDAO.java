package OLink.bpm.core.workflow.storage.runtime.dao;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.workflow.storage.runtime.ejb.Circulator;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.user.action.WebUser;

public interface CirculatorDAO extends IRuntimeDAO {
	/**
	 * 根据外键(DOCID、NODERT_ID、FLOWSTATERT_ID)级联查找Circulator
	 * 
	 * @param key
	 *            外键
	 * @param val
	 *            外键值
	 * @return 
	 * @throws Exception
	 */
	Collection<Circulator> queryByForeignKey(String key, Object val) throws Exception;
	
	/**
	 * 根据外键(DOCID、NODERT_ID、FLOWSTATERT_ID)删除Circulator
	 * @param key
	 * 		外键字段名
	 * @param val
	 * 		外键值
	 * @throws Exception
	 */
	void removeByForeignKey(String key, Object val) throws Exception;
	
	DataPackage<Circulator> queryPendingByUser(ParamsTable params, WebUser user) throws Exception;
	
	DataPackage<Circulator> queryWorksByUser(ParamsTable params, WebUser user) throws Exception;
	

	/**
	 * 根据当前文档的信息查找
	 * @return
	 */
	Circulator findByCurrDoc(String docId, String flowStateId, boolean isRead, WebUser user) throws Exception;
}
