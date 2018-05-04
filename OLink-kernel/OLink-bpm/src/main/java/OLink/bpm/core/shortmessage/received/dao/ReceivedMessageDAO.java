package OLink.bpm.core.shortmessage.received.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.shortmessage.received.ejb.ReceivedMessageVO;

public interface ReceivedMessageDAO extends IDesignTimeDAO<ReceivedMessageVO> {
	
	/**
	 * 返回回复记录对象
	 * @param replyCode 回复代码
	 * @param recvtel 回复者电话号码
	 * @return 回复记录对象
	 * @throws Exception
	 */
	ReceivedMessageVO getMessageByReplyCode(String replyCode,
											String recvtel) throws Exception;
	
	/**
	 * 返回未读回复记录集合
	 * @return 未读回复记录集合
	 * @throws Exception
	 */
	Collection<ReceivedMessageVO> queryUnReadMessage() throws Exception;

}