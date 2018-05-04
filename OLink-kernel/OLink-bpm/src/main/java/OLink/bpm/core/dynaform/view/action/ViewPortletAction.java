package OLink.bpm.core.dynaform.view.action;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.property.MultiLanguageProperty;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.portlet.context.PortletActionContext;

public class ViewPortletAction extends ViewRunTimeAction {
	private static final Logger LOG = Logger.getLogger(ViewPortletAction.class);

	public ViewPortletAction() throws ClassNotFoundException {
		super();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4794418883215417622L;

	/**
	 * 显示视图view数据列表(前台调用)
	 * 
	 * @return result.
	 * @throws Exception
	 */
	public String doDisplayView() throws Exception {
		try {
			return getSuccessResult(view, DO_DISPLAY_VIEW);
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null) {
				addFieldError("", e.getMessage());
			} else {
				addFieldError("errorMessage", e.toString());
			}
			return getInputResult(view);
		}
	}

	@SuppressWarnings("unchecked")
	protected void putRequestParameters() {
		PortletRequest request = getPortletRequest();
		Map m = request.getParameterMap();
		// realPath 如何获取
		// String realPath = getSession().getServletContext().getRealPath("/");
		// params.setParameter("realPath", realPath);
		params.setContextPath(request.getContextPath());
		Iterator<Entry<String, Object>> iter = m.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = iter.next();
			String name = entry.getKey();
			Object value = entry.getValue();
			try {
				// If there is only one string in the string array, the put the
				// string only, not array.
				if (value instanceof String[])
					if (((String[]) value).length > 1)
						params.setParameter(name, value);
					else
						params.setParameter(name, ((String[]) value)[0]);
				else
					params.setParameter(name, value);
			} catch (Exception e) {
				LOG.warn("Set parameter: " + name + " failed, the value is: " + value);
			}
		}
		// params.setHttpRequest(request);
	}

	private PortletSession getPortletSession() {
		return PortletActionContext.getRequest().getPortletSession();
	}

	private PortletRequest getPortletRequest() {
		return PortletActionContext.getRequest();
	}

	/**
	 * 从Session中获取用户
	 */
	public WebUser getUser() throws Exception {
		PortletSession session = getPortletSession();
		WebUser user = (WebUser) session.getAttribute(getWebUserSessionKey(), PortletSession.APPLICATION_SCOPE);

		if (user == null){
			user = getAnonymousUser();
		}
		
		return user;
	}

	/**
	 * 获取Session ID
	 */
	public String getSessionid() {
		return getPortletSession().getId();
	}

	/**
	 * 从Session中获取多语言设置
	 */
	public String getMultiLanguage(String key, String defaultValue) {
		try {
			PortletSession session = getPortletSession();
			String language = (String) session.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
			return MultiLanguageProperty.getProperty(language, key, defaultValue);
		} catch (Exception e) {
			LOG.warn("Load multilanguage " + key + "error: " + e.getMessage());
		}
		return defaultValue;
	}
}
