package OLink.bpm.base.web.ServletResult;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.util.OBPMDispatcher;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.portlet.context.PortletActionContext;
import com.opensymphony.webwork.portlet.result.PortletResult;
import com.opensymphony.xwork.ActionInvocation;

public class OBPMPortletResult extends PortletResult {

	private static final Log LOG = LogFactory.getLog(OBPMPortletResult.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2464833521213903747L;

	public void doExecute(String finalLocation, ActionInvocation actionInvocation) throws Exception {
		if (PortletActionContext.isRender()) {
			PortletRequest req = PortletActionContext.getRequest();
			PortletResponse res = PortletActionContext.getResponse();
			finalLocation = new OBPMDispatcher().getDispatchURL(finalLocation, req, res);
			executeRenderResult(finalLocation);
		} else if (PortletActionContext.isEvent()) {
			PortletRequest req = PortletActionContext.getRequest();
			PortletResponse res = PortletActionContext.getResponse();
			finalLocation = new OBPMDispatcher().getDispatchURL(finalLocation, req, res);
			executeActionResult(finalLocation, actionInvocation);
		} else {
			HttpServletRequest req = ServletActionContext.getRequest();
			HttpServletResponse res = ServletActionContext.getResponse();
			finalLocation = new OBPMDispatcher().getDispatchURL(finalLocation, req, res);
			executeRegularServletResult(finalLocation, actionInvocation);
		}
	}

	private void executeRegularServletResult(String finalLocation, ActionInvocation actionInvocation)
			throws ServletException, IOException {
		ServletContext ctx = ServletActionContext.getServletContext();
		HttpServletRequest req = ServletActionContext.getRequest();
		HttpServletResponse res = ServletActionContext.getResponse();
		try {
			ctx.getRequestDispatcher(finalLocation).include(req, res);
		} catch (ServletException e) {
			LOG.error("ServletException including " + finalLocation, e);
			throw e;
		} catch (IOException e) {
			LOG.error("IOException while including result '" + finalLocation + "'", e);
			throw e;
		}
	}

}
