package pers.acp.tools.task.timer.container;

import pers.acp.tools.exceptions.TimerException;
import pers.acp.tools.task.timer.ruletype.CircleType;
import pers.acp.tools.utility.CommonUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日历计算类
 *
 * @author zhangbin
 */
public final class Calculation {

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    static SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * yyyy-MM-dd
     */
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * HH:mm:ss
     */
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    /**
     * 获取日历对象
     *
     * @return 日历对象
     */
    public static Calendar getCalendar() throws TimerException {
        return getCalendar(null);
    }

    /**
     * 获取日历对象
     *
     * @param dateStr 日期字符串（yyyy-MM-dd）
     * @return 日历对象
     */
    public static Calendar getCalendar(String dateStr) throws TimerException {
        try {
            Calendar calendar = Calendar.getInstance();
            if (!CommonUtility.isNullStr(dateStr)) {
                Date date = DATE_FORMAT.parse(dateStr);
                calendar.setTime(date);
            }
            return calendar;
        } catch (Exception e) {
            throw new TimerException("date string is need format for 'yyyy-MM-dd'");
        }
    }

    /**
     * 判断当前时间是否是工作日
     *
     * @param now 日期对象
     * @return 是否是工作日
     */
    public static boolean isWeekDay(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int nowIndex = getWeekNo(calendar);
        return nowIndex < 6;
    }

    /**
     * 获取定时器参数
     *
     * @param circleType 周期
     * @param rules      规则
     * @return Object[]：[0]Date-firsttime开始执行时间点,[1]long-period执行间隔时间
     */
    public static Object[] getTimerParam(CircleType circleType, String rules) throws TimerException {
        Object[] param = new Object[2];
        String[] rule = rules.split("\\|");
        try {
            if (circleType.equals(CircleType.Time)) {
                if (rule.length == 2 || rule.length == 1) {
                    if (rule.length == 1) {
                        param[0] = new Date();
                        param[1] = Long.valueOf(rule[0]);
                    } else {
                        String time = DATE_FORMAT.format(new Date()) + " " + rule[0];
                        param[0] = DATETIME_FORMAT.parseObject(time);
                        param[1] = Long.valueOf(rule[1]);
                    }
                } else {
                    throw new TimerException("circleType is not support（circleType=" + CircleType.Time.getName() + ",rules=" + rules + "）");
                }
            } else if (circleType.equals(CircleType.Day)) {
                if (rule.length == 1) {
                    Calendar calendar = getNextDay(Calendar.getInstance());
                    String time = DATE_FORMAT.format(calendar.getTime()) + " " + rule[0];
                    param[0] = DATETIME_FORMAT.parseObject(time);
                } else {
                    throw new TimerException("circleType is not support（circleType=" + CircleType.Day.getName() + ",rules=" + rules + "）");
                }
                param[1] = (long) (1000 * 60 * 60 * 24);
            } else if (circleType.equals(CircleType.Week) || circleType.equals(CircleType.Month)) {
                if (rule.length == 2) {
                    Calendar calendar = getNextDay(Calendar.getInstance());
                    String time = DATE_FORMAT.format(calendar.getTime()) + " " + rule[1];
                    param[0] = DATETIME_FORMAT.parseObject(time);
                } else {
                    throw new TimerException("circleType is not support（circleType=" + CircleType.Week.getName() + ",rules=" + rules + "）");
                }
                param[1] = (long) (1000 * 60 * 60 * 24);
            } else if (circleType.equals(CircleType.Quarter) || circleType.equals(CircleType.Year)) {
                if (rule.length == 3) {
                    Calendar calendar = getNextDay(Calendar.getInstance());
                    String time = DATE_FORMAT.format(calendar.getTime()) + " " + rule[2];
                    param[0] = DATETIME_FORMAT.parseObject(time);
                } else {
                    throw new TimerException("circleType is not supportcircleType=" + CircleType.Week.getName() + ",rules=" + rules + "）");
                }
                param[1] = (long) (1000 * 60 * 60 * 24);
            } else {
                throw new TimerException("circleType is not support（circleType error）");
            }
            return param;
        } catch (Exception e) {
            throw new TimerException("getTimerParam is faild:" + e.getMessage());
        }
    }

