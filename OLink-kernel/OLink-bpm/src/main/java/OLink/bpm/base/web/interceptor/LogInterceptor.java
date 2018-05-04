package OLink.bpm.base.web.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;
import OLink.bpm.core.logger.action.LogHelper;
import OLink.bpm.core.logger.ejb.LogProcess;
import OLink.bpm.core.logger.ejb.LogVO;
import OLink.bpm.core.security.action.LoginAction;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * 日志拦截器
 * <p>
 * 所有webworld配置文件里配置到LogInterceptor拦截器，都会向日志表里插入日志记录
 * </p>
 * 配置样式：
 * <pre>
 * &lt;interceptor-ref name="logInterceptor"&gt;
 * 		&lt;param name="type"&gt;{*[Login]*}&lt;/param&gt;
 * 		&lt;param name="description"&gt;{*[i18n key]*}&lt;/param&gt;
 * &lt;/interceptor-ref&gt;
 * </pre>
 * 
 * @author Tom
 * 
 */
public class LogInterceptor implements Interceptor {

	private static final long serialVersionUID = 8783136098068337805L;
	
	private static final Logger LOG = Logger.getLogger(LogInterceptor.class);

	private String description = null;
	private String type = null;
	
	public void destroy() {
		//LOG.info("LogInterceptor.destroy().");
	}

	public void init() {
		//LOG.info("LogInterceptor.init().");
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		LogProcess process = (LogProcess) ProcessFactory.createProcess(LogProcess.class);
		try {
			String result = invocation.invoke();
			WebUser webUser = getWebUser(invocation);
			if (webUser != null && webUser.isRecordLog()) {
				LogVO log = getLogVO(invocation, webUser);
				process.doCreate(log);
			}
			return result;
		} catch (Exception e) {
			//LOG.warn(e);
			throw e;
		}
	}
	
	/**
	 * 
	 * @param invocation
	 * @return
	 * @throws Exception 
	 */
	private LogVO getLogVO(ActionInvocation invocation, WebUser webUser) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		String ip = LogHelper.getRequestIp(request);
		return LogVO.valueOf(webUser, getType(), getDescription(), ip);
	}
	
//	/**
//	 * @param object
//	 * @return
//	 */
//	private String getStringValueByStringArray(Object object) {
//		if (object instanceof String[]) {
//			String[] strs = (String[]) object;
//			for (int i = 0; i < strs.length; i++) {
//				String temp = strs[i];
//				if (!StringUtil.isBlank(temp)) {
//					return temp;
//				}
//			}
//		}
//		return null;
//	}
	
	/**
	 * @SuppressWarnings session不支持泛型
	 */
	@SuppressWarnings("unchecked")
	private WebUser getWebUser(ActionInvocation inv) throws Exception {
		// Update zb 2015-01-08
		// Map session = inv.getInvocationContext().getSession();
		HttpSession session = ServletActionContext.getRequest().getSession();
		Object object = inv.getAction();
		String webUserSessionKey = Web.SESSION_ATTRIBUTE_FRONT_USER;
		boolean isGetByAction = false;
		if (object instanceof OLink.bpm.core.admin.action.LoginAction) {
			webUserSessionKey = Web.SESSION_ATTRIBUTE_USER;
			isGetByAction = true;
		} else if (object instanceof LoginAction) {
			//webUserSessionKey = Web.SESSION_ATTRIBUTE_FRONT_USER;
			isGetByAction = true;
		} else {
			try {
				Method method = object.getClass().getMethod("getWebUserSessionKey");
				Object result = method.invoke(object);
				if (result instanceof String) {
					webUserSessionKey = (String) result;
					isGetByAction = true;
				}
			} catch (SecurityException e) {
				webUserSessionKey = Web.SESSION_ATTRIBUTE_FRONT_USER;
				LOG.warn("LogInterceptor.getLogVO(ActionInvocation invocation).SecurityException:" + e.getMessage());
			} catch (NoSuchMethodException e) {
				webUserSessionKey = Web.SESSION_ATTRIBUTE_FRONT_USER;
				LOG.warn("LogInterceptor.getLogVO(ActionInvocation invocation): Can't find method 'getWebUserSessionKey()'.");
			} catch (Exception e) {
				webUserSessionKey = Web.SESSION_ATTRIBUTE_FRONT_USER;
				LOG.warn("LogInterceptor.getLogVO(ActionInvocation invocation).Exception:" + e.getMessage());
			}
		}
		// Update zb 2015-01-08
		WebUser user = null;
		if (session != null) {
			user = (WebUser) session.getAttribute(webUserSessionKey);
		}
		if (user == null && !isGetByAction) {
			// Update XGY 2012.11.29
			// Update zb 2015-01-08
			webUserSessionKey = webUserSessionKey.equals(Web.SESSION_ATTRIBUTE_FRONT_USER) ? Web.SESSION_ATTRIBUTE_USER : Web.SESSION_ATTRIBUTE_FRONT_USER;
			user = (WebUser) session.getAttribute(webUserSessionKey);
		}
		//if (user == null) {
		//	user = new WebUser(null);
		//	LOG.warn("LogInterceptor.getLogVO(ActionInvocation invocation): Cant't set log operator.");
		//}
		return user;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
}
