/**
 * DomainService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import OLink.bpm.webservice.model.SimpleDomain;

public interface DomainService extends java.rmi.Remote {
	Object[] searchDomainsByDomainAdmin(
			String domainAdminId) throws java.rmi.RemoteException;

	SimpleDomain searchDomainByName(
			String name) throws java.rmi.RemoteException;
}
