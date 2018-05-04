package OLink.bpm.core.email.email.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.email.email.dao.EmailUserDAO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;

public class EmailUserProcessImapBean extends AbstractDesignTimeProcessBean<EmailUser> implements
		EmailUserProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8921925024606594234L;

	protected IDesignTimeDAO<EmailUser> getDAO() throws Exception {
		return (EmailUserDAO) DAOFactory.getDefaultDAO(EmailUser.class.getName());
	}
	
	public EmailUser getEmailUser(String account, String domainid)
			throws Exception {
		return null;
	}

	public EmailUser getEmailUserByAccount(String account) throws Exception {
		return null;
	}

	public DataPackage<EmailUser> getEmailUsers(String domainid, ParamsTable params)
			throws Exception {
		return null;
	}
	
	public static void main(String[] args) {
		try {
			EmailUserProcess processBean = (EmailUserProcess) ProcessFactory.createProcess(EmailUserProcess.class);
			EmailUser user = new EmailUser();
			user.setAccount("tom");
			user.setName("tom");
			user.setPassword("123456");
			processBean.doCreateEmailUser(user);
			
			EmailUser user2 = new EmailUser();
			user2.setAccount("tao");
			user2.setName("tao");
			user2.setPassword("123456");
			processBean.doCreateEmailUser(user2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EmailUser getEmailUserByOwner(String ownerid, String domainid)
			throws Exception {
		//EmailUser user = new EmailUser();
		//user.setAccount("tom");
		//user.setName("tom");
		//user.setPassword("123456");
		return null;
	}

	public void doCreateEmailUser(EmailUser user) throws Exception {
		
	}

	public void doRemoveEmailUser(String id) throws Exception {
		
	}

	public void doUpdateEmailUser(EmailUser user) throws Exception {
		
	}

}
