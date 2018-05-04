package pers.acp.tools.utility;

import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.exceptions.ConfigException;
import pers.acp.tools.task.threadpool.ThreadPoolService;
import pers.acp.tools.task.threadpool.basetask.BaseThreadTask;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhangbin on 2016/9/9.
 * 工具类
 */
public class CommonUtility {

    private static Logger log = Logger.getLogger(CommonUtility.class);

    /**
     * 判断是否空字符串
     *
     * @param src 源字符串
     * @return 是否为空
     */
    public static boolean isNullStr(String src) {
        return src == null || src.isEmpty() || src.trim().equals("");
    }

    /**
     * 获取36位全球唯一的字符串（带4个分隔符）
     *
     * @return 结果
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    /**
     * 获取32位全球唯一的字符串
     *
     * @return 结果
     */
    public static String getUuid32() {
        return getUuid().replace("-", "");
    }

    /**
     * 获取16位短uuid
     *
     * @return 结果
     */
    public static String getUuid16() {
        return getUuid(16);
    }

    /**
     * 获取8位短uuid
     *
     * @return 结果
     */
    public static String getUuid8() {
        return getUuid(8);
    }

    /**
     * 获取uuid
     *
     * @param length 字符串长度（必须能整除32）
     * @return 结果
     */
    private static String getUuid(int length) {
        String[] chars = new String[]{"a", "b", "c", "d", "e", "f", "g", "h",
                "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
                "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H",
                "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y", "Z"};
        StringBuilder shortBuffer = new StringBuilder();
        String uuid = getUuid32();
        for (int i = 0; i < length; i++) {
            String str = uuid.substring(i * (32 / length), i * (32 / length) + (32 / length));
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
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
        String result = src;
        int maxlength = src.length();
        for (int i = 0; i < maxlength / length; i++) {
            result = result.substring(0, (i + 1) * length + (i * insertString.length())) + insertString + result.substring((i + 1) * length + (i * insertString.length()));
        }
        if (result.lastIndexOf(insertString) == result.length() - insertString.length()) {
            result = result.substring(0, result.length() - insertString.length());
        }
        return result;
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
        return strFillIn(str, number, flag, c + "");
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
        String result = str;
        int charlength = getBytesLength(string);
        int tmpnum = getBytesLength(result);
        if (charlength == 1) {
            for (; tmpnum < number; tmpnum++) {
                if (flag == 0) {
                    result = string + result;
                } else {
                    result = result + string;
                }
            }
        } else {
            while (tmpnum < number) {
                if (flag == 0) {
                    result = string + result;
                } else {
                    result = result + string;
                }
                tmpnum += charlength;
            }
        }
        return result;
    }

    /**
     * 获取字符串的字节长度
     *
     * @param str 待处理的字符串
     * @return 字节长度
     */
    public static int getBytesLength(String str) {
        int len = 0;
        for (int i = 0; i < str.length(); i++) {
            char iCode = str.charAt(i);
            if (iCode <= 255 || iCode >= 0xff61 && iCode <= 0xff9f) {
                len += 1;
            } else {
                len += 2;
            }
        }
        return len;
    }

    /**
     * 判断字符串是否在数组中
     *
     * @param str        源字符串
     * @param array      字符串数组
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    public static boolean strInArray(String str, String[] array, boolean ignoreCase) {
        if (array != null) {
            for (String anArray : array) {
                if (ignoreCase) {
                    if (str.toUpperCase().equals(anArray.toUpperCase())) {
                        return true;
                    }
                } else {
                    if (str.equals(anArray)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断字符串是否在列表中
     *
     * @param str        源字符串
     * @param arrayList  字符串列表
     * @param ignoreCase 是否忽略大小写
     * @return 是否存在
     */
    public static boolean strInList(String str, List<String> arrayList, boolean ignoreCase) {
        if (arrayList != null) {
            long count;
            if (ignoreCase) {
                count = arrayList.stream().filter(anArrayList -> anArrayList.toUpperCase().equals(str.toUpperCase())).count();
            } else {
                count = arrayList.stream().filter(anArrayList -> anArrayList.equals(str)).count();
            }
            return count > 0;
        }
        return false;
    }

    /**
     * 获取指定格式的时间字符串
     *
     * @param date       Date实例
     * @param dateFormat 格式
     * @return 格式化的时间格式
     */
    public static String getDateTimeString(Date date, String dateFormat) {
        if (date == null) {
            date = new Date(); // 当前时间
        }
        if (isNullStr(dateFormat)) {
            dateFormat = "yyyy-MM-dd HH:mm:ss"; // 默认时间格式化模式
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取当前日期
     *
     * @return 日期字符串
     */
    public static String getNowString() {
        return getDateTimeString(null, "yyyy-MM-dd");
    }

    /**
     * 获取当前时间
     *
     * @return 日期时间字符串
     */
    public static String getNowTimeString() {
        return getDateTimeString(null, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 十六进制转字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public static String byte2hex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        String tmp;
        for (byte aB : b) {
            tmp = Integer.toHexString(aB & 0XFF);
            if (tmp.length() == 1) {
                sb.append("0").append(tmp);
            } else {
                sb.append(tmp);
            }

        }
        return sb.toString();
    }

    /**
     * 字符串转十六进制
     *
     * @param str 字符串
     * @return 字节数组
     */
    public static byte[] hex2byte(String str) {
        if (isNullStr(str)) {
            return new byte[0];
        }
        str = str.trim();
        int len = str.length();

        if (len == 0 || len % 2 == 1) {
            return new byte[0];
        }
        byte[] b = new byte[len / 2];
        try {
            for (int i = 0; i < str.length(); i += 2) {
                b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
            }
            return b;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * 获取线程池实例
     *
     * @return 线程池实例
     */
    public static ThreadPoolService getThreadPool() {
        SystemConfig systemConfig = null;
        try {
            systemConfig = SystemConfig.getInstance();
        } catch (ConfigException e) {
            log.error(e.getMessage(), e);
        }
        if (systemConfig == null) {
            return null;
        } else {
            if (systemConfig.getThreadPool().isEnabled()) {
                return ThreadPoolService.getInstance(systemConfig.getThreadPool().getSpacingTime(), systemConfig.getThreadPool().getMaxThreadNumber());
            } else {
                return null;
            }
        }
    }

    /**
     * 在线程池中执行任务
     *
     * @param task 线程池任务
     * @return 执行结果
     */
    public static Object excuteTaskInThreadPool(BaseThreadTask task) {
        ThreadPoolService threadPool = getThreadPool();
        return excuteTaskInThreadPool(threadPool, task);
    }

    /**
     * 在线程池中执行任务
     *
     * @param threadPool 线程池实例
     * @param task       线程池任务
     * @return 执行结果
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    public static Object excuteTaskInThreadPool(ThreadPoolService threadPool, BaseThreadTask task) {
        if (task != null && threadPool != null) {
            threadPool.addTask(task);
            try {
                synchronized (task) {
                    task.wait();
                    if (task.getTaskResult() != null) {
                        log.debug("excute task int threadpool [" + threadPool.getPoolName() + "] success:" + task.getTaskResult());
                        return task.getTaskResult();
                    } else {
                        log.debug("excute task int threadpool [" + threadPool.getPoolName() + "] success");
                        return null;
                    }
                }
            } catch (Exception e) {
                log.error("excute task int threadpool [" + threadPool.getPoolName() + "] faild:" + e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }
    }

}
