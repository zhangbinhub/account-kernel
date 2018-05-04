package OLink.bpm.core.multilanguage.action;

import javax.servlet.http.HttpSession;

import OLink.bpm.constans.Web;
import OLink.bpm.core.multilanguage.ejb.MultiLanguage;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.MultiLanguageProperty;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.multilanguage.ejb.MultiLanguageProcess;
import OLink.bpm.util.StringUtil;
import com.opensymphony.webwork.ServletActionContext;

public class MultiLanguageAction extends BaseAction<MultiLanguage> {
	
	private static final long serialVersionUID = 2948439801638083317L;
	
	private String domain;
	private int sm_type;

	public int getSm_type() {
		return sm_type;
	}

	public void setSm_type(int smType) {
		sm_type = smType;
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * getDefaultDAO
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public MultiLanguageAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(MultiLanguageProcess.class), new MultiLanguage());
	}
	
	public String doNew() {
		
		String application=(String)this.getParams().getParameter("id");
		this.getContent().setApplicationid(application);
		return SUCCESS;
	}

	/**
	 * 保存
	 */
	public String doSave(){
		
		try {
			String applicationid=getCurrentApplicationid();
			MultiLanguage tempMultiLanguage = (MultiLanguage) (this.getContent());
			
			if (tempMultiLanguage != null) {//!tempMultiLanguage.getId().equals("")
				tempMultiLanguage.setApplicationid(applicationid);
				boolean flag = false;
				MultiLanguage multiLanguage = ((MultiLanguageProcess) process).doView(tempMultiLanguage.getType(), tempMultiLanguage.getLabel(),tempMultiLanguage.getApplicationid());

				if (multiLanguage != null) {
					if (tempMultiLanguage.getId() == null || tempMultiLanguage.getId().trim().length() <= 0) {//判断新建不能重名
						this.addFieldError("1", "{*[LabelExist]*}");
						flag = true;
					} else if (tempMultiLanguage.getType()==multiLanguage.getType() && !tempMultiLanguage.getId().trim().equalsIgnoreCase(multiLanguage.getId())) {//修改不能重名
						this.addFieldError("1", "{*[LabelExist]*}");
						flag = true;
					}
				}
				if(!flag){
					if(StringUtil.isBlank(tempMultiLanguage.getId()))
						process.doCreate(tempMultiLanguage);
					else{
						MultiLanguage oldMl = null;
						if(!StringUtil.isBlank(tempMultiLanguage.getId()))
							oldMl = (MultiLanguage)process.doView(tempMultiLanguage.getId());
						if(oldMl != null)
							((MultiLanguageProcess)process).doRemove(oldMl.getType(), oldMl.getLabel());
						process.doUpdate(tempMultiLanguage);
					}

				}else{
					return INPUT;
				}
			}
				
			MultiLanguageProperty.putLanguage(applicationid, tempMultiLanguage);
			this.addActionMessage("{*[Save_Success]*}");
			return SUCCESS;
				
		}catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	/**
	 * 列表
	 * @return
	 */
	public String list() {
		String applicationid=getCurrentApplicationid();
		//getParams().setParameter("s_domainid",domain);
		//getParams().setParameter("s_applicationid",(String)this.getParams().getParameter("id"));
		getParams().setParameter("s_applicationid",applicationid);
		
		return super.doList();
	}

	/**
	 * @SuppressWarnings webwork不支持泛型
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String doChange() throws Exception {
		String languageType = this.getParams().getParameterAsString("language");
		int type = languageType == null || languageType.trim().length() <= 0 ? 1 : Integer.parseInt(languageType);
		String language = MultiLanguageProperty.getName(type);
		getContext().getSession().put(Web.SESSION_ATTRIBUTE_USERLANGUAGE, language);
		
		String debug = ServletActionContext.getRequest().getParameter("debug");
		
		if (!StringUtil.isBlank(debug))
			return "debug";
		
		return SUCCESS;
	}

	public String doLoadLanguage() throws Exception {
		MultiLanguageProperty.init();
		return SUCCESS;
	}

	/**
	 * 删除
	 */
	public String doDelete() {
		try {
			String applicationid=getCurrentApplicationid();
			getParams().setParameter("s_applicationid",applicationid);
			if (_selects != null)
				for (int i = 0; i < _selects.length; i++) {
					String id = _selects[i];
					try {
						MultiLanguage vo = (MultiLanguage) process.doView(id);
						if (vo != null){
							String prefix = MultiLanguageProperty.getName(vo.getType());
							MultiLanguageProperty.remove(applicationid, prefix + vo.getLabel());
						}
						process.doRemove(id);
					} catch (Exception e) {
						this.addFieldError("1", e.getMessage());
					}
				}
				if (getFieldErrors().size() > 0) {
					return INPUT;
				}
				addActionMessage("{*[delete.successful]*}");
				return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String getDomain() {
		if (domain != null && domain.trim().length() > 0) {
			return domain;
		} else {
			return (String) getContext().getSession().get(Web.SESSION_ATTRIBUTE_DOMAIN);
		}
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getCurrentApplicationid() {
		HttpSession session = ServletActionContext.getRequest().getSession();
		//currentApplication在点击软件doEdit()时被赋值
		String applicationid=(String) session.getAttribute("currentApplication");
		if (applicationid != null && applicationid.trim().length() > 0) {
			return applicationid;
		} else {
			return null;
		}
	}
}
