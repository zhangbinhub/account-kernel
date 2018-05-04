package OLink.bpm.core.report.crossreport.runtime.action;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateSQLUtils;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.dql.DQLASTUtil;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.form.ejb.mapping.TableMapping;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseDimension;
import OLink.bpm.core.report.crossreport.runtime.ejb.RuntimeProcessBean;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.web.DWRHtmlUtils;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.dynaform.view.ejb.ViewProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcessBean;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import eWAP.core.Tools;

public class RuntimeHelper {
	private final static Logger LOG = Logger.getLogger(RuntimeHelper.class);

	public Map<String, String> getMetaData(String sql, String formid, String type, String application, String domainid) throws Exception {
		Statement stat = null;
		ResultSet rs = null;

		try {
			RuntimeProcessBean bean = new RuntimeProcessBean(application);
			stat = bean.getRuntimeConn().createStatement();
			String tempSql = sql;
			Map<String, String> metas = new HashMap<String, String>();
			if (type.equals("01"))
				tempSql = getDqlSql(sql, application, domainid, "", null);
			else if (type.equals("02"))
				tempSql = getFormDesignSql(sql, formid, domainid, application, null);
			else if(type.equals("00"))
				tempSql = getParseSql(application, sql, null);
			rs = stat.executeQuery(tempSql);

			if (rs != null) {
				ResultSetMetaData resultSetMetaData = rs.getMetaData();

				if (resultSetMetaData != null) {
					for (int i = 1; i <= resultSetMetaData.getColumnCount(); ++i){
						//if (type.equals("00")) {
						//	metas.put(resultSetMetaData.getColumnName(i), resultSetMetaData.getColumnName(i));
						//} else {
							String colum = resultSetMetaData.getColumnName(i).toUpperCase();
							if (colum.indexOf("ITEM_") >= 0)
								metas.put(colum, colum.substring(colum.indexOf("ITEM_") + 5));
							else
								metas.put(colum, colum);
					    //}
					}

				}
			}

			return metas;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(stat);
		}
	}
	
	public String getParseSql(String applicationid,String sql, WebUser user) throws Exception{
		Document currdoc = new Document();
		ParamsTable params = new ParamsTable();
		WebUser webuser = user == null ? getAnonymousUser() : user;
		StringBuffer label = new StringBuffer();
		label.append("VIEW(").append("report).report.FilterScript(" + sql + ")");
		
		IRunner runner = JavaScriptFactory.getInstance(String.valueOf(Tools.getSequence()), applicationid);
		runner.initBSFManager(currdoc, params, webuser, new ArrayList<ValidateMessage>());
		Object result = runner.run(label.toString(), sql);
		if (result != null && result instanceof String) {
			return (String) result;
		}
		return sql;
	}
	
	
	
	//获得视图
	public String creatColumnOptions(String viewid,String domainid, String application, String[] selectFieldNames,
			String def) throws Exception  {
		ViewProcess viewProcess = (ViewProcess) ProcessFactory.createProcess(ViewProcess.class);
		View view = (View)viewProcess.doView(viewid);
		String sql = view.getEditModeType().getQueryString(new ParamsTable(),getAnonymousUser(), new Document());
		if(view.getEditMode().equals("01")){
			sql = this.getDqlSql(sql, application, domainid, null, getAnonymousUser());
		}
		Map<String, String> map = null;
		StringBuffer options = new StringBuffer();
		try {
			map = new LinkedHashMap<String, String>();
			if(view.getColumns()!=null && view.getColumns().size()>0){
				for (Iterator<Column> iter = view.getColumns().iterator(); iter.hasNext();) {
					Column column = iter.next();
					/*
					String fieldName = column.getFieldName();
					if(fieldName.equals("$Id")){
						map.put("ID", "{*[Form]*}ID"); // 表单ID
					}else if(fieldName.equals("$StateLabel")){
						map.put("STATELABEL", "{*[Form]*}{*[StateLabel]*}"); // 状态标签�
					}else if(fieldName.equals("$Created")){
						map.put("CREATED", "{*[Form]*}{*[Created]*}"); // 创建日期
					}else if(fieldName.equals("$AuditDate")){
						map.put("AUDITDATE", "{*[Form]*}{*[AuditDate]*}"); // 审批日期
					}else if(fieldName.equals("$LastModified")){
						map.put("LASTMODIFIER", "{*[Form]*}{*[LastModified]*}"); // 最后修改日期�
					}else if(fieldName.equals("$Author")){
						map.put("AUTHOR", "{*[Form]*}{*[Author]*}"); // 文档作者
					}else if(fieldName.equals("$AuditorNames")){
						map.put("AUDITORNAMES", "{*[Form]*}{*[AuditorNames]*}"); // 审批名称�
					}else if(fieldName.equals("$LastFlowOperation")){
						map.put("LASTFLOWOPERATION", "{*[Form]*}{*[LastFlowOperation]*}"); // 最后操作�
					}else if(fieldName.equals("$FormName")){
						map.put("FORMNAME", "{*[Form]*}{*[Name]*}"); // 表单名称�
					}else{
						map.put(column.getFieldName(), column.getName());
					}*/
					map.put(column.getName(), column.getName());
				}
			}
			//map = getViewMetaData(sql,application, domainid);
			//if(map==null){
			//	map = new HashMap<String, String>();
			//}
			options.append("new Function(\"");
			for (int i = 0; i < selectFieldNames.length; i++) {
				options.append(DWRHtmlUtils.createOptionScript(map, selectFieldNames[i], def));
			}
			options.append("\")");
		} catch (Exception e) {
			LOG.warn("Could not get table column metadata");
			throw e;
		}

		return options.toString();
	}
	//视图字段
	public Map<String, String> getViewMetaData(String sql,String application, String domainid) throws Exception {
		Statement stat = null;
		ResultSet rs = null;

		try {
			RuntimeProcessBean bean = new RuntimeProcessBean(application);
			stat = bean.getRuntimeConn().createStatement();
			Map<String, String> metas = new HashMap<String, String>();
			rs = stat.executeQuery(sql);

			if (rs != null) {
				ResultSetMetaData resultSetMetaData = rs.getMetaData();

				if (resultSetMetaData != null) {
					for (int i = 1; i <= resultSetMetaData.getColumnCount(); ++i){
						//if (type.equals("00")) {
						//	metas.put(resultSetMetaData.getColumnName(i), resultSetMetaData.getColumnName(i));
						//} else {
							String colum = resultSetMetaData.getColumnName(i).toUpperCase();
							if (colum.indexOf("ITEM_") >= 0)
								metas.put(colum, colum.substring(colum.indexOf("ITEM_") + 5));
							else
								metas.put(colum, colum);
					    //}
					}

				}
			}

			return metas;
		} catch (Exception e) {
			throw e;
		} finally {
			PersistenceUtils.closeResultSet(rs);
			PersistenceUtils.closeStatement(stat);
		}
	}
	

