/*
 * @(#)CommonUtil.java
 *
 * Copyright (c)cyberway Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of cyberway Inc.
 * ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with cyberway Inc.
 */

package OLink.bpm.core.workflow.utility;

import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * All Common utilty tool function
 * 
 * @version 1.00
 * @author Wang ZhengRong
 */

public class CommonUtil {

	// add char to string's back;
	static final public int ADD_BACK = 1;
	// add char to string's fore;
	static final public int ADD_FORE = 2;

	/**
	 * get a datetime String
	 * 
	 *@return String like :2001年5月4日 星期三 14点12分14秒
	 * 
	 */
	public static String getCurrentDateTime() {
		String week = "日一二三四五六";
		Calendar cal = Calendar.getInstance();

		String str = "";
		str = cal.get(Calendar.YEAR) + "年 ";
		str = str + (cal.get(Calendar.MONTH) + 1) + "月";
		str = str + cal.get(Calendar.DAY_OF_MONTH) + "日 ";

		str = str
				+ "星期"
				+ week.substring(cal.get(Calendar.DAY_OF_WEEK) - 1, cal
						.get(Calendar.DAY_OF_WEEK)) + " ";

		str = str + cal.get(Calendar.HOUR_OF_DAY) + "点 ";
		str = str + cal.get(Calendar.MINUTE) + "分 ";
		str = str + cal.get(Calendar.SECOND) + "秒 ";
		return str;
	} // String getCurrentDateTime() end;

	/**
	 * get next id For Example : getNext("009",3) will return "010";
	 * 
	 *@param curr
	 *            current id
	 *@param len
	 *            total length
	 *@return public static String
	 */
	public static String getNext(String curr, int len) {
		if (curr == null) {
			return CommonUtil.setChar('0', len);
		}
		String ret = "";
		long lValue = new Long(curr).longValue() + 1;
		ret += lValue;
		ret = CommonUtil.setChar('0', len - ret.length()) + ret;

		return ret;
	}

	public static boolean getBoolean(String str) {
		if (isBooleanString(str)) {
			return (Boolean.valueOf(str)).booleanValue();
		} else {
			return false;
		}
	}

	/**
	 * get special length string with the same char For Example : setChar('0',4)
	 * will return "0000"
	 * 
	 *@param chr
	 *            char
	 *@param totalLen
	 *            total length
	 * 
	 *@return String
	 */
	public static String setChar(char chr, int totalLen) {
		return CommonUtil.setChar("", chr, totalLen, CommonUtil.ADD_FORE);
	}

	/**
	 * get special length string with the same char For Example :
	 * setChar("12",'0',4) will return "0012"
	 * 
	 *@str String need to filled with
	 *@param chr
	 *            char
	 *@param totalLen
	 *            total length
	 * 
	 *@return String
	 */
	public static String setChar(String str, char chr, int totalLen) {
		return CommonUtil.setChar(str, chr, totalLen, CommonUtil.ADD_FORE);
	}

	/**
	 * get special length string with the same char For Example :
	 * setChar("12",'0',4,CommonUtil.ADD_BACK) will return "1200"
	 * 
	 *@str String need to filled with
	 *@param chr
	 *            char
	 *@param totalLen
	 *            total length
	 *@param position
	 *            wh
	 *@author Administrator
	 *@return String
	 */
	public static String setChar(String str, char chr, int totalLen,
			int position) {
		if (totalLen < 0 || str == null) {
			return str;
		}
		int sLen = str.length();
		String ret = str;
		for (int i = 0; i < totalLen - sLen; i++) {
			if (position == CommonUtil.ADD_BACK) {
				ret = ret + chr;
			} else {
				ret = chr + ret;
			}
		} // end of for
		return ret;
	}

