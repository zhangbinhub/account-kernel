package OLink.bpm.core.macro.runner;

import java.util.HashMap;
import java.util.Map;

public class JsMessage {
	public final static int TYPE_ALERT = 0x10;

	public final static int TYPE_CONFIRM = 0x20;

	private final static Map<Integer, String> TYPE_NAME_MAP = new HashMap<Integer, String>();

	static {
		TYPE_NAME_MAP.put(Integer.valueOf(TYPE_ALERT), "Alert");
		TYPE_NAME_MAP.put(Integer.valueOf(TYPE_CONFIRM), "Confirm");
	}

	public JsMessage(int type, String content) {
		this.type = type;
		this.content = content;
	}

	private int type = Integer.MAX_VALUE;

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getTypeName() {
		return TYPE_NAME_MAP.get(Integer.valueOf(type));
	}
}
