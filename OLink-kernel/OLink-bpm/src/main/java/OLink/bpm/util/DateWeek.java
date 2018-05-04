package OLink.bpm.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateWeek {
	public static String getName(String s) throws Exception {
		Date dt = DateUtil.parseDate(s);
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		String name = "null";
		if (c.get(Calendar.DAY_OF_WEEK) == 0 || c.get(Calendar.DAY_OF_WEEK) == 6) {
			name = "02";
		} else {
			name = "01";
		}
		return name;
	}

	public static String getstartday(String s) throws Exception {
		Date dt = DateUtil.parseDate(s);
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		String name = "0";
		name = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		return name;
	}

	public static int getDays(String month, String year) {
		int days = 30;
		if (month.equalsIgnoreCase("01") || month.equalsIgnoreCase("1")) {
			days = 31;
		}
		if (month.equalsIgnoreCase("02") || month.equalsIgnoreCase("2")) {
			if ((Integer.parseInt(year) % 4) == 0
					&& (((Integer.parseInt(year) % 100) != 0) || ((Integer
							.parseInt(year) % 400) == 0))) {
				days = 29;
			} else {
				days = 28;
			}
		}
		if (month.equalsIgnoreCase("03") || month.equalsIgnoreCase("3")) {
			days = 31;
		}
		if (month.equalsIgnoreCase("04") || month.equalsIgnoreCase("4")) {
			days = 30;
		}
		if (month.equalsIgnoreCase("05") || month.equalsIgnoreCase("5")) {
			days = 31;
		}
		if (month.equalsIgnoreCase("06") || month.equalsIgnoreCase("6")) {
			days = 30;
		}
		if (month.equalsIgnoreCase("07") || month.equalsIgnoreCase("7")) {
			days = 31;
		}
		if (month.equalsIgnoreCase("08") || month.equalsIgnoreCase("8")) {
			days = 31;
		}
		if (month.equalsIgnoreCase("09") || month.equalsIgnoreCase("9")) {
			days = 30;
		}
		if (month.equalsIgnoreCase("10") || month.equalsIgnoreCase("10")) {
			days = 31;
		}
		if (month.equalsIgnoreCase("11") || month.equalsIgnoreCase("11")) {
			days = 30;
		}
		if (month.equalsIgnoreCase("12") || month.equalsIgnoreCase("12")) {
			days = 31;
		}
		return days;
	}

	/*
	 * 返回本月1号所属星期位置
	 */
	public static int getStartday(java.sql.Date date) {
		Calendar scalendar = new GregorianCalendar();
		scalendar.setTime(date);
		scalendar.set(Calendar.DATE, 1);
		//Date d = scalendar.getTime();
		//return d.getDay();
		return scalendar.get(Calendar.DAY_OF_WEEK);
	}

	/*
	 * 返回此日期所属星期位置
	 */
	public static int getDay(java.sql.Date date) {
		Calendar scalendar = new GregorianCalendar();
		scalendar.setTime(date);
		//Date d = scalendar.getTime();
		//return d.getDay();
		return scalendar.get(Calendar.DAY_OF_WEEK);
	}

	public static int getDays(java.sql.Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		//int year = date.getYear() + 1900;
		//int month = date.getMonth() + 1;
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		return getDays(String.valueOf(month), String.valueOf(year));
	}

	// 获取当天所在星期日期
	public static java.sql.Date[] getWeekDate(java.sql.Date date) {
		java.sql.Date[] dates = new java.sql.Date[7];
		Calendar scalendar = new GregorianCalendar();
		scalendar.setTime(date);
		scalendar.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SUNDAY);
		dates[0] = new java.sql.Date(scalendar.getTime().getTime());
		scalendar.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
		dates[1] = new java.sql.Date(scalendar.getTime().getTime());
		scalendar.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.TUESDAY);
		dates[2] = new java.sql.Date(scalendar.getTime().getTime());
		scalendar.set(GregorianCalendar.DAY_OF_WEEK,
				GregorianCalendar.WEDNESDAY);
		dates[3] = new java.sql.Date(scalendar.getTime().getTime());
		scalendar
				.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.THURSDAY);
		dates[4] = new java.sql.Date(scalendar.getTime().getTime());
		scalendar.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.FRIDAY);
		dates[5] = new java.sql.Date(scalendar.getTime().getTime());
		scalendar
				.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.SATURDAY);
		dates[6] = new java.sql.Date(scalendar.getTime().getTime());
		return dates;
	}

	// 获取星期名称
	public static String getWeekName(int i) {
		if (i == 0) {
			return "星期日";
		} else if (i == 1) {
			return "星期一";
		} else if (i == 2) {
			return "星期二";
		} else if (i == 3) {
			return "星期三";
		} else if (i == 4) {
			return "星期四";
		} else if (i == 5) {
			return "星期五";
		} else if (i == 6) {
			return "星期六";
		}
		return "";
	}

	public static void main(String[] args) {
		try {
			Calendar scalendar = new GregorianCalendar();
			java.sql.Date d = new java.sql.Date(System.currentTimeMillis());
			scalendar.setTime(d);
			//String s = "2005-7-15";

			//int year = d.getYear();
			//int year = scalendar.get(Calendar.YEAR);
			//int month = d.getMonth();
			//int month = scalendar.get(Calendar.MONTH);
			//java.sql.Date dt = new java.sql.Date(year, month, 22);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
