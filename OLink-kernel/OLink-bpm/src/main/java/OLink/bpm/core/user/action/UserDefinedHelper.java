package OLink.bpm.core.user.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcess;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.homepage.ejb.ReminderProcess;
import OLink.bpm.core.role.ejb.RoleVO;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.user.ejb.UserDefined;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import OLink.bpm.base.action.BaseHelper;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcessBean;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.util.StringUtil;


public class UserDefinedHelper extends BaseHelper<UserDefined> {

	private static final Logger log = Logger.getLogger(UserDefinedHelper.class);
	
	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public UserDefinedHelper() throws Exception {
		super(ProcessFactory.createProcess(UserDefinedProcess.class));
	}

	public boolean isHomePageExist(WebUser user) throws Exception {
		Collection<UserDefined> userDefinedlist = ((UserDefinedProcess) process)
				.doViewByApplication(getApplicationid());
		if (userDefinedlist == null || userDefinedlist.size() == 0)
			return false;
		String rolelist = user.getRolelist(getApplicationid());
		String[] roles = rolelist.split(",");
		if (roles == null || roles.length <= 0)
			return false;
		for (Iterator<UserDefined> iterator = userDefinedlist.iterator(); iterator.hasNext();) {
			UserDefined home = iterator.next();
			if("1".equals(home.getDisplayTo())){
				return true;
			}else{
				String homeRoles = home.getRoleIds();
				if (homeRoles == null
						|| homeRoles.length() <= 0){
					return true;
				}
				for (int i = 0; i < roles.length; i++) {
					if (homeRoles.indexOf(roles[i]) >= 0)
						return true;
				}
			}
		}
		return false;
	}
	
	
	public boolean isUseHomePageInCurrentApp(WebUser user,String appid){
		if(user.getUserSetup()==null || user.getUserSetup().getStatus()!=1){
			return false;
		}else{
			JSONObject usePageJsonObj;
			try {
				usePageJsonObj = new JSONObject(user.getUserSetup().getGeneralPage());
				String usePageId= (String) usePageJsonObj.get(appid);
				return usePageId != null && !usePageId.equals("");
			} catch (JSONException e) {
				return false;
			}
		}
	}
	
