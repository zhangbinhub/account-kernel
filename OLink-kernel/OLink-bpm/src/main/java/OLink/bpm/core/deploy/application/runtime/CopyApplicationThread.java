package OLink.bpm.core.deploy.application.runtime;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.deploy.application.ejb.CopyApplicationProcess;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

public class CopyApplicationThread extends Thread {

	public final static Logger log = Logger.getLogger(CopyApplicationThread.class);
	String applicationid;

	String newApplication;

	public CopyApplicationThread(String applicationid, String newApplication) {
		this.applicationid = applicationid;
		this.newApplication = newApplication;
	}

	public CopyApplicationProcess getProcess() throws Exception {
		return (CopyApplicationProcess) ProcessFactory.createRuntimeProcess(CopyApplicationProcess.class,
				newApplication);
	}

	/*
	 * copy all (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		if (applicationid != null) {
			try {
				CopyApplicationProcess process = getProcess();
				// process.copyDataSource(applicationid);
				process.copyExcelConf(applicationid);
				process.copyStylelibs(applicationid);
				process.copyModule(applicationid);
				process.copyMacrolibs(applicationid);
				process.copyValidatelibs(applicationid);
				process.copyStatelabel(applicationid);
				process.copyComponent(applicationid);
				process.copyPage(applicationid);
				process.copyMenu(applicationid);
				process.copyRole(applicationid);
				process.copyHomepage(applicationid);
				process.copyReminder(applicationid);
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				try {
					PersistenceUtils.closeSession();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
