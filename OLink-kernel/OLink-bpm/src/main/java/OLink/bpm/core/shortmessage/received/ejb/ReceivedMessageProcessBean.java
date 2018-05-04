package OLink.bpm.core.shortmessage.received.ejb;

import java.util.Collection;
import java.util.Date;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.shortmessage.received.dao.ReceivedMessageDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;

public class ReceivedMessageProcessBean extends AbstractDesignTimeProcessBean<ReceivedMessageVO>
		implements ReceivedMessageProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1754608461011239235L;

	/**
	 * 创建待发短信
	 * 
	 * @param vo
	 *            短信内容
	 */
	public void doCreate(ValueObject vo) throws Exception {
		ReceivedMessageVO receiverVO = (ReceivedMessageVO) vo;
		receiverVO.setCreated(new Date());
		super.doCreate(receiverVO);
		PermissionPackage.clearCache();
	}

	/**
	 * 根据Document ID 查询回复记录
	 * @param docid 文档ID
	 * @see DataPackage
	 * @return　返回回复记录结果 
	 * @throws Exception
	 */
	public DataPackage<ReceivedMessageVO> queryByDocId(String docid) throws Exception {
		ParamsTable params = new ParamsTable();
		params.setParameter("s_docid", docid);
		return getDAO().query(params);
	}
	
	/**
	 * 根据Document ID 查询回复记录
	 * @param parentid 关联的发送记录ID
	 * @see DataPackage
	 * @return　返回回复记录结果 
	 * @throws Exception
	 */
	public DataPackage<ReceivedMessageVO> queryByParent(String id) throws Exception{
		ParamsTable params = new ParamsTable();
		params.setParameter("s_parent", id);
		return getDAO().query(params);
	}

	protected IDesignTimeDAO<ReceivedMessageVO> getDAO() throws Exception {
		return (ReceivedMessageDAO) DAOFactory.getDefaultDAO(ReceivedMessageVO.class.getName());
	}
	
	/**
	 * 查询出未读回复记录
	 * @return 返回未读回复记录集
	 * @throws Exception
	 */
	public Collection<ReceivedMessageVO> doQueryUnReadMessage() throws Exception{
		return ((ReceivedMessageDAO)getDAO()).queryUnReadMessage();
	}

}
