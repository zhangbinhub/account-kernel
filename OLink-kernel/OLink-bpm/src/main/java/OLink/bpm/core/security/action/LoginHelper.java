package OLink.bpm.core.security.action;

import java.util.Random;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.core.logger.action.LogHelper;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.http.CookieUtil;
import org.apache.log4j.Logger;

import OLink.bpm.constans.Environment;
import OLink.bpm.constans.Web;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.email.email.action.EmailUserHelper;
import OLink.bpm.core.logger.ejb.LogProcess;
import OLink.bpm.core.logger.ejb.LogVO;
import OLink.bpm.core.sysconfig.ejb.AuthConfig;
import OLink.bpm.core.user.action.OnlineUserBindingListener;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.notification.ejb.sendmode.SMSModeProxy;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.util.property.PropertyUtil;
import eWAP.core.ResourcePool;

public class LoginHelper {

	private static final Logger log = Logger.getLogger(LoginHelper.class);

	public static void initWebUser(PortletRequest request, UserVO user,
			String application, String domainName) throws Exception {
		WebUser webUser = new WebUser(user);
		DomainVO domain = getDomain(webUser, domainName);
		// 更新默认应用
		updateDefaultApplicationToUser(user, webUser, domain, application);

		PortletSession session = request.getPortletSession();
		if (user.getUserSetup() != null) {
			if (user.getUserSetup().getUserSkin() != null
					&& !user.getUserSetup().getUserSkin().equals(""))
				session.setAttribute("SKINTYPE", user.getUserSetup()
						.getUserSkin(), PortletSession.APPLICATION_SCOPE);
		} else {
			session.setAttribute("SKINTYPE", domain.getSkinType(),
					PortletSession.APPLICATION_SCOPE);
		}
		webUser.setRecordLog(domain.getLog().booleanValue());
		webUser.setUserSetup(user.getUserSetup());

		session.setAttribute(Web.SESSION_ATTRIBUTE_DOMAIN, domain.getId(),
				PortletSession.APPLICATION_SCOPE);
		OnlineUserBindingListener oluser = new OnlineUserBindingListener(
				webUser);
		// session.setAttribute(Web.SESSION_ATTRIBUTE_APPLICATION,
		// webUser.getApplicationid(), PortletSession.APPLICATION_SCOPE);
		session.setAttribute(Web.SESSION_ATTRIBUTE_ONLINEUSER, oluser,
				PortletSession.APPLICATION_SCOPE);
		session.setAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER, webUser,
				PortletSession.APPLICATION_SCOPE);
		// 重新加载多语言
		MultiLanguageProperty.load(user.getApplicationid(), false);

		EmailUserHelper.loginEmailSystem(webUser);

