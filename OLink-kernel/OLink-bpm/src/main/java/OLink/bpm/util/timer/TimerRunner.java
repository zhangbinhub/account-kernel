package OLink.bpm.util.timer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskConstants;
import OLink.bpm.core.task.ejb.TaskProcess;

public class TimerRunner {
	public final static Map<Task, TimeRunnerAble> runningList = new HashMap<Task, TimeRunnerAble>();

	private static Timer timer = new Timer();

	// public TimerRunner(TimeRunnerAble aRunnerable) {
	//
	// }
	//
	public static void registerTimerTask(TimeRunnerAble runnerAble,
			Date firstTime, long period) {
		timer.schedule(runnerAble, firstTime, period);
	}

	public static void registerTimerTask(TimeRunnerAble runnerAble, long delay,
			long period) {
		timer.schedule(runnerAble, delay, period);
	}

	public static void unregisterAllTimerTask() {
		timer.cancel();
		timer = null;
	}

	// 注册自动服务
	public static void registerJSService(String application) {
		try {
			IRunner runner = JavaScriptFactory.getInstance(null, application);
			runner.initBSFManager(null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static TimeRunnerAble createTimeRunnerAble(final Task task,
			final TaskProcess tp) {

		return new TimeRunnerAble() {
			int timesCount = 0;

			int currTotal = task.getTotalRuntimes();

			public void run() {
				String terminateScript = task.getTerminateScript(); // 停止条件
				int total = currTotal;

				// 达到运行次数则停止
				if (task.getRuntimes() == timesCount
						&& task.getPeriod() == TaskConstants.REAPET_TYPE_NOTREAPET) {
					this.cancel(task, tp);
					TimerRunner.runningList.remove(task);
				}

				// 符合停止条件则停止
				try {
					registerJSService(task.getApplicationid());

					StringBuffer label = new StringBuffer();
					label.append("Task(").append(task.getId()).append(
							")." + task.getName()).append(".TerminateScript");

					IRunner runner = JavaScriptFactory.getInstance(null, task
							.getApplicationid());
					Object terminateObj = runner.run(label.toString(),
							terminateScript);
					if (terminateObj instanceof Boolean) {
						if (((Boolean) terminateObj).booleanValue()) {
							this.cancel(task, tp);
							TimerRunner.runningList.remove(task);
						}
					}
					if (task.isExecuteAble()) {
						timesCount++;

						//SessionSignal signal = PersistenceUtils
								//.getSessionSignal();
						// signal.sessionSignal++;
						try {
							task.execute();

							total += timesCount;
							task.setTotalRuntimes(total);

							tp.doUpdate(task);
							
						} catch (Exception e) {
							e.printStackTrace();
						} finally {

							// signal.sessionSignal--;
							// if (signal.sessionSignal <= 0) {
							try {
								PersistenceUtils.closeSession();
							} catch (Exception e) {
							}
							// }
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
}
