package OLink.bpm.core.domain.level.dao;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.domain.level.ejb.DomainLevelVO;

public interface DomainLevelDAO extends IDesignTimeDAO<DomainLevelVO> {

	DomainLevelVO getRateByName(String tempname) throws Exception;

}