		// 在环境中设置context path
		Environment.getInstance().setContextPath(request.getContextPath());
	}

	public static void initWebUser(HttpServletRequest request, UserVO user,
			String application, String domainName) throws Exception {
		WebUser webUser = new WebUser(user);
		DomainVO domain = getDomain(webUser, domainName);
		// 更新默认应用
		updateDefaultApplicationToUser(user, webUser, domain, application);

		HttpSession session = request.getSession();
		if (user.getUserSetup() != null) {
			if (user.getUserSetup().getUserSkin() != null
					&& !user.getUserSetup().getUserSkin().equals(""))
				session.setAttribute("SKINTYPE", user.getUserSetup()
						.getUserSkin());
		} else {
			session.setAttribute("SKINTYPE", domain.getSkinType());
		}
		webUser.setRecordLog(domain.getLog().booleanValue());
		webUser.setUserSetup(user.getUserSetup());

		// session.setAttribute(Web.SESSION_ATTRIBUTE_DOMAIN, domain.getId());
		// session.setAttribute(Web.SESSION_ATTRIBUTE_APPLICATION,
		// user.getApplicationid());
		OnlineUserBindingListener oluser = new OnlineUserBindingListener(
				webUser);
		session.setAttribute(Web.SESSION_ATTRIBUTE_ONLINEUSER, oluser);
		session.setAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER, webUser);
		MultiLanguageProperty.load(user.getApplicationid(), false);

		EmailUserHelper.loginEmailSystem(webUser);

		// 在环境中设置context path
		Environment.getInstance().setContextPath(request.getContextPath());
	}

	private static DomainVO getDomain(WebUser webUser, String domainName)
			throws Exception {
		DomainProcess domainProcess = (DomainProcess) ProcessFactory
				.createProcess(DomainProcess.class);
		DomainVO domain = null;
		if (!StringUtil.isBlank(webUser.getDomainid()))
			domain = (DomainVO) domainProcess.doView(webUser.getDomainid());
		else
			domain = domainProcess.getDomainByName(domainName);

		return domain;
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
	private static void updateDefaultApplicationToUser(UserVO user,
			WebUser webUser, DomainVO domain, String application)
			throws Exception {
		UserProcess process = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);

		// 更新默认应用
		String applicationid = getDefaultApplication(webUser, domain,
				application);
		process.doUpdateDefaultApplication(user.getId(), applicationid);
		user.setApplicationid(applicationid);
		user.setDefaultApplication(applicationid);
		// webUser.setApplicationid(applicationid);
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
	private static String getDefaultApplication(WebUser webUser,
			DomainVO domain, String application) throws Exception {
		String rtn = application;
		if (StringUtil.isBlank(application)) {
			ApplicationProcess appProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			ApplicationVO appvo = appProcess.getDefaultApplication(
					webUser.getDefaultApplication(), webUser);
			if (domain.getApplications().contains(appvo)) {// 如果默认应用包含于域中
				rtn = appvo.getId();
			} else {
				if (!domain.getApplications().isEmpty()) {
					rtn = ((ApplicationVO) domain.getApplications().toArray()[0])
							.getId(); // 默认进入域中的第一个应用
				}
			}
		}
		return rtn;
	}

	/**
	 * 注销前台用户
	 * <p>
	 * 调用该方法无法保存注销操作日志，SSO相关操作
	 * </p>
	 * 
	 * @deprecated 旧版本方法，已掉弃
	 * @param session
	 */
	public static void logout(HttpSession session) {
		EmailUserHelper.logoutEmailSystem(session);
		session.removeAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		clearOtehr(session);
		if(session!=null) session.invalidate();//By XGY
	}

	/**
	 * 注销前台用户
	 * 
	 * @param request
	 * @param response
	 */
	public static void logout(HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		EmailUserHelper.logoutEmailSystem(session);
		LoginHelper.saveLogoutLog(request);
		session.removeAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		clearOtehr(session);
		clearSSO(response);
		ResourcePool.ClearResource(session);//By XGY
	}

	/**
	 * 注销后台用户
	 * 
	 * @param session
	 */
	public static void logoutAdmin(HttpSession session) {
		session.removeAttribute(Web.SESSION_ATTRIBUTE_USER);
		clearOtehr(session);
		if(session!=null) session.invalidate();//By XGY
	}

	/**
	 * 清空单点登录Cookie
	 * 
	 * @param response
	 */
	private static void clearSSO(HttpServletResponse response) {
		CookieUtil.clearCookie(PropertyUtil.get("sso.info.key.loginAccount"),
				response);
		CookieUtil.clearCookie(PropertyUtil.get("sso.info.key.domainName"),
				response);
		CookieUtil.clearSSO(response);
	}

	private static void clearOtehr(HttpSession session) {
		session.removeAttribute(Web.SKIN_TYPE);
		session.removeAttribute(Web.SESSION_ATTRIBUTE_DOMAIN);
		session.removeAttribute(Web.SESSION_ATTRIBUTE_APPLICATION);
		session.removeAttribute(Web.SESSION_ATTRIBUTE_ONLINEUSER);
	}

	private static void saveLogoutLog(HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			WebUser webUser = (WebUser) session
					.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
			if (webUser == null || !webUser.isRecordLog()) {
				return;
			}
			String ip = LogHelper.getRequestIp(request);
			LogVO logVO = LogVO.valueOf(webUser, "{*[Logout]*}",
					"{*[Logout]*}{*[System]*}", ip);
			LogProcess logProcess = (LogProcess) ProcessFactory
					.createProcess(LogProcess.class);
			logProcess.doCreate(logVO);
			log.info("Logout system: loginno=" + webUser.getLoginno() + ", ip="
					+ ip);
		} catch (Exception e) {
			log.warn("Save logout log error: " + e.toString());
		}
	}

	/**
	 * 是否需要手机短信验证
	 * 
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public static boolean smsVerificationRequired(String ip) throws Exception{
		try{
		PropertyUtil.reload("sso");
		String smsAuthenticate = PropertyUtil.get(AuthConfig.SMS_AUTHENTICATE);
		String smsAffectMode = PropertyUtil.get(AuthConfig.SMS_AFFECTMODE);
		String smssRangeIp = PropertyUtil.get(AuthConfig.SMS_STARTRANGEIP)+"-"+PropertyUtil.get(AuthConfig.SMS_ENDRANGEIP);
		
		
		
		if("true".equals(smsAuthenticate)){
			boolean ipIsValid = ipIsValid(smssRangeIp,ip);
			if("all".equals(smsAffectMode)){
				return true;
			}else if("match".equals(smsAffectMode) && ipIsValid){
				return true;
			}else if("exclude".equals(smsAffectMode) && !ipIsValid){
				return true;
			}
		}
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
		return false;
	}
	
	/**
	 * 获取客户端真实IP地址
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {  
	       String ip = request.getHeader("x-forwarded-for");  
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	           ip = request.getHeader("Proxy-Client-IP");  
	       }  
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	           ip = request.getHeader("WL-Proxy-Client-IP");  
	       }  
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
	           ip = request.getRemoteAddr();  
	       }  
	       return ip;  
	   }   

	/**
	 * 验证ip是否在给定的ip断内
	 * @param ipSection
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	public static boolean ipIsValid(String ipSection, String ip) throws Exception{
		if (ipSection == null)
			throw new NullPointerException("IP段不能为空！");
		if (ip == null)
			throw new NullPointerException("IP不能为空！");
		ipSection = ipSection.trim();
		ip = ip.trim();
		final String REGX_IP = "((25[0-5]|2[0-4]//d|1//d{2}|[1-9]//d|//d)//.){3}(25[0-5]|2[0-4]//d|1//d{2}|[1-9]//d|//d)";
		final String REGX_IPB = REGX_IP + "//-" + REGX_IP;
//		if (!ipSection.matches(REGX_IPB) || !ip.matches(REGX_IP))
//			return false;
		int idx = ipSection.indexOf('-');
		String[] sips = ipSection.substring(0, idx).split("\\.");
		String[] sipe = ipSection.substring(idx + 1).split("\\.");
		String[] sipt = ip.split("\\.");
		long ips = 0L, ipe = 0L, ipt = 0L;
		for (int i = 0; i < 4; ++i) {
			ips = ips << 8 | Integer.parseInt(sips[i]);
			ipe = ipe << 8 | Integer.parseInt(sipe[i]);
			ipt = ipt << 8 | Integer.parseInt(sipt[i]);
		}
		if (ips > ipe) {
			long t = ips;
			ips = ipe;
			ipe = t;
		}
		return ips <= ipt && ipt <= ipe;
	}
	
	/**
	 * 发送手机验证码短信
	 * @param user
	 * @param request
	 * @return
	 * 	超时时间
	 * @throws Exception
	 */
	public static int sendSMSCheckCode(WebUser user, HttpServletRequest request) throws Exception{
		int timeout = 0;
		try {
			
			PropertyUtil.reload("sso");
			timeout = Integer.parseInt(PropertyUtil.get(AuthConfig.SMS_TIMEOUT));
			String checkCode = getRandomCode(5);
			String content = PropertyUtil.get(AuthConfig.SMS_CONTENT).replace("$", checkCode);
			
			request.getSession().setAttribute(Web.SESSION_ATTRIBUTE_SMSCHECKCODE, checkCode);
			SMSModeProxy sender = new SMSModeProxy(user);
			
			sender.send("", content, user.getTelephone(), null, false);
		
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return timeout;
		
	}
	
	public static String getRandomCode(int length){
		StringBuffer code = new StringBuffer();
		String[] cos = new String[]{"0","1","2","3","4","5","6","7","8","9"};
		for(int i=0;i<length;i++){
			code.append(cos[new Random().nextInt(9)]);
		}
		return code.toString();
	}
}
