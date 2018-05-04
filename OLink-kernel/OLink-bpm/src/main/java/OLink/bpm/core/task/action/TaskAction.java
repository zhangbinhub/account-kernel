package OLink.bpm.core.task.action;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import OLink.bpm.core.deploy.module.ejb.ModuleProcess;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskConstants;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.timer.TimeRunnerAble;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.deploy.module.ejb.ModuleVO;
import OLink.bpm.util.timer.TimerRunner;

public class TaskAction extends BaseAction<Task> {

	private String rTime;

	private String rDate;
	
	//任务运行状态(0表示停止,1表示运行)
	private int runState = 0;

	public int getRunState() {
		return runState;
	}

	public void setRunState(int runState) {
		this.runState = runState;
	}

	private static final long serialVersionUID = 2948439801638083317L;

	private static Map<Integer, String> _TASKTYPE = new TreeMap<Integer, String>();

	private static Map<Integer, String> _STARTUPTYPE = new TreeMap<Integer, String>();

	private static Map<Integer, String> _REAPETTYPE = new TreeMap<Integer, String>();

	static {
		_TASKTYPE.put(Integer.valueOf(TaskConstants.TASK_TYPE_SCRIPT), "{*[Script]*}");

		_STARTUPTYPE.put(Integer.valueOf(TaskConstants.STARTUP_TYPE_MANUAL), "{*[Manual]*}");
		_STARTUPTYPE.put(Integer.valueOf(TaskConstants.STARTUP_TYPE_AUTO), "{*[Auto]*}");
		_STARTUPTYPE.put(Integer.valueOf(TaskConstants.STARTUP_TYPE_BANNED), "{*[Banned]*}");

		_REAPETTYPE = TaskConstants.getRepeatTypeList();
	}

	public static Map<Integer, String> get_TASKTYPE() {
		return _TASKTYPE;
	}

	public static Map<Integer, String> get_STARTUPTYPE() {
		return _STARTUPTYPE;
	}

	public static Map<Integer, String> get_REAPETTYPE() {
		return _REAPETTYPE;
	}

