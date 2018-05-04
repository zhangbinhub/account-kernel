package OLink.bpm.core.report.dataprepare.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface DataPrepareProcess extends IDesignTimeProcess<DataPrepare> {
	Collection<DataPrepare> getAllDataPrepareByApplication(String applicationid)throws Exception;
}
