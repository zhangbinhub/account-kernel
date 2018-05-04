package OLink.bpm.core.email.folder.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import OLink.bpm.constans.Web;
import OLink.bpm.core.email.email.ejb.EmailUser;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcess;
import OLink.bpm.core.email.util.EmailProcessUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.http.ResponseUtil;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.ejb.IDesignTimeProcess;

import com.opensymphony.webwork.ServletActionContext;

public class EmailFolderAction extends BaseAction<EmailFolder> {

	private static final long serialVersionUID = 3625782371300730725L;

	/**
	 * 默认构造方法
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public EmailFolderAction() throws Exception {
		super(ProcessFactory.createProcess(EmailFolderProcess.class), new EmailFolder());
	}
	
	@Override
	public String doList() {
		try {
			setCurrentEmailFolderProcess();
			EmailUser user = getEmailUser();
			ServletActionContext.getRequest().setAttribute("systemFolder", ((EmailFolderProcess)process).getSystemEmailFolders());
			setDatas(((EmailFolderProcess)process).getPersonalEmailFolders(user, getParams()));
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String doView() {
		try {
			setCurrentEmailFolderProcess();
			String folderid = getParams().getParameterAsString("id");
			if (StringUtil.isBlank(folderid)) {
				return SUCCESS;
			}
			EmailFolder folder = ((EmailFolderProcess)process).getEmailFolderById(folderid);
			if (folder == null) {
				folder = new EmailFolder();
			}
			setContent(folder);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String doSave() {
		try {
			setCurrentEmailFolderProcess();
			EmailFolder folder = (EmailFolder) getContent();
			if (folder == null || StringUtil.isBlank(folder.getName())) {
				throw new Exception(getMultiLanguage("core.email.folder.cannot.null"));
			}
			EmailFolder temp = ((EmailFolderProcess)process).getEmailFolderByOwnerId(folder.getName(), getEmailUser().getId());
			if (temp != null && !temp.getId().equals(folder.getId())) {
				throw new Exception(getMultiLanguage("core.email.folder.created"));
			}
			folder.setDisplayName(folder.getName());
			if (StringUtil.isBlank(folder.getId())) {
				folder.setOwnerId(getEmailUser().getId());
				folder.setCreateDate(new Date());
				process.doCreate(folder);
			} else {
				process.doUpdate(folder);
			}
			ResponseUtil.setTextToResponse(ServletActionContext.getResponse(), "INFO::" + getMultiLanguage("core.email.folder.saved"));
			return null;
		} catch (Exception e) {
			ResponseUtil.setTextToResponse(ServletActionContext.getResponse(), "ERROR::" + e.getMessage());
			return null;
		}
	}
	
	@Override
	public String doDelete() {
		try {
			setCurrentEmailFolderProcess();
			String folderid = getParams().getParameterAsString("id");
			EmailFolder folder = ((EmailFolderProcess)process).getEmailFolderById(folderid);
			if (folder != null) {
				((EmailFolderProcess)process).doRemoveEmailFolder(folder, getEmailUser());
			}
		} catch (Exception e) {
			e.printStackTrace();
			//addFieldError("1", e.getMessage());
			//return INPUT;
		}
		return SUCCESS;
	}
	
	public String doPersonalFolderHtml() {
		String returnString = "";
		try {
			returnString = EmailFolderHelper.toPersonalFolderHtml(getUser());
		} catch (Exception e) {
			e.printStackTrace();
		}
		ResponseUtil.setTextToResponse(ServletActionContext.getResponse(), returnString);
		return null;
	}
	
	public EmailUser getEmailUser() throws Exception {
		EmailUser emailUser = getUser().getEmailUser();
		if (emailUser == null) {
			throw new Exception(getMultiLanguage("core.email.user.error"));
		}
		return emailUser;
	}
	
	/**
	 * 设置当前业务处理Bean
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void setCurrentEmailFolderProcess() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		this.process = (IDesignTimeProcess<EmailFolder>) EmailProcessUtil.createProcess(EmailFolderProcess.class, request);
	}
	
	@Override
	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

}
