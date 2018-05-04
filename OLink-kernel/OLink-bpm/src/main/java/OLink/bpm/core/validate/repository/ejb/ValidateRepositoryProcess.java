package OLink.bpm.core.validate.repository.ejb;

import java.util.Collection;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface ValidateRepositoryProcess extends IDesignTimeProcess<ValidateRepositoryVO> {
	/**
	 * 根据(模块)module对象主键标识,获取验证对象集合.
	 * 
	 * @param moduleid
	 *            Module对象标识
	 * @return 验证对象集合
	 * @throws Exception
	 */
	Collection<ValidateRepositoryVO> get_validate(String applicationid) throws Exception;
	
	/**
	 * 根据检验库名，判断名称是否唯一
	 * @param id
	 * @param name
	 * @param application
	 * @return 存在返回true,否则返回false
	 * @throws Exception
	 */
	boolean isValidateNameExist(String id, String name, String application)throws Exception;
}
