package OLink.bpm.webservice.fault;

import java.rmi.RemoteException;

public class UserServiceFault extends RemoteException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7807740809359640938L;

	public UserServiceFault(String message) {
		super(message);
	}
}
