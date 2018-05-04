package OLink.bpm.core.dynaform.activity.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.util.xml.XmlUtil;
import OLink.bpm.core.dynaform.activity.ejb.Activity;
import OLink.bpm.core.dynaform.activity.ejb.ActivityType;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.printer.ejb.Printer;
import OLink.bpm.core.dynaform.printer.ejb.PrinterProcess;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.web.DWRHtmlUtils;

public class ActivityUtil {
	/**
	 * 根据模块主键与应用标识查询,返回视图集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application盘应用标识
	 * @return 视图集合
	 * @throws Exception
	 */
	public Map<String, String> getViewByModule(String moduleid,
			String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "{*[Select]*}");
		if (moduleid.equals("")) {
			return map;
		}
		ViewProcess vp = (ViewProcess) ProcessFactory
				.createProcess(ViewProcess.class);
		Collection<View> col = vp.getViewsByModule(moduleid, application);

		Iterator<View> it = col.iterator();
		while (it.hasNext()) {
			View vw = it.next();
			map.put(vw.getId(), vw.getName());
		}
		return map;

	}

	/**
	 * 根据模块主键与应用标识查询,返回表单集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application盘应用标识
	 * @return 表单集合
	 * @throws Exception
	 */
	public Map<String, String> getFormByModule(String moduleid,
			String application) throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "Select");
		if (moduleid.equals("")) {
			return map;
		}
		FormProcess fp = (FormProcess) ProcessFactory
				.createProcess(FormProcess.class);
		Collection<Form> col = fp.getFormsByModule(moduleid, application);

		Iterator<Form> it = col.iterator();
		while (it.hasNext()) {
			Form fm = it.next();
			map.put(fm.getId(), fm.getName());
		}
		return map;
	}

	/**
	 * 根据模块主键查询,返回流程集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * 
	 * @return 流程集合
	 * @throws Exception
	 */
	public Map<String, String> getFlowByModule(String moduleid)
			throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "Select");
		if (moduleid.equals("")) {
			return map;
		}

		BillDefiProcess bp = (BillDefiProcess) ProcessFactory
				.createProcess(BillDefiProcess.class);
		Collection<BillDefiVO> col = bp.getBillDefiByModule(moduleid);

		Iterator<BillDefiVO> it = col.iterator();
		while (it.hasNext()) {
			BillDefiVO bv = it.next();
			map.put(bv.getId(), bv.getSubject());
		}
		return map;
	}

	/**
	 * 获取视图所适用的按钮
	 * 
	 * @param selectFieldName
	 *            下拉框名称
	 * @param def
	 *            默认值
	 * @return 添加选项的JS脚本
	 */
	public String createViewType(String selectFieldName, String def) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "{*[Select]*}");
		map.putAll(ActivityType.getViewActTypeMapWithMulti());

		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	/**
	 * 获取表单所适用的按钮
	 * 
	 * @param selectFieldName
	 *            下拉框名称
	 * @param def
	 *            默认值
	 * @return 添加选项的JS脚本
	 */
	public String createFormType(String selectFieldName, String def) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "{*[Select]*}");
		map.putAll(ActivityType.getFormActTypeMapWithMulti());

		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	/**
	 * 获取表单所适用的按钮除了跳转按钮之外
	 * 
	 * @param selectFieldName
	 * @param def
	 * @return
	 */
	public String createFormTypeExceptJUMP(String selectFieldName, String def,
			int type) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "{*[Select]*}");
		// 遍历每个元素，并加上多语言标签
		map.putAll(ActivityType.getFormActTypeMapWithMulti());
		map.remove(type + "");

		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	/**
	 * 返回字符串为重定义后的javascript,创建按钮类型列表框.
	 * 
	 * @param selectFieldName
	 *            按钮描述名
	 * @param def
	 *            按钮值
	 * @return
	 */
	public String createType(String selectFieldName, String def) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("0", "{*[Select]*}");
		map.putAll(ActivityType.getFormActTypeMapWithMulti());

		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	/**
	 * 返回字符串为重定义后的javascript.实现下拉联动的动态加载的活动视图列表框.
	 * <p>
	 * 此重定义后的javascript是根据Activity类型值为ACTIVITY_TYPE_DOCUMENT_QUERY(
	 * Activity的类型为查询Document)的常量值,
	 * 以及所属模块获取的View集合,创建一个下拉联动的活动View(OnActionView)列表框.
	 * 
	 * @see Activity#ACTIVITY_TYPE_DOCUMENT_QUERY
	 * @param selectFieldName
	 *            选择字段名
	 * @param type
	 *            按钮类型(acvitity)
	 * @param moduleid
	 *            所属模块
	 * @param def
	 *            选择字段名值
	 * @param application
	 *            应用标识
	 * @return 字符串为重定义后的javascript.
	 * @throws Exception
	 */
	public String createOnActionView(String selectFieldName, String type,
			String moduleid, String def, String application) throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (!StringUtil.isBlank(type)
				&& Integer.parseInt(type) == ActivityType.DOCUMENT_QUERY) {
			map = getViewByModule(moduleid, application);
		}
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	/**
	 * 返回字符串为重定义后的javascript,实现下拉联动的动态加载的活动表单列表框.
	 * <p>
	 * 此重定义后的javascript是根据Activity类型值为ACTIVITY_TYPE_DOCUMENT_CREATE(
	 * Activity的类型为创建Document
	 * )的常量值或ACTIVITY_TYPE_DOCUMENT_UPDATE(Activity的类型为更新Document)的常量值,
	 * 以及所属模块获取的Form集合,创建一个下拉联动的活动Form(OnActionForm)列表框.
	 * 
	 * @see Activity#ACTIVITY_TYPE_DOCUMENT_CREATE
	 * @see Activity#ACTIVITY_TYPE_DOCUMENT_UPDATE
	 * @param selectFieldName
	 *            选择字段名
	 * @param type
	 *            按钮类型(acvitity)
	 * @param moduleid
	 *            所属模块
	 * @param def
	 *            选择字段名值
	 * @param application
	 *            应用标识
	 * @return 字符串为重定义后的javascript.
	 * @throws Exception
	 */
	public String createOnActionForm(String selectFieldName, String type,
			String moduleid, String def, String application) throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		int typeCode = 0;
		try {
			typeCode = Integer.parseInt(type);
		} catch (NumberFormatException e) {
		}

		if (typeCode == ActivityType.DOCUMENT_CREATE
				|| typeCode == ActivityType.DOCUMENT_UPDATE
				|| typeCode == ActivityType.CLEAR_ALL) {
			map = getFormByModule(moduleid, application);
		}
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	/**
	 * 返回字符串为重定义后的javascript,实现下拉联动的动态加载的活动流程列表框.
	 * <p>
	 * 此重定义后的javascript是根据Activity类型值为ACTIVITY_TYPE_WORKFLOW_PROCESS(
	 * Activity的类型为处理流程)的常量值, 以及所属模块获取的Flow集合,创建一个下拉联动的活动Flow(OnActionFlow)列表框.
	 * 
	 * @see Activity#ACTIVITY_TYPE_WORKFLOW_PROCESS
	 * 
	 * 
	 * @param selectFieldName
	 *            选择字段名
	 * @param type
	 *            按钮类型(acvitity)
	 * @param moduleid
	 *            所属模块
	 * @param def
	 *            选择字段名值
	 * @param application
	 *            应用标识
	 * @return 字符串为重定义后的javascript,实现下拉联动的动态加载活动流程列表框.
	 * @throws Exception
	 */
	public String crateOnActionFlow(String selectFieldName, String type,
			String moduleid, String def) throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (!StringUtil.isBlank(type)
				&& Integer.parseInt(type) == ActivityType.WORKFLOW_PROCESS) {
			map = getFlowByModule(moduleid);
		}
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	/*
	 * 根据模块id来查回流程 @param moduleid 模块id @return map @throws Exception
	 */
	public Map<String, String> getFlowsByModuleid(String moduleid)
			throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (moduleid != null && !StringUtil.isBlank(moduleid)) {
			map = getFlowByModule(moduleid);
		}
		return map;
	}

	public String createOnActionPrint(String selectFieldName, String type,
			String moduleid, String def) throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		if (!StringUtil.isBlank(type)
				&& (Integer.parseInt(type) == ActivityType.FLEX_PRINT || Integer
						.parseInt(type) == ActivityType.FLEX_PRINT_WITHFLOWHIS)) {
			map = getPrintByModule(moduleid);
		}
		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public Map<String, String> getPrintByModule(String moduleid)
			throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "{*[Select]*}");
		if (moduleid.equals("")) {
			return map;
		}

		PrinterProcess pp = (PrinterProcess) ProcessFactory
				.createProcess(PrinterProcess.class);
		Collection<Printer> col = pp.getPrinterByModule(moduleid);

		Iterator<Printer> it = col.iterator();
		while (it.hasNext()) {
			Printer printer = it.next();
			map.put(printer.getId(), printer.getName());
		}
		return map;
	}

	public Set<?> parseXML(String activityXML) {
		return (Set<?>) XmlUtil.toOjbect(activityXML);
	}

	public String parseObject(TreeSet<Activity> activitySet) {
		return XmlUtil.toXml(activitySet);
	}

	public String toListHtml(TreeSet<Activity> activitySet,
			HttpServletRequest request) {
		StringBuffer htmlBuilder = new StringBuffer();

		Map<String, String> map = ActivityType.getViewActTypeMapWithMulti();
		Map<String, String> formactMap = ActivityType
				.getFormActTypeMapWithMulti();
		int index = 0;
		htmlBuilder.append("<table class='table_noborder'>");
		htmlBuilder
				.append("<tr><td class='column-head2' align='center'><input type='checkbox' onclick='selectAllAct(this.checked)'></td><td class='column-head' align='center'>{*[Activity]*}{*[Name]*}</td><td class='column-head' align='center'>{*[Activity]*}{*[Type]*}</td><td class='column-head' align='center'>{*[Move_Up]*}</td><td class='column-head' align='center'>{*[Move_Down]*}</td></tr>");
		String contextPath = request.getContextPath();

		for (Iterator<Activity> iterator = activitySet.iterator(); iterator
				.hasNext(); index++) {

			Activity activity = iterator.next();
			if (index % 2 == 0) {
				htmlBuilder.append("<tr class='table-text'>");
			} else {
				htmlBuilder.append("<tr class='table-text2'>");
			}
			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder
					.append("<input type=\"checkbox\" name=\"activitySelects\" value="
							+ index + ">");
			htmlBuilder.append("</td>");

			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder.append("<a href=\"javascript:actProcess.doEdit("
					+ index + ")\" style=\"cursor:hand\">" + activity.getName()
					+ "</a>");
			htmlBuilder.append("</td>");

			htmlBuilder.append("<td class='table-td'>");
			String actType = formactMap.get(activity.getType() + "");
			if (actType == null) {
				actType = map.get(activity.getType() + "");
			}
			htmlBuilder.append("<a style=\"cursor:hand\">" + actType + "</a>");
			htmlBuilder.append("</td>");

			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder.append("<a href=\"javascript:actProcess.doOrderChange("
					+ index + ", 'p')\"><img border=0 SRC=\"" + contextPath
					+ "/resource/image/leftStep.GIF\"></a>");
			htmlBuilder.append("</td>");

			htmlBuilder.append("<td class='table-td'>");
			htmlBuilder.append("<a href=\"javascript:actProcess.doOrderChange("
					+ index + ", 'n')\"><img border=0 SRC=\"" + contextPath
					+ "/resource/image/rightStep.GIF\"></a>");
			htmlBuilder.append("</td>");
			htmlBuilder.append("</tr>");

		}
		htmlBuilder.append("</table>");

		return htmlBuilder.toString();

	}
}
