package OLink.bpm.util.message;


public class MessageUtil {

	private String applicationid = null;

	private ShortMessageUtil smsManager = null;

	public MessageUtil() {
	}

	public MessageUtil(String applicationid) {
		this.applicationid = applicationid;
	}

	public ShortMessageUtil getSMSManager() {
		if (smsManager == null)
			smsManager = new ShortMessageUtil();
		return smsManager;
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}

}
