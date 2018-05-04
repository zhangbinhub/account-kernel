/*
 * Copyright (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * Created on Jun 2, 2005 5:41:11 PM
 * The JForum Project
 * http://www.jforum.net
 */
package OLink.bpm.core.sso.autherticator;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.security.action.LoginHelper;
import OLink.bpm.core.sso.AuthenticationException;
import OLink.bpm.core.sso.LoginAuthenticator;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.core.sysconfig.ejb.LdapConfig;
import OLink.bpm.util.StringUtil;

/**
 * Authenticate users against a LDAP server.
 * 
 * @author Rafael Steil
 * @version $Id: LDAPAuthenticator.java,v 1.8 2006/08/20 22:47:43 rafaelsteil
 *          Exp $
 */
public class LDAPLoginAuthenticator extends AbstractLoginAuthenticator
		implements LoginAuthenticator {

	static {
		PropertyUtil.load("sso");
	}

	public LDAPLoginAuthenticator() {
	}

	public LDAPLoginAuthenticator(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	public LdapContext getLdapContext(String userDn, String password)
			throws NamingException {
		Control[] ctl = null;
		Hashtable<String, String> env = new Hashtable<String, String>();

		String url = PropertyUtil.get(LdapConfig.LDAP_URL);
		String baseDN = PropertyUtil.get(LdapConfig.LDAP_BASEDN);
		String pooled = PropertyUtil.get(LdapConfig.LDAP_POOLED);
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		// url,ldap的地址
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, userDn + "," + baseDN);
		env.put(Context.SECURITY_CREDENTIALS, password);
		// ldap连接池
		if ("true".equals(pooled) || "false".equals(pooled))
			env.put("com.sun.jndi.ldap.connect.pool", pooled);
		return new InitialLdapContext(env, ctl);
	}

	/**
	 * @throws NamingException
	 * @see net.jforum.sso.LoginAuthenticator#validateLogin(String,
	 *      String, java.util.Map)
	 */
	public UserVO validateLogin(String domain, String loginno, String password)
			throws AuthenticationException {
		String userDirStructure = PropertyUtil.get(LdapConfig.DIRSTRUCTURE);
		String userDn = getUserDn(userDirStructure, loginno, domain);
		LdapContext ctx = null;
		try {
			ctx = getLdapContext(userDn, password);
			Attributes attributes = ctx.getAttributes(userDn);
			UserVO user = converToUser(attributes);
			if (user != null && user.isActive()) {
				user = synchUser(user, domain);
				LoginHelper.initWebUser(request, user, defaultApplication,
						domain);
				saveInfo(user, domain);
				return user;
			}
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage());
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 同步域
	 * 
	 * @param domainName
	 * @return
	 * @throws Exception
	 */
	private DomainVO synDomain(String domainName) throws Exception {
		DomainProcess domainProcess = (DomainProcess) ProcessFactory
				.createProcess(DomainProcess.class);
		DomainVO domain = domainProcess.getDomainByName(domainName);
		if (domain == null) {
			domain = new DomainVO();
			domain.setName(domainName);
			domain.setSkinType("default");
			domain.setStatus(1);
			domainProcess.doCreate(domain);
		}
		return domain;
	}

	/**
	 * 同步用户
	 * 
	 * @param user
	 * @param domainName
	 * @return
	 */
	private UserVO synchUser(UserVO user, String domainName) {
		try {
			// 登录数据库,查询是否存在此账号的用户
			UserVO userInDB = userProcess.login(user.getLoginno());
			DomainVO domain = synDomain(domainName);
			/*
			 * DomainProcess domainProcess = (DomainProcess) ProcessFactory
			 * .createProcess(DomainProcess.class); DomainVO domain =
			 * domainProcess.getDomainByName(domainName);
			 */
			if (userInDB == null) {// 用户不存在,同步该用户
				if (domain != null && !StringUtil.isBlank(domain.getId()))
					user.setDomainid(domain.getId());
				userProcess.doCreate(user);
				return user;
			} else if (StringUtil.isBlank(userInDB.getDomainid())) {
				if (domain != null && !StringUtil.isBlank(domain.getId()))
					userInDB.setDomainid(domain.getId());
				userProcess.doUpdate(userInDB);
			}
			return userInDB;
		} catch (Exception e) {
			e.printStackTrace();
			return user;
		}
	}

	private UserVO converToUser(Attributes attributes) throws NamingException {
		if (attributes != null && attributes.size() > 0) {
			UserVO user = new UserVO();
			String id_ = PropertyUtil.get(LdapConfig.ID);
			String name_ = PropertyUtil.get(LdapConfig.NAME);
			String loginno_ = PropertyUtil.get(LdapConfig.LOGINNO);
			String loginpwd_ = PropertyUtil.get(LdapConfig.LOGINPWD);
			String email_ = PropertyUtil.get(LdapConfig.EMAIL);
			String telephone_ = PropertyUtil.get(LdapConfig.TELEPHONE);
			String id = attributes.get(id_) != null ? (String) attributes.get(
					id_).get() : null;
			user.setId(id);
			String name = attributes.get(name_) != null ? (String) attributes
					.get(name_).get() : null;
			user.setName(name);
			String loginno = attributes.get(loginno_) != null ? (String) attributes
					.get(loginno_).get()
					: null;
			user.setLoginno(loginno);
			Object loginpwd = attributes.get(loginpwd_) != null ? attributes
					.get(loginpwd_).get() : null;
			if (loginpwd instanceof String)
				user.setLoginpwd((String) loginpwd);
			else if (loginpwd instanceof byte[])
				user.setLoginpwd(new String((byte[]) loginpwd));
			String email = (String) attributes.get(email_).get();
			user.setEmail(email);
			String telephone = (String) attributes.get(telephone_).get();
			user.setTelephone(telephone);
			return user;
		}
		return null;
	}

	private String getUserDn(String dirStructure, String loginno, String domain) {
		StringBuffer dn = new StringBuffer();
		if (dirStructure == null || "".equals(dirStructure))
			return "";
		if (loginno == null)
			return "";
		String[] nodes = dirStructure.split(",");
		int n = 0;
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].contains("?")) {
				if (n < 1) {
					nodes[i] = nodes[i].replaceAll("[?]", loginno);
				} else {
					nodes[i] = nodes[i].replaceAll("[?]", domain);
				}
				n++;
			}
			if (dn.length() > 0)
				dn.append(",");
			dn.append(nodes[i]);
		}
		return dn.toString();
	}

	public UserVO validateLogin(String loginno, String password) {
		String userDirStructure = PropertyUtil.get(LdapConfig.DIRSTRUCTURE);
		String userDn = getUserDn(userDirStructure, loginno, null);
		LdapContext ctx = null;
		try {
			ctx = getLdapContext(userDn, password);
			Attributes attributes = ctx.getAttributes(userDn);
			return converToUser(attributes);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) throws AuthenticationException {
		LDAPLoginAuthenticator la = new LDAPLoginAuthenticator();
		UserVO user = la.validateLogin("teemlink", "keezzm", "435465");
		System.out.println(user.getName());
	}

}
