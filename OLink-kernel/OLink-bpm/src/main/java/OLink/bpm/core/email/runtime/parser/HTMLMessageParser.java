package OLink.bpm.core.email.runtime.parser;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import OLink.bpm.core.email.runtime.model.Email;
import OLink.bpm.core.email.runtime.model.EmailPart;
import OLink.bpm.core.email.util.Utility;

/**
 * @author Tom
 * 
 */
public class HTMLMessageParser {
	
	private static Locale loc = Locale.CHINESE;

	/**
	 * Default Constructor
	 */
	public HTMLMessageParser() {
		super();
	}

	public static String prepareInlineHTMLContent(Email msg, String str) {
		if ((str == null) || (str.equals("")))
			return "";
		int i = -1;
		// cid with double quotes
		String tmp = null;
		String contentId = null;
		int j = -1;
		int partId = -1;
		while ((i = str.indexOf("\"cid:")) != -1) {
			tmp = str.substring(i + 5);
			j = tmp.indexOf("\"");
			contentId = tmp.substring(0, j);
			partId = getPartIdByContentId(msg, contentId);

			str = str.substring(0, i) + "\"dumpPart.service?partid=" + partId
					+ "\"" + tmp.substring(j + 1);
		}

		return str;
	}

	public static String organizeLinks(String str) {
		// clear targets first to eliminate duplicate targets in hrefs
		String EXPR = null;
		StringBuffer buffer = null;
		Matcher matcher = null;
		int count = 0;
		String tag = null;
		try {
			EXPR = "target([ =\"\']*[\t\n\r:#0-9a-z\\./@~?&=;%_-]+[ \"']*)";
			buffer = new StringBuffer();
			matcher = Pattern.compile(EXPR, Pattern.CASE_INSENSITIVE).matcher(
					str);
			count = 0;
			while (matcher.find()) {
				count++;
				tag = matcher.group();
				tag = findTagValue(tag);
				matcher.appendReplacement(buffer, "");
			}
			matcher.appendTail(buffer);
			str = buffer.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		// now parse the mailto and href links.
		try {
			EXPR = " href([ =\"\']*[\t\n\r:#0-9a-z\\./@~?&=;%_-]+[ \"']*)";
			buffer = new StringBuffer();
			matcher = Pattern.compile(EXPR, Pattern.CASE_INSENSITIVE).matcher(
					str);
			count = 0;
			while (matcher.find()) {
				count++;
				tag = matcher.group();
				tag = findTagValue(tag);
				// eliminate the mailto hrefs
				if (tag != null && tag.toLowerCase(loc).startsWith("mailto:")) {
					tag = tag.substring(7);
				}
				// replace the values of mail links with the compose form values
				if (tag != null && tag.indexOf("@") > 0) {
					tag = "javascript:parent.fastEmail('" + tag + "')";
					matcher.appendReplacement(buffer, " href=\"" + tag + "\"");
				} else {
					matcher.appendReplacement(buffer, " href=\"" + tag
							+ "\" target=\"_blank\" ");
				}
			}
			matcher.appendTail(buffer);
			str = buffer.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return str;
	}

	private static String findTagValue(String tag) {
		int pos = tag.indexOf("=");
		tag = Utility.extendedTrim(tag.substring(pos + 1), "\"");
		tag = Utility.extendedTrim(tag, "'");
		return tag;
	}

	private static int getPartIdByContentId(Email msg, String cid) {
		List<EmailPart> parts = msg.getParts();
		for (int i = 0; i < parts.size(); i++) {
			EmailPart part = parts.get(i);
			String contentId = part.getContentId();
			String fileName = part.getFileName();
			if ((contentId != null && contentId.equals(cid))
					|| (fileName != null && fileName.equalsIgnoreCase(cid))) {
				return i;
			}
		}
		// still havent found it. maybe cid is surrounded with < >
		for (int i = 0; i < parts.size(); i++) {
			EmailPart part = parts.get(i);
			String contentId = part.getContentId();
			contentId = Utility.extendedTrim(contentId, "<");
			contentId = Utility.extendedTrim(contentId, ">");
			if (contentId != null && contentId.equals(cid)) {
				return i;
			}
		}
		return -1;
	}
	
	public static String toContent(String content) {
		if (content == null) {
			return "";
		}
		content = content.replaceAll("\r\n", "<br>");
		content = content.replaceAll("\n", "<br>");
		return organizeLinks(content);
	}
	
}
