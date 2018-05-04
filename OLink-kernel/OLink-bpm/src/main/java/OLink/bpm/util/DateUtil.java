package OLink.bpm.util;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import OLink.bpm.core.workcalendar.calendar.ejb.CalendarProcess;
import org.apache.log4j.Logger;

/**
 * <p>
 * Title: Cyberway Commons
 * </p>
 * <p>
 * Description: Common Date Utility
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company: Cyberway Compucomm Co., Ltd.
 * </p>
 * 
 * @author Gaven
 * @version 1.0
 */

public class DateUtil {
	
	private static final Logger log = Logger.getLogger(DateUtil.class);
	/**
	 * getDateStr get a string with format YYYY-MM-DD from a Date object
	 * 
	 * @param date
	 *            date
	 * @return String
	 */
	static public String getDateStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}

	static public String getDateStrC(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(date);
	}

	static public String getDateStrCompact(Date date) {
		if (date == null)
			return "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String str = format.format(date);
		return str;
	}

	/**
	 * getDateStr get a string with format YYYY-MM-DD HH:mm:ss from a Date
	 * object
	 * 
	 * @param date
	 *            date
	 * @return String
	 */
	static public String getDateTimeStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(date!=null){
			return format.format(date);
		}else
			return "";
	}

	static public String getDateTimeStrC(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		if(date!=null){
			return format.format(date);
		}else
			return "";
	}

	public static String getCurDateStr(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(new Date());
		
	}

	/**
	 * Parses text in 'YYYY-MM-DD' format to produce a date.
	 * 
	 * @param s
	 *            the text
	 * @return Date
	 * @throws ParseException
	 */
	static public Date parseDate(String s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(s);
	}

	static public Date parseDate(String s, String f) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(f);
		return format.parse(s);
	}

	static public Date parseDateC(String s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.parse(s);
	}

	/**
	 * Parses text in 'YYYY-MM-DD' format to produce a date.
	 * 
	 * @param s
	 *            the text
	 * @return Date
	 * @throws ParseException
	 */
	static public Date parseDateTime(String s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.parse(s);
	}

	static public Date parseDateTimeC(String s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		return format.parse(s);
	}

	/**
	 * Parses text in 'HH:mm:ss' format to produce a time.
	 * 
	 * @param s
	 *            the text
	 * @return Date
	 * @throws ParseException
	 */
	static public Date parseTime(String s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.parse(s);
	}

	static public Date parseTimeC(String s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("HH时mm分ss秒");
		return format.parse(s);
	}

	static public int yearOfDate(Date s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String d = format.format(s);
		return Integer.parseInt(d.substring(0, 4));
	}

	static public int monthOfDate(Date s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String d = format.format(s);
		return Integer.parseInt(d.substring(5, 7));
	}

	static public int dayOfDate(Date s) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String d = format.format(s);
		return Integer.parseInt(d.substring(8, 10));
	}

	static public String getDateTimeStr(java.sql.Date date, double time) {
		String format = "yyyy-MM-dd";
		SimpleDateFormat sf = new SimpleDateFormat(format);
		/*
		int year = date.getYear() + 1900;
		int month = date.getMonth() + 1;
		int day = date.getDate();
		String dateStr = year + "-" + month + "-" + day;
		*/
		String dateStr = sf.format(date);
		Double d = new Double(time);
		String timeStr = String.valueOf(d.intValue()) + ":00:00";

		return dateStr + " " + timeStr;
	}

	/**
	 * Get the total month from two date.
	 * 
	 * @param sd
	 *            the start date
	 * @param ed
	 *            the end date
	 * @return int month form the start to end date
	 * @throws ParseException
	 */
	static public int diffDateM(Date sd, Date ed) throws ParseException {
		Calendar c_sd = Calendar.getInstance();
		Calendar c_ed = Calendar.getInstance();
		c_sd.setTime(sd);
		c_ed.setTime(ed);
		return (c_ed.get(Calendar.YEAR) - c_sd.get(Calendar.YEAR)) * 12 + 
			c_ed.get(Calendar.MONTH) - c_sd.get(Calendar.MONTH) + 1;
		/*
		return (ed.getYear() - sd.getYear()) * 12 + ed.getMonth()
				- sd.getMonth() + 1;
				*/
	}

	static public int diffDateD(Date sd, Date ed) throws ParseException {
		return Math.round((float)(ed.getTime() - sd.getTime()) / 86400000) + 1;
	}

	static public int diffDateM(int sym, int eym) throws ParseException {
		return (Math.round((float)eym / 100) - Math.round((float)sym / 100)) * 12
				+ (eym % 100 - sym % 100) + 1;
	}

	static public java.sql.Date getNextMonthFirstDate(java.sql.Date date)
			throws ParseException {
		Calendar scalendar = new GregorianCalendar();
		scalendar.setTime(date);
		scalendar.add(Calendar.MONTH, 1);
		scalendar.set(Calendar.DATE, 1);
		return new java.sql.Date(scalendar.getTime().getTime());
	}

	static public Date getNextMonthDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}

	static public java.sql.Date getFrontDateByDayCount(java.sql.Date date,
			int dayCount) throws ParseException {
		Calendar scalendar = new GregorianCalendar();
		scalendar.setTime(date);
		scalendar.add(Calendar.DATE, -dayCount);
		return new java.sql.Date(scalendar.getTime().getTime());
	}

	/**
	 * Get first day of the month.
	 * 
	 * @param year
	 *            the year
	 * @param month
	 *            the month
	 * @return Date first day of the month.
	 * @throws ParseException
	 */
	static public Date getFirstDay(String year, String month)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(year + "-" + month + "-1");
	}

	static public Date getFirstDay(int year, int month) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.parse(year + "-" + month + "-1");
	}

	static public Date getFirstDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);

		return c.getTime();
	}

	static public Date getLastDay(String year, String month)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = format.parse(year + "-" + month + "-1");

		Calendar scalendar = new GregorianCalendar();
		scalendar.setTime(date);
		scalendar.add(Calendar.MONTH, 1);
		scalendar.add(Calendar.DATE, -1);
		date = scalendar.getTime();
		return date;
	}

	static public Date getLastDay(int year, int month) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = format.parse(year + "-" + month + "-1");

		Calendar scalendar = new GregorianCalendar();
		scalendar.setTime(date);
		scalendar.add(Calendar.MONTH, 1);
		scalendar.add(Calendar.DATE, -1);
		date = scalendar.getTime();
		return date;
	}

	static public Date getLastDay(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.roll(Calendar.DATE, false);
		return c.getTime();
	}

	/**
	 * getToday get todat string with format YYYY-MM-DD from a Date object
	 * 
	 * @param date
	 *            date
	 * @return String
	 */

	static public String getTodayStr() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		return getDateStr(calendar.getTime());
	}

	static public Date getToday() throws ParseException {
		return new Date(System.currentTimeMillis());
	}

	static public String getTodayAndTime() {
		return new Timestamp(System.currentTimeMillis()).toString();
	}

	static public String getTodayC() throws ParseException {
		Calendar calendar = Calendar.getInstance();
		return getDateStrC(calendar.getTime());
	}

	static public int getThisYearMonth() throws ParseException {
		//Date today = Calendar.getInstance().getTime();
		Calendar today = Calendar.getInstance();
		return (today.get(Calendar.YEAR)) * 100 + today.get(Calendar.MONTH) + 1;
	}

	static public int getYearMonth(Date date) throws ParseException {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return (c.get(Calendar.YEAR)) * 100 + c.get(Calendar.MONTH) + 1;
	}

	// 获取相隔年数
	static public int getDistinceYear(String beforedate, String afterdate)
			throws ParseException {
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");

		int yearCount = 0;
		try {
			Date d1 = d.parse(beforedate);
			Date d2 = d.parse(afterdate);
			Calendar c1 = Calendar.getInstance();
			c1.setTime(d1);
			Calendar c2 = Calendar.getInstance();
			c2.setTime(d2);
			yearCount = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);

		} catch (ParseException e) {
			log.info("Date parse error!");
		}
		return yearCount;
	}

	// 获取相隔月数
	static public long getDistinceMonth(String beforedate, String afterdate)
			throws ParseException {
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
		long monthCount = 0;
		try {
			Date d1 = d.parse(beforedate);
			Date d2 = d.parse(afterdate);
			Calendar c1 = Calendar.getInstance();
			c1.setTime(d1);
			Calendar c2 = Calendar.getInstance();
			c2.setTime(d2);

			monthCount = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12 + 
				c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
			// dayCount = (d2.getTime()-d1.getTime())/(30*24*60*60*1000);

		} catch (ParseException e) {
			log.info("Date parse error!");
			// throw e;
		}
		return monthCount;
	}

	// 获取相隔月数,精确到小数点后两位
	static public double getDistinceMonth1(Date beforedate, Date afterdate)
			throws ParseException {
		double monthCount = 0;
		if (beforedate != null && afterdate != null) {
			try {
				long dayCount = (afterdate.getTime() - beforedate.getTime())
						/ (24 * 60 * 60 * 1000);// 实际相隔天数
				monthCount = (dayCount + 1) / 28.0f;
			} catch (Exception e) {
				log.info("Date parse error!");	
			}
		}
		return Arith.round(monthCount, 2);
	}

	// 获取相隔天数
	static public long getDistinceDay(String beforedate, String afterdate)
			throws ParseException {
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
		long dayCount = 0;
		try {
			Date d1 = d.parse(beforedate);
			Date d2 = d.parse(afterdate);

			dayCount = (d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000);

		} catch (ParseException e) {
			log.info("Date parse error!");
		}
		return dayCount;
	}

	// 获取相隔天数
	static public long getDistinceDay(Date beforedate, Date afterdate)
			throws ParseException {
		long dayCount = 0;

		try {
			dayCount = (afterdate.getTime() - beforedate.getTime())
					/ (24 * 60 * 60 * 1000);

		} catch (Exception e) {
			log.info("Date parse error!");
		}
		return dayCount;
	}

	static public long getDistinceDay(java.sql.Date beforedate,
			java.sql.Date afterdate) throws ParseException {
		long dayCount = 0;

		try {
			dayCount = (afterdate.getTime() - beforedate.getTime())
					/ (24 * 60 * 60 * 1000);

		} catch (Exception e) {
			log.info("Date parse error!");
		}
		return dayCount;
	}

	// 获取相隔天数
	static public long getDistinceDay(String beforedate) throws ParseException {
		return getDistinceDay(beforedate, getTodayStr());
	}

	// 获取相隔时间数
	static public long getDistinceTime(String beforeDateTime,
			String afterDateTime) throws ParseException {
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeCount = 0;
		try {
			Date d1 = d.parse(beforeDateTime);
			Date d2 = d.parse(afterDateTime);

			timeCount = (d2.getTime() - d1.getTime()) / (60 * 60 * 1000);

		} catch (ParseException e) {
			log.info("Date parse error!");
			throw e;
		}
		return timeCount;
	}

	// 获取相隔时间数
	static public long getDistinceTime(String beforeDateTime)
			throws ParseException {
		return getDistinceTime(beforeDateTime, DateFormat.getDateInstance().format(new Timestamp(System
				.currentTimeMillis())));
	}

	// 获取相隔分钟数
	static public long getDistinceMinute(String beforeDateTime,
			String afterDateTime) throws ParseException {
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long timeCount = 0;
		try {
			Date d1 = d.parse(beforeDateTime);
			Date d2 = d.parse(afterDateTime);

			timeCount = (d2.getTime() - d1.getTime()) / (60 * 1000);

		} catch (ParseException e) {
			log.info("Date parse error!");
			throw e;
		}
		return timeCount;
	}

	// 获取相隔分钟数
	static public long getDistinceMinute(String afterDateTime)
			throws ParseException {
		return getDistinceMinute(DateFormat.getDateInstance().format(
				new Timestamp(System.currentTimeMillis())), afterDateTime);
	}

	// 判断是否超出指定相隔时间范围内
	static public boolean isOvertime(String beforeDateTime, String timeCount) {
		boolean exceed = false;
		try {
			long count1 = Long.parseLong(timeCount);
			long count2 = getDistinceTime(beforeDateTime);
			if (count1 < count2) {
				exceed = true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return exceed;
	}

	static public String getTimestamStr(Timestamp timestamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(timestamp);
	}

	static public String getTimeStr(Time time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		return format.format(time);
	}

	// 判断后者时间是否为前者时间前
	static public boolean isBeforeCheckDate(String checkdate,
			Date auditDate) throws ParseException {
		Date cd;
		try {
			cd = new Date(parseDate(checkdate).getTime());

		} catch (ParseException ex) {
			log.info(ex);
			return false;
		}
		return isBeforeCheckDate(cd, auditDate);
	}

	static private boolean isBeforeCheckDate(Date checkdate,
			Date auditDate) throws ParseException {
		return auditDate.before(checkdate);
	}

	static public String format(Date date, String formatText) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat(formatText);
		return format.format(date);
	}

	static public int getDaysOfMonth(Date startdate, Date enddate, String month)
			throws Exception {
		Calendar cs = Calendar.getInstance();
		cs.setTime(startdate);
		Calendar ce = Calendar.getInstance();
		ce.setTime(enddate);
		//int startmonth = startdate.getMonth() + 1;
		//int endmonth = enddate.getMonth() + 1;
		int startmonth = cs.get(Calendar.MONTH) + 1;
		int endmonth = ce.get(Calendar.MONTH) + 1;
		int m = Integer.parseInt(month);
		//int day = getLastDay(String.valueOf(startdate.getYear()), month)
				//.getDate();
		Date ld = getLastDay(String.valueOf(cs.get(Calendar.YEAR)), month);
		Calendar c = Calendar.getInstance();
		c.setTime(ld);
		int day = c.get(Calendar.DAY_OF_MONTH);
		if ((startmonth < m) && (m < endmonth)) {
			return day;
		} else if (m == startmonth) {
			//return day - startdate.getDate() + 1;
			return day - cs.get(Calendar.DAY_OF_MONTH);
		} else if (m == endmonth) {
			//return enddate.getDate();
			return ce.get(Calendar.DAY_OF_MONTH);
		}
		return 0;
	}

	static public int diffDateH(String beforeDateTime, String afterDateTime)
			throws ParseException {
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int HourCount = 0;
		try {
			Date d1 = d.parse(beforeDateTime);
			Date d2 = d.parse(afterDateTime);
			HourCount = (Math
					.round(((float)(d2.getTime() - d1.getTime()) / (24 * 60 * 60 * 1000)) * 100)) / 100;
		} catch (ParseException e) {
			log.info("Date parse error!");
			throw e;
		}
		return HourCount;
	}

	// /////////vinsun/////////////////
	static public Date getNextDateByYearCount(Date date, int yearCount)
			throws ParseException {
		Calendar scalendar = Calendar.getInstance();
		scalendar.setTime(date);
		scalendar.add(Calendar.YEAR, yearCount);
		return scalendar.getTime();
	}

	static public Date getNextDateByMonthCount(Date date, int monthCount)
			throws ParseException {
		Calendar scalendar = Calendar.getInstance();
		scalendar.setTime(date);
		scalendar.add(Calendar.MONTH, monthCount);
		return scalendar.getTime();
	}

	// /////////////////////////////////

	static public Date getNextDateByDayCount(Date date, int dayCount)
			throws ParseException {
		Calendar scalendar = Calendar.getInstance();
		scalendar.setTime(date);
		scalendar.add(Calendar.DAY_OF_MONTH, dayCount);
		return scalendar.getTime();
	}

	static public Date getNextDateByMinuteCount(Date date, int minuteCount)
			throws ParseException {
		Calendar scalendar = Calendar.getInstance();
		scalendar.setTime(date);
		scalendar.add(Calendar.MINUTE, minuteCount);
		return scalendar.getTime();
	}

	/**
	 * 获取两个日期时间的差值,忽略年月日
	 * 
	 * @param beforeTime
	 *            前一个时间
	 * @param afterTime
	 *            后一个时间
	 * @return 时间差值,单位为毫秒(ms)
	 */
	public static long getDiffTime(Date beforeTime, Date afterTime) {
		try {
			String beforeTimeStr = format(beforeTime, "HH:mm:ss");
			String afterTimeStr = format(afterTime, "HH:mm:ss");
			Date bTime = parseTime(beforeTimeStr);
			Date aTime = parseTime(afterTimeStr);
			long diff = aTime.getTime() - bTime.getTime();
			return diff;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 获取两个日期时间的差值,包括年月日
	 * 
	 * @param beforeTime
	 *            前一个时间
	 * @param afterTime
	 *            后一个时间
	 * @return 时间差值,单位为毫秒(ms)
	 */
	public static long getDiffDateTime(Date beforeTime, Date afterTime) {
		try {
			long diff = afterTime.getTime() - beforeTime.getTime();
			return diff;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	static public double getWorkingDayCount(String startdate, String enddate,
			String calendar) throws Exception {
		Date startD = parseDate(startdate, "yyyy-MM-dd HH:mm:ss");
		Date endD = parseDate(enddate, "yyyy-MM-dd HH:mm:ss");
		return getWorkingDayCount(startD, endD, calendar);
	}

	static public double getWorkingDayCount(Date startdate, Date enddate,
			String calendar) throws Exception {
		CalendarProcess process = (CalendarProcess) ProcessFactory
				.createProcess(CalendarProcess.class);
		return process.countWorkingDays(startdate, enddate, calendar);
	}

	static public double getWorkingTimesCount(String startdate, String enddate,
			String calendar) throws Exception {
		Date startD = parseDate(startdate, "yyyy-MM-dd HH:mm:ss");
		Date endD = parseDate(enddate, "yyyy-MM-dd HH:mm:ss");
		BigDecimal bd = new BigDecimal(getWorkingTimesCount(startD, endD,
				calendar));
		//四舍五入保留两位
		double d1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d1;
	}

	static public double getWorkingTimesCount(Date startdate, Date enddate,
			String calendar) throws Exception {
		CalendarProcess process = (CalendarProcess) ProcessFactory
				.createProcess(CalendarProcess.class);
		BigDecimal bd = new BigDecimal(process.countTimesOfWorkingDays(
				startdate, enddate, calendar));
		//四舍五入保留两位
		double d1 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return d1;
	}

	public static void main(String[] args) {
		try {
			// getDaysOfMonth(new Date(),new Date(),"02");
			// Double s=new Double("123.6");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			int days = getDaysOfMonth(format.parse("2007-6-6"), format
					.parse("2007-7-7"), "07");
			System.err.println(days);
			// java.text.SimpleDateFormat format = new
			// java.text.SimpleDateFormat(
			// "yyyy-MM-dd");

			// java.sql.Date date = new
			// java.sql.Date(System.currentTimeMillis());
			// log.info(getFrontDateByDayCount(date, 80));
			// Date date = getLastDay("2004","12");
			// log.info(getThisYearMonth());
			// log.info(getTodayAndTime());

			// String s = getDateTimeStr(new
			// java.sql.Date(System.currentTimeMillis()), 12.0d);
			// log.info(getDistinceMinute(s, "2005-7-20 12:45:58"));

			// log.info(isOvertime("2005-3-15 2:55:00", "26"));

			// String s = "2005-3-23 14:35:58.177";
			// Timestamp t = Timestamp.valueOf(s);
			// log.info(t.toString());
			// String s = new Timestamp(System.currentTimeMillis()).toString();
			// log.info(s);
			// log.info(getToday());
			// Timestamp t = Timestamp.valueOf(s);
			// log.info(t.toString());
			// log.info(getDistinceTime(s));
			// log.info(new Timestamp(0));

			// String s = getDateTimeStr(new
			// java.sql.Date(System.currentTimeMillis()), 12.0d);
			// log.info(getDistinceMonth(s, "2006-11-20 12:45:58"));
			// String s1 = "2006-2-1";
			// String s2 = "2006-2-28";

			// float f1 = 5;
			// float f2 = 3;
			// log.info(f1/f2);

			// NumberFormat format = new DecimalFormat("0.00");
			// String result = format.format(-1234561111111.1);

			Date current = new Date();
			//Date firstDate = getFirstDay(current);
			Date lastDate = getLastDay(current);
			Calendar c = Calendar.getInstance();
			c.setTime(lastDate);
			c.roll(Calendar.DATE, true);
			Calendar cld = Calendar.getInstance();
			Date startDate = new Date();
			cld.setTime(startDate);
			// cld.set(Calendar.DAY_OF_MONTH, 1);
			cld.set(Calendar.HOUR_OF_DAY, 7);
			// log.info(cld.get(Calendar.HOUR_OF_DAY));

			log.info(getWorkingDayCount(cld.getTime(), startDate, "id"));
			double countTime = getWorkingTimesCount(cld.getTime(), startDate,
					"id");
			log.info(countTime);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
