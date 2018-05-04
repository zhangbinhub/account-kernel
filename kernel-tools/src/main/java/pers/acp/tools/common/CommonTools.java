package pers.acp.tools.common;

import pers.acp.tools.common.model.RunTimeConfig;
import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.exceptions.OperateException;
import pers.acp.tools.file.FileTools;
import pers.acp.tools.file.common.FileCommon;
import pers.acp.tools.match.MoneyToCN;
import pers.acp.tools.match.Operate;
import pers.acp.tools.security.key.KeyManagement;
import pers.acp.tools.security.key.enums.StorageMode;
import pers.acp.tools.task.threadpool.ThreadPoolService;
import pers.acp.tools.task.threadpool.basetask.BaseThreadTask;
import pers.acp.tools.utility.CommonUtility;
import pers.acp.tools.utility.JSONUtility;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class CommonTools {

    private static Logger log = Logger.getLogger(CommonTools.class);

    /**
     * 初始化工具类
     */
    public static void InitTools() throws Exception {
        log.info("Tools init begin...");
        FileTools.InitTools();
        DBConTools.initTools();
        CommonUtility.getThreadPool();
        SystemConfig.Security seConf = SystemConfig.getInstance().getSecurity();
        StorageMode storageMode = StorageMode.getEnum(seConf.getStorageMode());
        switch (storageMode) {
            case Memory:
                KeyManagement.initParams(storageMode);
                break;
            case File:
                KeyManagement.initParams(storageMode, seConf.getStorageParam());
                break;
            case DataBase:
                KeyManagement.initParams(storageMode, seConf.getStorageParam(), seConf.getTablename(), seConf.getKeyCol(), seConf.getObjCol());
                break;
        }
        log.info("Tools init finished!");
    }

    /**
     * 获取系统默认字符集
     *
     * @return 字符集
     */
    public static String getDefaultCharset() {
        return FileCommon.getDefaultCharset();
    }

    /**
     * 获取36位全球唯一的字符串（带4个分隔符）
     *
     * @return 结果
     */
    public static String getUuid() {
        return CommonUtility.getUuid();
    }

    /**
     * 获取32位全球唯一的字符串
     *
     * @return 结果
     */
    public static String getUuid32() {
        return CommonUtility.getUuid32();
    }

    /**
     * 获取16位短uuid
     *
     * @return 结果
     */
    public static String getUuid16() {
        return CommonUtility.getUuid16();
    }

    /**
     * 获取8位短uuid
     *
     * @return 结果
     */
    public static String getUuid8() {
        return CommonUtility.getUuid8();
    }

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    public static String getRandomString(long length) {
        return KeyManagement.getRandomString(KeyManagement.RANDOM_STR, length);
    }

    /**
     * 判断是否空字符串
     *
     * @param src 源字符串
     * @return 是否为空
     */
    public static boolean isNullStr(String src) {
        return CommonUtility.isNullStr(src);
    }

    /**
     * 源字符串中每到指定长度时就插入子字符串
     *
     * @param src          源字符串
     * @param length       分隔长度
     * @param insertString 插入的子字符串
     * @return 目标字符串
     */
    public static String autoInsertString(String src, int length, String insertString) {
        return CommonUtility.autoInsertString(src, length, insertString);
    }

    /**
     * 字符串填充函数
     *
     * @param str    待填充的字符串
     * @param number 填充后的字节长度
     * @param flag   0-向左填充，1-向右填充
     * @param c      填充字符
     * @return 填充后的字符串
     */
    public static String strFillIn(String str, int number, int flag, char c) {
        return CommonUtility.strFillIn(str, number, flag, c);
    }

    /**
     * 字符串填充函数
     *
     * @param str    待填充的字符串
     * @param number 填充后的字节长度
     * @param flag   0-向左填充，1-向右填充
     * @param string 填充字符串
     * @return 填充后的字符串
     */
    public static String strFillIn(String str, int number, int flag, String string) {
        return CommonUtility.strFillIn(str, number, flag, string);
    }

    /**
     * 获取字符串的字节长度
     *
     * @param str 待处理的字符串
     * @return 字节长度
     */
    public static int getBytesLength(String str) {
        return CommonUtility.getBytesLength(str);
    }

    /**
     * 判断字符串是否在数组中
     *
     * @param str   源字符串
     * @param array 字符串数组
     * @return 是否存在
     */
    public static boolean strInArray(String str, String[] array) {
        return CommonUtility.strInArray(str, array, false);
    }

    /**
     * 判断字符串是否在列表中
     *
     * @param str       源字符串
     * @param arrayList 字符串列表
     * @return 是否存在
     */
    public static boolean strInList(String str, List<String> arrayList) {
        return CommonUtility.strInList(str, arrayList, false);
    }

    /**
     * 获取指定格式的时间字符串
     *
     * @param date       Date实例
     * @param dateFormat 格式
     * @return 格式化的时间格式
     */
    public static String getDateTimeString(Date date, String dateFormat) {
        return CommonUtility.getDateTimeString(date, dateFormat);
    }

    /**
     * 获取当前日期
     *
     * @return 日期字符串
     */
    public static String getNowString() {
        return CommonUtility.getNowString();
    }

    /**
     * 获取当前时间
     *
     * @return 日期时间字符串
     */
    public static String getNowTimeString() {
        return CommonUtility.getNowTimeString();
    }

    /**
     * 计算四则运算表达式结果
     *
     * @param caculateStr 表达式字符串
     * @return 结果
     */
    public static double doCaculate(String caculateStr) throws OperateException {
        Operate operate = new Operate();
        return operate.caculate(caculateStr);
    }

    /**
     * 金额转换为汉语中人民币的大写
     *
     * @param money 金额
     * @return 大写字符串
     */
    public static String getMoneyForCN(double money) {
        return MoneyToCN.moneyToCNMontrayUnit(money);
    }

    /**
     * 在线程池中执行任务
     *
     * @param task 线程池任务
     * @return 执行结果
     */
    public static Object excuteTaskInThreadPool(BaseThreadTask task) {
        return CommonUtility.excuteTaskInThreadPool(task);
    }

    /**
     * 在线程池中执行任务
     *
     * @param threadPool 线程池实例
     * @param task       线程池任务
     * @return 执行结果
     */
    public static Object excuteTaskInThreadPool(ThreadPoolService threadPool, BaseThreadTask task) {
        return CommonUtility.excuteTaskInThreadPool(threadPool, task);
    }

    /**
     * 字符串转JSON对象
     *
     * @param src 字符串
     * @return json对象
     */
    public static JSONObject getJsonObjectFromStr(String src) {
        return JSONUtility.getJsonObjectFromStr(src);
    }

    /**
     * 字符串转JSON数组
     *
     * @param src 字符串
     * @return json对象
     */
    public static JSONArray getJsonArrayFromStr(String src) {
        return JSONUtility.getJsonArrayFromStr(src);
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj json对象（JSONObject）
     * @param cls     目标类（不支持Map）
     * @return 目标对象（实体对象或List）
     */
    public static <T> T jsonToBean(JSONObject jsonObj, Class<T> cls) {
        return JSONUtility.jsonToBean(jsonObj, cls);
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj json对象（JSONObject）
     * @param cls     目标类
     * @param obj     目标实体对象（只持Map对象）
     * @return 目标对象（实体对象或List）
     */
    public static <T> T jsonToBean(JSONObject jsonObj, Class<?> cls, T obj) {
        return JSONUtility.jsonToBean(jsonObj, cls, obj);
    }

    /**
     * json对象转为java对象
     *
     * @param jsonObj   json对象（JSONArray）
     * @param itemClass List中元素的类
     * @return 目标对象（实体对象或List）
     */
    public static <T> List<T> jsonToList(JSONArray jsonObj, Class<T> itemClass) {
        return JSONUtility.jsonToList(jsonObj, itemClass);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @return json对象
     */
    public static JSONObject beanToJson(Object instance) {
        return JSONUtility.beanToJson(instance);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param cls      实体类（只持Map）
     * @param instance 实体对象（只持Map对象）
     * @return json对象
     */
    public static JSONObject beanToJson(Class<?> cls, Object instance) {
        return JSONUtility.beanToJson(cls, instance);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @param excludes 排除的实体成员
     * @return json对象
     */
    public static JSONObject beanToJson(Object instance, String[] excludes) {
        return JSONUtility.beanToJson(instance, excludes);
    }

    /**
     * 实体对象转换为json对象
     *
     * @param instance 实体对象（只持Map对象）
     * @param excludes 排除的实体成员
     * @return json对象
     */
    public static JSONObject beanToJson(Class<?> cls, Object instance, String[] excludes) {
        return JSONUtility.beanToJson(cls, instance, excludes);
    }

    /**
     * List转换为json数组
     *
     * @param list List对象
     * @return json数组
     */
    public static JSONArray listToJson(List list) {
        return JSONUtility.listToJson(list);
    }

    /**
     * List转换为json数组
     *
     * @param list     List对象
     * @param excludes 排除的实体成员
     * @return json数组
     */
    public static JSONArray listToJson(List list, String[] excludes) {
        return JSONUtility.listToJson(list, excludes);
    }

    /**
     * 十六进制转字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public static String byte2hex(byte[] b) {
        return CommonUtility.byte2hex(b);
    }

    /**
     * 字符串转十六进制
     *
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] hex2byte(String str) {
        return CommonUtility.hex2byte(str);
    }

    /**
     * 获取数据库表T_RuntimeConfig中系统参数值
     *
     * @param SysParamName 系统参数名称
     * @return 参数值
     */
    public static String getSysParamValue(String SysParamName) {
        Map<String, Object> param = new HashMap<>();
        param.put(RunTimeConfig.class.getCanonicalName() + ".confname", SysParamName);
        param.put(RunTimeConfig.class.getCanonicalName() + ".status", 1);
        RunTimeConfig runTimeConfig = (RunTimeConfig) RunTimeConfig.getInstance(param, RunTimeConfig.class, null);
        if (runTimeConfig != null) {
            return runTimeConfig.getConfvalue();
        } else {
            return "";
        }
    }

    /**
     * 判断数据库表T_RuntimeConfig中系统参数是否可用
     *
     * @param SysParamName 系统参数名称
     * @return 参数是否可用
     */
    public static boolean getSysParamIsEnabled(String SysParamName) {
        Map<String, Object> param = new HashMap<>();
        param.put(RunTimeConfig.class.getCanonicalName() + ".confname", SysParamName);
        RunTimeConfig runTimeConfig = (RunTimeConfig) RunTimeConfig.getInstance(param, RunTimeConfig.class, null);
        return runTimeConfig != null && runTimeConfig.getStatus() == 1;
    }

}
