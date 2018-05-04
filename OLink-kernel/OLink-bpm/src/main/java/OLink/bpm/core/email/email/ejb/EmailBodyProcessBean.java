package OLink.bpm.core.email.email.ejb;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.email.email.dao.EmailBodyDAO;
import OLink.bpm.core.email.util.Constants;

public class EmailBodyProcessBean extends AbstractDesignTimeProcessBean<EmailBody> implements
		EmailBodyProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6863199658668569708L;

	protected IDesignTimeDAO<EmailBody> getDAO() throws Exception {
		return (EmailBodyDAO) DAOFactory.getDefaultDAO(EmailBody.class.getName());
	}
	
	@Override
	public void doCreate(ValueObject vo) throws Exception {
		EmailBody body = (EmailBody) vo;
		checkAddress(body);
		super.doCreate(body);
	}
	
	private void checkAddress(EmailBody emailBody) {
		//if (EmailConfig.isInternalEmail()) {
			
		//}
		if (emailBody == null) {
			return;
		}
		emailBody.setFrom(Constants.emailAddress2Account(emailBody.getFrom()));
		emailBody.setBcc(changeAddress(emailBody.getBcc()));
		emailBody.setCc(changeAddress(emailBody.getCc()));
		emailBody.setTo(changeAddress(emailBody.getTo()));
	}
	
	private String changeAddress(String address) {
		if (address == null) {
			return null;
		}
		String[] addresss = address.split(";");
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < addresss.length; i++) {
			if (buffer.length() > 1) {
				buffer.append(";").append(Constants.emailAddress2Account(addresss[i]));
			} else {
				buffer.append(Constants.emailAddress2Account(addresss[i]));
			}
		}
		return buffer.toString();
	}

	@Override
	public void doUpdate(ValueObject vo) throws Exception {
		EmailBody body = (EmailBody) vo;
		checkAddress(body);
		super.doUpdate(body);
	}

}
