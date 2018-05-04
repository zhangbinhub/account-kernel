package OLink.bpm.core.report.crossreport.runtime.action;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.report.crossreport.runtime.ejb.RuntimeProcessBean;
import OLink.bpm.core.table.constants.FieldConstant;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcessBean;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataType;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.util.DateUtil;
import OLink.bpm.util.json.JsonUtil;

import com.opensymphony.xwork.ActionContext;

public class RuntimeDetailAction extends BaseAction<Object> {
	@SuppressWarnings("unchecked")
	public RuntimeDetailAction(IDesignTimeProcess<Object> process,
			ValueObject content) throws ClassNotFoundException {
		super(ProcessFactory.createProcess(CrossReportProcess.class), new CrossReportVO());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4970343100187740738L;

	protected final static Logger log = Logger.getLogger(RuntimeDetailAction.class);

	String detailStr = "";

	public String getDetailStr() {
		return detailStr;
	}

	public void setDetailStr(String detailStr) {
		this.detailStr = detailStr;
	}

	private String getRuntimeSql() throws Exception {
		String sql = "";
		String reportId = this.getParams().getParameterAsString("reportId");
		String application = "";
		String domainid = this.getUser().getDomainid();

		CrossReportProcess process = new CrossReportProcessBean();
		CrossReportVO vo = (CrossReportVO) process.doView(reportId);
		//RuntimeProcessBean bean = new RuntimeProcessBean(application);
		ViewProcess viewprocess = (ViewProcess)ProcessFactory.createProcess(ViewProcess.class);
		//View view = (View)viewprocess.doView("11df-00f4-5ac3dd84-a4a1-43b23a39c5d8");
		View view = (View)viewprocess.doView(vo.getView());
		RuntimeHelper helper = new RuntimeHelper();
        String tempsql = view.getEditModeType().getQueryString(this.getParams(), this.getUser(), new Document());
		if (view.getEditMode().equals("01")) {
			sql = helper.getDqlSql(tempsql, application, domainid, "", this.getUser());
		}else
			sql = tempsql;//helper.getParseSql(application, tempsql, this.getUser());
		sql = sql.replace("'$currentUserLowerDeparmentId'", getUser().getLowerDepartmentList());
		return sql;
	}
	
	public String doDetailReport() throws Exception {
		datas = new DataPackage<Object>();
		String tempSql = getRuntimeSql();
		String reportId = this.getParams().getParameterAsString("reportId");
		CrossReportProcess process = new CrossReportProcessBean();
		CrossReportVO vo = (CrossReportVO) process.doView(reportId);
		RuntimeProcessBean bean = new RuntimeProcessBean(application);

		int CurrentPag = this.getParams().getParameterAsInteger("_currpage");
		if(CurrentPag==0){
			CurrentPag = 1;
		}

		// 取得各个字段的类型
		//Statement stat = bean.getRuntimeConn().createStatement();
		//ResultSet rs = stat.executeQuery(tempSql);
		ViewProcess viewprocess = (ViewProcess)ProcessFactory.createProcess(ViewProcess.class);
		View view = (View)viewprocess.doView(vo.getView());
		Map<String, ConsoleMetaData> metadata = getMetaData(view);
		//stat.close();
		//rs.close();

		Collection<Object> paramsCol = JsonUtil.toCollection(vo.getColumns());
		paramsCol.addAll(JsonUtil.toCollection(vo.getRows(), String.class));
		paramsCol.addAll(JsonUtil.toCollection(vo.getFilters(), String.class));
		tempSql = addQueryParams(metadata, paramsCol, tempSql, application,view);

		String countSql = "select count(*) from (" + tempSql + ") a";
		Statement stat3 = bean.getRuntimeConn().createStatement();
		ResultSet rs3 = stat3.executeQuery(countSql);
		int rowcount = 0;
		if (rs3.next()) {
			rowcount = rs3.getInt(1);
		}

		rs3.close();
		stat3.close();

		tempSql = DbTypeUtil.getSQLFunction(application).getLimitString(tempSql, CurrentPag, 30);

		Statement stat2 = bean.getRuntimeConn().createStatement();
		log.info("DetailReportSQL: " + tempSql);
		ResultSet rs2 = stat2.executeQuery(tempSql);

		Collection<String> names = getDetailDisplayCol(vo.getRows(), vo.getColumns(), vo.getFilters());

		StringBuffer tempStr = new StringBuffer();
		tempStr.append(this.getDetailHiddenHtml(getQueryParams(paramsCol)));
		tempStr.append("<table cellpadding=0 cellspacing=0 class='display_view-table2'>");
		tempStr.append("<tr class='dtable-header' style=\"font-weight: bold\">");

		for (Iterator<String> iterator = names.iterator(); iterator.hasNext();) {
			String columnName = iterator.next();
			if (columnName != null) {
				String tempcolumnName = columnName.toUpperCase().startsWith("ITEM_") ? columnName.substring(5) : columnName;
				tempStr.append("<td class='content-detail-title'>" + tempcolumnName);
				tempStr.append("</td>");
			}

		}
		tempStr.append("</tr>");

		while (rs2.next()) {
			String docid = "";
			String formid = "";
			if (metadata.get("ID") != null) {
				docid = rs2.getString("ID");
				try {
					formid = rs2.getString("FORMID");
				} catch (Exception e) {
					log.warn("FORMID not defined");
				}

			}

			tempStr.append("<tr class=\"table-tr\" onmouseover=\"this.className='table-tr-onchange';\" onmouseout=\"this.className='table-tr';\">");
			for (Iterator<String> iterator = names.iterator(); iterator.hasNext();) {
				String name = iterator.next();
				if( metadata.get(name)!=null){
					//name = "ITEM_"+name;
				ConsoleDataType valueType = metadata.get(name).getDataType();
				String value = "";
				for (Iterator<Column> iter = view.getColumns().iterator(); iter.hasNext();) {
					Column column = iter.next();
					if(column.getName().equals(name)){
						if(Column.COLUMN_TYPE_FIELD.equals(column.getType())){
							if (column.getFieldName().startsWith("$")){
								name = column.getFieldName().replace("$", "");
							} else {
								name = "ITEM_"+column.getFieldName();
							}
						} else {
							name = "ITEM_"+name;
						}
						break;
					}
				}
				// 格式化显示值
				if (valueType.getValue() == 1) {
					value = rs2.getInt(name) + "";
				} else if (valueType.getValue() == 2) {
					DecimalFormat df = new DecimalFormat("######0.00");
					value = df.format(rs2.getDouble(name)) + "";
				} else if (valueType.getValue() == 3 || valueType.getValue() == 4) {
					value = rs2.getDate(name) != null ? DateUtil.getDateStr(rs2.getDate(name)) : "";
				} else if (valueType.getValue() == 5) {
					value = rs2.getBoolean(name) + "";
				} else {
					value = rs2.getString(name);
				}

				if (StringUtil.isBlank(docid))
					tempStr.append("<td class='content-detail-title'>" + value + "</td>");
				else
					tempStr.append("<td class='content-detail-title'><a href='#' onclick=\"viewDoc('" + docid + "','" + formid
							+ "')\" >" + value + "</a></td>");
				}else{
					if (StringUtil.isBlank(docid))
						tempStr.append("<td class='content-detail-title'>null</td>");
					else
						tempStr.append("<td class='content-detail-title'><a href='#' onclick=\"viewDoc('" + docid + "','" + formid
								+ "')\" >null</a></td>");
				}
			}
			tempStr.append("</tr>");
		}
		rs2.close();
		stat2.close();
		this.setDetailStr(tempStr.toString());
		datas.linesPerPage = 30;
		datas.pageNo = CurrentPag;
		datas.rowCount = rowcount;

		return SUCCESS;
	}

	public static ActionContext getContext() {
		ActionContext context = ActionContext.getContext();
		return context;
	}
	
	private String addQueryParams(Map<String, ConsoleMetaData> metadata, Collection<Object> rowColName, String sql, String application,View view) throws Exception {
		String queryParams = "select * from (" + sql + ") a where 1=1 ";
		Map<String, String> m = getQueryParams(rowColName);
		for (Iterator<Object> iterator = rowColName.iterator(); iterator.hasNext();) {
			String paramName = (String) iterator.next();
			String strValue = m.get(paramName) == null ? "" : m.get(paramName);
			ConsoleMetaData paraType = metadata.get(paramName);
			if(paraType==null){
				//paramName = "ITEM_"+paramName;
				paraType = metadata.get(paramName);
			}
			for (Iterator<Column> iter = view.getColumns().iterator(); iter.hasNext();) {
				Column column = iter.next();
				if(column.getName().equals(paramName)){
					if(Column.COLUMN_TYPE_FIELD.equals(column.getType())){
						if (column.getFieldName().startsWith("$")){
							paramName = column.getFieldName().replace("$", "");
						} else {
							paramName = "ITEM_"+column.getFieldName();
						}
					} else {
						paramName = "ITEM_"+paramName;
					}
					break;
				}
			}
			if (strValue.equalsIgnoreCase("null")) {
				queryParams += " AND (" + DbTypeUtil.getSQLFunction(application).getWhereClauseNullString(paramName) + " OR "
						+ paramName + "='')";
			} else if (!StringUtil.isBlank(strValue)) {
				if (paraType.getDataType().getValue() == ConsoleDataType.Date.getValue())
					queryParams += " AND " + DbTypeUtil.getSQLFunction(application).toChar(paramName, "yyyy-MM-dd") + "= '"
							+ strValue + "'";
				else if (paraType.getDataType().getValue() == ConsoleDataType.DateTime.getValue())
					queryParams += " AND " + DbTypeUtil.getSQLFunction(application).toChar(paramName, "yyyy-MM-dd") + "= '"
							+ strValue + "'";
				else if (paraType.getDataType().getValue() == ConsoleDataType.Numberic.getValue())
					queryParams += " AND " + paramName + "=" + strValue + "";
				else if (paraType.getDataType().getValue() == ConsoleDataType.String.getValue())
					queryParams += " AND " + paramName + "='" + strValue + "'";
			}
		}
		return queryParams;
	}

	private Map<String, String> getQueryParams(Collection<Object> rowColName) throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		Map<?, ?> m = getContext().getParameters();

		for (Iterator<Object> iterator = rowColName.iterator(); iterator.hasNext();) {
			String paramName = (String) iterator.next();
			String paraValue = "";
			if (m.get(paramName) != null) {
				paraValue = ((String[]) m.get(paramName))[0].replaceAll("&NBSP;", " ");
				params.put(paramName, paraValue);
			} else if (m.get("filter_" + paramName) != null)
				paraValue = ((String[]) m.get("filter_" + paramName))[0].replaceAll("&NBSP;", " ");
			params.put(paramName, paraValue);
		}
		return params;
	}

	
	private Map<String, ConsoleMetaData> getMetaData(View view) throws Exception {
		Map<String, ConsoleMetaData> metadata = new HashMap<String, ConsoleMetaData>();
		for (Iterator<Column> iter = view.getColumns().iterator(); iter.hasNext();) {
			int i =0;
			Column column = iter.next();
			ConsoleDataType dataType = null;
			if(column.getFormField()!=null){
				int typeCode = FieldConstant.getTypeCode(column.getFormField().getFieldtype());
				dataType = ConsoleDataType.toDataType(typeCode);//表单字段模式获取数据类型
			}else{
				dataType = ConsoleDataType.toDataType(2);//脚本模式默认数据类型为数字
			}

			metadata.put(column.getName(), new ConsoleMetaData(dataType, column.getName(), i));
			i++;
		}
		return metadata;
	}


