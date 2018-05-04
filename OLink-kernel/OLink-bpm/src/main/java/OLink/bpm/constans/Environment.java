package OLink.bpm.constans;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * The environment variable.
 */
public class Environment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7874636871392290110L;

	private String _wcpath;

	private String _contextPath;

	private String encoding;
	
	private String baseUrl;

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	private static Environment env = null;

	private Environment() {
		Properties prop = new Properties();
		InputStream is = Environment.class.getClassLoader().getResourceAsStream("webwork.properties");
		try {
			prop.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}

		setEncoding(prop.getProperty("webwork.i18n.encoding"));
	}

	public static Environment getInstance() {
		if (env == null) {
			env = new Environment();
		}

		return env;
	}

	/**
	 * @param wcpath
	 *            The web context path.
	 */
	public void setApplicationRealPath(String wcpath) {
		_wcpath = wcpath;
	}

	/**
	 * Retrieve the web context path.
	 * 
	 * @return The web context path.
	 */
	public String getApplicationRealPath() {
		return (_wcpath != null) ? _wcpath : "";
	}

	/**
	 * Retrieve the web context physicsal path.
	 * 
	 * @param path
	 *            The web path.
	 * @return The web context physicsal path.
	 */
	public String getRealPath(String path) {
		String realpath = (path != null) ? getApplicationRealPath() + path : "";
		realpath = realpath.replaceAll("\\\\", "/");

		return realpath;
	}

	/**
	 * Set request context path;
	 * 
	 * @param cpath
	 * @return
	 */
	public String setContextPath(String cpath) {
		return _contextPath = cpath;
	}

	public String getContextPath() {
		return _contextPath != null ? _contextPath : "/";
	}

	public String getContext(String uri) {
		if (_contextPath.equals("/")) {
			return uri;
		} else {
			return _contextPath + uri;
		}
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	
}
