package OLink.bpm.webservice.fault;

import java.rmi.RemoteException;

public class ApplicationServiceFault extends RemoteException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -398105568446890291L;

	public ApplicationServiceFault(String message) {
		super(message);
	}
}
