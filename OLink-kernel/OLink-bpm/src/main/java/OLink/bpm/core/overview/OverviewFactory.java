package OLink.bpm.core.overview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.util.StringUtil;

/**
 * 2.6版本新增的概览工厂类
 * 
 * @author Administrator
 * 
 */
public class OverviewFactory {

	// 现有的已知IOverview实现类
	public final static String APPLICATION_OVERVIEW_CLASSNAME = "ApplicationOverview";
	public final static String DATASOURCE_OVERVIEW_CLASSNAME = "DataSourceOverview";
	public final static String FORM_OVERVIEW_CLASSNAME = "FormOverview";
	public final static String VIEW_OVERVIEW_CLASSNAME = "ViewOverview";
	public final static String WORKFLOW_OVERVIEW_CLASSNAME = "WorkFlowOverview";
	public final static String REPORT_OVERVIEW_CLASSNAME = "ReportOverview";
	public final static String TASK_OVERVIEW_CLASSNAME = "TaskOverview";
	public final static String ROLE_OVERVIEW_CLASSNAME = "RoleOverview";
	public final static String MENU_OVERVIEW_CLASSNAME = "MenuOverview";
	public final static String HOMEPAGE_OVERVIEW_CLASSNAME = "HomePageOverview";
	public final static String REMINDER_OVERVIEW_CLASSNAME = "ReminderOverview";
	public final static String STATELABEL_OVERVIEW_CLASSNAME = "StateLabelOverview";
	public final static String REPOSITY_OVERVIEW_CLASSNAME = "ReposityOverview";
	public final static String STYLEREPOSITY_OVERVIEW_CLASSNAME = "StyleReposityOverview";
	public final static String VALIDATEREPOSITY_OVERVIEW_CLASSNAME = "ValidateReposityOverview";
	public final static String DEVELOPER_OVERVIEW_CLASSNAME = "DeveloperOverview";
	public final static String EXCELIMPORT_OVERVIEW_CLASSNAME = "ExcelImportConfigOverview";

	private final static Map<String, String> classNames = new LinkedHashMap<String, String>();

	static {
		classNames.put("APPLICATION", APPLICATION_OVERVIEW_CLASSNAME);
		classNames.put("DATASOURCE", DATASOURCE_OVERVIEW_CLASSNAME);
		classNames.put("FORM", FORM_OVERVIEW_CLASSNAME);
		classNames.put("VIEW", VIEW_OVERVIEW_CLASSNAME);
		classNames.put("WORKFLOW", WORKFLOW_OVERVIEW_CLASSNAME);
		classNames.put("REPORT", REPORT_OVERVIEW_CLASSNAME);
		classNames.put("TASK", TASK_OVERVIEW_CLASSNAME);
		classNames.put("ROLE", ROLE_OVERVIEW_CLASSNAME);
		classNames.put("MENU", MENU_OVERVIEW_CLASSNAME);
		classNames.put("HOMEPAGE", HOMEPAGE_OVERVIEW_CLASSNAME);
		classNames.put("REMINDER", REMINDER_OVERVIEW_CLASSNAME);
		classNames.put("STATELABEL", STATELABEL_OVERVIEW_CLASSNAME);
		classNames.put("REPOSITY", REPOSITY_OVERVIEW_CLASSNAME);
		classNames.put("STYLEREPOSITY", STYLEREPOSITY_OVERVIEW_CLASSNAME);
		classNames.put("VALIDATEREPOSITY", VALIDATEREPOSITY_OVERVIEW_CLASSNAME);
		classNames.put("DEVELOPER", DEVELOPER_OVERVIEW_CLASSNAME);
		classNames.put("EXCELIMPORT", EXCELIMPORT_OVERVIEW_CLASSNAME);
	}

	public static void put(String key, String className) {
		classNames.put(key, className);
	}
	
	public static void putAll(Map<String, String> m) {
		classNames.putAll(m);
	}

	public static void remove(String key) {
		classNames.remove(key);
	}

	/**
	 * 创建所有IOverview的实现类
	 * 
	 * 2.6版本新增
	 * 
	 * @return 所有的IOverview的实现类的实例集
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Collection<IOverview> createAllOverview()
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		List<IOverview> overviews = new ArrayList<IOverview>();
		Iterator<Entry<String, String>> it = classNames.entrySet().iterator();
		if (it != null) {
			while (it.hasNext()) {
				Entry<String, String> entry = it.next();
				if (entry != null) {
					String className = entry.getValue();
					if (!StringUtil.isBlank(className)) {
						Class<?> clazz = Class.forName(className);
						if (clazz != null) {
							Object instance = clazz.newInstance();
							if (instance instanceof IOverview)
								overviews.add((IOverview) instance);
						}
					}
				}
			}
		}
		return overviews;
	}

	/**
	 * 创建指定类名集合的所有的IOverview的实现类
	 * 
	 * 2.6版本新增
	 * 
	 * @param classNames
	 * @return IOverview的实现类的实例集
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static Collection<IOverview> createOverview(
			Collection<String> classNames) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		List<IOverview> overviews = new ArrayList<IOverview>();
		if (classNames != null) {
			Iterator<String> it = classNames.iterator();
			if (it != null) {
				while (it.hasNext()) {
					String className = it.next();
					Class<?> clazz = Class.forName(className);
					if (clazz != null) {
						Object instance = clazz.newInstance();
						if (instance instanceof IOverview) {
							overviews.add((IOverview) instance);
						}
					}
				}
			}
		}
		return overviews;
	}
}
