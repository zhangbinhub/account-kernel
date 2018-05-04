package OLink.bpm.util.http;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.util.StringUtil;

public class UrlUtil {
	public static String getServerName(String urlStr) {
		try {
			URL url = new URL(urlStr);
			String serverName = url.getHost();
			if (url.getPort() > -1) {
				serverName += ":" + url.getPort();
			}

			return serverName;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return "";
	}
	
	/**
	 * 获取服务器地址，不包括应用名称，如：http://localhost:8080
	 * @param urlStr
	 * @return
	 */
	public static String getUrlPrefix(String urlStr) {
		StringBuffer urlPrefix = new StringBuffer();

		try {
			URL url = new URL(urlStr);

			if (!StringUtil.isBlank(url.getProtocol())) {
				urlPrefix.append(url.getProtocol() + "://");
			}
			urlPrefix.append(url.getHost());

			if (url.getPort() > -1) {
				urlPrefix.append(":" + url.getPort());
			}

//			String[] elements = url.getPath().split("/");
//			if (!StringUtil.isBlank(url.getPath()) && elements.length >= 2) {
//				urlPrefix.append("/").append(elements[1]);
//			}

			return urlPrefix.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 为URI添加一个参数
	 * 
	 * @param uri
	 * @param paramsName
	 * @param paramsValue
	 * @return
	 */
	public static String parameterize(String uri, String paramsName, String paramsValue) {
		Map<Object, Object> params = new HashMap<Object, Object>();
		params.put(paramsName, paramsValue);
		return parameterize(uri, params);
	}

	/**
	 * 为URI添加参数
	 * 
	 * @param uri
	 *            URI
	 * @param parameters
	 *            参数
	 * @return The uri with added parameters
	 */
	public static String parameterize(String uri, Map<?, ?> parameters) {
		if (parameters.size() == 0) {
			return uri;
		}

		StringBuffer buffer = new StringBuffer(uri);
		if (uri.indexOf('?') == -1) {
			buffer.append('?');
		} else {
			buffer.append('&');
		}

		for (Iterator<?> i = parameters.entrySet().iterator(); i.hasNext();) {
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) i.next();
			if (entry.getValue().getClass().isArray()) {
				Object[] value = (Object[]) entry.getValue();
				for (int j = 0; j < value.length; j++) {
					if (j > 0) {
						buffer.append('&');
					}
					buffer.append(entry.getKey());
					buffer.append('=');
					try {
						buffer.append(URLEncoder.encode((String) value[j], "UTF-8"));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			} else {
				buffer.append(entry.getKey());
				buffer.append('=');
				try {
					buffer.append(URLEncoder.encode((String) entry.getValue(), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if (i.hasNext()) {
				buffer.append('&');
			}
		}
		return buffer.toString();
	}

	public static String deparameterize(String uri, Map<Object, Object> parameters) {
		return deparameterize(uri, parameters, false);
	}

	public static String deparameterize(String uri, Map<Object, Object> parameters, boolean isReplace) {
		int i = uri.lastIndexOf('?');
		if (i == -1) {
			return uri;
		}

		// parameters.clear();
		String[] params = uri.substring(i + 1).split("&");
		for (int j = 0; j < params.length; j++) {
			String p = params[j];
			int k = p.indexOf('=');
			if (k == -1) {
				break;
			}
			String name = p.substring(0, k);
			String value = p.substring(k + 1);
			Object values = parameters.get(name);
			if (values == null || isReplace) {
				parameters.put(name, new String[] { value });
			} else {
				String[] v1 = (String[]) values;
				String[] v2 = new String[v1.length + 1];
				System.arraycopy(v1, 0, v2, 0, v1.length);
				v2[v1.length] = value;
				parameters.put(name, v2);
			}
		}

		return uri.substring(0, i);
	}

	public static void main(String[] args) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("aaa", "aaavlue");
		// 将Map中的参数添加到URL
		String newURL = UrlUtil.parameterize("/eWAP/portal?bbb=bbbvalue", parameters);
		System.out.println(newURL + " " + parameters);

		// 将参数转移到Map中
		Map<Object, Object> parameters2 = new HashMap<Object, Object>();
		newURL = UrlUtil.deparameterize(newURL, parameters2);
		System.out.println(newURL + " " + parameters2);

		URL url = new URL("http://localhost:8080/eWAP-cas/login");
		System.out.println("path: " + url.toExternalForm());
		System.out.println(url.getPath().split("/").length);
		System.out.println(getServerName("http://localhost:8080/eWAP-cas/login"));
		System.out.println(getUrlPrefix("http://localhost:8080/eWAP-cas/login"));
	}
}
