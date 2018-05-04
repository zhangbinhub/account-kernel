package OLink.bpm.core.workflow.notification.ejb;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.timer.Job;
import OLink.bpm.util.timer.Schedule;
import OLink.bpm.core.deploy.application.ejb.ApplicationProcess;
import org.apache.log4j.Logger;

public class NotificationJob extends Job {
	public final static Logger LOG = Logger.getLogger(NotificationJob.class);

	public void run() {
		try {
			LOG.info("********************* Notification Job Start ********************");
			ApplicationProcess applicationProcess = (ApplicationProcess) ProcessFactory
					.createProcess(ApplicationProcess.class);
			Collection<ApplicationVO> applications = applicationProcess.doSimpleQuery(null);
			try {
				for (Iterator<ApplicationVO> iterator = applications.iterator(); iterator.hasNext();) {
					ApplicationVO application = iterator.next();
					if (application.testDB()) {
						try {
							NotificationProcess process = (NotificationProcess) ProcessFactory.createRuntimeProcess(
									NotificationProcess.class, application.getId());
							process.notifyOverDueAuditors();
						} catch (UnsupportedOperationException e) {
							continue;
						} finally {
							PersistenceUtils.closeSessionAndConnection();
						}
					}
				}
			} catch (Exception e) {
				LOG.error("Notification Job Error: ", e);
			}

			LOG.info("********************* Notification Job End ********************");
		} catch (Exception e) {
			LOG.error("Notification Job Error: ", e);
		} finally {
			try {
				PersistenceUtils.closeSessionAndConnection();
			} catch (Exception e) {
				LOG.error("Notification Job Error: ", e);
			}
		}
	}

	public static void main(String[] args) {
		Schedule.registerJob(new NotificationJob(), new Date());
	}
}
