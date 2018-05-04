/**
 * DocumentServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

public interface DocumentServiceService extends javax.xml.rpc.Service {
	String getDocumentServiceAddress();

	DocumentService getDocumentService()
			throws javax.xml.rpc.ServiceException;

	DocumentService getDocumentService(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
