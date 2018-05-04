package pers.acp.communications.server.http.online;

import pers.acp.communications.server.base.interfaces.IDaemonService;
import pers.acp.communications.server.ctrl.DaemonServiceManager;
import pers.acp.communications.server.http.config.HttpConfig;
import pers.acp.tools.common.DBConTools;
import pers.acp.tools.exceptions.ConfigException;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class OnlineService implements Runnable, IDaemonService {

    private static Logger log = Logger.getLogger(OnlineService.class);// 日志对象

    private static OnlineService instance;

    private long spacingTime = 3000;

    private Thread currThread = null;

    private boolean isRunning = true;

    private DBConTools dbconTools = new DBConTools();

    /**
     * 获取服务实例
     *
     * @return 实例对象
     */
    public static OnlineService getInstance(long SpacingTime) {
        if (instance == null) {
            synchronized (OnlineService.class) {
                instance = new OnlineService(SpacingTime);
            }
        }
        return instance;
    }

    /**
     * 构造函数
     *
     * @param SpacingTime 清理间隔时间，单位:毫秒
     */
    private OnlineService(long SpacingTime) {
        this.spacingTime = SpacingTime;
        currThread = new Thread(this);
        currThread.setDaemon(true);
    }

    /**
     * 启动任务
     */
    public void start() {
        isRunning = true;
        currThread.start();
        DaemonServiceManager.addService(this);
    }

    /**
     * 执行任务
     */
    @Override
    public void run() {
        try {
            HttpConfig httpConfig = null;
            try {
                httpConfig = HttpConfig.getInstance();
            } catch (ConfigException e) {
                log.error(e.getMessage(), e);
            }
            if (httpConfig != null) {
                HttpConfig.OnlineServer config = httpConfig.getOnlineServer();
                long timeout = config.getOnlineTimeout();
                if (timeout > 0) {
                    while (isRunning) {
                        Date date = new Date(new Date().getTime() - timeout);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = df.format(date);
                        String deleteSQL = "delete from "
                                + config.getTablename() + " where "
                                + config.getColname() + "<'" + time + "'";
                        if (!dbconTools.doUpdate(deleteSQL)) {
                            log.error("online user clearup service faild:" + deleteSQL);
                        }
                        Thread.sleep(this.spacingTime);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getServiceName() {
        return "online user clearup service";
    }

    @Override
    public void stopService() {
        isRunning = false;
    }
}
