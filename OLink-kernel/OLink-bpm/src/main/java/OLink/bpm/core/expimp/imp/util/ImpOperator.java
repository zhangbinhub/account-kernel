package OLink.bpm.core.expimp.imp.util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

public interface ImpOperator<E> extends Remote {
	Collection<E> writeDataToDB(Collection<E> datas, String applicationId)
			throws Exception;

	Collection<E> writeXmlToDB(String xmlStr, String applicationId)
			throws RemoteException;
}
