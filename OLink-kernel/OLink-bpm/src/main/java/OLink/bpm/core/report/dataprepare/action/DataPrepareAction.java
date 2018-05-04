package OLink.bpm.core.report.dataprepare.action;

import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSourceProcess;
import OLink.bpm.core.report.dataprepare.ExecuteDataPrepare;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepareProcess;
import OLink.bpm.util.ProcessFactory;
import com.opensymphony.webwork.ServletActionContext;

public class DataPrepareAction extends BaseAction<DataPrepare> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String _dts;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public DataPrepareAction() throws Exception {
		super(ProcessFactory.createProcess(DataPrepareProcess.class),
				new DataPrepare());
	}

	public String doSave() {
		try {
			DataPrepare vo = (DataPrepare) getContent();
			if (_dts != null && _dts.trim().length() > 0) {
				DataSourceProcess dp = (DataSourceProcess) (ProcessFactory
						.createProcess(DataSourceProcess.class));
				DataSource dt = (DataSource) dp.doView(this._dts);
				vo.setDataSource(dt);
			}
			if (vo.getId() != null && vo.getId().trim().length() > 0) {
				DataPrepare po = (DataPrepare) process.doView(vo.getId());
				vo.setSqlSentences(po.getSqlSentences());
			}
			vo.setApplicationid(getApplication());
			setContent(vo);
			return super.doSave();
		} catch (Exception e) {
			this.addFieldError("1", e.getMessage());
			return INPUT;
		}
	}

	public String get_dts() {
		DataPrepare vo = (DataPrepare) getContent();
		return (vo != null && vo.getDataSource() != null) ? vo.getDataSource()
				.getId() : null;
	}

	public void set_dts(String _dts) {
		this._dts = _dts;
	}

	public String produceData() throws Exception {
		if (_selects != null) {
			String message = null;
			DataPrepareProcess dp = (DataPrepareProcess) (ProcessFactory
					.createProcess(DataPrepareProcess.class));
			for (int i = 0; i < _selects.length; i++) {
				String id = _selects[i];
				DataPrepare vo = (DataPrepare) dp.doView(id);
				message = ExecuteDataPrepare.execute(vo);
			}
			ServletActionContext.getRequest().setAttribute("message", message);
		}
		return SUCCESS;
	}

	public String clearData() throws Exception {
		if (_selects != null) {
			String message = null;
			DataPrepareProcess dp = (DataPrepareProcess) (ProcessFactory
					.createProcess(DataPrepareProcess.class));
			for (int i = 0; i < _selects.length; i++) {
				String id = _selects[i];
				DataPrepare vo = (DataPrepare) dp.doView(id);
				message = ExecuteDataPrepare.clearTempData(vo);
			}
			ServletActionContext.getRequest().setAttribute("message", message);
		}

		return SUCCESS;
	}

}
