package OLink.bpm.webservice.fault;

import java.rmi.RemoteException;

public class DocumentServiceFault extends RemoteException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -398105568446890291L;

	public DocumentServiceFault(String message) {
		super(message);
	}
}
