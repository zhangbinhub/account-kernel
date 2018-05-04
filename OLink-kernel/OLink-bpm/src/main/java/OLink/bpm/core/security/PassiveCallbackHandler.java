package OLink.bpm.core.security;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class PassiveCallbackHandler implements CallbackHandler {

	private String username;

	private String password;

	/**
	 * @param username
	 *            The user name.
	 * @param password
	 *            The password.
	 */
	public PassiveCallbackHandler(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.security.auth.callback.CallbackHandler#handle(javax.security.auth
	 * .callback.Callback[])
	 */
	public void handle(Callback[] callbacks) throws java.io.IOException,
			UnsupportedCallbackException {
		for (int i = 0; i < callbacks.length; i++) {
			if (callbacks[i] instanceof NameCallback) {
				NameCallback nameCb = (NameCallback) callbacks[i];

				nameCb.setName(username);
			} else if (callbacks[i] instanceof PasswordCallback) {
				PasswordCallback passCb = (PasswordCallback) callbacks[i];
				if (password != null) {
					passCb.setPassword(password.toCharArray());
				}
			} else {
				throw (new UnsupportedCallbackException(callbacks[i],
						"Callback class not supported"));
			}
		}
	}
}
