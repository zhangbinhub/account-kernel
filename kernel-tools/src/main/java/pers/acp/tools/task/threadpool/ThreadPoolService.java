package pers.acp.tools.task.threadpool;

import pers.acp.tools.task.threadpool.basetask.BaseThreadTask;
import pers.acp.tools.utility.CommonUtility;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 线程池调度
 *
 * @author zb
 */
public final class ThreadPoolService {

    private static Logger log = Logger.getLogger(ThreadPoolService.class);// 日志对象

    private static ConcurrentHashMap<String, ThreadPoolService> threadPoolInstanceMap = new ConcurrentHashMap<>();

    private String poolName;

    private final CopyOnWriteArrayList<BaseThreadTask> taskQueue = new CopyOnWriteArrayList<>();

    private PoolWorker[] workers;

    /**
     * 获取线程池实例
     *
     * @param SpacingTime     轮询队列的间隔时间
     * @param MaxThreadNumber 最大线程数
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(long SpacingTime, int MaxThreadNumber) {
        return getInstance(null, SpacingTime, MaxThreadNumber);
    }

    /**
     * 获取线程池实例
     *
     * @param poolName        线程池实例名
     * @param SpacingTime     轮询队列的间隔时间
     * @param MaxThreadNumber 最大线程数
     * @return 线程池实例
     */
    public static ThreadPoolService getInstance(String poolName, long SpacingTime, int MaxThreadNumber) {
        ThreadPoolService instance;
        if (CommonUtility.isNullStr(poolName)) {
            poolName = "defaultThreadPool";
        }
        synchronized (ThreadPoolService.class) {
            if (!threadPoolInstanceMap.containsKey(poolName)) {
                instance = new ThreadPoolService(poolName, SpacingTime, MaxThreadNumber);
                log.debug("init ThreadPool [" + poolName + "] success,create thread:" + MaxThreadNumber);
                threadPoolInstanceMap.put(poolName, instance);
            } else {
                instance = threadPoolInstanceMap.get(poolName);
            }
        }
        return instance;
    }

    /**
     * 线程池实例构造函数
     *
     * @param poolName        线程池实例名
     * @param SpacingTime     轮询队列的间隔时间
     * @param MaxThreadNumber 最大线程数
     */
    private ThreadPoolService(String poolName, long SpacingTime, int MaxThreadNumber) {
        this.poolName = poolName;
        workers = new PoolWorker[MaxThreadNumber];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new PoolWorker(i, SpacingTime);
        }
    }

    /**
     * 获取线程池信息
     *
     * @return 线程池信息
     */
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Thread Pool instance : ").append(poolName).append("\n");
        sb.append("Task Queue Size : ").append(taskQueue.size()).append("\n");
        for (BaseThreadTask aTaskQueue : taskQueue) {
            sb.append("Task ").append(aTaskQueue.getTaskId()).append(" is ").append((aTaskQueue.isRunning()) ? "Running.\n" : "Waiting.\n");
        }
        sb.append("workerThread Number:").append(workers.length).append("\n");
        for (PoolWorker worker : workers) {
            sb.append("WorkerThread ").append(worker.getIndex()).append(" is ").append(worker.isWaiting() ? "Waiting.\n" : "Running.\n");
        }
        return sb.toString();
    }

    /**
     * 销毁线程池
     */
    public synchronized void destroy() {
        for (PoolWorker worker : workers) {
            worker.stopWorker();
        }
        taskQueue.clear();
        log.debug("thread pool [" + poolName + "] is destroyed");
    }

    /**
     * 增加新的任务 每增加一个新任务,都要唤醒任务队列
     *
     * @param newTask 任务
     */
    public void addTask(BaseThreadTask newTask) {
        synchronized (taskQueue) {
            newTask.setSubmitTime(new Date());
            taskQueue.add(newTask);
            taskQueue.notifyAll();
        }
        log.debug("thread pool [" + poolName + "] submit task[" + newTask.getTaskId() + "]: " + newTask.getTaskName());
    }

    /**
     * 批量增加新任务
     *
     * @param taskes 任务
     */
    public void batchAddTask(BaseThreadTask[] taskes) {
        if (taskes == null || taskes.length == 0) {
            return;
        }
        synchronized (taskQueue) {
            for (BaseThreadTask taske : taskes) {
                if (taske == null) {
                    continue;
                }
                taske.setSubmitTime(new Date());
                taskQueue.add(taske);
            }
            taskQueue.notifyAll();
        }
        for (BaseThreadTask taske : taskes) {
            if (taske == null) {
                continue;
            }
            log.debug("thread pool [" + poolName + "] submit task[" + taske.getTaskId() + "]: " + taske.getTaskName());
        }
    }

    /**
     * 获取线程池名称
     *
     * @return 线程池名称
     */
    public String getPoolName() {
        return poolName;
    }

    /**
     * 线程池任务队列是否为空
     *
     * @return 任务队列是否为空
     */
    public boolean isEmpty() {
        return taskQueue.isEmpty();
    }

    /**
     * 所有线程是否处于等待状态
     *
     * @return 是否处于等待状态
     */
    public boolean isWaitingAll() {
        for (PoolWorker worker : workers) {
            if (!worker.isWaiting()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 指定线程是否处于等待状态
     *
     * @param threadindex 线程编号
     * @return 是否处于等待状态
     */
    public boolean isWaitingCurr(int threadindex) {
        return workers[threadindex].isWaiting();
    }

    /**
     * 指定线程以外的其他线程是否处于等待状态
     *
     * @param threadindex 线程编号
     * @return 是否处于等待状态
     */
    public boolean isWaitingOther(int threadindex) {
        for (PoolWorker worker : workers) {
            if (worker.getIndex() != threadindex && !worker.isWaiting()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 池中工作线程
     *
     * @author zb
     */
    private class PoolWorker extends Thread {

        private Logger log = Logger.getLogger(this.getClass());// 日志对象

        private int index = -1;

        /**
         * 循环监听队列间隔时间:毫秒
         */
        private long SpacingTime = 3000;

        /**
         * 该工作线程是否正在工作
         */
        private boolean isRunning = true;

        /**
         * 该工作线程是否可以执行新任务
         */
        private boolean isWaiting = true;

        private PoolWorker(int index, long SpacingTime) {
            this.index = index;
            this.SpacingTime = SpacingTime;
            this.setDaemon(true);
            this.start();
        }

        private void stopWorker() {
            this.isRunning = false;
        }

        /**
         * 循环执行任务 这也许是线程池的关键所在
         */
        public void run() {
            while (isRunning) {
                BaseThreadTask task;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty()) {
                        try {
                            taskQueue.wait(SpacingTime);
                        } catch (InterruptedException ie) {
                            log.error(ie.getMessage(), ie);
                        }
                    }
                    task = taskQueue.remove(0);
                    taskQueue.notifyAll();
                }
                if (task != null) {
                    log.debug("thread pool [" + poolName + "] thread " + index + " begin excute task:" + task.getTaskName());
                    setWaiting(false);
                    try {
                        task.setThreadindex(getIndex());
                        if (task.isNeedExecuteImmediate()) {
                            new Thread(task).start();
                        } else {
                            task.doExcute();
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                    log.debug("thread pool [" + poolName + "] thread " + index + " excute task is finished:" + task.getTaskName());
                    setWaiting(true);
                    task = null;
                }
            }
        }

        private int getIndex() {
            return index;
        }

        private boolean isWaiting() {
            return isWaiting;
        }

        private void setWaiting(boolean isWaiting) {
            this.isWaiting = isWaiting;
        }
    }
}