	public String creatFieldOptions(String formid, String sql, String type, String domainid, String application, String[] selectFieldNames,
			String def) throws Exception {
		Map<String, String> map = null;
		StringBuffer options = new StringBuffer();
		try {
			map = getMetaData(sql, formid, type, application, domainid);
			options.append("new Function(\"");
			for (int i = 0; i < selectFieldNames.length; i++) {
				options.append(DWRHtmlUtils.createOptionScript(map, selectFieldNames[i], def));
			}
			options.append("\")");
		} catch (Exception e) {
			LOG.warn("Could not get table column metadata");
			throw e;
		}

		return options.toString();
	}

	public String creatReport(String selectFieldName, String application, String moduleid, String def) throws Exception {
		HashMap<String, String> map = new LinkedHashMap<String, String>();
		ParamsTable param = new ParamsTable();
		if(selectFieldName.equals("actionReport")){
			CrossReportProcess process = new CrossReportProcessBean();
			param.setParameter("s_module", moduleid);
			param.setParameter("t_type", "CrossReport");
			Collection<CrossReportVO> cols = process.doQuery(param).datas;
			map.put("$/portal/share/report/oReport/oReport.jsp", "图形{*[Report]*}工具");
			map.put("$/portal/share/report/standardreport/query.jsp", "时效{*[Report]*}");
			map.put("$/portal/share/report/wfdashboard/sumframe.jsp", "{*[Flow]*}仪表盘");
			map.put("", "---------------{*[CrossReport]*}---------------------");
			if (cols != null) {
				for (Iterator<CrossReportVO> iter = cols.iterator(); iter.hasNext();) {
					CrossReportVO vo = iter.next();
					if(vo.getJson()==null || vo.getJson().equals("")){
						map.put(vo.getId(), vo.getName());
					}
				}
			}
		}else{
			param.setParameter("t_userid", "null");
			param.setParameter("t_applicationid",application);
			param.setParameter("t_type", "CustomizeReport");
			CrossReportProcess vp = (CrossReportProcess)ProcessFactory.createProcess(CrossReportProcess.class);
			Collection<CrossReportVO> viewvo = vp.doQuery(param).datas;
			map.put("","{*[Select]*}");
			if (viewvo != null) {
				for (Iterator<CrossReportVO> iter = viewvo.iterator(); iter.hasNext();) {
					CrossReportVO vo = iter.next();
					map.put("portal/share/report/oReport/oReport.jsp?id="+vo.getId(), vo.getName());
				}
			}
			selectFieldName = "actionReport";
		}

		return DWRHtmlUtils.createOptions(map, selectFieldName, def);
	}

	public String parseDQLFormName(String dql) {

		int pos = dql.toUpperCase().indexOf("$FORMNAME");
		dql = dql.substring(pos + 9);

		int pos2 = dql.toUpperCase().indexOf(" AND");
		String formName = "";
		if (pos2 > 0) {
			formName = dql.substring(1, pos2).trim();
		} else {
			formName = dql.substring(1);
		}

		formName = formName.replaceAll("\\(", "");
		formName = formName.replaceAll("\\)", "");
		formName = formName.replaceAll("\\'", "");

		return formName;
	}

