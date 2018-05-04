package pers.acp.tools.task.threadpool.basetask;

import pers.acp.tools.task.base.BaseTask;

/**
 * 线程池任务基类，其他任务必须继承此类
 *
 * @author zhangbin
 */
public abstract class BaseThreadTask extends BaseTask {

    public int getThreadindex() {
        return threadindex;
    }

    public void setThreadindex(int threadindex) {
        this.threadindex = threadindex;
    }

    /**
     * 线程编号
     */
    private int threadindex = -1;

    /**
     * 创建任务
     *
     * @param taskName 任务名称
     */
    protected BaseThreadTask(String taskName) {
        super(taskName);
    }

    /**
     * 创建任务
     *
     * @param taskName             任务名称
     * @param needExecuteImmediate 是否需要立即执行：false-等待执行策略；true-立即执行
     */
    protected BaseThreadTask(String taskName, boolean needExecuteImmediate) {
        super(taskName, needExecuteImmediate);
    }

}