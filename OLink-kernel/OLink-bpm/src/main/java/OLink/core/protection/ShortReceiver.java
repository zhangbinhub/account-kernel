package OLink.core.protection;

import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.shortmessage.received.ejb.ReceivedMessageProcess;
import OLink.bpm.core.shortmessage.received.ejb.ReceivedMessageVO;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageProcess;
import OLink.bpm.core.shortmessage.submission.ejb.SubmitMessageVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.sequence.Sequence;
import eWAP.core.SMSReceive;
import eWAP.core.SMSService;

import java.util.Collection;
import java.util.Date;

public final class ShortReceiver
        implements IReceiver
{
    private SMSService shortService = null;

    public ShortReceiver()
    {
        this.shortService = ShortSMSManager.getInstance();
    }

    public String receiveMessage() throws Exception
    {
        DomainProcess dprocess = (DomainProcess)
                ProcessFactory.createProcess(DomainProcess.class);
        Collection<DomainVO> domains = dprocess.getAllDomain();

        if (domains == null) {
            return null;
        }
        for (DomainVO domain : domains)
        {
            SMSReceive[] receiveMsgs = this.shortService.receiveMsgs(
                    domain.getSmsMemberCode(), domain.getSmsMemberPwd());

            if ((receiveMsgs == null) ||
                    (receiveMsgs.length == 0)) {
                continue;
            }
            SubmitMessageProcess process = (SubmitMessageProcess)
                    ProcessFactory.createProcess(SubmitMessageProcess.class);
            ReceivedMessageProcess rcprocess = (ReceivedMessageProcess)
                    ProcessFactory.createProcess(ReceivedMessageProcess.class);

            for (SMSReceive sr : receiveMsgs) {
                ReceivedMessageVO receiver = new ReceivedMessageVO();
                receiver.setId(Sequence.getSequence());
                receiver.setContent(sr.getMsgContent());
                receiver.setReceiveDate(sr.getSentTime());
                receiver.setReceiver(sr.getRecvtel());
                receiver.setSender(sr.getSrctermid());
                receiver.setStatus(0);
                receiver.setCreated(new Date());
                rcprocess.doCreate(receiver);

                String[] strs = receiver.getContent().split(":");

                SubmitMessageVO vo = process.getMessageByReplyCode(strs[0], receiver.getSender());
                if (vo != null) {
                    vo.setReply(true);
                    process.doUpdate(vo);
                    receiver.setDocid(vo.getDocid());

                    if (strs.length == 2) {
                        int tempInt = Integer.parseInt(strs[1]);
                        if ((tempInt == 1) || (tempInt == 0))
                            receiver.setStatus(0);
                        else
                            receiver.setStatus(1);
                    } else {
                        receiver.setStatus(1);
                    }

                    receiver.setParent(vo.getId());
                    receiver.setApplicationid(vo.getApplicationid());
                    receiver.setDomainid(vo.getDomainid());
                    rcprocess.doUpdate(receiver);
                }
            }
        }
        return null;
    }
}