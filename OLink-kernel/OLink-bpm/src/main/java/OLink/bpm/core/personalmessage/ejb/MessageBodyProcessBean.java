package OLink.bpm.core.personalmessage.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.personalmessage.dao.MessageBodyDAO;

public class MessageBodyProcessBean extends AbstractDesignTimeProcessBean<MessageBody> implements MessageBodyProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7045793505461260376L;

	protected IDesignTimeDAO<MessageBody> getDAO() throws Exception {
		return (MessageBodyDAO) DAOFactory.getDefaultDAO(MessageBody.class.getName());
	}

}
