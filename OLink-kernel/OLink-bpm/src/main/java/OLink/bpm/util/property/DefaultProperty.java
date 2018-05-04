package OLink.bpm.util.property;

import java.io.InputStream;
import java.util.Properties;

/**
 * The default property.
 */
public class DefaultProperty {
	private static Properties prop = null;

	/**
	 * Initialize the proerties
	 * 
	 * @throws Exception
	 */
	private static void init() throws Exception {
		if (prop == null)
			prop = new Properties();
		//增加 by XGY
		InputStream is = DefaultProperty.class.getClassLoader().getResourceAsStream("eWAP.properties");
		prop.load(is);
	}

	/**
	 * Get the property value
	 * 
	 * @param key
	 *            The property key
	 * @param defaultValue
	 *            The default value.
	 * @return The Property value.
	 * @throws Exception
	 */
	public static String getProperty(String key, String defaultValue) {
		if (prop == null) {
			try {
				init();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return prop != null ? prop.getProperty(key, defaultValue):null;
	}

	/**
	 * Get the property value
	 * 
	 * @param key
	 *            The property key
	 * @return The Property value.
	 * @throws Exception
	 */
	public static String getProperty(String key) {
		return getProperty(key, null);
	}

}
