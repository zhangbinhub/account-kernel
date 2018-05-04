package OLink.bpm.core.logger.action;

import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.domain.ejb.DomainProcess;
import OLink.bpm.core.logger.ejb.LogProcess;
import OLink.bpm.core.logger.ejb.LogVO;
import OLink.bpm.util.ProcessFactory;
import org.apache.log4j.Logger;

public class LogAction extends BaseAction<LogVO> {

	private static final long serialVersionUID = 8435578592541938310L;
	
	private static final Logger LOG = Logger.getLogger(LogAction.class);
	
	/**
	 * 默认构造方法
	 * @SuppressWarnings ProcessFactory.createProcess不支持泛型
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public LogAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(LogProcess.class), new LogVO());
	}
	
	@Override
	public String doList() {
		try {
			this.validateQueryParams();
			setDatas(((LogProcess)process).getLogsByDomain(getParams(), getUser()));
			//setDatas(((LogProcess)process).doQuery(getParams(), getUser()));
		} catch (Exception e) {
			LOG.error("doList", e);
			addFieldError("1", e.getMessage());
			return INPUT;
		}

		return SUCCESS;
	}
	
	@Override
	public String doDelete() {
		return super.doDelete();
	}
	
	public String get_log() {
		try {
			DomainProcess process = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
			DomainVO domain = (DomainVO) process.doView(getParams().getParameterAsString("domain"));
			if (domain != null && domain.getLog() != null) {
				return domain.getLog().toString();
			}
		} catch (Exception e) {
		}
		return "false";
	}
	
	private String _log = "false";
	
	public void set_log(String _log) {
		this._log = _log;
	}
	
	@Override
	public String doSave() {
		try {
			DomainProcess process = (DomainProcess) ProcessFactory.createProcess(DomainProcess.class);
			DomainVO domain = (DomainVO) process.doView(getParams().getParameterAsString("domain"));
			if (domain != null) {
				domain.setLog(Boolean.parseBoolean(_log));
				process.doUpdate(domain);
				this.addActionMessage("{*[Save_Success]*}");
			}
		} catch (Exception e) {
			LOG.warn(e);
			this.addFieldError("1", e.getMessage());
		}
		return SUCCESS;
	}
	
	private static final String QUERY_REGEX = "[!$^&*()+=|{}';'\",<>/?~！#￥%……&*（）——|{}【】‘；：”“'。，、？]";
	
	@Override
	protected String getQueryRegex() {
		// 涉及到IP
		return QUERY_REGEX;
	}
	
}
