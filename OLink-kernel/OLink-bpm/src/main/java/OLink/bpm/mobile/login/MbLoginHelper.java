package OLink.bpm.mobile.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.constans.Web;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.property.MultiLanguageProperty;

public class MbLoginHelper {

	public static WebUser initLogin(HttpServletRequest request, UserVO user) throws Exception {
		
		WebUser webUser = new WebUser(user);
		
		DomainProcess domainProcess = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
		DomainVO domain  = (DomainVO) domainProcess.doView(webUser.getDomainid());

		updateDefaultApplicationToUser(user, webUser, domain, user.getDefaultApplication());

		HttpSession session = request.getSession();

		String language = request.getParameter("language");
		if (StringUtil.isBlank(language)) {
			language = MultiLanguageProperty.getName(2);
		}
		session.setAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE, language);
		// session.setMaxInactiveInterval(20 * 60); // 20 minutes
		session.setAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER, webUser);
		return webUser;
	}
	
	/**
	 * 为用户更新默认软件
	 * 
	 * @param user
	 * @param webUser
	 * @param domain
	 * @param application
	 * @throws Exception
	 */
	public static void updateDefaultApplicationToUser(UserVO user, WebUser webUser, DomainVO domain, String application)
			throws Exception {
		UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);

		// 更新默认应用
		String applicationid = getDefaultApplication(webUser, domain, application);
		process.doUpdateDefaultApplication(user.getId(), applicationid);
		user.setApplicationid(applicationid);
		webUser.setDefaultApplication(applicationid);
	}

	/**
	 * 获取默认软件
	 * 
	 * @param webUser
	 * @param domain
	 * @param application
	 * @return
	 * @throws Exception
	 */
	private static String getDefaultApplication(WebUser webUser, DomainVO domain, String application) throws Exception {
		String rtn = application;
		if (StringUtil.isBlank(application)) {
			ApplicationProcess appProcess = (ApplicationProcess) ProcessFactory.createProcess(ApplicationProcess.class);
			ApplicationVO appvo = appProcess.getDefaultApplication(webUser.getDefaultApplication(), webUser);
			if (domain.getApplications().contains(appvo)) {// 如果默认应用包含于域中
				rtn = appvo.getId();
			} else {
				if (!domain.getApplications().isEmpty()) {
					rtn = ((ApplicationVO) domain.getApplications().toArray()[0]).getId(); // 默认进入域中的第一个应用
				}
			}
		}
		return rtn;
	}
	
}
