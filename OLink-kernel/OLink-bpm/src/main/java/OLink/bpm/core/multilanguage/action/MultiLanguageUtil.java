package OLink.bpm.core.multilanguage.action;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;
import OLink.bpm.util.StringUtil;

public class MultiLanguageUtil {
	/**
	 * 获取用户语言
	 * 
	 * @param request
	 * @return
	 */
	public static String getUserLanguage(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String userLanguage = (String) session.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
		if (StringUtil.isBlank(userLanguage)) {
			Locale loc = request.getLocale();
			userLanguage = "EN";
			if (loc.equals(Locale.CHINA) || loc.equals(Locale.PRC))
				userLanguage = "CN";
			else if (loc.equals(Locale.TAIWAN))
				userLanguage = "TW";
		}

		return userLanguage;
	}

	public static String getUserLanguage(Locale loc) {
		String userLanguage = "EN";
		if (loc.equals(Locale.CHINA) || loc.equals(Locale.PRC))
			userLanguage = "CN";
		else if (loc.equals(Locale.TAIWAN))
			userLanguage = "TW";

		return userLanguage;
	}
}
