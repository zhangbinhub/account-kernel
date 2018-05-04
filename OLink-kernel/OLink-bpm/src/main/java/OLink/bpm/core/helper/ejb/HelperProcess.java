package OLink.bpm.core.helper.ejb;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface HelperProcess extends IDesignTimeProcess<HelperVO>{
	
	HelperVO getHelperByName(String urlname, String application) throws Exception;
}
