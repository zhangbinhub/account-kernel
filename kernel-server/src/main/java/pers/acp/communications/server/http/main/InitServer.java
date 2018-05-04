package pers.acp.communications.server.http.main;

import pers.acp.communications.server.http.config.HttpConfig;
import pers.acp.communications.server.http.config.InitFunctionConfig;
import pers.acp.communications.server.http.main.init.*;
import pers.acp.communications.server.http.param.ParamBuild;
import pers.acp.tools.common.CommonTools;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.List;

class InitServer {

    private static Logger log = Logger.getLogger(InitServer.class);// 日志对象

    /**
     * 主线程中进行系统初始化
     */
    static void StartNow() {
        try {
            InitSource.getInstance().run();
        } catch (Exception e) {
            log.error("system startup Exception:" + e.getMessage());
        }
    }

    /**
     * 新线程中进行系统初始化
     */
    static void StartThread() {
        try {
            InitSource.getInstance().start();
        } catch (Exception e) {
            log.error("system startup Exception:" + e.getMessage());
        }
    }
}

class InitSource extends Thread {

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    private static InitSource handle = null;

    protected InitSource() {
        this.setDaemon(true);
    }

    protected static InitSource getInstance() {
        if (handle == null) {
            synchronized (InitSource.class) {
                if (handle == null) {
                    handle = new InitSource();
                }
            }
        }
        return handle;
    }

    public void run() {
        log.info("begin start-up...");
        try {
            /* 初始化系统 start *******/
            /* 初始化系统服务 */
            InitConfig.load();
            InitTcpServer.startTcpServer();
            InitUdpServer.startUdpServer();
            InitFtpServer.startFtpServer();
            InitSFtpServer.startSFtpServer();
            InitWebService.publishWebService();
            HttpConfig httpConfig = HttpConfig.getInstance();
            if (httpConfig != null) {
                if (httpConfig.getOnlineServer() != null) {
                    InitOnlineServer.startOnline(httpConfig.getOnlineServer());
                }
            }
            CommonTools.InitTools();
            /* 初始化自定义服务 */
            InitFunctionConfig initFunctionConfig = InitFunctionConfig.getInstance();
            if (initFunctionConfig != null) {
                List<InitFunctionConfig.Function> functions = initFunctionConfig.getFunctions();
                if (functions != null) {
                    log.info("start " + functions.size() + " custom services");
                    for (InitFunctionConfig.Function function : functions) {
                        String name = function.getName();
                        String classname = function.getClassname();
                        String methodname = function.getMethod();
                        List<InitFunctionConfig.Param> params = function.getParams();
                        Class<?> cls = Class.forName(classname);
                        Object instance = cls.newInstance();
                        Method method = cls.getMethod(methodname, buildClass(params));
                        method.invoke(instance, buildValue(params));
                        log.info("start service success:[" + name + "]");
                    }
                } else {
                    log.info("don't find custom services");
                }
            }
            log.info("finish start-up");
            log.info("****************** system is started ******************");
            /* 初始化系统 end *******/
        } catch (Exception e) {
            log.error("init service Exception:" + e.getMessage());
        }
    }

    private Class[] buildClass(List<InitFunctionConfig.Param> params) {
        if (params == null) {
            return null;
        }
        Class[] result = new Class[params.size()];
        for (int i = 0; i < params.size(); i++) {
            result[i] = ParamBuild.getParamClass(params.get(i).getType());
        }
        return result;
    }

    private Object[] buildValue(List<InitFunctionConfig.Param> params) {
        if (params == null) {
            return null;
        }
        Object[] result = new Object[params.size()];
        for (int i = 0; i < params.size(); i++) {
            result[i] = ParamBuild.getParamValue(params.get(i).getType(), params.get(i).getValue());
        }
        return result;
    }
}