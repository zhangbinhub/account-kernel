package OLink.bpm.util.property;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyUtil {
	private final static Logger LOG = Logger.getLogger(PropertyUtil.class);

	private static Map<String, Properties> settings = new HashMap<String, Properties>();

	public synchronized static void load(String name) {
		if (!isLoaded(name)) {
			Properties props = new Properties();

			URL propsUrl = Thread.currentThread().getContextClassLoader().getResource(name + ".properties");

			if (propsUrl == null) {
				throw new IllegalStateException(name + ".properties missing");
			}

			// Load settings
			try {
				props.load(propsUrl.openStream());
			} catch (IOException e) {
				throw new RuntimeException("Could not load " + name + ".properties:" + e);
			}
			LOG.debug(" Load properties file successed");

			settings.put(name, props);
		}
	}

	public static void init() {
		if (settings == null || settings.isEmpty()) {
			load("sso");
			load("email");
			load("shortmessage");
			load("eWAP");
			load("ftp");
		}

	}

	public static String get(String key) {
		return get(key, null);
	}
	
	public static boolean getBoolean(String key) {
		String rtn = get(key, null);
		if (rtn != null) {
			return Boolean.getBoolean(rtn);
		}
		return false;
	}

	public static String get(String key, String defaultValue) {
		if (settings == null || settings.isEmpty()) {
			init();
		}

		if(settings != null) {
			for (Iterator<String> iter = settings.keySet().iterator(); iter.hasNext();) {
				String propName = iter.next();
				Properties props = settings.get(propName);
				String value = props.getProperty(key, defaultValue);
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}

	public static String getByPropName(String propName, String key) {
		if (settings == null || settings.isEmpty()) {
			init();
		}

		if (settings != null && settings.get(propName) != null) {
			return settings.get(propName).getProperty(key);
		}
		return null;
	}

	public static void clear() {
		settings.clear();

	}

	public static void reload() {
		Collection<String> tempNames = new HashSet<String>();
		tempNames.addAll(settings.keySet());
		settings.clear();

		for (Iterator<String> iter = tempNames.iterator(); iter.hasNext();) {
			String name = iter.next();
			load(name);
		}
	}
	
	public static void reload(String propKey) {
		Properties props = new Properties();
		URL propsUrl = Thread.currentThread().getContextClassLoader().getResource(propKey + ".properties");
		
		if (propsUrl == null) {
			throw new IllegalStateException(propKey + ".properties missing");
		}
		// Load settings
		try {
			props.load(propsUrl.openStream());
		} catch (IOException e) {
			throw new RuntimeException("Could not load " + propKey + ".properties:" + e);
		}
		LOG.debug(" Load properties file successed");

		settings.put(propKey, props);
	}

	public static Map<String, String> toMap() {
		Map<String, String> rtn = new HashMap<String, String>();
		for (Iterator<String> iterator = settings.keySet().iterator(); iterator.hasNext();) {
			Properties properties = settings.get(iterator.next());
			for (Iterator<?> iterator2 = properties.keySet().iterator(); iterator2.hasNext();) {
				String key = (String) iterator2.next();
				String value = properties.getProperty(key);
				rtn.put(key, value);

			}
		}
		return rtn;
	}

	private static boolean isLoaded(String name) {
		return settings.keySet().contains(name);
	}

	public static void main(String[] args) {
		PropertyUtil.load("interface");
		PropertyUtil.reload();
		String path = PropertyUtil.get("outbound1backuppath");
		System.out.println(path);
	}
}
