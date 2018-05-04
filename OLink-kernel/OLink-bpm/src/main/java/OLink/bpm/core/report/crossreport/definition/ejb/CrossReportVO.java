package OLink.bpm.core.report.crossreport.definition.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * @hibernate.class table="T_CROSSREPORT" batch-size="10" lazy="false"
 */
public class CrossReportVO extends ValueObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4068910641260393393L;
	
	private String qtype ;
	
	private String sql ;
	
	private String dql ;
	
	private String form;
	
	private String formCondition;
	/**
	 * The column set 
	 */
	private String columns;
	/**
	 * The row set 
	 */
	private String rows ;
	/**
	 * The filter set
	 */
	private String filters;

	/**
	 * 所有各个类型的列
	 */
	private String datas ;
	/**
	 * The calculation method
	 */
	private String calculationMethod;
	
	
	private String type ;
	
	private String view;
	
	private String name;
	
	private String applicationid;
	
	private String domainid;
	
	private String module;
	
	boolean displayRow;
	
	boolean displayCol;
	
	private String rowCalMethod;
	
	private String colCalMethod;
	
	private String reportTitle;
	
	private String note;
	
	private String json;
	
	private String userid;
	
	/**
	 * 是否签出
	 */
	private  boolean checkout = false;
	
	/**
	 * 签出者
	 */
	private String checkoutHandler;
	
	/**
	 * 是否被签出
	 * @return
	 */
	public boolean isCheckout() {
		return checkout;
	}

	/**
	 * 设置是否签出
	 * @param checkout
	 */
	public void setCheckout(boolean checkout) {
		this.checkout = checkout;
	}

	/**
	 * 获取签出者
	 * @return
	 */
	public String getCheckoutHandler() {
		return checkoutHandler;
	}

	/**
	 * 设置签出者
	 * @param checkoutHandler
	 */
	public void setCheckoutHandler(String checkoutHandler) {
		this.checkoutHandler = checkoutHandler;
	}

	public String getJson() {
		return json;
	}


	public void setJson(String json) {
		this.json = json;
	}


	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getReportTitle() {
		return reportTitle;
	}


	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}


	public String getNote() {
		return note;
	}


	public void setNote(String note) {
		this.note = note;
	}


	public boolean isDisplayRow() {
		return displayRow;
	}


	public void setDisplayRow(boolean displayRow) {
		this.displayRow = displayRow;
	}


	public boolean isDisplayCol() {
		return displayCol;
	}


	public void setDisplayCol(boolean displayCol) {
		this.displayCol = displayCol;
	}


	public String getRowCalMethod() {
		return rowCalMethod;
	}


	public void setRowCalMethod(String rowCalMethod) {
		this.rowCalMethod = rowCalMethod;
	}


	public String getColCalMethod() {
		return colCalMethod;
	}


	public void setColCalMethod(String colCalMethod) {
		this.colCalMethod = colCalMethod;
	}


	public String getModule() {
		return module;
	}


	public void setModule(String module) {
		this.module = module;
	}


	public String getApplicationid() {
		return applicationid;
	}


	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}


	public String getDomainid() {
		return domainid;
	}


	public void setDomainid(String domainid) {
		this.domainid = domainid;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return java.lang.String
	 * @hibernate.property column="CALCULATIONMETHOD"
	 * @roseuid 44C5FCE002C2
	 * @uml.property name="calculationMethod"
	 */

	public String getCalculationMethod() {
		return calculationMethod;
	}


	public void setCalculationMethod(String calculationMethod) {
		this.calculationMethod = calculationMethod;
	}

	


	public String getColumns() {
		return columns;
	}


	public void setColumns(String columns) {
		this.columns = columns;
	}


	public String getRows() {
		return rows;
	}


	public void setRows(String rows) {
		this.rows = rows;
	}


	public String getFilters() {
		return filters;
	}


	public void setFilters(String filters) {
		this.filters = filters;
	}


	public String getDatas() {
		return datas;
	}


	public void setDatas(String datas) {
		this.datas = datas;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getView() {
		return view;
	}


	public void setView(String view) {
		this.view = view;
	}


	public String getQtype() {
		return qtype;
	}


	public void setQtype(String qtype) {
		this.qtype = qtype;
	}


	public String getSql() {
		return sql;
	}


	public void setSql(String sql) {
		this.sql = sql;
	}


	public String getDql() {
		return dql;
	}


	public void setDql(String dql) {
		this.dql = dql;
	}


	public String getForm() {
		return form;
	}


	public void setForm(String form) {
		this.form = form;
	}


	public String getFormCondition() {
		return formCondition;
	}


	public void setFormCondition(String formCondition) {
		this.formCondition = formCondition;
	}
	
	
		
}
