package OLink.bpm.webservice.fault;

import java.rmi.RemoteException;

public class RoleServiceFault extends RemoteException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7807740809359640938L;

	public RoleServiceFault(String message) {
		super(message);
	}
}
