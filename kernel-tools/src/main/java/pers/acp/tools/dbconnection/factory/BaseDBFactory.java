package pers.acp.tools.dbconnection.factory;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/7/21.
 * 数据库操作工厂基类
 */
public abstract class BaseDBFactory {

    private Logger log = Logger.getLogger(this.getClass());

    /**
     * 构建LOB where条件语句
     *
     * @param whereValues where条件
     * @return Object[]:0-sql，1-参数Object[]
     */
    public static Object[] buildWhere(Map<String, Object> whereValues) {
        List<Object> param = new ArrayList<>();
        StringBuilder sql = new StringBuilder(" where 1=1");
        for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
            if (entry.getValue() == null) {
                sql.append(" and ").append(entry.getKey().toUpperCase()).append(" is null ");
            } else {
                param.add(entry.getValue());
                sql.append(" and ").append(entry.getKey().toUpperCase()).append("=? ");
            }
        }
        return new Object[]{sql.toString(), param.toArray()};
    }

    /**
     * 分页查询
     *
     * @param connection  数据库连接对象
     * @param param       参数
     * @param currPage    查询的当前页码
     * @param maxCount    每页最大记录数
     * @param pKey        主键名称
     * @param sqlArray    [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @param totlerecord 总记录数
     * @return 结果集
     */
    public abstract ResultSet doQueryPage(Connection connection, Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray, long totlerecord) throws SQLException;

    /**
     * 插入LOB数据
     *
     * @param connection  数据库连接对象
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     */
    public void doInsertLOB(Connection connection, String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) throws SQLException, IOException {
        StringBuilder builder = new StringBuilder();
        StringBuilder param = new StringBuilder();
        builder.append("insert into ").append(tableName).append("(").append(lobColName);
        whereValues.entrySet().stream().filter(entry -> entry.getValue() != null).forEach(entry -> {
            builder.append(",").append(entry.getKey().toUpperCase());
            param.append(",?");
        });
        builder.append(") values(?").append(param).append(")");
        log.debug("sql=" + builder.toString());
        PreparedStatement pstmt = connection.prepareStatement(builder.toString());
        pstmt.setBlob(1, input);
        int count = 2;
        for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
            if (entry.getValue() != null) {
                pstmt.setObject(count, entry.getValue());
                count++;
            }
        }
        pstmt.executeUpdate();
        if (input != null) {
            input.close();
        }
    }

    /**
     * 更新LOB数据
     *
     * @param connection  数据库连接对象
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     */
    public void doUpdateLOB(Connection connection, String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) throws SQLException, IOException {
        Object[] where = buildWhere(whereValues);
        String sqlStr = "update " + tableName + " set " + lobColName + "=? " + where[0];
        log.debug("sql=" + sqlStr);
        PreparedStatement pstmt = connection.prepareStatement(sqlStr);
        pstmt.setBlob(1, input);
        int count = 2;
        for (Map.Entry<String, Object> entry : whereValues.entrySet()) {
            if (entry.getValue() != null) {
                pstmt.setObject(count, entry.getValue());
                count++;
            }
        }
        pstmt.executeUpdate();
        if (input != null) {
            input.close();
        }
    }

}
