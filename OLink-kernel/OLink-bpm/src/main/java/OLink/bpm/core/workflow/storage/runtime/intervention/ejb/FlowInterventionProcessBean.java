package OLink.bpm.core.workflow.storage.runtime.intervention.ejb;

import java.util.Date;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.workflow.storage.runtime.intervention.dao.FlowInterventionDAO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.RuntimeDaoManager;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiVO;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;

/**
 * @author Happy
 * 
 */
public class FlowInterventionProcessBean extends
		AbstractRunTimeProcessBean<FlowInterventionVO> implements
		FlowInterventionProcess {

	public FlowInterventionProcessBean(String applicationId) {
		super(applicationId);
	}

	@Override
	protected IRuntimeDAO getDAO() throws Exception {
		RuntimeDaoManager runtimeDao = new RuntimeDaoManager();
		FlowInterventionDAO flowInterventionDAO = (FlowInterventionDAO) runtimeDao
				.getFlowInterventionDAO(getConnection(), getApplicationId());
		return flowInterventionDAO;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3862446754753478202L;

	public void doCreateByDocument(Document doc, WebUser user) throws Exception {
		if (doc.getFlowid() != null && doc.getFlowid().length() > 0) {
			FlowInterventionVO vo = new FlowInterventionVO();
			vo.setId(doc.getId());
			SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory
					.createProcess(SummaryCfgProcess.class);
			SummaryCfgVO summaryCfg = summaryCfgProcess
					.doViewByFormIdAndScope(doc.getFormid(),
							SummaryCfgVO.SCOPE_PENDING);
			if (summaryCfg != null) {
				vo.setSummary(summaryCfg.toSummay(doc, user));
			} else {
				vo.setSummary("");
			}
			vo.setInitiator(user.getName());
			vo.setLastAuditor(user.getName());
			vo.setApplicationid(doc.getApplicationid());
			vo.setFlowId(doc.getFlowid());
			vo.setDocId(doc.getId());
			vo.setDomainid(doc.getDomainid());
			vo.setFirstProcessTime(doc.getAuditdate());
			vo.setLastProcessTime(new Date());
			vo.setVersion(doc.getVersion());
			BillDefiVO flowvo = doc.getFlowVO();
			if (flowvo != null) {
				//flowvo.getSubject();
				if (flowvo.getSubject() != null)
					vo.setFlowName(flowvo.getSubject());
			}
			vo.setStateLabel(doc.getStateLabel());
			vo.setFormId(doc.getFormid());

			this.getDAO().create(vo);
		}
	}

	public void doUpdateByDocument(Document doc, WebUser user) throws Exception {
		if (doc.getFlowid() != null && doc.getFlowid().length() > 0) {
			FlowInterventionVO vo = (FlowInterventionVO) getDAO().find(
					doc.getId());
			if (vo != null) {
				SummaryCfgProcess summaryCfgProcess = (SummaryCfgProcess) ProcessFactory
						.createProcess(SummaryCfgProcess.class);
				SummaryCfgVO summaryCfg = summaryCfgProcess
						.doViewByFormIdAndScope(doc.getFormid(),
								SummaryCfgVO.SCOPE_PENDING);
				if (summaryCfg != null) {
					vo.setSummary(summaryCfg.toSummay(doc, user));
				} else {
					vo.setSummary("");
				}
				vo.setLastAuditor(user.getName());
				vo.setLastProcessTime(new Date());
				vo.setStateLabel(doc.getStateLabel());
				vo.setVersion(doc.getVersion());
				this.getDAO().update(vo);
			} else {// Add By XGY 20130604
				doCreateByDocument(doc, user);
			}
		}
	}

	@Override
	public DataPackage<FlowInterventionVO> doQuery(ParamsTable params,
												   WebUser user) throws Exception {
		if (getApplicationId() != null
				&& getApplicationId().trim().length() > 0) {
			return ((FlowInterventionDAO) this.getDAO()).queryByFilter(params,
					user);
		}
		return new DataPackage<FlowInterventionVO>();
	}

}
