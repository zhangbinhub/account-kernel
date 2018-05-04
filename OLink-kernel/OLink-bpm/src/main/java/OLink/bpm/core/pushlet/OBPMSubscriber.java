package OLink.bpm.core.pushlet;

import nl.justobjects.pushlet.core.Event;
import nl.justobjects.pushlet.core.Subscriber;
import nl.justobjects.pushlet.core.Subscription;

public class OBPMSubscriber extends Subscriber {
	protected String userid;

	public Subscription match(Event event) {
		Subscription subscription = super.match(event);
		// 是否有拥有者
		String user = event.getField("p_user");
		if (user != null) {
			if (subscription != null) {

				// 订阅者用户ID在拥有者列表中
				if (user.equals(userid)) {
					return subscription;
				}
			}
		} else {
			return subscription;
		}

		return null;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
}
