package OLink.bpm.util.message;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.shortmessage.received.ejb.ReceivedMessageProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.workflow.notification.ejb.sendmode.SMSModeProxy;
import OLink.bpm.core.shortmessage.received.ejb.ReceivedMessageVO;
import OLink.bpm.core.user.action.WebUser;

public class ShortMessageUtil {

	public DataPackage<ReceivedMessageVO> queryReplyById(String id) throws Exception {
		ReceivedMessageProcess process = (ReceivedMessageProcess) ProcessFactory
				.createProcess(ReceivedMessageProcess.class);
		if (process != null)
			return process.queryByDocId(id);
		else
			return null;
	}

	public SMSModeProxy getSender(WebUser user) {
		return new SMSModeProxy(user);
	}
}
