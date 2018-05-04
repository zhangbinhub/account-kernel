package pers.acp.communications.server.base;

import pers.acp.communications.server.base.interfaces.IBaseCommunication;
import pers.acp.tools.config.instance.SystemConfig;
import pers.acp.tools.exceptions.ConfigException;

public abstract class BaseCommunication implements IBaseCommunication {

    /**
     * 系统配置类
     */
    private SystemConfig systemConfig = null;

    /**
     * 应用编号
     */
    protected String appid = null;

    /**
     * 当前操作者ID
     */
    protected String operatorId = null;

    /**
     * 数据源编号
     */
    protected int dbno = 0;

    /**
     * 构造函数
     */
    public BaseCommunication() throws ConfigException {
        this.systemConfig = SystemConfig.getInstance();
    }

    /**
     * 获取系统配置类
     *
     * @return 系统配置实例
     */
    protected SystemConfig getSystemConfig() {
        return systemConfig;
    }

}
