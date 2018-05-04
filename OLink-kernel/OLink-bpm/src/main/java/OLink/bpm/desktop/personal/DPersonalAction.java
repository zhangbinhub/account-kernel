package OLink.bpm.desktop.personal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.personalmessage.action.PersonalMessageHelper;
import OLink.bpm.core.personalmessage.ejb.MessageBody;
import OLink.bpm.core.shortmessage.submission.action.SubmitMessageHelper;
import OLink.bpm.core.xmpp.XMPPSender;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.core.xmpp.notification.SiteMessageIQ;
import OLink.bpm.constans.Web;
import OLink.bpm.core.personalmessage.ejb.PersonalMessageProcess;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageProcess;
import OLink.bpm.core.workcalendar.calendar.action.CalendarHelper;
import org.apache.log4j.Logger;
import org.jivesoftware.smack.packet.IQ.Type;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.personalmessage.ejb.PersonalMessageVO;
import OLink.bpm.core.table.constants.MobileConstant;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.notification.ejb.sendmode.SMSModeProxy;
import OLink.bpm.util.DateUtil;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;

import com.opensymphony.webwork.ServletActionContext;

/**
 * 
 * @author Tom
 * @SuppressWarnings DPersonalAction内容涉及多个实体，不支持泛型
 */
@SuppressWarnings("unchecked")
public class DPersonalAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = Logger.getLogger(DPersonalAction.class);

	public DPersonalAction() {
		super(null, null);
	}

	private String operation;
	private String msgType = "in";
	private UserVO uservo = new UserVO();
	private SubmitMessageVO sms = new SubmitMessageVO();
	private PersonalMessageVO msg = new PersonalMessageVO();

	public String doEdit() {
		try {
			String toXml = "";
			if (getOperation().equals("user")) {
				toXml = getUserXml();
			} else if (getOperation().equals("message")) {
				toXml = getMessageXml();
			} else if (getOperation().equals("sms")) {
				toXml = getSmsXml();
			}
			ServletActionContext.getRequest().setAttribute("toXml", toXml);
			// System.out.println("toXml--> " + toXml);
		} catch (Exception e) {
			addFieldError("SystemError", e.toString());
			LOG.warn(e);
			return ERROR;
		}
		return SUCCESS;
	}

	private String getSmsXml() throws Exception {
		StringBuffer sb = new StringBuffer();
		try {
			SubmitMessageProcess process = (SubmitMessageProcess) ProcessFactory
					.createProcess(SubmitMessageProcess.class);
			sms = (SubmitMessageVO) process.doView(sms.getId());
			if (sms == null) {
				sms = new SubmitMessageVO();
			}
			sb.append("<" + MobileConstant.TAG_SMSPANEL + " "
					+ MobileConstant.ATT_READONLY + "='" + sms.getSubmission()
					+ "'>");

			sb.append(
					"<" + MobileConstant.TAG_HIDDENFIELD + " "
							+ MobileConstant.ATT_NAME + "='sms.id' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(sms.getId()) + "'>").append(
					"</" + MobileConstant.TAG_HIDDENFIELD + ">");
			sb.append(
					"<" + MobileConstant.TAG_TEXTFIELD + " "
							+ MobileConstant.ATT_NAME + "='sms.receiver' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(getSmsReceiver()) + "'>").append(
					"</" + MobileConstant.TAG_TEXTFIELD + ">");
			sb.append(
					"<" + MobileConstant.TAG_RADIOFIELD + " "
							+ MobileConstant.ATT_NAME + "='sms.needReply' "
							+ MobileConstant.ATT_VALUE + "='"
							+ sms.isNeedReply() + "'>").append(
					"</" + MobileConstant.TAG_RADIOFIELD + ">");
			sb.append(
					"<" + MobileConstant.TAG_RADIOFIELD + " "
							+ MobileConstant.ATT_NAME + "='sms.mass' "
							+ MobileConstant.ATT_VALUE + "='" + sms.isMass()
							+ "'>").append(
					"</" + MobileConstant.TAG_RADIOFIELD + ">");
			sb.append(
					"<" + MobileConstant.TAG_TEXTFIELD + " "
							+ MobileConstant.ATT_NAME + "='sms.title' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(sms.getTitle()) + "'>").append(
					"</" + MobileConstant.TAG_TEXTFIELD + ">");
			sb.append("<" + MobileConstant.TAG_SELECTFIELD + " "
					+ MobileConstant.ATT_NAME + "='_smgType' "
					+ MobileConstant.ATT_VALUE + "='" + get_msgType() + "'>");
			SubmitMessageHelper sh = new SubmitMessageHelper();
			Map<String, String> map = sh.getContentTypes();
			for (Iterator<Entry<String, String>> it = map.entrySet().iterator(); it
					.hasNext();) {
				Entry<String, String> entry = it.next();
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(
						"<" + MobileConstant.TAG_OPTION + " "
								+ MobileConstant.ATT_VALUE + "='" + key + "'>")
						.append(value).append(
								"</" + MobileConstant.TAG_OPTION + ">");
			}
			sb.append("</" + MobileConstant.TAG_SELECTFIELD + ">");
			sb.append(
					"<" + MobileConstant.TAG_TEXTAREAFIELD + " "
							+ MobileConstant.ATT_NAME + "='sms.content' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(sms.getContent()) + "'>").append(
					"</" + MobileConstant.TAG_TEXTAREAFIELD + ">");
			sb.append("</" + MobileConstant.TAG_SMSPANEL + ">");

		} catch (Exception e) {
			LOG.warn(e.toString());
			throw new Exception("查看失败");
		}
		return sb.toString();
	}

	private String getSmsReceiver() {
		if (StringUtil.isBlank(sms.getReceiver())) {
			if (!StringUtil.isBlank(uservo.getId())) {
				try {
					UserProcess process = (UserProcess) ProcessFactory
							.createProcess(UserProcess.class);
					uservo = (UserVO) process.doView(uservo.getId());
					if (uservo != null) {
						return getString(uservo.getTelephone());
					}
				} catch (Exception e) {
					LOG.warn(e);
					return "";
				}
			} else {
				return "";
			}
		}
		return sms.getReceiver();
	}

	private String getString(String str) {
		return str == null ? "" : str;
	}

	private String getSmsListXml() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"<" + MobileConstant.TAG_HIDDENFIELD + " "
						+ MobileConstant.ATT_NAME + "='operation'>").append(
				"sms");
		sb.append("</" + MobileConstant.TAG_HIDDENFIELD + ">");
		sb.append("<" + MobileConstant.TAG_TABLE + ">");

		// sb.append("<" + MobileConstant.TAG_ACTION + " " +
		// MobileConstant.ATT_NAME + "='{*[New]*}' " + MobileConstant.ATT_TYPE +
		// "='" + MobileConstant.BUTTON_NEW + "'>").append("</" +
		// MobileConstant.TAG_ACTION + ">");
		// sb.append("<" + MobileConstant.TAG_ACTION + " " +
		// MobileConstant.ATT_NAME + "='{*[Delete]*}' " +
		// MobileConstant.ATT_TYPE + "='" + MobileConstant.BUTTON_DELETE +
		// "'>").append("</" + MobileConstant.TAG_ACTION + ">");
		// sb.append("<" + MobileConstant.TAG_ACTION + " " +
		// MobileConstant.ATT_NAME + "='{*[Cancel]*}' " +
		// MobileConstant.ATT_TYPE + "='" + MobileConstant.BUTTON_CANCEL +
		// "'>").append("</" + MobileConstant.TAG_ACTION + ">");

		sb.append("<" + MobileConstant.TAG_TH + ">");
		sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Content]*}")
				.append("</" + MobileConstant.TAG_TD + ">");
		sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Receiver]*}")
				.append("</" + MobileConstant.TAG_TD + ">");
		sb.append("<" + MobileConstant.TAG_TD + ">").append(
				"{*[Send]*}{*[Date]*}").append(
				"</" + MobileConstant.TAG_TD + ">");
		sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Status]*}")
				.append("</" + MobileConstant.TAG_TD + ">");
		sb.append("</" + MobileConstant.TAG_TH + ">");

		int currPage = 1, total = 1;

		try {
			SubmitMessageProcess process = (SubmitMessageProcess) ProcessFactory
					.createProcess(SubmitMessageProcess.class);
			DataPackage<SubmitMessageVO> datas = process.list(getUser(),
					getParams());
			if (datas == null) {
				datas = new DataPackage<SubmitMessageVO>();
			}
			setDatas(datas);
			for (Iterator<SubmitMessageVO> it = datas.getDatas().iterator(); it
					.hasNext();) {
				SubmitMessageVO vo = it.next();
				sb.append("<" + MobileConstant.TAG_TR + " "
						+ MobileConstant.ATT_ID + "='" + vo.getId() + "'>");
				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table(vo.getContent()) + "").append(
						"</" + MobileConstant.TAG_TD + ">");
				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table(vo.getReceiver())).append(
						"</" + MobileConstant.TAG_TD + ">");
				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table(DateUtil.getDateTimeStr(vo
								.getSendDate()))).append(
						"</" + MobileConstant.TAG_TD + ">");
				String str = "";
				if (vo.getSubmission()) {
					str = "{*[Sent]*}";
				} else if (vo.isFailure()) {
					str = "{*[Failure]*}";
				} else if (vo.isDraft()) {
					str = "{*[Draft]*}";
				} else {
					str = "{*[Invalid]*}";
				}
				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table(str)).append(
						"</" + MobileConstant.TAG_TD + ">");
				sb.append("</" + MobileConstant.TAG_TR + ">");
			}
			currPage = getDatas().pageNo;
			total = getDatas().getPageCount();
		} catch (Exception e) {
			LOG.warn(e);
		}

		sb.append(
				"<" + MobileConstant.TAG_PAGE + " " + MobileConstant.ATT_TOTAL
						+ "='" + total + "' " + MobileConstant.ATT_CURRPAGE
						+ "='" + currPage + "'>").append(
				"</" + MobileConstant.TAG_PAGE + ">");

		sb.append("</" + MobileConstant.TAG_TABLE + ">");
		return sb.toString();
	}

	private String getMessageListXml() throws Exception {
		StringBuffer sb = new StringBuffer();
		try {
			ParamsTable params = getParams();
			params.removeParameter("msg_type");
			params.removeParameter("_selects");
			WebUser webUser = getUser();
			PersonalMessageProcess process = (PersonalMessageProcess) ProcessFactory
					.createProcess(PersonalMessageProcess.class);
			if (webUser != null) {
				String temp = "in";
				if (getMsgType().equals("out")) {
					temp = "out";
					setDatas(process.doOutbox(webUser.getId(), params));
				} else if (getMsgType().equals("trash")) {
					temp = "trash";
					setDatas(process.doTrash(webUser.getId(), params));
				} else { // in
					setDatas(process.doInbox(webUser.getId(), params));
				}
				sb.append(
						"<" + MobileConstant.TAG_HIDDENFIELD + " "
								+ MobileConstant.ATT_NAME + "='msg_type'>")
						.append(temp);
				sb.append("</" + MobileConstant.TAG_HIDDENFIELD + ">");
			}
			sb.append(
					"<" + MobileConstant.TAG_HIDDENFIELD + " "
							+ MobileConstant.ATT_NAME + "='operation'>")
					.append("message");
			sb.append("</" + MobileConstant.TAG_HIDDENFIELD + ">");
			sb.append("<" + MobileConstant.TAG_TABLE + ">");

			sb.append("<" + MobileConstant.TAG_TH + ">");
			sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Title]*}")
					.append("</" + MobileConstant.TAG_TD + ">");
			sb.append("<" + MobileConstant.TAG_TD + ">").append(
					"{*[Receiver]*}")
					.append("</" + MobileConstant.TAG_TD + ">");
			sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Sender]*}")
					.append("</" + MobileConstant.TAG_TD + ">");
			sb.append("<" + MobileConstant.TAG_TD + ">").append(
					"{*[SendDate]*}")
					.append("</" + MobileConstant.TAG_TD + ">");
			sb.append("</" + MobileConstant.TAG_TH + ">");

			int currPage = 1, total = 1;
			PersonalMessageHelper ph = new PersonalMessageHelper();
			for (Iterator<PersonalMessageVO> it = getDatas().getDatas()
					.iterator(); it.hasNext();) {
				PersonalMessageVO vo = it.next();
				if (vo.getBody() == null) {
					continue;
				}

				sb.append("<" + MobileConstant.TAG_TR + " "
						+ MobileConstant.ATT_ID + "='" + vo.getId() + "' "
						+ MobileConstant.ATT_ISNEW + "='" + vo.isRead() + "'>");
				if (!getMsgType().equals("out")) {
					if (vo.isRead()) {
						sb.append("<" + MobileConstant.TAG_TD + ">")
								.append(
										getString2Table("已读 "
												+ vo.getBody().getTitle())
												+ "").append(
										"</" + MobileConstant.TAG_TD + ">");
					} else {
						sb.append("<" + MobileConstant.TAG_TD + ">")
								.append(
										getString2Table("未读 "
												+ vo.getBody().getTitle())
												+ "").append(
										"</" + MobileConstant.TAG_TD + ">");
					}
				} else {
					sb.append("<" + MobileConstant.TAG_TD + ">").append(
							getString2Table(vo.getBody().getTitle()) + "")
							.append("</" + MobileConstant.TAG_TD + ">");
				}
				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table(ph.findUserNamesByMsgIds(vo
								.getReceiverId()))).append(
						"</" + MobileConstant.TAG_TD + ">");
				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table(ph.findUserNameById(vo.getSenderId())))
						.append("</" + MobileConstant.TAG_TD + ">");
				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table(DateUtil.getDateTimeStr(vo
								.getSendDate()))).append(
						"</" + MobileConstant.TAG_TD + ">");

				sb.append("<" + MobileConstant.TAG_TD + ">").append(
						getString2Table("")).append(
						"</" + MobileConstant.TAG_TD + ">");
				sb.append("</" + MobileConstant.TAG_TR + ">");
			}
			currPage = getDatas().pageNo;
			total = getDatas().getPageCount();

			sb.append(
					"<" + MobileConstant.TAG_PAGE + " "
							+ MobileConstant.ATT_TOTAL + "='" + total + "' "
							+ MobileConstant.ATT_CURRPAGE + "='" + currPage
							+ "'>")
					.append("</" + MobileConstant.TAG_PAGE + ">");

			sb.append("</" + MobileConstant.TAG_TABLE + ">");
		} catch (Exception e) {
			LOG.warn(e.toString());
			throw new Exception("获取站内短信失败");
		}
		return sb.toString();
	}

	private String getMessageXml() throws Exception {
		StringBuffer sb = new StringBuffer();
		try {
			PersonalMessageProcess process = (PersonalMessageProcess) ProcessFactory
					.createProcess(PersonalMessageProcess.class);
			msg = (PersonalMessageVO) process.doView(msg.getId());
			if (msg == null) {
				msg = new PersonalMessageVO();
				msg.setBody(new MessageBody());
			} else {
				if (!msg.isRead()) {
					msg.setRead(true);
					process.doUpdate(msg);
				}
			}

			sb.append("<" + MobileConstant.TAG_MESSAGEPANEL + " "
					+ MobileConstant.ATT_READONLY + "='"
					+ !StringUtil.isBlank(msg.getId()) + "'>");

			sb.append(
					"<" + MobileConstant.TAG_HIDDENFIELD + " "
							+ MobileConstant.ATT_NAME + "='msg.id' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(msg.getId()) + "'>").append(
					"</" + MobileConstant.TAG_HIDDENFIELD + ">");
			sb.append(
					"<" + MobileConstant.TAG_TEXTFIELD + " "
							+ MobileConstant.ATT_NAME + "='receiver' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(getReceiver()) + "'>").append(
					"</" + MobileConstant.TAG_TEXTFIELD + ">");
			sb.append(
					"<" + MobileConstant.TAG_TEXTFIELD + " "
							+ MobileConstant.ATT_NAME + "='msg.body.title' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(msg.getBody().getTitle()) + "'>")
					.append("</" + MobileConstant.TAG_TEXTFIELD + ">");

			sb.append(
					"<" + MobileConstant.TAG_TEXTAREAFIELD + " "
							+ MobileConstant.ATT_NAME + "='msg.body.content' "
							+ MobileConstant.ATT_VALUE + "='"
							+ getString(msg.getBody().getContent()) + "'>")
					.append("</" + MobileConstant.TAG_TEXTAREAFIELD + ">");
			sb.append("</" + MobileConstant.TAG_MESSAGEPANEL + ">");

		} catch (Exception e) {
			LOG.warn(e.toString());
			throw new Exception("查看失败");
		}
		return sb.toString();
	}

	private String getUserXml() throws Exception {
		StringBuffer sb = new StringBuffer();
		// WebUser user = getUser();
		UserProcess up = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);
		if (StringUtil.isBlank(uservo.getId())) {
			uservo = (UserVO) up.doView(getUser().getId());
		} else {
			uservo = (UserVO) up.doView(uservo.getId());
		}
		sb.append("<" + MobileConstant.TAG_USERPANEL + " "
				+ MobileConstant.ATT_OPTION + "='user' "
				+ MobileConstant.ATT_ID + "='" + uservo.getId() + "'>");

		sb.append(
				"<" + MobileConstant.TAG_TEXTFIELD + " "
						+ MobileConstant.ATT_NAME + "='uservo.name' "
						+ MobileConstant.ATT_VALUE + "='" + uservo.getName()
						+ "'>").append(
				"</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append(
				"<" + MobileConstant.TAG_TEXTFIELD + " "
						+ MobileConstant.ATT_NAME + "='uservo.loginno' "
						+ MobileConstant.ATT_VALUE + "='" + uservo.getLoginno()
						+ "'>").append(
				"</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append(
				"<" + MobileConstant.TAG_TEXTFIELD + " "
						+ MobileConstant.ATT_NAME + "='_password' "
						+ MobileConstant.ATT_VALUE + "='" + get_password()
						+ "'>").append(
				"</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append(
				"<" + MobileConstant.TAG_TEXTFIELD + " "
						+ MobileConstant.ATT_NAME + "='uservo.email' "
						+ MobileConstant.ATT_VALUE + "='" + uservo.getEmail()
						+ "'>").append(
				"</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append(
				"<" + MobileConstant.TAG_TEXTFIELD + " "
						+ MobileConstant.ATT_NAME + "='uservo.telephone' "
						+ MobileConstant.ATT_VALUE + "='"
						+ uservo.getTelephone() + "'>").append(
				"</" + MobileConstant.TAG_TEXTFIELD + ">");
		sb.append("<" + MobileConstant.TAG_SELECTFIELD + " "
				+ MobileConstant.ATT_NAME + "='uservo.calendarType' "
				+ MobileConstant.ATT_VALUE + "='" + uservo.getCalendarType()
				+ "'>");

		CalendarHelper ch = new CalendarHelper();
		ch.setDomain(uservo.getDomainid());
		Map<String, String> map = ch.getWorkCalendars();
		for (Iterator<Entry<String, String>> it = map.entrySet().iterator(); it
				.hasNext();) {
			Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(
					"<" + MobileConstant.TAG_OPTION + " "
							+ MobileConstant.ATT_VALUE + "='" + key + "'>")
					.append(value).append(
							"</" + MobileConstant.TAG_OPTION + ">");
		}
		sb.append("</" + MobileConstant.TAG_SELECTFIELD + ">");

		sb.append("<" + MobileConstant.TAG_SELECTFIELD + " "
				+ MobileConstant.ATT_NAME + "='_proxyUser' "
				+ MobileConstant.ATT_VALUE + "='" + get_proxyUser() + "'>");
		Collection<UserVO> list = getAllUsers();
		for (Iterator<UserVO> it = list.iterator(); it.hasNext();) {
			UserVO vo = it.next();
			sb.append(
					"<" + MobileConstant.TAG_OPTION + " "
							+ MobileConstant.ATT_VALUE + "='" + vo.getId()
							+ "'>").append(vo.getLoginno()).append(
					"</" + MobileConstant.TAG_OPTION + ">");
		}
		sb.append("</" + MobileConstant.TAG_SELECTFIELD + ">");

		sb.append("</" + MobileConstant.TAG_USERPANEL + ">");
		return sb.toString();
	}

	private String getUserListXml() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"<" + MobileConstant.TAG_HIDDENFIELD + " "
						+ MobileConstant.ATT_NAME + "='user'>").append("sms");
		sb.append("</" + MobileConstant.TAG_HIDDENFIELD + ">");
		sb.append("<" + MobileConstant.TAG_TABLE + ">");

		sb.append("<" + MobileConstant.TAG_TH + ">");
		sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Name]*}")
				.append("</" + MobileConstant.TAG_TD + ">");
		// sb.append("<" + MobileConstant.TAG_TD +
		// ">").append("{*[Account]*}").append("</" + MobileConstant.TAG_TD +
		// ">");
		sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Email]*}")
				.append("</" + MobileConstant.TAG_TD + ">");
		sb.append("<" + MobileConstant.TAG_TD + ">").append("{*[Mobile]*}")
				.append("</" + MobileConstant.TAG_TD + ">");
		sb.append("</" + MobileConstant.TAG_TH + ">");

		int currPage = 1, total = 1;

		try {
			UserProcess process = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			setDatas(process.doQuery(getParams()));
			if (getDatas().datas != null) {
				for (Iterator<UserVO> it = getDatas().datas.iterator(); it
						.hasNext();) {
					UserVO vo = it.next();
					sb.append("<" + MobileConstant.TAG_TR + " "
							+ MobileConstant.ATT_ID + "='" + vo.getId() + "'>");
					sb.append("<" + MobileConstant.TAG_TD + ">").append(
							getString2Table(vo.getName())).append(
							"</" + MobileConstant.TAG_TD + ">");
					// sb.append("<" + MobileConstant.TAG_TD +
					// ">").append(getString2Table(vo.getLoginno())).append("</"
					// + MobileConstant.TAG_TD + ">");
					sb.append("<" + MobileConstant.TAG_TD + ">").append(
							getString2Table(vo.getEmail())).append(
							"</" + MobileConstant.TAG_TD + ">");
					sb.append("<" + MobileConstant.TAG_TD + ">").append(
							getString2Table(vo.getTelephone())).append(
							"</" + MobileConstant.TAG_TD + ">");
					sb.append("</" + MobileConstant.TAG_TR + ">");
				}
				currPage = getDatas().pageNo;
				total = getDatas().getPageCount();
			}
		} catch (Exception e) {
			LOG.warn(e);
		}

		sb.append(
				"<" + MobileConstant.TAG_PAGE + " " + MobileConstant.ATT_TOTAL
						+ "='" + total + "' " + MobileConstant.ATT_CURRPAGE
						+ "='" + currPage + "'>").append(
				"</" + MobileConstant.TAG_PAGE + ">");

		sb.append("</" + MobileConstant.TAG_TABLE + ">");
		return sb.toString();
	}

	public UserVO getUservo() {
		return uservo;
	}

	public void setUservo(UserVO uservo) {
		this.uservo = uservo;
	}

	public String doSave() {
		String toXml = "";
		try {
			if (getOperation().equals("user")) {
				savePersonal();
				toXml = getUserXml();
			} else if (getOperation().equals("message")) {
				saveMsg();
				// toXml = getMessageXml();
			} else if (getOperation().equals("sms")) {
				saveSms();
				toXml = getSmsListXml();
			}
			ServletActionContext.getRequest().setAttribute("toXml", toXml);
			// System.out.println("toXml--> " + toXml);
		} catch (Exception e) {
			if (getOperation().equals("user")) {

			} else if (getOperation().equals("message")) {

			} else if (getOperation().equals("sms")) {
				toXml = sms.getId();
			}
			addFieldError("SystemError", e.toString() + "id=" + toXml);
			LOG.warn(e);
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * 保存个人信息
	 * 
	 * @return 成功处理返回"SUCCESS",否则提示失败
	 * @throws Exception
	 */
	private void savePersonal() throws Exception {
		// set_password(_password);
		validateUser();
		UserProcess up = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);
		set_proxyUser(_proxyUser);
		WebUser webUser = getUser();
		up.doPersonalUpdate(uservo);
		UserVO uv = up.login(uservo.getLoginno(), uservo.getLoginpwd(), webUser
				.getDomain().getName());

		webUser.setName(uservo.getName());
		webUser.setLoginno(uservo.getLoginno());
		webUser.setLoginpwd(uv.getLoginpwd());
		webUser.setEmail(uservo.getEmail());
		webUser.setTelephone(uservo.getTelephone());
		webUser.setCalendarType(uservo.getCalendarType());

		getSession().setAttribute(getWebUserSessionKey(), webUser);

		// this.addActionMessage("{*[Save_Success]*}");
	}

	private void validateUser() throws Exception {
		if (StringUtil.isBlank(uservo.getName())) {
			throw new Exception("{*[page.name.notexist]*}!");
		}
		if (StringUtil.isBlank(uservo.getLoginno())) {
			throw new Exception("{*[page.user.account.illegal]*}!");
		}
		if (StringUtil.isBlank(uservo.getLoginpwd())) {
			throw new Exception("{*[page.user.loginpwd.illegal]*}!");
		}
	}

	private void saveMsg() throws Exception {
		validateMsg();
		WebUser user = getUser();
		ParamsTable param = getParams();

		if (user != null && msg != null) {
			msg.setSenderId(user.getId());
		}
		if (StringUtil.isBlank(msg.getSenderId())) {
			throw new Exception("没有找到发件人");
		}
		// String ids = param.getParameterAsString("ids");
		String receiverid = param.getParameterAsString("receiver_id");
		// Collection receiverList = new ArrayList();
		// if (ids != null) {
		if (user != null) {
			// String[] idArray = ids.split(";");
			// String[] idArray = receiverid.split(";");
			PersonalMessageProcess process = (PersonalMessageProcess) ProcessFactory
					.createProcess(PersonalMessageProcess.class);
			// process.doCreateByUserIds(idArray, msg);
			msg.setReceiverId(receiverid);
			msg.setSenderId(user.getId());
			PersonalMessageVO pmVO = process.doSavePersonalMessageVO(msg);
			// 如果参数中附有sendMsg.by.xmpp参数,则使用XMPP发送站内短信
			String sendMsgByXMPP = param
					.getParameterAsString("sendMsg.by.xmpp");
			if (pmVO != null && "true".equalsIgnoreCase(sendMsgByXMPP)) {
				sendMessageByXMPP(pmVO);
			}

		} else {
			throw new Exception("没有找到发件人");
		}
	}

	/**
	 * 使用xmpp发送站内短信给接收者
	 * 
	 * @throws Exception
	 */
	private void sendMessageByXMPP(PersonalMessageVO pmVO) throws Exception {
		UserProcess process = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);

		WebUser user = getUser();
		ParamsTable param = getParams();
		String title = param.getParameterAsString("msg.body.title");
		String content = param.getParameterAsString("msg.body.content");
		String to = param.getParameterAsString("receiver");
		String receiverid = param.getParameterAsString("receiver_id");

		UserVO userVO = (UserVO) process.doView(receiverid);
		if (userVO != null) {
			ParamsTable param_user = new ParamsTable();
			param_user.setParameter("username", to);

			SiteMessageIQ siteMessageIQ = new SiteMessageIQ();
			siteMessageIQ.setId(pmVO.getId());
			siteMessageIQ.setTitle(title);
			siteMessageIQ.setContent(content);
			siteMessageIQ.setDomain(userVO.getDomain());
			siteMessageIQ.setSender(user); // 设置发送者
			siteMessageIQ.getReceivers().add(userVO); // 添加接收者
			siteMessageIQ.setType(Type.SET);

			XMPPSender sender = XMPPSender.getInstance();
			// if (!siteMessageIQSender.isConnected()) {
			// siteMessageIQSender.connect();
			// }
			sender.processNotification(siteMessageIQ);
		}
	}

	private void validateMsg() throws Exception {
		if (msg == null) {
			throw new Exception("保存失败!");
		}
	}

	private void saveSms() throws Exception {
		validateSms();
		WebUser uservo = getUser();
		if (uservo != null && uservo.getTelephone() != null
				&& sms.getSender() == null) {
			sms.setSender(uservo.getTelephone());
		}
		if (!sms.getSubmission()) {
			SMSModeProxy sender = new SMSModeProxy(uservo);
			String receiver = sms.getReceiver();
			String sendtel = uservo.getTelephone();
			if (sendtel != null && sendtel.trim().length() > 0) {
				if (receiver != null) {
					set_msgType(getParams().getParameterAsString("_smgType"));
					sms.setSender(sendtel);
					sms.setApplicationid(getApplication());
					sms.setDomainid(uservo.getDomainid());
					sender.send(sms);
					SubmitMessageProcess process = (SubmitMessageProcess) ProcessFactory
							.createProcess(SubmitMessageProcess.class);
					sms = (SubmitMessageVO) process.doView(sms.getId());
					if (sms.isFailure()) {
						throw new Exception("{*[Failure]*}");
					}
				} else {
					throw new Exception("{*[core.shortmessage.norecvlist]*}");
				}
			} else {
				throw new Exception("{*[core.shortmessage.nosender]*}");
			}
		}
	}

	private void validateSms() throws Exception {
		if (StringUtil.isBlank(sms.getReceiver())) {
			throw new Exception("{*[core.shortmessage.norecvlist]*}!");
		}
		if (StringUtil.isBlank(sms.getContent())) {
			throw new Exception("{*[page.content.notexist]*}!");
		}
	}

	public String doList() {
		try {
			String toXml = "<" + MobileConstant.TAG_VIEW + " "
					+ MobileConstant.ATT_OPTION + "='";
			if (getOperation().equals("user")) {
				toXml += "user'>" + getUserListXml();
			} else if (getOperation().equals("message")) {
				toXml += "message'>" + getMessageListXml();
			} else if (getOperation().equals("sms")) {
				toXml += "sms'>" + getSmsListXml();
			} else {
				toXml += "'>";
			}
			toXml += "</" + MobileConstant.TAG_VIEW + ">";
			ServletActionContext.getRequest().setAttribute("toXml", toXml);
			// System.out.println("toXml--> " + toXml);
		} catch (Exception e) {
			addFieldError("SystemError", e.toString());
			LOG.warn(e);
			return ERROR;
		}
		return SUCCESS;
	}

	public String doDelete() {
		try {
			if (getOperation().equals("user")) {

			} else if (getOperation().equals("message")) {
				if (_selects == null || _selects.length == 0) {
					// throw new Exception();
					return doList();
				}
				PersonalMessageProcess process = (PersonalMessageProcess) ProcessFactory
						.createProcess(PersonalMessageProcess.class);
				if (getMsgType().equals("trash")) {
					process.doRemove(_selects);
				} else {
					process.doSendToTrash(_selects);
				}
			} else if (getOperation().equals("sms")) {
				if (_selects == null || _selects.length == 0) {
					// throw new Exception();
					return doList();
				}
				SubmitMessageProcess process = (SubmitMessageProcess) ProcessFactory
						.createProcess(SubmitMessageProcess.class);
				process.doRemove(_selects);
			}
		} catch (Exception e) {
			LOG.warn(e);
			addFieldError("SystemError", "{*[Delete]*}{*[page.obj.dofailure]*}");
			return ERROR;
		}
		return doList();
	}

	public String getOperation() {
		if (operation == null)
			operation = "";
		return operation.split(",")[0].trim();
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public HttpSession getSession() {
		return ServletActionContext.getRequest().getSession();
	}

	public String getWebUserSessionKey() {
		return Web.SESSION_ATTRIBUTE_FRONT_USER;
	}

	private String _password = "";

	private String _proxyUser = "";

	public String get_password() {
		return Web.DEFAULT_SHOWPASSWORD;
	}

	public void set_password(String password) {
		_password = password;
		// if (!Web.DEFAULT_SHOWPASSWORD.equals(_password)) {
		uservo.setLoginpwd(_password);
		// }
	}

	public String get_proxyUser() {
		if (uservo.getProxyUser() != null) {
			_proxyUser = uservo.getProxyUser().getId();
		}
		return _proxyUser;
	}

	public void set_proxyUser(String proxyUser) throws Exception {
		_proxyUser = proxyUser;
		if (!StringUtil.isBlank(proxyUser)) {
			UserProcess up = (UserProcess) ProcessFactory
					.createProcess(UserProcess.class);
			UserVO proxyUserVO = (UserVO) up.doView(proxyUser);
			if (proxyUserVO != null) {
				uservo.setProxyUser(proxyUserVO);
			}
		}
	}

	/**
	 * 获取所有用户
	 * 
	 * @return 用户列表
	 * @throws Exception
	 */
	public Collection<UserVO> getAllUsers() throws Exception {
		UserProcess up = (UserProcess) ProcessFactory
				.createProcess(UserProcess.class);
		ParamsTable params = this.getParams();
		String domainid = params.getParameterAsString("domain");
		if (StringUtil.isBlank(domainid)) {
			domainid = uservo.getDomainid();
		}
		Collection<UserVO> userList = new ArrayList<UserVO>();
		UserVO none = new UserVO();
		none.setId("");
		none.setLoginno("{*[None]*}");
		none.setName("{*[None]*}");
		userList.add(none);
		userList.addAll(up.queryByDomain(domainid));
		return userList;
	}

	public SubmitMessageVO getSms() {
		return sms;
	}

	public void setSms(SubmitMessageVO sms) {
		this.sms = sms;
	}

	// private String _smgType = "0";

	public String get_msgType() {
		return sms.getContentType() + "";
	}

	public void set_msgType(String type) {
		if (type != null) {
			sms.setContentType(Integer.parseInt(type.trim()));
		}
	}

	private String getString2Table(String str) {
		if (StringUtil.isBlank(str)) {
			return " ";
		}
		return str;
	}

	@Override
	public void set_selects(String[] selects) {
		if (selects != null && selects.length == 1) {
			_selects = selects[0].split(";");
		} else {
			super.set_selects(selects);
		}
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		if (StringUtil.isBlank(msgType))
			msgType = "in";
		this.msgType = msgType;
	}

	// private String receiver;

	public String getReceiver() {
		if (msg == null)
			return "";
		PersonalMessageHelper helper = new PersonalMessageHelper();
		try {
			return helper.findUserNamesByMsgIds(msg.getReceiverId());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	// public void setReceiver(String receiver) {
	// this.receiver = receiver;
	// }

	public PersonalMessageVO getMsg() {
		return msg;
	}

	public void setMsg(PersonalMessageVO msg) {
		this.msg = msg;
	}

}
