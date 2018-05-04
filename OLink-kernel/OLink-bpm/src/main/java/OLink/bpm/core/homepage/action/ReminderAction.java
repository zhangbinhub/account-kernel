package OLink.bpm.core.homepage.action;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.homepage.ejb.HomePage;
import OLink.bpm.core.homepage.ejb.HomePageProcess;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.core.homepage.ejb.ReminderProcess;
import OLink.bpm.core.user.ejb.UserDefinedProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.user.ejb.UserDefined;

import com.opensymphony.webwork.ServletActionContext;

public class ReminderAction extends BaseAction<Reminder> {
	
	protected String editPersonalId;
	protected String templateStyle;
	protected String templateElement;
	private UserDefined userDefined;
	/**
	 * 
	 */
	private static final long serialVersionUID = 3291456528719270799L;

	public String getHomePageId() {
		String rtn = "";
		if (getContent() != null && ((Reminder)getContent()).getHomepage() != null) {
			rtn = ((Reminder)getContent()).getHomepage().getId();
		}
		return rtn;
	}

	public void setHomePageId(String homePageId) throws Exception {
		HomePageProcess process = getProcess();
		HomePage homepage = (HomePage) process.doView(homePageId);
		((Reminder)getContent()).setHomepage(homepage);
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public ReminderAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(ReminderProcess.class), new Reminder());
	}

	/**
	 * 保存
	 */
	public String doSave() {
		try {

			//content.setHomepage(homepage);
			return super.doSave();
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}
	
	public String doRemindList(){
		String html ="";
		try {
			ParamsTable params=getParams();
			String applicationid=(String)params.getParameter("application");
			String homepageId=(String)params.getParameter("homepage");
			String _currpage=(String)params.getParameter("_currpage");
			String _pagelines=(String)params.getParameter("_pagelines");
			DataPackage<Reminder> datas = ((ReminderProcess) process).getReminderByHomepage(params, homepageId, applicationid, _currpage, _pagelines);
			ReminderHelper rh=new ReminderHelper();
			html=rh.doRemindListToHtml(datas.datas);
			if (!html.equals("")) {
				ServletActionContext.getResponse().setContentType("text/html;charset=UTF-8");
				ServletActionContext.getResponse().getWriter().write(html.toString());
			}
		} catch (Exception e) {
			return "";
		}
		return html;
	}
	
	/**
	 * 删除
	 */
	public String doDelete(){
		boolean flag = false;
		try {
			HomePageProcess hpp = (HomePageProcess)ProcessFactory.createProcess(HomePageProcess.class);
			try {
				DataPackage<HomePage> datas = hpp.doQuery(getParams());
				if(datas.rowCount>0){
					for(Iterator<HomePage> ite = datas.datas.iterator();ite.hasNext();){
						HomePage hp= ite.next();
						if(hp.getReminders().size()>0){
							for(Iterator<Reminder> ite1 = hp.getReminders().iterator();ite1.hasNext();){
								Reminder reminder = ite1.next();
							    if(reminder!=null){
							    	for(int i=0;i<this.get_selects().length;i++){
							    		if(reminder.getId().equals(this.get_selects()[i])){
							    			flag=true;
							    			this.addFieldError("1", "{*[Home_Page]*}{*[Relation]*}{*[Reminder]*}");
							    			break;
							    		}
							    	}
							    }
							    if(flag){
							    	break;
							    }
							}
						}
						if(flag){
							break;
						}
					}
				}
			} catch (Exception e) {
				this.addFieldError("1", e.getMessage());
				flag =true;
			}
		} catch (ClassNotFoundException e) {
			this.addFieldError("1", e.getMessage());
			flag =true;
		}
		if(!flag){
			return super.doDelete();
		}else{
			return INPUT;
		}
	}

	public String doReminderlist() throws Exception {
		Reminder content = (Reminder) getContent();
		ReminderProcess process = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
		ParamsTable params = getParams();
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");
		String applicationid = params.getParameterAsString("application");
		String homepageId = params.getParameterAsString("s_homepage");
		setDatas(process.getReminderByHomepage(new ParamsTable(), homepageId, applicationid, _currpage, _pagelines));
		setContent(content);
		return SUCCESS;
	}

	public String doAddReminder() throws Exception {
		Reminder content = (Reminder) getContent();
		ReminderProcess process = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
		ParamsTable params = getParams();
		String _currpage = params.getParameterAsString("_currpage");
		String _pagelines = params.getParameterAsString("_pagelines");
		String applicationid = params.getParameterAsString("application");
		setDatas(process.getReminderByApplication(new ParamsTable(), applicationid, _currpage, _pagelines));
		setContent(content);
		return SUCCESS;
	}
	

	/**
	 * 修改待办提醒信息
	 * 
	 * @return 成功处理返回"SUCCESS",否则提示失败
	 */
	public String doEditReminder() {
		String id = this.editPersonalId;
		String appid = this.application;
		try {
			if(id != null && !id.equals("")){
				UserDefinedProcess udprocss=(UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
				String userDefinedId = id  + "_" + appid;
				try {
					userDefined = (UserDefined) udprocss.doView(userDefinedId);
				} catch (Exception e) {
					e.printStackTrace();
					return INPUT;
				}
				if(userDefined != null){
					this.setTemplateStyle(userDefined.getTemplateStyle());
					this.setTemplateElement(userDefined.getTemplateElement());
				}
			}
			return SUCCESS;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return INPUT;
		}
	}
	
	public String doSelectTemplate() throws Exception{
		return SUCCESS;
	}

	public String doSaveElement() throws Exception{
		try {
//			String homePageId = this.getEditPersonalId();
//			String application = this.application;
//			String templateStyle = this.getTemplateStyle();
//			String tempElem = this.getTemplateElement();
//			UserDefined userDefined = null;
//
//			UserDefinedProcess udprocss=(UserDefinedProcess) ProcessFactory.createProcess(UserDefinedProcess.class);
			//UserDefined userDefined = new UserDefined();
			
			
//			if(this.getParams()==null){
//				params = new ParamsTable();
//			}
//			params.setParameter("t_applicationid", application);
//			params.setParameter("t_homepage", homePageId);
//			DataPackage<UserDefined> dataPackage=udprocss.doQuery(params);
//			if(dataPackage.rowCount>0){
//				for(Iterator<UserDefined> ite1 = dataPackage.datas.iterator();ite1.hasNext();){
//					userDefined = (UserDefined)ite1.next();
//					
//					userDefined.setApplicationid(application);
//					userDefined.setHomepage(homePageId);
//					userDefined.setTemplateStyle(tempStyle);
//					userDefined.setTemplateElement(tempElem);
//					
//					udprocss.doUpdate(userDefined);
//				}
//			}else{
//				this.setApplication(application);
//				this.setHomePageId(homePageId);
//				this.setTemplateStyle(templateStyle);
//				this.setTemplateElement(templateElement);
//				userDefined.setTemplateElement(tempElem);
//				
//				udprocss.doCreate(userDefined);
//				
//			}
//			if(udprocss.doView(id) == null){
//				udprocss.doCreate(userDefined);
//			}else{
//				udprocss.doUpdate(userDefined);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			this.addFieldError("1", "{*[Save]*}{*[Error]*}");
			return INPUT;
		}
		this.addActionMessage("{*[Save_Success]*}");
		return SUCCESS;
		
	}
	
	public String confirm() throws Exception {
		try {
			Collection<Reminder> reminders = new HashSet<Reminder>();
			ParamsTable params = getParams();
			String ids[] = params.getParameterAsArray("_selects");
			if (ids == null || ids.length < 0) {
				throw new Exception("{*[page.records.notChoose]*}");
			} else {
				String homepageid = params.getParameterAsString("s_homepage");
				HomePageProcess process = getProcess();
				HomePage homepage = (HomePage) process.doView(homepageid);
				ReminderProcess remprocess = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
				for (int i = 0; i < ids.length; i++) {
					Reminder vo = (Reminder) remprocess.doView(ids[i]);
					if (vo != null) {
						reminders.add(vo);
						vo.setHomepage(homepage);
						remprocess.doUpdate(vo);
					}
				}
				homepage.setReminders(reminders);
				process.doUpdate(homepage);
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	public String doRemoveReminder() throws Exception {
		try {
			ParamsTable params = getParams();
			String ids[] = params.getParameterAsArray("_selects");
			ReminderProcess process = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
			for (int i = 0; i < ids.length; i++) {
				Reminder vo = (Reminder) process.doView(ids[i]);
				if (vo != null) {
					vo.setHomepage(null);
					process.doUpdate(vo);
				}
			}
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
		return SUCCESS;
	}

	public String get_styles() throws Exception {
		Reminder rem = (Reminder) this.getContent();
		return rem.getStyle();
	}

	public void set_styles(String _styles) throws Exception {
		if (_styles != null) {
			Reminder rem = (Reminder) this.getContent();
			rem.setStyle(_styles);
		}
	}

	public HomePageProcess getProcess() throws Exception {
		return (HomePageProcess) ProcessFactory.createProcess(HomePageProcess.class);
	}
	
	/** 保存并新建 */
	public String doSaveAndNew() {
		try {
			if (this.getContent().getId() == null || this.getContent().getId().equals(""))
				process.doCreate(this.getContent());
			else
				process.doUpdate(this.getContent());
			setContent(new Reminder());
			this.addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
		} catch (Exception e) {
			// Catch the exception and return the error message.
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String getEditPersonalId() {
		return editPersonalId;
	}

	public void setEditPersonalId(String editPersonalId) {
		this.editPersonalId = editPersonalId;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
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

	public UserDefined getUserDefined() {
		return userDefined;
	}

	public void setUserDefined(UserDefined userDefined) {
		this.userDefined = userDefined;
	}
	
	
	
}
