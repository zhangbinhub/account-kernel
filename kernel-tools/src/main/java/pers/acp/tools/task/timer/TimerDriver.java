package pers.acp.tools.task.timer;

import pers.acp.tools.exceptions.TimerException;
import pers.acp.tools.task.timer.basetask.BaseTimerTask;
import pers.acp.tools.task.timer.container.Calculation;
import pers.acp.tools.task.timer.container.TimerTaskContainer;
import pers.acp.tools.task.timer.ruletype.CircleType;
import pers.acp.tools.task.timer.ruletype.ExcuteType;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.Timer;

/**
 * 定时器驱动
 *
 * @author zhangbin
 */
public class TimerDriver extends Timer {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    /**
     * 执行周期:Time, Day, Week, Month, Quarter, Year
     */
    private CircleType circleType = CircleType.Day;

    /**
     * 执行时间点规则: 时间-开始执行时间（HH:MI:SS）（没有则表示当前时间为开始时间）|执行间隔（单位毫秒）, 日-时间（HH:MI:SS）,
     * 周-周几|时间（1|HH:MI:SS）, 月-几号|时间（31|HH:MI:SS）,
     * 季度-季度内第几月|几号|时间（3|31|HH:MI:SS）, 年-第几月|几号|时间（12|31|HH:MI:SS）
     */
    private String rules = "00:00:00";

    /**
     * 执行类型:WeekDay, Weekend, All
     */
    private ExcuteType excuteType = ExcuteType.All;

    /**
     * 任务容器
     */
    private TimerTaskContainer container = null;

    /**
     * 构造函数（无定时任务,周期默认“日”,规则默认“00:00:00”,执行类型默认“全部”）
     */
    public TimerDriver() {
        super();
    }

    /**
     * 构造函数
     *
     * @param circleType 执行周期:Time, Day, Week, Month, Quarter, Year
     * @param rules      执行时间点规则: 时间-开始执行时间（HH:MI:SS）（没有则表示当前时间为开始时间）|执行间隔（单位毫秒）,
     *                   日-时间（HH:MI:SS）, 周-周几|时间（1|HH:MI:SS）, 月-几号|时间（31|HH:MI:SS）,
     *                   季度-季度内第几月|几号|时间（3|31|HH:MI:SS）, 年-第几月|几号|时间（12|31|HH:MI:SS）
     */
    public TimerDriver(CircleType circleType, String rules) {
        this();
        this.circleType = circleType;
        this.rules = rules;
    }

    /**
     * 构造函数（周期默认“日”,规则默认“00:00:00”）
     *
     * @param excuteType 执行类型:WeekDay, Weekend, All
     */
    public TimerDriver(ExcuteType excuteType) {
        this();
        this.excuteType = excuteType;
    }

    /**
     * 构造函数
     *
     * @param circleType 执行周期:Time, Day, Week, Month, Quarter, Year
     * @param rules      执行时间点规则: 时间-开始执行时间（HH:MI:SS）（没有则表示当前时间为开始时间）|执行间隔（单位毫秒）,
     *                   日-时间（HH:MI:SS）, 周-周几|时间（1|HH:MI:SS）, 月-几号|时间（31|HH:MI:SS）,
     *                   季度-季度内第几月|几号|时间（3|31|HH:MI:SS）, 年-第几月|几号|时间（12|31|HH:MI:SS）
     * @param excuteType 执行类型:WeekDay, Weekend, All
     */
    public TimerDriver(CircleType circleType, String rules, ExcuteType excuteType) {
        this(excuteType);
        this.circleType = circleType;
        this.rules = rules;
    }

    /**
     * 设置定时任务
     *
     * @param task 任务对象
     */
    public void setTimerTask(BaseTimerTask task) {
        if (this.container != null) {
            this.container.cancel();
            this.purge();
        }
        task.setSubmitTime(new Date());
        this.container = new TimerTaskContainer(task, this.circleType, this.rules, this.excuteType);
    }

    /**
     * 获取定时器信息
     */
    public String getTimerInfo() {
        String info = "\ncircleType:" + this.circleType.getName() + "\nrules:" + this.rules + "\nexcuteType:" + this.excuteType.getName();
        if (this.container != null) {
            info += "\ntimertask[" + this.container.getTaskName() + "]\nisNeedExecuteImmediate=" + (this.container.isNeedExecuteImmediate() ? "true" : "false");
        }
        return info;
    }

    /**
     * 启动定时器
     */
    public void startTimer(BaseTimerTask task) {
        this.stopTimerTask();
        this.setTimerTask(task);
        runTimerTask();
    }

    /**
     * 停止任务
     */
    public void stopTimerTask() {
        if (this.container != null) {
            this.container.cancel();
            log.info("stop timertask:" + this.container.getTaskName());
        }
        this.purge();
    }

    /**
     * 停止定时器
     */
    public void stopTimer() {
        this.stopTimerTask();
        this.cancel();
        log.info("stop timerDriver!");
    }

    /**
     * 启动定时器,执行定时任务
     */
    private void runTimerTask() {
        try {
            if (this.container != null && this.container.isExistTask()) {
                if (this.container.isNeedExecuteImmediate()) {
                    this.container.ImmediateRun();
                }
                Object[] param = Calculation.getTimerParam(this.circleType, this.rules);
                this.scheduleAtFixedRate(this.container, (Date) param[0], (Long) param[1]);
                log.info("start timertask successfull:" + getTimerInfo());
            } else {
                throw new TimerException("timertask is null");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.stopTimer();
        }
    }

}
