package OLink.bpm.base.web.ServletResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.util.OBPMDispatcher;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.webwork.dispatcher.ServletRedirectResult;
import com.opensymphony.xwork.ActionInvocation;

public class OBPMServletRedirectResult extends ServletRedirectResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7182913458562750604L;

	public void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		finalLocation = new OBPMDispatcher().getDispatchURL(finalLocation, request, response);
		super.doExecute(finalLocation, invocation);
	}
}
