package OLink.bpm.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class URLConnector {
	public static final String SERVLET_POST = "POST";
	public static final String SERVLET_GET = "GET";
	public static final String SERVLET_DELETE = "DELETE";
	public static final String SERVLET_PUT = "PUT";

	public static String doPost(String URL, Map<String, String> params) {
		OutputStream os = null;
		BufferedReader br = null;
		try {
			URL url = new URL(URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(SERVLET_POST);
			String paramStr = prepareParam(params);

			conn.setDoInput(true);
			conn.setDoOutput(true);
			os = conn.getOutputStream();

			os.write(paramStr.toString().getBytes("utf-8"));
			os.close();

			String charset = getChareset(conn.getContentType());
			br = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
			String line;
			StringBuffer result = new StringBuffer();
			while ((line = br.readLine()) != null) {
				result.append(line + "\n");
			}
			return result.toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return "";
	}

	private static String getChareset(String contentType) {
		int i = contentType == null ? -1 : contentType.indexOf("charset=");
		return i == -1 ? "UTF-8" : contentType.substring(i + 8);
	}

	private static String prepareParam(Map<String, String> paramMap) {
		StringBuffer sb = new StringBuffer();
		if (paramMap.isEmpty()) {
			return "";
		} else {
			for (Iterator<Entry<String, String>> it = paramMap.entrySet().iterator();it.hasNext();) {
				Entry<String, String> entry = it.next();
				String key = entry.getKey();
				String value = entry.getValue();
				if (sb.length() < 1) {
					sb.append(key).append("=").append(value);
				} else {
					sb.append("&").append(key).append("=").append(value);
				}
			}
			return sb.toString();
		}
	}

	public static void main(String[] args) {
		String URL = "http://localhost:8080/eWAP/urlLogin.action";
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", "testuser");
		params.put("password", "123456");
		params.put("domainName", "demo");
		String result = doPost(URL, params);
		System.out.println(result);
	}
}
