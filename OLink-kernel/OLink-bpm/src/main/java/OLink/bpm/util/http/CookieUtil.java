package OLink.bpm.util.http;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.util.StringUtil;

public class CookieUtil {
	public static void setSSO(final HttpServletResponse response, final String loginno, final String password,
			final String domianName) {
		final String sSession = loginno + "-" + password + "-" + domianName;
		Cookie oItem;

		// 因为sSession需要使用其他方法加密, 密码建议使用Base64加密
		oItem = new Cookie("SSO", sSession);
		// oItem.setDomain(.google.com); 请用自己的域
		oItem.setMaxAge(-1); // 关闭浏览器后，cookie立即失效
		oItem.setPath("/");
		response.addCookie(oItem);
	}

	public static Map<String, String> getSSO(final HttpServletRequest request) {
		Map<String, String> rtn = new HashMap<String, String>();
		final Cookie[] oCookies = request.getCookies();
		if (oCookies != null) {
			for (final Cookie oItem : oCookies) {
				final String sName = oItem.getName();

				if (sName.equals("SSO")) {
					final String sSession = oItem.getValue();
					if (!StringUtil.isBlank(sSession)) {
						String[] info = sSession.split("-");
						if (info.length == 3) {
							rtn.put("loginno", info[0]);
							rtn.put("password", info[1]);
							rtn.put("domain", info[2]);
						}
					}
				}
			}
		}
		return rtn;
	}

	public static void clearSSO(final HttpServletResponse response) {
		Cookie cookie = new Cookie("SSO", null);
		cookie.setMaxAge(0); // delete the cookie.
		cookie.setPath("/");
		response.addCookie(cookie);
	}

	public static Cookie getCookie(final String name, final HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];

				if (c.getName().equals(name)) {
					return c;
				}
			}
		}

		return null;
	}
	
	public static Cookie getCookie(String name, PortletRequest request) {
		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];

				if (c.getName().equals(name)) {
					return c;
				}
			}
		}

		return null;
	}

	public static void setCookie(final String name, final String value, final HttpServletResponse response) {
		// 因为value需要使用其他方法加密, 密码建议使用Base64加密
		Cookie oItem = new Cookie(name, value);
		// oItem.setDomain(.google.com); 请用自己的域
		oItem.setMaxAge(-1); // 关闭浏览器后，cookie立即失效
		oItem.setPath("/"); // 有效期为session
		response.addCookie(oItem);
	}

	public static void clearCookie(final String name, final HttpServletResponse response) {
		Cookie cookie = new Cookie(name, null);
		cookie.setMaxAge(0); // delete the cookie.
		cookie.setPath("/");
		response.addCookie(cookie);
	}
}
