/**
 * DocumentServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import java.util.Map;

import OLink.bpm.webservice.fault.DocumentServiceFault;
import OLink.bpm.webservice.model.SimpleDocument;
import org.apache.axis.message.SOAPHeaderElement;

public class DocumentServiceSoapBindingStub extends org.apache.axis.client.Stub implements
		DocumentService {
	private java.util.Vector<Object> cachedSerClasses = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedSerQNames = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedSerFactories = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedDeserFactories = new java.util.Vector<Object>();

	static org.apache.axis.description.OperationDesc[] _operations;

	/**
	 * 用户校验所添加的代码
	 */
	private String loginno;
	private String password;
	private String domain;

	public String getLoginno() {
		return loginno;
	}

	public void setLoginno(String loginno) {
		this.loginno = loginno;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	static {
		_operations = new org.apache.axis.description.OperationDesc[7];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("createDocumentByGuest");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "formName"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "DocumentServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault"), true));
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("updateDocumentByGuest");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "documentId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "DocumentServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault"), true));
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("createDocumentByDomainUser");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "formName"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "domainUserId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "DocumentServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault"), true));
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("updateDocumentByDomainUser");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "documentId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "domainUserId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "DocumentServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault"), true));
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchDocumentsByFilter");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "formName"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "domainId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(Object[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchDocumentsByFilterReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "DocumentServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault"), true));
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchDocumentsByFilter");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "formName"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(Object[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchDocumentsByFilterReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "DocumentServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault"), true));
		_operations[5] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchDocumentByFilter");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "formName"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "domainId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn", "SimpleDocument"));
		oper.setReturnClass(SimpleDocument.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchDocumentByFilterReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "DocumentServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault"), true));
		_operations[6] = oper;

	}

	public DocumentServiceSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public DocumentServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public DocumentServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		if (service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service).setTypeMappingVersion("1.2");

		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
		Class<?> cls;
		Class<?> beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
		Class<?> beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
		/*
		 * java.lang.Class enumsf =
		 * org.apache.axis.encoding.ser.EnumSerializerFactory.class;
		 * java.lang.Class enumdf =
		 * org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
		 * java.lang.Class arraysf =
		 * org.apache.axis.encoding.ser.ArraySerializerFactory.class;
		 * java.lang.Class arraydf =
		 * org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
		 * java.lang.Class simplesf =
		 * org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
		 * java.lang.Class simpledf =
		 * org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
		 * java.lang.Class simplelistsf =
		 * org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
		 * java.lang.Class simplelistdf =
		 * org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
		 */
		qName = new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ArrayOf_xsd_anyType");
		cachedSerQNames.add(qName);
		cls = Object[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyType");
		qName2 = null;
		cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
		cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("http://xml.apache.org/xml-soap", "mapItem");
		cachedSerQNames.add(qName);
		cls = Map.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "DocumentServiceFault");
		cachedSerQNames.add(qName);
		cls = DocumentServiceFault.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn", "SimpleDocument");
		cachedSerQNames.add(qName);
		cls = SimpleDocument.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
		try {
			org.apache.axis.client.Call _call = super._createCall();
			if (super.maintainSessionSet) {
				_call.setMaintainSession(super.maintainSession);
			}
			if (super.cachedUsername != null) {
				_call.setUsername(super.cachedUsername);
			}
			if (super.cachedPassword != null) {
				_call.setPassword(super.cachedPassword);
			}
			if (super.cachedEndpoint != null) {
				_call.setTargetEndpointAddress(super.cachedEndpoint);
			}
			if (super.cachedTimeout != null) {
				_call.setTimeout(super.cachedTimeout);
			}
			if (super.cachedPortName != null) {
				_call.setPortName(super.cachedPortName);
			}

			if (loginno != null) {
				_call.addHeader(new SOAPHeaderElement("Authorization", "loginno", getLoginno()));

			}
			if (password != null) {
				_call.addHeader(new SOAPHeaderElement("Authorization", "password", getPassword()));
			}
			if (domain != null) {
				_call.addHeader(new SOAPHeaderElement("Authorization", "domain", getDomain()));
			}

			java.util.Enumeration<Object> keys = super.cachedProperties.keys();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				_call.setProperty(key, super.cachedProperties.get(key));
			}
			// All the type mapping information is registered
			// when the first call is made.
			// The type mapping information is actually registered in
			// the TypeMappingRegistry of the service, which
			// is the reason why registration is only needed for the first call.
			synchronized (this) {
				if (firstCall()) {
					// must set encoding style before registering serializers
					_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
					_call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
					for (int i = 0; i < cachedSerFactories.size(); ++i) {
						Class<?> cls = (Class<?>) cachedSerClasses.get(i);
						javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames.get(i);
						Object x = cachedSerFactories.get(i);
						if (x instanceof Class<?>) {
							Class<?> sf = (Class<?>) cachedSerFactories.get(i);
							Class<?> df = (Class<?>) cachedDeserFactories.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						} else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories
									.get(i);
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories
									.get(i);
							_call.registerTypeMapping(cls, qName, sf, df, false);
						}
					}
				}
			}
			return _call;
		} catch (Throwable _t) {
			throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
		}
	}

	public void createDocumentByGuest(String formName, java.util.HashMap<?, ?> parameters,
			String applicationId) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"createDocumentByGuest"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { formName, parameters, applicationId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof DocumentServiceFault) {
					throw (DocumentServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void updateDocumentByGuest(String documentId, java.util.HashMap<?, ?> parameters,
			String applicationId) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"updateDocumentByGuest"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { documentId, parameters, applicationId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof DocumentServiceFault) {
					throw (DocumentServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void createDocumentByDomainUser(String formName, java.util.HashMap<?, ?> parameters,
			String domainUserId, String applicationId) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"createDocumentByDomainUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { formName, parameters, domainUserId,
					applicationId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof DocumentServiceFault) {
					throw (DocumentServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void updateDocumentByDomainUser(String documentId, java.util.HashMap<?, ?> parameters,
			String domainUserId, String applicationId) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"updateDocumentByDomainUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { documentId, parameters, domainUserId,
					applicationId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof DocumentServiceFault) {
					throw (DocumentServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public Object[] searchDocumentsByFilter(String formName, java.util.HashMap<?, ?> parameters,
			String applicationId, String domainId) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchDocumentsByFilter"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { formName, parameters, applicationId,
					domainId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (Object[]) _resp;
				} catch (Exception _exception) {
					return (Object[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, Object[].class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof DocumentServiceFault) {
					throw (DocumentServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public Object[] searchDocumentsByFilter(String formName, java.util.HashMap<?, ?> parameters,
			String applicationId) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchDocumentsByFilter"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { formName, parameters, applicationId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (Object[]) _resp;
				} catch (Exception _exception) {
					return (Object[]) org.apache.axis.utils.JavaUtils
							.convert(_resp, Object[].class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof DocumentServiceFault) {
					throw (DocumentServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public SimpleDocument searchDocumentByFilter(String formName,
												 java.util.HashMap<?, ?> parameters, String applicationId, String domainId)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[6]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchDocumentByFilter"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { formName, parameters, applicationId,
					domainId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (SimpleDocument) _resp;
				} catch (Exception _exception) {
					return (SimpleDocument) org.apache.axis.utils.JavaUtils.convert(_resp,
							SimpleDocument.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof DocumentServiceFault) {
					throw (DocumentServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

}
