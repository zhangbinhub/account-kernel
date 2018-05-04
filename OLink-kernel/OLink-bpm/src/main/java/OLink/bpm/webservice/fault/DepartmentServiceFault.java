package OLink.bpm.webservice.fault;

import java.rmi.RemoteException;

public class DepartmentServiceFault extends RemoteException {

	private static final long serialVersionUID = 1L;

	public DepartmentServiceFault(String message) {
		super(message);
	}

}
