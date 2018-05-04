package OLink.bpm.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.constans.Environment;
import eWAP.core.Tools;

public class WebCookies implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -844540327559674397L;
	// ValueStack里面的键值
	public static final String KEY_WEBCOOKIES = "webCookies";
	
	public final static String ENCRYPTION_BASE64 = "base64";
	
	public final static String ENCRYPTION_URL = "url";
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Cookie[] cookies;
	private Map<String, Cookie> cookieMap = new HashMap<String, Cookie>(20);
	private String encryption;

	public WebCookies(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		setCookies(request.getCookies());
	}

	public WebCookies(HttpServletRequest request, HttpServletResponse response, String encryption) {
		this.request = request;
		this.response = response;
		this.encryption = encryption;
		setCookies(request.getCookies());
	}

	/**
	 * 设置Cookie数组
	 * 
	 * @param cookies
	 */
	public void setCookies(Cookie[] cookies) {
		if(cookies==null){
			cookies=request.getCookies();
		}else{
			cookieMap.clear();
			this.cookies = cookies;
			for (int i = 0; i < cookies.length; i++) {
				Cookie acookie = cookies[i];
				cookieMap.put(acookie.getName(), acookie);
			}
		}
	}

	/**
	 * 得到cookie数组
	 * 
	 * @return cookie的数组
	 */
	public Cookie[] getCookies() {
		return cookies;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 得到某个cookie
	 * 
	 * @param sKey
	 *            键名
	 * @return 对应的cookie
	 */
	public Cookie getCookie(String sKey) {
		if (null == cookieMap) {
			return null;
		}
		Object ac = cookieMap.get(sKey);
		if (null != ac) {
			return (Cookie) ac;
		}
		return null;
	}

	public Map<String, Cookie> getCookieMap() {
		return cookieMap;
	}

	public String getValue(String name) {
		Cookie cookie = getCookie(name);
		if (cookie != null) {
			if (ENCRYPTION_BASE64.equals(encryption)) {
				return Tools.decodeBASE64(cookie.getValue());
			}else if(ENCRYPTION_URL.equals(encryption)){
				return this.decodeURLCore(cookie.getValue());
			}

			return cookie.getValue();
		}
		return "";
	}

	/**
	 * 添加一个cookie设置到HttpServletResponse中去
	 * 
	 * @param aCookie
	 */
	public void addCookie(Cookie aCookie) {
		// 调用response的addCookie
		if (null != response) {
			response.addCookie(aCookie);
		}
	}

	public void addCookie(String name, String value) {
		addCookie(name, value, 60 * 60 * 24 * 15);
	}

	public void addCookie(String name, String value, int maxAge) {
		// 调用response的addCookie
		if (null != response) {
			Cookie cookie = null;
			if (ENCRYPTION_BASE64.equals(encryption)) {
				cookie = new Cookie(name, Tools.encodeToBASE64(value));// BASE64
			}else if(ENCRYPTION_URL.equals(encryption)){
				cookie = new Cookie(name, this.ecodeURLCore(value));
			}else {
				cookie = new Cookie(name, value);// BASE64
			}

			cookie.setPath("/");
			cookie.setMaxAge(maxAge);
			response.addCookie(cookie);
		}
	}

	public void destroyCookie(String cookiekey) {
		if (cookiekey != null) {
			Cookie cookie = getCookie(cookiekey);
			if (cookie != null) {
				cookie.setPath("/");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}
	
	public String decodeURLCore(String name){
		try {
			return URLDecoder.decode(name, Environment.getInstance().getEncoding());
		} catch (UnsupportedEncodingException e) {
			return name;
		}
	}
	
	public String ecodeURLCore(String name){
		try {
			return URLEncoder.encode(name, Environment.getInstance().getEncoding());
		} catch (UnsupportedEncodingException e) {
			return name;
		}
	}
}