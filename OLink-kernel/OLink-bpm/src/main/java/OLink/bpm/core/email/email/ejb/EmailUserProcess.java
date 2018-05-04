package OLink.bpm.core.email.email.ejb;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.base.action.ParamsTable;

public interface EmailUserProcess extends IDesignTimeProcess<EmailUser> {

	EmailUser getEmailUserByAccount(String account) throws Exception;
	
	EmailUser getEmailUser(String account, String domainid) throws Exception;
	
	DataPackage<EmailUser> getEmailUsers(String domainid, ParamsTable params) throws Exception;
	
	EmailUser getEmailUserByOwner(String ownerid, String domainid) throws Exception;
	
	void doCreateEmailUser(EmailUser user) throws Exception;
	
	void doUpdateEmailUser(EmailUser user) throws Exception;
	
	void doRemoveEmailUser(String id) throws Exception;
	
}
