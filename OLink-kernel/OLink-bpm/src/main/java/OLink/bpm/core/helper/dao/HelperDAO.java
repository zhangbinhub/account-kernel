package OLink.bpm.core.helper.dao;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.helper.ejb.HelperVO;

public interface HelperDAO extends IDesignTimeDAO<HelperVO> {
	HelperVO getHelperByName(String urlname, String application) throws Exception;

}
