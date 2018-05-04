package OLink.bpm.core.resource.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.util.web.DWRHtmlUtils;

import com.opensymphony.xwork.config.Configuration;
import com.opensymphony.xwork.config.ConfigurationManager;
import com.opensymphony.xwork.config.entities.ActionConfig;
import com.opensymphony.xwork.config.entities.PackageConfig;

public class WebWorkConfigUtil {

	/**
	 * Get the web work action config list.
	 * @return The web work action config list.
	 */
	public Collection<ActionConfig> getActionConfig() {
		ArrayList<ActionConfig> actionClasses = new ArrayList<ActionConfig>();

		Configuration conf = ConfigurationManager.getConfiguration();
		Set<?> names = conf.getPackageConfigNames();
		Iterator<?> iter = names.iterator();

		while (iter.hasNext()) {
			String name = (String) iter.next();

			PackageConfig pc = conf.getPackageConfig(name);

			Map<?, ?> actions = pc.getAllActionConfigs();

			Iterator<?> iter2 = actions.values().iterator();
			while (iter2.hasNext()) {
				ActionConfig actionConfig = (ActionConfig) iter2.next();
				actionClasses.add(actionConfig);
			}
		}

		return actionClasses;
	}

	/**
	 * Get the web work action class list.
	 * @return The web work action class list.
	 */
	public Map<String, String> getActionClasses() {
		Collection<ActionConfig> actionConfigs = getActionConfig();

		LinkedHashMap<String, String> actionClasses = new LinkedHashMap<String, String>();
		actionClasses.put("none", "Select");
		Iterator<ActionConfig> iter = actionConfigs.iterator();
		while (iter.hasNext()) {
			ActionConfig actionConfig = iter.next();
			actionClasses.put(actionConfig.getClassName(), actionConfig
					.getClassName());
		}
		
		return actionClasses;
	}
	
	public String createActionClassesOptionFunc(String selectFieldName, String def) {
		Map<String, String> map = getActionClasses();
		return DWRHtmlUtils.createOptions(map,selectFieldName,def);
	}
	
	public String createActionMethodsOptionFunc(String selectFieldName, String actionClass, String def) {
		Map<String, String> map = getActionMethods(actionClass);
		return DWRHtmlUtils.createOptions(map,selectFieldName,def);
	}
	
	public String createActionUrlsOptionFunc(String selectFieldName, String actionClass, String actionMethod, String def) {
		Map<String, String> map = getActionUrls(actionClass, actionMethod);
		return DWRHtmlUtils.createOptions(map,selectFieldName,def);
	}

	/**
	 * Get the web work action method list.
	 * @param className The action class name.
	 * @return the web work action method list.
	 */
	public Map<String, String> getActionMethods(String className) {
		Map<String, String> actionMethods = new LinkedHashMap<String, String>();
		actionMethods.put("none", "Select");

		if (className == null) {
			return actionMethods;
		}

		Collection<ActionConfig> actionConfigs = getActionConfig();

		Iterator<ActionConfig> iter = actionConfigs.iterator();
		while (iter.hasNext()) {
			ActionConfig actionConfig = iter.next();

			if (actionConfig.getClassName().equals(className)) {
				actionMethods.put(actionConfig.getMethodName(), actionConfig
						.getMethodName());
			}
		}

		return actionMethods;
	}

