package OLink.bpm.core.workcalendar.calendar.action;

public class CalendarType {

	public static final int[] TYPES = { 1, 2, 3 };

	public static final String[] NAMES = { "Standard_Calendar",
			"C24_Calendar", "Night_Calendar" };
	
	private static final String[] NAMES_2 = {"标准日历", "24小时日历", "夜班日历", "標準日曆", "24小時日曆", "夜班日曆", "Standard Calendar", "24-hour Calendar", "Night Calendar"};

	public static final String[] DESCRIPTIONS = {
			"core.workcalendar.standard.calendarinfo",
			"core.workcalendar.24.calendarinfo",
			"core.workcalendar.night.calendarinfo" };

	public static final String[] TIMEZONES = { "Eight_TimeZone" };

	public static String getTimezones(int type) {
		if (type == 0) {
			return null;
		}

		for (int i = 0; i < TIMEZONES.length; i++)
			if (type == (TYPES[i]))
				return TIMEZONES[i];
		return null;
	}

	public static String getDescriptions(int type) {
		if (type == 0) {
			return null;
		}

		for (int i = 0; i < DESCRIPTIONS.length; i++)
			if (type == (TYPES[i]))
				return DESCRIPTIONS[i];
		return null;
	}

	public static String getName(int type) {
		if (type == 0) {
			return null;
		}

		for (int i = 0; i < TYPES.length; i++)
			if (type == (TYPES[i]))
				return NAMES[i];
		return null;
	}

	public static int getType(String name) {
		if (name == null)
			return 0;

		for (int i = 0; i < NAMES.length; i++)
			if (name.equals(NAMES[i]))
				return TYPES[i];

		return 0;
	}
	
	public static String getKeyByName(String name) {
		for (int i = 0; i < NAMES_2.length; i++) {
			if (NAMES_2[i].equals(name)) {
				return NAMES[i % 3];
			}
		}
		return name;
	}
	
}
