package OLink.bpm.core.dynaform.summary.action;

import java.util.Collection;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgProcess;
import OLink.bpm.core.dynaform.summary.ejb.SummaryCfgVO;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.action.ParamsTable;

/**
 * @author Happy
 *
 */
public class SummaryCfgAction extends BaseAction<SummaryCfgVO> {
	private static final long serialVersionUID = 5617087045674948355L;

	@SuppressWarnings("unchecked")
	public SummaryCfgAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(SummaryCfgProcess.class), new SummaryCfgVO());
	}

	@Override
	public String doSave() {
		SummaryCfgVO vo = (SummaryCfgVO) this.getContent();
		if(vo.getScope()==SummaryCfgVO.SCOPE_PENDING){
			try {
				SummaryCfgVO that = ((SummaryCfgProcess)process).doViewByFormIdAndScope(vo.getFormId(), SummaryCfgVO.SCOPE_PENDING);
				if(that != null && !that.getId().equals(vo.getId())){
					throw new Exception("作用域为代办的摘要信息已经存在！");
				}
			} catch (Exception e) {
				this.addFieldError("error", e.getMessage());
				return INPUT;
			}
		}
		
		try {
			ParamsTable p = new ParamsTable();
			p.setParameter("t_title", vo.getTitle());
			//p.setParameter("t_application", vo.getApplicationid());
			Collection<SummaryCfgVO> scvo_list = process
					.doSimpleQuery(p, vo.getApplicationid());
			if (!scvo_list.isEmpty()) {
				SummaryCfgVO scvo = scvo_list.iterator().next();
				if (!scvo.getId().equals(vo.getId())) {
					throw new Exception("标题已存在！");
				}
			}
			// if(vo.getId() == null &&
			// ((SummaryCfgProcess)process).isExistWithSameTitle(vo.getTitle(),
			// vo.getApplicationid())){
			// throw new Exception("标题已存在！");
			// }
		} catch (Exception e) {
			this.addFieldError("error", e.getMessage());
			return INPUT;
		}
		return super.doSave();
	}

	@Override
	public String doNew() {
		SummaryCfgVO vo = (SummaryCfgVO)this.getContent();
		if(StringUtil.isBlank(vo.getType())){
			vo.setType("00");
		}
		return super.doNew();
	}

	public String doHomepageList() {
		try {
			this.validateQueryParams();
			datas = ((SummaryCfgProcess)this.process).doQueryHomePageSummaryCfgs(getParams());
		} catch (Exception e) {
			this.addFieldError("", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}

}
