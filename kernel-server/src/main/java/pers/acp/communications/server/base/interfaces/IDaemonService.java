package pers.acp.communications.server.base.interfaces;

/**
 * Created by zhangbin on 2016/12/21.
 * 后台守护任务
 */
public interface IDaemonService {

    /**
     * 获取守护任务名称
     *
     * @return 守护任务名称
     */
    String getServiceName();

    /**
     * 停止任务
     */
    void stopService();

}
