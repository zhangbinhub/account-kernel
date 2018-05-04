package OLink.bpm.core.dynaform.pending.action;

import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcess;
import OLink.bpm.core.homepage.ejb.Reminder;
import OLink.bpm.base.action.AbstractRunTimeAction;
import OLink.bpm.core.dynaform.pending.ejb.PendingProcessBean;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.dynaform.pending.ejb.PendingVO;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;

public class PendingAction extends AbstractRunTimeAction<PendingVO> {

	private static final long serialVersionUID = -8862606885675120112L;

	private Reminder reminder;
	
	private SummaryCfgVO summaryCfg;

	public Reminder getReminder() {
		return reminder;
	}

	public void setReminder(Reminder reminder) {
		this.reminder = reminder;
	}
	
	

	public SummaryCfgVO getSummaryCfg() {
		return summaryCfg;
	}

	public void setSummaryCfg(SummaryCfgVO summaryCfg) {
		this.summaryCfg = summaryCfg;
	}

	public String doList() {
		try {
//			ReminderProcess reminderProcess = (ReminderProcess) ProcessFactory.createProcess(ReminderProcess.class);
			
			SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);

			ParamsTable params = getParams();
			String summaryCfgId = (String) params.getParameter("summaryCfgId");
			summaryCfg = (SummaryCfgVO) summaryCfgProcess.doView(summaryCfgId);
			setSummaryCfg(summaryCfg);
			if(summaryCfg != null){
				params.setParameter("formid", summaryCfg.getFormId());
				params.setParameter("_orderby", summaryCfg.getOrderby());
				setDatas(((PendingProcess) getProcess()).doQueryByFilter(params, getUser()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("Pending list error", e.getMessage());
		}
		return SUCCESS;
	}

	public IRunTimeProcess<PendingVO> getProcess() {
		return new PendingProcessBean(getApplication());
	}

}
