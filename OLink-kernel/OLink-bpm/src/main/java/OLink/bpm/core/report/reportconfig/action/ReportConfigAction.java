package OLink.bpm.core.report.reportconfig.action;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.exp.Export;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.dynaform.form.ejb.FormProcess;
import OLink.bpm.core.report.dataprepare.ExecuteDataPrepare;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepareProcess;
import OLink.bpm.core.report.query.ejb.Parameter;
import OLink.bpm.core.report.query.ejb.QueryProcess;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfigProcess;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumnProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.file.FileOperate;
import OLink.bpm.util.property.DefaultProperty;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfigProcess;
import OLink.bpm.core.report.query.action.QueryHelper;
import OLink.bpm.core.report.query.ejb.Query;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;
import OLink.bpm.core.user.action.WebUser;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import eWAP.core.Tools;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

public class ReportConfigAction extends BaseAction<ReportConfig> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _query;

	private String _moduleid;

	private String _reportType; // 标记是ListTable,还是 CrossTable

	public static final String DATA_TYPE_STRING = "java.lang.String";

	public static final String DATA_TYPE_DOUBLE = "java.lang.Double";

	public static final String DATA_TYPE_DATE = "java.util.Date";

	private String _module; // 标记searchForm所属模块

	private String _searchForm;

	private String _path;

	private String _dts;

	private String _creatReportType; // 标记是上传jrxml文件,或CustomMode

	private String _dataPrepare;

	public String get_dataPrepare() {
		ReportConfig vo = (ReportConfig) this.getContent();
		return vo != null && vo.getDataPrepare() != null ? vo.getDataPrepare().getId() : null;
	}

	public void set_dataPrepare(String prepare) {
		_dataPrepare = prepare;
	}

	public String get_moduleid() {
		ReportConfig rc = (ReportConfig) this.getContent();
		if (rc.getModule() != null) {
			return rc.getModule().getId();
		} else {
			return this._moduleid;
		}

	}

	public void set_moduleid(String _moduleid) {
		this._moduleid = _moduleid;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ReportConfigAction() throws Exception {
		super(ProcessFactory.createProcess(ReportConfigProcess.class), new ReportConfig());
	}

	public String doNew(){

		if (_creatReportType.equals(ReportConfig.Creat_Report_Type_CustomMade)) {
			if (_reportType.equals(ReportConfig.Report_Type_ListTable))
				return "ListTable";
			else
				return "CrossTable";
		} else
			return "UploadJrxml";
	}

	public String doEdit() {
		try {
			Map<?, ?> params = getContext().getParameters();
			Object obj = params.get("id");
			String id = ((String[]) obj)[0];
			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
			if (_creatReportType.equals(ReportConfig.Creat_Report_Type_CustomMade)) {
				if (_reportType.equals(ReportConfig.Report_Type_ListTable))
					return "ListTable";
				else
					return "CrossTable";
			} else
				return "UploadJrxml";
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String doView() {
		try {
			Map<?, ?> params = getContext().getParameters();
			String[] ids = (String[]) (params.get("id"));
			String id = null;
			if (ids != null && ids.length > 0) {
				id = ids[0];
			}
			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
			if (_creatReportType.equals(ReportConfig.Creat_Report_Type_CustomMade)) {
				if (_reportType.equals(ReportConfig.Report_Type_ListTable))
					return "ListTable";
				else
					return "CrossTable";
			} else
				return "UploadJrxml";
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String doSave() {
		try {
			ModuleProcess mp = (ModuleProcess) ProcessFactory.createProcess(ModuleProcess.class);
			ModuleVO mv = (ModuleVO) mp.doView(_moduleid);
			ReportConfig vo = (ReportConfig) getContent();
			vo.setReportType(_reportType);

			DataPrepareProcess dpr = (DataPrepareProcess) (ProcessFactory.createProcess(DataPrepareProcess.class));
			DataPrepare dataprepare = (DataPrepare) dpr.doView(_dataPrepare);
			vo.setDataPrepare(dataprepare);

			vo.setApplicationid(getApplication());

			if (this._creatReportType != null
					&& this._creatReportType.equals(ReportConfig.Creat_Report_Type_CustomMade)) {
				QueryProcess rp = (QueryProcess) (ProcessFactory.createProcess(QueryProcess.class));
				Query query = (Query) rp.doView(_query);
				vo.setCreatReportType(ReportConfig.Creat_Report_Type_CustomMade);
				vo.setQuery(query);
				vo.setModule(mv);
				vo.setReportType(get_reportType());
				super.doSave();
				if (_reportType.equals(ReportConfig.Report_Type_ListTable))
					return "ListTable";
				else
					return "CrossTable";
			} else if (this._creatReportType != null
					&& this._creatReportType.equals(ReportConfig.Creat_Report_Type_UploadJrxml)) {

				vo.setCreatReportType(ReportConfig.Creat_Report_Type_UploadJrxml);

				FormProcess fp = (FormProcess) (ProcessFactory.createProcess(FormProcess.class));
				Form form = (Form) fp.doView(_searchForm);

				DataSourceProcess dp = (DataSourceProcess) (ProcessFactory.createProcess(DataSourceProcess.class));
				DataSource dts = (DataSource) dp.doView(_dts);

				ModuleVO from_mv = (ModuleVO) mp.doView(_module);

				vo.setForm_module(from_mv);
				vo.setSearchForm(form);
				vo.setModule(mv);
				vo.setDataSource(dts);

				String fullName = null;
				if (this._path != null && _path.length() > 0) {
					fullName = ServletActionContext.getServletContext().getRealPath(DefaultProperty.getProperty("JRXML_PATH")) + "\\"
							+ _path.substring(_path.lastIndexOf("/") + 1, _path.length());
					String jrxml = FileOperate.getFileContentAsStringUTF(fullName);
					vo.setJrxml(jrxml);
				}
				super.doSave();
				return "UploadJrxml";
			}

			return "";
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}

	}

	public String refData() throws Exception { // 刷新和该报表关联的数据
		String id = getParams().getParameterAsString("id");
		ReportConfig vo = (ReportConfig) this.process.doView(id);
		Collection<MappingConfig> coll = vo.getMappingconfigs();
		for (Iterator<MappingConfig> iter = coll.iterator(); iter.hasNext();) {
			MappingConfig em = iter.next();
			Export.exprotDocument(em, true, new WebUser(new UserVO()), getApplication());
		}
		setContent(vo);
		reportCondition();
		return vo.getSearchForm() != null ? SUCCESS : INPUT;
	}

	public String reportCondition() throws Exception { // 判断是否有SearchForm

		String id = getContent().getId();
		if (id == null || id.trim().length() < 1)
			id = getParams().getParameterAsString("id");
		ReportConfig vo = (ReportConfig) this.process.doView(id);

		if (vo != null) {
			ActionContext ctx = ActionContext.getContext();
			HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
			if (vo.getCreatReportType().equals(ReportConfig.Creat_Report_Type_CustomMade) && vo.getQuery() != null
					&& vo.getQuery().getSearchForm() != null) {

				request.setAttribute("searchForm", vo.getQuery().getSearchForm());
				return SUCCESS;
			} else if (vo.getCreatReportType().equals(ReportConfig.Creat_Report_Type_UploadJrxml)
					&& vo.getSearchForm() != null) {
				request.setAttribute("searchForm", vo.getSearchForm());
				return SUCCESS;
			}

		}
		return INPUT;

	}

	/**
	 * @SuppressWarnings webwork不支持泛型
	 * @return
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public String exportReport() throws SQLException {
		Connection conn = null;
		try {
			ReportConfigHepler helper = new ReportConfigHepler();
			ActionContext ctx = ActionContext.getContext();
			HttpServletRequest request = (HttpServletRequest) ctx.get(ServletActionContext.HTTP_REQUEST);
			String path = ServletActionContext.getServletContext().getRealPath("");
			String reportpath = path.replaceAll("\\\\", "/") + DefaultProperty.getProperty("REPORT_PATH");
			String jrxmlFile = reportpath + Tools.getSequence() + "report.jrxml";
			String jasperFile = reportpath + Tools.getSequence() + "report.jasper";

			// 存放路径
			if (!(new File(reportpath).isDirectory())) {
				File f = new File(reportpath);
				if(!f.exists()){
					if(!f.mkdirs())
						throw new IOException("create directory '" + reportpath + "' failed!");
				}
			}
			String fileExcelPath = reportpath + Tools.getSequence() + "reportExcel.xls";
			String filePdflPath = reportpath + Tools.getSequence() + "reportPdf.pdf";

			Map<String, Object> parameters = null;
			String id = getContent().getId();
			if (id == null || id.trim().length() < 1)
				id = getParams().getParameterAsString("id");
			ReportConfig vo = (ReportConfig) this.process.doView(id);

			if (vo.getCreatReportType() != null
					&& vo.getCreatReportType().equals(ReportConfig.Creat_Report_Type_CustomMade)) { // 用户自定义报表

				conn = helper.getConnection(vo.getQuery().getDataSource());
				parameters = getCreatReportParams(getParams(), vo);
				creatJasperTemplate(jrxmlFile, vo, parameters, application);
				JasperCompileManager.compileReportToFile(jrxmlFile, jasperFile);
			} else if (vo.getCreatReportType() != null
					&& vo.getCreatReportType().equals(ReportConfig.Creat_Report_Type_UploadJrxml)) {// 用户上传Jrxml产生报表
				conn = helper.getConnection(vo.getDataSource());

				FileOperate.writeFileUTF(jrxmlFile, vo.getJrxml(), true);
				JasperCompileManager.compileReportToFile(jrxmlFile, jasperFile);

				JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(jasperFile));
				parameters = getCreatReportParams(ctx.getParameters(), getParams(), jasperReport.getParameters());
			}
			if (conn != null) {

				DataPrepare dataprepare = vo.getDataPrepare();
				if (dataprepare != null) {
					ExecuteDataPrepare.clearTempData(dataprepare); // 先删除临时表数据
					ExecuteDataPrepare.execute(dataprepare); // 产生临时表数据
				}

				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperFile, parameters, conn);
				
				Map session = ctx.getSession();
				session.put("jasperPrint", jasperPrint);

				exportReportToExcel(jasperPrint, fileExcelPath);
				exportReportToPdf(jasperPrint, filePdflPath);
				request.setAttribute("excelPath", DefaultProperty.getProperty("REPORT_PATH")
						+ fileExcelPath.substring(reportpath.length(), fileExcelPath.length()));
				request.setAttribute("pdfPath", DefaultProperty.getProperty("REPORT_PATH")
						+ filePdflPath.substring(reportpath.length(), filePdflPath.length()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			if (conn != null)
				conn.close();
		}

		return SUCCESS;
	}

	// 生成Excel报表文件
	public String exportReportToExcel(JasperPrint jasperPrint, String filePath) throws Exception {
		JRXlsExporter exporter = new JRXlsExporter();
		File destFile = new File(filePath);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFile.toString());
		exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);

		exporter.exportReport();
		return SUCCESS;
	}

	// 生成Pdf报表文件
	public String exportReportToPdf(JasperPrint jasperPrint, String filePath) throws Exception {
		JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);
		return SUCCESS;
	}

	public Map<String, Object> getCreatReportParams(ParamsTable params, ReportConfig vo) throws Exception { // 根据queryString
		// 取得参数列表,并从params中取得参数对应该的值
		Map<String, Object> map = new HashMap<String, Object>();
		if (params == null || vo == null || vo.getQuery() == null)
			return map;
		String queryString = vo.getQuery().getQueryString();
		Collection<String> list = getParametersBySQL(queryString); // //
		for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
			String paramName = iter.next();
			String value = params.getParameterAsString(paramName);
			if (value != null && value.length() > 0)
				map.put(paramName, value);
		}
		return map;
	}

	public String creatJasperTemplate(String path, ReportConfig rc, Map<String, Object> params, String application) throws Exception { // 根据配置生成Jrxml
		TableColumnProcess tp = (TableColumnProcess) (ProcessFactory.createProcess(TableColumnProcess.class));
		DataPackage<TableColumn> data_group1 = tp.getFieldsByReportConfigAndType(rc.getId(), ReportConfig.Column_Type_Group1,
				application);
		DataPackage<TableColumn> data_group2 = tp.getFieldsByReportConfigAndType(rc.getId(), ReportConfig.Column_Type_Group2,
				application);
		DataPackage<TableColumn> data_group3 = tp.getFieldsByReportConfigAndType(rc.getId(), ReportConfig.Column_Type_Group3,
				application);
		DataPackage<TableColumn> data_group4 = tp.getFieldsByReportConfigAndType(rc.getId(), ReportConfig.Column_Type_Group4,
				application);
		DataPackage<TableColumn> data_detail = tp.getFieldsByReportConfigAndType(rc.getId(), ReportConfig.Column_Type_Detail,
				application);
		DataPackage<TableColumn> data_summary = tp.getFieldsByReportConfigAndType(rc.getId(), ReportConfig.Column_Type_summary,
				application);

		DataPackage<TableColumn> data_rowGroup = tp.getFieldsByReportConfigAndType(rc.getId(),
				ReportConfig.CrossTable_Type_RowGroup, application);
		DataPackage<TableColumn> data_columnGroup = tp.getFieldsByReportConfigAndType(rc.getId(),
				ReportConfig.CrossTable_Type_ColumnGroup, application);

		int pageWidth = getPageWidth(data_detail);
		if (pageWidth == 0)
			pageWidth = 800;
		StringBuffer sb = new StringBuffer();
		sb
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?> <!DOCTYPE jasperReport PUBLIC \"-//JasperReports//DTD Report Design//EN\" \"http://jasperreports.sourceforge.net/dtds/jasperreport.dtd\">");
		sb
				.append("<jasperReport name=\"ShapesReport\" pageWidth=\"")
				.append(pageWidth)
				.append(
						"\"  whenNoDataType=\"AllSectionsNoDetail\" columnWidth=\"50\" leftMargin=\"20\" rightMargin=\"20\" topMargin=\"30\" bottomMargin=\"30\" isIgnorePagination=\"true\" >");
		sb
				.append("<style name=\"Arial_Bold\" isDefault=\"false\" fontName=\"Arial\" fontSize=\"12\" isBold=\"true\" isItalic=\"false\" isUnderline=\"false\" isStrikeThrough=\"false\" pdfFontName=\"STSong-Light\" pdfEncoding=\"UniGB-UCS2-H\" isPdfEmbedded=\"true\"/>");
		sb
				.append("<style name=\"Arial_Normal\" isDefault=\"true\" fontName=\"Arial\" fontSize=\"12\" isBold=\"false\" isItalic=\"false\" isUnderline=\"false\" isStrikeThrough=\"false\" pdfFontName=\"STSong-Light\" pdfEncoding=\"UniGB-UCS2-H\" isPdfEmbedded=\"true\"/>");
		sb.append("	<style name=\"CellStyle\" isDefault=\"false\" mode=\"Opaque\" />");

		String jasperSQL = rc.getQuery().getQueryString();
		DataSource dts = rc.getQuery().getDataSource();
		sb.append(creatParamters(rc));

		jasperSQL += queryStringAppendOrderBy(data_group1, data_group2, data_group3, data_group4); //
		sb.append(creatQueryString(jasperSQL));

		String sql = parseJaperSQL(params, rc.getQuery());
		sb.append(ceartField(get_tableColumnFromDateBase(sql, dts)));

		sb.append(creatVariableByGroup(data_group1));
		sb.append(creatVariableByGroup(data_group2));
		sb.append(creatVariableByGroup(data_group3));
		sb.append(creatVariableByGroup(data_group4));
		sb.append(creatVariableBySummary(data_summary));

		sb.append(creatGroup(data_group1, pageWidth));
		sb.append(creatGroup(data_group2, pageWidth));
		sb.append(creatGroup(data_group3, pageWidth));
		sb.append(creatGroup(data_group4, pageWidth));

		sb.append(creatTitel(rc.getName()));
		sb.append(creatPageHeader());

		Map<TableColumn, String> detail = getColumnAndDataType(data_detail, get_tableColumnFromDateBase(sql, dts));

		sb.append(creatColumnHeader(detail));
		sb.append(creatDetial(detail));
		sb.append(creatColumnFooter());

		sb.append(creatPageFooter());

		sb.append(creatSummary(data_summary, pageWidth));
		sb.append(creatCrossTable(data_rowGroup, data_columnGroup, rc));
		sb.append("</jasperReport>");
		FileOperate.writeFileUTF(path, sb.toString(), true);
		return null;
	}

	private String parseJaperSQL(Map<String, Object> params, Query query) {
		String jasperSql = query.getQueryString();
		if (jasperSql == null || jasperSql.length() == 0)
			return null;
		while (jasperSql.indexOf("$P") >= 0) {
			int i = jasperSql.indexOf("$P");
			int j = jasperSql.indexOf("{", i);
			int k = jasperSql.indexOf("}", j + 1);
			String paramName = jasperSql.substring(j + 1, k);
			String tmpStr = jasperSql.substring(0, i);
			String tmpStr1 = jasperSql.substring(k + 1, jasperSql.length());
			String value = (String) params.get(paramName);
			if (value == null || value.trim().length() == 0) {
				Collection<Parameter> coll = query.getParamters();
				for (Iterator<Parameter> iter = coll.iterator(); iter.hasNext();) {
					Parameter em = iter.next();
					if (em.getName().equals(paramName))
						value = em.getDefaultValue();
				}
			}
			jasperSql = tmpStr + "'" + value + "'" + tmpStr1;
		}
		return jasperSql;
	}

	private Collection<String> getParametersBySQL(String sql) throws Exception {

		QueryHelper helper = new QueryHelper();
		Collection<String> params = helper.getParametersBySQL(sql);
		return params;
	}

	private int getPageWidth(DataPackage<TableColumn> detail) {
		int width = 0;
		if (detail == null || detail.datas == null || detail.datas.size() == 0)
			return width;
		for (Iterator<TableColumn> iter = detail.datas.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (em.getWidth() != null && em.getWidth().trim().length() > 0)
				width += Integer.valueOf(em.getWidth()).intValue();
			else
				width += 50;
		}

		return width;
	}

	public String creatParamters(ReportConfig vo) {
		StringBuffer sb = new StringBuffer();
		if (vo != null && vo.getQuery() != null) {
			Query query = vo.getQuery();
			Collection<Parameter> params = query.getParamters();
			for (Iterator<Parameter> iter = params.iterator(); iter.hasNext();) {
				Parameter em = iter.next();
				sb.append("<parameter name=\"").append(em.getName()).append("\"").append(
						" isForPrompting=\"false\" class=\"java.lang.String\">");
				sb.append("\n");
				sb.append("<defaultValueExpression ><![CDATA[\"");
				if (em.getDefaultValue() != null && em.getDefaultValue().trim().length() > 0)
					sb.append(em.getDefaultValue());
				else
					sb.append("%");
				sb.append("\"]]></defaultValueExpression>");
				sb.append("</parameter>");
			}
		}
		return sb.toString();
	}

	public String ceartField(Map<String, String> map) throws Exception {
		StringBuffer sb = new StringBuffer();
		for (Iterator<Entry<String, String>> iter = map.entrySet().iterator();iter != null && iter.hasNext();) {
			Entry<String, String> entry = iter.next();
			//String name = (String) iter.next();
			String name = entry.getKey();
			String type = entry.getValue();
			sb.append("<field name=\"").append(name).append("\" class=\"").append(type).append("\"/>");
			sb.append("\n");
		}
		return sb.toString();
	}

	public String creatVariableByGroup(DataPackage<TableColumn> data) {

		if (data == null || data.datas == null || data.datas.size() == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		Collection<TableColumn> coll = data.datas;
		for (Iterator<TableColumn> iter = coll.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (!em.isSort()) {
				sb.append("<variable name=\"").append(em.getType() + "_" + em.getName()).append(
						"\" class=\"java.lang.Double\" resetType=\"Group\" resetGroup=\"");
				sb.append(em.getType()).append("\" calculation=\"");
				if (em.getCalculateMode() == null || em.getCalculateMode().length() == 0)
					sb.append("Nothing");
				sb.append(em.getCalculateMode());
				sb.append("\">");
				sb.append("<variableExpression><![CDATA[$F{").append(em.getName()).append("}]]></variableExpression>");
				sb.append("</variable>");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public String creatVariableBySummary(DataPackage<TableColumn> data) {

		if (data == null || data.datas == null || data.datas.size() == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		Collection<TableColumn> coll = data.datas;
		for (Iterator<TableColumn> iter = coll.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (!em.isSort()) {
				sb.append("<variable name=\"").append(em.getType() + "_" + em.getName()).append(
						"\" class=\"java.lang.Double\" resetType=\"Report\" ").append(" calculation=\"");
				if (em.getCalculateMode() == null || em.getCalculateMode().length() == 0)
					sb.append("Nothing");
				sb.append(em.getCalculateMode());
				sb.append("\">");
				sb.append("<variableExpression><![CDATA[$F{").append(em.getName()).append("}]]></variableExpression>");
				sb.append("</variable>");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public String creatGroup(DataPackage<TableColumn> data, int pageWidth) {

		if (data == null || data.datas == null || data.datas.size() == 0)
			return "";
		Collection<TableColumn> coll = data.datas;
		String sortField = "";
		String groupName = "";
		for (Iterator<TableColumn> iter = coll.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (em.isSort()) {
				sortField = em.getName();
				groupName = em.getType();
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<group name=\"").append(groupName).append("\">");
		sb.append("<groupExpression>").append("$F{").append(sortField).append("}").append("</groupExpression>");
		sb.append("<groupHeader>");
		sb.append("<band height=\"25\"  isSplitAllowed=\"true\" >");
		sb.append("<rectangle radius=\"0\" ><reportElement x=\"0\" y=\"0\" width=\"").append(pageWidth).append(
				"\" height=\"25\" forecolor=\"#FFFFFF\" backcolor=\"#BFC9DC\"");
		sb.append(" key=\"rectangle\"/>");
		sb.append("<graphicElement stretchType=\"NoStretch\"/>");
		sb.append("</rectangle>");

		sb.append("<staticText>");
		sb
				.append("<reportElement x=\"3\" y=\"2\" width=\"96\" height=\"23\" forecolor=\"#ff0000\" key=\"staticText\"/>");
		sb
				.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>");
		sb.append("<textElement textAlignment=\"Left\">");
		sb.append("<font size=\"12\" />");
		sb.append("</textElement>");
		sb.append("<text><![CDATA[").append(sortField).append("]]></text>");
		sb.append("</staticText>");

		sb
				.append("<textField isStretchWithOverflow=\"false\" pattern=\"\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
		sb
				.append("<reportElement x=\"102\" y=\"2\"  width=\"106\" height=\"23\"  forecolor=\"#ff0000\" key=\"textField\"/> ");
		sb
				.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>");
		sb.append("<textElement textAlignment=\"Left\">");
		sb.append("<font size=\"12\"/>");
		sb.append("</textElement>");
		sb.append("<textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{").append(sortField).append(
				"}]]></textFieldExpression>");
		sb.append("</textField>");
		sb.append("</band>");

		sb.append("</groupHeader>");
		sb.append("<groupFooter>");
		sb.append("<band height=\"30\">");
		sb.append("<line>");
		sb.append("<reportElement x=\"0\" y=\"0\" width=\"").append(pageWidth).append(
				"\" height=\"1\" forecolor=\"#ff0000\"/>");
		sb.append("<graphicElement/>");
		sb.append("</line>");

		for (Iterator<TableColumn> iter = coll.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (!em.isSort()) {
				sb.append("<staticText>");
				sb.append("<reportElement x=\"").append(pageWidth - 200).append(
						"\" y=\"4\" width=\"150\" height=\"15\"/>");
				sb.append("<textElement textAlignment=\"Right\"/>");
				sb.append("<text>");
				sb.append("<![CDATA[").append(em.getName() + "--" + em.getCalculateMode() + ":").append("]]>");
				sb.append("</text>");
				sb.append("</staticText>");
				sb.append("<textField pattern=\"###0.00\">");
				sb.append("<reportElement x=\"").append(pageWidth - 50).append(
						"\" y=\"4\" width=\"50\" height=\"15\"/>");
				sb.append("<textElement textAlignment=\"Left\"/>");
				sb.append("<textFieldExpression class=\"java.lang.Double\">");
				sb.append("$V{").append(em.getType() + "_" + em.getName()).append("}"); // ****
				sb.append("</textFieldExpression>");
				sb.append("</textField>");
				pageWidth = pageWidth - 200;
			}
		}
		sb.append("</band>");
		sb.append("</groupFooter>");
		sb.append("</group>");
		sb.append("\n");
		return sb.toString();
	}

	public String creatColumnHeader(Map<TableColumn, String> detial) {
		if (detial == null || detial.size() == 0)
			return "";
		int totalWidth = 0;

		for (Iterator<TableColumn> iter = detial.keySet().iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (em.getWidth() != null && em.getWidth().length() > 0)
				totalWidth += Integer.parseInt(em.getWidth());
			else
				totalWidth += 50;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("<columnHeader>");
		sb.append("<band height=\"20\">");
		sb.append("<rectangle>");
		sb.append("<reportElement x=\"0\" y=\"0\" width=\"").append(totalWidth).append(
				"\" height=\"10\" backcolor=\"#333111\"/>");
		sb.append("<graphicElement/>");
		sb.append("</rectangle>");

		int excursion = 0;
		for (Iterator<TableColumn> iter = detial.keySet().iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			int width = 50;
			if (em.getWidth() != null && em.getWidth().length() > 0)
				width = Integer.parseInt(em.getWidth());
			sb.append("<staticText>");
			sb.append("<reportElement mode=\"Opaque\"  x=\"").append(excursion).append("\" y=\"0\" width=\"").append(
					width).append("\" height=\"15\"  backcolor=\"lightGray\" style=\"Arial_Bold\"/>");
			// sb.append("<textElement
			// textAlignment=\"Center\"><font/></textElement>");
			sb.append("<text><![CDATA[").append(em.getName()).append("]]></text>");
			sb.append("</staticText>");
			sb.append("\n");
			excursion += width;
		}
		sb.append("</band>");
		sb.append("</columnHeader>");
		return sb.toString();
	}

	public String creatColumnFooter() {
		StringBuffer sb = new StringBuffer();
		sb.append("<columnFooter>");
		sb.append("<band height=\"11\">");
		sb.append("</band>");
		sb.append("</columnFooter>");
		return sb.toString();
	}

	public String creatDetial(Map<TableColumn, String> detial) {

		if (detial == null || detial.size() == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		sb.append("<detail>");
		sb.append("<band height=\"30\">");
		int i = 0;
		int excursion = 0;
		for (Iterator<Entry<TableColumn, String>> iter = detial.entrySet().iterator();iter != null && iter.hasNext();) {
			Entry<TableColumn, String> entry = iter.next();
			//TableColumn em = (TableColumn) iter.next();
			TableColumn em = entry.getKey();
			String dataType = entry.getValue();
			int width = 50; // 默认宽度大小
			if (em.getWidth() != null && em.getWidth().length() > 0)
				width = Integer.parseInt(em.getWidth());

			sb.append("<textField isBlankWhenNull=\"true\">");
			sb.append("<reportElement x=\"").append(excursion).append("\" y=\"4\" width=\"").append(width).append(
					"\" height=\"15\" style=\"Arial_Normal\"/>");
			// sb.append("<textElement textAlignment=\"Right\"/>");
			sb.append("<textFieldExpression class=\"").append(dataType).append("\">");
			sb.append("$F{").append(em.getName()).append("}");// ///////
			sb.append("</textFieldExpression>");
			sb.append("</textField>");
			sb.append("\n");
			excursion += width;
			i++;
		}
		sb.append("</band>");
		sb.append("</detail>");
		sb.append("\n");
		return sb.toString();
	}

	public String creatSummary(DataPackage<TableColumn> data, int pageWidth) {

		if (data == null || data.datas == null || data.datas.size() == 0)
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append("<summary>");
		sb.append("<band height=\"60\">");

		sb.append("<line>");
		sb.append("<reportElement x=\"0\" y=\"0\" width=\"").append(pageWidth).append(
				"\" height=\"1\" forecolor=\"#faacf\"/>");
		sb.append("<graphicElement/>");
		sb.append("</line>");

		sb.append("<staticText>");
		sb.append("<reportElement x=\"").append(0).append("\" y=\"4\" width=\"150\" height=\"15\"/>");
		sb.append("<textElement textAlignment=\"Right\"/>");
		sb.append("<text>");
		sb.append("<![CDATA[Summary:]]>");
		sb.append("</text>");
		sb.append("</staticText>");

		for (Iterator<TableColumn> iter = data.datas.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if (!em.isSort()) {
				sb.append("<staticText>");
				sb.append("<reportElement x=\"").append(pageWidth - 200).append(
						"\" y=\"4\" width=\"150\" height=\"15\"/>");
				sb.append("<textElement textAlignment=\"Right\"/>");
				sb.append("<text>");
				sb.append("<![CDATA[").append(em.getName() + "--" + em.getCalculateMode() + ":").append("]]>");
				sb.append("</text>");
				sb.append("</staticText>");
				sb.append("<textField pattern=\"###0.00\">");
				sb.append("<reportElement x=\"").append(pageWidth - 50).append(
						"\" y=\"4\" width=\"50\" height=\"15\"/>");
				sb.append("<textElement textAlignment=\"Left\"/>");
				sb.append("<textFieldExpression class=\"java.lang.Double\">");
				sb.append("$V{").append(em.getType() + "_" + em.getName()).append("}"); // ****
				sb.append("</textFieldExpression>");
				sb.append("</textField>");
				pageWidth = pageWidth - 200;
			}
		}
		sb.append("</band>");
		sb.append("</summary>");
		sb.append("\n");
		return sb.toString();
	}

	public String creatPageHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append("<pageHeader>");
		sb.append("<band height=\"20\">");
		sb.append("</band>");
		sb.append("</pageHeader>");
		sb.append("\n");
		return sb.toString();
	}

	public String creatPageFooter() {
		StringBuffer sb = new StringBuffer();
		sb.append("<pageFooter>");
		sb.append("<band height=\"20\">");
		sb.append("</band>");
		sb.append("</pageFooter>");
		sb.append("\n");
		return sb.toString();
	}

	public String creatTitel(String title) {
		StringBuffer sb = new StringBuffer();
		sb.append("<title>");
		sb.append("<band height=\"30\">");
		sb.append("<staticText>");
		sb.append("<reportElement x=\"0\" y=\"0\" width=\"700\" height=\"30\"/>");
		// sb.append("<textElement textAlignment=\"Center\"/>");
		// sb.append("<text>");
		sb.append("<textElement textAlignment=\"Center\">");
		sb.append("<font size=\"18\"/>");
		sb.append("</textElement>");
		sb.append("<text>");
		sb.append("<![CDATA[").append(title).append("]]>");
		sb.append("</text>");
		sb.append("</staticText>");
		sb.append("</band>");
		sb.append("</title>");
		sb.append("\n");
		return sb.toString();
	}

	public String queryStringAppendOrderBy(DataPackage<TableColumn> group1, DataPackage<TableColumn> group2, DataPackage<TableColumn> group3,
			DataPackage<TableColumn> group4) {
		StringBuffer orderby = new StringBuffer();
		if (group1 != null && group1.datas != null && group1.datas.size() > 0) {
			for (Iterator<TableColumn> iter = group1.datas.iterator(); iter.hasNext();) {
				TableColumn em = iter.next();
				if (em.isSort())
					orderby.append(em.getName() + ",");
			}
		}
		if (group2 != null && group2.datas != null && group2.datas.size() > 0) {
			for (Iterator<TableColumn> iter = group1.datas.iterator(); iter.hasNext();) {
				TableColumn em = iter.next();
				if (em.isSort())
					orderby.append(em.getName() + ",");
			}
		}
		if (group3 != null && group3.datas != null && group3.datas.size() > 0) {
			for (Iterator<TableColumn> iter = group1.datas.iterator(); iter.hasNext();) {
				TableColumn em = iter.next();
				if (em.isSort())
					orderby.append(em.getName() + ",");
			}
		}
		if (group4 != null && group4.datas != null && group4.datas.size() > 0) {
			for (Iterator<TableColumn> iter = group1.datas.iterator(); iter.hasNext();) {
				TableColumn em = iter.next();
				if (em.isSort())
					orderby.append(em.getName());
			}
		}
		if (orderby.toString().trim().length() > 0 && orderby.substring(orderby.length() - 1, orderby.length()).endsWith(","))
			//orderby = orderby.substring(0, orderby.length() - 1);
			orderby = orderby.deleteCharAt(orderby.length() - 1);
		return orderby.toString().trim().length() > 0 ? " order by " + orderby : "";
	}

	public String creatQueryString(String queryString) {
		StringBuffer sb = new StringBuffer();
		sb.append("<queryString><![CDATA[").append(queryString).append("]]></queryString>");
		sb.append("\n");
		return sb.toString();
	}

	public Map<String, String> get_tableColumnFromDateBase(String sql, DataSource dts) throws Exception { // 根椐sql取得结果集中各项数据的类型
		if (sql == null || sql.length() == 0)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		ResultSet tmprs = null;
		try {
			DriverManager.registerDriver((Driver) Class.forName(dts.getDriverClass()).newInstance());
			conn = DriverManager.getConnection(dts.getUrl(), dts.getUsername(), dts.getPassword());
			String tmpsql = "select * from (" + sql + ") where 1<>1";
			ps = conn.prepareStatement(tmpsql);
			tmprs = ps.executeQuery();

			ResultSetMetaData dma = tmprs.getMetaData();
			for (int i = 1; i <= dma.getColumnCount(); i++) {
				String columnName = dma.getColumnName(i);
				String dataType = dma.getColumnTypeName(i);
				if (dataType.equals("NUMBER"))
					map.put(columnName, DATA_TYPE_DOUBLE);
				//else if (dataType.equals("VARCHAR2") || dataType.equals("VARCHAR"))
				//	map.put(columnName, DATA_TYPE_STRING);
				else
					map.put(columnName, DATA_TYPE_STRING); // *********
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			PersistenceUtils.closeResultSet(tmprs);
			PersistenceUtils.closeStatement(stmt);
			PersistenceUtils.closeStatement(ps);
			
			PersistenceUtils.closeConnection(conn);
		}

		return map;
	}

	public Map<TableColumn, String> getColumnAndDataType(DataPackage<TableColumn> pack, Map<String, String> columnInfo) { // 取得Column的数据类型且排序(用于用户自定义的报表)
		if (pack == null || pack.datas == null || pack.datas.size() == 0 || columnInfo == null)
			return null;

		Collection<TableColumn> coll = pack.datas;
		Map<TableColumn, String> map = new LinkedHashMap<TableColumn, String>();

		Object[] columns = coll.toArray();
		Object temp = null;
		int k = 0;
		for (int i = 0; i < columns.length; i++) {
			k = i;
			TableColumn em = (TableColumn) columns[i];
			for (int j = i + 1; j < columns.length; j++) {
				TableColumn cm = (TableColumn) columns[j];
				if (em.getOrderno() < cm.getOrderno()) {
					em = cm;
					k = j;
				}
			}
			if (k != i) {
				temp = columns[i];
				columns[i] = columns[k];
				columns[k] = temp;
			}
		}
		for (int i = 0; i < columns.length; i++) {
			TableColumn em = (TableColumn) columns[i];
			if (columnInfo.containsKey(em.getName()) && !map.containsKey(em)) {
				map.put(em, columnInfo.get(em.getName()));
			}
		}

		return map;
	}

	public String creatCrossTable(DataPackage<TableColumn> rows, DataPackage<TableColumn> columns, ReportConfig vo) { // 创建交叉报表

		if (rows == null || rows.datas == null || rows.datas.size() == 0 || columns == null || columns.datas == null
				|| columns.datas.size() == 0)
			return "";

		StringBuffer sb = new StringBuffer();
		sb.append("<summary>");

		sb.append("<band height=\"60\"  isSplitAllowed=\"true\" >");
		sb.append("<crosstab>");
		sb.append("<reportElement x=\"0\" y=\"0\" width=\"750\" height=\"60\" key=\"crosstab\"/>");
		sb.append("<crosstabDataset><dataset ></dataset></crosstabDataset>");
		sb.append("<crosstabHeaderCell>");
		sb.append("<cellContents mode=\"Transparent\">");
		sb
				.append("<box topBorder=\"Thin\" topBorderColor=\"#000000\" leftBorder=\"Thin\" leftBorderColor=\"#000000\" rightBorder=\"Thin\" rightBorderColor=\"#000000\" bottomBorder=\"Thin\" bottomBorderColor=\"#000000\"/>");
		sb.append("</cellContents>");
		sb.append("</crosstabHeaderCell>");

		for (Iterator<TableColumn> iter = rows.datas.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			sb.append("<rowGroup name=\"").append(em.getName()).append("\" width=\"76\" totalPosition=\"End\">");// //
			sb.append("<bucket>");
			sb.append("<bucketExpression class=\"java.lang.String\"><![CDATA[$F{").append(em.getName()).append(
					"}]]></bucketExpression>"); // //
			sb.append("</bucket>");
			sb.append("<crosstabRowHeader>");
			sb.append("<cellContents mode=\"Opaque\" style=\"CellStyle\">");
			sb
					.append("<box topBorder=\"1Point\" topBorderColor=\"#000000\" leftBorder=\"1Point\" leftBorderColor=\"#000000\" rightBorder=\"1Point\" rightBorderColor=\"#000000\" bottomBorder=\"1Point\" bottomBorderColor=\"#000000\"/>");
			sb
					.append("<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
			sb
					.append("<reportElement style=\"Arial_Bold\" x=\"7\" y=\"3\" width=\"67\" height=\"15\" key=\"textField\"/>");
			sb
					.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>");
			sb.append("<textElement verticalAlignment=\"Middle\">");
			sb.append("<font size=\"12\"/>");
			sb.append("</textElement>");
			sb.append("<textFieldExpression   class=\"java.lang.String\"><![CDATA[$V{").append(em.getName()).append(
					"}]]></textFieldExpression>"); // //
			sb.append("</textField>");
			sb.append("</cellContents>");
			sb.append("</crosstabRowHeader>");
			sb.append("<crosstabTotalRowHeader>");
			sb.append("<cellContents mode=\"Transparent\">");
			sb
					.append("<box topBorder=\"1Point\" topBorderColor=\"#000000\" leftBorder=\"1Point\" leftBorderColor=\"#000000\" rightBorder=\"1Point\" rightBorderColor=\"#000000\" bottomBorder=\"1Point\" bottomBorderColor=\"#000000\"/>");
			sb.append("<staticText>");
			sb
					.append("<reportElement style=\"Arial_Bold\" x=\"6\" y=\"3\" width=\"64\" height=\"15\" key=\"staticText\"/>");
			sb
					.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>");
			sb.append("<textElement verticalAlignment=\"Middle\">");
			sb.append("<font size=\"12\"/>");
			sb.append("</textElement>");
			sb.append("<text><![CDATA[TOTAL]]></text>"); // /
			sb.append("</staticText>");
			sb.append("</cellContents>");
			sb.append("</crosstabTotalRowHeader>");
			sb.append("</rowGroup>");
		}

		// //////////////////////////////////////////////////////////////////////////

		for (Iterator<TableColumn> iter = columns.datas.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();

			sb.append("<columnGroup name=\"").append(em.getName()).append("\" height=\"30\" totalPosition=\"End\">"); // ///
			sb.append("<bucket order=\"Descending\">");
			sb.append("<bucketExpression class=\"java.lang.String\"><![CDATA[$F{").append(em.getName()).append(
					"}]]></bucketExpression>"); //
			sb.append("</bucket>");
			sb.append("<crosstabColumnHeader>");
			sb.append("<cellContents mode=\"Transparent\">");
			sb
					.append("<box topBorder=\"Thin\" topBorderColor=\"#000000\" leftBorder=\"Thin\" leftBorderColor=\"#000000\" rightBorder=\"Thin\" rightBorderColor=\"#000000\" bottomBorder=\"Thin\" bottomBorderColor=\"#000000\"/>");
			sb
					.append("<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
			sb
					.append("<reportElement style=\"Arial_Normal\" x=\"2\" y=\"8\" width=\"60\" height=\"15\" key=\"textField-1\"/>");
			sb
					.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>");
			sb.append("<textElement textAlignment=\"Center\"><font size=\"12\"/></textElement>");
			sb.append("<textFieldExpression   class=\"java.lang.String\"><![CDATA[$V{").append(em.getName()).append(
					"}]]></textFieldExpression>");
			sb.append("</textField>");
			sb.append("</cellContents>");
			sb.append("</crosstabColumnHeader>");
			sb.append("<crosstabTotalColumnHeader>");
			sb.append("<cellContents mode=\"Transparent\">");
			sb
					.append("<box topBorder=\"Thin\" topBorderColor=\"#000000\" leftBorder=\"Thin\" leftBorderColor=\"#000000\" rightBorder=\"Thin\" rightBorderColor=\"#000000\" bottomBorder=\"Thin\" bottomBorderColor=\"#000000\"/>");
			sb.append("<staticText>");
			sb
					.append("<reportElement style=\"Arial_Normal\" x=\"4\" y=\"8\" width=\"60\" height=\"15\" key=\"staticText-1\"/>");
			sb
					.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>");
			sb.append("<textElement textAlignment=\"Center\"><font size=\"12\"/></textElement>");
			sb.append("<text><![CDATA[TOTAL]]></text>");
			sb.append("</staticText>");
			sb.append("</cellContents>");
			sb.append("</crosstabTotalColumnHeader>");
			sb.append("</columnGroup>");
		}

		sb.append("<measure name=\"amount\" class=\"java.lang.Double\" calculation=\"").append(
				vo.getCrossTable_CalculateType()).append("\">"); // /
		sb.append("<measureExpression><![CDATA[$F{").append(vo.getCrossTable_CalculateField()).append("}!=null?$F{")
				.append(vo.getCrossTable_CalculateField()).append("}:new Double(0.0)]]></measureExpression>"); // /
		sb.append("</measure>");
		sb.append("<crosstabCell width=\"78\" height=\"21\">");
		sb.append("<cellContents mode=\"Opaque\" style=\"CellStyle\">");
		sb
				.append("<box topBorder=\"Thin\" topBorderColor=\"#000000\" leftBorder=\"Thin\" leftBorderColor=\"#000000\" rightBorder=\"Thin\" rightBorderColor=\"#000000\" bottomBorder=\"Thin\" bottomBorderColor=\"#000000\"/>");
		sb
				.append("<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >");
		sb
				.append("<reportElement style=\"Arial_Normal\" x=\"3\" y=\"3\" width=\"60\" height=\"15\" key=\"textField-1\"/>");
		sb
				.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"None\" rightBorderColor=\"#000000\" bottomBorder=\"None\" bottomBorderColor=\"#000000\"/>");
		sb.append("<textElement><font size=\"12\"/></textElement>");

		sb.append("<textFieldExpression   class=\"java.lang.Double\"><![CDATA[$V{amount}]]></textFieldExpression>"); // /
		sb.append("</textField>");
		sb.append("</cellContents>");
		sb.append("</crosstabCell>");

		sb.append("<whenNoDataCell>");
		sb.append("<cellContents mode=\"Transparent\">");
		sb
				.append("<box topBorder=\"None\" topBorderColor=\"#000000\" leftBorder=\"None\" leftBorderColor=\"#000000\" rightBorder=\"Thin\" rightBorderColor=\"#000000\" bottomBorder=\"Thin\" bottomBorderColor=\"#000000\"/>");
		sb.append("</cellContents>");
		sb.append("</whenNoDataCell>");
		sb.append("</crosstab>");
		sb.append("</band>");
		sb.append("</summary>");
		return sb.toString();
	}

	public String get_query() {
		ReportConfig rc = (ReportConfig) getContent();
		return (rc != null && rc.getQuery() != null) ? rc.getQuery().getId() : null;
	}

	public void set_query(String _query) {
		this._query = _query;
	}

	public String get_reportType() {
		ReportConfig rc = ((ReportConfig) this.getContent());
		if (rc != null && rc.getReportType() != null) {
			_reportType = rc.getReportType();
		}
		return _reportType;
	}

	public void set_reportType(String reportType) {
		this._reportType = reportType;
	}

	public String get_creatReportType() {
		return _creatReportType;
	}

	public void set_creatReportType(String reportType) {
		_creatReportType = reportType;
	}

	public String get_module() {
		ReportConfig vo = (ReportConfig) getContent();
		return vo.getForm_module() != null ? vo.getForm_module().getId() : null;
	}

	public String get_path() {
		return _path;
	}

	public void set_path(String _path) {
		this._path = _path;
	}

	public void set_module(String _module) {
		this._module = _module;
	}

	public String get_searchForm() {
		ReportConfig vo = (ReportConfig) getContent();
		return vo.getSearchForm() != null ? vo.getSearchForm().getId() : null;
	}

	public void set_searchForm(String form) {
		_searchForm = form;
	}

	public String get_dts() {
		ReportConfig vo = (ReportConfig) getContent();
		return (vo != null && vo.getDataSource() != null) ? vo.getDataSource().getId() : null;
	}

	public void set_dts(String _dts) {
		this._dts = _dts;
	}

	public Collection<MappingConfig> get_mappingconfigs() {
		ReportConfig vo = (ReportConfig) getContent();
		return vo != null ? vo.getMappingconfigs() : null;
	}

	public void set_mappingconfigs(Collection<String> _mappingconfigs) throws Exception {

		Collection<MappingConfig> maps = new HashSet<MappingConfig>();
		MappingConfigProcess dp = (MappingConfigProcess) ProcessFactory.createProcess((MappingConfigProcess.class));
		for (Iterator<String> iter = _mappingconfigs.iterator(); iter.hasNext();) {
			String id = iter.next();
			MappingConfig mc = (MappingConfig) dp.doView(id);
			maps.add(mc);
		}
		((ReportConfig) getContent()).setMappingconfigs(maps);
	}

	public Map<String, Object> getCreatReportParams(Map<?, ?> params, ParamsTable paramstable, JRParameter[] p) {// paramstable里参数类型转化成符合JRParameter的类型,返回创建报表所需的参数Map(用于直接上传Jrxml产生的报表)
		Map<String, Object> creatReportParams = new HashMap<String, Object>();

		if (p == null || params == null || params.size() == 0 || p.length == 0)
			return creatReportParams;

		for (int i = 0; i < p.length; i++) {
			if (params.containsKey(p[i].getName())) {
				Class<?> clazz = p[i].getValueClass();
				if (paramstable.getParameter(p[i].getName()) != null
						&& paramstable.getParameter(p[i].getName()).equals("")) {
					continue;
				}
				if (clazz.equals(Double.class)) {
					Double value = paramstable.getParameterAsDouble(p[i].getName());
					creatReportParams.put(p[i].getName(), value);
				} else if (clazz.equals(Integer.class)) {
					Integer value = Integer.valueOf(paramstable.getParameterAsString(p[i].getName()));
					creatReportParams.put(p[i].getName(), value);
				} else if (clazz.equals(String.class)) {
					String value = paramstable.getParameterAsString(p[i].getName());
					creatReportParams.put(p[i].getName(), value);
				} else if (clazz.equals(java.util.Date.class)) {
					java.util.Date value = paramstable.getParameterAsDate(p[i].getName());
					creatReportParams.put(p[i].getName(), value);
				} else if (clazz.equals(Timestamp.class)) {
					java.util.Date date = paramstable.getParameterAsDate(p[i].getName());
					Timestamp value = new Timestamp(date.getTime());
					creatReportParams.put(p[i].getName(), value);
				} else if (clazz.equals(java.sql.Date.class)) {
					java.util.Date date = paramstable.getParameterAsDate(p[i].getName());
					java.sql.Date value = new java.sql.Date(date.getTime());
					creatReportParams.put(p[i].getName(), value);
				}
			}
		}
		return creatReportParams;
	}

}
