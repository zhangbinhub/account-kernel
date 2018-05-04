package OLink.bpm.base.web.tag;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.MultiLanguageProperty;

/**
 * The multiple language tag.In this tag all the string within tag "{*[" and
 * "]*}" with translate into different language according the name of tag and
 * client language setting.
 */

public class MultiLanguageTag extends BodyTagSupport {

	/**
	 * The serial version uuid.
	 */
	private static final long serialVersionUID = 4716431432593199143L;
	/**
	 * The content body.
	 */
	private BodyContentPrint content = null;

	private String value;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doEndTag()
	 */
	public int doEndTag() throws JspException {
		if (bodyContent != null) {
			String contentString = bodyContent.getString();

			content.write(contentString, 0, contentString.length()); // 语言转换
			try {
				pageContext.getOut().write(content.getPrintString()); // 输出内容
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return EVAL_PAGE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.jsp.tagext.BodyTagSupport#doStartTag()
	 */
	public int doStartTag() throws JspException {
		content = new BodyContentPrint(pageContext.getRequest(), value);
		return EVAL_BODY_AGAIN;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.jsp.tagext.BodyTagSupport#setBodyContent(javax.servlet.
	 * jsp.tagext.BodyContent)
	 */
	public void setBodyContent(BodyContent b) {
		this.bodyContent = b;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}

/**
 * The body content printer.
 */
class BodyContentPrint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5330634126677624750L;
	int flag;
	int count;
	int[] buf;
	String domain;

	// 多语言迁移到软件下，使用application进行过滤 by Dolly（2011-1-23）
	String application = null;

	StringBuffer buffer;
	String language;

	/**
	 * The default constructor
	 * 
	 * @param incomeRequest
	 */
	public BodyContentPrint(ServletRequest incomeRequest, String value) {
		// Get the session
		HttpServletRequest request = ((HttpServletRequest) incomeRequest);
		HttpSession session = request.getSession();

		// Set the user language.
		language = (String) session.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);

		if (StringUtil.isBlank(language)) {
			language = getUserLanguage(request);
			session.setAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE, language);
		}

		// Set the domain
		domain = getUserDomain(request);
		if (value != null) {
			// document的多语言执行
			if (value.equals("FRONTMULTILANGUAGETAG")) {
				application = getCurrentApplication(request);
			}
		}
		// Set the buffer.
		buffer = new StringBuffer();
		buf = new int[512];
	}

	/**
	 * Get the user domain
	 * 
	 * @param request
	 *            The http servlet request.
	 */
	private String getUserDomain(HttpServletRequest request) {

		WebUser webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		String userDomain = null;

		if (webUser != null) {
			userDomain = webUser.getDomainid();
		} else {
			userDomain = null;
		}

		return userDomain;
	}

	private String getCurrentApplication(HttpServletRequest request) {
		ValueObject vo = (ValueObject) request.getAttribute("content");
		String currentApplication = null;
		if (vo != null) {
			currentApplication = vo.getApplicationid();
		} else {
			currentApplication = null;
		}

		return currentApplication;
	}

	/**
	 * Get the user language
	 * 
	 * @param request
	 *            The Http servlet request
	 */
	private String getUserLanguage(HttpServletRequest request) {
		Locale loc = request.getLocale();
		String userLanguage = "EN";

		if (loc.equals(Locale.CHINA) || loc.equals(Locale.PRC))
			userLanguage = "CN";
		else if (loc.equals(Locale.TAIWAN))
			userLanguage = "TW";
		else {
			userLanguage = loc.getLanguage().toUpperCase();
			if (MultiLanguageProperty.getType(language) == 0) {
				userLanguage = "EN";
			}
		}

		return userLanguage;
	}

	/**
	 * Flush the buffer
	 */
	public void flush() {
		if (count > 0)
			clearBuf();
	}

	/**
	 * Clear the buffer.
	 */
	public void close() {
		if (count > 0)
			clearBuf();
	}

	/**
	 * Write the buffer
	 * 
	 * @param buf2
	 *            The buffer to write
	 * @param off
	 *            The offset
	 * @param len
	 *            The length
	 */
	public void write(char buf2[], int off, int len) {
		for (int i = off; i < Math.min(off + len, buf2.length); i++) {
			this.write((int) buf2[i]);
		}
	}

	/**
	 * Write the buffer
	 * 
	 * @param s
	 *            The string
	 * @param off
	 *            The offset
	 * @param len
	 *            The length
	 */
	public void write(String s, int off, int len) {
		buffer.append(s);
		write(s.toCharArray(), off, len);
	}

	/**
	 * Write to buffer
	 * 
	 * @param b
	 *            The indexs
	 */
	private void writeToBuf(int b) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			int newbuf[] = new int[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		}
		buf[count] = b;
		count = newcount;
	}

	/**
	 * Write the buffer
	 * 
	 * @param c
	 *            the index
	 */
	public void write(int c) {
		switch (c) {
		case '{':
			if (flag == 0) {
				flag = 1;
				writeToBuf(c);
			} else {
				clearBuf();
			}

			break;
		case '*':
			if (flag == 1) {
				flag = 2;
				writeToBuf(c);
			} else if (flag == 4) {
				flag = 5;
				writeToBuf(c);
			} else {
				clearBuf();
			}

			break;
		case '[':
			if (flag == 2) {
				flag = 3;
				writeToBuf(c);
			} else {
				clearBuf();
			}

			break;
		case ']':
			if (flag == 3) {
				flag = 4;
				writeToBuf(c);
			} else {
				clearBuf();
			}

			break;
		case '}':
			if (flag == 5) {
				writeToBuf(c);
				// start to replace
				String origText = StringUtil.toString(buf, 3, count - 6);
				String newText = MultiLanguageProperty.replaceText(application, language, origText);
				// try {
				// newText = StringUtil.toUTFBody(newText);
				// } catch (IOException e) {
				// }

				origText = "{*[" + origText + "]*}";
				int start = buffer.indexOf(origText);
				buffer.replace(start, start + origText.length(), newText);
				count = 0;
				flag = 0;
			} else {
				clearBuf();
			}

			break;

		default:
			if (flag == 3) {
				writeToBuf(c);
			} else {
				if (count > 0)
					clearBuf();
			}

			break;
		}
	}

	/**
	 * Clear the buffer
	 */
	private void clearBuf() {
		count = 0;
		flag = 0;
	}

	/**
	 * Get the print string.
	 * 
	 * @return The print string.
	 */
	public String getPrintString() {
		return buffer.toString();
	}

	/**
	 * Get the size.
	 * 
	 * @return the size
	 */
	public int getSize() {
		return buffer.length();
	}

}
