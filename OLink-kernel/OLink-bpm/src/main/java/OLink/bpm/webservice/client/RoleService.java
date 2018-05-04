/**
 * RoleService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import OLink.bpm.webservice.model.SimpleRole;

public interface RoleService extends java.rmi.Remote {
	SimpleRole getRole(String pk)
			throws java.rmi.RemoteException;

	SimpleRole createRole(
			SimpleRole role)
			throws java.rmi.RemoteException;

	void updateRole(SimpleRole role)
			throws java.rmi.RemoteException;

	void deleteRole(String pk)
			throws java.rmi.RemoteException;

	void setPermissionSet(SimpleRole role,
						  String[] resources) throws java.rmi.RemoteException;
}
