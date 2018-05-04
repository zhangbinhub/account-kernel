/**
 * ApplicationServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.webservice.fault.ApplicationServiceFault;
import OLink.bpm.webservice.model.SimpleApplication;
import org.apache.axis.message.SOAPHeaderElement;

public class ApplicationServiceSoapBindingStub extends org.apache.axis.client.Stub implements
		ApplicationService {
	private java.util.Vector<Object> cachedSerClasses = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedSerQNames = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedSerFactories = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedDeserFactories = new java.util.Vector<Object>();

	/**
	 * 用户校验所添加的代码
	 */
	private String loginno;
	private String password;
	private String domain;

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

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

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[6];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchApplicationsByName");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(Object[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchApplicationsByNameReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "ApplicationServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "ApplicationServiceFault"), true));
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchApplicationByName");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "name"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn", "SimpleApplication"));
		oper.setReturnClass(SimpleApplication.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchApplicationByNameReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "ApplicationServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "ApplicationServiceFault"), true));
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchApplicationsByFilter");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parameters"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://xml.apache.org/xml-soap", "Map"), java.util.HashMap.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(Object[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchApplicationsByFilterReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "ApplicationServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "ApplicationServiceFault"), true));
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchApplicationsByDomainAdmin");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "domainAdminId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(Object[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchApplicationsByDomainAdminReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "ApplicationServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "ApplicationServiceFault"), true));
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("searchApplicationsByDeveloper");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "developerId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "ArrayOf_xsd_anyType"));
		oper.setReturnClass(Object[].class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "searchApplicationsByDeveloperReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "ApplicationServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "ApplicationServiceFault"), true));
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("addApplication");
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "userAccount"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "domainName"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "applicationId"),
				org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"), String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
		oper.setReturnClass(boolean.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "addApplicationReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "fault"), "ApplicationServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "ApplicationServiceFault"), true));
		_operations[5] = oper;

	}

	public ApplicationServiceSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public ApplicationServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public ApplicationServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
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

		qName = new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn", "ApplicationServiceFault");
		cachedSerQNames.add(qName);
		cls = ApplicationServiceFault.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn", "SimpleApplication");
		cachedSerQNames.add(qName);
		cls = SimpleApplication.class;
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

	/**
	 * @SuppressWarnings invoke方法返回值类型为Object为不定类型
	 */
	@SuppressWarnings("unchecked")
	public Collection<SimpleApplication> searchApplicationsByName(String name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchApplicationsByName"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { name });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (Collection<SimpleApplication>) _resp;
				} catch (Exception _exception) {
					return (Collection<SimpleApplication>) org.apache.axis.utils.JavaUtils.convert(_resp,
							Collection.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof ApplicationServiceFault) {
					throw (ApplicationServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public SimpleApplication searchApplicationByName(String name)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchApplicationByName"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { name });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (SimpleApplication) _resp;
				} catch (Exception _exception) {
					return (SimpleApplication) org.apache.axis.utils.JavaUtils.convert(
							_resp, SimpleApplication.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof ApplicationServiceFault) {
					throw (ApplicationServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	/**
	 * @SuppressWarnings invoke方法返回值类型为Object为不定类型
	 */
	@SuppressWarnings("unchecked")
	public Collection<SimpleApplication> searchApplicationsByFilter(java.util.HashMap<?, ?> parameters)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchApplicationsByFilter"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { parameters });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (Collection<SimpleApplication>) _resp;
				} catch (Exception _exception) {
					return (Collection<SimpleApplication>) org.apache.axis.utils.JavaUtils.convert(_resp,
							Collection.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof ApplicationServiceFault) {
					throw (ApplicationServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	/**
	 * @SuppressWarnings invoke方法返回值类型为Object为不定类型
	 */
	@SuppressWarnings("unchecked")
	public Collection<SimpleApplication> searchApplicationsByDomainAdmin(String domainAdminId)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchApplicationsByDomainAdmin"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { domainAdminId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (Collection<SimpleApplication>) _resp;
				} catch (Exception _exception) {
					return (Collection<SimpleApplication>) org.apache.axis.utils.JavaUtils.convert(_resp,
							Collection.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof ApplicationServiceFault) {
					throw (ApplicationServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	/**
	 * @SuppressWarnings invoke方法返回值类型为Object为不定类型
	 */
	@SuppressWarnings("unchecked")
	public Collection<SimpleApplication> searchApplicationsByDeveloper(String developerId)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn",
				"searchApplicationsByDeveloper"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { developerId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (Collection<SimpleApplication>) _resp;
				} catch (Exception _exception) {
					return (Collection<SimpleApplication>) org.apache.axis.utils.JavaUtils.convert(_resp,
							Object[].class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof ApplicationServiceFault) {
					throw (ApplicationServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public boolean addApplication(String userAccount, String domainName,
			String applicationId) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName("http://client.webservice.eWAP.cn", "addApplication"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] { userAccount, domainName, applicationId });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return ((Boolean) _resp).booleanValue();
				} catch (Exception _exception) {
					return ((Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class))
							.booleanValue();
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof ApplicationServiceFault) {
					throw (ApplicationServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

}
