package services;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropertyUtil {
	private final static Logger LOG = Logger.getLogger(PropertyUtil.class);

	private Map<String, Properties> settings = new HashMap<String, Properties>();

	public synchronized void load(String name) {
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

	public String get(String key, String defaultValue) {
		if (settings == null || settings.isEmpty()) {
			return "";
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
		return "";
	}

	public String getByPropName(String propName, String key) {
		if (settings == null || settings.isEmpty()) {
			return "";
		}

		if (settings != null && settings.get(propName) != null) {
			return settings.get(propName).getProperty(key);
		}
		return "";
	}
}
