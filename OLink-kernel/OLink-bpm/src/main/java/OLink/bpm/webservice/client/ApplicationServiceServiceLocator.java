/**
 * ApplicationServiceServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

public class ApplicationServiceServiceLocator extends org.apache.axis.client.Service implements
		ApplicationServiceService {

	private static final long serialVersionUID = -4907599007699009414L;

	public ApplicationServiceServiceLocator() {
	}

	public ApplicationServiceServiceLocator(org.apache.axis.EngineConfiguration config) {
		super(config);
	}

	public ApplicationServiceServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName)
			throws javax.xml.rpc.ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for ApplicationService
	private String ApplicationService_address = "http://localhost:8080/eWAP/services/ApplicationService";

	public String getApplicationServiceAddress() {
		return ApplicationService_address;
	}

	// The WSDD service name defaults to the port name.
	private String ApplicationServiceWSDDServiceName = "ApplicationService";

	public String getApplicationServiceWSDDServiceName() {
		return ApplicationServiceWSDDServiceName;
	}

	public void setApplicationServiceWSDDServiceName(String name) {
		ApplicationServiceWSDDServiceName = name;
	}

	public ApplicationService getApplicationService() throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(ApplicationService_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getApplicationService(endpoint);
	}

	public ApplicationService getApplicationService(String loginno, String password,
													String domain) throws javax.xml.rpc.ServiceException {
		java.net.URL endpoint;
		try {
			endpoint = new java.net.URL(ApplicationService_address);
		} catch (java.net.MalformedURLException e) {
			throw new javax.xml.rpc.ServiceException(e);
		}
		return getApplicationService(loginno, password, domain, endpoint);
	}

	public ApplicationService getApplicationService(java.net.URL portAddress)
			throws javax.xml.rpc.ServiceException {
		try {
			ApplicationServiceSoapBindingStub _stub = new ApplicationServiceSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getApplicationServiceWSDDServiceName());
			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public ApplicationService getApplicationService(String loginno, String password,
													String domain, java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
		try {
			ApplicationServiceSoapBindingStub _stub = new ApplicationServiceSoapBindingStub(
					portAddress, this);
			_stub.setPortName(getApplicationServiceWSDDServiceName());
			_stub.setLoginno(loginno);
			_stub.setPassword(password);
			_stub.setDomain(domain);

			return _stub;
		} catch (org.apache.axis.AxisFault e) {
			return null;
		}
	}

	public void setApplicationServiceEndpointAddress(String address) {
		ApplicationService_address = address;
	}

	/**
	 * @SuppressWarnings getPort方法不支持泛型 For the given interface, get the stub
	 *                   implementation. If this service has no port for the
	 *                   given interface, then ServiceException is thrown.
	 */
	@SuppressWarnings("unchecked")
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
		try {
			if (ApplicationService.class.isAssignableFrom(serviceEndpointInterface)) {
				ApplicationServiceSoapBindingStub _stub = new ApplicationServiceSoapBindingStub(
						new java.net.URL(ApplicationService_address), this);
				_stub.setPortName(getApplicationServiceWSDDServiceName());
				return _stub;
			}
		} catch (Throwable t) {
			throw new javax.xml.rpc.ServiceException(t);
		}
		throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  "
				+ (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * @SuppressWarnings getPort方法不支持泛型 For the given interface, get the stub
	 *                   implementation. If this service has no port for the
	 *                   given interface, then ServiceException is thrown.
	 */

	@SuppressWarnings("unchecked")
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface)
			throws javax.xml.rpc.ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		String inputPortName = portName.getLocalPart();
		if ("ApplicationService".equals(inputPortName)) {
			return getApplicationService();
		} else {
			java.rmi.Remote _stub = getPort(serviceEndpointInterface);
			((org.apache.axis.client.Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public javax.xml.namespace.QName getServiceName() {
		return new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ApplicationServiceService");
	}

	private java.util.HashSet<Object> ports = null;

	public java.util.Iterator<?> getPorts() {
		if (ports == null) {
			ports = new java.util.HashSet<Object>();
			ports.add(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ApplicationService"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(String portName, String address)
			throws javax.xml.rpc.ServiceException {

		if ("ApplicationService".equals(portName)) {
			setApplicationServiceEndpointAddress(address);
		} else { // Unknown Port Name
			throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(javax.xml.namespace.QName portName, String address)
			throws javax.xml.rpc.ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
