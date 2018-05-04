package OLink.bpm.core.sso.autherticator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.constans.Web;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.http.CookieUtil;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.util.property.PropertyUtil;
import eWAP.core.Tools;

public class AbstractLoginAuthenticator  {
	protected UserProcess userProcess;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String defaultApplication;

	public UserProcess getUserProcess() {
		return userProcess;
	}

	public String getDefaultApplication() {
		return defaultApplication;
	}

	public void setDefaultApplication(String defaultApplication) {
		this.defaultApplication = defaultApplication;
	}
	
	public AbstractLoginAuthenticator() {
	}

	public AbstractLoginAuthenticator(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			this.request = request;
			this.response = response;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置信息到cookie或session中
	 * 
	 * @param userVO
	 * @param domainName
	 */
	protected void saveInfo(UserVO userVO, String domainName) {
		if ("cookie".equals(PropertyUtil.get(Web.SSO_INFO_SAVE_TYEP))) {
			CookieUtil.setCookie(PropertyUtil.get(Web.SSO_INFO_KEY_LGOINACCOUNT), Tools
					.encodeToBASE64(userVO.getLoginno()), response);
			CookieUtil.setCookie(PropertyUtil.get(Web.SSO_INFO_KEY_DOMAINNAME), Tools
					.encodeToBASE64(domainName), response);
		} else if ("session".equals(PropertyUtil.get(Web.SSO_INFO_SAVE_TYEP))) {
			request.getSession().setAttribute(PropertyUtil.get(Web.SSO_INFO_KEY_LGOINACCOUNT),
					Tools.encodeToBASE64(userVO.getLoginno()));
			request.getSession().setAttribute(PropertyUtil.get(Web.SSO_INFO_KEY_DOMAINNAME),
					Tools.encodeToBASE64(domainName));
		}
	}
}
