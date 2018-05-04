package OLink.bpm.core.shortmessage.submission.action;

import OLink.bpm.constans.Web;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageProcess;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;
import OLink.bpm.core.workflow.notification.ejb.sendmode.SMSModeProxy;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.user.action.WebUser;

/**
 * @SuppressWarnings 不支持泛型
 * @author Administrator
 *
 */
@SuppressWarnings("unchecked")
public class SubmitMessageAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2199658886883473982L;
	private String domain;

	public SubmitMessageAction() throws Exception {
		super(ProcessFactory.createProcess(SubmitMessageProcess.class), new SubmitMessageVO());
	}

	public String doSave() {
		try {
			SubmitMessageVO vo = (SubmitMessageVO) getContent();
			WebUser user = getUser();

			
			if (user != null && user.getTelephone() != null && vo.getSender() == null) {
				vo.setSender(user.getTelephone());
			}
			if (!vo.getSubmission()) {
				SMSModeProxy sender = new SMSModeProxy(user);
				String receiver = vo.getReceiver();
				String sendtel = user.getTelephone();
				if (sendtel != null && sendtel.trim().length() > 0) {
					if (receiver != null) {
						vo.setSender(sendtel);
						sender.send(vo);
						setContent(new SubmitMessageVO());
					} else {
						this.addFieldError("1", "{*[core.shortmessage.norecvlist]*}");
						return INPUT;
					}
				} else {
					this.addFieldError("1", "{*[core.shortmessage.nosender]*}");
					return INPUT;
				}
			}
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String doList() {
		try {
			WebUser user = getUser();
			ParamsTable params = getParams();
			params.setParameter("s_domainid", user.getDomainid());
			setDatas(((SubmitMessageProcess) process).list(user, params));
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

	public void set_needReply(String value) {
		SubmitMessageVO vo = (SubmitMessageVO) getContent();
		if (value != null && value.trim().equals("true")) {
			vo.setNeedReply(true);
		} else {
			vo.setNeedReply(false);
		}
	}

	public String get_msgType() {
		SubmitMessageVO vo = (SubmitMessageVO) getContent();
		return "" + vo.getContentType();
	}

	public void set_msgType(String type) {
		if (type != null) {
			SubmitMessageVO vo = (SubmitMessageVO) getContent();
			vo.setContentType(Integer.parseInt(type.trim()));
		}
	}

	public String get_needReply() {
		SubmitMessageVO vo = (SubmitMessageVO) getContent();
		if (vo.isNeedReply()) {
			return "true";
		} else {
			return "false";
		}
	}

	public void set_mass(String value) {
		SubmitMessageVO vo = (SubmitMessageVO) getContent();
		if (value != null && value.trim().equals("true")) {
			vo.setMass(true);
		} else {
			vo.setMass(false);
		}
	}

	public String get_mass() {
		SubmitMessageVO vo = (SubmitMessageVO) getContent();
		if (vo.isMass()) {
			return "true";
		} else {
			return "false";
		}
	}

	public String getDomain() {
		if (domain != null && domain.trim().length() > 0) {
			return domain;
		} else {
			return (String) getContext().getSession().get(Web.SESSION_ATTRIBUTE_DOMAIN);
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
		getContent().setDomainid(domain);
	}
}
