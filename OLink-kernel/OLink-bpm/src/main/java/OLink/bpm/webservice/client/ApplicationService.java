/**
 * ApplicationService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import java.util.Collection;

import OLink.bpm.webservice.model.SimpleApplication;

public interface ApplicationService extends java.rmi.Remote {
	Collection<SimpleApplication> searchApplicationsByName(String name)
			throws java.rmi.RemoteException;

	SimpleApplication searchApplicationByName(
			String name) throws java.rmi.RemoteException;

	Collection<SimpleApplication> searchApplicationsByFilter(
			java.util.HashMap<?, ?> parameters)
			throws java.rmi.RemoteException;

	Collection<SimpleApplication> searchApplicationsByDomainAdmin(
			String domainAdminId) throws java.rmi.RemoteException;

	Collection<SimpleApplication> searchApplicationsByDeveloper(
			String developerId) throws java.rmi.RemoteException;

	boolean addApplication(String userAccount,
						   String domainName, String applicationId)
			throws java.rmi.RemoteException;
}
