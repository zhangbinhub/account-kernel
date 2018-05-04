package OLink.bpm.core.workflow.notification.ejb;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.user.ejb.BaseUser;

/**
 * 待办超时通知
 * 
 * @author Nicholas
 * 
 */
public class OverDueNotification extends Notification {
	protected Date curDate; // 当前日期

	protected Date deadline; // 最后限期

	protected boolean isnotifysuperior; // 是否通知上级

	protected int timeunit; // 时间单位

	protected double limittimecount; // 限制的时间数(根据时间单位的不同而有所变化)

	/**
	 * 构造方法
	 * 
	 * @param applicationid
	 *            应用标识
	 */
	public OverDueNotification(String applicationid) {
		this.applicationid = applicationid;
	}

	/**
	 * 发送信息
	 */
	public void send() throws Exception {
		if (sendModes == null) {
			return;
		}

		if (responsibles != null && !responsibles.isEmpty()) {
			for (Iterator<BaseUser> iterator = responsibles.iterator(); iterator
					.hasNext();) {
				UserVO responsible = (UserVO) iterator.next();
				if (responsible != null
						&& isOverDue(deadline, responsible.getLevel())) {
					send(getLastResponsible(deadline, responsible));
				}
			}
		}
	}

	/**
	 * 获取最终要通知的人，为当前用户或其上级
	 * 
	 * @param deadline
	 *            过期日间
	 * @param user
	 *            用户对象
	 * @return 用户对象
	 */
	private UserVO getLastResponsible(Date deadline, UserVO user) {
		long overTime = curDate.getTime() - deadline.getTime();
		if (overTime >= 0 && overTime <= NotificationConstant.JOB_PEIROD) {
			return user;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(deadline);
		addDuration(calendar);

		if (user.getLevel() != 0) {
			return getLastResponsible(calendar.getTime(), user.getSuperior());
		}

		return null;
	}

	/**
	 * 是否执行通知
	 * 
	 * @param deadline
	 *            最后限期
	 * @param level
	 *            用户等级
	 * @return
	 */
	public boolean isOverDue(Date deadline, int level) {
		// 超时30分钟内发通知
		long overTime = curDate.getTime() - deadline.getTime();
		if (overTime >= 0 && overTime <= NotificationConstant.JOB_PEIROD) {
			return true;
		}

		// 如果通知上级，则deadline+limitTimeCount通知上一级，deadline+limitTimeCount*2通知上上级
		if (isnotifysuperior) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(deadline);
			addDuration(calendar);

			if (level != 0) {
				level--;
				return isOverDue(calendar.getTime(), level);
			}
		}
		return false;
	}

	/**
	 * 增加时间值
	 * 
	 * @param calendar
	 *            日历
	 * 
	 */
	public void addDuration(Calendar calendar) {
		switch (timeunit) {
		case NotificationConstant.TIME_UNIT_DAY:
			calendar.add(Calendar.MINUTE, (int) (limittimecount * 3600)); // 强制转换前加()
			break;
		case NotificationConstant.TIME_UNIT_HOUR:
			calendar.add(Calendar.MINUTE, (int) (limittimecount * 60));
			break;
		default:
			break;
		}
	}

	/**
	 * 获取创建日期
	 * 
	 * @return 创建日期
	 */
	public Date getCurDate() {
		return curDate;
	}

	/**
	 * 设置创建日期
	 * 
	 * @param curDate
	 *            创建日期
	 */
	public void setCurDate(Date curDate) {
		this.curDate = curDate;
	}

	/**
	 * 获取过期时间
	 * 
	 * @return 过期时间
	 */
	public Date getDeadline() {
		return deadline;
	}

	/**
	 * 设置过期时间
	 * 
	 * @param deadline
	 *            过期时间
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public boolean isIsnotifysuperior() {
		return isnotifysuperior;
	}

	public void setIsnotifysuperior(boolean isnotifysuperior) {
		this.isnotifysuperior = isnotifysuperior;
	}

	public int getTimeunit() {
		return timeunit;
	}

	public void setTimeunit(int timeunit) {
		this.timeunit = timeunit;
	}

	public double getLimittimecount() {
		return limittimecount;
	}

	public void setLimittimecount(double limittimecount) {
		this.limittimecount = limittimecount;
	}
}