	/**
	 * @SuppressWarnings webwork不支持泛型
	 * the web work forward url list according the class and the method.
	 * @param className The action class name.
	 * @param methodName The action method name.
	 * @return The forward url list according the class and the method.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getActionUrls(String className, String methodName) {
		Map<String, String> urls = new LinkedHashMap<String, String>();
		urls.put("none", "Select");

		if (className == null || methodName == null) {
			return urls;
		}

		Configuration conf = ConfigurationManager.getConfiguration();
		Set<?> names = conf.getPackageConfigNames();
		Iterator<?> iter = names.iterator();

		while (iter.hasNext()) {
			String name = (String) iter.next();
			PackageConfig pc = conf.getPackageConfig(name);

			String namespace = pc.getNamespace();

			Map actions = pc.getAllActionConfigs();

			Iterator<Entry<String, ActionConfig>> iter2 = actions.entrySet().iterator();
			while (iter2.hasNext()) {
				Entry<String, ActionConfig> entry = iter2.next();
				ActionConfig actionConfig = entry.getValue();

				if (actionConfig.getClassName().equals(className)
						&& actionConfig.getMethodName().equals(methodName)) {
					String tmp = namespace + "/" + entry.getKey() + ".action";
					urls.put(tmp, tmp);
				}
			}
		}

		return urls;
	}

	/**
	 * Get the menu tree.
	 * @return The menu tree.
	 */
	public Map<String, String> getMenuTree(String application) {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("", "Select");

		try {
			ResourceProcess process = (ResourceProcess) ProcessFactory
					.createProcess(ResourceProcess.class);
			Collection<ResourceVO> rc = process.doSimpleQuery(null, application);
			ResourceVO node = null;

			Iterator<ResourceVO> iter = rc.iterator();
			while (iter.hasNext()) {
				node = iter.next();
				if (node.getSuperior() == null) {
					map.putAll(getSubTree(rc, node, 0));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	/**
	 * Get the menu tree.
	 * @param startNodeId The parent menu tree.
	 * @return The menu tree.
	 */
	public Map<String, String> getMenuTree(String startNodeId, String application) {
		Map<String, String> map = null;

		try {
			ResourceProcess process = (ResourceProcess) ProcessFactory
					.createProcess(ResourceProcess.class);
			Collection<ResourceVO> rc = process.doSimpleQuery(null, application);
			ResourceVO node = null;

			Iterator<ResourceVO> iter = rc.iterator();
			while (iter.hasNext()) {
				node = iter.next();
				if (node.getId() == null
						|| node.getId().trim().equals(startNodeId))
					break;
			}

			if (node != null) {
				map = new TreeMap<String, String>();
				map.put("", "Select");
				map.putAll(getSubTree(rc, node, 0));
			} else {
				map = new TreeMap<String, String>();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;

	}

	/**
	 * Get the menu tree expect the current menu.
	 * @param startNodeId The parent menu.
	 * @param currResourceId The current menu.
	 * @return  The menu tree expect the parent menu.
	 */
	public Map<String,String> getMenuTreeExpCurrResource(String startNodeId,
			String currResourceId, String application) {
		Map<String, String> tree = getMenuTree(startNodeId, application);
		tree.remove(currResourceId);
		return tree;
	}

	/**
	 * Get the menu tree expect the parent menu.
	 * @param currResourceId  The current menu.
	 * @return The menu tree expect the parent menu.
	 */
	public Map<String,String> getMenuTreeExpCurrResource(String currResourceId, String application) {
		Map<String,String> tree = getMenuTree(application);
		if (currResourceId != null)
			tree.remove(currResourceId);
		return tree;
	}

	/**
	 * Get the sub menu tree tree.
	 * @param rc The resouce collection
	 * @param node The parent node.
	 * @param deep The tree height
	 * @return The sub menu tree.
	 */
	private Map<String, String> getSubTree(Collection<ResourceVO> rc, ResourceVO node, int deep) {
		String prefix = "|---------------------------------";
		TreeMap<String, String> rtn = new TreeMap<String, String>();
		rtn.put(node.getId(), prefix.substring(0, deep * 2)
				+ node.getDescription());
		Iterator<ResourceVO> iter = rc.iterator();
		while (iter.hasNext()) {
			ResourceVO sub = iter.next();
			if (node.getId().equals(sub.getSuperior().getId())
					&& !node.getId().equals(sub.getId())) {
				Map<String, String> subTree = getSubTree(rc, sub, deep + 1);
				rtn.putAll(subTree);
			}
		}
		return rtn;
	}

	public static void main(String[] args) {
		WebWorkConfigUtil test = new WebWorkConfigUtil();
		ResourceVO root;
		ArrayList<ResourceVO> list = new ArrayList<ResourceVO>();
		{
			root = new ResourceVO();
			root.setId("1");
			root.setDescription("root");
			list.add(root);
		}

		Map<String,String> tree = test.getSubTree(list, root, 0);

		Iterator<String> iter = tree.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			key.toString();
		}
	}

}
