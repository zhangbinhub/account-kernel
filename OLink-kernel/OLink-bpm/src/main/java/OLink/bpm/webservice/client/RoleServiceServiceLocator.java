/**
 * RoleServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

public class RoleServiceServiceLocator extends org.apache.axis.client.Service
		implements RoleServiceService {

	private static final long serialVersionUID = 8324810887408299512L;

	public RoleServiceServiceLocator() {
	}

	public RoleServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public RoleServiceServiceLocator(String wsdlLoc,
			javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for RoleService
	private String RoleService_address = "http://localhost:8082/eWAP/services/RoleService";

	public String getRoleServiceAddress() {
		return RoleService_address;
	}

	// The WSDD service name defaults to the port name.
	private String RoleServiceWSDDServiceName = "RoleService";

	public String getRoleServiceWSDDServiceName() {
		return RoleServiceWSDDServiceName;
	}

	public void setRoleServiceWSDDServiceName(String name) {
		RoleServiceWSDDServiceName = name;
	}

	public RoleService getRoleService()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(RoleService_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getRoleService(endpoint);
	}

	public RoleService getRoleService(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			RoleServiceSoapBindingStub _stub = new RoleServiceSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getRoleServiceWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setRoleServiceEndpointAddress(String address) {
		RoleService_address = address;
	}

	/**
	 * @SuppressWarnings getPort方法不支持泛型
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	@SuppressWarnings("unchecked")
	public java.rmi.Remote getPort(Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		try {
			if (RoleService.class
					.isAssignableFrom(serviceEndpointInterface)) {
				RoleServiceSoapBindingStub _stub = new RoleServiceSoapBindingStub(
						new java.net.URL(RoleService_address), this);
				_stub.setPortName(getRoleServiceWSDDServiceName());
				return _stub;
			}
		} catch (Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException(
				"There is no stub implementation for the interface:  "
						+ (serviceEndpointInterface == null ? "null"
								: serviceEndpointInterface.getName()));
	}

	/**
	 * @SuppressWarnings getPort方法不支持泛型
	 * For the given interface, get the stub implementation. If this service has
	 * no port for the given interface, then ServiceException is thrown.
	 */
	@SuppressWarnings("unchecked")
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName,
			Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		String inputPortName = portName.getLocalPart();
		if ("RoleService".equals(inputPortName)) {
			return getRoleService();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "RoleServiceService");
	}

	private java.util.HashSet<Object> ports = null;

	public java.util.Iterator<?> getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet<Object>();
			ports.add(new javax.xml.namespace.QName(
					"http://client.webservice.eWAP.cn", "RoleService"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(String portName,
			String address) throws javax.xml.rpc.ServiceException {

		if ("RoleService".equals(portName)) {
			setRoleServiceEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(
					" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName,
			String address) throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
