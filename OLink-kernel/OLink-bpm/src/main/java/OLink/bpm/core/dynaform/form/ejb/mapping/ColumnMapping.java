package OLink.bpm.core.dynaform.form.ejb.mapping;

import java.io.Serializable;

/**
 * 数据库字段映射
 * 
 * @author Administrator
 * 
 */
public class ColumnMapping implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7559832281270972498L;

	/**
	 * 表单字段名称
	 */
	private String fieldName;

	/**
	 * 数据库字段名称
	 */
	private String columnName;

	/**
	 * 是否为主键
	 */
	private boolean primaryKey;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}
}
