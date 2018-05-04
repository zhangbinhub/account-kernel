package OLink.bpm.core.report.wfdashboard.action;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.core.report.wfdashboard.ejb.DashBoardVO;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.report.wfdashboard.ejb.DashBoardProcessBean;
import OLink.bpm.constans.Web;
import OLink.bpm.core.workflow.storage.definition.action.BillDefiHelper;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.report.wfdashboard.ejb.DashBoardProcess;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

public class WFDashBoardAction extends ActionSupport implements Action {

	private static final long serialVersionUID = 1L;

	private DataPackage<DashBoardVO> datas = null;

	protected DashBoardProcess process = null;

	protected String[] _selects = null;

	private ParamsTable params;

	protected String application;

	private ValueObject content = null;

	//private ArrayList gdatas = new ArrayList();

	//private ArrayList labels = new ArrayList();

	//private ArrayList<String> colors = new ArrayList<String>();

	//private ArrayList links = new ArrayList();
	
	String chartstr = ""; 

	public String getChartstr() {
		return chartstr;
	}

	public void setChartstr(String chartstr) {
		this.chartstr = chartstr;
	}

	public ValueObject getContent() {
		return content;
	}

	public void setContent(ValueObject content) {
		this.content = content;
	}

	/**
	 * BaseAction constructor
	 * 
	 * @param process
	 *            The BaseProcess
	 * @param content
	 *            The ValueObject
	 */
	public WFDashBoardAction() {
		this.content = new DashBoardVO();
	}

	
	/**
	 * 返回查询页面
	 * 
	 * @return "SUCCESS" when action run successfully, "INPUT" when the input
	 *         doesn't pass validation. "ERROR" when error occur.
	 * @throws Exception
	 */
	public String doQuery() throws Exception {
	   return SUCCESS;
	}
	/**
	 * The action to summary approving record for each workflow
	 * 
	 * @return "SUCCESS" when action run successfully, "INPUT" when the input
	 *         doesn't pass validation. "ERROR" when error occur.
	 * @throws Exception
	 */

	public String doSumWF() throws Exception {
		try {
			this.process = new DashBoardProcessBean(getApplication());
			ParamsTable params = getParams();
			String domainId = params.getParameterAsString("domain");
			setChartstr(process.getSumWFChartStr(domainId));
			
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			return INPUT;
		}

	}

	/**
	 * The action to summary approving record for each statelabel
	 * 
	 * @return "SUCCESS" when action run successfully, "INPUT" when the input
	 *         doesn't pass validation. "ERROR" when error occur.
	 * @throws Exception
	 */
	public String doSumStateLabel() throws Exception {
		try {
			this.process = new DashBoardProcessBean(getApplication());
			ParamsTable params = getParams();
			String domainId = params.getParameterAsString("domain");
			setChartstr(process.getSumStateLabelChartStr(domainId, getFlowId()));
			return SUCCESS;

		} catch (Exception e) {
			return INPUT;
		}

	}

	/**
	 * The action to summary approving record for each role
	 * 
	 * @return "SUCCESS" when action run successfully, "INPUT" when the input
	 *         doesn't pass validation. "ERROR" when error occur.
	 * @throws Exception
	 */
	public String doSumRole() throws Exception {
		try {
			this.process = new DashBoardProcessBean(getApplication());
			ParamsTable params = getParams();
			String domainId = params.getParameterAsString("domain");
			int CurrentPag = Integer.parseInt(params.getParameterAsString("_currpage")==null?"1":params.getParameterAsString("_currpage"));
			String flowId = getFlowId();
			DataPackage<DashBoardVO> dpg = (process.getSumRole(domainId, flowId, CurrentPag));
			this.setDatas(dpg);
			return SUCCESS;
		} catch (Exception e) {
			return INPUT;
		}

	}

	/**
	 * The action to summary approving record for each role
	 * 
	 * @return "SUCCESS" when action run successfully, "INPUT" when the input
	 *         doesn't pass validation. "ERROR" when error occur.
	 * @throws Exception
	 */
	public String doSumTime() throws Exception {
		try {
			this.process = new DashBoardProcessBean(getApplication());
			ParamsTable params = getParams();
			String domainId = params.getParameterAsString("domain");

			setChartstr(this.process.getSumTimeChartStr(domainId, getFlowId()));

			return SUCCESS;
		} catch (Exception e) {
			return INPUT;
		}

	}

	/**
	 * Get the application
	 * 
	 * @return The application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * Set the application
	 * 
	 * @param application
	 *            The application to set.
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * Get the Parameters table
	 * 
	 * @return ParamsTable
	 */
	public ParamsTable getParams() {
		if (params == null) {
			// If the parameters table is empty, then initiate it.
			params = new ParamsTable();

			// put all the request parameters map in to parameters table.
			putRequestParameters();

			// put the application id to parameters table.
			if (getApplication() != null)
				params.setParameter("application", getApplication());
			// put the session id to parameters table.
			if (getSessionid() != null)
				params.setSessionid(getSessionid());

			// put the page line count id to parameters table.
			if (params.getParameter("_pagelines") == null)
				params.setParameter("_pagelines", Web.DEFAULT_LINES_PER_PAGE);
		}

		return params;
	}

	/**
	 * Get the ActionContext
	 * 
	 * @return ActionContext
	 */
	public static ActionContext getContext() {
		ActionContext context = ActionContext.getContext();
		return context;
	}

	/**
	 * Get the session id.
	 * 
	 * @return The session id.
	 */
	public String getSessionid() {
		return ServletActionContext.getRequest().getSession().getId();
	}

	/**
	 * @SuppressWarnings servlet不支持泛型
	 * Put all the request parameters map in to parameters table.
	 */
	@SuppressWarnings("unchecked")
	private void putRequestParameters() {
		HttpServletRequest request = ServletActionContext.getRequest();
		Map m = request.getParameterMap();

		Iterator<Entry<?, ?>> iter = m.entrySet().iterator();
		while (iter != null && iter.hasNext()) {
			Entry<?, ?> entry = iter.next();
			//String name = (String) iter.next();
			String name = (String)entry.getKey();
			Object value = entry.getValue();
			try {
				// If there is only one string in the string array, the put the
				// string only, not array.
				if (value instanceof String[])
					if (((String[]) value).length > 1)
						params.setParameter(name, value);
					else
						params.setParameter(name, ((String[]) value)[0]);
				else
					params.setParameter(name, value);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Get the DataPackage
	 * 
	 * @return the DataPackage
	 */
	public DataPackage<DashBoardVO> getDatas() {
		return datas;
	}

	/**
	 * @param datas
	 *            the datas to set
	 */
	public void setDatas(DataPackage<DashBoardVO> datas) {
		this.datas = datas;
	}

	/**
	 * 取得flowid,如果flowid为空则取第一个流程
	 * 
	 * @throws exception
	 */
	public String getFlowId() throws Exception {
		String rtn = "";
		ParamsTable params = getParams();
		String flowId = params.getParameterAsString("flowid");

		if(StringUtil.isBlank(flowId)){
			BillDefiHelper helper = new BillDefiHelper();
			String domainId = params.getParameterAsString("domain");
			rtn = helper.get_FirstflowId(getApplication(),domainId);
		}else{
			rtn = flowId;
		}	
		return rtn;
	}
	

}
