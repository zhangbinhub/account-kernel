package pers.acp.tools.task.timer.basetask;

import pers.acp.tools.task.base.BaseTask;

/**
 * 定时任务基类
 *
 * @author zhangbin
 */
public abstract class BaseTimerTask extends BaseTask {

    /**
     * 创建任务
     *
     * @param taskName 任务名称
     */
    public BaseTimerTask(String taskName) {
        super(taskName);
    }

    /**
     * 创建任务
     *
     * @param taskName             任务名称
     * @param needExecuteImmediate 是否需要立即执行：false-等待执行策略；true-立即执行
     */
    public BaseTimerTask(String taskName, boolean needExecuteImmediate) {
        super(taskName, needExecuteImmediate);
    }

}
