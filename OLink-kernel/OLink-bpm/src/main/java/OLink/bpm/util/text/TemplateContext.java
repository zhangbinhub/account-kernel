package OLink.bpm.util.text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TemplateContext {
	public static final String TAG_LEFT = "${";

	public static final String TAG_RIGHT = "}";

	private Collection<AbstractPart> textlist = new ArrayList<AbstractPart>();

	private Collection<String> placeholderList = new ArrayList<String>();

	private Map<String, String> params = new HashMap<String, String>();

	private TemplateContext() {

	}

	public void putParams(String key, String value) {
		params.put(key, value);
	}

	public String getParams(String key) {
		return params.get(key);
	}

	public Collection<String> getPlaceholders() {
		return placeholderList;
	}

	public static TemplateContext parse(String text) {
		TemplateContext tmpl = new TemplateContext();

		if (text != null) {
			int pos1 = 0;
			int pos2 = 0;
			while (pos1 >= 0) {
				pos1 = text.indexOf(TAG_LEFT);
				pos2 = text.indexOf(TAG_RIGHT, pos1);
				if (pos2 > pos1) {
					String left = text.substring(0, pos1);
					String paramText = text.substring(pos1 + TAG_LEFT.length(), pos2 - TAG_RIGHT.length() + 1);

					tmpl.appendPart(new TextPart(left));
					tmpl.appendPart(new ParameterPart(paramText));
					tmpl.getPlaceholders().add(paramText);

					text = text.substring(pos2 + TAG_RIGHT.length());
				}
			}

			tmpl.appendPart(new TextPart(text));
		}

		return tmpl;
	}

	public void appendPart(AbstractPart part) {
		if (part != null)
			textlist.add(part);
	}

	public String toText() {
		StringBuffer buffer = new StringBuffer();
		for (Iterator<AbstractPart> iter = textlist.iterator(); iter.hasNext();) {
			AbstractPart part = iter.next();
			buffer.append(part.toText(this));
		}

		return buffer.toString();
	}

}
