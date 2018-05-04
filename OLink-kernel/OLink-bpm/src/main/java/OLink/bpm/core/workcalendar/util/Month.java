package OLink.bpm.core.workcalendar.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import OLink.bpm.core.workcalendar.special.ejb.SpecialDayVO;
import OLink.bpm.core.workcalendar.standard.ejb.BaseDay;
import OLink.bpm.core.workcalendar.standard.ejb.DayPart;

/**
 * 工作月
 * 
 * @author Chris
 * 
 */
public class Month implements java.io.Serializable {
	
	private static final long serialVersionUID = -4573326455169439326L;

	private BaseDay[][] days;

	private int monthIndex;

	private int year;

	public BaseDay[][] getDays() {
		if (this.days == null) {
			this.days = new BaseDay[6][7];
		}
		return this.days;
	}

	public void setDays(BaseDay[][] days) {
		this.days = days;
	}

	public String toHtml() {
		StringBuffer htmlBuilder = new StringBuffer();
		htmlBuilder
				.append("<div class='WdateDiv'><table id='Calendar1' class='WdayTable' width='100%' height='100%' border='1'  bordercolor='#FFFFFF' cellpadding='0' cellspacing='0' align='center' valign='middle'><tr><td></td></tr>");
		htmlBuilder
				.append("<tr align='center' class='MTitle'><th><font color='red'>{*[SUN]*}</font></th><th>{*[MON]*}</th><th>{*[TUE]*}</th>");
		htmlBuilder
				.append("<th>{*[WED]*}</th><th>{*[THU]*}</th><th>{*[FRI]*}</th><th><font color='green'>{*[SAT]*}</font></th></tr>");

		BaseDay[][] days = getDays();
		for (int i = 0; days != null && i < 6; i++) {
			if (!(i == 5 && (days[i][0] == null || days[i][0].getDayIndex() == 0))) {
				htmlBuilder.append("<tr>");
				for (int j = 0; days[i] != null && j < 7; j++)
					if (days[i][j] != null) {
						if (days[i][j] instanceof SpecialDayVO) {
							days[i][j].setSpc(2);
							htmlBuilder.append(days[i][j].toHtml());

						} else {
							htmlBuilder.append(days[i][j].toHtml());
						}
					}

				htmlBuilder.append("</tr>");
			}
		}
		htmlBuilder.append("</table></div>");
		return htmlBuilder.toString();
	}

	public int countWorkingDays() {
		return countWorkingDays(1);
	}

	public int countWorkingDays(int start) {
		Calendar thisMonth = getThisMonth();
		return countWorkingDays(start, thisMonth.getActualMaximum(Calendar.DAY_OF_MONTH));
	}

	public int countWorkingDays(int start, int end) {
		Calendar thisMonth = getThisMonth();

		int firstIndex = thisMonth.get(Calendar.DAY_OF_WEEK) - 1;
		int day;
		int count = 0;
		BaseDay[][] standingDays = getDays();
		for (int i = 0; standingDays != null && i < standingDays.length; i++)
			for (int j = 0; standingDays[i] != null && j < standingDays[i].length; j++) {
				day = (i * 7 + j) - firstIndex + 1;
				if (day < start)
					continue;
				else if (day <= end && standingDays[i][j] != null && standingDays[i][j].isWorkingDay()) {
					count++;
				}
			}
		return count;
	}

	public boolean isWorkingDay(Date currentDate) {
		Calendar cld = Calendar.getInstance();
		cld.setTime(currentDate);
		int dayCount = cld.get(Calendar.DAY_OF_MONTH);
		int count = countWorkingDays(dayCount, dayCount);

		return count == 1;
	}

