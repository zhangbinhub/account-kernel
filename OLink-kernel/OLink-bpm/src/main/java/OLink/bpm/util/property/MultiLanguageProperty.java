package OLink.bpm.util.property;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import OLink.bpm.core.multilanguage.ejb.MultiLanguageProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.multilanguage.ejb.MultiLanguage;

public class MultiLanguageProperty {
	private static final Logger log = Logger.getLogger(MultiLanguageProperty.class);

	public static final int[] TYPES = { 1, 2, 3, 4 };

	public static final String[] NAMES = { "EN", "CN", "TW", "RU" };

	private static MultiLanguageProcess mp = null;
	private static Map<String, Properties> propCache = null;
	private static Map<String, Properties> defaultProps = null;

	/**
	 * Initialize the properties default load english properties
	 * 
	 * @throws Exception
	 */
	public static synchronized void init() throws Exception {
		defaultProps = new HashMap<String, Properties>();
		URL url = Thread.currentThread().getContextClassLoader().getResource("");
//		File file = new File(url.getFile().substring(1) + File.separator + "i18n");
		File file = new File(url.getFile().substring(1) + "/" + "i18n");
		String[] files = file.list();
		for (int i = 0; files != null && i < files.length; i++) {
			String regex = "multiLanguage_i18n_";

			if (files[i].startsWith(regex)) {
				String name = files[i].substring(regex.length(), regex.length() + 2);
				if (getType(name) != 0) {
					init(name);
					log.info("Initializing property " + files[i]);
				}

			}
		}

	}

	/**
	 * 
	 * @param application
	 *            ：根据Application加载对应的用户定义语言
	 * @param clearCache
	 *            ：boolean类型，是否重新加载语言。 true：总是加载；
	 *            false：读取缓存，如果缓存中没有对应Application的用户定义语言才重新加载语言
	 * @throws Exception
	 */
	public static synchronized void load(String application, boolean clearCache) throws Exception {
		Properties prop = null;
		if (propCache != null && !clearCache) {
			prop = propCache.get(application);
		} else {
			if (propCache == null)
				propCache = new HashMap<String, Properties>();
			if (propCache.containsKey(application))
				propCache.remove(application);
		}
		if (prop==null || prop.isEmpty()) {
			prop = new Properties();
			if (mp==null) {
				try {
					mp = (MultiLanguageProcess) ProcessFactory.createProcess(MultiLanguageProcess.class);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			if (application != null) {
				Collection<MultiLanguage> cols = mp.doSimpleQuery(null, application);
				if (cols != null) {
					for (Iterator<MultiLanguage> iterator = cols.iterator(); iterator.hasNext();) {
						MultiLanguage language = iterator.next();
						prop.setProperty(getName(language.getType()) + language.getLabel(), language.getText());
					}
				}
				propCache.put(application, prop);
			}
		}
	}

	public static void remove(String application, String key) {
		Properties prop=null;
		if (propCache != null) {
			prop = propCache.get(application);
			if (prop != null) {
				prop.remove(key);
			}
		} 
		
	}

	/**
	 * Get the property value
	 * 
	 * @param application
	 *            The application
	 * @param language
	 *            The language of user
	 * @param key
	 *            The property key
	 * @param defaultValue
	 *            The default value.
	 * @return The Property value.
	 * @throws Exception
	 */
	public static String getProperty(String application, String language, String key, String defaultValue) throws Exception {
		load(application, false);
		Properties p = propCache.get(application);
		String value = null;
		if (p == null || p.isEmpty()) {
			if (defaultProps == null) {
				init();
			}
			value = getProperty(language, key, defaultValue);
		} else {
			value = p.getProperty(language + key, null);
			if (value == null) {
				value = getProperty(language, key, defaultValue);
			}
		}

		return value;
	}

	/**
	 * 
	 * @param language
	 *            The language of user
	 * @param key
	 *            The property key
	 * @param defaultValue
	 * @return The Property value find from default Property,if no value,return
	 *         defaultValue,else return the Property value.
	 * @throws Exception
	 */
	public static String getProperty(String language, String key, String defaultValue) throws Exception {
		if (defaultProps == null) {
			init();
		}
		Properties prop = defaultProps.get(language);
		String rtn = null;
		if (prop != null)
			rtn = prop.getProperty(key, defaultValue);
		else {
			init(language);
			prop = defaultProps.get(language);
			rtn = prop.getProperty(key, defaultValue);
		}
		if (!StringUtil.isBlank(rtn) && !rtn.equals(defaultValue))
			return rtn;
		return defaultValue;
	}

	private static void init(String language) throws IOException {
		Properties prop = new Properties();
		String path = "i18n/"+ "multiLanguage_i18n_" + language + ".properties";
		URL propsUrl = MultiLanguageProperty.class.getClassLoader().getResource(path);
		InputStream is = propsUrl.openStream();
		prop.load(is);
		defaultProps.put(language, prop);
	}

	/**
	 * Get the property value
	 * 
	 * @param key
	 *            The property key
	 * @return The Property value find from default Property.
	 * @throws Exception
	 */
	public static String getProperty(String key) throws Exception {
		return getProperty(key, null);
	}

	/**
	 * Get the property value
	 * 
	 * @param language
	 *            The language of user
	 * @param key
	 *            The property key
	 * @return The Property value find from default Property.
	 * @throws Exception
	 */
	public static String getProperty(String key, String defaultValue) throws Exception {
		Properties prop = defaultProps.get(NAMES[0]);
		return prop.getProperty(key, defaultValue);
	}

	/**
	 * 
	 * @param application
	 *            根据应用查询语言
	 * @param text
	 *            要替换语言标识（Label）
	 * @param languageType
	 *            语言类别
	 * @return 替换后语言内容
	 */
	public static String replaceText(String application, String language, String text) {
		String rtext = text.replaceAll("[\t\n\'\"\f\r]", "");
		try {
			// String stype = MultiLanguageProperty.getName(languageType);
			String newText = "";
			if (application == null)
				newText = getProperty(language, rtext, rtext);
			else
				newText = getProperty(application, language, rtext, rtext);

			return newText;
		} catch (Exception e) {
			log.warn("Could not found key[" + rtext + "] of " + language + " properties");
		}
		return text;
	}

	public static String getName(int type) {
		if (type == 0) {
			return null;
		}

		for (int i = 0; i < TYPES.length; i++)
			if (type == (TYPES[i]))
				return NAMES[i];
		return "";
	}

	public static int getType(String name) {
		if (name == null)
			return 0;

		for (int i = 0; i < NAMES.length; i++)
			if (name.equals(NAMES[i]))
				return TYPES[i];
		return 0;
	}

	public static void putLanguage(String application, MultiLanguage language) throws Exception {
		Properties prop = null;
		if (propCache != null) {
			prop = propCache.get(application);
		}
		if (prop != null) {
			prop.setProperty(getName(language.getType()) + language.getLabel(), language.getText());
		} else {
			try {
				load(application, true);
			} catch (Exception e) {
				log.warn("To add multiple languages in the application=" + application + " of the cache!");
			}
		}
	}

}
