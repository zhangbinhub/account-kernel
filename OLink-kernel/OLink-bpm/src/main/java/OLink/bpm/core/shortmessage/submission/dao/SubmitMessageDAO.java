package OLink.bpm.core.shortmessage.submission.dao;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;
import OLink.bpm.core.user.action.WebUser;

public interface SubmitMessageDAO extends IDesignTimeDAO<SubmitMessageVO> {

	SubmitMessageVO getMessageByReplyCode(String replyCode, String recvtel)
			throws Exception;
	
	DataPackage<SubmitMessageVO> list(WebUser user, ParamsTable params)throws Exception;

}