package OLink.core.protection;

import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import eWAP.core.SMSService;

public final class ShortSender implements ISender {
    private MessageManager manager = MessageManager.getInstance();
    private SMSService shortService = null;

    public ShortSender() {
        this.shortService = ShortSMSManager.getInstance();
    }

    public int battchSendMessage(String[] recTels, String content) throws Exception {
        if ((recTels == null) || (recTels.length <= 0)) {
            throw new Exception("No Receiver!");
        }
        StringBuffer telephones = new StringBuffer();
        for (int i = 0; i < recTels.length; i++) {
            if (StringUtil.isBlank(recTels[i])) {
                continue;
            }
            telephones.append(recTels[i]).append(",");
        }
        if (StringUtil.isBlank(telephones.toString())) {
            throw new Exception("No Receiver!");
        }

        return sendMessage(telephones.substring(0, telephones.length() - 1),
                content);
    }

    public int battchSendMessage(String[] recTels, String content, String replyCode) throws Exception {
        if ((recTels == null) || (recTels.length <= 0)) {
            throw new Exception("No Receiver!");
        }
        StringBuffer telephones = new StringBuffer();
        for (int i = 0; i < recTels.length; i++) {
            if (StringUtil.isBlank(recTels[i])) {
                continue;
            }
            telephones.append(recTels[i]).append(",");
        }
        if (StringUtil.isBlank(telephones.toString())) {
            throw new Exception("No Receiver!");
        }

        return sendMessage(telephones.substring(0, telephones.length() - 1),
                content, replyCode);
    }

    public int sendMessage(String recTelephone, String content) throws Exception {
        if (this.manager.getValidator() == null)
            throw new Exception("无效的验证对象!");
        DomainProcess process = (DomainProcess)
                ProcessFactory.createProcess(DomainProcess.class);
        DomainVO vo = (DomainVO) process.doView(this.manager.getValidator().getDomainid());

        return this.shortService.sendMsg(vo.getSmsMemberCode(), vo.getSmsMemberPwd(),
                recTelephone, content);
    }

    public int sendMessage(String recTelephone, String content, String replyCode) throws Exception {
        if (this.manager.getValidator() == null)
            throw new Exception("无效的验证对象!");
        DomainProcess process = (DomainProcess)
                ProcessFactory.createProcess(DomainProcess.class);
        DomainVO vo = (DomainVO) process.doView(this.manager.getValidator().getDomainid());

        return this.shortService.sendMsg(vo.getSmsMemberCode(), vo.getSmsMemberPwd(),
                recTelephone, content, replyCode);
    }
}