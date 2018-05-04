package OLink.bpm.desktop.login;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.constans.Web;
import OLink.bpm.core.deploy.application.ejb.ApplicationVO;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcess;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.user.action.UserDefinedHelper;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcessBean;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

public class DLoginHelper {

	private static final Logger LOG = Logger.getLogger(DLoginHelper.class);
	
	private HttpServletRequest request;
	//private HttpServletResponse response;
	
	public DLoginHelper(HttpServletRequest request, 
			HttpServletResponse response) {
		this.request = request;
		//this.response = response;
	}
	
	/**
	 * @deprecated 旧版本方法，已掉弃
	 * @return
	 */
	public String getChangePendingXml() {
		return processPending();
	}
	
	/**
	 * 代码需要优化
	 * @deprecated 旧版本方法，已掉弃
	 * @SuppressWarnings Servlet API不支持泛型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private String processPending() {
		StringBuffer sb = new StringBuffer();
		List<String> newCompare = new ArrayList<String>();
		HttpSession session = request.getSession();
		List<String> compareList = (List<String>) session.getAttribute(DLoginAction.PENGING_LIST);
		WebUser webUser = (WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_FRONT_USER);
		try {
			DomainProcess process = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
			DomainVO vo = (DomainVO) process.doView(webUser.getDomainid());
			Collection<ApplicationVO> apps = vo.getApplications();
			for (Iterator<ApplicationVO> it = apps.iterator(); it.hasNext();) {
				ApplicationVO app = it.next();
				UserDefinedHelper hph = new UserDefinedHelper();
				hph.setApplicationid(app.getId());
				Collection<UserDefined> homes = hph.getDefaultHomePage(webUser);
				if (homes == null || homes.isEmpty()) {
					continue;
				}
				UserDefined home = homes.iterator().next();
				Collection<Reminder> reminders = home.getReminders();
				for (Iterator<Reminder> it2 = reminders.iterator(); it2.hasNext();) {
					Reminder reminder = it2.next();
					PendingProcess pProcess = new PendingProcessBean(home
							.getApplicationid());
					ParamsTable params = new ParamsTable();
					params.setParameter("formid", reminder.getFormId());
					params.setParameter("_orderby", reminder.getOrderby());
					params.setParameter("_pagelines", Integer.MAX_VALUE + "");
					DataPackage<PendingVO> datas = pProcess.doQueryByFilter(params, webUser);
					Collection<PendingVO> list = datas.datas;
					for (Iterator<PendingVO> it3 = list.iterator(); it3.hasNext();) {
						PendingVO pending = it3.next();
						if (!compareList.contains(pending.getId())) {
							// 增加一条待办
							sb.append("<"+ MobileConstant.TAG_CHANGE + " " + MobileConstant.ATT_ID + "='" + pending.getId() + "' " + MobileConstant.ATT_GROUPID + "='" + app.getId() + "' " + MobileConstant.ATT_OPTION + "='1'>");
							
							sb.append("<" + MobileConstant.TAG_PENDING_ITEM + " ");
							String url = "/portal/dynaform/document/view.action";
							url += "?_docid=" + pending.getId();
							url += "&amp;_formid=" + pending.getFormid();
							url += "&amp;_backURL=" + request.getContextPath()
									+ "/portal/dispatch/homepage.jsp";
							sb.append(MobileConstant.ATT_ID + "='" + pending.getId()
									+ "' ");
							sb.append(MobileConstant.ATT_URL + "='" + url + "'>");
							sb.append("(" + reminder.getTitle() + ")" + pending.getSummary());
							sb.append("</" + MobileConstant.TAG_PENDING_ITEM + ">");
							
							sb.append("</"+MobileConstant.TAG_CHANGE + ">");
						}
						newCompare.add(pending.getId());
					}
				}
				try {
					PersistenceUtils.closeSessionAndConnection();
				} catch (Exception e) {
					LOG.warn(e);
				}
			}
			
			List<String> list = compare(compareList, newCompare);
			for (Iterator<String> it = list.iterator(); it.hasNext();) {
				String id = it.next();
				// 删除一条待办
				sb.append("<"+MobileConstant.TAG_CHANGE + " " + MobileConstant.ATT_OPTION + "='0'>");
				sb.append(id).append("</"+MobileConstant.TAG_CHANGE + ">");
			}
			session.setAttribute(DLoginAction.PENGING_LIST, newCompare);
		} catch (Exception e) {
			LOG.warn(e);
			return sb.toString();
		}
		return sb.toString();
	}
	
	/**
	 * 查找参数1在参数2不存在的对象
	 * @param list1
	 * @param list2
	 * @return
	 */
	private List<String> compare(List<String> list1, List<String> list2) {
		List<String> list = new ArrayList<String>();
		for (Iterator<String> it = list1.iterator(); it.hasNext();) {
			String object = it.next();
			if (!list2.contains(object)) {
				list.add(object);
			}
		}
		return list;
	}

}
