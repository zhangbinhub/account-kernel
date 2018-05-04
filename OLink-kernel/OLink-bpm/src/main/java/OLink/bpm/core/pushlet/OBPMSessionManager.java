package OLink.bpm.core.pushlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;

import nl.justobjects.pushlet.core.Event;
import nl.justobjects.pushlet.core.Session;
import nl.justobjects.pushlet.core.SessionManager;
import nl.justobjects.pushlet.util.PushletException;

public class OBPMSessionManager extends SessionManager {

	public Session createSession(Event anEvent, HttpServletRequest request, HttpServletResponse response) throws PushletException {
		HttpSession httpSession = request.getSession();
		// 为OBPM订阅者设置用户ID
		WebUser webUser = (WebUser) httpSession.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);

		String sessionId = createSessionId();
		if (webUser != null) {
			// 设置登录用户ID为订阅会话ID
			sessionId = webUser.getId();
		}

		// Trivial
		return Session.create(sessionId);
	}
}
