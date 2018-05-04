package pers.acp.tools.common;

import pers.acp.tools.exceptions.TimerException;
import pers.acp.tools.task.timer.container.Calculation;

import java.util.Calendar;
import java.util.Date;

public final class CalendarTools {

    /**
     * 获取日历对象
     *
     * @return 日历对象
     */
    public static Calendar getCalendar() throws TimerException {
        return Calculation.getCalendar();
    }

    /**
     * 获取日历对象
     *
     * @param dateStr 日期字符串（yyyy-MM-dd）
     * @return 日历对象
     */
    public static Calendar getCalendar(String dateStr) throws TimerException {
        return Calculation.getCalendar(dateStr);
    }

    /**
     * 获取指定日期的后一天
     *
     * @param calendar 日历对象
     * @return 日历对象
     */
    public static Calendar getNextDay(Calendar calendar) {
        return Calculation.getNextDay(calendar);
    }

    /**
     * 获取指定日期前一天
     *
     * @param calendar 日历对象
     * @return 日历对象
     */
    public static Calendar getPrevDay(Calendar calendar) {
        return Calculation.getPrevDay(calendar);
    }

    /**
     * 获取指定日期是一周中第几天
     *
     * @param calendar 日历对象
     * @return 日历对象
     */
    public static int getWeekNo(Calendar calendar) {
        return Calculation.getWeekNo(calendar);
    }

    /**
     * 获取指定日期日号
     *
     * @param calendar 日历对象
     * @return 日号（1-31）
     */
    public static int getDayNo(Calendar calendar) {
        return Calculation.getDayNo(calendar);
    }

    /**
     * 获取指定月号
     *
     * @param calendar 日历对象
     * @return 月号（1-12）
     */
    public static int getMonthNo(Calendar calendar) {
        return Calculation.getMonthNo(calendar);
    }

    /**
     * 获取指定月所在季度内的月号
     *
     * @param calendar 日历对象
     * @return 季度号（1, 2, 3）
     */
    public static int getMontNoInQuarter(Calendar calendar) {
        return Calculation.getMontNoInQuarter(calendar);
    }

    /**
     * 获取指定月最后一天日号
     *
     * @param calendar 日历对象
     * @return 日号
     */
    public static int getLastDayInMonthNo(Calendar calendar) {
        return Calculation.getLastDayInMonthNo(calendar);
    }

    /**
     * 判断当前时间是否是工作日
     *
     * @param now 日期对象
     * @return 是否是工作日
     */
    public static boolean isWeekDay(Date now) {
        return Calculation.isWeekDay(now);
    }

    /**
     * 判断当前时间是否是周末
     *
     * @param now 日期对象
     * @return 是否是周末
     */
    public static boolean isWeekend(Date now) {
        return Calculation.isWeekend(now);
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
        return Calculation.validateDay(now, contrast, rule);
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
        return Calculation.validateWeek(now, contrast, rule);
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
        return Calculation.validateMonth(now, contrast, rule);
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
        return Calculation.validateQuarter(now, contrast, rule);
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
        return Calculation.validateYear(now, contrast, rule);
    }

}
