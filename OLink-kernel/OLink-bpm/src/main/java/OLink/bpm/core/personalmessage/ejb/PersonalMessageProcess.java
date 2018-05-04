package OLink.bpm.core.personalmessage.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface PersonalMessageProcess extends
		IDesignTimeProcess<PersonalMessageVO> {

	/**
	 * 将指定的站内短信删除
	 * 
	 * @param pk
	 *            站内短信标识
	 */
	void doRemove(String pk) throws Exception;

	/**
	 * 根据条件查询用户所有在收件箱中的站内短信
	 * 
	 * @param userid
	 *            用户的标识
	 * @param params
	 *            查询的条件
	 * @return 站内短信的集合
	 * @throws Exception
	 */
	DataPackage<PersonalMessageVO> doInbox(String userid,
										   ParamsTable params) throws Exception;

	/**
	 * 根据条件查询用户所有在发件箱中的站内短信
	 * 
	 * @param userid
	 *            用户的标识
	 * @param params
	 *            查询的条件
	 * @return 站内短信的集合
	 * @throws Exception
	 */
	DataPackage<PersonalMessageVO> doOutbox(String id,
											ParamsTable params) throws Exception;

	/**
	 * 根据条件查询用户所有在回收箱中的站内短信
	 * 
	 * @param userid
	 *            用户的标识
	 * @param params
	 *            查询的条件
	 * @return 站内短信的集合
	 * @throws Exception
	 */
	DataPackage<PersonalMessageVO> doTrash(String userid,
										   ParamsTable params) throws Exception;

	/**
	 * 将单条站内短信移至回收箱
	 */
	void doSendToTrash(ValueObject vo) throws Exception;

	/**
	 * 将一到多条站内短信移至回收箱
	 */
	void doSendToTrash(String[] msgs) throws Exception;

	/**
	 * 根据角色分组进行群发短信
	 * 
	 * @param roleid
	 *            角色组唯一标识
	 * @param domainid
	 *            需要发送的用户组所在的域标识
	 * @param pmVO
	 *            需要发送的短信内容
	 * @throws Exception
	 */
	void doCreateByRole(String roleid, String domainid,
						PersonalMessageVO pmVO) throws Exception;

	/**
	 * 根据角色分组进行群发短信
	 * 
	 * @param roleid
	 *            角色组唯一标识
	 * @param domainid
	 *            需要发送的用户组所在的域标识
	 * @param senderid
	 *            发送者标识
	 * @param title
	 *            短信的标题
	 * @param content
	 *            短信的内容
	 * @throws Exception
	 */
	void doCreateByRole(String roleid, String domainid,
						String senderid, String title, String content) throws Exception;

	/**
	 * 根据部门组别群发短信
	 * 
	 * @param departmentid
	 *            部门唯一标识
	 * @param pmVO
	 *            需要发送的短信内容
	 * @throws Exception
	 */
	void doCreateByDepartment(String departmentid,
							  PersonalMessageVO pmVO) throws Exception;

	/**
	 * 根据部门群发短信
	 * 
	 * @param departmentid
	 *            部门标识
	 * @param senderid
	 *            发送者标识
	 * @param title
	 *            短信标题
	 * @param content
	 *            短信内容
	 * @throws Exception
	 */
	void doCreateByDepartment(String departmentid,
							  String senderid, String title, String content) throws Exception;

	/**
	 * 查找用户是否有新的站内短信
	 * 
	 * @param user
	 *            当前用户
	 * @return 短信数量
	 * @throws Exception
	 */
	int countNewMessages(String userId) throws Exception;

	String[] getReceiverUserIdsByMessageBodyId(String bodyId)
			throws Exception;

	DataPackage<PersonalMessageVO> doNoRead(String id,
											ParamsTable params) throws Exception;

	void doCreate(String senderid, String receiverid, String title,
				  String content, String type) throws Exception;

	void doCreate(String senderid, String receiverid, String title,
				  String content) throws Exception;

	void doGroupSend(Collection<UserVO> users, PersonalMessageVO pmVO)
			throws Exception;

	void doCreateByDepartment(String departmentid, String senderid,
							  String title, String content, String type) throws Exception;

	void doCreateByRole(String roleid, String domainid, String senderid,
						String title, String content, String type) throws Exception;

	void doCreateByUserIds(String[] ids, PersonalMessageVO vo)
			throws Exception;

	PersonalMessageVO doSavePersonalMessageVO(PersonalMessageVO vo)
			throws Exception;

}