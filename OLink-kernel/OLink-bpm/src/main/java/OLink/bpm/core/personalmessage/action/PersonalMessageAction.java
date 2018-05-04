package OLink.bpm.core.personalmessage.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.constans.Web;
import OLink.bpm.core.personalmessage.ejb.MessageBody;
import OLink.bpm.core.personalmessage.ejb.PersonalMessageProcess;
import OLink.bpm.core.personalmessage.ejb.PersonalMessageVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.http.ResponseUtil;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.personalmessage.ejb.MessageBodyProcess;
import OLink.bpm.core.user.action.WebUser;
import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;

/**
 * @SuppressWarnings 不支持泛型
 * @author Administrator
 * 
 */
@SuppressWarnings("unchecked")
public class PersonalMessageAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2199658886883473982L;

	private String operation = "";

	private static final Logger LOG = Logger
			.getLogger(PersonalMessageAction.class);

	public PersonalMessageAction() throws Exception {
		super(ProcessFactory.createProcess(PersonalMessageProcess.class),
				new PersonalMessageVO());
	}

	public String doSave() {
		try {
			PersonalMessageVO pmVO = (PersonalMessageVO) getContent();
			// pmVO.setBody(messageBody);
			WebUser user = getWebUser();
			ParamsTable param = getParams();

			if (pmVO != null) {
				if (user != null && pmVO.getBody() != null) {
					pmVO.setSenderId(user.getId());
					pmVO.getBody().setDomainid(user.getDomainid());
					pmVO.setDomainid(user.getDomainid());
				}
				if (StringUtil.isBlank(pmVO.getSenderId())) {
					this.addFieldError("1", "{*[could.not.find.sender]*}");
					return INPUT;
				}
				String ids = param.getParameterAsString("receiverid");
				// Collection receiverList = new ArrayList();
				if (ids != null) {
					pmVO.setReceiverId(ids);
					String[] idArray = ids.split(";");// 多个用户用“;”分隔
					((PersonalMessageProcess) process).doCreateByUserIds(
							idArray, pmVO);
					return SUCCESS;
				} else {
					this.addFieldError("1", "{*[could.not.find.sender]*}");
					return INPUT;
				}
			} else {
				this.addFieldError("1", "{*[could.not.find.sender]*}");
				return INPUT;
			}

		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	private WebUser getWebUser() throws Exception {
		return (WebUser) getContext().getSession().get(
				Web.SESSION_ATTRIBUTE_FRONT_USER);
	}

	public String doInbox() throws Exception {
		ParamsTable params = getParams();
		params.setParameter("_orderby", "sendDate");
		params.setParameter("_desc", "desc");
		params.removeParameter("domain");
		Map<String, String> request = (Map<String, String>) ActionContext
				.getContext().get("request");
		request.put("operation", "inbox");
		request.put("isInbox", "true");
		WebUser user = getWebUser();
		if (user != null) {
			setDatas(((PersonalMessageProcess) process).doInbox(user.getId(),
					params));
		}
		return SUCCESS;
	}

	public String doSearchInbox() throws Exception {
		ParamsTable params = getParams();
		Map<String, String> request = (Map<String, String>) ActionContext
				.getContext().get("request");
		request.put("operation", "inbox");
		request.put("isInbox", "true");

		// String _title = params.getParameterAsString("_title");
		// params.setParameter("s_body.title", _title);
		String msg_id = params.getParameterAsString("msgId");

		WebUser user = getWebUser();
		if (user != null) {
			PersonalMessageVO pmVO = (PersonalMessageVO) process
					.doView(msg_id);
			DataPackage<PersonalMessageVO> newDts = new DataPackage<PersonalMessageVO>();
			Collection<PersonalMessageVO> newPms = new ArrayList<PersonalMessageVO>();
			// Collection<PersonalMessageVO> pms = dts.getDatas();
			// for (java.util.Iterator<PersonalMessageVO> it = pms.iterator();
			// it
			// .hasNext();) {
			// PersonalMessageVO pmVO = it.next();
			// if (pmVO.isInbox() && _title.equals(pmVO.getBody().getTitle())) {
			// newPms.add(pmVO);
			// newDts.setDatas(newPms);
			// }
			if (pmVO != null) {
				newPms.add(pmVO);
				newDts.setDatas(newPms);
			}
			setDatas(newDts);
		}

		// }
		return SUCCESS;
	}

	public String doOutbox() throws Exception {
		ParamsTable params = getParams();
		params.setParameter("_orderby", "sendDate");
		params.setParameter("_desc", "desc");
		params.setParameter("application", "");
		Map<String, String> request = (Map<String, String>) ActionContext
				.getContext().get("request");
		request.put("operation", "outbox");
		request.put("isOutbox", "true");
		WebUser user = getWebUser();
		if (user != null) {
			setDatas(((PersonalMessageProcess) process).doOutbox(user.getId(),
					params));
		}
		return SUCCESS;
	}

	public String doTrash() throws Exception {
		ParamsTable params = getParams();
		params.setParameter("_orderby", "sendDate");
		params.setParameter("_desc", "desc");
		Map<String, String> request = (Map<String, String>) ActionContext
				.getContext().get("request");
		request.put("operation", "trash");
		request.put("isTrash", "true");
		WebUser user = getWebUser();
		if (user != null) {
			setDatas(((PersonalMessageProcess) process).doTrash(user.getId(),
					params));
		}
		return SUCCESS;
	}

	public String doToTrash() throws Exception {
		if (_selects != null) {
			// String tempString = getParams().getParameterAsString("isOutbox");
			((PersonalMessageProcess) process).doSendToTrash(_selects);
		}
		return SUCCESS;
	}

	public String doRead() throws Exception {
		Map<String, String> request = (Map<String, String>) ActionContext
				.getContext().get("request");
		request.put("operation", "read");
		request.put("isRead", "true");
		WebUser user = getWebUser();
		if (user != null) {
			setDatas(((PersonalMessageProcess) process).doNoRead(user.getId(),
					getParams()));
		}
		return SUCCESS;
	}

	public String doShow() throws Exception {
		ParamsTable params = getParams();
		String msgid = params.getParameterAsString("id");
		String isRead = params.getParameterAsString("read");

		if (!StringUtil.isBlank(msgid)) {
			if (!StringUtil.isBlank(isRead)) {
				PersonalMessageVO pmVO = (PersonalMessageVO) process
						.doView(msgid);
				if (isRead.equalsIgnoreCase("false")) {
					process.doUpdate(pmVO);
				}
				setContent(pmVO);
			} else {
				MessageBodyProcess bodyProcess = (MessageBodyProcess) ProcessFactory
						.createProcess(MessageBodyProcess.class);
				MessageBody body = (MessageBody) bodyProcess.doView(msgid);
				((PersonalMessageVO) getContent()).setBody(body);
			}

		}

		return SUCCESS;
	}

	public String doReply() throws Exception {
		return this.doSave();
	}

	@Override
	public String doDelete() {
		ParamsTable params = getParams();
		try {
			String inbox = params.getParameterAsString("isInbox");
			String outbox = params.getParameterAsString("isOutbox");
			// String read = params.getParameterAsString("isRead");
			if (inbox != null && inbox.equals("true")) {
				if (_selects != null && _selects.length > 0) {
					process.doRemove(_selects);
					addActionMessage("{*[delete.successful]*}");
				}
				return doInbox();
			} else if (outbox != null && outbox.equals("true")) {
				if (_selects != null && _selects.length > 0) {
					MessageBodyProcess bodyProcess = (MessageBodyProcess) ProcessFactory
							.createProcess(MessageBodyProcess.class);
					bodyProcess.doRemove(_selects);
					addActionMessage("{*[delete.successful]*}");
				}
				return doOutbox();
			} else {
				if (_selects != null && _selects.length > 0) {
					process.doRemove(_selects);
					addActionMessage("{*[delete.successful]*}");
				}
				return doTrash();
			}
		} catch (Exception e) {
			addFieldError("", e.getMessage());
			return INPUT;
		}
	}

	public String doCount() {
		PersonalMessageHelper pmh = new PersonalMessageHelper();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			WebUser user = getWebUser();
			if (user != null) {
				ResponseUtil.setTextToResponse(response, pmh.countMessage(user
						.getId())
						+ "");
				return null;
			}
		} catch (Exception e) {
			LOG.debug(e);
		}
		ResponseUtil.setTextToResponse(response, "0");
		return null;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getApplication() {
		return null;
	}

}