	/**
	 * 根据当前日期获取时段列表
	 * 
	 * @param currentDate
	 *            当前日期
	 * @return
	 * @throws Exception
	 */
	public DayPart[] getDayParts(Date currentDate) throws Exception {
		Collection<DayPart> dayPartList = new ArrayList<DayPart>();

		Calendar thisMonth = getThisMonth();
		Calendar cld = Calendar.getInstance();
		cld.setTime(currentDate);
		int start = cld.get(Calendar.DAY_OF_MONTH);
		int firstIndex = thisMonth.get(Calendar.DAY_OF_WEEK) - 1;
		int day;

		BaseDay[][] standingDays = getDays();
		for (int i = 0; standingDays != null && i < standingDays.length; i++)
			for (int j = 0; standingDays[i] != null && j < standingDays[i].length; j++) {
				day = (i * 7 + j) - firstIndex + 1;
				if (day < start)
					continue;
				else {
					BaseDay standardDay = standingDays[i][j];
					for (int k = 1; k <= 5; k++) {
						DayPart dayPart = new DayPart(); // 时间段
						try {
							// 设置开始时间
							Method method0 = BaseDay.class.getMethod("getStartTime" + k);
							String starttime = (String) method0.invoke(standardDay);
							int[] fromTime = getTime(starttime);

							// 设置结束时间
							Method method1 = BaseDay.class.getMethod("getEndTime" + k);
							String endtime = (String) method1.invoke(standardDay);
							int[] toTime = getTime(endtime);

							if (fromTime != null && toTime != null) {
								dayPart.setFromHour(fromTime[0]);
								dayPart.setFromMinute(fromTime[1]);
								dayPart.setToHour(toTime[0] == 0 ? 24 : toTime[0]);
								dayPart.setToMinute(toTime[1]);
								dayPartList.add(dayPart);
							}
						} catch (Exception e) {
							throw e;
						}
					}
					return dayPartList.toArray(new DayPart[dayPartList.size()]);
				}
			}
		return new DayPart[0];
	}

	/**
	 * 从时间字符串中获取小时和分钟, 0为小时数, 1为分钟数, 如果小时数为0则转为24
	 * 
	 * @param timeStr
	 * @return
	 */
	public int[] getTime(String timeStr) {
		int[] time = new int[2];
		String[] time1 = timeStr.split(":");
		if (time1 != null && time1.length == 2) {
			time[0] = Integer.parseInt(time1[0]);
			time[1] = Integer.parseInt(time1[1]);
			return time;
		}
		return null;
	}

	public double getStartTimeOfDay(Date currentDate) {
		Calendar thisMonth = getThisMonth();
		Calendar cld = Calendar.getInstance();
		cld.setTime(currentDate);
		int start = cld.get(Calendar.DAY_OF_MONTH);
		int firstIndex = thisMonth.get(Calendar.DAY_OF_WEEK) - 1;
		int day;
		BaseDay[][] standingDays = getDays();
		for (int i = 0; standingDays != null && i < standingDays.length; i++)
			for (int j = 0; standingDays[i] != null && j < standingDays[i].length; j++) {
				day = (i * 7 + j) - firstIndex + 1;
				if (day < start)
					continue;
				else {
					int[] time1 = getTime(standingDays[i][j].getStartTime1());
					if (time1 != null && time1.length == 2) {
						return time1[0] + (time1[1] / 60.0);
					} else {
						return 0;
					}
				}
			}
		return 0;
	}

