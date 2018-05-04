package pers.acp.communications.server.ctrl;

import pers.acp.communications.server.base.interfaces.IDaemonService;
import pers.acp.tools.common.DBConTools;
import org.apache.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by zhangbin on 2016/12/21.
 * 后台守护服务控制类
 */
public class DaemonServiceManager implements ServletContextListener {

    private static Logger log = Logger.getLogger(DaemonServiceManager.class);

    private static final ConcurrentLinkedDeque<IDaemonService> serverDeque = new ConcurrentLinkedDeque<>();

    /**
     * 添加后台守护服务
     *
     * @param daemonService 后台守护服务
     */
    public static void addService(IDaemonService daemonService) {
        synchronized (serverDeque) {
            serverDeque.push(daemonService);
            serverDeque.notifyAll();
        }
        log.info("add daemon service [" + daemonService.getServiceName() + "]");
    }

    /**
     * 停止后台守护服务
     */
    public static void stopAllService() {
        synchronized (serverDeque) {
            while (!serverDeque.isEmpty()) {
                IDaemonService daemonService = serverDeque.pop();
                daemonService.stopService();
                log.info("destroy daemon service [" + daemonService.getServiceName() + "]");
            }
        }
        DBConTools.destroyAllConnections();
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // 服务器启动时执行初始化
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        stopAllService();
    }

}