	/**
	 * 隐藏框用于传参数到进入明细页面翻页保存明细信息
	 */
	public String getDetailHiddenHtml(Map<String, String> values) throws Exception {

		Collection<String> hiddenRowSet = values.keySet();
		StringBuffer printStr = new StringBuffer();
		printStr.append("<table cellpadding=0 cellspacing=0 class='display_view-table2'>");
		for (Iterator<String> iterator = hiddenRowSet.iterator(); iterator.hasNext();) {
			String metadata = iterator.next();
			printStr.append("<input type=\"hidden\" name=" + metadata + " id=" + metadata + " value=\"" + values.get(metadata)
					+ "\"/>");

		}
		printStr.append("</table>");
		return printStr.toString();
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

	/**
	 * 
	 * @param row
	 * @param col
	 * @param filter
	 * @return row,column,filter中选中的列.去掉重复列
	 * @throws Exception
	 */
	private Collection<String> getDetailDisplayCol(String row, String col, String filter) throws Exception {
		Map<String, String> tempCols = new HashMap<String, String>();
		Collection<Object> tempCols1 = JsonUtil.toCollection(row, String.class);
		Collection<Object> tempCols2 = JsonUtil.toCollection(col, String.class);
		Collection<Object> tempCols3 = JsonUtil.toCollection(filter, String.class);

		for (Iterator<Object> iterator = tempCols1.iterator(); iterator.hasNext();) {
			String obj1 = (String) iterator.next();
			tempCols.put(obj1, obj1);
		}

		for (Iterator<Object> iterator2 = tempCols2.iterator(); iterator2.hasNext();) {
			String obj2 = (String) iterator2.next();
			if (tempCols.get(obj2) == null)
				tempCols.put(obj2, obj2);
		}

		for (Iterator<Object> iterator3 = tempCols3.iterator(); iterator3.hasNext();) {
			String obj3 = (String) iterator3.next();
			if (tempCols.get(obj3) == null)
				tempCols.put(obj3, obj3);
		}

		return tempCols.keySet();
	}
}
