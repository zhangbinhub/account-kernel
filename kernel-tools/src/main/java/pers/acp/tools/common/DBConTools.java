package pers.acp.tools.common;

import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.dbconnection.ConnectionFactory;
import pers.acp.tools.dbconnection.DBType;
import pers.acp.tools.dbconnection.entity.DBTable;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.utility.CommonUtility;
import com.jolbox.bonecp.BoneCPDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public final class DBConTools {

    private static Logger log = Logger.getLogger(DBConTools.class);

    private ConnectionFactory dbcon = null;

    /**
     * 默认构造函数
     */
    public DBConTools() {
        dbcon = new ConnectionFactory();
    }

    /**
     * 构造函数
     *
     * @param connectionNo 系统配置的数据源编号
     */
    public DBConTools(int connectionNo) {
        dbcon = new ConnectionFactory(connectionNo);
    }

    /**
     * 初始化数据库工具类
     */
    public static void initTools() {
        SystemConfig systemConfig = null;
        try {
            systemConfig = SystemConfig.getInstance();
        } catch (ConfigException e) {
            log.error(e.getMessage(), e);
        }
        if (systemConfig != null && systemConfig.getDataBaseConfig() != null) {
            List<SystemConfig.DataBaseConfig.Connection> connections = systemConfig.getDataBaseConfig().getConnection();
            if (connections != null && !connections.isEmpty()) {
                int defaultNo = -1;
                for (SystemConfig.DataBaseConfig.Connection con : connections) {
                    boolean isDefault = false;
                    if (con.isDefault() && defaultNo < 0) {
                        defaultNo = con.getConnectionNo();
                        isDefault = true;
                    }
                    String poolName = ConnectionFactory.DEFAULT_POOLNAME;
                    if (!CommonUtility.isNullStr(con.getPoolName())) {
                        poolName = con.getPoolName();
                    }
                    log.info("database resource " + con.getConnectionNo() + ": [default=" + isDefault + "] poolName=" + poolName + ",name=" + con.getName() + ",type=" + con.getDbtype() + ",jndi=java:comp/env/" + con.getResourceName());
                }
                if (defaultNo < 0) {
                    defaultNo = 0;
                }
                ConnectionFactory.setDefaultConnectionNo(defaultNo);
                log.info("default database number is [" + ConnectionFactory.getDefaultConnectionNo() + "]");
            } else {
                log.error("don't find database resources!");
            }
        } else {
            log.error("don't find database resources!");
        }
    }

    /**
     * 销毁所有连接池
     */
    public static void destroyAllConnections() {
        ConnectionFactory.destroyAllConnections();
    }

    /**
     * 获取数据库连接资源
     *
     * @return 数据库连接资源
     */
    public BoneCPDataSource getDataSource() {
        return dbcon.getDataSource();
    }

    /**
     * 查询数据
     *
     * @param sql sql语句
     * @return 结果集
     */
    public ResultSet getDataSetBySql(String sql) {
        return dbcon.doQueryForSet(sql);
    }

    /**
     * 查询数据
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果集
     */
    public ResultSet getDataSetBySql(String sql, Object[] param) {
        return dbcon.doQueryForSet(sql, param);
    }

    /**
     * 查询数据
     *
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] getDataSetBySql(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbcon.doQueryForSet(currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param param    参数
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] getDataSetBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbcon.doQueryForSet(param, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param sql sql语句
     * @return 结果List
     */
    public List<Map<String, Object>> getDataListBySql(String sql) {
        return dbcon.doQueryForList(sql);
    }

    /**
     * 查询数据
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果List
     */
    public List<Map<String, Object>> getDataListBySql(String sql, Object[] param) {
        return dbcon.doQueryForList(sql, param);
    }

    /**
     * 查询数据
     *
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataListBySql(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbcon.doQueryForList(currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param param    参数
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataListBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbcon.doQueryForList(param, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param sql sql语句
     * @return json对象
     */
    public JSONArray getDataJSONArrayBySql(String sql) {
        return dbcon.doQueryForJSON(sql);
    }

    /**
     * 查询数据
     *
     * @param sql   sql语句
     * @param param 参数
     * @return json对象
     */
    public JSONArray getDataJSONArrayBySql(String sql, Object[] param) {
        return dbcon.doQueryForJSON(sql, param);
    }

    /**
     * 查询数据
     *
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:long-总页数,records:long-总记录数,datas:array-结果集}
     */
    public JSONObject getDataJSONArrayBySql(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbcon.doQueryForJSON(currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param param    参数
     * @param currPage 当前页数
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray SQL信息，用以特殊优化：[0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:int-总页数,records:int-总记录数,datas:array-结果集}
     */
    public JSONObject getDataJSONArrayBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        return dbcon.doQueryForJSON(param, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询数据
     *
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @param attachStr   附加语句（例如：order by）
     * @return 结果List
     */
    public List<DBTable> getDataObjListBySql(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) {
        return dbcon.doQueryForObjList(whereValues, cls, obj, attachStr);
    }

    /**
     * 查询
     *
     * @param currPage    查询的当前页码
     * @param maxCount    每页最大记录数
     * @param whereValues 查询条件
     * @param cls         类
     * @param obj         实例对象
     * @param attachStr   附加语句（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataObjListBySql(long currPage, long maxCount, Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) {
        return dbcon.doQueryForObjList(currPage, maxCount, whereValues, cls, obj, attachStr);
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @param cls 类
     * @return 结果List
     */
    public <T> List<T> getDataCusObjListBySql(String sql, Class<T> cls) {
        return dbcon.doQueryForCusObjList(sql, cls);
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @param cls   类
     * @return 结果List
     */
    public <T> List<T> getDataCusObjListBySql(String sql, Object[] param, Class<T> cls) {
        return dbcon.doQueryForCusObjList(sql, param, cls);
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @param cls      类
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataCusObjListBySql(long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        return dbcon.doQueryForCusObjList(currPage, maxCount, pKey, sqlArray, cls);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @param cls      类
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] getDataCusObjListBySql(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        return dbcon.doQueryForCusObjList(param, currPage, maxCount, pKey, sqlArray, cls);
    }

    /**
     * 查询LOB数据
     *
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @return 输入流
     */
    public InputStream getLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        return dbcon.doQueryLOB(tableName, whereValues, lobColName);
    }

    /**
     * 查询CLOB数据
     *
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @return 结果字符串
     */
    public String getCLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        return dbcon.doQueryCLOB(tableName, whereValues, lobColName);
    }

    /**
     * 执行SQL
     *
     * @param sql sql语句
     * @return 成功或失败
     */
    public boolean doUpdate(String sql) {
        return dbcon.doUpdate(sql);
    }

    /**
     * 执行SQL
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 成功或失败
     */
    public boolean doUpdate(String sql, Object[] param) {
        return dbcon.doUpdate(sql, param);
    }

    /**
     * 插入LOB数据
     *
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     * @return 成功或失败
     */
    public boolean doInsertLOB(String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) {
        return dbcon.doInsertLOB(tableName, whereValues, lobColName, input);
    }

    /**
     * 更新LOB数据
     *
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @param input       lob数据流
     * @return 成功或失败
     */
    public boolean doUpdateLOB(String tableName, Map<String, Object> whereValues, String lobColName, InputStream input) {
        return dbcon.doUpdateLOB(tableName, whereValues, lobColName, input);
    }

    /**
     * 开始事务
     */
    public void beginTranslist() {
        dbcon.beginTranslist();
    }

    /**
     * 提交事务
     */
    public void commitTranslist() {
        dbcon.commitTranslist();
    }

    /**
     * 回滚事务
     */
    public void rollBackTranslist() {
        dbcon.rollBackTranslist();
    }

    /**
     * 添加批量SQL语句
     *
     * @param sql sql语句
     */
    public void addBatch(String sql) {
        dbcon.addBatch(sql);
    }

    /**
     * 批量执行SQL
     *
     * @return 成功或失败
     */
    public boolean excuteBatch() {
        return dbcon.excuteBatch();
    }

    /**
     * 获取当前数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        return dbcon.getDbName();
    }

    /**
     * 获取数据库连接类型
     *
     * @return 链接类型
     */
    public DBType getDbType() {
        return dbcon.getDbType();
    }

    /**
     * 获取数据源编号
     *
     * @return 数据源编号
     */
    public int getDbNo() {
        return dbcon.getDbNo();
    }

    /**
     * 获取数据库驱动类型
     *
     * @return 驱动类型
     */
    public String getDbDriverType() {
        return dbcon.getDbDriverType();
    }

    /**
     * 获取数据库连接对象
     *
     * @return 数据库连接对象
     */
    public ConnectionFactory getDbcon() {
        return dbcon;
    }

    /**
     * 是否自动提交事务
     *
     * @return 是否自动提交
     */
    public boolean isAutoCommit() {
        return dbcon.isAutoCommit();
    }

    /**
     * 获取系统默认数据源编号
     *
     * @return 默认数据源编号
     */
    public static int getDefaultConnectionNo() {
        return ConnectionFactory.getDefaultConnectionNo();
    }

}
