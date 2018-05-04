package OLink.bpm.core.sysconfig.ejb;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.util.StringUtil;
import services.BaseProcess;

import eWAP.core.Tools;

public class SysConfigProcessBean implements SysConfigProcess {

	public AuthConfig getAuthConfig() throws Exception {
		AuthConfig authConfig = new AuthConfig();
		PropertyUtil.reload("sso");
		authConfig
				.setAuthType(PropertyUtil.get(AuthConfig.AUTHENTICATION_TYPE));
		authConfig.setLoginAuth(PropertyUtil
				.get(AuthConfig.LOGIN_AUTHENTICATION));
		authConfig.setSsoAuth(PropertyUtil.get(AuthConfig.SSO_IMLEMENTATION));
		authConfig.setSsoDefaultEmail(PropertyUtil
				.get(AuthConfig.SSO_DEFAULT_EMAIL));
		authConfig.setSsoDefaultPassword(PropertyUtil
				.get(AuthConfig.SSO_DEFAULT_PASSWORD));
		authConfig.setSsoRedirect(PropertyUtil.get(AuthConfig.SSO_REDIRECT));
		authConfig.setSsoLogoutRedirect(PropertyUtil
				.get(AuthConfig.SSO_LOGOUT_REDIRECT));
		authConfig.setCasLoginUrl(PropertyUtil
				.get(AuthConfig.CAS_SERVER_LOGIN_URL));
		authConfig.setCasUrlPrefix(PropertyUtil
				.get(AuthConfig.CAS_SERVER_URL_PREFIX));
		authConfig.setLocalServerName(PropertyUtil
				.get(AuthConfig.LOCAL_SERVER_NAME));
		authConfig.setSsoSaveType(PropertyUtil
				.get(AuthConfig.SSO_INFO_SAVE_TYPE));
		authConfig.setSsoKeyLoginAccount(PropertyUtil
				.get(AuthConfig.SSO_INFO_KEY_LOGINACCOUNT));
		authConfig.setSsoKeyPassword(PropertyUtil
				.get(AuthConfig.SSO_INFO_KEY_PASSWORD));
		authConfig.setSsoKeyDomain(PropertyUtil
				.get(AuthConfig.SSO_INFO_KEY_DOMAINNAME));
		authConfig.setSsoKeyEmail(PropertyUtil
				.get(AuthConfig.SSO_INFO_KEY_EMAIL));
		authConfig.setSsoDataEncryption(PropertyUtil
				.get(AuthConfig.SSO_INFO_DATA_ENCRYPTION));
		authConfig.setAdDefaultDomain(PropertyUtil
				.get(AuthConfig.AD_DEFAULT_DOMAIN));
		authConfig.setAdDomainController(PropertyUtil
				.get(AuthConfig.AD_DOMAIN_CONTROLLER));
		authConfig.setSmsAuthenticate(PropertyUtil
				.get(AuthConfig.SMS_AUTHENTICATE));
		authConfig.setSmsTimeout(PropertyUtil
				.get(AuthConfig.SMS_TIMEOUT));
		authConfig.setSmsContent(PropertyUtil
				.get(AuthConfig.SMS_CONTENT));
		authConfig.setSmsAffectMode(PropertyUtil
				.get(AuthConfig.SMS_AFFECTMODE));
		authConfig.setSmsStartRangeIp(PropertyUtil
				.get(AuthConfig.SMS_STARTRANGEIP));
		authConfig.setSmsEndRangeIp(PropertyUtil
				.get(AuthConfig.SMS_ENDRANGEIP));
		return authConfig;
	}

