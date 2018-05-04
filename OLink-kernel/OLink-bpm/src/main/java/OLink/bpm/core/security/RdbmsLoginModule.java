package OLink.bpm.core.security;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;

/**
 * @author jameshe
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
/**
 * @author jameshe
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class RdbmsLoginModule implements LoginModule {

	private Subject subject;

	private CallbackHandler callbackHandler;

	private Map<String, ?> sharedState;

	private Map<String, ?> options;

	@SuppressWarnings("unused")
	private boolean _debug;

	/**
	 * (non-Javadoc)
	 * @SuppressWarnings 不使用泛型
	 * @see
	 * LoginModule#initialize(Subject
	 * , CallbackHandler, Map,
	 * Map)
	 */
	@SuppressWarnings("unchecked")
	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {

		// this.subject = subject;

		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

		_debug = "true".equalsIgnoreCase((String) options.get("debug"));
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public Map<String, ?> getSharedState() {
		return sharedState;
	}

	public void setSharedState(Map<String, ?> sharedState) {
		this.sharedState = sharedState;
	}

	public Map<String, ?> getOptions() {
		return options;
	}

	public void setOptions(Map<String, ?> options) {
		this.options = options;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#login()
	 */
	public boolean login() throws LoginException {
		if (callbackHandler == null)
			throw new LoginException("no handler");

		NameCallback nameCb = new NameCallback("user: ");
		PasswordCallback passCb = new PasswordCallback("password: ", true);
		Callback[] callbacks = new Callback[] { nameCb, passCb };
		try {
			callbackHandler.handle(callbacks);
		} catch (Exception e) {
			e.printStackTrace();
			throw new LoginException(e.getMessage());
		}

		String username = nameCb.getName();
		String password = null;
		if (passCb.getPassword() != null) {
			password = String.valueOf(passCb.getPassword());
		}

		return rdbmsValidate(username, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#commit()
	 */
	public boolean commit() throws LoginException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#abort()
	 */
	public boolean abort() throws LoginException {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.security.auth.spi.LoginModule#logout()
	 */
	public boolean logout() throws LoginException {
		return false;
	}

	/**
	 * Rdbms validate from the system user from database.
	 * 
	 * @param username
	 *            The user name
	 * @param password
	 *            The user password.
	 * @return
	 */
	private boolean rdbmsValidate(String username, String password) {
		try {
			UserProcess process = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			UserVO user = process.login(username, password);
			if (user != null)
				return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static void main(String[] args) {

		// Obtain a LoginContext, needed for authentication. Tell it
		// to use the LoginModule implementation specified by the
		// entry named "Sample" in the JAAS login configuration
		// file and to also use the specified CallbackHandler.
		LoginContext lc = null;
		try {
			lc = new LoginContext("Sample", new PassiveCallbackHandler("hello",
					"world"));
		} catch (LoginException le) {
			System.err
					.println("Cannot create LoginContext. " + le.getMessage());
			System.exit(-1);
		} catch (SecurityException se) {
			se.printStackTrace();
			System.err
					.println("Cannot create LoginContext. " + se.getMessage());
			System.exit(-1);
		}

		// the user has 3 attempts to authenticate successfully
		int i;
		for (i = 0; i < 3; i++) {
			try {

				// attempt authentication
				lc.login();

				// if we return with no exception, authentication succeeded
				break;

			} catch (LoginException le) {

				System.err.println("Authentication failed:");
				System.err.println("  " + le.getMessage());
				try {
					// this.currentThread().sleep(3000);
				} catch (Exception e) {
					// ignore
				}

			}
		}

		// did they fail three times?
		if (i == 3) {
			System.exit(-1);
		}
	}
}
