package OLink.bpm.core.report.crossreport.runtime.action;

import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportProcess;
import OLink.bpm.core.report.crossreport.definition.ejb.CrossReportVO;
import OLink.bpm.util.ProcessFactory;


public class RuntimeDetailFrontAction extends RuntimeDetailAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public RuntimeDetailFrontAction() throws ClassNotFoundException {
		super(ProcessFactory.createProcess(CrossReportProcess.class), new CrossReportVO());
	}
}
