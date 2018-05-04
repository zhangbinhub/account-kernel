package OLink.bpm.core.shortmessage.submission.action;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.core.shortmessage.submission.ejb.MessageType;

public class SubmitMessageHelper {

	public Map<String, String> getContentTypes() {
		String[] names = MessageType.NAMES;
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < names.length; i++) {
			map.put(""+MessageType.getType(names[i]), names[i]);
		}
		return map;
	}
}
