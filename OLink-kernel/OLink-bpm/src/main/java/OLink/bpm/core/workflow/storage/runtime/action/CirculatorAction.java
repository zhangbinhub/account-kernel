package OLink.bpm.core.workflow.storage.runtime.action;

import OLink.bpm.base.action.AbstractRunTimeAction;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.IRunTimeProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.core.workflow.storage.runtime.ejb.CirculatorProcessBean;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.CirculatorProcess;
import OLink.bpm.core.workflow.storage.runtime.ejb.Circulator;

/**
 * @author happy
 *
 */
public class CirculatorAction extends AbstractRunTimeAction<Circulator> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5449790888013032605L;
	
	private SummaryCfgVO summaryCfg;

	public SummaryCfgVO getSummaryCfg() {
		return summaryCfg;
	}

	public void setSummaryCfg(SummaryCfgVO summaryCfg) {
		this.summaryCfg = summaryCfg;
	}

	@Override
	public IRunTimeProcess<Circulator> getProcess() {
		return new CirculatorProcessBean(getApplication());
	}
	
	
	public String doList() {
		try {
			SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory.createProcess(SummaryCfgProcess.class);

			ParamsTable params = getParams();
			String summaryCfgId = (String) params.getParameter("summaryCfgId");
			summaryCfg = (SummaryCfgVO) summaryCfgProcess.doView(summaryCfgId);
			setSummaryCfg(summaryCfg);
			if(summaryCfg != null){
				params.setParameter("formid", summaryCfg.getFormId());
				//params.setParameter("_orderby", summaryCfg.getOrderby());
				setDatas(((CirculatorProcess) getProcess()).getPendingByUser(params, getUser()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("Pending list error", e.getMessage());
		}
		return SUCCESS;
	}
	
	
	public String doWorkList() {
		try {
			ParamsTable params = getParams();
			setDatas(((CirculatorProcess) getProcess()).getWorksByUser(params, getUser()));
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("Pending list error", e.getMessage());
		}
		return SUCCESS;
	}
	


}
