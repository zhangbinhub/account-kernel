package OLink.bpm.core.report.dataprepare.action;

import java.util.Collection;

import OLink.bpm.core.report.dataprepare.ejb.DataPrepare;
import OLink.bpm.core.report.dataprepare.ejb.DataPrepareProcess;
import OLink.bpm.util.ProcessFactory;

public class DataPrepareHepler {
	public Collection<DataPrepare> getAllDataPrepareByApplication(String applicationid)throws Exception{
		DataPrepareProcess dp = (DataPrepareProcess) (ProcessFactory
				.createProcess(DataPrepareProcess.class));
		return dp.getAllDataPrepareByApplication(applicationid);
	}
}