    /**
     * 判断当前时间是否是周末
     *
     * @param now 日期对象
     * @return 是否是周末
     */
    public static boolean isWeekend(Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int nowIndex = getWeekNo(calendar);
        return nowIndex > 5;
    }

    /**
     * 以日为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateDay(Date now, Date contrast, String rule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(contrast);
        String[] rules = rule.split("\\|");
        boolean isexcute = false;
        if (rules.length == 1) {
            // 后一天时间
            calendar = getNextDay(calendar);
            String afterday = DATE_FORMAT.format(calendar.getTime()) + " " + rules[0];
            // 当前日期和时间
            String nowday = DATETIME_FORMAT.format(now);
            if (nowday.compareTo(afterday) >= 0) {
                isexcute = true;
            }
        } else {
            isexcute = false;
        }
        return isexcute;
    }

    /**
     * 以周为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateWeek(Date now, Date contrast, String rule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(contrast);
        String[] rules = rule.split("\\|");
        boolean isexcute = false;
        if (rules.length == 2) {
            // 后一天0点
            calendar = getNextDay(calendar);
            String afterday = DATE_FORMAT.format(calendar.getTime()) + " 00:00:00";
            // 当前日期和时间
            String nowday = DATETIME_FORMAT.format(now);
            // 当前时间
            String nowtime = TIME_FORMAT.format(now);

            calendar.setTime(now);
            int dayIndex = Integer.valueOf(rules[0]);// 一周中第几天
            int nowIndex = getWeekNo(calendar);// 当前日期是一周中第几天
            // 比上次发送时间至少晚一天，符合一周中第几天，等于或超过配置的发送时间
            if (nowday.compareTo(afterday) >= 0 && dayIndex == nowIndex && nowtime.compareTo(rules[1]) >= 0) {
                isexcute = true;
            }
        } else {
            isexcute = false;
        }
        return isexcute;
    }

    /**
     * 以月为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateMonth(Date now, Date contrast, String rule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(contrast);
        String[] rules = rule.split("\\|");
        boolean isexcute = false;
        if (rules.length == 2) {
            // 后一天0点
            calendar = getNextDay(calendar);
            String afterday = DATE_FORMAT.format(calendar.getTime()) + " 00:00:00";

            calendar.setTime(now);
            int dayIndex = Integer.valueOf(rules[0]);// 日号
            int nowIndex = getDayNo(calendar);// 当前日期是几号
            int maxday = getLastDayInMonthNo(calendar);// 当前日期所在月最后一天
            if (maxday < dayIndex) {
                dayIndex = maxday;
            }
            // 当前日期和时间
            String nowday = DATETIME_FORMAT.format(now);
            // 当前时间
            String nowtime = TIME_FORMAT.format(now);
            // 比上次发送时间至少晚一天，符合月中日期，等于或超过配置的发送时间
            if (nowday.compareTo(afterday) >= 0 && dayIndex == nowIndex && nowtime.compareTo(rules[1]) >= 0) {
                isexcute = true;
            }
        } else {
            isexcute = false;
        }
        return isexcute;
    }

    /**
     * 以季度为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateQuarter(Date now, Date contrast, String rule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(contrast);
        String[] rules = rule.split("\\|");
        boolean isexcute = false;
        if (rules.length == 3) {
            // 后一天0点
            calendar = getNextDay(calendar);
            String afterday = DATE_FORMAT.format(calendar.getTime()) + " 00:00:00";

            calendar.setTime(now);
            int monthIndex = Integer.valueOf(rules[0]);// 季度内第几月
            int month = getMontNoInQuarter(calendar);// 获取当前月所在季度内的月号

            int dayIndex = Integer.valueOf(rules[1]);// 日号
            int nowIndex = getDayNo(calendar);// 当前日期是几号
            int maxday = getLastDayInMonthNo(calendar);// 当前日期所在月最后一天
            if (maxday < dayIndex) {
                dayIndex = maxday;
            }
            // 当前日期和时间
            String nowday = DATETIME_FORMAT.format(now);
            // 当前时间
            String nowtime = TIME_FORMAT.format(now);
            // 比上次发送时间至少晚一天，符合季度内月号，符合月内日号，等于或超过配置的发送时间
            if (nowday.compareTo(afterday) >= 0 && month == monthIndex && dayIndex == nowIndex && nowtime.compareTo(rules[2]) >= 0) {
                isexcute = true;
            }
        } else {
            isexcute = false;
        }
        return isexcute;
    }

    /**
     * 以年为周期进行校验
     *
     * @param now      需校验的时间
     * @param contrast 参照时间
     * @param rule     校验规则
     * @return 是否符合执行规则
     */
    public static boolean validateYear(Date now, Date contrast, String rule) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(contrast);
        String[] rules = rule.split("\\|");
        boolean isexcute = false;
        if (rules.length == 3) {
            // 后一天0点
            calendar = getNextDay(calendar);
            String afterday = DATE_FORMAT.format(calendar.getTime()) + " 00:00:00";

            calendar.setTime(now);
            int monthIndex = Integer.valueOf(rules[0]);// 年内月号
            int dayIndex = Integer.valueOf(rules[1]);// 日号
            int nowIndex = getDayNo(calendar);// 当前日期是几号
            int nowMonthIndex = getMonthNo(calendar);// 当前日期月号
            int maxday = getLastDayInMonthNo(calendar);// 当前日期所在月最后一天
            if (maxday < dayIndex) {
                dayIndex = maxday;
            }
            // 当前日期和时间
            String nowday = DATETIME_FORMAT.format(now);
            // 当前时间
            String nowtime = TIME_FORMAT.format(now);
            // 比上次发送时间至少晚一天，符合月号，符合月内日号，等于或超过配置的发送时间
            if (nowday.compareTo(afterday) >= 0 && nowMonthIndex == monthIndex && dayIndex == nowIndex && nowtime.compareTo(rules[2]) >= 0) {
                isexcute = true;
            }
        } else {
            isexcute = false;
        }
        return isexcute;
    }

    /**
     * 获取指定日期后一天
     *
     * @param calendar 日历对象
     * @return 日历对象
     */
    public static Calendar getNextDay(Calendar calendar) {
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day + 1);
        return calendar;
    }

    /**
     * 获取指定日期前一天
     *
     * @param calendar 日历对象
     * @return 日历对象
     */
    public static Calendar getPrevDay(Calendar calendar) {
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE, day - 1);
        return calendar;
    }

    /**
     * 获取指定日期是一周中第几天
     *
     * @param calendar 日历对象
     * @return 日历对象
     */
    public static int getWeekNo(Calendar calendar) {
        int nowIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (nowIndex <= 0) {
            nowIndex = 7;
        }
        return nowIndex;
    }

    /**
     * 获取指定日期日号
     *
     * @param calendar 日历对象
     * @return 日号（1-31）
     */
    public static int getDayNo(Calendar calendar) {
        return calendar.get(Calendar.DATE);
    }

    /**
     * 获取指定月号
     *
     * @param calendar 日历对象
     * @return 月号（1-12）
     */
    public static int getMonthNo(Calendar calendar) {
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定月所在季度内的月号
     *
     * @param calendar 日历对象
     * @return 季度号（1, 2, 3）
     */
    public static int getMontNoInQuarter(Calendar calendar) {
        int month = getMonthNo(calendar) % 3;
        if (month == 0) {
            month = 3;
        }
        return month;
    }

    /**
     * 获取指定月最后一天日号
     *
     * @param calendar 日历对象
     * @return 日号
     */
    public static int getLastDayInMonthNo(Calendar calendar) {
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        return calendar.get(Calendar.DATE);
    }

}
