package OLink.bpm.core.email.email.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.email.email.ejb.EmailUser;

public interface EmailUserDAO extends IDesignTimeDAO<EmailUser> {

	EmailUser queryEmailUserByAccount(String account) throws Exception;
	
	EmailUser queryEmailUser(String account, String domainid) throws Exception;
	
	DataPackage<EmailUser> queryEmailUsers(String domainid, ParamsTable params) throws Exception;
	
	EmailUser queryEmailUserByOwner(String ownerid, String domainid) throws Exception;
	
}
