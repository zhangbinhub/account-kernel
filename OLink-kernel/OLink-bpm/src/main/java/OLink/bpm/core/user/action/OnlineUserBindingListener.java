/*
 * Created on 2005-4-25
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.user.action;

/**
 * @author Administrator
 * 
 * Preferences - Java - Code Style - Code Templates
 */
import java.io.Serializable;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import eWAP.core.Tools;


public class OnlineUserBindingListener implements HttpSessionBindingListener, Serializable{
	
	private static final long serialVersionUID = 6544377366339056471L;

	WebUser _user;

	public OnlineUserBindingListener(WebUser user) {
		_user = user;
	}

	public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
		String onlineUserid = null;
		try {
			onlineUserid = Tools.getTimeSequence();
		} catch (Exception e) {
			onlineUserid = Tools.getUUID();
		}
		if (this._user != null) {
			this._user.setOnlineUserid(onlineUserid);
		}
		OnlineUsers.add(onlineUserid, _user);
	}

	public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
		if (this._user != null) {
			OnlineUsers.remove(this._user.getOnlineUserid());
		}
	}
}
