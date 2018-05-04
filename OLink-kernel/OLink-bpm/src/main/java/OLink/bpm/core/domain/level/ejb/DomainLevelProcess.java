package OLink.bpm.core.domain.level.ejb;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface DomainLevelProcess extends IDesignTimeProcess<DomainLevelVO> {
	/**
	 * 根据域等级对象的名称取域等级对象
	 * 
	 * @param tempname
	 *            域等级对象的名称
	 * @return 域等级对象
	 * @throws Exception
	 */
	DomainLevelVO getRateByName(String tempname) throws Exception;
}
