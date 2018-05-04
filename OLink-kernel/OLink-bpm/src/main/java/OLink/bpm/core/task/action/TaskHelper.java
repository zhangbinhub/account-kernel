package OLink.bpm.core.task.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskConstants;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.timer.TimerRunner;

public class TaskHelper extends BaseHelper<Task> {
	
	/**
	 * 默认构造方法
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public TaskHelper() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(TaskProcess.class));
	}

	private static Collection<String> timelist = new ArrayList<String>();

	public String getStartupName(int startupType) { // 启动类型名称的列表
		String name = "";
		switch (startupType) {
		case TaskConstants.STARTUP_TYPE_MANUAL:
			name = "{*[Manual]*}";
			break;
		case TaskConstants.STARTUP_TYPE_AUTO:
			name = "{*[Auto]*}";
			break;
		case TaskConstants.STARTUP_TYPE_BANNED:
			name = "{*[Banned]*}";
			break;
		default:
			break;
		}
		return name;
	}

	public Collection<String> getTimeList() {
		int date = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int hour = date;

		for (int i = 0; i < hour; i++) {

			timelist.add(i + ":00");
			timelist.add(i + ":30");
		}

		return timelist;
	}

	public Collection<Integer> getHourList() {
		Collection<Integer> hourList = new ArrayList<Integer>();
		for (int i = 1; i <= 24; i++) {
			hourList.add(Integer.valueOf(i));
		}
		return hourList;
	}

	public Collection<Integer> getMinuteList() {
		Collection<Integer> minuteList = new ArrayList<Integer>();
		for (int i = 0; i <= 60; i++) {
			minuteList.add(Integer.valueOf(i));
		}
		return minuteList;
	}

	public Collection<Integer> getSecondList() {
		Collection<Integer> secondList = new ArrayList<Integer>();
		for (int i = 0; i <= 60; i++) {
			secondList.add(Integer.valueOf(i));
		}
		return secondList;
	}

	public Map<Integer, String> getDayOfWeekList() {
		Map<Integer, String> rtn = new LinkedHashMap<Integer, String>();

		rtn.put(Integer.valueOf(1), "{*[Sunday]*}");
		rtn.put(Integer.valueOf(2), "{*[Monday]*}");
		rtn.put(Integer.valueOf(3), "{*[Tuesday]*}");
		rtn.put(Integer.valueOf(4), "{*[Wednesday]*}");
		rtn.put(Integer.valueOf(5), "{*[Thursday]*}");
		rtn.put(Integer.valueOf(6), "{*[Friday]*}");
		rtn.put(Integer.valueOf(7), "{*[Saturday]*}");

		return rtn;
	}

	public Collection<Integer> getDayOfMonthList() {
		Collection<Integer> rtn = new ArrayList<Integer>();
		for (int i = 1; i <= 31; i++) {
			rtn.add(Integer.valueOf(i));
		}

		return rtn;
	}

	public static String getPeriodName(int periodType) {
		return TaskConstants.getPeriodName(periodType);
	}

	public static String getPeriodName(Object periodType) {
		if (periodType instanceof Integer) {
			int type = ((Integer) periodType).intValue();
			return getPeriodName(type);
		}

		return "";
	}

	public static String getStateName(String taskid) { // 状态名称
		Collection<Task> runningList = TimerRunner.runningList.keySet();
		for (Iterator<Task> iter = runningList.iterator(); iter.hasNext();) {
			Task runningTask = iter.next();
			if (runningTask.getId().equals(taskid)) {
				return TaskConstants.RUNNING;
			}
		}

		return TaskConstants.STOPPING;
	}
}
