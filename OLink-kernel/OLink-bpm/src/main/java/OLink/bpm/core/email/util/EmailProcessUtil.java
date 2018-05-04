package OLink.bpm.core.email.util;

import java.lang.reflect.Constructor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import OLink.bpm.core.email.runtime.mail.ConnectionMetaHandler;
import OLink.bpm.core.email.runtime.mail.ConnectionProfile;
import OLink.bpm.core.email.runtime.mail.ProtocolFactory;
import OLink.bpm.constans.Web;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.email.runtime.mail.AuthProfile;

/**
 * 
 * @author Tom
 *
 */
public class EmailProcessUtil {
	
	//private static final Logger log = Logger.getLogger(EmailProcessUtil.class);
	
	/**
	 * 
	 * @param iProcessClass
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static IDesignTimeProcess<?> createProcess(Class<?> iProcessClass, HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		WebUser webUser = (WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		return createProcess(iProcessClass, webUser);
	}
	
	/**
	 * 
	 * @param iProcessClass
	 * @param user
	 * @param handler
	 * @return
	 * @throws Exception
	 */
	public static IDesignTimeProcess<?> createProcess(Class<?> iProcessClass, WebUser webUser) throws Exception {
		if (!EmailConfig.isUserEmail()) {
			return null;
		}
		if (EmailConfig.isInternalEmail()) {
			return ProcessFactory.createProcess(iProcessClass);
		} else {
			String cn = iProcessClass.getName() + "ImapBean";
			Class<?> imapClass = Class.forName(cn);
			try {
				Constructor<?> constructor = imapClass.getConstructor(ProtocolFactory.class);
				if (constructor != null) {
					ConnectionProfile profile = EmailConfig.getConnectionProfile();
					EmailUser user = webUser.getEmailUser();
					ConnectionMetaHandler handler = webUser.getConnectionMetaHandler();
					AuthProfile auth = new AuthProfile();
					auth.setUserName(user.getAccount());
					auth.setPassword(user.getPassword());
					ProtocolFactory factory = new ProtocolFactory(profile, auth, handler);
					return (IDesignTimeProcess<?>) constructor.newInstance(factory);
				}
			} catch (Exception e) {
				//log.info(e.getMessage());
			}
			return (IDesignTimeProcess<?>) imapClass.newInstance();
		}
	}
	
}
