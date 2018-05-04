package OLink.bpm.core.report.tablecolumn.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;

/**
 * @hibernate.class table="T_TABLECOLUMN"
 * 
 */
public class TableColumn  extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	
	private String name;

	private ReportConfig reportConfig;
	
	private String type;
	
	private boolean sort;
	
	private String calculateMode;
	
	private String description;
	
	private String  width;
	
	private int orderno;
	
	private int fontSize;
	
	private String backColor;
	
	public  static final String CALCULATE_TYPE_SUM="Sum";
	
	public static final String  CALCULATE_TYPE_AVG="Average";
	
	public static final String  CALCULATE_TYPE_COUNT="Count";
	
	public static final String  CALCULATE_TYPE_LOWEST="Lowest";
	
	public static final String  CALCULATE_TYPE_HIGHEST="Highest";
	
	public static final String  CALCULATE_TYPE_NOTHING="Nothing";
	
	public static final String  CALCULATE_TYPE_SYSTEM="System";
	/**
	 * @hibernate.id column="ID" generator-class="assigned"
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @hibernate.property column="NAME"
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @hibernate.property column="TYPE"
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @hibernate.many-to-one column="REPORTCONFIG_ID"
	 *                        class="ReportConfig"
	 */
	public ReportConfig getReportConfig() {
		return reportConfig;
	}

	/**
	 * @param reportConfig The reportConfig to set.
	 */
	public void setReportConfig(ReportConfig reportConfig) {
		this.reportConfig = reportConfig;
	}

	/**
	 * @hibernate.property column="CALCULATEMODE"
	 */
	public String getCalculateMode() {
		return calculateMode;
	}

	/**
	 * @param calculateMode The calculateMode to set.
	 */
	public void setCalculateMode(String calculateMode) {
		this.calculateMode = calculateMode;
	}

	/**
	 * @hibernate.property column="SORT"
	 */
	public boolean isSort() {
		return sort;
	}

	/**
	 * @param sort The sort to set.
	 */
	public void setSort(boolean sort) {
		this.sort = sort;
	}

	/**
	 * @hibernate.property column="DESCRIPTION"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @hibernate.property column="WIDTH"
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * @param width The width to set.
	 */
	public void setWidth(String width) {
		this.width = width;
	}

	/**
	 * @hibernate.property column="ORDERNO"
	 */
	public int getOrderno() {
		return orderno;
	}

	/**
	 * @param orderno The orderno to set.
	 */
	public void setOrderno(int orderno) {
		this.orderno = orderno;
	}

	/**
	 * @hibernate.property column="BACKCOLOR"
	 */
	public String getBackColor() {
		return backColor;
	}

	/**
	 * @param backColor The backColor to set.
	 */
	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}

	/**
	 * @hibernate.property column="FONTSIZE"
	 */
	public int getFontSize() {
		return fontSize;
	}

	/**
	 * @param fontSize The fontSize to set.
	 */
	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}






	
}
