package OLink.bpm.core.task.ejb;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaskConstants {
	public static final int TASK_TYPE_SCRIPT = 0x0000001;

	//public static final int TASK_TYPE_EXPDATA = 0x0000010;

	public static final String RUNNING = "Running";

	public static final String STOPPING = "Stopped";

	/**手动*/
	public static final int STARTUP_TYPE_MANUAL = 0;
	/**自动*/
	public static final int STARTUP_TYPE_AUTO = 1;
	/**禁止*/
	public static final int STARTUP_TYPE_BANNED = 2;

	public static final int REAPET_TYPE_NOTREAPET = 0;
	
	public static final int ONE_MINUTES = 60 * 1000;
	
	public static final int ONE_HOURS = 60 * ONE_MINUTES;

	public static final int ONE_DAY = 24 * ONE_HOURS;

	public static final int ONE_WEEK = ONE_DAY * 7;

	public static final int ONE_MONTH = ONE_DAY * 31;
	/**每天*/
	public static final int REPEAT_TYPE_DAILY = 0x0000002;
	/**每分*/
	public static final int REPEAT_TYPE_DAILY_MINUTES = 0x0000022;
	/**每时*/
	public static final int REPEAT_TYPE_DAILY_HOURS = 0x0000222;
	/**每周*/
	public static final int REPEAT_TYPE_WEEKLY = 0x0000020;
	/**每月*/
	public static final int REPEAT_TYPE_MONTHLY = 0x0000200;
	/**立刻*/
	public static final int REPEAT_TYPE_IMMEDIATE = 0x0002000;
	/**不重复*/
	public static final int REPEAT_TYPE_NONE = 0;

	public static final int[] REPEAT_TYPE = { REPEAT_TYPE_NONE,
		REPEAT_TYPE_IMMEDIATE,REPEAT_TYPE_DAILY_MINUTES,REPEAT_TYPE_DAILY_HOURS, REPEAT_TYPE_DAILY, REPEAT_TYPE_WEEKLY, REPEAT_TYPE_MONTHLY };

	public static final String[] REAPET_NAMES = { "{*[Do_not_repeat]*}", "{*[Immediately]*}","{*[Minute]*}", "{*[Hour]*}", "{*[Daily]*}",
			"{*[Weekly]*}", "{*[Monthly]*}" };

	public static Map<Integer, String> getRepeatTypeList() {
		Map<Integer, String> rtn = new LinkedHashMap<Integer, String>();

		for (int i = 0; i < REPEAT_TYPE.length; i++) {
			rtn.put(Integer.valueOf(REPEAT_TYPE[i]), REAPET_NAMES[i]);
		}

		return rtn;
	}

	public static String getPeriodName(int periodType) {
		for (int i = 0; i < REPEAT_TYPE.length; i++) {
			if (REPEAT_TYPE[i] == periodType) {
				return REAPET_NAMES[i];
			}
		}
		return "";
	}
}
