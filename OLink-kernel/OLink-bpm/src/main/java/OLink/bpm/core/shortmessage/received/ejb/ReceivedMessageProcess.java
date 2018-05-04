package OLink.bpm.core.shortmessage.received.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface ReceivedMessageProcess extends IDesignTimeProcess<ReceivedMessageVO> {
	/**
	 * 根据Document ID 查询回复记录
	 * @param docid 文档ID
	 * @see DataPackage
	 * @return　返回回复记录结果 
	 * @throws Exception
	 */
	DataPackage<ReceivedMessageVO> queryByDocId(String docid) throws Exception;
	
	/**
	 * 根据Document ID 查询回复记录
	 * @param parentid 关联的发送记录ID
	 * @see DataPackage
	 * @return　返回回复记录结果 
	 * @throws Exception
	 */
	DataPackage<ReceivedMessageVO> queryByParent(String parentid) throws Exception;
	
	/**
	 * 查询出未读回复记录
	 * @return 返回未读回复记录集
	 * @throws Exception
	 */
	Collection<ReceivedMessageVO> doQueryUnReadMessage() throws Exception;
}