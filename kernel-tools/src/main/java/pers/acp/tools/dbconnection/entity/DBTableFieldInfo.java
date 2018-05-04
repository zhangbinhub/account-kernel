package pers.acp.tools.dbconnection.entity;


public class DBTableFieldInfo {

    /**
     * 数据库字段名
     */
    private String name;

    /**
     * java实体字段名
     */
    private String fieldName;

    private Object value;

    private boolean allowNull;

    private DBTableFieldType fieldType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isAllowNull() {
        return allowNull;
    }

    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    public DBTableFieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(DBTableFieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
