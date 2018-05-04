package OLink.bpm.core.usersetup.ejb;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface UserSetupProcess extends IDesignTimeProcess<UserSetupVO> {
	
	
	UserSetupVO getUserSetupByUserId(String uId) throws Exception;
	
}
