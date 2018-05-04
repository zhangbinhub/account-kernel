package OLink.bpm.core.report.crossreport.runtime.dataset;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * System parameters, which include the different system configuration, like
 * server character setting / regional setting / resource file path.
 */
public class SystemParameters {

	public final static Properties props = null;

	public final static String SRV_DATE_FORMAT = "yyyy-MM-dd";

	public final static String SRV_TIME_FORMAT = "hh:mm:ss";

	public final static String SRV_DATETIME_FORMAT = "yyyy-MM-dd";

	public final static boolean ARCH_USING_AOP = false;

	public final static boolean ARCH_LOG_ACTION = false;

	public final static boolean ARCH_LOG_DB = false;

	public final static boolean ARCH_LOG_EXCEPTION = false;

	public final static boolean ARCH_LOG_JOB = false;

	public final static boolean ARCH_LOG_SECRITY = false;

	public final static String[] ARCH_APPCONTEXT_FILES = null;

	public final static boolean EC_NEED_ENCODE = false;

	public final static boolean AD_EXCEPTION_LOG_DETAIL = false;

	public final static int JDBC_BATCH_NUMNER = 5;

	public final static String RESOURCE_DATA_TMP = "";


	/**
	 * Load the parameters form the file.
	 * 
	 * @param filename
	 *            The file name
	 */
	@SuppressWarnings("unused")
	private static void load(String filename) {
		InputStream is = null;
		Properties p = new Properties();
		try {
			p.clear();
			is = SystemParameters.class.getClassLoader().getResourceAsStream(
					filename);
			p.load(is);

			if (p != null) {
				Enumeration<?> e = p.propertyNames();
				while (e.hasMoreElements()) {
					String key = (String) e.nextElement();
					props.put(key, p.get(key));

				}
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get the value in properties file.
	 * 
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String get(String key) {
		return props.getProperty(key);
	}
}
