package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.user.action.WebUser;

/**
 * @author happy
 *
 */
public interface CirculatorProcess extends IRunTimeProcess<Circulator> {

	/**
	 * 根据NodeRtId查询
	 * @return
	 * @throws Exception
	 */
	Collection<Circulator> doQueryByNodeRtId(String id) throws Exception;
	
	/**
	 * 根据用户查找代办
	 * @param user
	 * 		登陆用户
	 * @return
	 * @throws Exception
	 */
	DataPackage<Circulator> getPendingByUser(ParamsTable params, WebUser user) throws Exception;
	
	/**
	 * 获取用户的已阅、未阅信息
	 * @param params
	 * @param user
	 * @return
	 * @throws Exception
	 */
	DataPackage<Circulator> getWorksByUser(ParamsTable params, WebUser user) throws Exception;
	
	/**
	 * 根据当前文档的信息查找
	 * @return
	 */
	Circulator findByCurrDoc(String docId, String flowStateId, boolean isRead, WebUser user) throws Exception;
	
	/**
	 * 根据外键(DOCID、NODERT_ID、FLOWSTATERT_ID)删除Circulator
	 * @param key
	 * 		外键字段名
	 * @param val
	 * 		外键值
	 * @throws Exception
	 */
	void doRemoveByForeignKey(String key, Object val) throws Exception;
}
