package OLink.bpm.core.user.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Web;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.homepage.ejb.ReminderProcess;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.user.ejb.UserDefined;

import com.opensymphony.webwork.ServletActionContext;

public class UserDefinedAction extends BaseAction<UserDefined> {

	protected String userId;
	protected String templateStyle;
	protected String templateElement;
	private String tempRoles;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4664734701291763551L;

	public final static int REGULAR_MODE = 16, CUSTOMIZE_MODE = 256;
	
	@SuppressWarnings("unchecked")
	public UserDefinedAction() throws Exception {
		super(ProcessFactory.createProcess(UserDefinedProcess.class), new UserDefined());
	}

	
	public String doUserDefinedList() throws Exception{
		try {
			String hql = "from UserDefined ud where ud.userId is null or userId=''";
			datas = this.getProcess().getDatapackage(hql, getParams());
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public String doEditHomepage() throws Exception{
		try {
			Map params = getContext().getParameters();

			String id = ((String[]) params.get("id"))[0];
			ValueObject contentVO = process.doView(id);
			setContent(contentVO);
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

	public String doSaveElement() {
		try {
			UserDefined userDefined = (UserDefined) getContent();
			
			if (process.doView(userDefined.getId()) == null) {
				process.doCreate(userDefined);
			} else {
				process.doUpdate(userDefined);
			}
			this.addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
		
	public String doSaveAndNewHomepage() {
		try {
			UserDefined userDefined = (UserDefined) getContent();

			if (process.doView(userDefined.getId()) == null) {
				process.doCreate(userDefined);
			} else {
				process.doUpdate(userDefined);
			}

			UserDefined userDefined1 = new UserDefined();
			userDefined1.setType("1");
			userDefined1.setDisplayTo("1");
			userDefined1.setDefineMode(REGULAR_MODE);
			HttpSession session=ServletActionContext.getRequest().getSession();
			WebUser webUser = (WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_USER);
			userDefined1.setCreator(webUser.getName());
			this.setContent(userDefined1);

			this.addActionMessage("{*[Save_Success]*}");
			
			return SUCCESS;
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	public String doNewHomepage() {
		try {
			UserDefined userDefined = new UserDefined();
			userDefined.setType("1");
			userDefined.setDisplayTo("1");
			userDefined.setDefineMode(REGULAR_MODE);
			HttpSession session=ServletActionContext.getRequest().getSession();
			WebUser webUser = (WebUser) session.getAttribute(Web.SESSION_ATTRIBUTE_USER);
			userDefined.setCreator(webUser.getName());
			this.setContent(userDefined);
			
		} catch (Exception e) {
			return INPUT;
		}
			return SUCCESS;
	}
	
	public String doDelete() {
		try {			
			super.doDelete();
			String hql = "from UserDefined ud where (ud.userId is null or ud.userId='') and ud.applicationid='" + this.application + "'";
			datas = this.getProcess().getDatapackage(hql, getParams());
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			doList();
			return INPUT;
		}
		return SUCCESS;
	}
	
	public String doQueryHomepage() {
		try {
			String hql = "from UserDefined ud where (ud.userId is null or ud.userId='') and ud.applicationid='" + this.application + "'";
			datas = this.getProcess().getDatapackage(hql, getParams());
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			doList();
			return INPUT;
		}
		return SUCCESS;
	}

	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getTemplateStyle() {
		return templateStyle;
	}


	public void setTemplateStyle(String templateStyle) {
		this.templateStyle = templateStyle;
	}


	public String getTemplateElement() {
		return templateElement;
	}


	public void setTemplateElement(String templateElement) {
		this.templateElement = templateElement;
	}


	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	
	public String get_defineMode(){
		UserDefined content = (UserDefined)getContent();
		if(content != null){
			return String.valueOf(content.getDefineMode());
		}
		return String.valueOf(UserDefined.REGULAR_MODE);
	}
	
	public void set_defineMode(String _defineMode){
		UserDefined content = (UserDefined)getContent();
		if(content != null){
			content.setDefineMode(Integer.parseInt(_defineMode));
		}else{
			content = new UserDefined();
			content.setDefineMode(Integer.parseInt(_defineMode));
			setContent(content);
		}
	}
	
	/*
	 * get Page list
	 * */
	public String doPageList() throws Exception{
		HttpServletResponse response = ServletActionContext.getResponse();
		String html="";
		DataPackage<UserDefined> datas = process.doQuery(getParams());
		html=doRemindListToHtml(datas.datas,(String)getParams().getParameter("definemode"));
		
		if (!"".equals(html.toString())) {
			response.setContentType("text/html;charset=UTF-8");
			response.getWriter().write(html.toString());
		}
		return html;
	}	
	public String doSelectTemplate() throws Exception{
		return SUCCESS;
	}
	
	public String doAddReminderList() throws Exception{
		return SUCCESS;
	}
	
	public String doRemindListToHtml(Collection<UserDefined> collection,String definemode){
		StringBuffer html =new StringBuffer();
		if(definemode.equals("1")){
			definemode="16";
		}else if(definemode.equals("0")){
			definemode="256";
		}
		try {
			for(Iterator<UserDefined> iter=collection.iterator();iter.hasNext();){
				UserDefined userDefined= iter.next();
				if(userDefined.getDefineMode().toString().equals(definemode)){
					html.append("<div class='pendingitem'><div class='pendingitemtitle'>");
					html.append("<table class='table_noborder' height='18px'><tr>");
					if(definemode.equals("16")){
						html.append("<td width='70px'>{*[Default]*}{*[HomePage]*}：</td>");
					}else{
						html.append("<td width='70px'>{*[Customize]*}{*[Page]*}：</td>");
					}
					html.append("<td align='left'>"+userDefined.getName()+"</td>");
					html.append("<td width='100px' align='right'><input onclick='selectHomePage(this)' name='pageItemCheckbox' type='radio' id='"+userDefined.getId()+"'></td>");
					html.append("</tr></table></div>");
					html.append("<div class='pendingitemtitlecontent'>"+userDefined.getName()+"</div></div>");
				}
			}
		} catch (Exception e) {
			return "";
		}
		return html.toString();
	}

	/**
	 * 是否显示发布
	 * 
	 * @return
	 */
	public String get_isPublished() {
		UserDefined content = (UserDefined) getContent();
		if (content.getPublished()) {
			return "true";
		} else {
			return "false";
		}
	}

	public void set_isPublished(String _isPublished) {
		UserDefined content = (UserDefined) getContent();
		if (_isPublished != null) {
			if (_isPublished.trim().equalsIgnoreCase("true")) {
				content.setPublished(true);
				return;
			}
		}
		content.setPublished(false);
	}
	
//	public String doNew(){
//		UserDefined homepage = new UserDefined();
//		setContent(homepage);
//		return SUCCESS;
//	}
	
//	private void validation() throws Exception {
//		UserDefined page = (UserDefined) getContent();
//		if (page == null || StringUtil.isBlank(page.getName())) {
//			throw new Exception("{*[page.name.notexist]*}!");
//		}
//		if (StringUtil.specialSymbols(page.getName())) {
//			throw new Exception("{*[core.name.illegal]*}: " + page.getName());
//		}
//		int count = ((UserDefinedProcess)process).doViewCountByName(page.getName(), page.getApplicationid());
//		if (count > 1) {
//			throw new Exception("{*[core.form.exist]*}");
//		} else if (count == 1) {
//			UserDefined hp = (UserDefined) process.doViewByName(page.getName(), page.getApplicationid());
//			if (hp != null) {
//				if (StringUtil.isBlank(page.getId())) {
//					throw new Exception("{*[core.form.exist]*}");
//				} else {
//					if (!page.getId().equals(hp.getId())) {
//						throw new Exception("{*[core.form.exist]*}");
//					}
//				}
//			}
//		}
//	}

//	public String doHomeDelete() {
//		try {
//			PersistenceUtils.beginTransaction();
//			if (_selects != null && _selects.length > 0) {
//				for (int i = 0; i < _selects.length; i++) {
//					UserDefinedProcess process = getProcess();
//					UserDefined homepage = (UserDefined) process.doView(_selects[i]);
//					for (Iterator<Reminder> iterator = homepage.getReminders().iterator(); iterator.hasNext();) {
//						Reminder reminder = (Reminder) iterator.next();
//						reminder.setHomepage(null);
//					}
//					homepage.setReminders(null);
//					process.doUpdate(homepage);
//					process.doRemove(homepage);
//				}
//
//			} else {
//				throw new Exception("{*[page.records.notChoose]*}");
//			}
//			PersistenceUtils.commitTransaction();
//		} catch (Exception e) {
//			this.addFieldError("1", e.getMessage());
//			//e.printStackTrace();
//			//doList();
//			return INPUT;
//		}
//		addActionMessage("{*[delete.successful]*}");
//		return doList();
//	}

	public String doAddReminder() throws Exception {
		try {
			ParamsTable params = getParams();
			String ids[] = params.getParameterAsArray("_selects");
			if (ids == null || ids.length <= 0) {
				throw new Exception("{*[core.Reminder.notChoose]*}");
			} else {
				String homepageid = params.getParameterAsString("s_homepage");
				UserDefinedProcess process = getProcess();
				ReminderProcess remprocess = (ReminderProcess) ProcessFactory
						.createProcess(ReminderProcess.class);
				UserDefined homepage = (UserDefined) process.doView(homepageid);
				Set<Reminder> old = new HashSet<Reminder>();
				old.addAll(homepage.getReminders());
				for (int i = 0; i < ids.length; i++) {
					Reminder vo = (Reminder) remprocess.doView(ids[i]);
					if (vo != null) {
						old.add(vo);
					}
				}
				
				homepage.setReminders(old);
				process.doUpdate(homepage);
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	public UserDefinedProcess getProcess() throws Exception {
		return (UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
	}
	public String getTempRoles() {
		return tempRoles;
	}

	public void setTempRoles(String tempRoles) {
		this.tempRoles = tempRoles;
	}
	
	/**
	 * 在首页更改待办布局和提醒后保存
	 * @return
	 * @throws Exception
	 */
	public String saveAtHomePage() throws Exception{
		ParamsTable params = getParams();
		String id = params.getParameterAsString("id");
		String templateStyle = params.getParameterAsString("templateStyle");
		String templateElement = params.getParameterAsString("templateElement");
		if(!StringUtil.isBlank(id)){
			UserDefined vo = (UserDefined) process.doView(id);
			if(vo != null){
				vo.setTemplateStyle(templateStyle);
				vo.setTemplateElement(templateElement);
				process.doUpdate(vo);
				return SUCCESS;
			}
			
		}
		
		return "FAILD";
		
	}
	
//	@Override
//	public String doSave() {
//		try {
//			this.validation();
//			if (this.getContent().getId() == null || this.getContent().getId().equals(""))
//				process.doCreate(this.getContent());
//			else
//				process.doUpdate(this.getContent());
//			this.addActionMessage("{*[Save_Success]*}");
//			return SUCCESS;
//		} catch (Exception e) {
//			this.addFieldError("1", e.getMessage());
//			return INPUT;
//		}
//	}
	
	/** 保存并新建 */
//	public String doSaveAndNew() {
//		try {
//			this.validation();
//			if (this.getContent().getId() == null || this.getContent().getId().equals(""))
//				process.doCreate(this.getContent());
//			else
//				process.doUpdate(this.getContent());
//			setContent(new UserDefined());
//			this.addActionMessage("{*[Save_Success]*}");
//			return SUCCESS;
//		} catch (Exception e) {
//			// Catch the exception and return the error message.
//			this.addFieldError("1", e.getMessage());
//			return INPUT;
//		}
//	}
	
}
