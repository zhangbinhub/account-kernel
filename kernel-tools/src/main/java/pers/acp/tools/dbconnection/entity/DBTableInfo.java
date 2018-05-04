package pers.acp.tools.dbconnection.entity;

import java.util.HashMap;
import java.util.Map;

public class DBTableInfo {

    /**
     * 表名
     */
    private String tableName;

    /**
     * java类名，不含包路径
     */
    private String className;

    /**
     * 是否分开存储
     */
    private boolean isSeparate;

    /**
     * 主键信息，主键名=>主键信息
     */
    private Map<String, DBTablePrimaryKeyInfo> pKeys = new HashMap<>();

    /**
     * 字段信息，字段名=>字段信息
     */
    private Map<String, DBTableFieldInfo> fields = new HashMap<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean isSeparate() {
        return isSeparate;
    }

    public void setSeparate(boolean isSeparate) {
        this.isSeparate = isSeparate;
    }

    public Map<String, DBTablePrimaryKeyInfo> getpKeys() {
        return pKeys;
    }

    public void setpKeys(Map<String, DBTablePrimaryKeyInfo> pKeys) {
        this.pKeys = pKeys;
    }

    public Map<String, DBTableFieldInfo> getFields() {
        return fields;
    }

    public void setFields(Map<String, DBTableFieldInfo> fields) {
        this.fields = fields;
    }

}
