package OLink.bpm.core.image.repository.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;

/**
 * 
 * @author Marky
 * 
 */
public interface ImageRepositoryDAO extends IDesignTimeDAO<ImageRepositoryVO> {

	/**
	 * 根据名称以及应用标识,返回图片库对象ImageRepositoryVO
	 * 
	 * @param name
	 *            名称
	 * @param application
	 *            应用标识
	 * @return 图片库值对象
	 * @throws Exception
	 */
	ImageRepositoryVO getImageRepositoryByName(String name,
											   String application) throws Exception;

	/**
	 * 根据所属模块以及应用标识,返回图片库集合
	 * 
	 * @param application
	 *            应用标识
	 * @param moduleid
	 *            所属模块主键
	 * @return 图片库集合
	 */
	Collection<ImageRepositoryVO> getImageRepositoryByModule(
			String moduleid, String application) throws Exception;

	/**
	 * 根据应用标识,返回图片库集合
	 * 
	 * @param application
	 *            应用标识
	 * @param applicationid
	 *            应用标识
	 * @return 图片库集合
	 */
	Collection<ImageRepositoryVO> getImageRepositoryByApplication(
			String applicationid, String application) throws Exception;

	/**
	 * 根据图片库内容地址以及应用标识,返回图片库值对象
	 * 
	 * @param IconURl
	 *            图片库内容地址
	 * @param application
	 *            应用标识
	 * @return 图片库值对象
	 * @throws Exception
	 */
	ImageRepositoryVO getImageRepositoryByIconURl(String IconURl,
												  String application) throws Exception;

}
