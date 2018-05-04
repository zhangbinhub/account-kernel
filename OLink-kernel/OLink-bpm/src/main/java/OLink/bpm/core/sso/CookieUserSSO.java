package OLink.bpm.core.sso;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.util.http.CookieUtil;
import OLink.bpm.constans.Web;
import OLink.bpm.util.property.PropertyUtil;
import eWAP.core.Tools;

public class CookieUserSSO implements SSO {

	public Map<String, String> authenticateUser(HttpServletRequest request, HttpServletResponse response) {
		// login cookie set by my web LOGIN application
		Map<String, String> rtn = new HashMap<String, String>();
		
		Cookie cookieLoginAccount = CookieUtil.getCookie(PropertyUtil.get("sso.info.key.loginAccount"),
				request);
		Cookie cookieDomainName = CookieUtil.getCookie(PropertyUtil.get("sso.info.key.domainName"), request);

		if (cookieLoginAccount != null && cookieDomainName != null) {
			try {
				rtn.put(Web.SSO_LOGINACCOUNT_ATTRIBUTE, Tools.decodeBASE64(cookieLoginAccount.getValue()));
				rtn.put(Web.SSO_DOMAINNAME_ATTRIBUTE, Tools.decodeBASE64(cookieDomainName.getValue()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return rtn; // return login account for obpm
	}

	public Map<String, String> authenticateUser(PortletRequest request) {
		// login cookie set by my web LOGIN application
		Map<String, String> rtn = new HashMap<String, String>();
		
		Cookie cookieLoginAccount = CookieUtil.getCookie(PropertyUtil.get("sso.info.key.loginAccount"),
				request);
		Cookie cookieDomainName = CookieUtil.getCookie(PropertyUtil.get("sso.info.key.domainName"), request);

		if (cookieLoginAccount != null && cookieDomainName != null) {
			try {
				rtn.put(Web.SSO_LOGINACCOUNT_ATTRIBUTE, Tools.decodeBASE64(cookieLoginAccount.getValue()));
				rtn.put(Web.SSO_DOMAINNAME_ATTRIBUTE, Tools.decodeBASE64(cookieDomainName.getValue()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return rtn; // return login account for obpm
	}
}
