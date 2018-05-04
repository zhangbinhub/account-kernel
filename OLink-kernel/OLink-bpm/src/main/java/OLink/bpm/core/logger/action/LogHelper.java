package OLink.bpm.core.logger.action;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityParent;
import OLink.bpm.core.superuser.ejb.SuperUserProcess;
import OLink.bpm.core.superuser.ejb.SuperUserVO;
import org.apache.log4j.Logger;

import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.activity.action.ActivityAction;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.logger.ejb.LogProcess;
import OLink.bpm.core.logger.ejb.LogVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;

import com.opensymphony.webwork.ServletActionContext;

public class LogHelper {

	private static final Logger logger = Logger.getLogger(LogHelper.class);

	public String getUserName(LogVO log) {
		try {
			if (log.getUser() == null) {
				SuperUserProcess sup = (SuperUserProcess) ProcessFactory.createProcess(SuperUserProcess.class);
				if (log.getSuperUser() != null) {
					SuperUserVO suser = (SuperUserVO) sup.doView(log.getSuperUser().getId());
					if (suser != null) {
						return suser.getName();
					}
				}
			} else {
				UserProcess up = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
				UserVO user = (UserVO) up.doView(log.getUser().getId());
				if (user != null) {
					return user.getName();
				}
			}
		} catch (Exception e) {
			logger.warn(e);
		}
		return "";
	}

	public String getWdatePickerLang(String lang) {
		if ("EN".equals(lang)) {
			return "en";
		} else if ("TW".equals(lang)) {
			return "zh-tw";
		} else if ("CN".equals(lang)) {
			return "zh-cn";
		}
		return "zh-cn";
	}

	/**
	 * 获取发起请求的客户端IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getRequestIp(HttpServletRequest request) {
		String ip = null;
		ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static void saveLogByDynaform(HttpServletRequest request, Document document, WebUser webUser) {
		try {
			Activity activity = (Activity) request.getAttribute(ActivityAction.REQUEST_ATTRIBUTE_ACTIVITY);
			if (webUser == null) {
				webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
				if (webUser == null) {
					webUser = new WebUser(null);
				}
			}
			saveLogByDynaform(activity, document, webUser);
		} catch (Exception e) {
			logger.warn(e);
		}
	}

	public static void saveLogByDynaform(Activity activity, Document document, WebUser webUser) {
		try {
			if (webUser == null || !webUser.isRecordLog()) {
				return;
			}
			String type = null;
			String description = "";
			if (activity != null) {
				type = activity.getName();
				description = type;
				
				ActivityParent parent = activity.getParent();
				if (parent != null) {
					description = "{*[" + parent.getSimpleClassName() + "]*}-" + parent.getName();
				}
			}
			
			String ip = LogHelper.getRequestIp(ServletActionContext.getRequest());
			LogVO logVo = LogVO.valueOf(webUser, type, description, ip);
			LogProcess logProcess = (LogProcess) ProcessFactory.createProcess(LogProcess.class);
			logProcess.doCreate(logVo);
		} catch (Exception e) {
			logger.warn(e);
		}
	}

}
