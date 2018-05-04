package OLink.bpm.util.timer;

import java.util.TimerTask;

import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskProcess;

public abstract class TimeRunnerAble extends TimerTask {
	
	public boolean cancel(Task task , TaskProcess tp) {
		try {
			tp.doUpdate(task);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.cancel();
	}
}