	/**
	 * Validate a date string format YYYY-MM-DD or YYYY-MM-DD HH:MM:SS
	 * 
	 * @param strDate
	 *            a string representing date
	 * @return boolean true if format is "YYYY-MM-DD HH:MM:SS" else return false
	 */
	static public boolean isValidDate(String strDate) {
		try {
			return strToDate(strDate) != null;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * check if the string are composite of number character for example
	 * "-48594.94395" return true "454kdf.94" return false
	 * 
	 * @param str
	 *            String
	 * 
	 * @return true or false
	 */
	static public boolean isNumberString(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}
		try {
			new Double(str).doubleValue();
		} catch (Exception e) {
			CommonUtil.printDebugInfo("it is not a number!");
			return false;
		}
		return true;
	}

	static public boolean isBooleanString(String str) {
		if (str == null || str.trim().equals("")) {
			return false;
		}
		try {
			Boolean.valueOf(str);
		} catch (Exception e) {
			CommonUtil.printDebugInfo("it is not a boolean!");
			return false;
		}
		return true;
	}

	static public int getFirstIndexInStrArray(String[] list, String str) {
		int pos = -1;
		if (list != null && list.length > 0) {
			for (int i = 0; i < list.length; i++) {
				if (list[i] != null && list[i].equals(str)) {
					pos = i;
					break;
				}
			}

		}

		return pos;
	}

	/**
	 * check if the string are composite of letter for example "iefKASDFK"
	 * return true "454kdfadf4" return false
	 * 
	 * @param str
	 *            String
	 * 
	 * @return true or false
	 */
	static public boolean isLetterString(String str) {
		if (str == null) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isLetter(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * check if the string are composite of letter or digit for example
	 * "iefKASDFK" return true "454kdfadf4" return true "^rek/." return false
	 * 
	 * @param str
	 *            String
	 * 
	 * @return true or false
	 */
	static public boolean isLetterOrDigitString(String str) {
		if (str == null) {
			return false;
		}
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isLetterOrDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * check if the string are composite of number character and length less
	 * than special bit number for example "-48594.94395" return true
	 * "454kdf.94" return false
	 * 
	 * @param str
	 *            String
	 * 
	 * @return true or false
	 */
	static public boolean isNumberString(String str, int bit) {
		if (bit <= 0) {
			return false;
		}
		return isNumberString(str) && str.length() <= bit;
	}

	/**
	 * getDateStr get a string with format YYYY-MM-DD from a Date object
	 * 
	 * @param date
	 *            Date
	 * @return String
	 */
	static public String getDateStr(Date date) {
		if (date == null) {
			return "";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		return year + "-" + month + "-" + day;
	}

	/**
	 * getDateStr get a string with format YYYY-MM-DD from a Date object
	 * 
	 * @param Date
	 *            date
	 * @return String
	 */
	static public String getCurrentDateToSerializeNo() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);

		return year + "-" + month + "-" + day;
	}

	/**
	 * getTimeStr get a string with format HH24:MI:SS from a Time object
	 * 
	 * @param java
	 *            .sql.Time time
	 * @return String
	 */
	static public String getTimeStr(java.sql.Time time) {
		if (time == null) {
			return "";
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		return " " + cal.get(Calendar.HOUR) + ":" + cal.get(Calendar.MINUTE)
				+ ":" + "00";
	}

	/**
	 * convert a string with format YYYY-MM-DD to a java.sql.Date object For
	 * Example : Date String "2000-12-10"
	 * 
	 * @param dateString
	 *            format "YYYY-MM-DD" or "YYYY-MM-DD HH:MM:SS"
	 * 
	 * @return java.sql.Date type object
	 */
	@SuppressWarnings("deprecation")
	static public java.sql.Date strToDate(String str) {
		String dtstr = str;

		dtstr = dtstr.replace('/', '-');
		while (dtstr.indexOf("  ") >= 0) {
			dtstr = dtstr.substring(0, dtstr.indexOf("  "))
					+ dtstr.substring(dtstr.indexOf("  ") + 1);
		}

		if (dtstr == null || dtstr.length() < 8) {
			return null;
		}

		// int len = str.length();
		StringBuffer year = new StringBuffer("0");
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			year.append(dtstr.charAt(0));
			dtstr = dtstr.substring(1);
		}

		StringBuffer month = new StringBuffer("0");
		dtstr = dtstr.trim().length() > 0 ? dtstr.trim().substring(1) : "";
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			month.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}

		StringBuffer date = new StringBuffer("0");
		dtstr = dtstr.trim().length() > 0 ? dtstr.trim().substring(1) : "";
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			date.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}

		StringBuffer hour = new StringBuffer("0");
		dtstr = dtstr.trim();
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			hour.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}

		StringBuffer minute = new StringBuffer("0");
		dtstr = dtstr.trim().length() > 0 ? dtstr.trim().substring(1) : "";
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			minute.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}
		StringBuffer second = new StringBuffer("0");
		dtstr = dtstr.trim().length() > 0 ? dtstr.trim().substring(1) : "";
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			second.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}
		int iyear = Integer.parseInt(year.toString()) - 1900;
		int imonth = Integer.parseInt(month.toString()) - 1;
		int idate = Integer.parseInt(date.toString());
		int ihour = Integer.parseInt(hour.toString());
		int iminute = Integer.parseInt(minute.toString());
		int isecond = Integer.parseInt(second.toString());
		Date uDate = new Date(iyear, imonth, idate, ihour,
				iminute, isecond);
		java.sql.Date sDate = new java.sql.Date(0);
		sDate.setTime(uDate.getTime());
		return sDate;
	}

	/**
	 * create a date string format YYYY-MM-DD HH:MM:SS "98-1-2" ===>
	 * "0098-01-02 00:00:00" "1999-1-2 11:9:" ===> "1999-01-02 11:09:00"
	 * 
	 * @param strDate
	 *            a string representing date
	 * @return "YYYY-MM-DD HH:MM:SS" or null
	 */
	public static String createStandDateStr(String strDate) {
		String year, month, day;
		String hour = "00";
		String minute = "00";
		String second = "00";

		int i = 0;
		// year
		String sub = strDate;
		if (sub.indexOf("-") < 0) {
			return null;
		}
		year = sub.substring(i, sub.indexOf("-")).trim();
		if (!CommonUtil.isNumberString(year, 4)) {
			return null;
		}
		year = CommonUtil.setChar(year, '0', 4);
		// month
		i = sub.indexOf("-") + 1;
		sub = sub.substring(i);
		if (sub.indexOf("-") < 0) {
			return null;
		}
		month = sub.substring(0, sub.indexOf("-")).trim();
		if (!CommonUtil.isNumberString(month, 2)) {
			return null;
		}
		month = CommonUtil.setChar(month, '0', 2);
		// day
		i = sub.indexOf("-") + 1;
		sub = sub.substring(i);
		if (sub.indexOf(" ") < 0) {
			day = sub.trim();
			if (day.length() <= 0) {
				return null;
			}
			if (day.length() <= 2) {
				day = sub.trim();
				if (!CommonUtil.isNumberString(day, 2)) {
					return null;
				}
				day = CommonUtil.setChar(day, '0', 2);
				return year + "-" + month + "-" + day + " " + hour + ":"
						+ minute + ":" + second;
			} else {
				return null;
			}
		} else {
			day = sub.substring(0, sub.indexOf(" ")).trim();
			if (day.length() <= 2) {
				day = day.trim();
				if (!CommonUtil.isNumberString(day, 2)) {
					return null;
				}
				day = CommonUtil.setChar(day, '0', 2);
			} else {
				return null;
			}
		} // end of if(<0)
		// hour
		i = sub.indexOf(" ") + 1;
		sub = sub.substring(i);
		if (sub.indexOf(":") < 0) {
			hour = sub.trim();
			if (hour.length() <= 0) {
				return year + "-" + month + "-" + day + " " + "00" + ":"
						+ minute + ":" + second;
			}
			if (hour.length() <= 2) {
				hour = hour.trim();
				if (!CommonUtil.isNumberString(hour, 2)) {
					return null;
				}
				hour = CommonUtil.setChar(hour, '0', 2);
				return year + "-" + month + "-" + day + " " + hour + ":"
						+ minute + ":" + second;
			} else {
				return null;
			}
		} else {
			hour = sub.substring(0, sub.indexOf(":")).trim();
			if (hour.length() <= 2) {
				hour = hour.trim();
				if (!CommonUtil.isNumberString(day, 2)) {
					return null;
				}
				hour = CommonUtil.setChar(hour, '0', 2);
			} else {
				return null;
			}
		} // end of if(<0)

		// minute
		i = sub.indexOf(":") + 1;
		sub = sub.substring(i);
		if (sub.indexOf(":") < 0) {
			minute = sub.trim();
			if (minute.length() <= 0) {
				return year + "-" + month + "-" + day + " " + hour + ":" + "00"
						+ ":" + second;
			}
			if (minute.length() <= 2) {
				minute = sub.trim();
				if (!CommonUtil.isNumberString(minute, 2)) {
					return null;
				}
				minute = CommonUtil.setChar(minute, '0', 2);
				return year + "-" + month + "-" + day + " " + hour + ":"
						+ minute + ":" + second;
			} else {
				return null;
			}
		} else {
			minute = sub.substring(0, sub.indexOf(":")).trim();
			if (minute.length() <= 2) {
				minute = minute.trim();
				if (!CommonUtil.isNumberString(minute, 2)) {
					return null;
				}
				minute = CommonUtil.setChar(minute, '0', 2);
			} else {
				return null;
			}
		} // end of if(<0)

		// second
		i = sub.indexOf(":") + 1;
		sub = sub.substring(i);
		second = sub.trim();
		if (second.length() <= 0) {
			return year + "-" + month + "-" + day + " " + hour + ":" + minute
					+ ":" + "00";
		}
		if (second.length() <= 2) {
			second = sub.trim();
			if (!CommonUtil.isNumberString(second, 2)) {
				return null;
			}
			second = CommonUtil.setChar(second, '0', 2);
			return year + "-" + month + "-" + day + " " + hour + ":" + minute
					+ ":" + second;
		} else {
			return null;
		}
	}

	/**
	 * Validate a date string format HH:MM:SS
	 * 
	 * @param strDate
	 *            a string representing date
	 * @return boolean true if format is "HH:MM:SS" else return false
	 */
	static public boolean isValidTime(String strTime) {
		try {
			return strToTime(strTime) != null;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * convert a string with format hh:mm:ss to a java.sql.Date object For
	 * Example : Date String "12:00:00"
	 * 
	 * @param dateString
	 *            format "HH:MM:SS"
	 * 
	 * @return java.sql.Time type object
	 */
	@SuppressWarnings("deprecation")
	static public java.sql.Time strToTime(String str) {
		String dtstr = str;

		dtstr = dtstr.replace('/', '-');
		while (dtstr.indexOf("  ") >= 0) {
			dtstr = dtstr.substring(0, dtstr.indexOf("  "))
					+ dtstr.substring(dtstr.indexOf("  ") + 1);
		}

		if (dtstr == null || dtstr.length() < 3)
			return null;

		// int len =str.length();
		StringBuffer hour = new StringBuffer("0");
		dtstr = dtstr.trim();
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			hour.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}

		StringBuffer minute = new StringBuffer("0");
		dtstr = dtstr.trim().length() > 0 ? dtstr.trim().substring(1) : "";
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			minute.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}
		StringBuffer second = new StringBuffer("0");
		dtstr = dtstr.trim().length() > 0 ? dtstr.trim().substring(1) : "";
		while (!dtstr.equals("") && !dtstr.startsWith("-")
				&& !dtstr.startsWith(":") && !dtstr.startsWith(" ")) {
			second.append(dtstr.charAt(0));
			dtstr = dtstr.trim().substring(1);
		}
		int ihour = Integer.parseInt(hour.toString());
		int iminute = Integer.parseInt(minute.toString());
		int isecond = Integer.parseInt(second.toString());
		java.sql.Time sTime = new java.sql.Time(ihour, iminute, isecond);
		return sTime;
	}

	/**
	 * translate a date string such as 2000-9-13 to a array of string
	 * 
	 * @param String
	 *            src the date string
	 * @param char sepChar a separate character in the date string such as '-'
	 *        ':' or '/' and so on
	 * @return an array of string
	 */
	static public String[] split(String src, char sepChar) {
		Vector<String> v = new Vector<String>();
		// char sepChar = ';' ;
		if (src == null || src.trim().equals("")) {
			return null;
		} else {

			String s = src;
			String elm = null;
			int p = -1;

			while (s.length() > 0) {
				p = s.indexOf(sepChar);
				if (p >= 0) {
					elm = s.substring(0, p);
					v.addElement(elm);
					s = s.substring(p + 1);
				} else {
					if (s != null) {
						v.addElement(s);
					}
					s = "";
				}
			}
			String rtn[] = new String[v.size()];
			Enumeration<String> enum11 = v.elements();
			int i = 0;
			while (enum11.hasMoreElements()) {
				Object item = enum11.nextElement();
				rtn[i] = (String) item;
				i++;
			}
			return rtn;
		}

	}

	public static String[] split(String str, String separator) {
		StringTokenizer tok = null;
		int max = -1;
		if (separator == null) {
			// Null separator means we're using StringTokenizer's default
			// delimiter, which comprises all whitespace characters.
			tok = new StringTokenizer(str);
		} else {
			tok = new StringTokenizer(str, separator);
		}

		int listSize = tok.countTokens();
		if (max > 0 && listSize > max) {
			listSize = max;
		}

		String[] list = new String[listSize];
		int i = 0;
		int lastTokenBegin = 0;
		int lastTokenEnd = 0;
		while (tok.hasMoreTokens()) {
			if (max > 0 && i == listSize - 1) {
				String endToken = tok.nextToken();
				lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
				list[i] = str.substring(lastTokenBegin);
				break;
			} else {
				list[i] = tok.nextToken();
				lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
				lastTokenEnd = lastTokenBegin + list[i].length();
			}
			i++;
		}
		return list;
	}

	/**
	 * translate a date string array to a string such as "aaa, bbb, ccc, ddd"
	 * 
	 * @param String
	 *            [] strs
	 * @return an string
	 */
	static public String arrayToString(String[] strs, char splt) {
		StringBuffer rtn = new StringBuffer();
		if (strs != null) {
			for (int i = 0; i < strs.length; i++) {
				if (i < strs.length) {
					rtn.append(strs[i]).append(splt);
				} else {
					rtn.append(strs[i]);
				}
			}
		}

		return rtn.toString();
	}

	/**
   *
   */

	static public String strToPathfrmt(String str) {
		StringBuffer s = new StringBuffer();

		int pos = 0;
		pos = str.indexOf("\\");

		while (pos > 0) {

			s.append(str.substring(0, pos)).append("\\\\");

			str = str.substring(pos + 1);
			CommonUtil.printDebugInfo(str);
			pos = str.indexOf("\\");
		}

		s.append(str);

		return s.toString();

		// str.indexOf("\\")
	}

	/**
	 * translate a date string such as 2000-9-13 to a array of int
	 * 
	 * @param String
	 *            str the date string
	 * @return an array of int
	 */
	static public int[] getDateParts(String str) {
		String[] adstr = split(str, '-');
		int[] datePart = new int[6];

		int i;
		for (i = 0; i < datePart.length; i++) {
			datePart[i] = Integer.parseInt(adstr[i]);
		}
		return datePart;
	}

	/**
	 * Sort a array of string
	 * 
	 * @param String
	 *            [] strs An array of String
	 * @return String[] the sorted array
	 */
	public static String[] sort(String[] strs) {
		int i = 0, j = 1, len = strs.length;
		if (len <= 1) {
			return strs;
		}

		String strTmp = null;
		for (i = 0; i < len - 1; i++) {
			for (j = i + 1; j < len; j++) {
				if (strs[i].compareTo(strs[j]) > 0) {
					strTmp = strs[i];
					strs[i] = strs[j];
					strs[j] = strTmp;
				}
			}
		}
		return strs;
	}

	/**
	 * GET A date string with format "yyyy-mm-dd hh:mm:ss" of current time
	 * 
	 * @return string For example "yyyy-mm-dd hh:mm:ss"
	 */
	static public String getCurrDateStr() {
		Date date = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH) + 1;
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":"
				+ second;
	}

	/**
	 * translante an int to 人民币大写形式
	 * 
	 * @param int money , money want to translate
	 * @return String[] the return array . 从"分" 到 "亿"
	 **/
	public static String[] moneyToRMB(long money) {
		String[] returnStr = { "", "", "", "", "", "", "", "", "", "" };
		int i, j, index;
		long mod, result;
		String[] RMB = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };

		// for (i = 0; i < 10; i++) {
		// try {
		// byte[] byteTmp = RMB[i].getBytes("GB2312");
		// RMB[i] = String.valueOf(byteTmp, "8859_1");
		// }
		// catch (Exception e) {
		// CommonUtil.printDebugInfo(" CommonUtil.java.moneyToRMB() error" +
		// e.toString());
		// e.printStackTrace();
		// }
		// }

		for (i = 0; i < 10; i++) {
			mod = 1;
			for (j = 0; j < (9 - i); j++) {
				mod = mod * 10;

			}
			result = money / mod;
			Long tmp = Long.valueOf(result);
			index = tmp.intValue();

			if (result == 0) {
				returnStr[9 - i] = RMB[0];
			} else {
				try {
					returnStr[9 - i] = RMB[index];
					money = money - mod * result;
				} catch (Exception e) {
					CommonUtil
							.printDebugInfo(" CommonUtil.java.moneyToRMB() error"
									+ e.toString());
					e.printStackTrace();
				}
			}
		}

		return returnStr;
	}

	/**
	 * user char 'c' padding befoer String str , make it length fixed
	 * 
	 * @param String
	 *            str : the string want to padding
	 * @param int length : the length after padding
	 * @param char c : which char to padding
	 * @return String : the string after padding
	 **/
	public static String padding(String str, int length, char c) {
		int i, len;
		len = length - str.length();

		for (i = 0; i < len; i++) {
			str = c + str;
		}
		return str;
	}

	/**
	 * convert gb2312 string to 8859-1 string
	 * 
	 * @param String
	 *            str : the string want to convert
	 * @return String : the string after convert
	 * 
	 **/

	public static String gbTo8859(String str) {
		// try {
		// byte[] byteTmp = str.getBytes("GB2312");
		// str = String.valueOf(byteTmp, "8859_1");
		// }
		// catch (Exception e) {
		// CommonUtil.printDebugInfo(" converting gb2312 to 8859_1 error" +
		// e.toString());
		// e.printStackTrace();
		// }
		return str;
	}

	/**
	 * convert 8859-1 string to GB2312 string
	 * 
	 * @param String
	 *            str : the string want to convert
	 * @return String : the string after convert
	 * 
	 **/

	public static String toGB(String str) {
		// try {
		// byte[] byteTmp = str.getBytes("ISO8859_1");
		// str = String.valueOf(byteTmp, "GB2312");
		// }
		// catch (Exception e) {
		// CommonUtil.printDebugInfo(" converting  8859_1 to gb2312  error" +
		// e.toString());
		// e.printStackTrace();
		// }
		return str;
	}

	public static double twoPrecision(double d) {
		long l = (long) (d * 100 + 0.5);
		return (float) (l / (double) 100);
	}

	public static float twoPrecision(float f) {
		long l = (long) (f * 100 + 0.5);
		return (float) (l / (double) 100);
	}

	public static double iPrecision(double d, int i) {
		long l = (long) (d * (10 ^ i) + 0.5);
		return l / 10 ^ i;
	}

	/**
	 * Print Debug Info .If You complite program and debug over , You can commit
	 * the System.out.print() sentence. then the compilier will skip this
	 * options.
	 */
	public static void printDebugInfo(Object obj) {
	}

	public static String native2Unicode(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		byte[] buffer = new byte[s.length()];
		for (int i = 0; i < s.length(); i++) {
			buffer[i] = (byte) s.charAt(i);
		}
		return String.valueOf(buffer);
	}

	public static String unicode2Native(String s) {
		if (s == null || s.length() == 0) {
			return null;
		}
		char[] buffer = new char[s.length() * 2];
		char c;
		int j = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= 0x100) {
				c = s.charAt(i);
				byte[] buf = ("" + c).getBytes();
				buffer[j++] = (char) buf[0];
				buffer[j++] = (char) buf[1];
			} else {
				buffer[j++] = s.charAt(i);
			}
		}
		return String.valueOf(buffer, 0, j);
	}

	public static String encodeToMD5(String str) {
		if (str == null) {
			return null;
		}
		StringBuffer digstr = new StringBuffer();
		MessageDigest MD = null;
		try {
			MD = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] oldbyte = new byte[str.length()];
		for (int i = 0; i < str.length(); i++) {
			oldbyte[i] = (byte) str.charAt(i);
		}
		if (MD != null) {
			MD.update(oldbyte);
			byte[] newbyte = null;
			newbyte = MD.digest(oldbyte);
			for (int i = 0; i < newbyte.length; i++) {
				digstr.append(newbyte[i]);
			}
		}
		return digstr.toString();
	}

	public static String foldString(String src, int length) {
		if (src == null || src.equals("") || length <= 0) {
			return src;
		}
		StringBuffer rtn = new StringBuffer();
		int pos = 0;
		while (pos <= src.length()) {
			if (pos > 0) {
				rtn.append("\n");
			}
			rtn.append(src.substring(pos, pos + length <= src.length() ? pos
					+ length : src.length()));
			pos += length;
		}
		return rtn.toString();

	}

	public static String replaceCharacter(String str) {
		if (str != null) {
			// String tmp = str.replace('\"','\'');
			// tmp = tmp.replace('<','[');
			// tmp = tmp.replace('>',']');
			// tmp = tmp.replace('&','~');
			String tmp = str.replaceAll("&", "@amp;");
			tmp = tmp.replaceAll("\"", "@quot;");
			tmp = tmp.replaceAll("<", "@lt;");
			tmp = tmp.replaceAll(">", "@gt;");
			// tmp = tmp.replaceAll("'", "@#146;");
			// tmp = tmp.replaceAll(" ", "@nbsp;");
			return tmp;
		}
		return "";
	}

	public static String undoReplaceCharacter(String str) {
		if (str != null) {
			// String tmp = str.replace('[','<');
			// tmp = tmp.replace(']','>');
			// tmp = tmp.replace('~','&');

			String tmp = str.replaceAll("@amp;", "&");
			tmp = tmp.replaceAll("@quot;", "\"");
			tmp = tmp.replaceAll("@lt;", "<");
			tmp = tmp.replaceAll("@gt;", ">");

			// tmp = tmp.replaceAll("@#146;", "'");
			// tmp = tmp.replaceAll("@nbsp;", " ");
			// tmp = tmp.replaceAll("&#13;", "\n");
			// tmp = tmp.replaceAll("&#10;", "\n");
			return tmp;
		}
		return "";
	}

	static public boolean compareTime(String beforeDateTime,
			String afterDateTime) {
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// long timeCount=0;
		try {
			Date d1 = d.parse(beforeDateTime);
			Date d2 = d.parse(afterDateTime);

			return d2.after(d1);

		} catch (ParseException e) {
			return false;
		}
	}

	static public double converStrToDouble(String str) {
		str = str.trim();
		if (str.indexOf(",") > 0)
			str = str.replaceAll(",", "");
		if (str.indexOf("-") > 0)
			str = "-" + str.replaceAll("-", "");

		return Double.parseDouble(str);
	}

	public static void main(String[] args) {
		/*
		 * log.info(strToDate("2002-03-04 5:6:7")); String[] tst =
		 * split("111;222;333;444;555",';'); for (int i = 0; i < tst.length;
		 * i++) { log.info("str->"+tst[i]); }
		 * log.info("array->"+arrayToString(tst,';')); tst =
		 * split("111;222;333;444;555",';'); for (int i = 0; i < tst.length;
		 * i++) { log.info("str->"+tst[i]); }
		 * 
		 * 
		 * 
		 * String t1="2005-3-25 16:49:30:345"; String t2 =
		 * "2005-3-25 16:49:31:362";
		 */
	}

}
