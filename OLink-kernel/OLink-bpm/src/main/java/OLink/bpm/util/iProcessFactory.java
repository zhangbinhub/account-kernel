package OLink.bpm.util;

import OLink.bpm.base.ejb.IRunTimeProcess;
import net.sf.cglib.proxy.MethodInterceptor;
import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface iProcessFactory extends MethodInterceptor{
	IRunTimeProcess createRuntimeProcess(Class<?> iProcessClazz,
										 String applicationid) throws CreateProcessException ;

	IDesignTimeProcess createProcess(Class<?> iProcessClazz)
			throws ClassNotFoundException;

	IDesignTimeProcess<?> createProcess(String clazzName)
			throws ClassNotFoundException;
}
