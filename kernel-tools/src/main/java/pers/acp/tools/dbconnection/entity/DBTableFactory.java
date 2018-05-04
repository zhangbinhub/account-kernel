package pers.acp.tools.dbconnection.entity;

import pers.acp.tools.exceptions.DBException;
import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.utility.CommonUtility;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class DBTableFactory {

    /**
     * 构建条件语句
     *
     * @param whereValues 查询条件
     * @param tableInfos  数据表信息
     * @return 条件语句
     */
    private static String buildWhereStr(Map<String, Object> whereValues, List<DBTableInfo> tableInfos) {
        StringBuilder sql = new StringBuilder(" where 1=1 ");
        if (tableInfos.size() > 1) {
            int before = 0;
            int after = 1;
            while (after < tableInfos.size()) {
                DBTableInfo bTable = tableInfos.get(before);
                DBTableInfo fTable = tableInfos.get(after);
                Map<String, DBTablePrimaryKeyInfo> pKeys = bTable.getpKeys();
                for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
                    sql.append("and ").append(bTable.getTableName()).append(".").append(entry.getKey()).append("=").append(fTable.getTableName()).append(".").append(entry.getKey()).append(" ");
                }
                before++;
                after++;
            }
        }
        if (whereValues != null) {
            List<String> remove = new ArrayList<>();
            for (Entry<String, Object> entry : whereValues.entrySet()) {
                String tablefield = "";
                for (DBTableInfo tableInfo : tableInfos) {
                    for (Entry<String, DBTablePrimaryKeyInfo> pKey : tableInfo.getpKeys().entrySet()) {
                        if ((tableInfo.getClassName() + "." + pKey.getValue().getFieldName()).equals(entry.getKey())) {
                            tablefield = tableInfo.getTableName() + "." + pKey.getKey();
                            break;
                        }
                    }
                    if (CommonUtility.isNullStr(tablefield)) {
                        for (Entry<String, DBTableFieldInfo> field : tableInfo.getFields().entrySet()) {
                            if ((tableInfo.getClassName() + "." + field.getValue().getFieldName()).equals(entry.getKey())) {
                                tablefield = tableInfo.getTableName() + "." + field.getKey();
                                break;
                            }
                        }
                    }
                    if (!CommonUtility.isNullStr(tablefield)) {
                        break;
                    }
                }
                if (!CommonUtility.isNullStr(tablefield)) {
                    if (entry.getValue() == null) {
                        sql.append("and ").append(tablefield).append(" is null ");
                    } else {
                        sql.append("and ").append(tablefield).append("=? ");
                    }
                } else {
                    remove.add(entry.getKey());
                }
            }
            remove.forEach(whereValues::remove);
        }
        return sql.toString();
    }

    /**
     * 构建where条件
     *
     * @param sql   sql语句
     * @param param 参数列表
     * @param pKeys 主键Map
     * @return 0-SQL(String),1-param(Object[])
     */
    private static Object[] appendWhere(StringBuilder sql, ArrayList<Object> param, Map<String, DBTablePrimaryKeyInfo> pKeys) throws DBException {
        sql.append(" where 1=1 ");
        for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
            DBTablePrimaryKeyInfo pKeyInfo = entry.getValue();
            if (pKeyInfo.getValue() == null) {
                throw new DBException("primary key is null!");
            } else {
                sql.append("and ").append(pKeyInfo.getName()).append("=? ");
                param.add(pKeyInfo.getValue());
            }
        }
        Object[] result = new Object[2];
        result[0] = sql.toString();
        result[1] = param.toArray();
        return result;
    }

    /**
     * 构建查询语句
     *
     * @param whereValues 查询条件
     * @param cls         目标类
     * @param obj         实例对象
     * @param attachStr   附加语句-fremark表达式，变量：${java字段名}
     * @return [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-主键名，[4]-param(Object[])，[5]-附加语句
     */
    public static Object[] buildSelectParam(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) throws Exception {
        DBTable table;
        if (obj != null) {
            table = obj;
        } else {
            table = cls.newInstance();
        }
        List<DBTableInfo> tableInfos = table.getTableInfos();
        String pkeystr = "";
        String filedstr = "";
        String tablestr = "";
        String wherestr = buildWhereStr(whereValues, tableInfos);
        Map<String, String> attachMap = new HashMap<>();
        for (DBTableInfo tableinfo : tableInfos) {
            Map<String, DBTablePrimaryKeyInfo> pKeys = tableinfo.getpKeys();
            for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
                String pkey = tableinfo.getTableName() + "." + entry.getKey();
                if (CommonUtility.isNullStr(pkeystr)) {
                    pkeystr = pkey;
                }
                filedstr += pkey + ",";
                if (!attachMap.containsKey(tableinfo.getClassName() + "." + entry.getValue().getFieldName())) {
                    attachMap.put(tableinfo.getClassName() + "." + entry.getValue().getFieldName(), pkey);
                }
            }
            Map<String, DBTableFieldInfo> fields = tableinfo.getFields();
            for (Entry<String, DBTableFieldInfo> entry : fields.entrySet()) {
                String filed = tableinfo.getTableName() + "." + entry.getKey();
                filedstr += filed + ",";
                if (!attachMap.containsKey(tableinfo.getClassName() + "." + entry.getValue().getFieldName())) {
                    attachMap.put(tableinfo.getClassName() + "." + entry.getValue().getFieldName(), filed);
                }
            }
            tablestr += tableinfo.getTableName() + ",";
        }
        filedstr = filedstr.substring(0, filedstr.length() - 1);
        tablestr = tablestr.substring(0, tablestr.length() - 1);
        Object[] result = new Object[6];
        result[0] = filedstr;
        result[1] = tablestr;
        result[2] = wherestr;
        result[3] = pkeystr;
        ArrayList<Object> param = new ArrayList<>();
        if (whereValues != null) {
            param.addAll(whereValues.entrySet().stream().filter(entry -> entry.getValue() != null).map(Entry::getValue).collect(Collectors.toList()));
        }
        if (!param.isEmpty()) {
            result[4] = param.toArray();
        } else {
            result[4] = null;
        }
        if (CommonUtility.isNullStr(attachStr)) {
            result[5] = "";
        } else {
            result[5] = FileCommon.replaceVar(attachStr, attachMap);
        }
        return result;
    }

    /**
     * 构建查询语句
     *
     * @param whereValues 查询条件
     * @param cls         目标类
     * @param obj         实例对象
     * @param attachStr   附加语句
     * @return 0-SQL(String),1-param(Object[])
     */
    public static Object[] buildSelectStr(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) throws Exception {
        Object[] param = buildSelectParam(whereValues, cls, obj, attachStr);
        Object[] result = new Object[2];
        result[0] = "select " + param[0] + " from " + param[1] + param[2] + " " + param[5];
        result[1] = param[4];
        return result;
    }

    /**
     * 构建插入语句
     *
     * @param tableInfo 数据表信息
     * @return 0-SQL(String),1-param(Object[])
     */
    static Object[] buildInsertStr(DBTableInfo tableInfo) throws DBException {
        Object[] result = new Object[2];
        if (tableInfo != null) {
            StringBuilder sql = new StringBuilder("insert into " + tableInfo.getTableName() + "(");
            String values = "";
            ArrayList<Object> param = new ArrayList<>();
            Map<String, DBTablePrimaryKeyInfo> pKeys = tableInfo.getpKeys();
            for (Entry<String, DBTablePrimaryKeyInfo> entry : pKeys.entrySet()) {
                DBTablePrimaryKeyInfo pKeyInfo = entry.getValue();
                if (pKeyInfo.getValue() == null) {
                    throw new DBException("primary key is null!");
                } else {
                    sql.append(pKeyInfo.getName()).append(",");
                    values += "?,";
                    param.add(pKeyInfo.getValue());
                }
            }
            Map<String, DBTableFieldInfo> fields = tableInfo.getFields();
            Iterator<Entry<String, DBTableFieldInfo>> ifields = fields.entrySet().iterator();
            while (ifields.hasNext()) {
                Entry<String, DBTableFieldInfo> entry = ifields.next();
                DBTableFieldInfo fieldInfo = entry.getValue();
                if (!fieldInfo.getFieldType().equals(DBTableFieldType.Blob)) {
                    if (!fieldInfo.isAllowNull() && fieldInfo.getValue() == null) {
                        throw new DBException("field " + fieldInfo.getName() + " is null!");
                    } else {
                        if (ifields.hasNext()) {
                            sql.append(fieldInfo.getName()).append(",");
                            values += "?,";
                        } else {
                            sql.append(fieldInfo.getName()).append(")");
                            values += "?";
                        }
                        param.add(fieldInfo.getValue());
                    }
                }
            }
            result[0] = sql.substring(0, sql.length() - 1) + ") values(" + values + ")";
            result[1] = param.toArray();
        }
        return result;
    }

    /**
     * 构建更新语句
     *
     * @param tableInfo      数据表信息
     * @param updateIncludes 更新时指定只更新的字段名
     * @param updateExcludes 更新时指定不更新的字段名
     * @return 0-SQL(String),1-param(Object[])
     */
    static Object[] buildUpdateStr(DBTableInfo tableInfo, List<String> updateIncludes, List<String> updateExcludes) throws DBException {
        if (tableInfo != null) {
            StringBuilder sql = new StringBuilder("update " + tableInfo.getTableName() + " set ");
            ArrayList<Object> param = new ArrayList<>();
            Map<String, DBTableFieldInfo> fields = tableInfo.getFields();
            for (Entry<String, DBTableFieldInfo> entry : fields.entrySet()) {
                DBTableFieldInfo fieldInfo = entry.getValue();
                if (!isFilter(fieldInfo.getFieldName(), updateIncludes, updateExcludes)) {
                    if (!fieldInfo.getFieldType().equals(DBTableFieldType.Blob)) {
                        if (!fieldInfo.isAllowNull() && fieldInfo.getValue() == null) {
                            throw new DBException("field " + fieldInfo.getName() + " is null!");
                        } else {
                            sql.append(fieldInfo.getName()).append("=?,");
                            param.add(fieldInfo.getValue());
                        }
                    }
                }
            }
            if (param.isEmpty()) {
                return null;
            } else {
                return appendWhere(new StringBuilder(sql.substring(0, sql.length() - 1)), param, tableInfo.getpKeys());
            }
        } else {
            return null;
        }
    }

    /**
     * 构建更新语句
     *
     * @param tableInfo 数据表信息
     * @return 0-SQL(String),1-param(Object[])
     */
    static Object[] buildDeleteStr(DBTableInfo tableInfo) throws DBException {
        Object[] result = new Object[2];
        if (tableInfo != null) {
            StringBuilder sql = new StringBuilder("delete from ").append(tableInfo.getTableName());
            result = appendWhere(sql, new ArrayList<>(), tableInfo.getpKeys());
        }
        return result;
    }

    /**
     * 字段是否需要被过滤
     *
     * @param fieldName      字段名
     * @param updateIncludes 包含的字段数组
     * @param updateExcludes 排除的字段数组
     * @return 是否被过滤
     */
    static boolean isFilter(String fieldName, List<String> updateIncludes, List<String> updateExcludes) {
        return !((updateIncludes == null || updateIncludes.isEmpty() || CommonUtility.strInList(fieldName, updateIncludes, true)) && (updateExcludes == null || updateExcludes.isEmpty() || !CommonUtility.strInList(fieldName, updateExcludes, true)));
    }

}
