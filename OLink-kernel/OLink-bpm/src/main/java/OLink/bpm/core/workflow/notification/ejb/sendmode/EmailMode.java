package OLink.bpm.core.workflow.notification.ejb.sendmode;

import java.util.HashMap;
import java.util.Map;

import OLink.bpm.core.shortmessage.runtime.DeFineReplyContentUtil;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.workflow.notification.ejb.SendMode;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.mail.EmailSender;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.util.property.PropertyUtil;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.user.ejb.BaseUser;

public class EmailMode implements SendMode {

	String from;
	String host;
	String user;
	String password;
	String bcc;

	public EmailMode() {
		from = PropertyUtil.get("from");
		host = PropertyUtil.get("host");
		user = PropertyUtil.get("user");
		password = PropertyUtil.get("password");
		bcc = PropertyUtil.get("bcc");
	}

	public boolean send(String subject, String content, BaseUser responsible)
			throws Exception {
		return send(null, subject, content, responsible);
	}

	public boolean send(String subject, String content, String receiver)
			throws Exception {
		return send(null, subject, content, receiver);
	}

	public boolean send(String subject, String content, String receiver,
			Map<String, String> defineReply, boolean mass) throws Exception {
		return send(null, subject, content, receiver, defineReply, mass);
	}

	public boolean send(String subject, String content, String receiver,
			String replyPrompt, String code, boolean mass) throws Exception {
		return send(null, subject, content, receiver, replyPrompt, code, mass);
	}

	public boolean send(String subject, String content, String receiver,
			boolean mass) throws Exception {
		return send(subject, content, receiver, null, mass);
	}

	public boolean send(String docid, String subject, String content,
			BaseUser responsible) throws Exception {
		return send(null, subject, content, responsible.getEmail());
	}

	public boolean send(String docid, String subject, String content,
			String receiver) throws Exception {
		return send(subject, content, receiver, false);
	}

	public boolean send(String docid, String subject, String content,
			String receiver, String replyPrompt, String code, boolean mass)
			throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		params.put(replyPrompt, code);
		return send(docid, subject, content, receiver, params, mass);

	}

	public boolean send(String docid, String subject, String content,
			String receiver, Map<String, String> defineReply, boolean mass)
			throws Exception {
		if (!StringUtil.isBlank(receiver)) { // 接收人地址
			String replyContent = DeFineReplyContentUtil
					.getDeFineReplyString(defineReply);
			String body = content;
			if (!StringUtil.isBlank(replyContent)) {
				body += replyContent;
			}
			body = this.htmlDecodeEncoder(body);

			EmailSender sender = EmailSender.getInstance();
			sender.addEmail(from, receiver, subject, body, host, user, password, bcc, true);
			sender.sendEmail();
			return true;
		}

		return false;
	}
	
	private String htmlDecodeEncoder(String content) {
		if (StringUtil.isBlank(content)) {
			return content;
		}
		content = content.replaceAll("&quot;", "\"");
		content = content.replaceAll("&amp;", "&");
		content = content.replaceAll("&lt;", "<");
		content = content.replaceAll("&gt;", ">");
		return content;
	}

	public boolean send(SummaryCfgVO summaryCfg, Document doc, BaseUser responsible)
			throws Exception {
		return send(summaryCfg.getTitle(), summaryCfg, doc, responsible);
	}

	public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc,
			BaseUser responsible) throws Exception {
		subject = subject + "[" + responsible.getName() + "]";

		String content = "";
		if (doc != null) {
			content = summaryCfg.toSummay(doc,new WebUser(responsible));
			return send(doc.getId(), subject, content, responsible.getEmail());
		} else {
			throw new Exception("Could not send with null document");
		}
	}

	public boolean send(String docid, String subject, String content,
			String receiver, boolean isNeedReply, boolean mass)
			throws Exception {
		throw new UnsupportedOperationException();
	}

	public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc,
			BaseUser responsible, boolean approval) throws Exception {
		return false;
	}
}
