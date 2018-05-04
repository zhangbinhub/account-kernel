/**
 * DepartmentService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import OLink.bpm.webservice.model.SimpleDepartment;

public interface DepartmentService extends java.rmi.Remote {
	SimpleDepartment createDepartment(
			SimpleDepartment dep)
			throws java.rmi.RemoteException;

	void updateDepartment(SimpleDepartment dep)
			throws java.rmi.RemoteException;

	SimpleDepartment getDepartment(
			String pk) throws java.rmi.RemoteException;

	void deleteDepartment(String pk)
			throws java.rmi.RemoteException;

	void upateSuperior(SimpleDepartment dep,
					   SimpleDepartment superDep)
			throws java.rmi.RemoteException;
}
