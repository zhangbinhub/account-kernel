package OLink.bpm.webservice.fault;

import java.rmi.RemoteException;

public class DomainServiceFault extends RemoteException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -398105568446890291L;

	public DomainServiceFault(String message) {
		super(message);
	}
}
