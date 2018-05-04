/**
 * DomainServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

public class DomainServiceServiceLocator extends org.apache.axis.client.Service
		implements DomainServiceService {

	private static final long serialVersionUID = -5400545794109609755L;

	public DomainServiceServiceLocator() {
	}

	public DomainServiceServiceLocator(
			org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public DomainServiceServiceLocator(String wsdlLoc,
			javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for DomainService
	private String DomainService_address = "http://localhost:8080/eWAP/services/DomainService";

	public String getDomainServiceAddress() {
		return DomainService_address;
	}

	// The WSDD service name defaults to the port name.
	private String DomainServiceWSDDServiceName = "DomainService";

	public String getDomainServiceWSDDServiceName() {
		return DomainServiceWSDDServiceName;
	}

	public void setDomainServiceWSDDServiceName(String name) {
		DomainServiceWSDDServiceName = name;
	}

	public DomainService getDomainService()
			throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(DomainService_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getDomainService(endpoint);
	}

	public DomainService getDomainService(
			java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			DomainServiceSoapBindingStub _stub = new DomainServiceSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getDomainServiceWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setDomainServiceEndpointAddress(String address) {
		DomainService_address = address;
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
			if (DomainService.class
					.isAssignableFrom(serviceEndpointInterface)) {
				DomainServiceSoapBindingStub _stub = new DomainServiceSoapBindingStub(
						new java.net.URL(DomainService_address), this);
				_stub.setPortName(getDomainServiceWSDDServiceName());
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
		if ("DomainService".equals(inputPortName)) {
			return getDomainService();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "DomainServiceService");
	}

	private java.util.HashSet<Object> ports = null;

	public java.util.Iterator<?> getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet<Object>();
			ports.add(new javax.xml.namespace.QName(
					"http://client.webservice.eWAP.cn", "DomainService"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(String portName,
			String address) throws javax.xml.rpc.ServiceException {

		if ("DomainService".equals(portName)) {
			setDomainServiceEndpointAddress(address);
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