	/**
	 * 默认构造方法
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public TaskAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(TaskProcess.class), new Task());
	}

	/**
	 * 保存运行时所以的数据
	 */
	public String doSave() {
		try {
			this.setDate();
			Task task = getTask();
			task.setModifyTime(new Date());
			if (task.getStartupType() == TaskConstants.STARTUP_TYPE_BANNED) {
				this.stopTask();
			}
			return super.doSave();
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	/**
	 * 保存并新建
	 */
	public String doSaveAndNew() {
		try {
			Task task = getTask();
			task.setModifyTime(new Date());
			if (task.getName().equals("")) {
				throw new Exception(
						"{*[page.name.notexist]*}");
			}
			this.setDate();
			if (task.getStartupType() == TaskConstants.STARTUP_TYPE_BANNED) {
				this.stopTask();
			}

			String msg = super.doSave(); 
			task.setId(null);
			task.setName("");
			task.setRunningTime(null);
			this.setRDate("");
			this.setRTime("");
			this.setContent(task);
			return msg;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	public String doEdit() {
		try {
			Map<?, ?> params = getContext().getParameters();

			String id = ((String[]) params.get("id"))[0];
			Task bi = (Task) process.doView(id);
			// 获取所有运行时列表
			Collection<Task> runningList = TimerRunner.runningList.keySet();
			if(runningList != null){
			for (Iterator<Task> iter = runningList.iterator(); iter.hasNext();) {
				Task task = iter.next();
				if ((task.getId()).equals(id)) {
					this.setRunState(1);
					break;
				}
			}
			}
			setContent(bi);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}
	
	/**
	 * 开始运行
	 * 
	 * @return
	 * @throws Exception
	 */

	public String doStart() throws Exception {
		try {
			this.setDate();
			this.startTask();
			this.setRunState(1);
			addActionMessage("{*[core.task.started]*}");
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	/**
	 * 结束
	 * 
	 * @return
	 * @throws Exception
	 */
	public String doStop() throws Exception {
		try {
			this.setDate();
			this.stopTask();
			addActionMessage("{*[Stopped]*} {*[Successful]*}");
			return SUCCESS;
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	private void startTask() throws Exception {
		boolean flag = false;

		// 获取所有运行时列表
		Collection<Task> runningList = TimerRunner.runningList.keySet();
		int count = 0;

		for (Iterator<Task> iter = runningList.iterator(); iter.hasNext();) {
			Task task = iter.next();
			if (!(task.getId()).equals(getTask().getId())) {
				count++;
				continue;
			}
		}
		if (count == runningList.size() || runningList.size() == 0) {
			flag = true;
		}
		
		int taskType = getTask().getStartupType();
		if (flag && taskType != TaskConstants.STARTUP_TYPE_BANNED) {
			TimeRunnerAble job = TimerRunner.createTimeRunnerAble(getTask(), (TaskProcess) this.process);

			TimerRunner.registerTimerTask(job, new Date(), 60 * 1000);

			TimerRunner.runningList.put(getTask(), job);
		} else {
			throw new Exception("{*[core.task.error.runing]*}");
		}
	}
	
	private void stopTask() {
		Set<Task> runningList = TimerRunner.runningList.keySet();
		this.setRunState(0);
		for (Iterator<Task> iter = runningList.iterator(); iter.hasNext();) {
			Task task = iter.next();
			// 以id判断runningList的task与当前task是否相等
			if ((task.getId()).equals(getTask().getId())) {
				Object obj = TimerRunner.runningList.get(task);
				long delay = 1000L;
				if (obj != null && delay > 0) {
					TimerRunner.runningList.remove(task);
					TimeRunnerAble job = (TimeRunnerAble) obj;
					job.cancel(task, (TaskProcess) this.process);
					break;
				}
			}
		}
	}

	// 设置日期
	private void setDate() throws Exception {
		Date date = new Date();
		try {
			int period = getTask().getPeriod();
			if (period == TaskConstants.REPEAT_TYPE_DAILY
					|| period == TaskConstants.REPEAT_TYPE_WEEKLY
					|| period == TaskConstants.REPEAT_TYPE_MONTHLY) {
				// 当为每天，每周，每月的时候。选择运行日期的时候
				SimpleDateFormat formater = new SimpleDateFormat();
				formater.applyPattern("HH:mm:ss");
				if (rTime.equals("")) {
					throw new Exception("{*[core.task.choosetime]*}");
				}
				String dateStr = rTime;
				date = formater.parse(dateStr);
			} else if (period == TaskConstants.REPEAT_TYPE_NONE) {
				// 当为不重复的时候。
				if (rDate.equals("")) {
					throw new Exception(
							"{*[Please]*}{*[Select]*}{*[Running]*}{*[Time]*}");
				} else if (rTime.equals("")) {
					throw new Exception("{*[core.task.choosetime]*}");
				}
				String dateStr = rDate + " " + rTime;
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				date = formatter.parse(dateStr);
			} else if (period == TaskConstants.REPEAT_TYPE_DAILY_MINUTES
					|| period == TaskConstants.REPEAT_TYPE_DAILY_HOURS) {
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				date = formatter.parse(formatter.format(date));
			} else {
				// 当为立即的时候
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				date = formatter.parse(formatter.format(date));
			}
		} catch (Exception e) {
			addFieldError("1", e.getMessage());
			throw e;
		}
		// 把时间放入到运行的那里去
		getTask().setRunningTime(date);
	}

	private Task getTask() {
		return ((Task) this.getContent());
	}

	public Collection<Integer> get_dayOfWeek() {
		Task vo = (Task) getContent();
		return vo != null ? vo.getDaysOfWeek() : null;
	}

	public void set_dayOfWeek(Collection<String> _dayOfWeek) {
		((Task) getContent()).getDaysOfWeek().clear();
		for (Iterator<String> iterator = _dayOfWeek.iterator(); iterator.hasNext();) {
			String day = iterator.next();
			((Task) getContent()).getDaysOfWeek().add(Integer.valueOf(day));
		}
	}

	public String getRTime() {
		return rTime;
	}

	public void setRTime(String time) {
		rTime = time;
	}

	public String getRDate() {
		return rDate;
	}

	public void setRDate(String date) {
		rDate = date;
	}

	/**
	 * 返回所属模块主键(module id)
	 * 
	 * @return 所属模块主键(module id)
	 */
	public String get_moduleid() {
		Task task = (Task) getContent();
		if (task.getModule() != null) {
			return task.getModule().getId();
		}
		return null;
	}

	/**
	 * Set模块主键(module id)
	 * 
	 * @param _moduleid
	 *            模块主键
	 */
	public void set_moduleid(String _moduleid) {
		Task task = (Task) getContent();
		if (_moduleid != null) {
			ModuleProcess mp;
			try {
				mp = (ModuleProcess) ProcessFactory.createProcess((ModuleProcess.class));
				ModuleVO module = (ModuleVO) mp.doView(_moduleid);
				task.setModule(module);
			} catch (Exception e) {
			}
		}
	}

}
