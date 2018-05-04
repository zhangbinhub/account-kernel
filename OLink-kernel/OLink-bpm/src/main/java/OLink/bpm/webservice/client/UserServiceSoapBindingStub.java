/**
 * UserServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.client;

import OLink.bpm.webservice.fault.UserServiceFault;
import OLink.bpm.webservice.model.SimpleUser;

public class UserServiceSoapBindingStub extends org.apache.axis.client.Stub
		implements UserService {
	private java.util.Vector<Object> cachedSerClasses = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedSerQNames = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedSerFactories = new java.util.Vector<Object>();
	private java.util.Vector<Object> cachedDeserFactories = new java.util.Vector<Object>();

	static org.apache.axis.description.OperationDesc[] _operations;

	static {
		_operations = new org.apache.axis.description.OperationDesc[8];
		_initOperationDesc1();
	}

	private static void _initOperationDesc1() {
		org.apache.axis.description.OperationDesc oper;
		org.apache.axis.description.ParameterDesc param;
		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("createUser");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "user"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn",
						"SimpleUser"),
				SimpleUser.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"urn:model.webservice.eWAP.cn", "SimpleUser"));
		oper.setReturnClass(SimpleUser.class);
		oper.setReturnQName(new javax.xml.namespace.QName("",
				"createUserReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[0] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("getUser");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "pk"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"),
				String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"urn:model.webservice.eWAP.cn", "SimpleUser"));
		oper.setReturnClass(SimpleUser.class);
		oper.setReturnQName(new javax.xml.namespace.QName("", "getUserReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[1] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("validateUser");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "domainName"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"),
				String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "userAccount"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"),
				String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "userPassword"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"),
				String.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "userType"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://www.w3.org/2001/XMLSchema", "int"), int.class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(new javax.xml.namespace.QName(
				"urn:model.webservice.eWAP.cn", "SimpleUser"));
		oper.setReturnClass(SimpleUser.class);
		oper.setReturnQName(new javax.xml.namespace.QName("",
				"validateUserReturn"));
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[2] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("updateUser");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "user"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn",
						"SimpleUser"),
				SimpleUser.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[3] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("deleteUser");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "pk"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://schemas.xmlsoap.org/soap/encoding/", "string"),
				String.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[4] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("deleteUser");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "user"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn",
						"SimpleUser"),
				SimpleUser.class, false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[5] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setRoleSet");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "user"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn",
						"SimpleUser"),
				SimpleUser.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "roles"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn",
						"ArrayOf_soapenc_string"), String[].class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[6] = oper;

		oper = new org.apache.axis.description.OperationDesc();
		oper.setName("setDepartmentSet");
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "user"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn",
						"SimpleUser"),
				SimpleUser.class, false, false);
		oper.addParameter(param);
		param = new org.apache.axis.description.ParameterDesc(
				new javax.xml.namespace.QName("", "deps"),
				org.apache.axis.description.ParameterDesc.IN,
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn",
						"ArrayOf_soapenc_string"), String[].class,
				false, false);
		oper.addParameter(param);
		oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
		oper.setStyle(org.apache.axis.constants.Style.RPC);
		oper.setUse(org.apache.axis.constants.Use.ENCODED);
		oper.addFault(new org.apache.axis.description.FaultDesc(
				new javax.xml.namespace.QName(
						"http://client.webservice.eWAP.cn", "fault"),
				"UserServiceFault",
				new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
						"UserServiceFault"), true));
		_operations[7] = oper;

	}

	public UserServiceSoapBindingStub() throws org.apache.axis.AxisFault {
		this(null);
	}

	public UserServiceSoapBindingStub(java.net.URL endpointURL,
			javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
		this(service);
		super.cachedEndpoint = endpointURL;
	}

	public UserServiceSoapBindingStub(javax.xml.rpc.Service service)
			throws org.apache.axis.AxisFault {
		if (service == null) {
			super.service = new org.apache.axis.client.Service();
		} else {
			super.service = service;
		}
		((org.apache.axis.client.Service) super.service)
				.setTypeMappingVersion("1.2");
		Class<?> cls;
		javax.xml.namespace.QName qName;
		javax.xml.namespace.QName qName2;
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
		qName = new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "ArrayOf_soapenc_string");
		cachedSerQNames.add(qName);
		cls = String[].class;
		cachedSerClasses.add(cls);
		qName = new javax.xml.namespace.QName(
				"http://schemas.xmlsoap.org/soap/encoding/", "string");
		qName2 = null;
		cachedSerFactories
				.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(
						qName, qName2));
		cachedDeserFactories
				.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

		qName = new javax.xml.namespace.QName("urn:fault.webservice.eWAP.cn",
				"UserServiceFault");
		cachedSerQNames.add(qName);
		cls = UserServiceFault.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

		qName = new javax.xml.namespace.QName("urn:model.webservice.eWAP.cn",
				"SimpleUser");
		cachedSerQNames.add(qName);
		cls = SimpleUser.class;
		cachedSerClasses.add(cls);
		cachedSerFactories.add(beansf);
		cachedDeserFactories.add(beandf);

	}

	protected org.apache.axis.client.Call createCall()
			throws java.rmi.RemoteException {
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
					_call
							.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
					_call
							.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
					for (int i = 0; i < cachedSerFactories.size(); ++i) {
						Class<?> cls = (Class<?>) cachedSerClasses
								.get(i);
						javax.xml.namespace.QName qName = (javax.xml.namespace.QName) cachedSerQNames
								.get(i);
						Object x = cachedSerFactories.get(i);
						if (x instanceof Class<?>) {
							Class<?> sf = (Class<?>) cachedSerFactories
									.get(i);
							Class<?> df = (Class<?>) cachedDeserFactories
									.get(i);
							_call
									.registerTypeMapping(cls, qName, sf, df,
											false);
						} else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
							org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory) cachedSerFactories
									.get(i);
							org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory) cachedDeserFactories
									.get(i);
							_call
									.registerTypeMapping(cls, qName, sf, df,
											false);
						}
					}
				}
			}
			return _call;
		} catch (Throwable _t) {
			throw new org.apache.axis.AxisFault(
					"Failure trying to get the Call object", _t);
		}
	}

	public SimpleUser createUser(
			SimpleUser user)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[0]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "createUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call
					.invoke(new Object[] { user });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (SimpleUser) _resp;
				} catch (Exception _exception) {
					return (SimpleUser) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									SimpleUser.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public SimpleUser getUser(String pk)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[1]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "getUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call
					.invoke(new Object[] { pk });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (SimpleUser) _resp;
				} catch (Exception _exception) {
					return (SimpleUser) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									SimpleUser.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public SimpleUser validateUser(
			String domainName, String userAccount,
			String userPassword, int userType)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[2]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "validateUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] {
					domainName, userAccount, userPassword,
					Integer.valueOf(userType) });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			} else {
				extractAttachments(_call);
				try {
					return (SimpleUser) _resp;
				} catch (Exception _exception) {
					return (SimpleUser) org.apache.axis.utils.JavaUtils
							.convert(_resp,
									SimpleUser.class);
				}
			}
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void updateUser(SimpleUser user)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[3]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "updateUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call
					.invoke(new Object[] { user });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void deleteUser(String pk)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[4]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "deleteUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call
					.invoke(new Object[] { pk });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void deleteUser(SimpleUser user)
			throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[5]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "deleteUser"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call
					.invoke(new Object[] { user });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void setRoleSet(SimpleUser user,
			String[] roles) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[6]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "setRoleSet"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] {
					user, roles });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

	public void setDepartmentSet(SimpleUser user,
			String[] deps) throws java.rmi.RemoteException {
		if (super.cachedEndpoint == null) {
			throw new org.apache.axis.NoEndPointException();
		}
		org.apache.axis.client.Call _call = createCall();
		_call.setOperation(_operations[7]);
		_call.setUseSOAPAction(true);
		_call.setSOAPActionURI("");
		_call
				.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
		_call.setOperationName(new javax.xml.namespace.QName(
				"http://client.webservice.eWAP.cn", "setDepartmentSet"));

		setRequestHeaders(_call);
		setAttachments(_call);
		try {
			Object _resp = _call.invoke(new Object[] {
					user, deps });

			if (_resp instanceof java.rmi.RemoteException) {
				throw (java.rmi.RemoteException) _resp;
			}
			extractAttachments(_call);
		} catch (org.apache.axis.AxisFault axisFaultException) {
			if (axisFaultException.detail != null) {
				if (axisFaultException.detail instanceof java.rmi.RemoteException) {
					throw (java.rmi.RemoteException) axisFaultException.detail;
				}
				if (axisFaultException.detail instanceof UserServiceFault) {
					throw (UserServiceFault) axisFaultException.detail;
				}
			}
			throw axisFaultException;
		}
	}

}
