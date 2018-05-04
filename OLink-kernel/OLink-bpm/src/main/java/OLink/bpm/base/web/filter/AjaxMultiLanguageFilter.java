package OLink.bpm.base.web.filter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.MultiLanguageProperty;

/**
 * @author nicholas
 */
public final class AjaxMultiLanguageFilter extends HttpServlet implements Filter {
	
	private static final long serialVersionUID = 4540909316344106992L;

	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest hreq = (HttpServletRequest) rq;

		String x_requested_with = hreq.getHeader("x-requested-with");

		if (x_requested_with==null) {
			chain.doFilter(rq, rs);
			return;
		}

		HttpServletResponse response = (HttpServletResponse) rs;

		ReplaceTextWrapper myResponse = new ReplaceTextWrapper(hreq, response);

		chain.doFilter(rq, myResponse);
		myResponse.setContentLength(myResponse.getSize());
		myResponse.flushBuffer();
	}

	public void init(FilterConfig arg0) throws ServletException {
		
	}

}

/**
 * @author nicholas
 */
class ReplaceTextWrapper extends HttpServletResponseWrapper {
	private ServletResponse inResp;

	private ServletRequest inRequ;

	private ReplaceTextStream outStream;

	private MyPrintWriter outWriter;

	public ReplaceTextWrapper(ServletRequest inRequ, ServletResponse inResp) throws IOException {
		super((HttpServletResponse) inResp);
		this.inResp = inResp;
		this.inRequ = inRequ;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		outStream = new ReplaceTextStream(inRequ, inResp);
		return outStream;
	}

	public PrintWriter getWriter() throws IOException {
		outWriter = new MyPrintWriter(inRequ, inResp);
		return outWriter;
	}

	public int getSize() {
		int count = 0;
		if (outStream != null) {
			count += outStream.getSize();
		}

		if (outWriter != null) {
			count += outWriter.getSize();
		}

		return count;
	}
}

/**
 * @author nicholas
 */
class ReplaceTextStream extends ServletOutputStream {
	int flag;

	int count;

	int[] buf;

	/**
	 * @uml.property name="size"
	 */
	int size;

	OutputStream outStream;

	public ReplaceTextStream(ServletRequest inRequ, ServletResponse inResp) throws IOException {
		this.outStream = inResp.getOutputStream();
		buf = new int[512];
	}

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

	private void clearBuf() throws IOException {
		for (int i = 0; i < count; i++) {
			outStream.write(buf[i]);
			size++;
		}
		count = 0;
		flag = 0;
	}

	public void write(int i) throws IOException {
		switch (i) {
		case '{':
			if (flag == 0) {
				flag = 1;
				writeToBuf(i);
			} else {
				clearBuf();
				outStream.write(i);
				size++;
			}

			break;
		case '*':
			if (flag == 1) {
				flag = 2;
				writeToBuf(i);
			} else if (flag == 4) {
				flag = 5;
				writeToBuf(i);
			} else {
				clearBuf();
				outStream.write(i);
				size++;
			}

			break;
		case '[':
			if (flag == 2) {
				flag = 3;
				writeToBuf(i);
			} else {
				clearBuf();
				outStream.write(i);
				size++;
			}

			break;
		case ']':
			if (flag == 3) {
				flag = 4;
				writeToBuf(i);
			} else {
				clearBuf();
				outStream.write(i);
				size++;
			}

			break;
		case '}':
			if (flag == 5) {
				writeToBuf(i);
				// start to replace
				String origText = StringUtil.toString(buf, 3, count - 6);
				String newText;
				try {
					newText = MultiLanguageProperty.getProperty(origText, origText);
					outStream.write(newText.getBytes());
					size += newText.getBytes().length;
				} catch (Exception e) {
					e.printStackTrace();
				}
				count = 0;
				flag = 0;
			} else {
				clearBuf();
				outStream.write(i);
				size++;
			}

			break;

		default:
			if (flag == 3) {
				writeToBuf(i);
			} else {
				if (count > 0)
					clearBuf();
				outStream.write(i);
				size++;
			}

			break;

		}
	}

	public void close() throws IOException {
		if (count > 0)
			clearBuf();
		outStream.close();

	}

	public void flush() throws IOException {
		if (count > 0)
			clearBuf();
		outStream.flush();
	}

