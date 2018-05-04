package OLink.bpm.core.pushlet;

import org.hibernate.event.PostInsertEvent;
import org.hibernate.event.PostInsertEventListener;

public class HibernatePublishListener implements PostInsertEventListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void onPostInsert(PostInsertEvent event) {
		Object entity = event.getEntity();
		if (entity instanceof PublishAble) {
			((PublishAble) entity).publish();
		}
	}

}
