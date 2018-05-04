package OLink.bpm.base.action;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Web;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.portlet.context.PortletActionContext;

public abstract class BasePortletAction<E> extends BaseAction<E> {
	private static final Logger log = Logger.getLogger(BasePortletAction.class);

	public BasePortletAction(IDesignTimeProcess<E> process, ValueObject content) {
		super(process, content);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3228614580227173707L;

	private PortletSession getPortletSession() {
		return PortletActionContext.getRenderRequest().getPortletSession();
	}

	private PortletRequest getPortletRequest() {
		return PortletActionContext.getRenderRequest();
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
				log.warn("Set parameter: " + name + " failed, the value is: "
						+ value);
			}
		}
		// params.setHttpRequest(request);
	}

	public String getSessionid() {
		return getPortletSession().getId();
	}

	public String getMultiLanguage(String key, String defaultValue) {
		try {
			PortletSession session = getPortletSession();
			String language = (String) session
					.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
			return MultiLanguageProperty.getProperty(language, key,
					defaultValue);
		} catch (Exception e) {
			log.warn("Load multilanguage " + key + "error: " + e.getMessage());
		}
		return defaultValue;
	}
}
