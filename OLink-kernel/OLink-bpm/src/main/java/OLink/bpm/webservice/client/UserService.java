/**
 * UserService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import OLink.bpm.webservice.model.SimpleUser;

public interface UserService extends java.rmi.Remote {
	SimpleUser createUser(
			SimpleUser user)
			throws java.rmi.RemoteException;

	SimpleUser getUser(String pk)
			throws java.rmi.RemoteException;

	SimpleUser validateUser(
			String domainName, String userAccount,
			String userPassword, int userType)
			throws java.rmi.RemoteException;

	void updateUser(SimpleUser user)
			throws java.rmi.RemoteException;

	void deleteUser(String pk)
			throws java.rmi.RemoteException;

	void deleteUser(SimpleUser user)
			throws java.rmi.RemoteException;

	void setRoleSet(SimpleUser user,
					String[] roles) throws java.rmi.RemoteException;

	void setDepartmentSet(SimpleUser user,
						  String[] deps) throws java.rmi.RemoteException;
}
