package OLink.bpm.util;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * Session上下文-单例 
 * <p> 记录与注销用户登录的每个Session</p>
 * @author Tom
 */
public class OBPMSessionContext {

	private static OBPMSessionContext instance;
	private Map<String, HttpSession> sessionMap;

	private OBPMSessionContext() {
		sessionMap = new HashMap<String, HttpSession>();
	}

	/**
	 * 获取Session上下文实例
	 * @return Session上下文实例
	 */
	public static OBPMSessionContext getInstance() {
		synchronized (OBPMSessionContext.class) {
			if (instance == null) {
				instance = new OBPMSessionContext();
			}
		}
		return instance;
	}

	/**
	 * 往Session上下文添加Session
	 * @param session HttpSession
	 */
	public synchronized void addSession(HttpSession session) {
		if (session != null) {
			sessionMap.put(session.getId(), session);
		}
	}

	/**
	 * 在Session上下文移除Session
	 * @param session HttpSession
	 */
	public synchronized void removeSession(HttpSession session) {
		if (session != null) {
			sessionMap.remove(session.getId());
		}
	}

	public synchronized HttpSession getSession(String sessionid) {
		if (StringUtil.isBlank(sessionid)) {
			return null;
		}
		return sessionMap.get(sessionid);
	}

}
