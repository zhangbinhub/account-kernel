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
 * Created on Jan 3, 2005 1:20:24 PM
 * The JForum Project
 * http://www.jforum.net
 */
package OLink.bpm.core.sso.autherticator;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.core.security.action.LoginHelper;
import OLink.bpm.core.sso.AuthenticationException;
import OLink.bpm.core.sso.LoginAuthenticator;
import OLink.bpm.core.user.ejb.UserVO;

/**
 * Default login authenticator for JForum. This authenticator will validate the
 * input against <i>jforum_users</i>.
 * 
 * @author Rafael Steil
 * @version $Id: DefaultLoginAuthenticator.java,v 1.10 2007/07/28 14:17:10
 *          rafaelsteil Exp $
 */
public class DefaultLoginAuthenticator extends AbstractLoginAuthenticator implements LoginAuthenticator {

	public DefaultLoginAuthenticator(HttpServletRequest request, HttpServletResponse response) {
		super(request, response);
	}

	/**
	 * @throws AuthenticationException
	 * @see net.jforum.sso.LoginAuthenticator#validateLogin(String, String,
	 *      java.util.Map)
	 */
	public UserVO validateLogin(String domainName, String loginno, String password) throws AuthenticationException {
		try {
			UserVO userVO = userProcess.login(loginno, password, domainName);

			if (userVO != null && userVO.isActive()) {
				LoginHelper.initWebUser(request, userVO, defaultApplication, domainName);
				saveInfo(userVO, domainName);

				return userVO;
			}
		} catch (Exception e) {
			if(e instanceof InvocationTargetException){
				throw new AuthenticationException(((InvocationTargetException)e).getTargetException().getMessage());
			}else{
				throw new AuthenticationException(e.getMessage());
			}
		}

		return null;
	}
}
