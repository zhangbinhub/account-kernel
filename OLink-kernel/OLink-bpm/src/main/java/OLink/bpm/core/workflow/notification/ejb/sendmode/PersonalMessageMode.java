package OLink.bpm.core.workflow.notification.ejb.sendmode;

import java.util.Map;

import OLink.bpm.core.personalmessage.ejb.PersonalMessageProcess;
import OLink.bpm.core.personalmessage.ejb.PersonalMessageVO;
import OLink.bpm.core.workflow.notification.ejb.SendMode;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.personalmessage.ejb.MessageBody;
import OLink.bpm.core.sysconfig.ejb.ImConfig;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.gkmsapi.GK_MS_API;
import OLink.bpm.gkmsapi.Message;
import OLink.bpm.gkmsapi.XMLParser;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.property.PropertyUtil;
import org.apache.log4j.Logger;
import org.jfree.util.Log;

/**
 * 站内短信发送模版
 * 
 * @author Tom
 * 
 */
public class PersonalMessageMode implements SendMode {

	private String subject;
	private String content;
	private String receiver;
	private static final Logger LOG = Logger
			.getLogger(PersonalMessageMode.class);

	public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc,
						BaseUser responsible) throws Exception {
		return send(doc.getId(), subject, summaryCfg.toSummay(doc,new WebUser(responsible)), responsible);
	}

	public boolean send(SummaryCfgVO summaryCfg, Document doc, BaseUser responsible)
			throws Exception {
		return send(null, summaryCfg, doc, responsible);
	}

	public boolean send(String subject, String content, BaseUser responsible)
			throws Exception {
		//--------发送IM消息例子-------
		try{
			PropertyUtil.reload("im");
			//判断是否开启GKE
			if(PropertyUtil.get(ImConfig.GKE_API_OPEN).equals(ImConfig.GKEOPEN)){
				XMLParser xml = GK_MS_API.getInstance().getUser(responsible.getLoginno());
				if(xml.getCode()==0){
					Message imm=new Message("im","sys");
					imm.addRecvAccount(responsible.getLoginno());
					imm.setMessage(subject,content);
					xml=GK_MS_API.getInstance().sendMessage(imm);
					if(xml.getCode()!=0){
						Log.error(responsible.getLoginno()+" Gke-Server("+xml.getCode()+"):"+xml.getMessage());
					}
				}else{
					Log.error(responsible.getLoginno()+" Gke-Server("+xml.getCode()+"):"+xml.getMessage());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return send(subject, content, responsible.getId());
	}

	public boolean send(String docid, String subject, String content,
			BaseUser responsible) throws Exception {
		if (subject != null) {
			subject += "[" + responsible.getName() + "]";
		}
		return send(docid, subject, content, responsible.getId());
	}

	public boolean send(String subject, String content, String receiver)
			throws Exception {
		return send(subject, content, receiver, false);
	}

	public boolean send(String docid, String subject, String content,
			String receiver) throws Exception {
		return send(docid, subject, content, receiver, false, false);
	}

	public boolean send(String subject, String content, String receiver,
			boolean mass) throws Exception {
		return send(subject, content, receiver, "", "", mass);
	}

	public boolean send(String subject, String content, String receiver,
			String replyPrompt, String code, boolean mass) throws Exception {
		return send(null, subject, content, receiver, replyPrompt, code, mass);
	}

	/**
	 * @SuppressWarnings 不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean send(String subject, String content, String receiver,
			Map defineReply, boolean mass) throws Exception {
		return send(null, subject, content, receiver, defineReply, mass);
	}

	public boolean send(String docid, String subject, String content,
			String receiver, String replyPrompt, String code, boolean mass)
			throws Exception {
		this.subject = subject;
		this.content = content;
		this.receiver = receiver;
		return send();
	}

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 */
	@SuppressWarnings("unchecked")
	public boolean send(String docid, String subject, String content,
			String receiver, Map defineReply, boolean mass) throws Exception {
		this.subject = subject;
		this.content = content;
		this.receiver = receiver;
		return send();
	}

	public boolean send(String docid, String subject, String content,
			String receiver, boolean isNeedReply, boolean mass)
			throws Exception {
		this.subject = subject;
		this.content = content;
		this.receiver = receiver;
		return send();
	}

	private boolean send() {
		PersonalMessageVO vo = new PersonalMessageVO();
		MessageBody body = new MessageBody();
		body.setTitle(subject);
		body.setContent(content);
		vo.setBody(body);
		vo.setReceiverId(receiver);
		SendThread sendThread = new SendThread(vo);
		sendThread.start();
		return true;
	}
	
	public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc,
			BaseUser responsible, boolean approval) throws Exception {
		return false;
	}

	private static class SendThread extends Thread {

		private PersonalMessageVO personalMessageVO;

		public SendThread(PersonalMessageVO personalMessageVO) {
			this.personalMessageVO = personalMessageVO;
		}

		@Override
		public void run() {
			try {
				if (personalMessageVO != null) {
					PersonalMessageProcess process = (PersonalMessageProcess) ProcessFactory
							.createProcess(PersonalMessageProcess.class);
					process.doCreate(personalMessageVO);
					// process.doCreateByUserIds(new
					// String[]{personalMessageVO.getReceiverId()},
					// personalMessageVO);
				}
			} catch (Exception e) {
				LOG.warn(e.toString());
			}
		}

	}

}