	public EmailConfig getEmailConfig() throws Exception {
		EmailConfig emailConfig = new EmailConfig();
		PropertyUtil.reload("email");
		emailConfig.setSendHost(PropertyUtil.get(EmailConfig.EMAIL_SEND_HOST));
		emailConfig.setSendAddress(PropertyUtil
				.get(EmailConfig.EMAIL_SEND_ADDRESS));
		emailConfig.setSendAccount(PropertyUtil.get(EmailConfig.EMAIL_USER));
		emailConfig.setSendPassword(PropertyUtil
				.get(EmailConfig.EMAIL_SEND_PASSWORD));
		emailConfig
				.setCcAddress(PropertyUtil.get(EmailConfig.EMAIL_CC_ADDRESS));
		emailConfig.setIsUseClient(PropertyUtil
				.get(EmailConfig.USE_EMAIL_CLIENT));
		emailConfig.setIsUseInnerEmail(PropertyUtil
				.get(EmailConfig.USE_INNER_EMAIL));
		emailConfig.setFunctionDomain(PropertyUtil
				.get(EmailConfig.EMAIL_FUNCTION_DOAMIN));
		emailConfig.setTrash(PropertyUtil.get(EmailConfig.EMAIL_TRASH));
		emailConfig.setSender(PropertyUtil.get(EmailConfig.EMAIL_SENDER));
		emailConfig.setDraft(PropertyUtil.get(EmailConfig.EMAIL_DRAFT));
		emailConfig.setRemoved(PropertyUtil.get(EmailConfig.EMAIL_REMOVED));
		emailConfig.setFetchServer(PropertyUtil
				.get(EmailConfig.EMAIL_RECEIVE_SERVER));
		emailConfig.setFetchServerPort(PropertyUtil
				.get(EmailConfig.EMAIL_RECEIVE_SERVER_PORT));
		emailConfig.setFetchProtocol(PropertyUtil
				.get(EmailConfig.EMAIL_RECEIVE_PROTOCOL));
		emailConfig.setFetchssl(PropertyUtil
				.get(EmailConfig.EMAIL_RECEIVE_NEED_CERTIFICATE));
		emailConfig.setSmtpServer(PropertyUtil
				.get(EmailConfig.EMAIL_SEND_SERVER));
		emailConfig.setSmtpServerPort(PropertyUtil
				.get(EmailConfig.EMAIL_SEND_SERVER_PORT));
		emailConfig.setSmtpAuthenticated(PropertyUtil
				.get(EmailConfig.EMAIL_ENABLE_ACCESSOORIES));
		emailConfig.setSmtpssl(PropertyUtil
				.get(EmailConfig.EMAIL_SEND_NEED_CERTIFICATE));
		return emailConfig;
	}

	public LdapConfig getLdapConfig() throws Exception {
		LdapConfig ldapConfig = new LdapConfig();
		PropertyUtil.reload("sso");
		ldapConfig.setUrl(PropertyUtil.get(LdapConfig.LDAP_URL));
		ldapConfig.setBaseDN(PropertyUtil.get(LdapConfig.LDAP_BASEDN));
		ldapConfig.setPooled(PropertyUtil.get(LdapConfig.LDAP_POOLED));
		ldapConfig.setDirStructure(PropertyUtil.get(LdapConfig.DIRSTRUCTURE));
		ldapConfig.setId_(PropertyUtil.get(LdapConfig.ID));
		ldapConfig.setLoginno_(PropertyUtil.get(LdapConfig.LOGINNO));
		ldapConfig.setLoginpwd_(PropertyUtil.get(LdapConfig.LOGINPWD));
		ldapConfig.setName_(PropertyUtil.get(LdapConfig.NAME));
		ldapConfig.setEmail_(PropertyUtil.get(LdapConfig.EMAIL));
		ldapConfig.setTelephone_(PropertyUtil.get(LdapConfig.TELEPHONE));
		return ldapConfig;
	}
	
	/**
	 * 获取IM的配置
	 * @return
	 * @throws Exception
	 */
	public ImConfig getImConfig() throws Exception{
		ImConfig imConfig = new ImConfig();
		PropertyUtil.reload("im");
		imConfig.setOpen(PropertyUtil.get(ImConfig.GKE_API_OPEN));
		imConfig.setIp(PropertyUtil.get(ImConfig.GKE_API_IP));
		imConfig.setPort(PropertyUtil.get(ImConfig.GKE_SERVER_PORT));
		imConfig.setLoginno(PropertyUtil.get(ImConfig.GKE_SERVER_LOGINNO));
		imConfig.setPassword(PropertyUtil.get(ImConfig.GKE_SERVER_PASSWORD));
		return imConfig;
	}
	
	public CheckoutConfig getCheckoutConfig() throws Exception {
		CheckoutConfig checkoutConfig = new CheckoutConfig();
		PropertyUtil.reload("checkout");
		checkoutConfig.setInvocation((PropertyUtil.get(CheckoutConfig.INVOCATION)).equals("true"));
		return checkoutConfig;
	}

