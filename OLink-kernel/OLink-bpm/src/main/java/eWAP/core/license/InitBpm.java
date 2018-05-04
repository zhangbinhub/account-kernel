package eWAP.core.license;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.email.email.action.EmailUserHelper;
import OLink.bpm.core.task.ejb.Task;
import OLink.bpm.core.task.ejb.TaskProcess;
import OLink.bpm.core.user.action.OnlineUsers;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.engine.AutoAuditJobManager;
import OLink.bpm.core.workflow.notification.ejb.NotificationJob;
import OLink.bpm.init.InitSystem;
import OLink.bpm.util.OBPMSessionContext;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.timer.Schedule;
import OLink.bpm.util.timer.TimeRunnerAble;
import OLink.bpm.util.timer.TimerRunner;
import com.jamonapi.MonitorFactory;
import com.jamonapi.proxy.MonProxyFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Date;

/**
 * Created by zhang on 2016/3/21.
 */
public class InitBpm {
    public static void Init(ServletContextEvent paramServletContextEvent, String paramString)
            throws Exception {
        try {
            Environment localEnvironment = Environment.getInstance();
            localEnvironment.setApplicationRealPath(paramString);
            MonitorFactory.disable();
            MonProxyFactory.enableAll(false);
            PersistenceUtils.beginTransaction();
            InitSystem.init();
            PersistenceUtils.commitTransaction();
            paramServletContextEvent.getServletContext().setAttribute(Environment.class.getName(), localEnvironment);
            Schedule.registerJob(new NotificationJob(), 1800000L, 1800000L);
            AutoAuditJobManager.initJobs();
            Collection localCollection = a().doSimpleQuery(null);
            PersistenceUtils.closeSession();
            for (Object aLocalCollection : localCollection) {
                Task localTask = (Task) aLocalCollection;
                if (localTask.getStartupType() != 1)
                    continue;
                long l = 60000L;
                TimerRunner.registerJSService(localTask.getApplicationid());
                TimeRunnerAble localTimeRunnerAble = TimerRunner.createTimeRunnerAble(localTask, a());
                TimerRunner.runningList.put(localTask, localTimeRunnerAble);
                TimerRunner.registerTimerTask(localTimeRunnerAble, new Date(), l);
            }
        } catch (Exception localException1) {
            localException1.printStackTrace();
            throw localException1;
        } finally {
            try {
                PersistenceUtils.closeSessionAndConnection();
            } catch (Exception localException2) {
                localException2.printStackTrace();
            }
        }
    }

    private static TaskProcess a()
            throws ClassNotFoundException {
        return (TaskProcess) ProcessFactory.createProcess(TaskProcess.class);
    }

    public static void addSession(HttpSession paramHttpSession) {
        OBPMSessionContext.getInstance().addSession(paramHttpSession);
    }

    public static void removeOtherObject(HttpSession paramHttpSession) {
        EmailUserHelper.logoutEmailSystem(paramHttpSession);
        WebUser localWebUser = (WebUser) paramHttpSession.getAttribute("FRONT_USER");
        if (localWebUser != null)
            OnlineUsers.remove(localWebUser.getOnlineUserid());
        localWebUser = (WebUser) paramHttpSession.getAttribute("USER");
        if (localWebUser != null)
            OnlineUsers.remove(localWebUser.getOnlineUserid());
    }
}
