package OLink.bpm.core.image.repository.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.image.repository.ejb.ImageRepositoryVO;

public class HibernateImageRepositoryDAO extends HibernateBaseDAO<ImageRepositoryVO> implements
		ImageRepositoryDAO {
        /**
         * HibernateImageRepositoryDAO构造函数
         * @param voClassName
         */
	public HibernateImageRepositoryDAO(String voClassName) {
		super(voClassName);
	}
       /**
        * 根据图片库名以及应用标识,返回相应图片库的值对象
        * @param name 图片库名
        * @param application 应用标识
        * @return 图片库的值对象
        */
	public ImageRepositoryVO getImageRepositoryByName(String name,
			String application) throws Exception {
		String hql = "from ImageRepositoryVO rp where rp.name=" + "'" + name
				+ "'";

		if (application != null && application.length() > 0) {
			hql += (" and rp.applicationid = '" + application + "' ");
		}

		return (ImageRepositoryVO) this.getData(hql);
	}
	/**
	 * 根据所属模块以及应用标识,返回图片库集合
	 * @param application 应用标识
	 * @param moduleid 所属模块主键
	 * @return 图片库集合
	 */
	public Collection<ImageRepositoryVO> getImageRepositoryByModule(String moduleid,
			String application) throws Exception {

		String hql = "from ImageRepositoryVO sp where sp.module.id='"
				+ moduleid + "'";
		   ParamsTable params=new ParamsTable();
		   params.setParameter("application",application);
		   return getDatas(hql, params);
	}
	/**
	 * 根据应用标识,返回图片库集合
	 * @param application 应用标识
	 * @param applicationid   应用标识
	 * @return 图片库集合
	 */
	public Collection<ImageRepositoryVO> getImageRepositoryByApplication(String applicationid,
																		 String application) throws Exception {

		String hql = "from ImageRepositoryVO sp where (sp.applicationid='"
				+ applicationid + "'" + " ) "
				+ " and sp.module is null";
		   ParamsTable params=new ParamsTable();
		   params.setParameter("application",application);
		   return getDatas(hql, params);
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
		String hql = "from ImageRepositoryVO rp where content=" + "'" + IconURl
				+ "'";
		
		if (application != null && application.length()>0) {
			hql+=(" and rp.applicationid = '"+application+"' ");
		}
		
		return (ImageRepositoryVO) this.getData(hql);
	}
}
