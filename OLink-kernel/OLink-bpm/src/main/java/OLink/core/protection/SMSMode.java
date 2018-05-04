package OLink.core.protection;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.shortmessage.runtime.DeFineReplyContentUtil;
import OLink.bpm.core.shortmessage.submission.ejb.MessageType;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageProcess;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;
import OLink.bpm.core.workflow.notification.ejb.SendMode;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SMSMode implements SendMode {
    private static final Logger log = Logger.getLogger(SendMode.class);
    private String content;
    private String[] receiver;
    private String sign;
    private String application;
    private String domainid;
    private String from;
    private String receiverUserId;
    private String replyCode;

    public SMSMode(WebUser user) {
        this("", user);
    }

    public SMSMode(String sign, WebUser user) {
        this(user.getTelephone(), sign, user.getDefaultApplication(), user.getDomainid());
    }

    public SMSMode(String sign, String application, String domainid) {
        this("70104", sign, application, domainid);
    }

    public SMSMode(String from, String sign, String application, String domainid) {
        this.from = from;
        this.sign = sign;
        this.application = application;
        this.domainid = domainid;
    }

    public String getContent() {
        return this.content;
    }

    public String[] getReceiver() {
        return this.receiver;
    }

    public String getSign() {
        return this.sign;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReceiver(String[] receiver) {
        this.receiver = receiver;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public boolean send(String subject, String content, BaseUser responsible)
            throws Exception {
        return send(subject, content, responsible.getTelephone());
    }

    public boolean send(String subject, String content, String receiver)
            throws Exception {
        return send(subject, content, receiver, false);
    }

    public boolean send(String subject, String content, String receiver, boolean mass)
            throws Exception {
        return send(subject, content, receiver, null, mass);
    }

    public boolean send(String subject, String content, String receiver, String replyPrompt, String code, boolean mass)
            throws Exception {
        return send(null, subject, content, receiver, replyPrompt, code, mass);
    }

    public boolean send(int type, String docid, String subject, String content, String receiver, String replyPrompt, String code, boolean mass) throws Exception {
        String body = MessageType.getName(type);
        boolean falg = false;
        if (!StringUtil.isBlank(subject)) {
            body = body + subject.trim();
        }
        body = body + content.trim();

        this.replyCode = code;

        this.content = body;
        this.receiver = receiver.trim().split(",");
        SubmitMessageVO senderVO = createMessage(subject, content, receiver, code, docid, mass);
        if ((falg = send()))
            senderVO.setSubmission(true);
        else
            senderVO.setFailure(true);
        try {
            SubmitMessageProcess process = (SubmitMessageProcess)
                    ProcessFactory.createProcess(SubmitMessageProcess.class);
            process.doCreate(senderVO);
        } catch (Exception e) {
            throw e;
        } finally {
            PersistenceUtils.closeSession();
        }
        return falg;
    }

    public boolean send(String subject, String content, String receiver, Map<String, String> defineReply, boolean mass)
            throws Exception {
        return send(null, subject, content, receiver, defineReply, mass);
    }

    public boolean send(int type, String docId, String subject, String content, String receiver, Map<String, String> defineReply, boolean mass)
            throws Exception {
        boolean falg = false;
        if (!StringUtil.isBlank(receiver)) {
            String body = MessageType.getName(type);
            if (!StringUtil.isBlank(subject)) {
                body = body + subject.trim();
            }
            body = body + content.trim();

            if ((defineReply != null) && (defineReply.size() > 0))
                this.replyCode = ((String) defineReply.values().toArray()[0]);
            this.content = body;
            this.receiver = receiver.trim().split(",");
            SubmitMessageVO senderVO = createMessage(subject, content, receiver, this.replyCode, docId, mass);
            if ((falg = send()))
                senderVO.setSubmission(true);
            else
                senderVO.setFailure(true);
            try {
                SubmitMessageProcess process = (SubmitMessageProcess)
                        ProcessFactory.createProcess(SubmitMessageProcess.class);
                process.doCreate(senderVO);
            } catch (Exception e) {
                throw e;
            } finally {
                PersistenceUtils.closeSession();
            }
        }
        return falg;
    }

    private boolean send() throws Exception {
        SendSMSThread thread = new SendSMSThread();
        thread.start();
        int count = 0;
        while (thread.flag == null) {
            Thread.sleep(1000L);
            count++;
            if ("true".equals(thread.flag))
                return true;
            if ("false".equals(thread.flag)) {
                return false;
            }

            if (count == 3) {
                return false;
            }
        }
        return true;
    }

    public boolean send(int type, String docid, String subject, String content, String receiver, boolean isNeedReply, boolean mass) throws Exception {
        Calendar cld = Calendar.getInstance();
        if (isNeedReply) {
            if (mass) {
                return send(type, docid, subject, content, receiver, "收到请回短信", DeFineReplyContentUtil.getReplyCode("G",
                        "GROUP" + cld.get(1) + cld.get(2), this.application), mass);
            }

            return send(type, docid, subject, content, receiver, "收到请回短信", DeFineReplyContentUtil.getReplyCode("",
                    receiver, this.application), mass);
        }

        return send(type, docid, subject, content, receiver, new HashMap(), mass);
    }

    public boolean send(int type, String subject, String content, String receiver, boolean isNeedReply, boolean mass)
            throws Exception {
        return send(type, null, subject, content, receiver, isNeedReply, mass);
    }

    public boolean send(String docid, String subject, String content, String receiver, boolean isNeedReply, boolean mass) throws Exception {
        return send(0, docid, subject, content, receiver, isNeedReply, mass);
    }

    public boolean send(SubmitMessageVO receiverVO) throws Exception {
        Calendar cld = Calendar.getInstance();
        String replyContent = "";
        boolean flag = false;
        if (receiverVO.isNeedReply()) {
            if (receiverVO.isMass())
                this.replyCode = DeFineReplyContentUtil.getReplyCode("G", "GROUP" + cld.get(1) +
                        cld.get(2), receiverVO.getApplicationid());
            else {
                this.replyCode = DeFineReplyContentUtil.getReplyCode("", receiverVO.getReceiver(), receiverVO
                        .getApplicationid());
            }
        }

        String body = MessageType.getName(receiverVO.getContentType());
        if (!StringUtil.isBlank(receiverVO.getTitle())) {
            body = body + receiverVO.getTitle().trim();
        }
        body = body + receiverVO.getContent().trim();
        if (!StringUtil.isBlank(replyContent)) {
            body = body + replyContent.trim();
        }
        receiverVO.setReplyCode(this.replyCode);
        this.content = body;
        String[] receivers = receiverVO.getReceiver().trim().split(";");
        if ((receivers != null) && (receivers.length > 2))
            this.receiver = receivers;
        else {
            this.receiver = receiverVO.getReceiver().trim().split(",");
        }
        if ((flag = send()))
            receiverVO.setSubmission(true);
        else
            receiverVO.setFailure(true);
        try {
            SubmitMessageProcess process = (SubmitMessageProcess)
                    ProcessFactory.createProcess(SubmitMessageProcess.class);
            if ((receiverVO.getId() == null) || (receiverVO.getId().trim().length() <= 0)) {
                process.doCreate(receiverVO);
            } else {
                receiverVO.setSendDate(new Date());
                process.doUpdate(receiverVO);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            PersistenceUtils.closeSession();
        }
        return flag;
    }

    private SubmitMessageVO createMessage(String subject, String content, String receiver, String replyCode, String docId, boolean mass) throws Exception {
        SubmitMessageVO senderVO = new SubmitMessageVO();
        senderVO.setTitle(subject);
        senderVO.setContent(content);
        senderVO.setReceiver(receiver);
        senderVO.setReplyCode(replyCode);
        if (!StringUtil.isBlank(replyCode)) {
            senderVO.setNeedReply(true);
        }
        senderVO.setSendDate(new Date());
        senderVO.setApplicationid(this.application);
        senderVO.setDomainid(this.domainid);
        senderVO.setMass(mass);
        senderVO.setDocid(docId);
        senderVO.setSender(this.from);
        senderVO.setReceiverUserID(this.receiverUserId);
        return senderVO;
    }

    public String getApplication() {
        return this.application;
    }

    public String getDomainid() {
        return this.domainid;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public void setDomainid(String domainid) {
        this.domainid = domainid;
    }

    public boolean send(String docid, String subject, String content, BaseUser responsible) throws Exception {
        return send(docid, subject, content, responsible.getTelephone());
    }

    public boolean send(String docid, String subject, String content, String receiver) throws Exception {
        return send(docid, subject, content, receiver, new HashMap(), false);
    }

    public boolean send(String docid, String subject, String content, String receiver, String replyPrompt, String code, boolean mass) throws Exception {
        return send(0, docid, subject, content, receiver, replyPrompt, code, mass);
    }

    public boolean send(String docid, String subject, String content, String receiver, Map<String, String> defineReply, boolean mass) throws Exception {
        return send(0, docid, subject, content, receiver, defineReply, mass);
    }

    public String getReceiverUserId() {
        return this.receiverUserId;
    }

    public void setReceiverUserId(String receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public static void main(String[] args) {
        String content = "[通知]TH0004455单据审批超期，请及时处理。客户信息：广州国美，金额：20000，产品：热水器。";
        String receiver = "13725303059";
        String applicationid = "01b807a0-8c7f-3f80-aa27-c741f5c90259";
        String domainid = "01b807a9-22fc-0da0-8bf0-bec042dc9770";
        try {
            SMSMode sender = new SMSMode("", domainid, applicationid);
            Map map = new HashMap();
            map.put("收到", DeFineReplyContentUtil.getReplyCode(null, receiver, applicationid));
            sender.send("", content, receiver, map, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean send(SummaryCfgVO summaryCfg, Document doc, BaseUser responsible) throws Exception {
        return send(summaryCfg.getTitle(), summaryCfg, doc, responsible);
    }

    public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc, BaseUser responsible)
            throws Exception {
        return send(subject, summaryCfg, doc, responsible, false);
    }

    public boolean send(String subject, SummaryCfgVO summaryCfg, Document doc, BaseUser responsible, boolean approval)
            throws Exception {
        subject = subject + "[" + responsible.getName() + "]";

        if (doc == null) throw new Exception("Cound not send with null document");

        String content = summaryCfg.toText(doc);
        if (approval) {
            content = content + ".短信审批方式: 回复码:0(回退)或1(提交),如AA0001:0";
            String replyCode = DeFineReplyContentUtil.getReplyCode("", responsible.getTelephone(), this.application);
            return send(doc.getId(), subject, content, responsible.getTelephone(), "", replyCode, false);
        }
        return send(doc.getId(), subject, content, responsible.getTelephone());
    }

    public class SendSMSThread extends Thread {
        String flag = null;

        public SendSMSThread() {
        }

        public void run() {
            try {
                MessageManager manager = MessageManager.getInstance(new Validator(SMSMode.this.domainid, SMSMode.this.application));
                ISender sender = manager.getSender();
                if (sender != null) {
                    String contents = SMSMode.this.content + SMSMode.this.sign.trim();
                    contents = contents.replace('\n', ' ');
                    contents = contents.replace('\r', ' ');
                    contents = contents.replace("&#160;&#160;&#160;", " ");
                    contents = contents.replace("</br>", " ");
                    int result = -100;
                    if ((SMSMode.this.receiver != null) && (SMSMode.this.receiver.length == 1)) {
                        if (StringUtil.isBlank(SMSMode.this.replyCode))
                            result = sender.sendMessage(SMSMode.this.receiver[0], contents);
                        else
                            result = sender.sendMessage(SMSMode.this.receiver[0], contents, SMSMode.this.replyCode);
                    } else if ((SMSMode.this.receiver != null) && (SMSMode.this.receiver.length > 1)) {
                        if (StringUtil.isBlank(SMSMode.this.replyCode))
                            result = sender.battchSendMessage(SMSMode.this.receiver, contents);
                        else {
                            result = sender.battchSendMessage(SMSMode.this.receiver, contents, SMSMode.this.replyCode);
                        }
                    }
                    if (result < 0)
                        this.flag = "false";
                    else
                        this.flag = "true";
                }
            } catch (Exception e) {
                this.flag = "false";
                SMSMode.log.warn("##" + e.toString() + " ##");
            }
        }
    }
}