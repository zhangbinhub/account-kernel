package OLink.bpm.core.email.email.action;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.constans.Web;
import OLink.bpm.core.email.attachment.ejb.Attachment;
import OLink.bpm.core.email.attachment.ejb.AttachmentProcess;
import OLink.bpm.core.email.folder.ejb.EmailFolder;
import OLink.bpm.core.email.folder.ejb.EmailFolderProcess;
import OLink.bpm.core.email.util.*;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.http.ResponseUtil;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.base.ejb.IDesignTimeProcess;
import OLink.bpm.core.email.email.ejb.Email;
import OLink.bpm.core.email.email.ejb.EmailProcess;
import OLink.bpm.core.email.folder.action.EmailFolderHelper;
import OLink.bpm.core.email.util.Constants;
import OLink.bpm.core.email.util.EmailConfig;
import OLink.bpm.core.email.util.EmailProcessUtil;
import org.apache.log4j.Logger;

import OLink.bpm.core.email.email.ejb.EmailUser;

import com.opensymphony.webwork.ServletActionContext;

public class EmailAction extends BaseAction<Email> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(EmailAction.class);
	
	private String[] _attids = null;
	
	/**
	 * 默认构造方法
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public EmailAction() throws Exception {
		super(ProcessFactory.createProcess(EmailProcess.class), new Email());
	}

	@Override
	public String doList() {
		try {
			setCurrentEmailProcess();
			ParamsTable params = getParams();
			EmailUser user = getEmailUser();
			String folderid = params.getParameterAsString("folderid");
			EmailFolder folder = null;
			EmailFolderProcess folderProcess = getEmailFolderProcess();
			if (Utility.isBlank(folderid)) {
				folder = folderProcess.getEmailFolderByOwnerId(Constants.DEFAULT_FOLDER_INBOX, Constants.SYSTEM_FOLDER_ID);
				folderid = folder.getId();
			} else {
				folder = folderProcess.getEmailFolderById(folderid);
			}
			if (folder == null) {
				throw new Exception(getMultiLanguage("core.email.folder.null"));
			}
			ServletActionContext.getRequest().setAttribute("folder", folder);
			setDatas(((EmailProcess)process).getEmailsByFolderUser(folderid, params, user));
			String type = params.getParameterAsString("type");
			if (!Utility.isBlank(type) && type.equals("0")) {
				return "main";
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
			return ERROR;
		}
		return SUCCESS;
	}
	
	@Override
	public String doView() {
		try {
			setCurrentEmailProcess();
			ParamsTable params = getParams();
			String id = params.getParameterAsString("id");
			String folderid = params.getParameterAsString("folderid");
			EmailFolderProcess folderProcess = getEmailFolderProcess();
			EmailFolder folder = folderProcess.getEmailFolderById(folderid);
			if (folder != null) {
				Email email = ((EmailProcess)process).getEmailByID(id, folder);
				if (email != null) {
					setContent(email);
					ServletActionContext.getRequest().setAttribute("email", email);
					if (Constants.DEFAULT_FOLDER_DRAFTS.equals(email.getEmailFolder().getName())) {
						AttachmentProcess process = (AttachmentProcess) ProcessFactory.createProcess(AttachmentProcess.class);
						ParamsTable table = new ParamsTable();
						if (!EmailConfig.isInternalEmail()) {
							table.setParameter("sm_emailid", email.getId());
							Collection<Attachment> attachments = process.doSimpleQuery(table);
							email.getEmailBody().setAttachments(toSet(attachments));
						}
						return "drafts";
					}
					if (!email.isRead()) {
						email.setRead(true);
						email.setReadDate(new Date());
						process.doUpdate(email);
					}
					return SUCCESS;
				}
			}
			ServletActionContext.getRequest().setAttribute(ERROR, getMultiLanguage("page.obj.dofailure"));
			return ERROR;
		} catch (Exception e) {
			LOG.warn(e);
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}
	
	@Override
	public String doNew() {
		try {
			setCurrentEmailProcess();
			ParamsTable params = getParams();
			String type = params.getParameterAsString("type");
			String emailid = params.getParameterAsString("id");
			String folderid = params.getParameterAsString("folderid");
			EmailFolderProcess folderProcess = getEmailFolderProcess();
			EmailFolder folder = folderProcess.getEmailFolderById(folderid);
			if (!Utility.isBlank(type) && folder != null) {
				Email email = ((EmailProcess)process).getEmailByID(emailid, folder);
				setEmailContent(type, email);
			}
		} catch (Exception e) {
			LOG.debug(e);
		}
		return SUCCESS;
	}
	
	@Override
	public String doSave() {
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			setCurrentEmailProcess();
			Email email = (Email) getContent();
			EmailUser user = getEmailUser();
			EmailFolderProcess folderProcess = getEmailFolderProcess();
			EmailFolder folder = folderProcess.getEmailFolderById(email.getEmailFolder().getId());
			if (folder == null) {
				folder = EmailFolderHelper.createEmptyEmailFolder();
			}
			if (email.getEmailBody() == null) {
				throw new Exception();
			}
			this.addAttachmentsByIds(email);
			boolean sentBox = false;
			if (folder.getName().equals(Constants.DEFAULT_FOLDER_DRAFTS)) {
				email.setEmailFolder(folder);
				email.setEmailUser(user);
				((EmailProcess)process).doSaveEmail(email, folder);
				String text = getMultiLanguage("core.email.save.drafts.success");
				ResponseUtil.setTextToResponse(response, "INFO*" + Utility.getDateToString() + " " + text + "！*" + email.getId() + "*" + email.getEmailBody().getId());
				return null;
			} else if (folder.getName().equals(Constants.DEFAULT_FOLDER_SENT)) {
				//if (!Utility.isBlank(email.getId())) {
					//Email temp = (Email) ((EmailProcess)process).doView(email.getId());
					//if (!folder.getName().equals(temp.getEmailFolder().getName())) {
						//((EmailProcess)process).doUpdate(email);
					//}
				//}
				sentBox = true;
			}
			if (!checkEmailAddress(email)) {
				throw new Exception("Email address error");
			}
			email.setEmailFolder(folder);
			((EmailProcess)process).sendEmail(email, user, sentBox);
			
			//this.saveAttachmentsByIds(email.getEmailBody());
			return SUCCESS;
		} catch (Exception e) {
			LOG.warn(e);
			//String text = getMultiLanguage("core.email.save.sent.error");
			//ResponseUtil.setTextToResponse(response, "ERROR*" + Utility.getDateToString() + " " + text);
		}
		ServletActionContext.getRequest().setAttribute(ERROR, getMultiLanguage("core.email.save.sent.error"));
		return ERROR;
	}
	
	@Override
	public String doDelete() {
		//HttpServletResponse response = ServletActionContext.getResponse();
		try {
			setCurrentEmailProcess();
			ParamsTable params = getParams();
			String folderid = params.getParameterAsString("folderid");
			EmailFolderProcess folderProcess = getEmailFolderProcess();
			EmailFolder folder = folderProcess.getEmailFolderById(folderid);
			if (_selects != null && folder != null) {
				((EmailProcess)process).doRemoveByFolder(_selects, folder);
			}
			//ResponseUtil.setTextToResponse(response, "邮件删除成功！");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.warn(e);
		}
		//ResponseUtil.setTextToResponse(response, "邮件删除失败！");
		return SUCCESS;
	}
	
	public String doMoveTo() {
		try {
			setCurrentEmailProcess();
			ParamsTable params = getParams();
			HttpServletResponse response = ServletActionContext.getResponse();
			String currFolderid = params.getParameterAsString("folderid");
			String folderid = params.getParameterAsString("toid");
			EmailFolderProcess folderProcess = getEmailFolderProcess();
			EmailFolder folder = folderProcess.getEmailFolderById(folderid);
			EmailFolder currFolder = folderProcess.getEmailFolderById(currFolderid);
			if (folder == null || currFolder == null) {
				ResponseUtil.setTextToResponse(response, getMultiLanguage("core.email.move.error"));
				return null;
			} else {
				((EmailProcess)process).doMoveTo(get_selects(), currFolder, folder);
			}
		} catch (Exception e) {
			LOG.warn(e);
		}
		return SUCCESS;
	}
	
	public String doMark() {
		try {
			setCurrentEmailProcess();
			String mark = getParams().getParameterAsString("mark");
			String folderid = getParams().getParameterAsString("folderid");
			EmailFolder folder = new EmailFolder();
			folder.setId(folderid);
			doMarkOperation(Integer.parseInt(mark), folder);
		} catch (Exception e) {
			HttpServletResponse response = ServletActionContext.getResponse();
			ResponseUtil.setTextToResponse(response, getMultiLanguage("core.email.mark.error"));
			return null;
		}
		return SUCCESS;
	}
	
	private void doMarkOperation(int mark, EmailFolder folder) throws Exception {
		switch (mark) {
		case 0: // 未读
			((EmailProcess)process).doUpdateMarkRead(_selects, false, folder);
			break;
			
		case 1: // 已读
			((EmailProcess)process).doUpdateMarkRead(_selects, true, folder);
			break;
		}
	}
	
	public String doToRecy() {
		//HttpServletResponse response = ServletActionContext.getResponse();
		try {
			setCurrentEmailProcess();
			ParamsTable params = getParams();
			String folderid = params.getParameterAsString("folderid");
			if (!Utility.isBlank(folderid)) {
				((EmailProcess)process).doToRecy(_selects, folderid);
				//ResponseUtil.setTextToResponse(response, "邮件删除成功！");
			}
		} catch (Exception e) {
			LOG.warn(e);
		}
		//ResponseUtil.setTextToResponse(response, "删除失败！");
		return SUCCESS;
	}
	
	public String doGetEmailContent() {
		try {
			ParamsTable params = getParams();
			//String folderid = params.getParameterAsString("folderid");
			String emailid = params.getParameterAsString("id");
			Email email = (Email) process.doView(emailid);
			ServletActionContext.getRequest().setAttribute("emailContent", getEmailContent(email));
		} catch (Exception e) {
			LOG.warn(e);
		}
		return SUCCESS;
	}
	
	private String getEmailContent(Email email) {
		StringBuffer html = new StringBuffer();
		html.append(email.getEmailBody().getContent()).append("<br></br>");
		return html.toString();
	}
	
	private void setEmailContent(String type, Email email) {
		StringBuffer html = new StringBuffer();
		html.append("<br></br>");
		html.append("<div align=\"left\">");
		
		Email content = (Email) getContent();
		content.getEmailBody().setCc(email.getEmailBody().getCc());
		content.getEmailBody().setTo(email.getEmailBody().getFrom());
		if (type.equals("reply")) {
			content.getEmailBody().setSubject(getMultiLanguage("Reply") + ": " + email.getEmailBody().getSubject());
			//content.getEmailBody().setTo(email.getEmailBody().getFrom());
			//html.append("- - - - - - - - " + getMultiLanguage("core.email.reply.info") + " - - - - - - - -");
			html.append("<div style='border:none; border-top:solid #B5C4DF 1.0pt; padding:3.0pt 0cm 0cm 0cm' />");
			content.setReply(true);
		} else if (type.equals("forward")) {
			content.getEmailBody().setSubject(getMultiLanguage("core.email.transport") + ": " + email.getEmailBody().getSubject());
			//content.getEmailBody().setTo(email.getEmailBody().getFrom());
			//html.append("- - - - - - - - " + getMultiLanguage("core.email.transport.info") + " - - - - - - - -");
			html.append("<div style='border:none; border-top:solid #B5C4DF 1.0pt; padding:3.0pt 0cm 0cm 0cm' />");
			content.setForward(true);
		}
		
		html.append("<p style=\"line-height: 22px;\">");
		html.append("<b>").append(getMultiLanguage("core.email.from") + ":</b> ").append(email.getEmailBody().getFrom()).append("<br>");
		html.append("<b>").append(getMultiLanguage("SendDate") + ":</b> ").append(Utility.getDateToString(email.getEmailBody().getSendDate())).append("<br>");
		html.append("<b>").append(getMultiLanguage("core.email.to") + ":</b> ").append(email.getEmailBody().getTo()).append("</br>");
		html.append("<b>").append(getMultiLanguage("Subject") + ":</b> ").append(email.getEmailBody().getSubject()).append("</p>");
		html.append("<p>").append(email.getEmailBody().getContent()).append("</p>");
		html.append("</div>");
		content.getEmailBody().setContent(html.toString());
		setContent(content);
	}
	
	@Override
	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}
	
	public EmailUser getEmailUser() throws Exception {
		EmailUser emailUser = getUser().getEmailUser();
		if (emailUser == null) {
			throw new Exception("Email user can't login!");
		}
		return emailUser;
	}
	
	/**
	 * 设置当前业务处理Bean
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private void setCurrentEmailProcess() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		this.process = (IDesignTimeProcess<Email>) EmailProcessUtil.createProcess(EmailProcess.class, request);
	}
	
	private EmailFolderProcess getEmailFolderProcess() throws Exception {
		HttpServletRequest request = ServletActionContext.getRequest();
		return (EmailFolderProcess) EmailProcessUtil.createProcess(EmailFolderProcess.class, request);
	}

	/**
	 * @param attids the _attids to set
	 */
	public void set_attids(String[] attids) {
		_attids = attids;
	}
	
	private void addAttachmentsByIds(Email email) {
		try {
			AttachmentProcess process = (AttachmentProcess) ProcessFactory.createProcess(AttachmentProcess.class);
			if (_attids != null) 
				for (int i = 0; i < _attids.length; i++) {
					Attachment temp = (Attachment) process.doView(_attids[i]);
					if (temp == null) {
						continue;
					}
					//if (EmailConfig.isInternalEmail()) {
						email.getEmailBody().addAttachment(temp);
					//}
				}
		} catch (Exception e) {
			LOG.warn(e);
		}
	}
	
	private Set<Attachment> toSet(Collection<Attachment> attachments) {
		Set<Attachment> result = new HashSet<Attachment>();
		for (Attachment att : attachments) {
			result.add(att);
		}
		return result;
	}
	
	private boolean checkEmailAddress(Email email) {
		if (email.getEmailBody().getTo() == null) {
			return false;
		}
		String strings[] = email.getEmailBody().getTo().split(";");
		if (strings.length > 1) {
			for (int i = 0; i < strings.length; i++) {
				if (!Utility.checkEmailAddress(strings[i])) {
					return false;
				}
			}
		} else {
			strings = email.getEmailBody().getTo().split(",");
			for (int i = 0; i < strings.length; i++) {
				if (!Utility.checkEmailAddress(strings[i])) {
					return false;
				}
			}
		}
		return true;
	}
	
}
