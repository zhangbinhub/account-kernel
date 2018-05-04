package OLink.bpm.core.image.repository.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.dynaform.form.action.ImpropriateException;
import OLink.bpm.core.image.repository.dao.ImageRepositoryDAO;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * 
 * @author Marky
 *
 */
public class ImageRepositoryProcessBean extends AbstractDesignTimeProcessBean<ImageRepositoryVO> implements
		ImageRepositoryProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4520257281463865197L;
	protected IDesignTimeDAO<ImageRepositoryVO> getDAO() throws Exception {
		return (ImageRepositoryDAO) DAOFactory.getDefaultDAO(ImageRepositoryVO.class.getName());
	}
	/**
         * 根据名称以及应用标识,返回图片库对象ImageRepositoryVO
         * @param name 名称
         * @param application  应用标识
         * @return 图片库值对象 
         * @throws Exception
         */
	public ImageRepositoryVO getImageRepositoryByName(String name,
			String application) throws Exception {
		return ((ImageRepositoryDAO) getDAO()).getImageRepositoryByName(name, application);

	}
	/**
	 * 根据所属模块以及应用标识,返回图片库集合
	 * @param application 应用标识
	 * @param moduleid 所属模块主键
	 * @return 图片库集合
	 */
	public Collection<ImageRepositoryVO> getImageRepositoryByModule(String moduleid,
			String application) throws Exception {
		return ((ImageRepositoryDAO) getDAO())
				.getImageRepositoryByModule(moduleid, application);
	}
	/**
	 * 根据应用标识,返回图片库集合
	 * @param application 应用标识
	 * @param applicationid   应用标识
	 * @return 图片库集合
	 */
	public Collection<ImageRepositoryVO> getImageRepositoryByApplication(String applicationid,
			String application) throws Exception {
		return ((ImageRepositoryDAO) getDAO())
				.getImageRepositoryByApplication(applicationid, application);
	}
	/**
	 * 根据图片库内容地址以及应用标识,返回图片库值对象
	 * @param IconURl 图片库内容地址
	 * @param application 应用标识
	 * @return 图片库值对象
	 * @throws Exception
	 */
	public ImageRepositoryVO getImageRepositoryByIconURl(String IconURl,
			String application) throws Exception {
		return ((ImageRepositoryDAO) getDAO())
				.getImageRepositoryByIconURl(IconURl, application);
	}
         /**
          * @param vo 值对象
          * 更新图片库
          */
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			ValueObject po = getDAO().find(vo.getId());
			if (po != null) {
				if (vo.getVersion() != po
						.getVersion())
					throw new ImpropriateException("{*[core.util.cannotsave]*}");
				vo.setVersion(vo
						.getVersion() + 1);
				PropertyUtils.copyProperties(po, vo);
				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}

			PersistenceUtils.commitTransaction();
		} catch (ImpropriateException e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

}
