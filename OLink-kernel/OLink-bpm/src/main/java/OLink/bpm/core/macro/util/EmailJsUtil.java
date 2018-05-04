package OLink.bpm.core.macro.util;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.email.email.ejb.EmailProcess;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.email.email.ejb.Email;

public class EmailJsUtil {

	private EmailProcess _process;

//	private WebUser user;

	private static EmailJsUtil _application;

	public static EmailJsUtil getInstance() throws Exception {
		if (_application == null) {
			EmailProcess process = (EmailProcess) ProcessFactory
					.createProcess(EmailProcess.class);
			
			_application = new EmailJsUtil(process);
		}
		return _application;
	}

	public EmailJsUtil(EmailProcess process) {
		_process = process;
	}

	public Object getProcess() {
		return _process;
	}

	public Object find(String id) throws Exception {
		return _process.doView(id);
	}

	public void remove(String id) throws Exception {
		_process.doRemove(id);
	}

	public DataPackage<Email> query(ParamsTable params, WebUser user) throws Exception {
		return _process.doQuery(params, user);
	}

	/*public DataPackage query(ParamsTable params, WebUser user, String type,
			String read) throws Exception {
		return _process.doQuery(params, user, type, read);
	}*/

	public static void main(String[] args) {
	}
}
