package OLink.bpm.core.report.crossreport.runtime.action;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseFilter;
import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseTable;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataSet;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;
import OLink.bpm.core.report.crossreport.runtime.ejb.RuntimeProcessBean;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.json.JsonUtil;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataType;
import net.sf.json.JSONObject;

import eWAP.core.Tools;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

public class RuntimeAction extends BaseAction<CrossReportVO> {

	@SuppressWarnings("unchecked")
	public RuntimeAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(CrossReportProcess.class), new CrossReportVO());
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String str = "";
	
	String showSearchForm="";
	
	protected String valuesMap;

	private static final String defaultPath = "C:/DoucemntExportLog/";

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public String doRunReport() {
		try {
			String reportId = this.getParams().getParameterAsString("reportId");
			if(valuesMap !=null && !valuesMap.equals("")){
				JSONObject jsonObject = JSONObject.fromObject(valuesMap);
				for(int i=0;i<jsonObject.names().size();i++){
					String key = jsonObject.names().getString(i);
					if(jsonObject.get(key)!=null && !jsonObject.get(key).equals("") && !jsonObject.get(key).equals("null")){
						params.setParameter(key,jsonObject.get(key));
					}
				}

			}
			CrossReportVO vo = (CrossReportVO) process.doView(reportId);
			ViewProcess viewprocess = (ViewProcess)ProcessFactory.createProcess(ViewProcess.class);
			//View view = (View)viewprocess.doView("11df-00f4-5ac3dd84-a4a1-43b23a39c5d8");
			View view = (View)viewprocess.doView(vo.getView());
			if(view.getSearchForm()!=null){
				showSearchForm = "0";
			}else{
				showSearchForm = "1";
			}
			datas = new ConsoleDataSet(view, getParams(),getUser());
			
			this.getAvailableAnalyseColumnSet();
			this.getAvailableAnalyseRowSet();
			this.getAvailableAnalyseFilter();
			this.getAvailableCalculationFieldSet();

			this.setSelectedAnalyseRowSet(JsonUtil.toCollection(vo.getColumns(), String.class));
			this.setSelectedAnalyseColumnSet(JsonUtil.toCollection(vo.getRows(), String.class));
			Collection<Object> colls = JsonUtil.toCollection(vo.getFilters(), String.class);
			String[] filters = colls.toArray(new String[colls.size()]);
			this.setSelectedAnalyseFilter(filters);
			this.setSelectedAnalyseMethod(vo.getCalculationMethod());
			this.setAvailableCalculationFieldSet(JsonUtil.toCollection(vo.getDatas(), String.class));

			Collection<Object> cols = JsonUtil.toCollection(vo.getDatas(), String.class);
			if (cols != null && cols.size() > 0) {
				Iterator<Object> iterator = cols.iterator();
				if (iterator.hasNext()) {
					this.selectedCalculationField = (String) iterator.next();
				}
			}

			analyseTable = new AnalyseTable(prepareAnalyseColumnSet(), prepareAnalyseRowSet(),
					prepareCalculationFieldSet(), prepareCalculationMethod(), prepareAnalyseFilters(),
					prepareFilterData(), datas);
			analyseTable.setReport(reportId);
			analyseTable.setColCalMethod(vo.getColCalMethod());
			analyseTable.setRowCalMethod(vo.getRowCalMethod());
			analyseTable.setDisplayCol(vo.isDisplayCol());
			analyseTable.setDisplayRow(vo.isDisplayRow());
			if(this.getParams().getParameterAsString("showRowHead")!=null){
				if(this.getParams().getParameterAsString("showRowHead").equals("true")){
					analyseTable.setShowRowHead(true);
				}else if(this.getParams().getParameterAsString("showRowHead").equals("false")){
					analyseTable.setShowRowHead(false);
				}
			}

			AnalyseTableWriter writer = new HtmlAnalyseTableWriter();

			if(this.getParams().getParameterAsString("filter")==null){
				//str = str + this.getAnalyseFilterHtml();
				str = str + this.getHiddenHtml();
			}
			str = str + getTitleHtml(vo.getReportTitle());
			str = str + writer.getTableHtml(null, analyseTable);
			str += getNoteHtml(vo.getNote());
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();
			return INPUT;
		} finally {
		}

	}
	
	
	public String doSearchForm() throws Exception{
		String reportId = this.getParams().getParameterAsString("reportId");
		CrossReportVO vo = (CrossReportVO) process.doView(reportId);
		ViewProcess viewprocess = (ViewProcess)ProcessFactory.createProcess(ViewProcess.class);
		//View view = (View)viewprocess.doView("11df-00f4-5ac3dd84-a4a1-43b23a39c5d8");
		View view = (View)viewprocess.doView(vo.getView());
		if(view.getSearchForm()!=null){
			StringBuffer html = new StringBuffer();
			html.append("<div id=\"searchFormTable\"><table >");
				html.append("<tr>");
				html.append("<td> ");
				html.append(this.toSearchFormHtml(view));
				html.append("</td>");
				if(this.isShowSearchFormButton(view)){
					html.append("<td>");
					html.append("<span class=\"button-cmd\"><a href=\"#\" onclick=\"doQuery()\"><span><img align=\"middle\" border=\"0\" src=\""+this.getParams().getContextPath()+"/resource/imgv2/front/main/query.gif\">{*[Query]*}</span></a></span>");
					html.append("</td><td class='content-detail-title-right' nowrap><span class=\"button-cmd\"><a href=\"#\" onclick=\"ev_resetAll()\"><span><img align=\"middle\"  border=\"0\" src=\""+this.getParams().getContextPath()+"/resource/imgv2/front/main/reset.gif\">{*[Reset]*}</span></a></span></td>");
				}
				html.append("</tr>");
				html.append("</table></div>");
			str = str + html.toString();
			
		}
		return SUCCESS;
	}

	/**
	 * 导出excel报表
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doExport() throws Exception {
		Statement stat = null;
		ResultSet rs = null;

		try {
			//Map<?, ?> m = getContext().getParameters();
			//String sql = getRuntimeSql(m);
			String sql = "";

			//String reportId = ((String[]) m.get("reportId"))[0];
			String application = "";
			//String domainid = (String) getContext().getSession().get("DOMAIN");
			Object app = getContext().getSession().get("APPLICATION");
			application = (String) app;

			//CrossReportProcess process = new CrossReportProcessBean();
			//CrossReportVO vo = (CrossReportVO) process.doView(reportId);
			RuntimeProcessBean bean = new RuntimeProcessBean(application);

			stat = bean.getRuntimeConn().createStatement();
			rs = stat.executeQuery(sql);

			//ConsoleDataSet dataSet = new ConsoleDataSet(rs);

			String fileName = Tools.getSequence() + ".xls";

			String path = DefaultProperty.getProperty("REPORT_PATH");
			if (path == null || path.trim().length() < 1)
				path = defaultPath;

			ServletActionContext.getRequest().setAttribute("filename", fileName);
			//String fileFullName = ServletActionContext.getRequest().getRealPath(path) + "\\" + fileName;// 输出出错信息的路径和文件名
			// ExcelWriter writer = new ExcelWriter();
			// writer.write(fileFullName, dataSet);

		} catch (Exception e) {
			addFieldError("", e.getMessage());
			e.printStackTrace();

			return INPUT;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(stat);
		}

		return SUCCESS;
	}

	/**
	 * The result of program.
	 */
	private ConsoleDataSet datas;

	/**
	 * The available AnalyseRow set.
	 */
	private Collection<String> availableAnalyseRowSet = new ArrayList<String>();

	/**
	 * The selected AnalyseRow set.
	 */
	private Collection<Object> selectedAnalyseRowSet = new ArrayList<Object>();

	/**
	 * The available AnalyseColumn set.
	 */
	private Collection<String> availableAnalyseColumnSet;

	/**
	 * The selected AnalyseColumn set.
	 */
	private Collection<Object> selectedAnalyseColumnSet = new ArrayList<Object>();

	/**
	 * The selected AnalyseMethod
	 */
	private String selectedAnalyseMethod = "1";

	/**
	 * The available available AnalyseMethod set
	 */
	//private Collection<Object> availableAnalyseMethodSet = new ArrayList<Object>();

	/**
	 * The available available AnalyseMethod set
	 */
	private Collection<String> availableAnalyseFilter;

	/**
	 * The selected the analyse filter.
	 */
	private String[] selectedAnalyseFilter;

	/**
	 * The console analyse table
	 */
	private AnalyseTable analyseTable;

	/**
	 * The selected CalculationField.
	 */
	private String selectedCalculationField;

	/**
	 * The available CalculationField set.
	 */
	private Collection<Object> availableCalculationFieldSet;

	/**
	 * @return the datas
	 */
	public ConsoleDataSet getConsoleDataSet() {
		return datas;
	}

	/**
	 * @param datas
	 *            the datas to set
	 */
	public void setDatas(ConsoleDataSet datas) {
		this.datas = datas;
	}

	/**
	 * @return the availableAnalyseRowSet
	 */
	public Collection<String> getAvailableAnalyseRowSet() {
		if (availableAnalyseRowSet == null) {
			if (datas != null) {
				availableAnalyseRowSet = new ArrayList<String>();

				for (Iterator<ConsoleMetaData> iterator = datas.getMetaDataSetIterator(); iterator.hasNext();) {
					ConsoleMetaData metaData = iterator.next();

					availableAnalyseRowSet.add(metaData.getColumnName());
				}
			}
		}

		return availableAnalyseRowSet;
	}

	/**
	 * @param availableAnalyseRowSet
	 *            the availableAnalyseRowSet to set
	 */
	public void setAvailableAnalyseRowSet(Collection<String> availableAnalyseRowSet) {
		this.availableAnalyseRowSet = availableAnalyseRowSet;
	}

	/**
	 * @return the selectedAnalyseRowSet
	 */
	public Collection<Object> getSelectedAnalyseRowSet() {
		return selectedAnalyseRowSet;
	}

	/**
	 * @param selectedAnalyseRowSet
	 *            the selectedAnalyseRowSet to set
	 */
	public void setSelectedAnalyseRowSet(Collection<Object> selectedAnalyseRowSet) {
		this.selectedAnalyseRowSet = selectedAnalyseRowSet;
	}

	/**
	 * @return the availableAnalyseColumnSet
	 */
	public Collection<String> getAvailableAnalyseColumnSet() {
		if (availableAnalyseColumnSet == null) {
			if (datas != null) {
				availableAnalyseColumnSet = new ArrayList<String>();

				for (Iterator<ConsoleMetaData> iterator = datas.getMetaDataSetIterator(); iterator.hasNext();) {
					ConsoleMetaData metaData = iterator.next();
					//if (!selectedAnalyseRowSet.equals(metaData.getColumnName()))
					if (!selectedAnalyseRowSet.contains(metaData.getColumnName()))
						availableAnalyseColumnSet.add(metaData.getColumnName());
				}
			}
		}
		return availableAnalyseColumnSet;
	}

	/**
	 * @param availableAnalyseColumnSet
	 *            the availableAnalyseColumnSet to set
	 */
	public void setAvailableAnalyseColumnSet(Collection<String> availableAnalyseColumnSet) {
		this.availableAnalyseColumnSet = availableAnalyseColumnSet;
	}

	/**
	 * @return the selectedAnalyseColumnSet
	 */
	public Collection<Object> getSelectedAnalyseColumnSet() {
		return selectedAnalyseColumnSet;
	}

	/**
	 * @param selectedAnalyseColumnSet
	 *            the selectedAnalyseColumnSet to set
	 */
	public void setSelectedAnalyseColumnSet(Collection<Object> selectedAnalyseColumnSet) {
		this.selectedAnalyseColumnSet = selectedAnalyseColumnSet;
	}

	/**
	 * @return the selectedAnalyseMethod
	 */
	public String getSelectedAnalyseMethod() {
		return selectedAnalyseMethod;
	}

	/**
	 * @param selectedAnalyseMethod
	 *            the selectedAnalyseMethod to set
	 */
	public void setSelectedAnalyseMethod(String selectedAnalyseMethod) {
		this.selectedAnalyseMethod = selectedAnalyseMethod;
	}

	/**
	 * @return the analyseTable
	 */
	public AnalyseTable getAnalyseTable() {
		return analyseTable;
	}

	/**
	 * @param analyseTable
	 *            the analyseTable to set
	 */
	public void setAnalyseTable(AnalyseTable analyseTable) {
		this.analyseTable = analyseTable;
	}

	/**
	 * @return the availableAnalyseFilter
	 */
	public Collection<String> getAvailableAnalyseFilter() {
		if (availableAnalyseFilter == null) {
			if (datas != null) {
				availableAnalyseFilter = new ArrayList<String>();

				for (Iterator<ConsoleMetaData> iterator = datas.getMetaDataSetIterator(); iterator.hasNext();) {
					ConsoleMetaData metaData = iterator.next();
					availableAnalyseFilter.add(metaData.getColumnName());
				}
			}
		}
		return availableAnalyseFilter;
	}

	/**
	 * @param availableAnalyseFilter
	 *            the availableAnalyseFilter to set
	 */
	public void setAvailableAnalyseFilter(Collection<String> availableAnalyseFilter) {
		this.availableAnalyseFilter = availableAnalyseFilter;
	}

	/**
	 * @return the selectedAnalyseFilter
	 */
	public String[] getSelectedAnalyseFilter() {
		return selectedAnalyseFilter;
	}

	/**
	 * @param alyseFilteselectedAnalyseFilterr
	 *            the selectedAnalyseFilter to set
	 */
	public void setSelectedAnalyseFilter(String[] alyseFilteselectedAnalyseFilterr) {
		this.selectedAnalyseFilter = alyseFilteselectedAnalyseFilterr;
	}

	/**
	 * @return the analyseTableHtml
	 * @throws Expcetion
	 */
	public String getAnalyseTableHtml() throws Exception {

		analyseTable = new AnalyseTable(prepareAnalyseColumnSet(), prepareAnalyseRowSet(), null, null,
				prepareAnalyseFilters(), prepareFilterData(), datas);

		AnalyseTableWriter writer = new HtmlAnalyseTableWriter();

		return writer.getTableHtml(null, analyseTable);
	}
	
	/**
	 * 是否显示查询表单常规按钮(查询、重置)
	 * 
	 * @return 是否显示查询表单
	 */
	public boolean isShowSearchFormButton(View view) {
		try {
			Form searchForm = view.getSearchForm();
			if (searchForm != null && searchForm.getFields().size() > 0) {
				return searchForm.checkDisplayType();
			}
		} catch (Exception e) {
			LOG.warn("isShowSearchFormButton", e);
		}

		return false;
	}

	/**
	 * 输出查询表单HTML
	 * 
	 * @return
	 */
	public String toSearchFormHtml(View view) {
		try {
			Document searchDocument = null;
			if (view.getSearchForm() != null) {
				try {
					searchDocument = view.getSearchForm().createDocument(params,this.getUser());
					String ehtml = view.getSearchForm().toHtml(searchDocument, params, this.getUser(),
							new ArrayList<ValidateMessage>());
					return ehtml;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				return "";
			}
		} catch (Exception e) {
			LOG.warn("toSearchFormHtml", e);
		}

		return "";
	}

	/**
	 * @return the analyseFilterHtml
	 * @throws Expcetion
	 */
	public String getAnalyseFilterHtml() throws Exception {

		Collection<ConsoleMetaData> filters = prepareAnalyseFilters();
		Collection<ConsoleData> filtersData = prepareFilterData();
		Collection<AnalyseFilter> filterSet = new ArrayList<AnalyseFilter>();

		Object[] filterAry = filters.toArray();
		Object[] filterDataAry = filtersData.toArray();

		for (int i = 0; i < filterAry.length; ++i) {
			filterSet.add(new AnalyseFilter((ConsoleMetaData) filterAry[i], (ConsoleData) filterDataAry[i]));
		}
		AnalyseTableWriter writer = new HtmlAnalyseTableWriter();

		return writer.getFilterHtml(null, filterSet, filtersData, datas);
	}

	/**
	 * 行数据列信息作为一个隐藏框用于传参数到进入明细而面
	 */
	public String getHiddenHtml() throws Exception {

		Collection<ConsoleMetaData>  allHiddenCol = this.prepareAnalyseRowSet();
		allHiddenCol.addAll(this.prepareAnalyseColumnSet());
		//Collection hiddenRowSet = this.prepareAnalyseRowSet();
		StringBuffer printStr = new StringBuffer();
		StringBuffer hiddenNames = new StringBuffer();
		printStr.append("<table cellpadding=0 cellspacing=0 class='display_view-table2'>");
		for (Iterator<ConsoleMetaData> iterator = allHiddenCol.iterator(); iterator.hasNext();) {
			ConsoleMetaData metadata = iterator.next();
			if(metadata != null){
				printStr.append("<input type=\"hidden\" name=" + metadata.getColumnName() + " id="
						+ metadata.getColumnName() + " value=\"\"/>");
				hiddenNames.append(metadata.getColumnName() + ";");
			}

		}
		printStr.append("<input type=\"hidden\" name=allcolumn id=allcolumn value=\"" + hiddenNames.toString() + "\"/>");
		printStr.append("</table>");
		return printStr.toString();
	}

	/**
	 * 隐藏框用于传参数到进入明细页面翻页保存明细信息
	 */
	public String getDetailHiddenHtml(Map<?, ?> values) throws Exception {

		Collection<?> hiddenRowSet = values.keySet();
		StringBuffer printStr = new StringBuffer();
		printStr.append("<table cellpadding=0 cellspacing=0 class='display_view-table2'>");
		for (Iterator<?> iterator = hiddenRowSet.iterator(); iterator.hasNext();) {
			String metadata = (String) iterator.next();
			printStr.append("<input type=\"hidden\" name=" + metadata + " id=" + metadata + " value="
					+ values.get(metadata) + "/>");

		}
		printStr.append("</table>");
		return printStr.toString();
	}

	/**
	 * Prepare the analyse column set.
	 * 
	 * @return The current analyse column set.
	 */
	private Collection<ConsoleMetaData> prepareAnalyseColumnSet() {
		Map<String, ConsoleMetaData>  metaDateSet = datas.getMetaDataSet();

		Collection<ConsoleMetaData> analyseColumnSet = new ArrayList<ConsoleMetaData>();
		if (selectedAnalyseColumnSet.iterator().hasNext()) {
			for (Iterator<Object> iterator = selectedAnalyseColumnSet.iterator(); iterator.hasNext();) {
				String colName = (String) iterator.next();
				ConsoleMetaData metaData = metaDateSet.get(colName);
				analyseColumnSet.add(metaData);
			}
		} else {
			ConsoleDataType dataType = ConsoleDataType.toDataType(-1);
			ConsoleMetaData metaData = new ConsoleMetaData(dataType, "小计", 0);
			analyseColumnSet.add(metaData);
		}
		return analyseColumnSet;
	}

	/**
	 * Prepare the analyse row set.
	 * 
	 * @return The current analyse row set.
	 */
	private Collection<ConsoleMetaData> prepareAnalyseRowSet() {
		Map<String, ConsoleMetaData> metaDateSet = datas.getMetaDataSet();

		Collection<ConsoleMetaData> analyseRowSet = new ArrayList<ConsoleMetaData>();

		for (Iterator<Object> iterator = selectedAnalyseRowSet.iterator(); iterator.hasNext();) {
			String colName = (String) iterator.next();
			ConsoleMetaData metaData = metaDateSet.get(colName);
			analyseRowSet.add(metaData);
		}

		return analyseRowSet;
	}

	/**
	 * Prepare the analyse filters.
	 * 
	 * @return The analyse filters.
	 */
	private Collection<ConsoleMetaData> prepareAnalyseFilters() {
		Map<String , ConsoleMetaData> metaDateSet = datas.getMetaDataSet();

		Collection<ConsoleMetaData> filterSet = new ArrayList<ConsoleMetaData>();

		for (int i = 0; i < selectedAnalyseFilter.length; i++) {
			ConsoleMetaData metaData = metaDateSet.get(selectedAnalyseFilter[i]);
			filterSet.add(metaData);
		}

		return filterSet;
	}

	/**
	 * Prepare the analyse filters.
	 * 
	 * @return The analyse filters.
	 */
	private Collection<ConsoleData> prepareFilterData() throws Exception {
		Collection<ConsoleMetaData> filter = prepareAnalyseFilters();
		Collection<ConsoleData> filterData = new ArrayList<ConsoleData>();

		for (Iterator<ConsoleMetaData> iterator = filter.iterator(); iterator.hasNext();) {
			ConsoleMetaData metaData = iterator.next();
			if (metaData != null){
				Object object = null;
				if(RuntimeAction.getContext()!=null && RuntimeAction.getContext().getParameters()!=null){
					object = RuntimeAction.getContext().getParameters().get("filter_" + metaData.getColumnName());
				}
				String stringValue = "";
	
				if (object != null)
					stringValue = ((String[]) object)[0];
	
				if (stringValue != null && stringValue.length() > 0)
					filterData.add(new ConsoleData(metaData, stringValue));
				else
					filterData.add(null);
			}
		}

		return filterData;
	}

	/**
	 * Clean all the data.
	 */
	@SuppressWarnings("all")
	private void cleanData() {
		datas = null;

		availableAnalyseRowSet = null;
		selectedAnalyseRowSet.clear();

		availableAnalyseColumnSet = null;
		selectedAnalyseColumnSet.clear();

		selectedAnalyseMethod = "";

		availableAnalyseFilter = null;
		selectedAnalyseFilter = null;
	}

	public static ActionContext getContext() {
		ActionContext context = ActionContext.getContext();
		return context;
	}

	public String getSelectedCalculationField() {
		return selectedCalculationField;
	}

	public void setSelectedCalculationField(String selectedCalculationField) {
		this.selectedCalculationField = selectedCalculationField;
	}

	/**
	 * @return the availableCalculationFieldSet
	 */
	public Collection<Object> getAvailableCalculationFieldSet() {

		if (availableCalculationFieldSet == null) {
			if (datas != null) {
				availableCalculationFieldSet = new ArrayList<Object>();

				for (Iterator<ConsoleMetaData> iterator = datas.getMetaDataSetIterator(); iterator.hasNext();) {
					ConsoleMetaData metaData = iterator.next();

					if (!selectedAnalyseRowSet.contains(metaData.getColumnName())
							&& !selectedAnalyseColumnSet.contains(metaData.getColumnName()))
						availableCalculationFieldSet.add(metaData.getColumnName());

				}
			}
		}

		return availableCalculationFieldSet;
	}

	/**
	 * @param availableCalculationFieldSet
	 *            the availableCalculationFieldSet to set
	 */
	public void setAvailableCalculationFieldSet(Collection<Object> availableCalculationFieldSet) {
		this.availableCalculationFieldSet = availableCalculationFieldSet;
	}

	/**
	 * Prepare the analyse row set.
	 * 
	 * @return The current analyse row set.
	 */
	private ConsoleMetaData prepareCalculationFieldSet() {
		Map<String, ConsoleMetaData>  metaDateSet = datas.getMetaDataSet();
		return metaDateSet.get(selectedCalculationField);
	}

	/**
	 * Prepare the calculation method
	 * 
	 * @return The calculation method
	 */
	private CalculationMethod prepareCalculationMethod() {
		return CalculationMethod.valueOf(selectedAnalyseMethod);
	}

	public WebUser getUser() throws Exception {
		Map<?, ?> session = getContext().getSession();

		WebUser user = null;

		if (session == null || session.get(Web.SESSION_ATTRIBUTE_USER) == null)
			user = getAnonymousUser();
		else
			user = (WebUser) session.get(Web.SESSION_ATTRIBUTE_USER);

		return user;
	}

	protected WebUser getAnonymousUser() throws Exception {
		UserVO vo = new UserVO();

		vo.getId();
		vo.setName("GUEST");
		vo.setLoginno("guest");
		vo.setLoginpwd("");
		vo.setRoles(null);
		vo.setEmail("");

		return new WebUser(vo);
	}
/*
	private String getRuntimeSql(Map<?, ?> m) throws Exception {
		String sql = "";
		String reportId = ((String[]) m.get("reportId"))[0];
		String application = "";
		String domainid = (String) getContext().getSession().get("DOMAIN");
		Object app = getContext().getSession().get("APPLICATION");
		if (app == null) {
			application = ((String[]) m.get("application"))[0];
		} else {
			application = (String) app;
		}

		CrossReportProcess process = new CrossReportProcessBean();
		CrossReportVO vo = (CrossReportVO) process.doView(reportId);
		//RuntimeProcessBean bean = new RuntimeProcessBean(application);
		RuntimeHelper helper = new RuntimeHelper();

		if (vo.getQtype().equals("01")) {

			sql = helper.getDqlSql(vo.getDql(), application, domainid, "", this.getUser());
		} else if (vo.getQtype().equals("02")) {
			sql = helper.getFormDesignSql(vo.getFormCondition(), vo.getForm(), domainid, application, this.getUser());
		} else
			sql = helper.getParseSql(application, vo.getSql(), this.getUser());
		sql = sql.replace("'$currentUserLowerDeparmentId'", getUser().getLowerDepartmentList());
		return sql;
	}
*/
	private String getTitleHtml(String title) throws Exception {
		String html = "<div id=\"activityTable\"><table width=\"100%\" class=\"title1\" >";
		html += "<tr>";
		html += "<td align=\"center\">";
		html += "{*[Report]*}:" + (title == null ? "" : title);
		html += "</td>";
		html += "</tr>";
		html += "</table></div>";
		return html;
	}

	private String getNoteHtml(String note) throws Exception {
		String html = "<div id=\"pageTable\"><table class=\"note\" width=\"100%\">";
		html += "<tr>";
		html += "<td>";
		html += "{*[备注]*}:" + (note == null ? "" : note);
		html += "</td>";
		html += "</tr>";
		html += "</table></div>";

		return html;
	}

	public String getValuesMap() {
		return valuesMap;
	}

	public void setValuesMap(String valuesMap) {
		this.valuesMap = valuesMap;
	}

	public String getShowSearchForm() {
		return showSearchForm;
	}

	public void setShowSearchForm(String showSearchForm) {
		this.showSearchForm = showSearchForm;
	}

	
	
}
