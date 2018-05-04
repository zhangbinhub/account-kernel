package OLink.bpm.core.workcalendar.standard.ejb;

import java.util.Calendar;
import java.util.Date;

/**
 * 时段, 用于记录一天类工作时段
 * 
 * @author Nicholas
 * 
 */
public class DayPart {
	int fromHour = -1;
	int fromMinute = -1;
	int toHour = -1;
	int toMinute = -1;
//	int index = -1;

	public int getFromHour() {
		return fromHour;
	}

	public void setFromHour(int fromHour) {
		this.fromHour = fromHour;
	}

	public int getFromMinute() {
		return fromMinute;
	}

	public void setFromMinute(int fromMinute) {
		this.fromMinute = fromMinute;
	}

	public int getToHour() {
		return toHour;
	}

	public void setToHour(int toHour) {
		this.toHour = toHour;
	}

	public int getToMinute() {
		return toMinute;
	}

	public void setToMinute(int toMinute) {
		this.toMinute = toMinute;
	}

	/**
	 * 是否为此时间的开始时段
	 * 
	 * @param date
	 * @return
	 */
	public boolean isStartAfter(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		return (hour < fromHour) || ((hour == fromHour) && (minute < fromMinute));
	}

	public boolean isEnd(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		return ((hour >= toHour) || ((hour == toHour) && (minute >= toMinute)));
	}

	/**
	 * 是否在此时段内
	 * 
	 * @param date
	 * @return
	 */
	public boolean includes(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		return (((fromHour < hour) || ((fromHour == hour) && (fromMinute <= minute))) && ((hour < toHour) || ((hour == toHour) && (minute <= toMinute))));
	}

	public Date getStartTime(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, fromHour);
		calendar.set(Calendar.MINUTE, fromMinute);
		return calendar.getTime();
	}

	/**
	 * 获取此时段的剩余时间
	 * 
	 * @param date
	 *            日期
	 * @return
	 */
	public int getRemainingMinutes(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int fromHour = calendar.get(Calendar.HOUR_OF_DAY);
		int fromMinute = calendar.get(Calendar.MINUTE);

		int toHour = getToHour();
		int toMinute = getToMinute();

		return (toHour - fromHour) * 60 + (toMinute - fromMinute);
	}

	public String toString() {
		return fromHour + ":" + fromMinute + "-" + toHour + ":" + toMinute;
	}
}
