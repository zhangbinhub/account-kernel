package OLink.bpm.core.report.query.action;

import OLink.bpm.core.report.query.ejb.ParameterProcess;
import OLink.bpm.util.ProcessFactory;
import OLink.bpm.base.action.BaseAction;
import OLink.bpm.core.report.query.ejb.Parameter;

public class ParameterAction extends BaseAction<Parameter> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @SuppressWarnings 工厂方法不支持泛型
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public ParameterAction() throws Exception {
		super(ProcessFactory.createProcess(ParameterProcess.class), new Parameter());
	}
}