	public String getUserSetHomePage(WebUser user, ParamsTable params) throws Exception{
		//获取皮肤参数
		String pendingllistjson = "";
		if(user.getUserSetup() != null){
			pendingllistjson=user.getUserSetup().getPendingStyle();
		}
		String appid=(String)params.getParameter("application");
		
		JSONObject jsonObj = new JSONObject(pendingllistjson); 
		String[] summarys= ((String)jsonObj.get(appid)).split(",");
		StringBuffer html = new StringBuffer();
		String contextPath = Environment.getInstance().getContextPath();
		SummaryCfgProcess process=(SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
		for (int i=0;i<summarys.length;i++) {
			//应该根据还usePageId
			SummaryCfgVO summaryCfg =(SummaryCfgVO) process.doView(summarys[i]);
			if(summaryCfg!=null 
					&& summaryCfg.getApplicationid()!=null 
					&& summaryCfg.getApplicationid().equals(appid)){
				StringBuffer htmlBuilder = new StringBuffer();
				htmlBuilder.append("<div id='"+summaryCfg.getId()+"'>");
				htmlBuilder.append("<iframe");
				htmlBuilder.append(" src='" + contextPath);
				htmlBuilder
						.append("/portal/dynaform/document/pendinglist.action?formid=");
				htmlBuilder.append(summaryCfg.getFormId());
				htmlBuilder.append(
						"&_pagelines=10&reminderid=" + summaryCfg.getId()
								+ "&_orderby=" + summaryCfg.getOrderby()).append(
						"'");
				htmlBuilder.append(" frameborder='0'");
				htmlBuilder.append(" height='250px'");
				htmlBuilder.append(" width='" + 320 + "px'");
				htmlBuilder.append(" style='text-align:");
				htmlBuilder.append(">");
				htmlBuilder.append("</iframe>");
				htmlBuilder.append("</div>");
				html.append(htmlBuilder);
			}
		}
		if (StringUtil.isBlank(html.toString())) {
			StringBuffer htmlBuilder = new StringBuffer();
			htmlBuilder.append("<font size='2' color='red'>{*[core.homepage.onreminder]*}!</font>");
			html.append(htmlBuilder);
		}
		return html.toString() ;
	}
	
	public String getPageToHtml(WebUser user, ParamsTable params) {
		StringBuffer buff = new StringBuffer();
		JSONObject jsonObj;
		try {
			//获取皮肤参数
			if(user.getUserSetup() != null){
				jsonObj = new JSONObject(user.getUserSetup().getGeneralPage());
				String usePageId= (String) jsonObj.get(params.getParameterAsString("application"));
				UserDefinedProcess pp = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
				UserDefined page=(UserDefined) pp.doView(usePageId);
				if(page!=null)
					buff.append(page.toHtml(params, user));
			}
		} catch (Exception e) {
			return "";
		} 
		return buff.toString();
	}

	public String toHtml(WebUser user, ParamsTable params) throws Exception {
		Collection<UserDefined> homePages = getDefaultHomePage(user);
		StringBuffer htmlXml = new StringBuffer();
		String html = "";
		
		for (Iterator<UserDefined> iterator = homePages.iterator(); iterator.hasNext();) {
			UserDefined home = iterator.next();
			html = home.toHtml(params, user);
			if (!StringUtil.isBlank(html)) {
				break;
			}
		}
		
		if (html.length() > 0 && !"".equals(html)) {
			htmlXml.append(html);
		}
		return htmlXml.toString();
	}

	public Collection<UserDefined> getDefaultHomePage(WebUser user) throws Exception {
		Collection<UserDefined> userDefinedlist = ((UserDefinedProcess) process)
				.doViewByApplication(getApplicationid());
		String rolelist = user.getRolelist();
		rolelist = rolelist.replaceAll("'", "");
		String[] roles = rolelist.split(",");
		Collection<UserDefined> rtnList = new HashSet<UserDefined>();
		for (Iterator<UserDefined> iterator = userDefinedlist.iterator(); iterator.hasNext();) {
			UserDefined home = iterator.next();
			String homeRoles = home.getRoleIds();
			if (homeRoles == null
					|| homeRoles.length() <= 0){
				rtnList.add(home);
				continue;
			}
			for (int i = 0; i < roles.length; i++) {
				if (homeRoles.indexOf(roles[i]) >= 0) {
					rtnList.add(home);
					break;
				}
			}
		}
		return rtnList;
	}

	/**
	 * 根据reminder的id来取得一个reminder的对象
	 * 
	 * @param reminderId
	 * @return
	 */
	public Reminder getReminderId(String reminderId) throws Exception {
		ReminderProcess rem = (ReminderProcess) ProcessFactory
				.createProcess(ReminderProcess.class);
		return (Reminder) rem.doView(reminderId);
	}
	
	//@SuppressWarnings("unchecked")
	public List<PendingVO> getPendingsByHomePage(UserDefined home, WebUser user, ParamsTable params) {
		List<PendingVO> list = new ArrayList<PendingVO>();
		try {

//			Collection<Reminder> reminders = home.getReminders();
			Collection<SummaryCfgVO> summarys = home.getSummaryCfgs();
			for (Iterator<SummaryCfgVO> it = summarys.iterator(); it.hasNext();) {
				SummaryCfgVO summary = it.next();
				PendingProcess process = new PendingProcessBean(home
						.getApplicationid());
				params.setParameter("formid", summary.getFormId());
				params.setParameter("_orderby", summary.getOrderby());
				DataPackage<PendingVO> datas = process.doQueryByFilter(params, user);
				list.addAll(datas.datas);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取当前应用下的所以待办提醒
	 * @param user
	 * @return
	 */
	//@SuppressWarnings("unchecked")
	public List<PendingVO> getAllPendings(WebUser user) {
		List<PendingVO> list = new ArrayList<PendingVO>();
		try {
			Collection<UserDefined> homes = getDefaultHomePage(user);
			for (Iterator<UserDefined> iterator = homes.iterator(); iterator.hasNext();) {
				UserDefined home = iterator.next();
//				Collection<Reminder> reminders = home.getReminders();
				Collection<SummaryCfgVO> summarys = home.getSummaryCfgs();
				for (Iterator<SummaryCfgVO> it = summarys.iterator(); it.hasNext();) {
					SummaryCfgVO summary = it.next();
					PendingProcess process = new PendingProcessBean(home
							.getApplicationid());
					ParamsTable params = new ParamsTable();
					params.setParameter("formid", summary.getFormId());
					params.setParameter("_orderby", summary.getOrderby());
					DataPackage<PendingVO> datas = process.doQueryByFilter(params, user);
					list.addAll(datas.datas);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取当前用户摘要
	 * @param user
	 * @return
	 */
	public Collection<SummaryCfgVO> getUserSummaryCfg(WebUser user) {
		Collection<SummaryCfgVO> list = new ArrayList<SummaryCfgVO>();
		if (isUseHomePageInCurrentApp(user, getApplicationid())) {
			//获取皮肤参数
			String pendingllistjson = "";
			if(user.getUserSetup() != null){
				pendingllistjson = user.getUserSetup().getPendingStyle();
			}
			String appid = super.getApplicationid();
			try {
				JSONObject jsonObj = new JSONObject(pendingllistjson); 
				String[] summarys = ((String)jsonObj.get(appid)).split(",");
				SummaryCfgProcess process=(SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
				for (int i = 0; i < summarys.length; i++) {
					SummaryCfgVO summaryCfg =(SummaryCfgVO) process.doView(summarys[i]);
					if (summaryCfg != null) {
						list.add(summaryCfg);
					}
				}
			} catch (Exception e) {
				log.warn(e);
			}
			return list;
		}
		try {
			if (isHomePageExist(user)) {
				Collection<UserDefined> homePages = getDefaultHomePage(user);
				Iterator<UserDefined> iterator = homePages.iterator();
				while (iterator.hasNext()) {
					UserDefined defined = iterator.next();
					if (defined.getDefineMode().intValue() == UserDefined.REGULAR_MODE) {
						if (defined.getPublished()) {
							//if (defined.getSummaryCfgs() != null && 
							//		!defined.getSummaryCfgs().isEmpty()) {
							//	return defined.getSummaryCfgs();
							//}
							list = getDafultSummaryCfg(user);
							if (list != null && !list.isEmpty()) {
								return list;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.warn(e);
		}
		return list;
	}
	
	public Collection<SummaryCfgVO> getDafultSummaryCfg(WebUser user) throws Exception {
		Collection<RoleVO> userRoles = user.getRoles();
		RoleVO roleVO = new RoleVO();
		ParamsTable params = new ParamsTable();
		params.setParameter("t_applicationid", applicationid);
		params.setParameter("n_published", true);
		//获取当前软件下所有首页待办的角色
		UserDefinedProcess udprocss = (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
		UserDefined userDefined = null;
		DataPackage<UserDefined> dataPackage1 = udprocss.doQuery(params);
		if(dataPackage1.datas != null && !dataPackage1.datas.isEmpty()){
			for(Iterator<UserDefined> ite1 = dataPackage1.datas.iterator(); ite1.hasNext(); ){
				UserDefined userDefined1 = ite1.next();
				//判断是否适用于所有角色
				if("1".equals(userDefined1.getDisplayTo())){
					userDefined = userDefined1;
				}else{
					//获取某一首页的角色
					String roleIds = userDefined1.getRoleIds();
					if(!StringUtil.isBlank(roleIds)){
						String[] userRoleIds = roleIds.split(",");
						for(int i=0; i<userRoleIds.length; i++){
							if(userRoles.size()>0){
								for(Iterator<RoleVO> ite2 = userRoles.iterator(); ite2.hasNext(); ){
									roleVO = ite2.next();
									if(userRoleIds[i].equals(roleVO.getId())){
										//当前角色与 后台首页待办设置的角色 相同时，返回此后台定制的首页待办信息
										userDefined = userDefined1;
									}
								}
							}
						}
					}
				}
				if (userDefined != null) {
					Collection<SummaryCfgVO> result = getSummaryCfgByTemplateElement(userDefined);
					if (result != null && !result.isEmpty()) {
						return result;
					}
				}
			}
		}
		return new ArrayList<SummaryCfgVO>();
	}
	
	public Collection<SummaryCfgVO> getSummaryCfgByTemplateElement(UserDefined defined) throws Exception {
		Collection<SummaryCfgVO> result = new ArrayList<SummaryCfgVO>();
		String templateElement = defined.getTemplateElement();
		if(!StringUtil.isBlank(templateElement) && templateElement.length() > 1){
			templateElement = templateElement.substring(1, templateElement.length() - 1);
			String[] templateElements = templateElement.split(",");
			for (int i = 0; i < templateElements.length; i++) {
				String[] templateElementSubs = templateElements[i].split(":");
				
				if(!StringUtil.isBlank(templateElementSubs[0])){
					if (templateElementSubs.length < 2) continue;
					//摘要Id和title
					String[] templateTdEles = templateElementSubs[1].split(";");
					if (templateTdEles[0].length() < 1) continue;
					String templateTdEle = templateTdEles[0].substring(1, templateTdEles[0].length() - 1);
					//摘要id数组
					templateTdEles = templateTdEle.split("\\|");
					if (templateTdEles.length >= 1) {
						SummaryCfgProcess process=(SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);
						for (int j = 0; j < templateTdEles.length; j++) {
							String summaryCfgid = templateTdEles[j];
							if (!StringUtil.isBlank(summaryCfgid)) {
								SummaryCfgVO summaryCfg =(SummaryCfgVO) process.doView(summaryCfgid);
								if (summaryCfg != null) {
									result.add(summaryCfg);
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

}