	public void save(AuthConfig authConfig, LdapConfig ldapConfig,
			EmailConfig emailConfig,ImConfig imConfig,CheckoutConfig checkoutConfig) throws IOException {
		Properties properties1 = new Properties();
		Properties properties2 = new Properties();
		/**
		 * IM
		 */
		Properties properties3 = new Properties();
		/**
		 * checkoutConfig
		 */
		Properties properties4 = new Properties();
//		String file1 = SysConfigProcessBean.class.getClassLoader().getResource("sso.properties").getFile();
//		String file2 = SysConfigProcessBean.class.getClassLoader().getResource("email.properties").getFile();
		String file1 =  Tools.getFullPathRelateClass("../sso.properties", BaseProcess.class);
		String file2 =  Tools.getFullPathRelateClass("../email.properties", BaseProcess.class);
		/**
		 * IM
		 */
//		String file3 = SysConfigProcessBean.class.getClassLoader().getResource("im.properties").getFile();
//		String file4 = SysConfigProcessBean.class.getClassLoader().getResource("checkout.properties").getFile();
		String file3 =  Tools.getFullPathRelateClass("../im.properties", BaseProcess.class);
		String file4 =  Tools.getFullPathRelateClass("../checkout.properties", BaseProcess.class);
		
		if (authConfig != null && ldapConfig != null && file1 != null) {
			// 身份验证设置
			properties1.setProperty(AuthConfig.AUTHENTICATION_TYPE, authConfig
					.getAuthType() != null ? authConfig.getAuthType() : "");
			properties1.setProperty(AuthConfig.LOGIN_AUTHENTICATION,
					!StringUtil.isBlank(authConfig.getLoginAuth()) ? authConfig
							.getLoginAuth() : AuthConfig
							.getLoginAuth("default"));
			properties1.setProperty(AuthConfig.SSO_IMLEMENTATION, authConfig
					.getSsoAuth() != null ? authConfig.getSsoAuth() : "");
			properties1.setProperty(AuthConfig.SSO_DEFAULT_EMAIL, authConfig
					.getSsoDefaultEmail() != null ? authConfig
					.getSsoDefaultEmail() : "");
			properties1.setProperty(AuthConfig.SSO_DEFAULT_PASSWORD, authConfig
					.getSsoDefaultPassword() != null ? authConfig
					.getSsoDefaultPassword() : "");
			properties1.setProperty(AuthConfig.SSO_REDIRECT, authConfig
					.getSsoRedirect() != null ? authConfig.getSsoRedirect()
					: "");
			properties1.setProperty(AuthConfig.SSO_LOGOUT_REDIRECT, authConfig
					.getSsoLogoutRedirect() != null ? authConfig
					.getSsoLogoutRedirect() : "");
			properties1.setProperty(AuthConfig.CAS_SERVER_LOGIN_URL, authConfig
					.getCasLoginUrl() != null ? authConfig.getCasLoginUrl()
					: "");
			properties1.setProperty(AuthConfig.CAS_SERVER_URL_PREFIX,
					authConfig.getCasUrlPrefix() != null ? authConfig
							.getCasUrlPrefix() : "");
			properties1.setProperty(AuthConfig.LOCAL_SERVER_NAME, authConfig
					.getLocalServerName() != null ? authConfig
					.getLocalServerName() : "");
			properties1.setProperty(AuthConfig.SSO_INFO_SAVE_TYPE, authConfig
					.getSsoSaveType() != null ? authConfig.getSsoSaveType()
					: "");
			properties1.setProperty(AuthConfig.SSO_INFO_KEY_LOGINACCOUNT,
					authConfig.getSsoKeyLoginAccount() != null ? authConfig
							.getSsoKeyLoginAccount() : "");
			properties1.setProperty(AuthConfig.SSO_INFO_KEY_PASSWORD,
					authConfig.getSsoKeyPassword() != null ? authConfig
							.getSsoKeyPassword() : "");
			properties1.setProperty(AuthConfig.SSO_INFO_KEY_DOMAINNAME,
					authConfig.getSsoKeyDomain() != null ? authConfig
							.getSsoKeyDomain() : "");
			properties1.setProperty(AuthConfig.SSO_INFO_KEY_EMAIL, authConfig
					.getSsoKeyEmail() != null ? authConfig.getSsoKeyEmail()
					: "");
			properties1.setProperty(AuthConfig.SSO_INFO_DATA_ENCRYPTION,
					authConfig.getSsoDataEncryption() != null ? authConfig
							.getSsoDataEncryption() : "");
			properties1.setProperty(AuthConfig.AD_DOMAIN_CONTROLLER,
					authConfig.getAdDomainController() != null ? authConfig
							.getAdDomainController() : "");
			properties1.setProperty(AuthConfig.AD_DEFAULT_DOMAIN,
					authConfig.getAdDefaultDomain() != null ? authConfig
							.getAdDefaultDomain() : "");
			properties1.setProperty(AuthConfig.SMS_AUTHENTICATE,
					authConfig.getSmsAuthenticate() != null ? authConfig
							.getSmsAuthenticate() : "false");
			properties1.setProperty(AuthConfig.SMS_TIMEOUT,
					authConfig.getSmsTimeout() != null ? authConfig
							.getSmsTimeout() : "0");
			properties1.setProperty(AuthConfig.SMS_CONTENT,
					authConfig.getSmsContent() != null ? authConfig
							.getSmsContent() : "");
			properties1.setProperty(AuthConfig.SMS_AFFECTMODE,
					authConfig.getSmsAffectMode() != null ? authConfig
							.getSmsAffectMode() : "");
			properties1.setProperty(AuthConfig.SMS_STARTRANGEIP,
					authConfig.getSmsStartRangeIp() != null ? authConfig
							.getSmsStartRangeIp() : "");
			properties1.setProperty(AuthConfig.SMS_ENDRANGEIP,
					authConfig.getSmsEndRangeIp() != null ? authConfig
							.getSmsEndRangeIp() : "");
			
			// ldap的设置
			properties1.setProperty(LdapConfig.LDAP_URL,
					ldapConfig.getUrl() != null ? ldapConfig.getUrl() : "");
			properties1.setProperty(LdapConfig.LDAP_BASEDN, ldapConfig
					.getBaseDN() != null ? ldapConfig.getBaseDN() : "");
			properties1.setProperty(LdapConfig.LDAP_POOLED, ldapConfig
					.getPooled() != null ? ldapConfig.getPooled() : "");
			properties1.setProperty(LdapConfig.DIRSTRUCTURE, ldapConfig
					.getDirStructure() != null ? ldapConfig.getDirStructure()
					: "");
			properties1.setProperty(LdapConfig.ID,
					ldapConfig.getId_() != null ? ldapConfig.getId_() : "");
			properties1.setProperty(LdapConfig.NAME,
					ldapConfig.getName_() != null ? ldapConfig.getName_() : "");
			properties1.setProperty(LdapConfig.LOGINNO, ldapConfig
					.getLoginno_() != null ? ldapConfig.getLoginno_() : "");
			properties1.setProperty(LdapConfig.LOGINPWD, ldapConfig
					.getLoginpwd_() != null ? ldapConfig.getLoginpwd_() : "");
			properties1.setProperty(LdapConfig.EMAIL,
					ldapConfig.getEmail_() != null ? ldapConfig.getEmail_()
							: "");
			properties1.setProperty(LdapConfig.TELEPHONE, ldapConfig
					.getTelephone_() != null ? ldapConfig.getTelephone_() : "");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file1);
				properties1
						.store(fos,
								"#####################auth and ldap setting#####################");
				PropertyUtil.reload("sso");
				PropertyUtil.reload("email");
			} catch (IOException e) {
				throw e;
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (emailConfig != null && file2 != null) {
			// email的设置
			properties2.setProperty(EmailConfig.EMAIL_SEND_HOST, emailConfig
					.getSendHost() != null ? emailConfig.getSendHost() : "");
			properties2.setProperty(EmailConfig.EMAIL_SEND_ADDRESS, emailConfig
					.getSendAddress() != null ? emailConfig.getSendAddress()
					: "");
			properties2.setProperty(EmailConfig.EMAIL_USER, emailConfig
					.getSendAccount() != null ? emailConfig.getSendAccount()
					: "");
			properties2.setProperty(EmailConfig.EMAIL_SEND_PASSWORD,
					emailConfig.getSendPassword() != null ? emailConfig
							.getSendPassword() : "");
			properties2.setProperty(EmailConfig.EMAIL_CC_ADDRESS, emailConfig
					.getCcAddress() != null ? emailConfig.getCcAddress() : "");
			properties2.setProperty(EmailConfig.USE_EMAIL_CLIENT, emailConfig
					.getIsUseClient() != null ? emailConfig.getIsUseClient()
					: "");
			properties2.setProperty(EmailConfig.USE_INNER_EMAIL, emailConfig
					.getIsUseInnerEmail() != null ? emailConfig
					.getIsUseInnerEmail() : "");
			properties2.setProperty(EmailConfig.EMAIL_FUNCTION_DOAMIN,
					emailConfig.getFunctionDomain() != null ? emailConfig
							.getFunctionDomain() : "");
			properties2.setProperty(EmailConfig.EMAIL_TRASH, emailConfig
					.getTrash() != null ? emailConfig.getTrash() : "");
			properties2.setProperty(EmailConfig.EMAIL_SENDER, emailConfig
					.getSender() != null ? emailConfig.getSender() : "");
			properties2.setProperty(EmailConfig.EMAIL_DRAFT, emailConfig
					.getDraft() != null ? emailConfig.getDraft() : "");
			properties2.setProperty(EmailConfig.EMAIL_REMOVED, emailConfig
					.getRemoved() != null ? emailConfig.getRemoved() : "");
			properties2.setProperty(EmailConfig.EMAIL_RECEIVE_SERVER,
					emailConfig.getFetchServer() != null ? emailConfig
							.getFetchServer() : "");
			properties2.setProperty(EmailConfig.EMAIL_RECEIVE_SERVER_PORT,
					emailConfig.getFetchServerPort() != null ? emailConfig
							.getFetchServerPort() : "");
			properties2.setProperty(EmailConfig.EMAIL_RECEIVE_PROTOCOL,
					emailConfig.getFetchProtocol() != null ? emailConfig
							.getFetchProtocol() : "");
			properties2.setProperty(EmailConfig.EMAIL_RECEIVE_NEED_CERTIFICATE,
					emailConfig.getFetchssl() != null ? emailConfig
							.getFetchssl() : "");
			properties2
					.setProperty(EmailConfig.EMAIL_SEND_SERVER, emailConfig
							.getSmtpServer() != null ? emailConfig
							.getSmtpServer() : "");
			properties2.setProperty(EmailConfig.EMAIL_SEND_SERVER_PORT,
					emailConfig.getSmtpServerPort() != null ? emailConfig
							.getSmtpServerPort() : "");
			properties2.setProperty(EmailConfig.EMAIL_ENABLE_ACCESSOORIES,
					emailConfig.getSmtpAuthenticated() != null ? emailConfig
							.getSmtpAuthenticated() : "");
			properties2.setProperty(EmailConfig.EMAIL_SEND_NEED_CERTIFICATE,
					emailConfig.getSmtpssl() != null ? emailConfig.getSmtpssl()
							: "");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file2);
				properties2
						.store(fos,
								"#####################email setting#####################");
			} catch (IOException e) {
				try {
					throw e;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		if (imConfig != null && file3 != null) {
			// IM的设置
			properties3.setProperty(ImConfig.GKE_API_OPEN, imConfig.getOpen()!=null?imConfig.getOpen():"0");
			properties3.setProperty(ImConfig.GKE_API_IP, imConfig.getIp() != null ? imConfig.getIp() : "127.0.0.1");
			properties3.setProperty(ImConfig.GKE_SERVER_PORT, imConfig.getPort() != null ? imConfig.getPort() : "8900");
			properties3.setProperty(ImConfig.GKE_SERVER_LOGINNO, imConfig.getLoginno() != null ? imConfig.getLoginno() : "admin");
			properties3.setProperty(ImConfig.GKE_SERVER_PASSWORD, imConfig.getPassword() != null ? imConfig.getPassword() : "admin");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file3);
				properties3.store(fos,"#####################im setting#####################");
			} catch (IOException e) {
				try {
					throw e;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		if(file4 != null){
			if(checkoutConfig != null){
				properties4.setProperty(CheckoutConfig.INVOCATION, "true");
			}else if(checkoutConfig == null){
				properties4.setProperty(CheckoutConfig.INVOCATION, "false");
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file4);
				properties4.store(fos,"#####################checkout setting#####################");
			} catch (IOException e) {
				try {
					throw e;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		PropertyUtil.reload("sso");
		PropertyUtil.reload("email");
		PropertyUtil.reload("im");
		PropertyUtil.reload("checkout");
		OLink.bpm.core.email.util.EmailConfig.initEmailConfig();
	}

}
