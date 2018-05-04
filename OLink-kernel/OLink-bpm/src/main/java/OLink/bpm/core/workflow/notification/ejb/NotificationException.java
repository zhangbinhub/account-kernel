package OLink.bpm.core.workflow.notification.ejb;

public class NotificationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3524581495699002127L;

	public NotificationException() {

	}

	public NotificationException(String message) {
		super(message);
	}

	public NotificationException(Throwable cause) {
		super(cause);
	}

	public NotificationException(String message, Throwable cause) {
		super(message, cause);
	}
}
