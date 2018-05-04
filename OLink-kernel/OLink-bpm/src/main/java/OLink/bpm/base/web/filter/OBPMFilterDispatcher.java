package OLink.bpm.base.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import OLink.bpm.util.OBPMDispatcher;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.dispatcher.FilterDispatcher;

/**
 * 此类继承webwork filter，对部分URL不进行封装
 * 
 * @author Nicholas
 * 
 */
public class OBPMFilterDispatcher extends FilterDispatcher {
	private static final Logger LOG = Logger.getLogger(OBPMFilterDispatcher.class);

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		String url = "";
		if (req instanceof HttpServletRequest) {
			HttpServletRequest hreq = (HttpServletRequest) req;
			url = hreq.getRequestURI();
			if (url.indexOf("/form/webeditor/editor/filemanager/connectors/") != -1) {
				chain.doFilter(req, res);
				return;
			} else if (url.indexOf("/portal/dispatch/") != -1) {
				new OBPMDispatcher().forward(url, hreq, res);
				LOG.debug("URL: " + url);
				return;
			}
		}

		try {
			super.doFilter(req, res, chain);
		} catch (IOException ioe) {
			LOG.error("URL: " + url);
			throw ioe;
		} catch (ServletException se) {
			LOG.error("URL: " + url);
			throw se;
		}
	
	}
}
