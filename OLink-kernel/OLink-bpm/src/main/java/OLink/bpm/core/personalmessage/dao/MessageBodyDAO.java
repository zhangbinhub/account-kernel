package OLink.bpm.core.personalmessage.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.personalmessage.ejb.MessageBody;

public interface MessageBodyDAO extends IDesignTimeDAO<MessageBody> {

	DataPackage<MessageBody> queryTrash(String userId, ParamsTable params) throws Exception;
	
}