package OLink.bpm.core.macro.util;

import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.document.ejb.DocumentProcess;

public class DocProcessJsUtil {

	private DocumentProcess _process;

//	private WebUser user;

	private static DocProcessJsUtil _application;

	public static DocProcessJsUtil getInstance() throws Exception {
		if (_application == null) {
			DocumentProcess proxy = (DocumentProcess) ProcessFactory
					.createProcess(DocumentProcess.class);
			_application = new DocProcessJsUtil(proxy);
		}
		return _application;
	}

	public DocProcessJsUtil(DocumentProcess proxy) {
		_process = proxy;

	}

	public Object getProcessBean() {
		return _process;
	}

	public Object find(String id) throws Exception {
		return _process.doView(id);
	}

	public void remove(String id) throws Exception {
		_process.doRemove(id);
	}

//	public DataPackage query(ParamsTable params, WebUser user) throws Exception {
//		return _process.doQuery(params, user);
//	}

}
