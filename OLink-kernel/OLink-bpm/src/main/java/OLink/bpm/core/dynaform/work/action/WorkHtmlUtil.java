package OLink.bpm.core.dynaform.work.action;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Web;
import OLink.bpm.core.dynaform.work.ejb.WorkVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;

/**
 * @author Happy
 * 
 */
public class WorkHtmlUtil {

	protected WebUser webUser;

	protected ParamsTable params;

	protected HttpServletRequest request;

	protected Collection<WorkVO> works;

	public WorkHtmlUtil(HttpServletRequest request) {
		super();
		setRequest(request);
	}

	public WebUser getWebUser() {
		return webUser;
	}

	public void setWebUser(WebUser webUser) {
		this.webUser = webUser;
	}

	public ParamsTable getParams() {
		return params;
	}

	public void setParams(ParamsTable params) {
		this.params = params;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	@SuppressWarnings("unchecked")
	public void setRequest(HttpServletRequest request) {
		this.request = request;
		params = ParamsTable.convertHTTP(request);
		webUser = (WebUser) request.getSession().getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		Object workDatas = request.getAttribute("workDatas");
		if (workDatas != null) {
			setWorks(((DataPackage<WorkVO>) workDatas).getDatas());
		}
	}

	public Collection<WorkVO> getWorks() {
		return works;
	}

	public void setWorks(Collection<WorkVO> works) {
		this.works = works;
	}

	/**
	 * 输出主页上的流程监控连接HTML
	 * 
	 * @return
	 */
	public String toHomePageHTML() {
		StringBuffer html = new StringBuffer();
		/*
		 * boolean isWorkManager = false; for (Iterator<RoleVO> iter =
		 * webUser.getRoles().iterator(); iter .hasNext();) { if
		 * (RoleVO.WORK_MANAGER_TRUE.equals(iter.next().getIsWorkManager())) {
		 * isWorkManager = true; break; } } if (isWorkManager) { html
		 * .append("<a title=\"{*[core.dynaform.work.monitoringWork]*}\" href=\""
		 * + request.getContextPath() +
		 * "/portal/dynaform/work/workList.action?_actorId="
		 * +webUser.getId()+"\" target=\"detail\"><img class=\"icon\" src=\"" +
		 * request.getContextPath() +
		 * "/resource/imgv2/front/main/mywork.gif\" /></a>"); } else {
		 */
		String applicationid = request.getParameter(Web.REQUEST_ATTRIBUTE_APPLICATION);
		//update by zb 2013-6-25
		html.append("<input type=\"hidden\" id=\"MyWorkIdValue\" value=\"" + request.getContextPath()
			+ "/portal/dynaform/work/workList.action?_actorId=" + webUser.getId() + "&actionMode=work&_processType=processing&application=" + applicationid + "\"/>");
					
		return html.toString();
	}

	public Map<String, String> getFlowMap() throws Exception {
		String applicationid = request.getParameter(Web.REQUEST_ATTRIBUTE_APPLICATION);
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("", "");
		BillDefiProcess bp = (BillDefiProcess) ProcessFactory.createProcess(BillDefiProcess.class);
		Collection<BillDefiVO> col = bp.doSimpleQuery(new ParamsTable(), applicationid);

		Iterator<BillDefiVO> it = col.iterator();
		while (it.hasNext()) {
			BillDefiVO bv = it.next();
			map.put(bv.getId(), bv.getSubject());
		}
		return map;
	}

}
