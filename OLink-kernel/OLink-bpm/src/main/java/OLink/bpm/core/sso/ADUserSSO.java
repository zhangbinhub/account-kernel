package OLink.bpm.core.sso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.constans.Web;
import OLink.bpm.core.sso.ntlm.OBPMNtlmSsp;
import OLink.bpm.util.property.PropertyUtil;
import jcifs.Config;
import jcifs.UniAddress;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbSession;
import jcifs.util.Base64;

public class ADUserSSO implements SSO {

	public Map<String, String> authenticateUser(HttpServletRequest req,
			HttpServletResponse resp) {

		initJcifsConfig();
		Map<String, String> userInfo = new HashMap<String, String>();
		UniAddress dc = null;
		String msg = req.getHeader("Authorization");
		NtlmPasswordAuthentication ntlm = null;
		if (msg != null) {
			if (msg.startsWith("NTLM ")) {
				// HttpSession ssn = req.getSession();
				try {
					/**
					 * 获取AD主机地址
					 */
					dc = UniAddress.getByName(PropertyUtil
							.get(Web.SSO_AD_DOMAINCONTROLLER), true);
					/**
					 * 获取challenge
					 */
					byte[] challenge = SmbSession.getChallenge(dc);
					ntlm = OBPMNtlmSsp.authenticate(req, resp, challenge);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					String auth = new String(Base64.decode(msg.substring(6)),
							"US-ASCII");
					int index = auth.indexOf(':');
					String user = (index != -1) ? auth.substring(0, index)
							: auth;
					String password = (index != -1) ? auth.substring(index + 1)
							: "";
					index = user.indexOf('\\');
					if (index == -1)
						index = user.indexOf('/');
					String domain = (index != -1) ? user.substring(0, index)
							: PropertyUtil.get(Web.SSO_AD_DEFAULTDOMAIN);
					user = (index != -1) ? user.substring(index + 1) : user;
					ntlm = new NtlmPasswordAuthentication(domain, user,
							password);
					dc = UniAddress.getByName(PropertyUtil
							.get(Web.SSO_AD_DOMAINCONTROLLER), true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				if (ntlm != null) {
					SmbSession.logon(dc, ntlm);
					userInfo.put(Web.SSO_LOGINACCOUNT_ATTRIBUTE, ntlm
							.getUsername());
					userInfo
							.put(Web.SSO_DOMAINNAME_ATTRIBUTE, ntlm.getDomain());
				}
			} catch (SmbException e) {
				e.printStackTrace();
			}
		} else {
			resp.setHeader("WWW-Authenticate", "NTLM");
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.setContentLength(0);
			try {
				resp.flushBuffer();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return userInfo;
	}

	public Map<String, String> authenticateUser(PortletRequest req) {
		/** 目前还不支持portlet **/
		throw new RuntimeException(
				"Currently does not support portlet interface");
	}

	private void initJcifsConfig() {
		/*
		 * Set jcifs properties we know we want; soTimeout and cachePolicy to
		 * 30min.
		 */
		Config.setProperty("jcifs.smb.client.soTimeout", "1800000");
		Config.setProperty("jcifs.netbios.cachePolicy", "1200");
		/*
		 * The Filter can only work with NTLMv1 as it uses a man-in-the-middle
		 * techinque that NTLMv2 specifically thwarts. A real NTLM Filter would
		 * need to do a NETLOGON RPC that JCIFS will likely never implement
		 * because it requires a lot of extra crypto not used by CIFS.
		 */
		Config.setProperty("jcifs.smb.lmCompatibility", "0");
		Config.setProperty("jcifs.smb.client.useExtendedSecurity", "false");
		Config.setProperty("jcifs.http.domainController", PropertyUtil
				.get(Web.SSO_AD_DOMAINCONTROLLER));
		Config.setProperty("jcifs.smb.client.domain", PropertyUtil
				.get(Web.SSO_AD_DEFAULTDOMAIN));
	}

}
