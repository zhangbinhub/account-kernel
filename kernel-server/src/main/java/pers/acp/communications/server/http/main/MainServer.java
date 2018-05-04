package pers.acp.communications.server.http.main;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public final class MainServer extends HttpServlet {

    private static final long serialVersionUID = -7281299952110926121L;

    private Logger log = Logger.getLogger(this.getClass());// 日志对象

    @Override
    public void init() throws ServletException {
        super.init();
        log.info("****************** system is starting... ******************");
        /* 启动初始化服务 */
        InitServer.StartNow();
    }

}
