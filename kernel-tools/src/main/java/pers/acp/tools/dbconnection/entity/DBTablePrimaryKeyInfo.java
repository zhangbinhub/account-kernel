package pers.acp.tools.dbconnection.entity;


public class DBTablePrimaryKeyInfo {

    /**
     * 数据库字段名
     */
    private String name;

    /**
     * java实体字段名
     */
    private String fieldName;

    private DBTablePrimaryKeyType pKeyType;

    private Object value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public DBTablePrimaryKeyType getpKeyType() {
        return pKeyType;
    }

    public void setpKeyType(DBTablePrimaryKeyType pKeyType) {
        this.pKeyType = pKeyType;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
