package OLink.bpm.core.dynaform.pending.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

public interface PendingProcess extends IRunTimeProcess<PendingVO> {
	/**
	 * 根据参数创建或删除待办的记录
	 * 
	 * @param doc
	 *            Document对象
	 * @return
	 * @throws Exception
	 */
	PendingVO doCreateOrRemoveByDocument(Document doc, WebUser user) throws Exception;

	/**
	 * 查询所有待办数据，根据用户过滤待办记录,当用户有审批权限时记录才显示到待办
	 * 
	 * @param params
	 *            ParamsTable(参数对象)
	 * @param user
	 *            webUser
	 * @return 数据集合(DataPackage)
	 * @throws Exception
	 */
	DataPackage<PendingVO> doQueryByFilter(ParamsTable params, WebUser user) throws Exception;

	/**
	 * 根据参数更新待办的记录
	 * 
	 * @param doc
	 *            Document对象
	 * @throws Exception
	 */
	void doUpdateByDocument(Document doc, WebUser user) throws Exception;

	/**
	 * 获取待办的记录数量
	 * 
	 * @param params
	 *            参数
	 * @param user
	 *            webUser
	 * @return 记录数量
	 * @throws Exception
	 */
	long conutByFilter(ParamsTable params, WebUser user) throws Exception;
}