	/**
	 * 根据当前日期获取距离下班的工作时长
	 * 
	 * @param crrDate
	 * @return
	 * @throws Exception
	 */
	public double countRemainingTimes(Date crrDate) throws Exception {
		Calendar cld = Calendar.getInstance();
		cld.setTime(crrDate);
		int date = cld.get(Calendar.DAY_OF_MONTH);
		double hour = cld.get(Calendar.HOUR_OF_DAY) + (cld.get(Calendar.MINUTE) / 60.0);
		cld.setFirstDayOfWeek(Calendar.SUNDAY);
		cld.set(Calendar.DAY_OF_MONTH, 1);
		int firstIndex = cld.get(Calendar.DAY_OF_WEEK) - 1;
		int i = ((date + firstIndex - 1) / 7);
		int j = ((date + firstIndex - 1) % 7);
		BaseDay vo = getDays()[i][j];
		double[] strtime = new double[5];
		double[] endtime = new double[5];
		for (int k = 1; k < 6; k++) {
			try {
				Method method0 = BaseDay.class.getMethod("getStartTime" + k);
				String starttime = (String) method0.invoke(vo);
				int[] fromTime = getTime(starttime);
				if (fromTime != null)
					strtime[k - 1] = fromTime[0] + fromTime[1] / 60.0;
				Method method1 = BaseDay.class.getMethod("getEndTime" + k);
				String edtime = (String) method1.invoke(vo);
				int[] toTime = getTime(edtime);
				if (toTime != null)
					endtime[k - 1] = toTime[0] + toTime[1] / 60.0;
			} catch (Exception e) {
				throw e;
			}
		}

		double rtnTeme = 0;
		for (int k = 4; k >= 0; k--) {
			if (endtime[k] > 0 && hour > endtime[k])
				return rtnTeme;
			else if (strtime[k] > 0 && hour > strtime[k])
				return endtime[k] - hour + rtnTeme;
			rtnTeme += (endtime[k] - strtime[k]);
		}
		return rtnTeme;
	}

	/**
	 * 根据当前日期获取已工作时长
	 * 
	 * @param crrDate
	 * @return
	 * @throws Exception
	 */
	public double countWorkedTimes(Date crrDate) throws Exception {
		Calendar cld = Calendar.getInstance();
		cld.setTime(crrDate);
		int date = cld.get(Calendar.DAY_OF_MONTH);
		double hour = cld.get(Calendar.HOUR_OF_DAY) + (cld.get(Calendar.MINUTE) / 60.0);
		cld.setFirstDayOfWeek(Calendar.SUNDAY);
		cld.set(Calendar.DAY_OF_MONTH, 1);
		int firstIndex = cld.get(Calendar.DAY_OF_WEEK) - 1;
		int i = ((date + firstIndex - 1) / 7);
		int j = ((date + firstIndex - 1) % 7);
		BaseDay vo = getDays()[i][j];

		double[] strtime = new double[5];
		double[] endtime = new double[5];
		for (int k = 1; k < 6; k++) {
			try {
				Method method0 = BaseDay.class.getMethod("getStartTime" + k);
				String starttime = (String) method0.invoke(vo);
				int[] fromTime = getTime(starttime);
				if (fromTime != null)
					strtime[k - 1] = fromTime[0] + fromTime[1] / 60.0;
				Method method1 = BaseDay.class.getMethod("getEndTime" + k);
				String edtime = (String) method1.invoke(vo);
				int[] toTime = getTime(edtime);
				if (toTime != null)
					endtime[k - 1] = toTime[0] + toTime[1] / 60.0;
			} catch (Exception e) {
				throw e;
			}
		}

		double rtnTeme = 0;
		for (int k = 0; k < 5; k++) {
			if (strtime[k] > 0 && hour < strtime[k])
				return rtnTeme;
			else if (endtime[k] > 0 && hour < endtime[k])
				return hour - strtime[k] + rtnTeme;
			rtnTeme += (endtime[k] - strtime[k]);
		}
		return rtnTeme;
	}

	private Calendar getThisMonth() {
		Calendar thisMonth = Calendar.getInstance();
		if (year > 1900)
			thisMonth.set(Calendar.YEAR, year);
		else
			thisMonth.set(Calendar.YEAR, 1901);

		if (monthIndex >= 0 && monthIndex < 12)
			thisMonth.set(Calendar.MONTH, monthIndex);
		else
			thisMonth.set(Calendar.MONTH, 0);

		thisMonth.setFirstDayOfWeek(Calendar.SUNDAY);

		thisMonth.set(Calendar.DAY_OF_MONTH, 1);
		return thisMonth;
	}

	public int getMonthIndex() {
		return monthIndex;
	}

	public int getYear() {
		return year;
	}

	public void setMonthIndex(int monthIndex) {
		this.monthIndex = monthIndex;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
