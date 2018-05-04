package pers.acp.tools.dbconnection;

import com.jolbox.bonecp.BoneCPConfig;
import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.dbconnection.annotation.ADBTable;
import pers.acp.tools.dbconnection.annotation.ADBTableField;
import pers.acp.tools.dbconnection.annotation.ADBTablePrimaryKey;
import pers.acp.tools.dbconnection.entity.DBTable;
import pers.acp.tools.dbconnection.entity.DBTableFactory;
import pers.acp.tools.dbconnection.entity.DBTableFieldType;
import pers.acp.tools.dbconnection.entity.DBTablePrimaryKeyType;
import pers.acp.tools.dbconnection.factory.BaseDBFactory;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.utility.CommonUtility;
import com.jolbox.bonecp.BoneCPDataSource;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class ConnectionFactory {

    private Logger log = Logger.getLogger(this.getClass());

    /**
     * 默认连接池实例名称
     */
    public static String DEFAULT_POOLNAME = "BoneCP";

    /**
     * 系统默认数据源编号
     */
    private static int DEFAULT_CONNECTION_NO = 0;

    private int connectionNo = 0;

    private DBType dbType;

    private static ConcurrentHashMap<String, BoneCPDataSource> dsMap = new ConcurrentHashMap<>();

    private BoneCPDataSource ds = null;

    private Connection connection = null;

    private Statement stmt = null;

    /**
     * 构造函数
     */
    public ConnectionFactory() {
        this(DEFAULT_CONNECTION_NO);
    }

    /**
     * 构造函数
     */
    public ConnectionFactory(int no) {
        connectionNo = no;
        try {
            initConnection();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 初始化数据库链接
     */
    private void initConnection() throws NamingException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, ConfigException, IOException {
        File file = new File(this.getClass().getResource("/").getPath() + "/bonecp.properties");
        if (file.exists()) {
            Properties pps = new Properties();
            pps.load(new FileInputStream(file));
            BoneCPConfig boneCPConfig = new BoneCPConfig();
            boneCPConfig.setJdbcUrl(pps.getProperty("bonecp." + connectionNo + ".jdbcurl"));
            boneCPConfig.setUsername(pps.getProperty("bonecp." + connectionNo + ".username"));
            boneCPConfig.setPassword(pps.getProperty("bonecp." + connectionNo + ".password"));
            boneCPConfig.setPoolName(pps.getProperty("bonecp." + connectionNo + ".poolname"));

            String poolName = DEFAULT_POOLNAME;
            if (!CommonUtility.isNullStr(boneCPConfig.getPoolName())) {
                poolName = boneCPConfig.getPoolName();
            }
            if (dsMap.containsKey(poolName)) {
                ds = dsMap.get(poolName);
            } else {
                ds = new BoneCPDataSource(boneCPConfig);
                ds.setDriverClass(pps.getProperty("bonecp." + connectionNo + ".driverclass"));
                dsMap.put(poolName, ds);
            }
            String jdbcurl = boneCPConfig.getJdbcUrl();
            if (jdbcurl.startsWith("jdbc:sqlserver:")) {
                dbType = DBType.MsSQL;
            } else if (jdbcurl.startsWith("jdbc:oracle:")) {
                dbType = DBType.Oracle;
            } else {
                dbType = DBType.MySQL;
            }
        } else {
            SystemConfig systemConfig = SystemConfig.getInstance();
            if (systemConfig != null) {
                ArrayList<SystemConfig.DataBaseConfig.Connection> connectionList = (ArrayList<SystemConfig.DataBaseConfig.Connection>) systemConfig.getDataBaseConfig().getConnection();
                SystemConfig.DataBaseConfig.Connection connectionConfig = null;
                for (SystemConfig.DataBaseConfig.Connection aConnectionList : connectionList) {
                    int indexno = aConnectionList.getConnectionNo();
                    if (connectionNo == indexno) {
                        connectionConfig = aConnectionList;
                    }
                }
                if (connectionConfig != null) {
                    String poolName = DEFAULT_POOLNAME;
                    if (!CommonUtility.isNullStr(connectionConfig.getPoolName())) {
                        poolName = connectionConfig.getPoolName();
                    }
                    if (dsMap.containsKey(poolName)) {
                        ds = dsMap.get(poolName);
                    } else {
                        Class.forName(connectionConfig.getClassName());
                        Context c = new InitialContext();
                        Context envCtx = (Context) c.lookup("java:comp/env");
                        ds = (BoneCPDataSource) envCtx.lookup(connectionConfig.getResourceName());
                        ds.setPoolName(poolName);
                        dsMap.put(poolName, ds);
                    }
                    try {
                        dbType = DBType.getEnum(connectionConfig.getDbtype());
                    } catch (EnumValueUndefinedException e) {
                        dbType = DBType.MySQL;
                    }
                } else {
                    throw new ConfigException("load database config faild,don't find connectionNo=" + connectionNo);
                }
            } else {
                throw new ClassNotFoundException("SystemConfig load faild!");
            }
        }
    }

    /**
     * 销毁所有连接池
     */
    public static void destroyAllConnections() {
        try {
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            for (Map.Entry<String, BoneCPDataSource> entry : dsMap.entrySet()) {
                entry.getValue().close();
            }
        } catch (Exception e) {
            // again failure, not much you can do
        }
    }

    /**
     * 获取数据库连接资源
     *
     * @return 数据库连接资源
     */
    public BoneCPDataSource getDataSource() {
        return this.ds;
    }

    /**
     * 获取数据库连接
     */
    private void getConnection() throws SQLException {
        if (connection == null) {
            connection = ds.getConnection();
            stmt = connection.createStatement();
            connection.setAutoCommit(true);
        }
    }

    /**
     * 生成数据库操作工厂
     *
     * @return 数据库操作工厂
     */
    private BaseDBFactory produceDBFactory() throws Exception {
        String fname = dbType.getFactoryName();
        String classname = ConnectionFactory.class.getCanonicalName();
        Class<?> cls = Class.forName(classname.substring(0, classname.lastIndexOf(".")) + ".factory." + fname);
        return (BaseDBFactory) cls.newInstance();
    }

    /**
     * 查询结果转换为List
     *
     * @param rs 结果集
     * @return 结果List
     */
    private List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        if (rs == null) {
            return new ArrayList<>();
        }
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> rowData;
        while (rs.next()) {
            rowData = new Hashtable<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                if (CommonUtility.isNullStr(rs.getString(i))) {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), "");
                } else {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), rs.getObject(i));
                }
            }
            list.add(rowData);
        }
        rs.close();
        return list;
    }

    /**
     * 查询结果转换为JSONArray
     *
     * @param rs 结果集
     * @return 结果json对象
     */
    private JSONArray resultSetToJSON(ResultSet rs) throws SQLException {
        if (rs == null) {
            return new JSONArray();
        }
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        JSONArray json = new JSONArray();
        while (rs.next()) {
            JSONObject rowData = new JSONObject();
            for (int i = 1; i <= columnCount; i++) {
                if (CommonUtility.isNullStr(rs.getString(i))) {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), "");
                } else {
                    rowData.put(md.getColumnLabel(i).toUpperCase(), rs.getString(i));
                }
            }
            json.add(rowData);
        }
        rs.close();
        return json;
    }

    /**
     * 查询结果转换为List
     *
     * @param rs  结果集
     * @param cls 类
     * @param obj 实例对象
     * @return 结果List
     */
    private List<DBTable> resultSetToObjList(ResultSet rs, Class<? extends DBTable> cls, DBTable obj) throws SQLException, InstantiationException, IllegalAccessException, IOException {
        if (rs == null) {
            return new ArrayList<>();
        }
        if (cls == null) {
            return new ArrayList<>();
        }
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        ArrayList<DBTable> list = new ArrayList<>();
        while (rs.next()) {
            DBTable rowData = cls.newInstance();
            for (int i = 1; i <= columnCount; i++) {
                String colName = md.getColumnLabel(i).toUpperCase();
                Class<?> clas = cls;
                while (clas != null && clas.isAnnotationPresent(ADBTable.class)) {
                    Field[] fields = clas.getDeclaredFields();
                    boolean isFind = false;
                    for (Field field : fields) {
                        field.setAccessible(true);
                        String fieldName;
                        DBTableFieldType fieldType;
                        if (field.isAnnotationPresent(ADBTablePrimaryKey.class)) {
                            ADBTablePrimaryKey aPKey = field.getAnnotation(ADBTablePrimaryKey.class);
                            fieldName = aPKey.name().toUpperCase();
                            DBTablePrimaryKeyType pKeyType = aPKey.pKeyType();
                            if (pKeyType.equals(DBTablePrimaryKeyType.Number)) {
                                fieldType = DBTableFieldType.Integer;
                            } else {
                                fieldType = DBTableFieldType.String;
                            }
                        } else if (field.isAnnotationPresent(ADBTableField.class)) {
                            ADBTableField aField = field.getAnnotation(ADBTableField.class);
                            fieldName = aField.name().toUpperCase();
                            fieldType = aField.fieldType();
                        } else {
                            continue;
                        }
                        if (colName.equals(fieldName)) {
                            if (DBTableFieldType.Blob.equals(fieldType)) {
                                Blob lob = rs.getBlob(i);
                                if (lob != null) {
                                    field.set(rowData, lob.getBinaryStream());
                                }
                            } else if (DBTableFieldType.Clob.equals(fieldType)) {
                                Clob lob = rs.getClob(i);
                                field.set(rowData, clobToStr(lob));
                            } else {
                                field.set(rowData, rs.getObject(i));
                            }
                            isFind = true;
                            break;
                        }
                    }
                    if (isFind) {
                        clas = null;
                    } else {
                        clas = clas.getSuperclass();
                    }
                }
            }
            if (obj != null) {
                rowData.setPrefix(obj.getPrefix());
                rowData.setSuffix(obj.getSuffix());
            }
            list.add(rowData);
        }
        rs.close();
        return list;
    }

    /**
     * 查询结果转换为List
     *
     * @param rs  结果集
     * @param cls 自定义类
     * @return 结果List
     */
    private <T> List<T> resultSetToCusObjList(ResultSet rs, Class<T> cls) throws SQLException, InstantiationException, IllegalAccessException, IOException {
        if (rs == null) {
            return new ArrayList<>();
        }
        ArrayList<T> list = new ArrayList<>();
        while (rs.next()) {
            list.add(rowToCusObjList(rs, cls));
        }
        rs.close();
        return list;
    }

    /**
     * 结果转为自定义对象
     *
     * @param rs   数据集
     * @param clas 自定义类
     * @return 自定义对象实例
     */
    private <T> T rowToCusObjList(ResultSet rs, Class<T> clas) throws SQLException, IllegalAccessException, InstantiationException, IOException {
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        Field[] fields = clas.getDeclaredFields();
        T rowData = clas.newInstance();
        for (int i = 1; i <= columnCount; i++) {
            String colName = md.getColumnLabel(i).toUpperCase();
            for (Field field : fields) {
                field.setAccessible(true);
                if (colName.equals(field.getName().toUpperCase())) {
                    switch (md.getColumnType(i)) {
                        case Types.BLOB:
                            Blob blob = rs.getBlob(i);
                            if (blob != null) {
                                field.set(rowData, blob.getBinaryStream());
                            }
                            break;
                        case Types.CLOB:
                            Clob clob = rs.getClob(i);
                            field.set(rowData, clobToStr(clob));
                            break;
                        default:
                            field.set(rowData, rs.getObject(i));
                    }
                }
            }
        }
        return rowData;
    }

    /**
     * CLOB转为字符串
     *
     * @param clob clob对象
     * @return 字符串
     */
    private String clobToStr(Clob clob) throws IOException, SQLException {
        if (clob != null) {
            BufferedReader br = new BufferedReader(clob.getCharacterStream());
            StringBuilder buffer = new StringBuilder();
            String str = br.readLine();
            while (!CommonUtility.isNullStr(str)) {
                buffer.append(str);
                str = br.readLine();
            }
            return buffer.toString();
        } else {
            return null;
        }
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return 结果集
     */
    private ResultSet doQueryBase(String sql) throws SQLException {
        getConnection();
        stmt.clearBatch();
        log.debug("sql=" + sql);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        return pstmt.executeQuery();
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果集
     */
    private ResultSet doQueryBase(String sql, Object[] param) throws SQLException {
        getConnection();
        stmt.clearBatch();
        log.debug("sql=" + sql);
        PreparedStatement pstmt = connection.prepareStatement(sql);
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                pstmt.setObject(i + 1, param[i]);
            }
        }
        return pstmt.executeQuery();
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    private Object[] doQueryBase(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) throws Exception {
        getConnection();
        stmt.clearBatch();
        Object[] result = new Object[3];
        StringBuilder searchNumberStr = new StringBuilder("");
        if (sqlArray != null && sqlArray.length == 4) {
            searchNumberStr.append("select count(").append(pKey).append(") as CNM from ").append(sqlArray[1]).append(" ").append(sqlArray[2]);
        } else {
            log.error("query Exception: sqlArray is null or length is not 4");
            result[0] = 1;
            result[1] = 0;
            result[2] = null;
            return result;
        }
        log.debug("sql=" + searchNumberStr.toString());
        PreparedStatement pstmt = connection.prepareStatement(searchNumberStr.toString());
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                pstmt.setObject(i + 1, param[i]);
            }
        }
        ResultSet rs_t = pstmt.executeQuery();
        rs_t.next();
        long totlerecord = rs_t.getInt("CNM");
        long totlepage;
        if (currPage <= 0 || maxCount <= 0) {
            totlepage = 1;
            currPage = 1;
            maxCount = totlerecord;
        } else {
            totlepage = totlerecord / maxCount;
            if (totlerecord % maxCount != 0) {
                totlepage++;
            }
        }
        rs_t.close();
        BaseDBFactory dbFacroty = produceDBFactory();
        result[0] = totlepage;
        result[1] = totlerecord;
        result[2] = dbFacroty.doQueryPage(connection, param, currPage, maxCount, pKey, sqlArray, totlerecord);
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return 结果集
     */
    public ResultSet doQueryForSet(String sql) {
        ResultSet result;
        try {
            result = doQueryBase(sql);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果集
     */
    public ResultSet doQueryForSet(String sql, Object[] param) {
        ResultSet result;
        try {
            result = doQueryBase(sql, param);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] doQueryForSet(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return doQueryForSet(null, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ResultSet结果集
     */
    public Object[] doQueryForSet(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        Object[] result = new Object[3];
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result[0] = tmpr[0];
            result[1] = tmpr[1];
            result[2] = tmpr[2];
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result[0] = 1;
            result[1] = 0;
            result[2] = null;
        }
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return 结果List
     */
    public List<Map<String, Object>> doQueryForList(String sql) {
        List<Map<String, Object>> result;
        try {
            ResultSet rs = doQueryBase(sql);
            result = resultSetToList(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 结果List
     */
    public List<Map<String, Object>> doQueryForList(String sql, Object[] param) {
        List<Map<String, Object>> result;
        try {
            ResultSet rs = doQueryBase(sql, param);
            result = resultSetToList(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] doQueryForList(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return doQueryForList(null, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] doQueryForList(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        Object[] result = new Object[3];
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result[0] = tmpr[0];
            result[1] = tmpr[1];
            result[2] = resultSetToList((ResultSet) tmpr[2]);
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result[0] = 1;
            result[1] = 0;
            result[2] = new ArrayList<Map<String, Object>>();
        }
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @return json对象
     */
    public JSONArray doQueryForJSON(String sql) {
        JSONArray result;
        try {
            ResultSet rs = doQueryBase(sql);
            result = resultSetToJSON(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new JSONArray();
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @return json对象
     */
    public JSONArray doQueryForJSON(String sql, Object[] param) {
        JSONArray result;
        try {
            ResultSet rs = doQueryBase(sql, param);
            result = resultSetToJSON(rs);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception, sql:" + sql);
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new JSONArray();
        }
    }

    /**
     * 查询
     *
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:int-总页数,records:int-总记录数,datas:array-结果集}
     */
    public JSONObject doQueryForJSON(long currPage, long maxCount, String pKey, String[] sqlArray) {
        return doQueryForJSON(null, currPage, maxCount, pKey, sqlArray);
    }

    /**
     * 查询
     *
     * @param param    参数
     * @param currPage 查询的当前页码
     * @param maxCount 每页最大记录数
     * @param pKey     主键名称
     * @param sqlArray [0]-字段名，[1]-表名，[2]-where条件（where 开头），[3]-附加条件（例如：order by）
     * @return JSONObject:{pages:int-总页数,records:int-总记录数,datas:array-结果集}
     */
    public JSONObject doQueryForJSON(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray) {
        JSONObject result = new JSONObject();
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result.put("pages", tmpr[0]);
            result.put("records", tmpr[0]);
            result.put("datas", resultSetToJSON((ResultSet) tmpr[2]));
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result.put("pages", 1);
            result.put("records", 0);
            result.put("datas", new JSONArray());
        }
        return result;
    }

    /**
     * 查询
     *
     * @param whereValues 查询条件，key=java类名.字段名
     * @param cls         类
     * @param obj         实例对象
     * @param attachStr   附加语句（例如：order by ${java类名.字段名}）
     * @return 结果List
     */
    public List<DBTable> doQueryForObjList(Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) {
        try {
            Object[] attr = DBTableFactory.buildSelectStr(whereValues, cls, obj, attachStr);
            ResultSet rs = doQueryBase(String.valueOf(attr[0]), (Object[]) attr[1]);
            List<DBTable> result = resultSetToObjList(rs, cls, obj);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
    }

    /**
     * 查询
     *
     * @param currPage    查询的当前页码
     * @param maxCount    每页最大记录数
     * @param whereValues 查询条件，key=java类名.字段名
     * @param cls         类
     * @param obj         实例对象
     * @param attachStr   附加语句（例如：order by ${java类名.字段名}）
     * @return Object[]:[0]-long总页数,[1]-long总记录数,[2]-ArrayList结果集
     */
    public Object[] doQueryForObjList(long currPage, long maxCount, Map<String, Object> whereValues, Class<? extends DBTable> cls, DBTable obj, String attachStr) {
        Object[] result = new Object[3];
        try {
            Object[] attr = DBTableFactory.buildSelectParam(whereValues, cls, obj, attachStr);
            String[] sqlArray = new String[4];
            sqlArray[0] = String.valueOf(attr[0]);
            sqlArray[1] = String.valueOf(attr[1]);
            sqlArray[2] = String.valueOf(attr[2]);
            sqlArray[3] = String.valueOf(attr[5]);
            Object[] rs = doQueryBase((Object[]) attr[4], currPage, maxCount, String.valueOf(attr[3]), sqlArray);
            result[0] = rs[0];
            result[1] = rs[1];
            result[2] = resultSetToObjList((ResultSet) rs[2], cls, obj);
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result[0] = 1;
            result[1] = 0;
            result[2] = new ArrayList<DBTable>();
        }
        return result;
    }

    /**
     * 查询
     *
     * @param sql sql语句
     * @param cls 类
     * @return 结果List
     */
    public <T> List<T> doQueryForCusObjList(String sql, Class<T> cls) {
        List<T> result;
        try {
            ResultSet rs = doQueryBase(sql);
            result = resultSetToCusObjList(rs, cls);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
    }

    /**
     * 查询
     *
     * @param sql   sql语句
     * @param param 参数
     * @param cls   类
     * @return 结果List
     */
    public <T> List<T> doQueryForCusObjList(String sql, Object[] param, Class<T> cls) {
        List<T> result;
        try {
            ResultSet rs = doQueryBase(sql, param);
            result = resultSetToCusObjList(rs, cls);
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return new ArrayList<>();
        }
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
    public Object[] doQueryForCusObjList(long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        return doQueryForCusObjList(null, currPage, maxCount, pKey, sqlArray, cls);
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
    public Object[] doQueryForCusObjList(Object[] param, long currPage, long maxCount, String pKey, String[] sqlArray, Class<?> cls) {
        Object[] result = new Object[3];
        try {
            Object[] tmpr = doQueryBase(param, currPage, maxCount, pKey, sqlArray);
            result[0] = tmpr[0];
            result[1] = tmpr[1];
            result[2] = resultSetToCusObjList((ResultSet) tmpr[2], cls);
            if (connection.getAutoCommit()) {
                this.close();
            }
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            result[0] = 1;
            result[1] = 0;
            result[2] = new ArrayList<>();
        }
        return result;
    }

    /**
     * 查询LOB数据
     *
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @return 输入流
     */
    public InputStream doQueryLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        try {
            InputStream result = null;
            Object[] where = BaseDBFactory.buildWhere(whereValues);
            String sql = "select " + lobColName.toUpperCase() + " from " + tableName + where[0];
            Object[] param = (Object[]) where[1];
            ResultSet rs;
            if (param.length > 0) {
                rs = doQueryBase(sql, param);
            } else {
                rs = doQueryBase(sql, null);
            }
            if (rs.next()) {
                Blob lob = rs.getBlob(1);
                if (lob != null) {
                    result = lob.getBinaryStream();
                } else {
                    result = null;
                }
            }
            rs.close();
            if (connection.getAutoCommit()) {
                this.close();
            }
            return result;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 查询CLOB数据
     *
     * @param tableName   表名
     * @param whereValues 字段名称=>字段值
     * @param lobColName  lob字段名称
     * @return 结果字符串
     */
    public String doQueryCLOB(String tableName, Map<String, Object> whereValues, String lobColName) {
        try {
            Object[] where = BaseDBFactory.buildWhere(whereValues);
            String sql = "select " + lobColName.toUpperCase() + " from " + tableName + where[0];
            Object[] param = (Object[]) where[1];
            ResultSet rs;
            if (param.length > 0) {
                rs = doQueryBase(sql, param);
            } else {
                rs = doQueryBase(sql, null);
            }
            Reader result = null;
            if (rs.next()) {
                Clob lob = rs.getClob(1);
                if (lob != null) {
                    result = lob.getCharacterStream();
                } else {
                    result = null;
                }
            }
            rs.close();
            String ret = null;
            if (result != null) {
                BufferedReader br = new BufferedReader(result);
                StringBuilder buffer = new StringBuilder();
                String str = br.readLine();
                while (!CommonUtility.isNullStr(str)) {
                    buffer.append(str);
                    str = br.readLine();
                }
                ret = buffer.toString();
            }
            if (connection.getAutoCommit()) {
                this.close();
            }
            return ret;
        } catch (Exception e) {
            log.error("query Exception:" + e.getMessage(), e);
            this.close();
            return null;
        }
    }

    /**
     * 执行SQL
     *
     * @param sql sql语句
     * @return 成功或失败
     */
    public boolean doUpdate(String sql) {
        try {
            getConnection();
            stmt.clearBatch();
            log.debug("sql=" + sql);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.executeUpdate();
            if (connection.getAutoCommit()) {
                this.close();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception, sql:" + sql);
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
    }

    /**
     * 执行SQL
     *
     * @param sql   sql语句
     * @param param 参数
     * @return 成功或失败
     */
    public boolean doUpdate(String sql, Object[] param) {
        try {
            getConnection();
            stmt.clearBatch();
            log.debug("sql=" + sql);
            PreparedStatement pstmt = connection.prepareStatement(sql);
            if (param != null) {
                for (int i = 0; i < param.length; i++) {
                    pstmt.setObject(i + 1, param[i]);
                }
            }
            pstmt.executeUpdate();
            if (connection.getAutoCommit()) {
                this.close();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception, sql:" + sql);
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
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
        try {
            getConnection();
            stmt.clearBatch();
            boolean commitable = false;
            if (connection.getAutoCommit()) {
                beginTranslist();
                commitable = true;
            }
            BaseDBFactory dbFacroty = produceDBFactory();
            dbFacroty.doInsertLOB(connection, tableName, whereValues, lobColName, input);
            if (commitable) {
                commitTranslist();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
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
        try {
            getConnection();
            stmt.clearBatch();
            boolean commitable = false;
            if (connection.getAutoCommit()) {
                beginTranslist();
                commitable = true;
            }
            BaseDBFactory dbFacroty = produceDBFactory();
            dbFacroty.doUpdateLOB(connection, tableName, whereValues, lobColName, input);
            if (commitable) {
                commitTranslist();
            }
            return true;
        } catch (Exception e) {
            log.error("excute Exception:" + e.getMessage(), e);
            this.close();
            return false;
        }
    }

    /**
     * 开始事务
     */
    public void beginTranslist() {
        try {
            getConnection();
            connection.setAutoCommit(false);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.close();
        }
    }

    /**
     * 提交事务
     */
    public void commitTranslist() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            this.close();
        }
    }

    /**
     * 回滚事务
     */
    public void rollBackTranslist() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            this.close();
        }
    }

    /**
     * 添加批量SQL语句
     *
     * @param sql sql语句
     */
    public void addBatch(String sql) {
        try {
            getConnection();
            stmt.addBatch(sql);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.close();
        }
    }

    /**
     * 批量执行SQL
     *
     * @return 成功或失败
     */
    public boolean excuteBatch() {
        try {
            getConnection();
            this.beginTranslist();
            stmt.executeBatch();
            stmt.clearBatch();
            this.commitTranslist();
            return true;
        } catch (Exception e) {
            this.rollBackTranslist();
            log.error("excute Exception:" + e.getMessage(), e);
            return false;
        } finally {
            this.close();
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (stmt != null && !stmt.isClosed()) {
                stmt.close();
            }
            if (connection != null && !connection.isClosed()) {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                connection.close();
            }
            stmt = null;
            connection = null;
        } catch (Exception e) {
            log.error("close database connection Exception:", e);
        }
    }

    /**
     * 获取当前数据库名称
     *
     * @return 数据库名称
     */
    public String getDbName() {
        String url = ds.getJdbcUrl();
        String dbName = "";
        switch (dbType) {
            case MySQL:
                dbName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));
                break;
            case MsSQL:
                String[] subs = url.split(";");
                for (String sub : subs) {
                    if (sub.toUpperCase().contains("databaseName=".toUpperCase())) {
                        dbName = sub.substring(13);
                    }
                }
                break;
            case Oracle:
                dbName = url.substring(url.lastIndexOf(":") + 1);
                break;
        }
        return dbName;
    }

    /**
     * 获取数据库链接类型
     *
     * @return 链接类型
     */
    public DBType getDbType() {
        return dbType;
    }

    /**
     * 获取数据源编号
     *
     * @return 数据源编号
     */
    public int getDbNo() {
        return connectionNo;
    }

    /**
     * 获取数据库驱动类型
     *
     * @return 驱动类型
     */
    public String getDbDriverType() {
        try {
            getConnection();
            String dbDriver = connection.getMetaData().getDriverName().toLowerCase();
            this.close();
            return dbDriver;
        } catch (Exception e) {
            log.error("getDbDriverType Exception:" + e.getMessage(), e);
            this.close();
            return "";
        }
    }

    /**
     * 是否自动提交事务
     *
     * @return 是否自动提交
     */
    public boolean isAutoCommit() {
        try {
            return connection == null || connection.getAutoCommit();
        } catch (SQLException e) {
            log.error("isAutoCommit Exception:" + e.getMessage(), e);
            return true;
        }
    }

    public static int getDefaultConnectionNo() {
        return DEFAULT_CONNECTION_NO;
    }

    public static void setDefaultConnectionNo(int defaultConnectionNo) {
        DEFAULT_CONNECTION_NO = defaultConnectionNo;
    }

}
