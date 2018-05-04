package OLink.bpm.core.security;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.core.security.action.LoginHelper;
import OLink.bpm.core.email.email.action.EmailUserHelper;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;

import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.OBPMSessionContext;

/**
 * 
 * @author Tom
 *
 */
public class CheckCookieHelper {

	public static final String UUID = "uuid";

	/**
	 * @SuppressWarnings getParameterNames不支持泛型
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void initJump(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		String sessionid = request.getParameter("jsessionid");
		if (StringUtil.isBlank(sessionid)) {
			sessionid = request.getParameter("JSESSIONID");
		}
		if (StringUtil.isBlank(sessionid)) {
			// clientKey是为兼用旧版本
			sessionid = request.getParameter("clientKey");
		}
		if (StringUtil.isBlank(sessionid)) {
			response.sendRedirect(request.getContextPath() + "/portal/share/security/login.jsp");
			return;
		}
		String page = request.getParameter("page");
		if (StringUtil.isBlank(page)) {
			// requestPage是为兼用旧版本
			page = request.getParameter("requestPage");
		}
		String application = request.getParameter("application");
		if (StringUtil.isBlank(application)) {
			// applicationId是为兼用旧版本
			application = request.getParameter("applicationId");
		}

		HttpSession oldSession = OBPMSessionContext.getInstance().getSession(sessionid);
		//copySessionParameter(session, oldSession);
		CheckCookieHelper.initLogin(request, oldSession, application);
		
		String uuid = Tools.getUUID();
		StringBuffer requestPage = new StringBuffer();
		
		if (!StringUtil.isBlank(page)) {
			Enumeration en = request.getParameterNames();
			requestPage.append(page);
			while (en.hasMoreElements()) {
				String key = (String) en.nextElement();
				String parameter = request.getParameter(key);
				if (key.equals("clientKey") || key.equals("requestPage")
						|| key.equals("applicationId") || key.equalsIgnoreCase("jsessionid")
						 || key.equalsIgnoreCase("page") || key.equalsIgnoreCase("application")) {
					continue;
				}
				if (!StringUtil.isBlank(parameter) && requestPage.indexOf(key) < 0) {
					if (requestPage.indexOf("?") >= 0) {
						requestPage.append("&").append(key).append("=").append(parameter);
					} else {
						requestPage.append("?").append(key).append("=").append(parameter);
					}
				}
			}
			requestPage.append("&application=").append(application);
			OBPMParameter oParameter = new OBPMParameter();
			oParameter.setPage(requestPage.toString());
			session.setAttribute(uuid, oParameter);
		}
		
		String urlParameter = "?returnUrl=&application=" + application + "&" + UUID + "=" + uuid;
		response.sendRedirect(request.getContextPath() + "/portal/dispatch/main.jsp" + urlParameter);
	}

	private static void initLogin(HttpServletRequest request,
			HttpSession oldSession, String application) throws Exception {
		if (oldSession == null) {
			throw new Exception("Can't login by the user");
		}
		EmailUserHelper.logoutEmailSystem(request.getSession());
		WebUser webUser = (WebUser) oldSession.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		if (webUser == null) {
			throw new Exception("Can't login by the user");
		}
		UserProcess process = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
		UserVO user = (UserVO) process.doView(webUser.getId());
		LoginHelper.initWebUser(request, user, application, null);
		request.getSession().setAttribute(Web.SKIN_TYPE, "default");
	}

	/**
	 * @SuppressWarnings getAttributeNames不支持泛型
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void copySessionParameter(HttpSession newSession, HttpSession oldSession) throws Exception {
		if (oldSession == null) {
			throw new Exception("Can't login by the user");
		}
		EmailUserHelper.logoutEmailSystem(newSession);
		Enumeration enumeration = newSession.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String element = (String) enumeration.nextElement();
			newSession.removeAttribute(element);
		}
		enumeration = oldSession.getAttributeNames();
		while (enumeration.hasMoreElements()) {
			String element = (String) enumeration.nextElement();
			newSession.setAttribute(element, oldSession.getAttribute(element));
		}
	}
	
	public static String getRequestPage(HttpServletRequest request) {
		String uuid = request.getParameter(UUID);
		if (StringUtil.isBlank(uuid)) {
			return null;
		}
		HttpSession session = request.getSession();
		OBPMParameter parameter = (OBPMParameter) session.getAttribute(uuid);
		if (parameter == null) {
			return null;
		}
		String page = parameter.getPage();
		if (!StringUtil.isBlank(page)) {
			if (!page.startsWith("http://")) {
				if (page.startsWith("/")) {
					page = page.substring(1);
				}
			}
			String context = request.getContextPath();
			page = page.replaceAll(context + context, context);
			//page = page.replaceAll("http:/", "http://");
			//page = page.replaceAll("http:///", "http://");
		}
		removeRequestPage(request);
		return page;
	}
	
	public static void removeRequestPage(HttpServletRequest request) {
		String uuid = request.getParameter(UUID);
		if (!StringUtil.isBlank(uuid)) {
			HttpSession session = request.getSession();
			session.removeAttribute(uuid);
		}
	}
	
}

class OBPMParameter {
	
	public String page = null;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
	
}
