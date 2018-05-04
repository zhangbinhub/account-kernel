package OLink.bpm.core.remoteserver.action;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.remoteserver.ejb.RemoteServerVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.remoteserver.ejb.RemoteServerProcess;

public class RemoteServerAction extends BaseAction<RemoteServerVO> {

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public RemoteServerAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(RemoteServerProcess.class), new RemoteServerVO());
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
