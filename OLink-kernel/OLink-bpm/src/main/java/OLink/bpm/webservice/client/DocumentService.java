/**
 * DocumentService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import OLink.bpm.webservice.model.SimpleDocument;

public interface DocumentService extends java.rmi.Remote {
	void createDocumentByGuest(String formName,
							   java.util.HashMap<?, ?> parameters, String applicationId)
			throws java.rmi.RemoteException;

	void updateDocumentByGuest(String documentId,
							   java.util.HashMap<?, ?> parameters, String applicationId)
			throws java.rmi.RemoteException;

	void createDocumentByDomainUser(String formName,
									java.util.HashMap<?, ?> parameters, String domainUserId,
									String applicationId) throws java.rmi.RemoteException;

	void updateDocumentByDomainUser(String documentId,
									java.util.HashMap<?, ?> parameters, String domainUserId,
									String applicationId) throws java.rmi.RemoteException;

	Object[] searchDocumentsByFilter(
			String formName, java.util.HashMap<?, ?> parameters,
			String applicationId, String domainId)
			throws java.rmi.RemoteException;

	Object[] searchDocumentsByFilter(
			String formName, java.util.HashMap<?, ?> parameters,
			String applicationId) throws java.rmi.RemoteException;

	SimpleDocument searchDocumentByFilter(
			String formName, java.util.HashMap<?, ?> parameters,
			String applicationId, String domainId)
			throws java.rmi.RemoteException;
}