	/**
	 * @return the size
	 * @uml.property name="size"
	 */
	public int getSize() {
		return size;
	}

	@Override
	public boolean isReady() {
		return false;
	}

	@Override
	public void setWriteListener(WriteListener arg0) {
	}

}

/**
 * @author nicholas
 */
class MyPrintWriter extends PrintWriter {
	int flag;

	int count;

	int[] buf;

	String domain;

	String language;

	String tStr=""; //Add By XGY 20130502

	/**
	 * @uml.property name="size"
	 */
	int size;

	public MyPrintWriter(ServletRequest inRequ, ServletResponse inResp) throws IOException {

		this(inResp.getWriter(), false);
		this.out = inResp.getWriter();

		HttpServletRequest request = ((HttpServletRequest) inRequ);
		HttpSession session = request.getSession();
		WebUser webUser = (WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		WebUser admin = (WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_USER);

		language = (String) session.getAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE);
		if (StringUtil.isBlank(language)) {
			Locale loc = request.getLocale();
			if (loc.equals(Locale.CHINA) || loc.equals(Locale.PRC))
				language = "CN";
			else if (loc.equals(Locale.TAIWAN))
				language = "TW";
			else {
				language = loc.getLanguage().toUpperCase();
				if (MultiLanguageProperty.getType(language) == 0) {
					language = "EN";
				}
			}
		}

		session.setAttribute(Web.SESSION_ATTRIBUTE_USERLANGUAGE, language);

		if (admin != null) {
			domain = request.getParameter("domain");
		} else if (webUser != null) {
			domain = webUser.getDomainid();
		} else {
			domain = null;
		}
	}

	public MyPrintWriter(Writer out, boolean autoFlush) {
		super(out);
		buf = new int[512];
	}

	public void flush() {//modify By XGY 20130502
		super.flush();
	}

	public void close() {//modify By XGY 20130502
		super.close();
	}

	//modify By XGY 20130502
	public void write(char buf2[], int off, int len) {
		for (int i = off; i < Math.min(off + len, buf2.length); i++) {
			this.write((int) buf2[i]);
		}
		try {
			super.write(tStr);
			size += tStr.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public void write(String s) {
		write(s.toCharArray(), 0, s.length());
	}

	//modify By XGY 20130502
	private void writeToBuf(int b,boolean wFlag) {
		if(!wFlag){
			for (int i = 0; i < count; i++) {
				tStr+=(char)buf[i];
			}
			count = 0;
			flag = 0;
			tStr+=(char)b;
			return ;
		}
		int newcount = count + 1;
		if (newcount > buf.length) {
			int newbuf[] = new int[Math.max(buf.length << 1, newcount)];
			System.arraycopy(buf, 0, newbuf, 0, count);
			buf = newbuf;
		}
		buf[count] = b;
		count = newcount;
	}

	//modify By XGY 20130502
	public void write(int c) {
		switch (c) {
		case '{':
			if (flag == 0) {
				flag = 1;
				writeToBuf(c,true);
			} else {
				writeToBuf(c,false);
			}

			break;
		case '*':
			if (flag == 1) {
				flag = 2;
				writeToBuf(c,true);
			} else if (flag == 4) {
				flag = 5;
				writeToBuf(c,true);
			} else {
				writeToBuf(c,false);
			}

			break;
		case '[':
			if (flag == 2) {
				flag = 3;
				writeToBuf(c,true);
			} else {
				writeToBuf(c,false);
			}

			break;
		case ']':
			if (flag == 3) {
				flag = 4;
				writeToBuf(c,true);
			} else {
				writeToBuf(c,false);
			}

			break;
		case '}':
			if (flag == 5) {
				writeToBuf(c,true);
				// start to replace
				String origText = StringUtil.toString(buf, 3, count - 6);
				String newText = MultiLanguageProperty.replaceText(domain, language, origText);
				tStr+=newText;
				count = 0;
				flag = 0;
			} else {
				writeToBuf(c,false);
			}

			break;

		default:
			if (flag == 3) {
				writeToBuf(c,true);
			} else {
				writeToBuf(c,false);
			}

			break;
		}
	}


	/**
	 * @return the size
	 * @uml.property name="size"
	 */
	public int getSize() {
		return size;
	}

}
