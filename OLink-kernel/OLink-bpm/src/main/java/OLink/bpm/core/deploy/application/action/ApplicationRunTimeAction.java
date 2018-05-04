package OLink.bpm.core.deploy.application.action;

import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.constans.Web;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.property.MultiLanguageProperty;

public class ApplicationRunTimeAction extends ApplicationAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1781227401370916693L;

	public ApplicationRunTimeAction() throws ClassNotFoundException {
		super();
	}
	
	/**
	 * @SuppressWarnings 工厂方法无法使用泛型
	 * @return
	 */
	public String doChange() {
		try {
			WebUser webUser = getUser();
			String userid = webUser.getId();
			
			UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			// 更新默认应用
			userProcess.doUpdateDefaultApplication(userid, getId());
			//webUser.setApplicationid(getId());
			webUser.setDefaultApplication(getId());

			MultiLanguageProperty.load(getId(), false);
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
		}

		return SUCCESS;
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}
}
