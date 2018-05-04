package OLink.bpm.core.pushlet;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;

import nl.justobjects.pushlet.core.Command;
import nl.justobjects.pushlet.core.Event;
import nl.justobjects.pushlet.core.Protocol;
import nl.justobjects.pushlet.core.Session;
import nl.justobjects.pushlet.core.SessionManager;
import nl.justobjects.pushlet.servlet.Pushlet;
import nl.justobjects.pushlet.util.Log;
import nl.justobjects.pushlet.util.PushletException;
import nl.justobjects.pushlet.util.Servlets;

public class OBPMPushlet extends Pushlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1849201766042742560L;

	/**
	 * Servlet POST request: extracts event data from body.
	 * @SuppressWarnings Servlet API不支持泛型
	 */
	//@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Event event = null;
		try {
			// Create Event by parsing XML from input stream.
			String eventType = Servlets.getParameter(request, P_EVENT);

			// Always must have an event type
			if (eventType == null) {
				Log.warn("Pushlet.doPost(): bad request, no event specified");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No eventType specified");
				return;
			}

			// Create Event and set attributes from parameters
			event = new Event(eventType);
			for (Enumeration<?> e = request.getParameterNames(); e.hasMoreElements();) {
				String nextAttribute = (String)e.nextElement();
				event.setField(nextAttribute, request.getParameter(nextAttribute));
			}
		} catch (Throwable t) {
			// Error creating event
			Log.warn("Pushlet:  Error creating event in doPost(): ", t);
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// Handle parsed request
		doRequest(event, request, response);
	}

	/**
	 * Generic request handler (GET+POST).
	 */
	protected void doRequest(Event anEvent, HttpServletRequest request, HttpServletResponse response) {
		// Must have valid event type.
		String eventType = anEvent.getEventType();
		try {
			// Get Session: either by creating (on Join eventType)
			// or by id (any other eventType, since client is supposed to have
			// joined).
			Session session = null;
			if (eventType.startsWith(Protocol.E_JOIN)) {
				session = createSession(anEvent, request, response);
			} else {
				// Must be a request for existing Session

				// Get id
				String id = anEvent.getField(P_ID);

				// We must have an id value
				if (id == null) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No id specified");
					Log.warn("Pushlet: bad request, no id specified event=" + eventType);
					return;
				}

				// We have an id: get the session object
				session = SessionManager.getInstance().getSession(id);

				// Check for invalid id
				if (session == null) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid or expired id: " + id);
					Log.warn("Pushlet:  bad request, no session found id=" + id + " event=" + eventType);
					return;
				}
			}

			// ASSERTION: we have a valid Session

			// Let Controller handle request further
			// including exceptions
			Command command = Command.create(session, anEvent, request, response);
			session.getController().doCommand(command);
		} catch (Throwable t) {
			// Hmm we should never ever get here
			Log.warn("Pushlet:  Exception in doRequest() event=" + eventType, t);
			t.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * 创建订阅会话
	 * 
	 * @param anEvent
	 * @param request
	 * @param response
	 * @return
	 * @throws PushletException
	 */
	protected Session createSession(Event anEvent, HttpServletRequest request, HttpServletResponse response)
			throws PushletException {
		Session session = null;
		// Join request: create new subscriber
		SessionManager manager = SessionManager.getInstance();

		session = manager.createSession(anEvent);

		String userAgent = request.getHeader("User-Agent");
		if (userAgent != null) {
			userAgent = userAgent.toLowerCase();
		} else {
			userAgent = "unknown";
		}
		session.setUserAgent(userAgent);

		// 为订阅者设置用户ID，多个订阅者可能共享同一个用户ID
		HttpSession httpSession = request.getSession();
		WebUser webUser = (WebUser) httpSession.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		if (webUser != null) {

			OBPMSubscriber subscriber = (OBPMSubscriber) session.getSubscriber();
			subscriber.setUserid(webUser.getId());
		}

		return session;
	}
}
