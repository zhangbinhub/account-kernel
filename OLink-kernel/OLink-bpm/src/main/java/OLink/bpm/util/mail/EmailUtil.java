package OLink.bpm.util.mail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.page.ejb.Page;
import OLink.bpm.core.page.ejb.PageProcess;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;
import OLink.bpm.core.user.ejb.UserProcess;
import OLink.bpm.core.user.ejb.UserVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.util.property.PropertyUtil;
import org.apache.log4j.Logger;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.Tag;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.util.NodeList;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;

public class EmailUtil {

	private static final Logger log = Logger.getLogger(EmailUtil.class);
	
	public static boolean _DEBUG = true;
	private EmailSender _sender;
	private String applicationid;

	public EmailUtil() {
		this(null);
	}

	public EmailUtil(String applicationid) {
		setApplicationid(applicationid);
		_sender = EmailSender.getInstance();
	}

	public void setEmail(String from, String to, String subject, String body, String host, String user,
			String password, String bcc, boolean validate) {

		//_sender = new EmailSender(from, to, subject, body, host, user, password, bcc, validate);
		
		_sender.addEmail(from, to, subject, body, host, user, password, bcc, validate);
	}

	/**
	 * Send the e-mail.
	 * 
	 * @param email
	 *            The emal object.
	 * @throws Exception
	 */
	public void send() throws Exception {
		_sender.sendEmail();
	}

	public void sendMailToAllUser(String from, String subject, String host, String account, String password,
			String bbc, boolean validate) throws Exception {

		try {
			// PersistenceUtils.getSessionSignal().sessionSignal++;

			UserProcess up = (UserProcess) ProcessFactory.createProcess(UserProcess.class);
			Collection<UserVO> colls = up.doQueryHasMail(applicationid);
			String body = "";
			for (Iterator<UserVO> iter = colls.iterator(); iter.hasNext();) {
				UserVO user = iter.next();
				String to = user.getEmail();
				// String to = "jarod@teemlink.com";
				if (isEmailAddress(to) && user.getStatus() == 1) {
					body = getSendPageTxt(new WebUser(user));
					if (!isEmptyPage(body)) {
//						EmailSender _sender = new EmailSender(from, to, subject + "[" + user.getName() + "]", body,
//								host, account, password, bbc, validate);
						_sender.addEmail(from, to, subject + "[" + user.getName() + "]", body,
								host, account, password, bbc, validate);
						_sender.sendEmail();
					}
				}
			}
			// map.put("zhen_001@163.com", txt);
		} catch (Exception e) {
			log.warn("---------sendMailToAllUser error--------------");
		} finally {
			// PersistenceUtils.getSessionSignal().sessionSignal--;
			PersistenceUtils.closeSession();
		}
	}

	private String getSendPageTxt(WebUser webUser) throws Exception {
		StringBuffer html = new StringBuffer();

		PageProcess pp = (PageProcess) ProcessFactory.createProcess(PageProcess.class);
		Page mailPage = pp.doViewByName("mail", applicationid);
		if (mailPage != null) {
			ParamsTable params = new ParamsTable();
			StyleRepositoryVO style = mailPage.style;
			html.append("<HTML>");
			if (style != null) {
				String content = style.getContent();
				if (content != null && content.trim().length() > 0) {
					html.append("<HEAD>");
					html.append("<STYLE>");
					html.append(content);
					html.append("</STYLE>");
					html.append("</HEAD>");
				}
			}

			html.append("<BODY>");
			html.append(mailPage.toHtml(new Document(), params, webUser, new ArrayList<ValidateMessage>()));
			html.append("</BODY>");
			html.append("</HTML>");
		}

		return html.toString();
	}

	private boolean isEmptyPage(String page) throws Exception {
		Parser parser = new Parser();
		parser.setInputHTML(page);
		NodeList list = parser.parse(new TagNameFilter("TABLE"));
		Node[] nodes = list.toNodeArray();

		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i] instanceof Tag) {
				String pageid = ((Tag) nodes[i]).getAttribute("pageid");
				if (pageid != null && pageid.trim().length() > 0) {
					return false;
				}
			}
		}
		return true;
	}

	public String getApplicationid() {
		return applicationid;
	}

	public void setApplicationid(String applicationid) {
		this.applicationid = applicationid;
	}
	
	/**
	 * 用系统配置用户发送邮件
	 * @param to 邮件接收人
	 * @param subject 邮件主题
	 * @param content 邮件内容
	 */
	public void sendEmailBySystemUser(String to, String subject, String content) {
		if (isEmailAddress(to) && !StringUtil.isBlank(content)) {
			String from = PropertyUtil.getByPropName("email", "from");
			String host = PropertyUtil.getByPropName("email", "host");
			String user = PropertyUtil.getByPropName("email", "user");
			String password = PropertyUtil.getByPropName("email", "password");
			Email email = new Email(from, to, subject, content, host, user, password, null, true);
			_sender.addEmail(email);
			_sender.sendEmail();
		} else {
			log.warn("Sent email by system user error: to=" + to);
		}
	}
	
	public boolean isEmailAddress(String address) {
		if (address != null && address.trim().length() > 0) {
			Pattern p = Pattern.compile("(.*)@(.*)\\.(.*)"); // 检验Email地址
			Matcher m = p.matcher(address);
			return m.matches();
		}
		return false;
	}

	/**
	 * 示例
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		EmailUtil eUtil = new EmailUtil();
		String to = "xiuwei168@qq.com";
		String subject = "主题註冊";
		String body = "内容";
		String from = "xiuwei168@qq.com";
		String host = "smtp.qq.com";
		String user = "xiuwei168";
		String password = "happy";
		String bbc = "";
		boolean validate = true;

		eUtil.setEmail(from, to, subject, body, host, user, password, bbc, validate);
		eUtil.send();
		
		
		eUtil.sendEmailBySystemUser("411238450@qq.com", "test", "test email!");
	}
}
