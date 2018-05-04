package pers.acp.communications.client.soap;

import pers.acp.communications.client.exceptions.WSException;
import pers.acp.tools.common.CommonTools;
import com.sun.xml.internal.ws.client.BindingProviderProperties;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class WebServiceClient {

    private static int MAX_TIMEOUT = 3600000;

    private static Logger log = Logger.getLogger(WebServiceClient.class);

    private SoapType soapType = SoapType.SOAP_1_2;

    private String wsdlLocation;

    private String namespace;

    private String serviceName;

    private String portName;

    private String methodName;

    private Map<String, String> patameterMap;

    private String returnName = "return";

    private int timeout = MAX_TIMEOUT;

    /**
     * 构造函数
     *
     * @param wsdlLocation wsdl地址
     */
    public WebServiceClient(String wsdlLocation) {
        this.wsdlLocation = wsdlLocation;
    }

    /**
     * 执行WebService请求
     *
     * @return 响应信息
     */
    public String doCallWebService() {
        try {
            if (CommonTools.isNullStr(namespace)) {
                throw new WSException("namespace is null");
            }
            if (CommonTools.isNullStr(serviceName)) {
                throw new WSException("serviceName is null");
            }
            if (CommonTools.isNullStr(portName)) {
                throw new WSException("portName is null");
            }
            if (CommonTools.isNullStr(methodName)) {
                throw new WSException("namespace is null");
            }
            if (patameterMap == null || patameterMap.isEmpty()) {
                throw new WSException("patameterMap is null");
            }
            return doWSRequest();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 执行WebService请求
     *
     * @return 响应信息
     */
    private String doWSRequest() throws Exception {
        if (timeout > MAX_TIMEOUT) {
            timeout = MAX_TIMEOUT;
        }
        URL url = new URL(wsdlLocation);
        QName sname = new QName(namespace, serviceName);
        Service service = Service.create(url, sname);

        Dispatch<SOAPMessage> dispatch = service.createDispatch(new QName(namespace, portName), SOAPMessage.class, Service.Mode.MESSAGE);
        dispatch.getRequestContext().put("thread.local.request.context", Boolean.TRUE);
        dispatch.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, timeout);
        dispatch.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, timeout);
        dispatch.getRequestContext().put("set-jaxb-validation-event-handler:jaxb", Boolean.TRUE);
        dispatch.getRequestContext().put("schema-validation-enabled:schema", Boolean.TRUE);
        SOAPMessage msg = buildSOAPMessage();
        SOAPMessage response = dispatch.invoke(msg);
        return getReturnString(response);
    }

    /**
     * 构建WebService请求消息
     *
     * @return 响应信息
     */
    private SOAPMessage buildSOAPMessage() throws SOAPException {
        SOAPMessage msg = MessageFactory.newInstance(soapType.getName()).createMessage();
        SOAPEnvelope envelope = msg.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();

        QName ename = new QName(namespace, methodName, "n1");
        SOAPBodyElement ele = body.addBodyElement(ename);
        for (Entry<String, String> entry : patameterMap.entrySet()) {
            ele.addChildElement(entry.getKey()).setValue(entry.getValue());
        }
        return msg;
    }

    /**
     * 获取WebService请求报文（SOAP+XML）
     *
     * @return 信息字符串
     */
    public String getSOAPMessageString() {
        try {
            SOAPMessage msg = buildSOAPMessage();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            msg.writeTo(bout);
            return bout.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * 获取WebService返回
     *
     * @param response 响应信息对象
     * @return 响应信息值
     */
    private String getReturnString(SOAPMessage response) throws SOAPException {
        Document doc = response.getSOAPPart().getEnvelope().getBody().extractContentAsDocument();
        return doc.getElementsByTagName(returnName).item(0).getTextContent();
    }

    /**
     * 获取WebService返回
     *
     * @param responseString 响应字符串
     * @param charset        字符集
     * @return 响应信息值
     */
    public String getReturnString(String responseString, String charset) {
        MessageFactory msgFactory;
        try {
            msgFactory = MessageFactory.newInstance(soapType.getName());
            byte[] bytes;
            if (CommonTools.isNullStr(charset)) {
                bytes = responseString.getBytes();
            } else {
                bytes = responseString.getBytes(charset);
            }
            SOAPMessage reqMsg = msgFactory.createMessage(new MimeHeaders(), new ByteArrayInputStream(bytes));
            reqMsg.saveChanges();
            return getReturnString(reqMsg);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    public SoapType getSoapType() {
        return soapType;
    }

    /**
     * 设置soap协议类型，默认soap1.2
     *
     * @param soapType
     */
    public void setSoapType(SoapType soapType) {
        this.soapType = soapType;
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, String> getPatameterMap() {
        return patameterMap;
    }

    public void setPatameterMap(Map<String, String> patameterMap) {
        this.patameterMap = patameterMap;
    }

    public String getReturnName() {
        return returnName;
    }

    /**
     * 设置返回节点名称，默认“return”
     *
     * @param returnName 结果名称
     */
    public void setReturnName(String returnName) {
        this.returnName = returnName;
    }

    public int getTimeout() {
        return timeout;
    }

    /**
     * 设置请求超时时间，单位毫秒，默认1小时，最大1小时
     *
     * @param timeout 超时时间，单位毫秒
     */
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