	public String parseDQLWhere(String dql) {
		int pos = dql.toUpperCase().indexOf("$FORMNAME");
		dql = dql.substring(pos + 9);

		int pos2 = dql.toUpperCase().indexOf(" AND");
		if (pos2 > 0) {
			return dql.substring(pos2 + 4).trim();
		} else {
			return "";
		}
	}

	public String getDqlSql(String dql, String application, String domainid, String sessionid, WebUser user) throws Exception {
		//StringBuffer label = new StringBuffer();
		//label.append("VIEW(").append("report).report.FilterScript(" + dql + ")");

		//IRunner runner = JavaScriptFactory.getInstance(String.valueOf(Tools.getSequence()), application);
		//Document currdoc = new Document();
		//ParamsTable params = new ParamsTable();
		//ArrayList<ValidateMessage> errors = new ArrayList<ValidateMessage>();
		//WebUser webuser = user == null ? getAnonymousUser() : user;
		//runner.initBSFManager(currdoc, params, webuser, errors);
		//Object result = runner.run(label.toString(), dql);
		String formName = parseDQLFormName(dql);

		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Form form = formProcess.doViewByFormName(getFormShortName(formName), application);
		if (form == null) {
			throw new Exception("Form: " + formName + " does not exist");
		}
		TableMapping mapping = form.getTableMapping();

		String dqlWhere = parseDQLWhere(dql);
		String where = "";
		if (dqlWhere != null && dqlWhere.trim().length() > 0) {
			where = DQLASTUtil.parseToHQL(dqlWhere, mapping, DbTypeUtil.getSQLFunction(application));
		}

		String sql = "SELECT doc.* FROM " + mapping.getTableName() + " doc WHERE ISTMP=0 ";
		if (where != null && where.trim().length() > 0) {
			sql += " AND " + where;
		}

		HibernateSQLUtils sqlUtil = new HibernateSQLUtils();
		sql = sqlUtil.appendCondition(sql, "DOMAINID ='" + domainid + "'");

		return sql;
	}

	private String getFormShortName(String formName) {
		String rtn = formName;
		if (formName.indexOf("=") != -1) {
			rtn = rtn.substring(formName.indexOf("=") + 1).trim();
		}
		return rtn.substring((rtn.lastIndexOf("/") + 1), rtn.length());
	}

	public String getFormDesignSql(String formCondition, String formid, String domainid, String application, WebUser user) throws Exception {
		//WebUser webuser = user == null ? getAnonymousUser() : user;
		FormProcess formProcess = (FormProcess) ProcessFactory.createProcess(FormProcess.class);
		Form relatedForm = (Form) formProcess.doView(formid);
		ParamsTable param = new ParamsTable();
		param.setParameter("application", application);
		String rtn = "";

		StringBuffer sqlTmp = new StringBuffer();
		sqlTmp.append("SELECT AUTH_DOC.* FROM ").append(DQLASTUtil.TBL_PREFIX).append(relatedForm.getName());
		sqlTmp.append(" AUTH_DOC WHERE AUTH_DOC.ISTMP = 0 AND DOMAINID='" + domainid + "'");
		rtn = sqlTmp.toString();

		if (formCondition != null && !formCondition.equals(""))
			rtn = sqlTmp + ReportConditionParser.parseToSQL(formCondition, param, user);

		return rtn;
	}

	private WebUser getAnonymousUser() throws Exception {
		UserVO vo = new UserVO();

		vo.getId();
		vo.setName("GUEST");
		vo.setLoginno("guest");
		vo.setLoginpwd("");
		vo.setRoles(null);
		vo.setEmail("");

		return new WebUser(vo);
	}

	public static String getArrayStr(Object[] keys) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < keys.length; i++) {
			AnalyseDimension key = (AnalyseDimension) keys[i];
			ConsoleMetaData metaData = key.getMetaData();
			if(metaData != null)
				str.append(metaData.getColumnName() + ";");
		}
		return str.toString();
	}

	public static String reOrgLastRowKey(String keys) {
		String[] array = keys.split("!!");
		StringBuffer str = new StringBuffer();
		for (int i = 0; i <= array.length - 1; i++) {

			str.append((StringUtil.isBlank(array[i]) ? "null" : array[i]) + "!!");
		}
		return str.toString();
	}

	public static String[] getKeyArray(Object[] keys) {
		String[] array = new String[keys.length];
		for (int i = 0; i < keys.length; i++) {
			AnalyseDimension key = (AnalyseDimension) keys[i];
			ConsoleMetaData metaData = key.getMetaData();
			if (metaData != null)
				array[i] = metaData.getColumnName();
		}
		return array;
	}

	public static String getArrayStrFromTo(int end, String[] keys) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i <= end; i++) {
			str.append(keys[i] + ";");
		}
		return str.toString();
	}

}
