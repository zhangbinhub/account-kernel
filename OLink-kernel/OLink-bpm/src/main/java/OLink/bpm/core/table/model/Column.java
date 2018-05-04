package OLink.bpm.core.table.model;

import java.sql.Types;

/**
 * 
 * @author nicholas
 * 
 */
public class Column implements Cloneable {
	private String id;

	private String name;

	private int typeCode;

	private boolean primaryKey;

	private boolean notNull;

	private String fieldName;

	private String length;

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Column(String id, String name, int typeCode) {
		this.id = id;
		this.name = name;
		this.typeCode = typeCode;
	}

	public Column(String id, String name, int typeCode, String length) {
		this.id = id;
		this.name = name;
		this.typeCode = typeCode;
		this.length = length;
	}

	public Column(String id, String name, int typeCode, boolean isPrimaryKey, boolean isNotNull) {
		this.id = id;
		this.name = name;
		this.typeCode = typeCode;
		this.primaryKey = isPrimaryKey;
		this.notNull = isNotNull;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(int typeCode) {
		this.typeCode = typeCode;
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String toString() {
		return name;
	}

	public boolean equals(Object obj) {
		if(obj == null)return false;
		if(!(obj instanceof Column))return false;
		Column anColumn = (Column) obj;

		if (this.getName().equalsIgnoreCase(anColumn.getName()) && this.getTypeCode() == anColumn.getTypeCode()) {
			return true;
		} else {
			return super.equals(obj);
		}
	}
	
	public int hashCode(){
		return super.hashCode();
	}

	/**
	 * 比较新旧Column是否兼容
	 * 
	 * @param column
	 *            要比较的列
	 * @return true or false
	 */
	public boolean isCompatible(Column anColumn) {
		boolean rtn = false;

		int anotherTypeCode = anColumn.getTypeCode();

		switch (typeCode) {
		case Types.VARCHAR:
			switch (anotherTypeCode) {
			case Types.CLOB:
				rtn = true;
				break;
			default:
				rtn = false;
			}
			break;

		case Types.NUMERIC:
			switch (anotherTypeCode) {
			case Types.CLOB:
				rtn = true;
				break;
			default:
				rtn = false;
			}
			break;

		case Types.DATE:
			switch (anotherTypeCode) {
			case Types.CLOB:
				rtn = true;
				break;
			default:
				rtn = false;
			}
			break;

		case Types.CLOB:
			switch (anotherTypeCode) {
			case Types.CLOB:
				rtn = true;
				break;
			default:
				rtn = false;
			}
			break;

		default:
			break;
		}
		return rtn;
	}

	public boolean isNotNull() {
		return notNull;
	}

	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
}
