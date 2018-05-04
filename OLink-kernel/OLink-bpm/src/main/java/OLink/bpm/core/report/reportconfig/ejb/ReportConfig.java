package OLink.bpm.core.report.reportconfig.ejb;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb.MappingConfig;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;
import OLink.bpm.core.report.query.ejb.Query;
import OLink.bpm.core.dynaform.form.ejb.Form;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;

/**
 * @hibernate.class table="T_REPORTCONFIG"
 * 
 */
public class ReportConfig extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 379757668636659300L;

	private String id;

	private String title;

	private Set<TableColumn> fields;

	private Query query;

	// private ApplicationVO application;

	private ModuleVO module;

	private String reportType;

	private String CreatReportType;

	public static final String Column_Type_Group1 = "group1";
	public static final String Column_Type_Group2 = "group2";
	public static final String Column_Type_Group3 = "group3";
	public static final String Column_Type_Group4 = "group4";
	public static final String Column_Type_Detail = "detail";
	public static final String Column_Type_summary = "summary";

	public static final String CrossTable_Type_RowGroup = "rowGroup";
	public static final String CrossTable_Type_ColumnGroup = "columnGroup";
	public static final String CrossTable_Type_CrosstabCell = "crosstabCell";

	public static final String Report_Type_ListTable = "ListTable";
	public static final String Report_Type_CrossTable = "CrossTable";

	public static final String Creat_Report_Type_UploadJrxml = "Upload_ReportJrxml";
	public static final String Creat_Report_Type_CustomMade = "CustomMade";

	private String crossTable_CalculateType;

	private String crossTable_CalculateField;

	private String name;

	private Form searchForm;

	private String jrxml;

	// private ApplicationVO form_application;

	private ModuleVO form_module;

	private DataSource dataSource;

	private Collection<MappingConfig> mappingconfigs = new HashSet<MappingConfig>();

	private DataPrepare dataPrepare;

	/**
	 * @hibernate.many-to-one column="DATAPREPARE_ID"
	 *                        class="DataPrepare"
	 */
	public DataPrepare getDataPrepare() {
		return dataPrepare;
	}

	/**
	 * @param dataPrepare
	 *            The dataPrepare to set.
	 */
	public void setDataPrepare(DataPrepare dataPrepare) {
		this.dataPrepare = dataPrepare;
	}

	/**
	 * @hibernate.property column="CREATREPORTTYPE"
	 */
	public String getCreatReportType() {
		return CreatReportType;
	}

	/**
	 * @param creatReportType
	 *            The creatReportType to set.
	 */
	public void setCreatReportType(String creatReportType) {
		CreatReportType = creatReportType;
	}

	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	/**
	 * @hibernate.property column="TITLE"
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            The title to set.
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.collection-one-to-many class="TableColumn"
	 * @hibernate.collection-key column="REPORTCONFIG_ID"
	 * @hibernate.set name="fields" table="T_TABLECOLUMN" cascade="all"
	 * @return
	 */
	public Set<TableColumn> getFields() {
		return fields;
	}

	/**
	 * @param columns
	 *            The columns to set.
	 */
	public void setFields(Set<TableColumn> fields) {
		this.fields = fields;
	}

	/**
	 * @hibernate.many-to-one column="QUERY_ID"
	 *                        class="Query"
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * @param query
	 *            The query to set.
	 */
	public void setQuery(Query query) {
		this.query = query;
	}

	// /**
	// * @return ApplicationVO
	// * @hibernate.many-to-one
	// class="ApplicationVO"
	// * column="APPLICATION"
	// */
	// public ApplicationVO getApplication() {
	// return application;
	// }
	//
	// public void setApplication(ApplicationVO application) {
	// this.application = application;
	// }

	/**
	 * @return ModuleVO
	 * @hibernate.many-to-one class="ModuleVO"
	 *                        column="MODULE"
	 */
	public ModuleVO getModule() {
		return module;
	}

	public void setModule(ModuleVO module) {
		this.module = module;
	}

	/**
	 * @hibernate.property column="REPORTTYPE"
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * @param reportType
	 *            The reportType to set.
	 */
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	/**
	 * @hibernate.many-to-one column="DATASOURCE_ID"
	 *                        class="DataSource"
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            The dataSource to set.
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	// /**
	// * @return ApplicationVO
	// * @hibernate.many-to-one
	// class="ApplicationVO"
	// * column="FORM_APPLICATION"
	// */
	// public ApplicationVO getForm_application() {
	// return form_application;
	// }
	//
	// /**
	// * @param form_application The form_application to set.
	// */
	// public void setForm_application(ApplicationVO form_application) {
	// this.form_application = form_application;
	// }

	/**
	 * @return ModuleVO
	 * @hibernate.many-to-one class="ModuleVO"
	 *                        column="FORM_MODULE"
	 */
	public ModuleVO getForm_module() {
		return form_module;
	}

	/**
	 * @param form_module
	 *            The form_module to set.
	 */
	public void setForm_module(ModuleVO form_module) {
		this.form_module = form_module;
	}

	/**
	 * @hibernate.property column="JRXML" type = "text"
	 */
	public String getJrxml() {
		return jrxml;
	}

	/**
	 * @param jrxml
	 *            The jrxml to set.
	 */
	public void setJrxml(String jrxml) {
		this.jrxml = jrxml;
	}

	/**
	 * @hibernate.set name="mappingconfigs" table="T_REPORT_MAPPING_SET"
	 *                lazy="true" cascade="save-update"
	 * @hibernate.collection-key column="REPORT_ID"
	 * @hibernate.collection-many-to-many class="MappingConfig"
	 *                                    column="MAPPINGCONFIGS_ID"
	 */
	public Collection<MappingConfig> getMappingconfigs() {
		return mappingconfigs;
	}

	/**
	 * @param mappingconfigs
	 *            The mappingconfigs to set.
	 */
	public void setMappingconfigs(Collection<MappingConfig> mappingconfigs) {
		this.mappingconfigs = mappingconfigs;
	}

	/**
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @hibernate.many-to-one column="FORM_ID"
	 *                        class="Form"
	 */
	public Form getSearchForm() {
		return searchForm;
	}

	/**
	 * @param searchForm
	 *            The searchForm to set.
	 */
	public void setSearchForm(Form searchForm) {
		this.searchForm = searchForm;
	}

	/**
	 * @hibernate.property column="CROSSTABLE_CALCULATETYPE"
	 */
	public String getCrossTable_CalculateType() {
		return crossTable_CalculateType;
	}

	/**
	 * @param crossTable_CalculateType
	 *            The crossTable_CalculateType to set.
	 */
	public void setCrossTable_CalculateType(String crossTable_CalculateType) {
		this.crossTable_CalculateType = crossTable_CalculateType;
	}

	/**
	 * @hibernate.property column="CROSSTABLE_CALCULATEFIELD"
	 */
	public String getCrossTable_CalculateField() {
		return crossTable_CalculateField;
	}

	/**
	 * @param crossTable_CalculateField
	 *            The crossTable_CalculateField to set.
	 */
	public void setCrossTable_CalculateField(String crossTable_CalculateField) {
		this.crossTable_CalculateField = crossTable_CalculateField;
	}

}
