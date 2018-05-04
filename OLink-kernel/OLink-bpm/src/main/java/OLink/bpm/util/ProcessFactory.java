package OLink.bpm.util;

import OLink.bpm.base.ejb.IRunTimeProcess;
import eWAP.core.license.InitLicense;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public abstract class ProcessFactory {

	private static iProcessFactory _process;

	private ProcessFactory() {
	}

	public static iProcessFactory getInstance() {
		if (_process == null) {
			try {//增加 by XGY
				_process = (iProcessFactory) InitLicense.initProcess().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return _process;
	}

	public static IRunTimeProcess createRuntimeProcess(Class<?> iProcessClazz,
													   String applicationid) throws CreateProcessException {

		return getInstance().createRuntimeProcess(iProcessClazz, applicationid);
	}

	public static IDesignTimeProcess createProcess(Class<?> iProcessClazz)
			throws ClassNotFoundException {
		return getInstance().createProcess(iProcessClazz);
	}

	public static IDesignTimeProcess<?> createProcess(String clazzName)
			throws ClassNotFoundException {

		return getInstance().createProcess(clazzName);
	}
}
