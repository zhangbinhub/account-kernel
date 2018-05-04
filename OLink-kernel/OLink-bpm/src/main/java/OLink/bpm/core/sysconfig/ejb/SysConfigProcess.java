package OLink.bpm.core.sysconfig.ejb;

public interface SysConfigProcess {
	
	void save(AuthConfig authConfig, LdapConfig ldapConfig, EmailConfig emailConfig, ImConfig imConfig, CheckoutConfig checkoutConfig) throws Exception;
	AuthConfig getAuthConfig() throws Exception;
	LdapConfig getLdapConfig() throws Exception;
	EmailConfig getEmailConfig() throws Exception;
	ImConfig getImConfig() throws Exception;
	CheckoutConfig getCheckoutConfig() throws Exception;
}
