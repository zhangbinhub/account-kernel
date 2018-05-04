/**
 * RoleServiceService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

public interface RoleServiceService extends javax.xml.rpc.Service {
	String getRoleServiceAddress();

	RoleService getRoleService()
			throws javax.xml.rpc.ServiceException;

	RoleService getRoleService(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
