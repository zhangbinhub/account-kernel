package OLink.bpm.core.dynaform.dts.datasource.ejb.metadata;

public class ColumnMetadata implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1741722047509650117L;

	private String column;
	private String field;
	private String fieldType;
	private String type;
	private String des;

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDes() {
		return des;
	}

	public void setDes(String des) {
		this.des = des;
	}

}
