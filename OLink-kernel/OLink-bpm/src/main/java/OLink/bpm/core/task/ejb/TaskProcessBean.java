package OLink.bpm.core.task.ejb;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.task.dao.TaskDAO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.DateUtil;
import OLink.bpm.util.timer.TimeRunnerAble;
import OLink.bpm.util.timer.TimerRunner;
import org.apache.commons.beanutils.PropertyUtils;

public class TaskProcessBean extends AbstractDesignTimeProcessBean<Task> implements TaskProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3102485736075605573L;

	protected IDesignTimeDAO<Task> getDAO() throws Exception {
		return (TaskDAO) DAOFactory.getDefaultDAO(Task.class.getName());
	}

	public Collection<Task> doQuery(String application) throws Exception {
		return ((TaskDAO) getDAO()).query(application);
	}

	public Collection<Task> getTaskByModule(String application, String module) throws Exception {
		return ((TaskDAO) getDAO()).getTaskByModule(application, module);
	}

	public void doStart(String rDate, String rTime, Task stopedTask) throws Exception {
		stopedTask.setRunningTime(getRunningTime(rDate, rTime));

		boolean flag = false;

		// 获取所有运行时列表
		Collection<Task> runningList = TimerRunner.runningList.keySet();
		int count = 0;

		for (Iterator<Task> iter = runningList.iterator(); iter.hasNext();) {
			Task task = iter.next();
			if (!(task.getId()).equals(stopedTask.getId())) {
				count++;
				continue;
			}
		}
		if (count == runningList.size() || runningList.size() == 0) {
			flag = true;
		}

		if (flag && stopedTask.getStartupType() != TaskConstants.STARTUP_TYPE_BANNED) {
			TimeRunnerAble job = TimerRunner.createTimeRunnerAble(stopedTask, this);

			TimerRunner.registerTimerTask(job, new Date(), 60 * 1000);

			TimerRunner.runningList.put(stopedTask, job);
		}
	}

	// 设置日期
	private Date getRunningTime(String rDate, String rTime) throws Exception {
		Date date = new Date();
		if (rDate == null) {// 当为每天，每周，每月的时候。选择运行日期的时候
			SimpleDateFormat formater = new SimpleDateFormat();
			formater.applyPattern("yyyy-MM-dd");
			rDate = formater.format(date);
			String dateStr = rDate + " " + rTime;
			date = formater.parse(dateStr);

			return date;
		} else {
			String dateStr = DateUtil.format(date, "yyyy-MM-dd");
			dateStr += " " + rTime;
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			date = formatter.parse(dateStr);

			return date;
		}
	}
	
	@Override
	public void doUpdate(ValueObject vo) throws Exception {
		Task task = (Task) vo;
		task.setModifyTime(new Date());
		super.doUpdate(vo);
	}
	
	@Override
	public void doUpdate(Collection<ValueObject> vos) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			if (vos != null)
				for (Iterator<ValueObject> iter = vos.iterator(); iter.hasNext();) {
					Task vo = (Task) iter.next();
					vo.setModifyTime(new Date());
					ValueObject po = getDAO().find(vo.getId());
					if (po != null) {
						PropertyUtils.copyProperties(po, vo);
						getDAO().update(po);
					} else {
						getDAO().update(vo);
					}
				}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}
	
	@Override
	public void doUpdate(ValueObject vo, WebUser user) throws Exception {
		super.doCreate(vo);
	}
	
	@Override
	public void doUpdate(ValueObject[] vos) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			if (vos != null)
				for (int i = 0; i < vos.length; i++) {
					Task vo = (Task) vos[i];
					vo.setModifyTime(new Date());
					ValueObject po = getDAO().find(vo.getId());
					if (po != null) {
						PropertyUtils.copyProperties(po, vo);
						getDAO().update(po);
					} else {
						getDAO().update(vo);
					}
				}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}
	
}
