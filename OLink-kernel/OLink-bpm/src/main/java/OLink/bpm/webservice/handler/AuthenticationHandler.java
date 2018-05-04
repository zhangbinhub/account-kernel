////////////////////////////////////////////////////////////////
// COPYRIGHT (C) 2009 KOAL INTERNATIONAL INFORMATION CORPORATION
//
// ALL RIGHTS RESERVED BY KOAL INTERNATIONAL INFORMATION 
// CORPORATION, THIS PROGRAM MUST BE USED SOLELY FOR THE 
// PURPOSE FOR WHICH IT WAS FURNISHED BY KII CORPORATION ,
// NO PART OF THIS PROGRAM MAY BE REPRODUCED OR DISCLOSED 
// TO OTHERS, IN ANY FORM WITHOUT THE PRIOR WRITTEN 
// PERMISSION OF KII CORPORATION.USE OF COPYRIGHT NOTICE 
// DOES NOT EVIDENCE PUBLICATION OF THE PROGRAM
//
//KOAL INTERNATIONAL INFORMATION CONFIDENTIAL AND PROPROETARY
/////////////////////////////////////////////////////////////////

package OLink.bpm.webservice.handler;

import OLink.bpm.core.user.ejb.UserVO;
import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.security.SecurityProvider;
import org.apache.axis.security.simple.SimpleAuthenticatedUser;
import org.apache.axis.security.simple.SimpleSecurityProvider;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;

import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.PropertyUtil;

/**
 * 简单的使用用户名和密码作验证。 这个类复制了Axis自带的SimpleAuthenticationHandler类，只修改了其中或者用户名和密码的部分。
 * 因为发现 msgContext.getUsername();拿不到值。客户端使用call.getMessageContext().
 * setUsername方法不能把数据传输进来。
 * 客户端需要在SOAPHeader中放入用户名和密码属性。用户名和密码存储在服务端的WEB-INF/usrs.lst下。
 * 加入这些属性后生成的SOAP请求HEADER部分会增加如下内容。 <soapenv:Header> <ns1:username
 * soapenv:actor="http://schemas.xmlsoap.org/soap/actor/next"
 * soapenv:mustUnderstand="0" xsi:type="soapenc:string"
 * xmlns:ns1="Authorization"
 * xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/">test</ns1:username>
 * <ns2:password soapenv:actor="http://schemas.xmlsoap.org/soap/actor/next"
 * soapenv:mustUnderstand="0" xsi:type="soapenc:string"
 * xmlns:ns2="Authorization"
 * xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/">test</ns2:password>
 * </soapenv:Header>
 * 
 * 客户端加入代码如下： call.addHeader(new
 * SOAPHeaderElement("Authorization","username","changeit"));//用户名
 * call.addHeader(new
 * SOAPHeaderElement("Authorization","password",Config.getUserPassword()));//密码
 * 
 * @CopyRight KOAL Co. Lmt 2009
 * @author linn@bjkoal.com
 */
public class AuthenticationHandler extends BasicHandler {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2329407486293251431L;

	public AuthenticationHandler() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.axis.Handler#invoke(org.apache.axis.MessageContext)
	 */
	public void invoke(MessageContext msgContext) throws AxisFault {
		try {
			// 是否使用用户身份校验
			boolean useAuthentication = PropertyUtil.getBoolean("webservices.use.authentication");
			if (!useAuthentication) {
				return;
			}

			if (log.isDebugEnabled())
				log.debug("Enter: SimpleAuthenticationHandler::invoke");
			SecurityProvider provider = (SecurityProvider) msgContext.getProperty("securityProvider");
			if (provider == null) {
				provider = new SimpleSecurityProvider();
				msgContext.setProperty("securityProvider", provider);
			}
			if (provider != null) {
				// String userID = msgContext.getUsername();
				// 获取用户登录账号
				String userID = msgContext.getRequestMessage().getSOAPEnvelope().getHeaderByName(
						"Authorization", "loginno").getValue();
				if (log.isDebugEnabled())
					log.debug(Messages.getMessage("user00", userID));
				if (userID == null || userID.equals(""))
					throw new AxisFault("Server.Unauthenticated", Messages.getMessage("cantAuth00", userID), null, null);

				// String passwd = msgContext.getPassword();
				// 获取密码
				String passwd = msgContext.getRequestMessage().getSOAPEnvelope().getHeaderByName(
						"Authorization", "password").getValue();
				if (log.isDebugEnabled())
					log.debug(Messages.getMessage("password00", passwd));

				// 获取企业域
				String domain = msgContext.getRequestMessage().getSOAPEnvelope().getHeaderByName(
						"Authorization", "domain").getValue();
				if (log.isDebugEnabled())
					log.debug(Messages.getMessage("domain00", passwd));

				msgContext.setUsername(userID);
				msgContext.setPassword(passwd);

				UserProcess userProcess = (UserProcess) ProcessFactory.createProcess(UserProcess.class);

				UserVO user = userProcess.login(userID, passwd, domain);

				org.apache.axis.security.AuthenticatedUser authUser = new SimpleAuthenticatedUser(user.getName());
				if (authUser == null)
					throw new AxisFault("Server.Unauthenticated", Messages.getMessage("cantAuth01", userID), null, null);
				if (log.isDebugEnabled())
					log.debug(Messages.getMessage("auth00", userID));
				msgContext.setProperty("authenticatedUser", authUser);
			}
		} catch (Exception e) {
			throw new AxisFault("Server.Unauthenticated", e);
		}

		if (log.isDebugEnabled())
			log.debug("Exit: SimpleAuthenticationHandler::invoke");

	}

	protected static Log log;

	static {
		log = LogFactory.getLog((org.apache.axis.handlers.SimpleAuthenticationHandler.class).getName());
	}
}
