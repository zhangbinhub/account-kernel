package pers.acp.tools.task.base;

import pers.acp.tools.task.base.interfaces.IBaseTask;
import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * 任务基类
 *
 * @author zb
 */
public abstract class BaseTask implements IBaseTask, Runnable {

    /**
     * 日志对象
     */
    private Logger log = Logger.getLogger(this.getClass());

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 任务名称
     */
    private String taskName = null;

    /**
     * 创建时间
     */
    private Date generateTime = null;

    /**
     * 提交执行时间
     */
    private Date submitTime = null;

    /**
     * 开始执行时间
     */
    private Date beginExceuteTime = null;

    /**
     * 执行完成时间
     */
    private Date finishTime = null;

    /**
     * 任务执行结果
     */
    private Object taskResult = null;

    /**
     * 是否需要立即执行：false-等待执行策略；true-立即执行
     */
    private boolean needExecuteImmediate = false;

    /**
     * 任务是否处于正在执行状态
     */
    private boolean isRunning = false;

    /**
     * 创建任务
     *
     * @param taskName 任务名称
     */
    public BaseTask(String taskName) {
        this.taskId = CommonUtility.getUuid();
        this.generateTime = new Date();
        this.taskName = taskName;
    }

    /**
     * 创建任务
     *
     * @param taskName             任务名称
     * @param needExecuteImmediate 是否需要立即执行：false-等待执行策略；true-立即执行
     */
    public BaseTask(String taskName, boolean needExecuteImmediate) {
        this.taskId = CommonUtility.getUuid();
        this.generateTime = new Date();
        this.taskName = taskName;
        this.needExecuteImmediate = needExecuteImmediate;
    }

    @Override
    public void run() {
        this.isRunning = true;
        this.beginExceuteTime = new Date();
        try {
            if (this.beforeExcuteFun()) {
                Object result = this.excuteFun();
                this.setTaskResult(result);
                if (result != null) {
                    this.afterExcuteFun(result);
                } else {
                    this.setTaskResult(null);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            this.setTaskResult(null);
        }
        this.finishTime = new Date();
        this.isRunning = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    /**
     * 执行任务，返回执行结果
     *
     * @return 执行结果对象
     */
    public Object doExcute() {
        this.run();
        return this.taskResult;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Date getGenerateTime() {
        return generateTime;
    }

    public Date getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    public Date getBeginExceuteTime() {
        return beginExceuteTime;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public Object getTaskResult() {
        return taskResult;
    }

    protected void setTaskResult(Object taskResult) {
        this.taskResult = taskResult;
    }

    /**
     * 是否需要立即执行
     *
     * @return false-等待执行策略；true-立即执行
     */
    public boolean isNeedExecuteImmediate() {
        return needExecuteImmediate;
    }

    public void setNeedExecuteImmediate(boolean needExecuteImmediate) {
        this.needExecuteImmediate = needExecuteImmediate;
    }

    public boolean isRunning() {
        return isRunning;
    }

}
