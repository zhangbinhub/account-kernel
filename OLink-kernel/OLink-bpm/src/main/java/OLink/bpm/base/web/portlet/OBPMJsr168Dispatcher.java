package OLink.bpm.base.web.portlet;

import java.util.Map;

import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import OLink.bpm.constans.Web;
import OLink.bpm.core.sso.SSOUtil;
import OLink.bpm.core.sso.SSOException;
import OLink.bpm.util.property.PropertyUtil;

import com.opensymphony.webwork.dispatcher.mapper.ActionMapping;
import com.opensymphony.webwork.portlet.dispatcher.Jsr168Dispatcher;

public class OBPMJsr168Dispatcher extends Jsr168Dispatcher {
	
	@SuppressWarnings("unchecked")
	@Override
	public void serviceAction(PortletRequest request, PortletResponse response, ActionMapping mapping, Map requestMap,
			Map parameterMap, Map sessionMap, Map applicationMap, String portletNamespace, Integer phase)
			throws PortletException {
		
		try {
			// 单点登录实现
			SSOUtil ssoUtil = new SSOUtil();
			if (Web.AUTHENTICATION_TYPE_SSO.equals(PropertyUtil.get(Web.AUTHENTICATION_TYPE))) {
				ssoUtil.checkSSO(request, response);
			}
		} catch (SSOException e) {
			throw new PortletException(e.getMessage());
		}
		
		
		super.serviceAction(request, response, mapping, requestMap, parameterMap, sessionMap, applicationMap, portletNamespace,
				phase);
	}
}
