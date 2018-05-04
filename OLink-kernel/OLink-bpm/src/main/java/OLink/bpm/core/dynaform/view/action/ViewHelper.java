package OLink.bpm.core.dynaform.view.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.condition.FilterConditionParser;
import OLink.bpm.core.dynaform.view.ejb.type.CalendarType;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.resource.ejb.ResourceProcess;
import OLink.bpm.core.resource.ejb.ResourceVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.workcalendar.calendar.ejb.CalendarVO;
import OLink.bpm.util.*;
import OLink.bpm.core.dynaform.document.ejb.Item;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.util.CreateProcessException;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.FormField;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.form.ejb.ViewDialogField;
import OLink.bpm.util.web.DWRHtmlUtils;

import com.opensymphony.xwork.ActionContext;

public class ViewHelper extends BaseHelper<View> {
	private int displayType;

	// private static final Logger log = Logger.getLogger(ViewHelper.class);

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public ViewHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ViewProcess.class));
	}

	/**
	 * 返回全部符号
	 * 
	 * @return
	 */
	public static Map<String, String> getALL_SYMBOL() {
		return ALL_SYMBOL;
	}

	public static void setALL_SYMBOL(Map<String, String> all_symbol) {
		if (all_symbol != null)
			ALL_SYMBOL = all_symbol;
	}

	public static Map<String, String> getCOMPARE_SYMBOL() {
		return COMPARE_SYMBOL;
	}

	public static void setCOMPARE_SYMBOL(Map<String, String> compare_symbol) {
		if (compare_symbol != null)
			COMPARE_SYMBOL = compare_symbol;
	}

	public static Map<String, String> getOPERATOR_SYMBOL() {
		return OPERATOR_SYMBOL;
	}

	public static void setOPERATOR_SYMBOL(Map<String, String> operator_symbol) {
		if (operator_symbol != null)
			OPERATOR_SYMBOL = operator_symbol;
	}

	public static Map<String, String> getRELATION_SYMBOL() {
		return RELATION_SYMBOL;
	}

	public static void setRELATION_SYMBOL(Map<String, String> relation_symbol) {
		if (relation_symbol != null)
			RELATION_SYMBOL = relation_symbol;
	}

	private static Map<String, String> RELATION_SYMBOL = new LinkedHashMap<String, String>();

	private static Map<String, String> OPERATOR_SYMBOL = new LinkedHashMap<String, String>();

	private static Map<String, String> COMPARE_SYMBOL = new LinkedHashMap<String, String>();

	private static Map<String, String> ALL_SYMBOL = new LinkedHashMap<String, String>();

	static {
		RELATION_SYMBOL.put("AND", "AND");
		RELATION_SYMBOL.put("OR", "OR");

		OPERATOR_SYMBOL.put("+", "+");
		OPERATOR_SYMBOL.put("-", "-");
		OPERATOR_SYMBOL.put("*", "*");
		OPERATOR_SYMBOL.put("/", "/");

		COMPARE_SYMBOL.put("LIKE", "LIKE");
		COMPARE_SYMBOL.put(">", ">");
		COMPARE_SYMBOL.put(">=", ">=");
		COMPARE_SYMBOL.put("<", "<");
		COMPARE_SYMBOL.put("<=", "<=");
		COMPARE_SYMBOL.put("=", "=");
		COMPARE_SYMBOL.put("IN", "IN");
		COMPARE_SYMBOL.put("NOT IN", "NOT IN");

		ALL_SYMBOL.putAll(RELATION_SYMBOL);
		ALL_SYMBOL.putAll(OPERATOR_SYMBOL);
		ALL_SYMBOL.putAll(COMPARE_SYMBOL);
	}

	/**
	 * 根据所属模块以及应用标识查询,返回视图集合
	 * 
	 * @param application
	 *            应用标识
	 * @return 视图集合
	 * @throws Exception
	 */
	public Collection<View> get_viewList(String application) throws Exception {
		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess((ViewProcess.class));
		return vp.getViewsByModule(this.getModuleid(), application);
	}

	/**
	 * 根据相关视图主键查询,返回样式 id
	 * 
	 * @param viewid
	 *            视图主键
	 * @return 样式 id
	 * @throws Exception
	 */
	public static String get_Styleid(String viewid) throws Exception {
		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess((ViewProcess.class));
		View vw = (View) vp.doView(viewid);
		if (vw != null && vw.getStyle() != null) {
			return vw.getStyle().getId();
		} else
			return null;
	}

	/**
	 * 根据应用标识查询,返回树型菜单(Resource)集合
	 * 
	 * @param application
	 *            应用标识
	 * @return 树型菜单集合
	 * @throws Exception
	 */
	public Map<String, String> get_MenuTree(String application) throws Exception {
		ResourceProcess rp = (ResourceProcess) ProcessFactory.createProcess((ResourceProcess.class));
		Collection<ResourceVO> dc = rp.doSimpleQuery(null, application);
		Map<String, String> dm = rp.deepSearchMenuTree(dc, null, null, 0);
		return dm;
	}

	/**
	 * 根据所属模块以及应用标识查询,返回查询表单
	 * 
	 * @param application
	 *            应用标识
	 * @return 查询表单
	 * @throws Exception
	 */
	public Collection<Form> get_searchForm(String application) throws Exception {
		FormProcess fp = (FormProcess) ProcessFactory.createProcess((FormProcess.class));
		Collection<Form> searchForms = fp.getSearchFormsByModule(moduleid, application);
		return searchForms;
	}

	public String convertValuesMapToPage(Map<String, String> valuesMap) throws Exception {
		StringBuffer html = new StringBuffer();
		for (Iterator<Entry<String, String>> iter = valuesMap.entrySet().iterator(); iter.hasNext();) {
			Entry<String, String> entry = iter.next();
			html.append("<input type='hidden' name='").append(entry.getKey()).append("' value='").append(
					entry.getValue()).append("'").append(" />");
		}
		return html.toString();
	}

	/**
	 * 返回字符串为重定义后的html,以html显示视图
	 * 
	 * @param formid
	 *            表单
	 * @param viewid
	 *            视图
	 * @param actfield
	 *            按钮
	 * @param userid
	 *            user
	 * @param valuesMap
	 *            值
	 * @param application
	 *            应用标识
	 * @return 字符串为重定义后的html,以html显示视图
	 * @throws Exception
	 */
	public String displayViewHtml(String formid, String viewid, String actfield, String userid,
			Map<String, String> valuesMap, String application) throws Exception {
		try {
			// //PersistenceUtils.getSessionSignal().sessionSignal++;
			DocumentProcess dp = (DocumentProcess) ProcessFactory.createProcess(DocumentProcess.class);

			ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);

			View view = (View) vp.doView(viewid);

			FormProcess fp = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

			Form form = (Form) fp.doView(formid);

			// 显示Column
			StringBuffer html = new StringBuffer();
			html.append("<table width='100%' style='position:relative; z-index:1'>");
			html.append("<tr>");
			Collection<Column> columns = view.getColumns();
			for (Iterator<Column> iter = columns.iterator(); iter.hasNext();) {
				Column clm = iter.next();
				html.append("<td").append(
						clm.getWidth() != null && clm.getWidth().trim().length() > 0 ? " width='" + clm.getWidth()
								+ "'" : "").append(">").append(clm.getName()).append("</td>");
			}

			String js = view.getFilterScript();
			if (js != null && js.trim().length() > 0) {
				ParamsTable params = new ParamsTable();
				params.putAll(valuesMap);

				UserProcess up = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
				UserVO uservo = (UserVO) up.doView(userid);
				WebUser user = new WebUser(uservo);
				ArrayList<ValidateMessage> errors = new ArrayList<ValidateMessage>();

				Document currdoc = new Document();
				currdoc = form.createDocument(currdoc, params, false, user);

				IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), application);
				runner.initBSFManager(currdoc, params, user, errors);

				StringBuffer label = new StringBuffer();
				label.append("VIEW(").append(view.getId()).append(")." + view.getName()).append(".FilterScript");

				Object result = runner.run(label.toString(), js);

				if (result != null && result instanceof String) {
					String dql = (String) result;
					DataPackage<Document> datas = dp.queryByDQL(dql, user.getDomainid());
					// 显示数据
					if (datas != null && datas.datas != null)
						for (Iterator<Document> iter = datas.datas.iterator(); iter.hasNext();) {
							Document doc = iter.next();
							runner.initBSFManager(doc, params, user, errors);
							html.append("<tr bgcolor='#999999' style='cursor:hand' onclick='document.getElementById(\""
									+ actfield + "\").value=\"" + doc.getId() + "\";dy_refresh(\"" + actfield
									+ "\");cClick();'>");
							for (Iterator<Column> iter2 = columns.iterator(); iter2.hasNext();) {
								Column col = iter2.next();
								if (col.getType() != null && col.getType().equals(Column.COLUMN_TYPE_SCRIPT)) {

									StringBuffer clabel = new StringBuffer();
									clabel.append("VIEW").append(".").append(view.getName()).append(".COLUMN(").append(
											col.getId()).append(")." + col.getName()).append("ValueScript");

									html.append("<td nowarp>").append(
											runner.run(clabel.toString(), col.getValueScript())).append("</td>");
								} else if (col.getType() != null && col.getType().equals(Column.COLUMN_TYPE_FIELD)) {
									html.append("<td nowarp>").append(doc.getItemValueAsString(col.getFieldName()))
											.append("</td>");
								}
							}
							html.append("</tr>");
						}
				}
			}

			html.append("</table>");
			return html.toString();

		} finally {
			// //PersistenceUtils.getSessionSignal().sessionSignal--;
			PersistenceUtils.closeSession();
		}
	}

	public Map<String, String> get_viewListByModules(String moduleId) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess((ViewProcess.class));
		Collection<View> viewList = vp.getViewsByModule(moduleId, null);
		if (viewList != null) {
			for (Iterator<View> iterator = viewList.iterator(); iterator.hasNext();) {
				View view = iterator.next();
				map.put(view.getId(), view.getName());
			}
		}
		return map;
	}

	/**
	 * 给view生成的排序的checkbox
	 * 
	 * @param moduleId
	 * @param divid
	 * @return
	 * @throws Exception
	 */
	public String getViewNameCheckBox(String moduleId, String divid, String application) throws Exception {
		ViewProcess fp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		Collection<View> col = fp.getViewsByModule(moduleId, application);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if (col != null) {
			for (Iterator<View> iter = col.iterator(); iter.hasNext();) {
				View view = iter.next();
				map.put(view.getId(), view.getName());
			}
		}
		String[] str = new String[10];
		return DWRHtmlUtils.createFiledCheckbox(map, divid, str);
	}

	public Map<String, String> getSystemVariable() throws Exception {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "{*[Select]*}");

		map.put(FilterConditionParser.SYSVAR_CURRACC, "{*[Current]*}{*[User]*}{*[Account]*}");
		map.put(FilterConditionParser.SYSVAR_CURRNAME, "{*[Current]*}{*[User]*}{*[Name]*}");
		map.put(FilterConditionParser.SYSVAR_CURRUSERID, "{*[Current]*}{*[User]*}ID");
		map.put(FilterConditionParser.SYSVAR_CURRROLEID, "{*[Current]*}{*[User]*}{*[Role]*}ID");
		map.put(FilterConditionParser.SYSVAR_CURRDEPTID, "{*[Current]*}{*[User]*}{*[Department]*}ID");
		map.put(FilterConditionParser.SYSVAR_SESSION, "{*[Session]*}");

		return map;
	}

	/**
	 * 返回月视图信息组成的HTML字串
	 * 
	 * @param view
	 *            视图对象
	 * @param params
	 *            参数体对象
	 * @param user
	 *            当前用户对象
	 * @param applicationid
	 *            软件ID
	 * @param yearIndex
	 *            年
	 * @param monthIndex
	 *            月
	 * @return 返回月视图信息组成的HTML字串
	 * @throws Exception
	 */
	public String toMonthHtml(View view, ParamsTable params, WebUser user, String applicationid, int yearIndex,
			int monthIndex, boolean isPreview) throws Exception {
		StringBuffer buf = new StringBuffer();
		String rootPath = params.getContextPath();
		buf.append(head(rootPath));
		buf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("<tr>");
		buf.append("<td class=\"eventHead\">");
		buf.append(tHead(params.getParameterAsString("viewMode"), yearIndex, monthIndex, yearIndex + "-" + monthIndex,
				rootPath));
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("<tr>");
		buf.append("<td>");
		buf.append(tMonthBodyHtml(view, params, user, applicationid, yearIndex, monthIndex, displayType,
				isPreview ? false : true));
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}

	private String head(String rootPath) {
		StringBuffer buf = new StringBuffer();
		buf.append("<table width='100%' border='0' cellspacing='0' cellpadding='0' class='eventHead'>");
		buf.append("<tr>");
		buf
				.append("<td nowrap width=\"50%\" style='padding-top:3px;'>&nbsp;&nbsp;<a	href='javascript:showActions(\"DAYVIEW\")' class=\"link\" title=\"{*[Day]*}{*[View]*}\">"
						+ "<img src='"
						+ rootPath
						+ "/portal/share/icon/16x16_0180/calendar_view_day.png'	class='calen' width=\"16\" height=\"16\" border=\"0\" "
						+ " align=\"absmiddle\" alt=\"{*[Day]*}{*[View]*}\">{*[Day]*}{*[View]*}</a>&nbsp;&nbsp;<a href='javascript:showActions(\"WEEKVIEW\")' class=\"link\" title=\"{*[Week]*}{*[View]*}\">"
						+ "<img src='"
						+ rootPath
						+ "/portal/share/icon/16x16_0180/calendar_view_week.png' class=\"nor-week\" width=\"16\" height=\"16\" border=\"0\""
						+ " align=\"absmiddle\" alt=\"{*[Week]*}{*[View]*}\">{*[Week]*}{*[View]*}</a>&nbsp;&nbsp;<a href='javascript:showActions(\"MONTHVIEW\")' class=\"link\" title=\"{*[Month]*}{*[View]*}\">"
						+ "<img src='"
						+ rootPath
						+ "/portal/share/icon/16x16_0180/calendar_view_month.png' class=\"sel-month\" width=\"16\" height=\"16\" border=\"0\""
						+ " align=\"absmiddle\" title=\"{*[Month]*}{*[View]*}\">{*[Month]*}{*[View]*}</a></td>");
		buf.append("<td align=\"right\" nowrap></td>");
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}

	private String tHead(String viewMode, int year, int month, String headString, String rootPath) {
		StringBuffer buf = new StringBuffer();
		String llab1 = "previousYear";
		String lab1 = "previousMonth";
		String llab2 = "nextYear";
		String lab2 = "nextMonth";
		if ("WEEKVIEW".equals(viewMode)) {
			llab1 = "previousMonth";
			lab1 = "previousWeek";
			llab2 = "nextMonth";
			lab2 = "nextWeek";
		} else if ("DAYVIEW".equals(viewMode)) {
			llab1 = "previousMonth";
			lab1 = "previousDay";
			llab2 = "nextMonth";
			lab2 = "nextDay";
		}
		buf.append("<table width=\"220\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\">");
		buf
				.append("<tr><td width=\"16\"><div align=\"center\"><a href=\"javascript:showAction('"
						+ viewMode
						+ "','"
						+ llab1
						+ "')\""
						+ "title=\"{*["
						+ llab1
						+ "]*}\"><img src='"
						+ rootPath
						+ "/portal/share/icon/16x16_0060/arrow_left.png' class=\"backward\" width=\"16\" height=\"16\" "
						+ "alt=\"{*["
						+ llab1
						+ "]*}\" width=\"16\" height=\"10\" border=\"0\" align=\"absmiddle\"></a></div></td><td width=\"16\">"
						+ "<div align=\"center\"><a href=\"javascript:showAction('"
						+ viewMode
						+ "','"
						+ lab1
						+ "')\" title=\"{*["
						+ lab1
						+ "]*}\">"
						+ "<img src='"
						+ rootPath
						+ "/portal/share/icon/16x16_0760/resultset_previous.png' class=\"previous\" alt=\"{*["
						+ lab1
						+ "]*}\" width=\"16\" height=\"16\" border=\"0\""
						+ " align=\"absmiddle\"></a></div></td><td class=\"eventTitle\"><div align=\"center\">"
						+ headString
						+ "</div></td><td width=\"16\">"
						+ "<div align=\"center\"><a href=\"javascript:showAction('"
						+ viewMode
						+ "','"
						+ lab2
						+ "')\" title=\"{*["
						+ lab2
						+ "]*}\"><img "
						+ "src='"
						+ rootPath
						+ "/portal/share/icon/16x16_0760/resultset_next.png' class=\"next\" alt=\"{*["
						+ lab2
						+ "]*}\" width=\"16\" height=\"16\" border=\"0\" "
						+ " align=\"absmiddle\"></a></div></td><td width=\"17\"><div align=\"center\"><a href=\"javascript:showAction('"
						+ viewMode + "','" + llab2 + "')\"" + " title=\"{*[" + llab2 + "]*}\"><img src='" + rootPath
						+ "/portal/share/icon/16x16_0060/arrow_right.png' class=\"fastfor\" alt=\"{*[" + llab2
						+ "]*}\" width=\"16\" height=\"16\" "
						+ " border=\"0\" align=\"absmiddle\"></a></div></td></tr>");
		buf.append("</table>");
		return buf.toString();
	}

	/**
	 * 
	 * @param yearIndex
	 * @param monthIndex
	 * @param href
	 * @param dayInfo
	 * @return
	 * @throws Exception
	 */
	public String tMonthBodyHtml(View view, ParamsTable params, WebUser user, String applicationid, int yearIndex,
			int monthIndex, int viewType, boolean isShowDayInfo) throws Exception {
		Calendar thisMonth = CalendarVO.getThisMonth(yearIndex, monthIndex - 1);
		StringBuffer htmlBuilder = new StringBuffer();
		thisMonth.set(Calendar.DAY_OF_MONTH, 1);
		int firstIndex = thisMonth.get(Calendar.DAY_OF_WEEK) - 1;
		int maxIndex = thisMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
		final Calendar cld = thisMonth;
		cld.set(Calendar.HOUR_OF_DAY, 0);
		cld.set(Calendar.MINUTE, 0);
		cld.set(Calendar.SECOND, 0);
		Date stDate = cld.getTime();
		cld.set(Calendar.DAY_OF_MONTH, cld.getActualMaximum(Calendar.DAY_OF_MONTH));
		cld.set(Calendar.HOUR_OF_DAY, 23);
		cld.set(Calendar.MINUTE, 59);
		cld.set(Calendar.SECOND, 59);
		Collection<Document> datas = getDatas(view, params, user, applicationid, stDate, cld.getTime());
		IRunner jsrun = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
		htmlBuilder.append("<table style='border:1px solid #e6e6e6;' width='100%' cellspacing='0' cellpadding='0' class=\"eventBg\">");
		htmlBuilder.append("<tr class='weekDay'>");
		htmlBuilder.append("<td align='center' width=\"14%\" nowrap class=\"eventDOW\">{*[Sunday]*}</td>");
		htmlBuilder.append("<td align='center' width=\"14%\" nowrap class=\"eventDOW\">{*[Monday]*}</td>");
		htmlBuilder.append("<td align='center' width=\"14%\" nowrap class=\"eventDOW\">{*[Tuesday]*}</td>");
		htmlBuilder.append("<td align='center' width=\"14%\" nowrap class=\"eventDOW\">{*[Wednesday]*}</td>");
		htmlBuilder.append("<td align='center' width=\"14%\" nowrap class=\"eventDOW\">{*[Thursday]*}</td>");
		htmlBuilder.append("<td align='center' width=\"14%\" nowrap class=\"eventDOW\">{*[Friday]*}</td>");
		htmlBuilder.append("<td align='center' width=\"14%\" nowrap class=\"eventDOW\">{*[Saturday]*}</td>");
		htmlBuilder.append("</tr>");
		int day;// 日			
		for (int i = 0; i < 6; i++) {
			htmlBuilder.append("<tr height='100'>");
			for (int j = 0; j < 7; j++) {
				day = (i * 7 + j) - firstIndex + 1;
				if (day > 0 && day <= maxIndex) {
					htmlBuilder.append("<td style='border-left:1px solid #e6e6e6;border-top:1px solid #e6e6e6;' valign='top' onMouseOver=\"this.bgColor='#fefed8';ShowDayCube("+day+");\""
							+ " onMouseOut=\"this.bgColor='#ffffff';HideDayCube("+day+");\" id='cal" + day + "'>");
					htmlBuilder.append("<table width='100%' border='0' cellspacing='0' cellpadding='0'>");
					htmlBuilder.append("<tr style='height:16px;'>");
					htmlBuilder.append("<td align='right'>");
					if (viewType == ViewAction.DO_DIALOG_VIEW) {
						htmlBuilder.append(day);
					} else {
						htmlBuilder.append("<a href=\"javascript:ShowDayView('" + yearIndex + "-" + monthIndex + "-"
								+ day + "')\" class='eventDateImg' id='eventDateImg"+day+"'></a>");
						htmlBuilder.append("<a href=\"javascript:ShowDayView('" + yearIndex + "-" + monthIndex + "-"
								+ day + "')\" class='eventDate'>" + day + "</a>");
					}
					htmlBuilder.append("</td>");
					htmlBuilder.append("</tr>");
					htmlBuilder.append("<tr>");
					String dayInfo = getDayViewList(jsrun, view, datas, params, user, thisMonth, day, viewType, yearIndex, monthIndex);	
					String taskInfo= getTaskViewList(jsrun, view, datas, params, user, thisMonth, day, viewType, yearIndex, monthIndex);					
					htmlBuilder.append("<td valign='top' align='left'><div onclick='showTaskContent("+day+");' style='padding:2px;height:80px;'>"
							+ (isShowDayInfo ? dayInfo : "") + "</div><div id='taskContent"+day+"' style='display:none'>"
							+ (isShowDayInfo ? taskInfo : "") + "</div></td>");
					htmlBuilder.append("</tr>");
					htmlBuilder.append("</table>");
					htmlBuilder.append("</td>");
				} else {
					htmlBuilder.append("<td style='border-left:1px solid #e6e6e6;border-top:1px solid #ddd;' valign='top' class='eventTD'>&nbsp;</td>");
				}
			}
			htmlBuilder.append("</tr>");
		}
		htmlBuilder.append("</table>");
		return htmlBuilder.toString();
	}

	public String tWeekBodyHtml(View view, ParamsTable params, WebUser user, String applicationid, final Calendar cld,int yearIndex,int monthIndex,
			int viewType, boolean isShowInfo) throws Exception {
		StringBuffer htmlBuilder = new StringBuffer();
		Date startDate = cld.getTime();
		cld.add(Calendar.DAY_OF_MONTH, 7);
		Collection<Document> datas = getDatas(view, params, user, applicationid, startDate, cld.getTime());
		cld.add(Calendar.DAY_OF_MONTH, -7);
		IRunner jsrun = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
		htmlBuilder
				.append("<table border='1' cellpadding='1' cellspacing='0' bgcolor='#f8f8f8' frame='void' bordercolordark='#FFFFFF'"
						+ " bordercolorlight='#DDDDDD' style='border-top:1 #DDDDDD solid;border-left:1 #DDDDDD solid;' width=100%>");
		htmlBuilder.append("<tr height=24>");
		htmlBuilder.append("<td width='120' align='center' nowrap>{*[Week]*}</td>");
		htmlBuilder.append("<td align='center' nowrap>{*[Content]*}</td>");
		htmlBuilder.append("</tr>");
		htmlBuilder.append("</table>");
		htmlBuilder
				.append("<table border='0' cellpadding='3' cellspacing='1' bgcolor='#f8f8f8' align='center' width='100%'>");
		for (int i = 0; i < 7; i++) {
			htmlBuilder.append("<tr height=80>");
			htmlBuilder.append("<td width='120' align='center' bgcolor='#FFFFEE' nowrap>");
			htmlBuilder.append("<font class='t9'>");
			switch (i) {
			case 0:
				htmlBuilder.append("{*[Sunday]*}");
				break;
			case 1:
				htmlBuilder.append("{*[Monday]*}");
				break;
			case 2:
				htmlBuilder.append("{*[Tuesday]*}");
				break;
			case 3:
				htmlBuilder.append("{*[Wednesday]*}");
				break;
			case 4:
				htmlBuilder.append("{*[Thursday]*}");
				break;
			case 5:
				htmlBuilder.append("{*[Friday]*}");
				break;
			case 6:
				htmlBuilder.append("{*[Saturday]*}");
				break;
			}
			htmlBuilder.append("<br>" + DateUtil.format(cld.getTime(), "yyyy-MM-dd") + "</font>");
			htmlBuilder.append("</td>");
			htmlBuilder.append("<td bgcolor='#ffffff' nowrap>");
			String info = getDayViewList(jsrun, view, datas, params, user, cld, cld.get(Calendar.DAY_OF_MONTH),
					viewType, yearIndex, monthIndex);
			htmlBuilder.append("<font size='2px'>" + (isShowInfo ? info : "") + "</font>");
			htmlBuilder.append("</td>");
			htmlBuilder.append("</tr>");
			cld.add(Calendar.DAY_OF_MONTH, 1);
		}
		htmlBuilder.append("</table>");
		return htmlBuilder.toString();
	}

	/**
	 * 返回周视图信息组成的HTML字串
	 * 
	 * @param view
	 *            视图对象
	 * @param params
	 *            参数体对象
	 * @param user
	 *            当前用户对象
	 * @param applicationid
	 *            软件ID
	 * @param yearIndex
	 *            年
	 * @param monthIndex
	 *            月
	 * @param dayIndex
	 *            日（基准日）
	 * @return 返回周视图信息组成的HTML字串
	 * @throws Exception
	 */
	public String toWeekHtml(View view, ParamsTable params, WebUser user, String applicationid, int yearIndex,
			int monthIndex, int dayIndex, boolean isPreview) throws Exception {
		StringBuffer buf = new StringBuffer();
		final Calendar cld = Calendar.getInstance();
		cld.set(Calendar.YEAR, yearIndex);
		cld.set(Calendar.MONTH, monthIndex - 1);
		cld.set(Calendar.DAY_OF_MONTH, dayIndex);
		int firstIndex = cld.get(Calendar.DAY_OF_WEEK) - 1;
		cld.add(Calendar.DAY_OF_MONTH, -firstIndex);
		cld.set(Calendar.HOUR_OF_DAY, 0);
		cld.set(Calendar.MINUTE, 0);
		cld.set(Calendar.SECOND, 0);
		Date stDate = cld.getTime();
		String body = tWeekBodyHtml(view, params, user, applicationid, cld, yearIndex,monthIndex, displayType, isPreview ? false : true);
		cld.add(Calendar.DAY_OF_MONTH, -1);

		String rootPath = params.getContextPath();
		buf.append(head(rootPath));
		buf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("<tr>");
		buf.append("<td class=\"eventHead\">");
		buf.append(tHead(params.getParameterAsString("viewMode"), yearIndex, monthIndex, DateUtil.format(stDate,
				"yyyy-MM-dd")
				+ "~" + DateUtil.format(cld.getTime(), "yyyy-MM-dd"), rootPath));
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("<tr>");
		buf.append("<td>");
		buf.append(body);
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}

	/**
	 * 返回日视图信息组成的HTML字串
	 * 
	 * @param view
	 *            视图对象
	 * @param params
	 *            参数体对象
	 * @param user
	 *            当前用户对象
	 * @param applicationid
	 *            软件ID
	 * @param yearIndex
	 *            年
	 * @param monthIndex
	 *            月
	 * @param dayIndex
	 *            日
	 * @return 返回日视图信息组成的HTML字串
	 * @throws Exception
	 */
	public String toDayHtml(View view, ParamsTable params, WebUser user, String applicationid, int yearIndex,
			int monthIndex, int dayIndex, boolean isPreview) throws Exception {
		StringBuffer buf = new StringBuffer();
		String rootPath = params.getContextPath();
		buf.append(head(rootPath));
		buf.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		buf.append("<tr>");
		buf.append("<td class=\"eventHead\">");
		buf.append(tHead(params.getParameterAsString("viewMode"), yearIndex, monthIndex, yearIndex + "-" + monthIndex
				+ "-" + dayIndex, rootPath));
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("<tr>");
		buf.append("<td>");
		buf.append(tDayBodyHtml(view, params, user, applicationid, yearIndex, monthIndex, dayIndex, displayType,
				isPreview ? false : true));
		buf.append("</td>");
		buf.append("</tr>");
		buf.append("</table>");
		return buf.toString();
	}

	public String tDayBodyHtml(View view, ParamsTable params, WebUser user, String applicationid, int yearIndex,
			int monthIndex, int dayIndex, int viewType, boolean isShowInfo) throws Exception {
		Calendar cld = Calendar.getInstance();
		StringBuffer htmlBuilder = new StringBuffer();
		cld.set(Calendar.YEAR, yearIndex);
		cld.set(Calendar.MONTH, monthIndex - 1);
		cld.set(Calendar.DAY_OF_MONTH, dayIndex);
		cld.set(Calendar.HOUR_OF_DAY, 0);
		cld.set(Calendar.MINUTE, 0);
		cld.set(Calendar.SECOND, 0);
		Date stDate = cld.getTime();
		cld.add(Calendar.DAY_OF_MONTH, 1);
		Collection<Document> datas = getDatas(view, params, user, applicationid, stDate, cld.getTime());
		IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
		cld.add(Calendar.DAY_OF_MONTH, -1);
		htmlBuilder
				.append("<table border='1' cellpadding='1' cellspacing='0' bgcolor='#f8f8f8' frame='void' bordercolordark='#FFFFFF'"
						+ " bordercolorlight='#DDDDDD' style='border-top:1 #DDDDDD solid;border-left:1 #DDDDDD solid;' width=100%>");
		htmlBuilder.append("<tr height=24>");
		htmlBuilder.append("<td width='120' align='center' nowrap>{*[Time]*}</td>");
		htmlBuilder.append("<td align='center' nowrap>{*[Content]*}</td>");
		htmlBuilder.append("</tr>");
		htmlBuilder.append("</table>");
		// htmlBuilder
		// .append("<div
		// style='overflow:scroll;overflow-x:hidden;width:100%;height:expression(document.body.clientHeight);"
		// +
		// "background:#FFFFFF;border-right:1 #DDDDDD solid;border-bottom:1
		// #DDDDDD #FFFFFF inset;border-left:1 #DDDDDD solid;"
		// +
		// "border-top:1 #DDDDDD solid;' id=calMonthArea>");
		htmlBuilder
				.append("<table border='0' cellpadding='1' cellspacing='1' bgcolor='#EEEEEE' align='center' width='100%' style='line-height:20px'>");
		htmlBuilder.append("<tr height=60>");
		htmlBuilder.append("<td width='120' bgcolor='#FFFFEE' nowrap align='center'>00:00 -- 08:00</td>");
		htmlBuilder.append("<td bgcolor='#ffffff' nowrap>");
		htmlBuilder.append(isShowInfo ? getDayTimeList(runner, view, datas, params, user, cld, 8, viewType) : "");
		htmlBuilder.append("</td>");
		htmlBuilder.append("</tr>");
		cld.set(Calendar.HOUR_OF_DAY, 8);
		int sTime = 8;
		for (int i = 0; i < 11; i++) {
			htmlBuilder.append("<tr height=60>");
			htmlBuilder.append("<td width='120' bgcolor='#FFFFEE' nowrap align='center'>" + (sTime++) + ":00 -- "
					+ sTime + ":00</td>");
			htmlBuilder.append("<td bgcolor='#ffffff' nowrap>");
			htmlBuilder.append(isShowInfo ? getDayTimeList(runner, view, datas, params, user, cld, 1, viewType) : "");
			htmlBuilder.append("</td>");
			htmlBuilder.append("</tr>");
			cld.set(Calendar.HOUR_OF_DAY, sTime);
		}
		htmlBuilder.append("<tr height=60>");
		htmlBuilder.append("<td width='120' bgcolor='#FFFFEE' nowrap align='center'>" + sTime + ":00 -- 00:00</td>");
		htmlBuilder.append("<td bgcolor='#ffffff' nowrap>");
		htmlBuilder.append(isShowInfo ? getDayTimeList(runner, view, datas, params, user, cld, 8, viewType) : "");
		htmlBuilder.append("</td>");
		htmlBuilder.append("</tr>");
		htmlBuilder.append("</table>");
		// htmlBuilder.append("</div>");
		return htmlBuilder.toString();
	}

	private String getDayTimeList(IRunner runner, View view, Collection<Document> datas, ParamsTable params,
			WebUser user, final Calendar cld, int step, int viewType) {
		StringBuffer buf = new StringBuffer();
		String templateForm = "";
		if(View.DISPLAY_TYPE_TEMPLATEFORM.equals(view.getDisplayType())){
			templateForm = view.getTemplateForm();
		}
		if (datas != null) {
			Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
			Iterator<Document> it = datas.iterator();
			try {
				StringBuffer colBuf = new StringBuffer();
				// String fieldName = view.getRelationDateColum();
				Object column = view.getViewTypeImpl().getColumnMapping().get(CalendarType.DEFAULT_KEY_FIELDS[0]);
				String fieldName = "";
				if (column instanceof String)
					fieldName = column.toString();
				else if (column instanceof Column)
					fieldName = ((Column) column).getFieldName();

				Date stDate = cld.getTime();
				cld.add(Calendar.HOUR, step);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String filterValue1 = format.format(stDate);
				String filterValue2 = format.format(cld.getTime());
				int i = 0;
				while (it.hasNext()) {
					Document doc = it.next();
					String value = getDocDateTimeValueAsString(doc, fieldName);
					if (filterValue1.compareTo(value) <= 0 && filterValue2.compareTo(value) > 0) {
						runner.initBSFManager(doc, params, user, errors);
						Iterator<Column> iter = view.getColumns().iterator();
						String list = "";
						while (iter.hasNext()) {
							Column col = iter.next();
							Object result = col.getText(doc, runner, user);
							if (!(col.getWidth() != null && col.getWidth().equals("0"))) {
								list += result + "&nbsp;";
							}
						}
						if (view.getReadonly().booleanValue()) {
							colBuf.append(list);
						} else {

							switch (viewType) {
							case 1: {
								colBuf.append("<a href=\"javascript:viewDoc('"

								+ doc.getId() + "','" + doc.getFormid() + "',"+ false + ",'"+ templateForm + "')\" class='eventDate'>");
								colBuf.append(list);
								colBuf.append("</a>");
							}
								break;
							case 2: {
								DocumentProcess proxy = (DocumentProcess) getProcess();
								Document doc2 = (Document) proxy.doView(doc.getId());
								IRunner jsrun = JavaScriptFactory.getInstance(params.getSessionid(), view
										.getApplicationid());
								jsrun.initBSFManager(doc2, params, user, errors);
								Collection<Column> columns = view.getColumns();
								StringBuffer valuesMap = new StringBuffer("{");
								Iterator<Column> it2 = columns.iterator();
								while (it2.hasNext()) {
									Column key = it2.next();
									Object value1 = key.getText(doc, jsrun, user);
									valuesMap.append("'").append(key.getId()).append("':'").append(
											StringUtil.encodeHTML(value1.toString())).append("',");
								}
								valuesMap.setLength(valuesMap.length() - 1);
								valuesMap.append("}");
								colBuf.append("<a href=\"javascript:ev_selectone(" + valuesMap.toString()
										+ ")\" class='eventDate'>");
								colBuf.append(list);
								colBuf.append("</a>");

							}
								break;
							default:
							}

						}
						i++;
						colBuf.append("<br/>");
						if (i >= 5) {
							colBuf
									.append("<div align='right'><a href=\"javascript:ShowDayMore()\" class='eventDate'>More>></a></div>");
							break;
						}
					}
				}
				if (colBuf.length() > 0)
					buf.append(colBuf);
			} catch (Exception e) {
				e.printStackTrace();
				buf.append("&nbsp;");
			}
		} else {
			buf.append("&nbsp;");
		}
		return buf.toString();
	}

	private String getDayViewList(IRunner runner, View view, Collection<Document> datas, ParamsTable params,
			WebUser user, final Calendar cld, int day, int viewType,int yearIndex,int monthIndex) {
		StringBuffer buf = new StringBuffer();
		String templateForm = "";
		if(View.DISPLAY_TYPE_TEMPLATEFORM.equals(view.getDisplayType())){
			templateForm = view.getTemplateForm();
		}
		if (datas != null) {
			Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
			Iterator<Document> it = datas.iterator();
			try {
				StringBuffer colBuf = new StringBuffer();
				// String fieldName = view.getRelationDateColum();
				Object column = view.getViewTypeImpl().getColumnMapping().get(CalendarType.DEFAULT_KEY_FIELDS[0]);
				String fieldName = "";
				if (column instanceof String)
					fieldName = column.toString();
				else if (column instanceof Column)
					fieldName = ((Column) column).getFieldName();

				cld.set(Calendar.DAY_OF_MONTH, day);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String filterValue = format.format(cld.getTime());
				int i = 0;
				while (it.hasNext()) {
					Document doc = it.next();
					String value;
					value = doc.getValueByField(fieldName);
					if (filterValue.equals(value)) {
						runner.initBSFManager(doc, params, user, errors);
						Iterator<Column> iter = view.getColumns().iterator();
							
						String list = "";
						while (iter.hasNext()) {
							Column col = iter.next();
							if (!isHiddenColumn(col, runner)) {
								Object result = col.getText(doc, runner, user);
								if (!(col.getWidth() != null && col.getWidth().equals("0"))) {
									list += result + "&nbsp;";
								}
							}
						}
						if (view.getReadonly().booleanValue()) {							
							colBuf.append("<div style='margin:1px;color:#4664a2;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>");	
							colBuf.append("<a href=\"javascript:ShowDayView('" + yearIndex + "-" + monthIndex + "-"	+ day + "')\">");	
							colBuf.append(list);
							colBuf.append("</a>");
							colBuf.append("</div>");
						} else {
							switch (viewType) {
							case 1: {
								colBuf.append("<div style='margin:1px;color:#4664a2;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>");
								colBuf.append("<a href=\"javascript:viewDoc('"
								+ doc.getId() + "','" + doc.getFormid() + "',"+ false + ",'"+ templateForm + "')\" class='eventDate'>");															
								colBuf.append(list);								
								colBuf.append("</a>");
								colBuf.append("</div>");
							}
								break;
							case 2: {
								DocumentProcess proxy = createDocumentProcess(view.getApplicationid());
								Document doc2 = (Document) proxy.doView(doc.getId());
								IRunner jsrun = JavaScriptFactory.getInstance(params.getSessionid(), view
										.getApplicationid());
								jsrun.initBSFManager(doc2, params, user, errors);
								Collection<Column> columns = view.getColumns();
								StringBuffer valuesMap = new StringBuffer("{");
								Iterator<Column> it2 = columns.iterator();
								while (it2.hasNext()) {
									Column key = it2.next();
									Object value1 = key.getText(doc, jsrun, user);
									valuesMap.append("'").append(key.getId()).append("':'").append(
											StringUtil.encodeHTML(value1.toString())).append("',");
								}
								valuesMap.setLength(valuesMap.length() - 1);
								valuesMap.append("}");
								colBuf.append("<div style='margin:1px;color:#4664a2;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;'>");
								colBuf.append("<a href=\"javascript:ev_selectone(" + valuesMap.toString()
										+ ")\" class='eventDate'>");
								colBuf.append(list);								
								colBuf.append("</a>");
								colBuf.append("</div>");

							}
								break;
							default:
							}
						}
						i++;
						//colBuf.append("</div>");
						if (i >= 4) {
							if (viewType != ViewAction.DO_DIALOG_VIEW) {
								colBuf.append("<div align='right'><a href=\"javascript:ShowDayView('" + filterValue
										+ "')\" class='eventDate'>More>></a></div>");
							}
							break;
						}
					}
				}
				if (colBuf.length() > 0)
					buf.append(colBuf);
			} catch (Exception e) {
				e.printStackTrace();
				buf.append("&nbsp;");
			}
		} else {
			buf.append("&nbsp;");
		}
		return buf.toString();
	}
	
	
	private String getTaskViewList(IRunner runner, View view, Collection<Document> datas, ParamsTable params,
			WebUser user, final Calendar cld, int day, int viewType,int yearIndex,int monthIndex) {
		StringBuffer buf = new StringBuffer();
		String templateForm = "";
		if(View.DISPLAY_TYPE_TEMPLATEFORM.equals(view.getDisplayType())){
			templateForm = view.getTemplateForm();
		}
		if (datas != null) {
			Collection<ValidateMessage> errors = new ArrayList<ValidateMessage>();
			Iterator<Document> it = datas.iterator();
			try {
				StringBuffer colBuf = new StringBuffer();
				// String fieldName = view.getRelationDateColum();
				Object column = view.getViewTypeImpl().getColumnMapping().get(CalendarType.DEFAULT_KEY_FIELDS[0]);
				String fieldName = "";
				if (column instanceof String)
					fieldName = column.toString();
				else if (column instanceof Column)
					fieldName = ((Column) column).getFieldName();

				cld.set(Calendar.DAY_OF_MONTH, day);
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
				String filterValue = format.format(cld.getTime());
				int i = 0;
				while (it.hasNext()) {
					Document doc = it.next();
					String value;
					value = doc.getValueByField(fieldName);
					if (filterValue.equals(value)) {
						runner.initBSFManager(doc, params, user, errors);
						Iterator<Column> iter = view.getColumns().iterator();
							
						String list = "";
						while (iter.hasNext()) {
							Column col = iter.next();
							if (!isHiddenColumn(col, runner)) {
								Object result = col.getText(doc, runner, user);
								if (!(col.getWidth() != null && col.getWidth().equals("0"))) {
									list += result + "&nbsp;&nbsp;";
								}
							}
						}
						if (view.getReadonly().booleanValue()) {							
							colBuf.append("<div class='taskList'>");	
							colBuf.append("<a href=\"javascript:ShowDayView('" + yearIndex + "-" + monthIndex + "-"	+ day + "')\">");	
							colBuf.append(list);
							colBuf.append("</a>");
							colBuf.append("<div style='color:#848484;font-size:11px;'>由"+user.getLoginno()+"</div>");
							colBuf.append("</div>");
						} else {
							switch (viewType) {
							case 1: {
								colBuf.append("<div>");
								colBuf.append("<a href=\"javascript:viewDoc('"
								+ doc.getId() + "','" + doc.getFormid() + "',"+ false + ",'"+ templateForm + "')\" class='eventDate'>");															
								colBuf.append(list);								
								colBuf.append("</a>");
								colBuf.append("</div>");
							}
								break;
							case 2: {
								DocumentProcess proxy = createDocumentProcess(view.getApplicationid());
								Document doc2 = (Document) proxy.doView(doc.getId());
								IRunner jsrun = JavaScriptFactory.getInstance(params.getSessionid(), view
										.getApplicationid());
								jsrun.initBSFManager(doc2, params, user, errors);
								Collection<Column> columns = view.getColumns();
								StringBuffer valuesMap = new StringBuffer("{");
								Iterator<Column> it2 = columns.iterator();
								while (it2.hasNext()) {
									Column key = it2.next();
									Object value1 = key.getText(doc, jsrun, user);
									valuesMap.append("'").append(key.getId()).append("':'").append(
											StringUtil.encodeHTML(value1.toString())).append("',");
								}
								valuesMap.setLength(valuesMap.length() - 1);
								valuesMap.append("}");
								colBuf.append("<div>");
								colBuf.append("<a href=\"javascript:ev_selectone(" + valuesMap.toString()
										+ ")\" class='eventDate'>");
								colBuf.append(list);								
								colBuf.append("</a>");
								colBuf.append("</div>");
							}
								break;
							default:
								break;
							}
						}
						i++;						
					}
				}
				if (colBuf.length() > 0)
					buf.append(colBuf);
			} catch (Exception e) {
				e.printStackTrace();
				buf.append("&nbsp;");
			}
		} else {
			buf.append("&nbsp;");
		}
		return buf.toString();
	}
	

	private Collection<Document> getDatas(View view, ParamsTable params, WebUser user, String applicationid,
			Date stDate, Date endDate) throws Exception {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		DataPackage<Document> datas = viewProcess.getDataPackage(view, params, user, applicationid, stDate, endDate,
				Integer.MAX_VALUE);
		return datas != null ? datas.datas : null;
	}

	/**
	 * 获取视图的列
	 * 
	 * @param viewid
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getColumns(String viewid) throws Exception {
		Map<String, String> map = new LinkedHashMap<String, String>();
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		View view = (View) viewProcess.doView(viewid);
		Collection<Column> collection = view.getColumns();
		map.put("", "{*[please.select]*}");
		for (Iterator<Column> ite = collection.iterator(); ite.hasNext();) {
			Column column = ite.next();
			map.put(column.getName(), column.getName());
		}
		return map;
	}

	/**
	 * 根据日期时间字段标识，查找对应日期时间字段值，以字符串返回
	 * 
	 * @param doc
	 *            数据文档
	 * @param fieldName
	 *            日期时间字段标识
	 * @return 根据日期时间字段标识，查找对应日期时间字段值，以字符串返回
	 * @throws Exception
	 */
	public String getDocDateTimeValueAsString(Document doc, String fieldName) throws Exception {
		String value = "";
		if (fieldName.toUpperCase().trim().startsWith("$")) {
			String propName = fieldName.substring(1);
			if (propName.equalsIgnoreCase("AuditDate")) {
				if (doc.getAuditdate() != null) {
					try {
						value = DateUtil.format(doc.getAuditdate(), "yyyy-MM-dd HH:mm:ss");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (propName.equalsIgnoreCase("LastModified")) {
				if (doc.getLastmodified() != null) {
					try {
						value = DateUtil.format(doc.getLastmodified(), "yyyy-MM-dd HH:mm:ss");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (propName.equalsIgnoreCase("Created")) {
				if (doc.getCreated() != null) {
					try {
						value = DateUtil.format(doc.getCreated(), "yyyy-MM-dd HH:mm:ss");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Item item = doc.findItem(fieldName);
			if (item != null && item.getType().equals(Item.VALUE_TYPE_DATE)) {
				if (item.getDatevalue() != null) {
					value = DateUtil.getDateTimeStr(item.getDatevalue());
				}
			}
		}
		return value;
	}

	public static View get_ViewById(String viewid) throws Exception {
		ViewProcess vp = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		return (View) vp.doView(viewid);
	}

	private static DocumentProcess createDocumentProcess(String applicationid) throws CreateProcessException {
		DocumentProcess process = (DocumentProcess) ProcessFactory.createRuntimeProcess(DocumentProcess.class, applicationid);
		return process;
	}
	
	public IRunTimeProcess<Document> getProcess() throws CreateProcessException {
		return createDocumentProcess(getApplication());
	}

	public String getApplication() {
		if (application != null && application.trim().length() > 0)
			return application;

		return (String) getContext().getSession().get("APPLICATION");
	}

	public static ActionContext getContext() {
		return ActionContext.getContext();
	}

	protected String application;

	public int getDisplayType() {
		return displayType;
	}

	public void setDisplayType(int displayType) {
		this.displayType = displayType;
	}

	/**
	 * 执行脚本
	 * 
	 * @param parameters
	 *            页面参数
	 * @param request
	 *            HTTP请求
	 * @return
	 */
	public String runScript(Map<String, String> parameters, HttpServletRequest request) {
		try {
			FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);

			String viewid = parameters.get("_viewid");
			String formid = parameters.get("formid");
			String fieldid = parameters.get("fieldid");
			
			WebUser user = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);

			Form form = (Form) formProcess.doView(formid);
			if (form == null) {
				return "";
			}

			FormField field = form.findField(fieldid);
			if (field != null && field instanceof ViewDialogField) {
				String script = ((ViewDialogField) field).getOkScript();
				if (!StringUtil.isBlank(script)) {
					script = StringUtil.dencodeHTML(script);
					ParamsTable params = ParamsTable.convertHTTP(request);
					params.putAll(parameters);

					Document searchDocument = getSearchDocument(viewid, params, user);
					IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), form.getApplicationid());
					runner.initBSFManager(searchDocument, params, user, new ArrayList<ValidateMessage>());

					Object result = runner.run(field.getScriptLable("OkScript"), script);
					if (result instanceof org.mozilla.javascript.Undefined) {
						return "";
					}

					return (String) result;
				}
			}
		} catch (Exception e) {
			return e.getMessage();
		}

		return "";
	}

	/**
	 * 获取视图查询文档
	 * 
	 * @param viewid
	 *            视图ID
	 * @param params
	 *            参数
	 * @param user
	 *            当前用户
	 * @return 查询文档
	 * @throws Exception
	 */
	private Document getSearchDocument(String viewid, ParamsTable params, WebUser user) throws Exception {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		View view = (View) viewProcess.doView(viewid);
		if (view != null) {
			Form searchForm = view.getSearchForm();
			if (searchForm != null) {
				return searchForm.createDocument(params, user);
			}
		}

		return new Document();
	}

	public boolean isHiddenColumn(Column column, IRunner runner) {
		try {
			if (column.getHiddenScript() != null && column.getHiddenScript().trim().length() > 0) {
				StringBuffer label = new StringBuffer();
				label.append("View").append(".Activity(").append(column.getId()).append(")." + column.getName())
						.append(".runHiddenScript");

				Object result = runner.run(label.toString(), column.getHiddenScript());// 运行脚本
				if (result != null && result instanceof Boolean) {
					return ((Boolean) result).booleanValue();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * 创建权限字段范围下拉选项
	 * 
	 * @param selectFieldName
	 *            下拉框名称
	 * @param def
	 *            默认值
	 * @return 添加选项的JS脚本
	 */
	public String createAuthFieldOptions(String selectFieldName,String authField, String def) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		if(View.AUTHFIELD_AUTHOR.equals(authField)){
//			map.put(View.AUTHFIELD_SCOPE_ITSELF, "{*[core.dynaform.view.authfield.scope.itself]*}");
			map.put(View.AUTHFIELD_SCOPE_AUTHOR_SUPERIOR, "{*[core.dynaform.view.authfield.scope.superior]*}");
			map.put(View.AUTHFIELD_SCOPE_AUTHOR_LOWER, "{*[core.dynaform.view.authfield.scope.lower]*}");
		}else if(View.AUTHFIELD_AUTHOR_DEFAULT_DEPT.equals(authField)){
			map.put(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_DEFAULT, "{*[core.dynaform.view.authfield.scope.author.dept.default]*}");
			map.put(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_ALL_SUPERIOR, "{*[core.dynaform.view.authfield.scope.author.dept.allsuperior]*}");
			map.put(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_ALL_LOWER, "{*[core.dynaform.view.authfield.scope.author.dept.alllower]*}");
			map.put(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_LINE_SUPERIOR, "{*[core.dynaform.view.authfield.scope.author.dept.linesuperior]*}");
			map.put(View.AUTHFIELD_SCOPE_AUTHOR_DEPT_LINE_LOWER, "{*[core.dynaform.view.authfield.scope.author.dept.linelower]*}");
		}else if(View.AUTHFIELD_AUDITOR.equals(authField)){
			map.put(View.AUTHFIELD_SCOPE_ITSELF, "{*[core.dynaform.view.authfield.scope.itself]*}");
		}

		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}
}
