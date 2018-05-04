package OLink.bpm.core.email.email.ejb;

import java.util.Date;

import OLink.bpm.core.email.email.dao.EmailUserDAO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import eWAP.core.Tools;

public class EmailUserProcessBean extends AbstractDesignTimeProcessBean<EmailUser> implements
		EmailUserProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8921925024606594233L;

	protected IDesignTimeDAO<EmailUser> getDAO() throws Exception {
		return (EmailUserDAO) DAOFactory.getDefaultDAO(EmailUser.class.getName());
	}
	
	public EmailUser getEmailUser(String account, String domainid)
			throws Exception {
		return ((EmailUserDAO)getDAO()).queryEmailUser(account, domainid);
	}

	public EmailUser getEmailUserByAccount(String account) throws Exception {
		return ((EmailUserDAO)getDAO()).queryEmailUserByAccount(account);
	}

	public DataPackage<EmailUser> getEmailUsers(String domainid, ParamsTable params)
			throws Exception {
		return ((EmailUserDAO)getDAO()).queryEmailUsers(domainid, params);
	}
	
	public static void main(String[] args) {
		try {
			EmailUserProcess processBean = (EmailUserProcess) ProcessFactory.createProcess(EmailUserProcess.class);
			EmailUser user = new EmailUser();
			user.setAccount("tom");
			user.setName("tom");
			user.setPassword("123456");
			processBean.doCreate(user);
			
			EmailUser user2 = new EmailUser();
			user2.setAccount("tao");
			user2.setName("tao");
			user2.setPassword("123456");
			processBean.doCreate(user2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public EmailUser getEmailUserByOwner(String ownerid, String domainid)
			throws Exception {
		return ((EmailUserDAO)getDAO()).queryEmailUserByOwner(ownerid, domainid);
	}

	public void doCreateEmailUser(EmailUser user) throws Exception {
		super.doCreate(user);
	}
	
	@Override
	public void doCreate(ValueObject vo) throws Exception {
		EmailUser user = (EmailUser) vo;
		if (user.getPassword() != null && user.getPassword().trim().length() > 0) {
			user.setPassword(Tools.encodeToBASE64(user.getPassword()));
		}
		user.setCreateDate(new Date());
		super.doCreate(vo);
	}

	public void doRemoveEmailUser(String id) throws Exception {
		super.doRemove(id);
	}

	public void doUpdateEmailUser(EmailUser user) throws Exception {
		super.doUpdate(user);
	}
	
	@Override
	public void doUpdate(ValueObject vo) throws Exception {
		EmailUser user = (EmailUser) vo;
		try {
			PersistenceUtils.beginTransaction();

			EmailUser po = (EmailUser) getDAO().find(vo.getId());
			if (po != null) {
				if (user.getPassword() != null && user.getPassword().trim().length() > 0) {
					String base64 = Tools.encodeToBASE64(user.getPassword());
					user.setPassword(base64);
					po.setPassword(base64);
				} else {
					//user.setPassword(((EmailUser)po).getPassword());
				}
				po.setAccount(user.getAccount());
				//if (Web.DEFAULT_SHOWPASSWORD.equals(user.getPassword())) {
				//	user.setPassword(((EmailUser)po).getPassword());
				//} else {
				//	user.setPassword(Security.encodeToBASE64(user.getPassword()));
				//}
				//PropertyUtils.copyProperties(po, user);
				getDAO().update(po);
			} else {
				if (user.getPassword() != null && user.getPassword().trim().length() > 0) {
					user.setPassword(Tools.encodeToBASE64(user.getPassword()));
				}
				getDAO().update(user);
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}

}
