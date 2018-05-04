package OLink.bpm.core.personalmessage.dao;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.personalmessage.ejb.PersonalMessageVO;

public interface PersonalMessageDAO extends IDesignTimeDAO<PersonalMessageVO> {

	/**
	 * 查找回收箱中的站内短信
	 * 
	 * @param userid
	 *            用户标识
	 * @param page
	 *            分页信息
	 * @param line
	 *            每页行数
	 * @return 站内短信
	 * @throws Exception
	 */
	DataPackage<PersonalMessageVO> queryTrash(String userid, ParamsTable params)
			throws Exception;

	/**
	 * 查询新的站内短信条数
	 * 
	 * @param userid
	 *            用户标识
	 * @return 新站内短信的数量
	 * @throws Exception
	 */
	int countNewMessages(String userid) throws Exception;
	
	String[] getReceiverUserIdsByMessageBodyId(String bodyId) throws Exception;

	DataPackage<PersonalMessageVO> queryNewMessage(String userid, ParamsTable params)
		throws Exception;
	
	DataPackage<PersonalMessageVO> queryInBox(String userid, ParamsTable params)
		throws Exception;
	
	DataPackage<PersonalMessageVO> queryOutbox(String userid, ParamsTable params) throws Exception;
	
}