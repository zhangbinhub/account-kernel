package OLink.bpm.core.report.dataprepare.action;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepareProcess;
import OLink.bpm.core.report.dataprepare.ejb.SqlSentenceProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.report.dataprepare.ejb.SqlSentence;

public class SqlSentenceAction extends BaseAction<SqlSentence> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String _dataprepare;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public SqlSentenceAction() throws Exception {
		super(ProcessFactory.createProcess(SqlSentenceProcess.class), new SqlSentence());
	}

	public String doSave() {
		try {
			SqlSentence vo = (SqlSentence) getContent();
			if (_dataprepare != null && _dataprepare.trim().length() > 0) {
				DataPrepareProcess dp = (DataPrepareProcess) (ProcessFactory.createProcess(DataPrepareProcess.class));
				DataPrepare dt = (DataPrepare) dp.doView(this._dataprepare);
				vo.setDataPrepare(dt);
			}
			vo.setApplicationid(getApplication());
			setContent(vo);
			return super.doSave();
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String doList() {
		try {
			String _dataprepare = getParams().getParameterAsString("_dataprepare");
			DataPackage<SqlSentence> datas = ((SqlSentenceProcess) process).getSqlSentenceByDataPrepare(_dataprepare);
			setDatas(datas);
			return SUCCESS;
		} catch (Exception e) {
			e.printStackTrace();
			addFieldError("", e.getMessage());
			return INPUT;
		}

	}

	public String get_dataprepare() {
		SqlSentence vo = (SqlSentence) getContent();
		return vo != null && vo.getDataPrepare() != null ? vo.getDataPrepare().getId() : null;
	}

	public void set_dataprepare(String _dataprepare) {
		this._dataprepare = _dataprepare;
	}
}
