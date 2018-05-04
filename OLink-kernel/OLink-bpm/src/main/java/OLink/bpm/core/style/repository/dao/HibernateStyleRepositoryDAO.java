package OLink.bpm.core.style.repository.dao;

import java.util.Collection;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.style.repository.ejb.StyleRepositoryVO;

/**
 * 
 * @author Marky
 * 
 */
public class HibernateStyleRepositoryDAO extends HibernateBaseDAO<StyleRepositoryVO> implements StyleRepositoryDAO {
	/**
	 * HibernateStyleRepositoryDAO构造函数
	 * 
	 * @see HibernateBaseDAO#_voClazzName
	 * @param voClassName
	 *            值对象类名
	 */
	public HibernateStyleRepositoryDAO(String voClassName) {
		super(voClassName);
	}

	/**
	 * 根据样式库名以及应用标识,返回样式库对象StyleRepositoryVO
	 * 
	 * @param name
	 *            样式库名
	 * @param application
	 *            应用标识
	 * @return 样式库对象StyleRepositoryVO
	 * @throws Exception
	 */
	public StyleRepositoryVO getRepositoryByName(String name, String application) throws Exception {
		String hql = "from StyleRepositoryVO sp where sp.name=" + "'" + name + "'";

		if (application != null && application.length() > 0) {
			hql += (" and vo.applicationid = '" + application + "' ");
		}

		return (StyleRepositoryVO) this.getData(hql);
	}

	/**
	 * 根据模块(module)主键(id)以及应用标识查找,返回相应样式库(StyleRepository)集合
	 * 
	 * @param moduleid
	 *            模块主键
	 * @param application
	 *            应用标识
	 * @return 样式库(StyleRepository)集合
	 * @throws Exception
	 */
	public Collection<StyleRepositoryVO> getStyleRepositoryByModule(String moduleid, String application)
			throws Exception {

		String hql = "from StyleRepositoryVO sp ";
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	/**
	 * 根据应用标识,返回相应样式库(StyleRepository)集合.
	 * 
	 * @param application
	 *            应用标识
	 * @return 样式库(StyleRepository)集合
	 * @throws Exception
	 */
	public Collection<StyleRepositoryVO> getStyleRepositoryByApplication(String applicationid) throws Exception {

		String hql = "from StyleRepositoryVO sp where (sp.applicationid='" + applicationid + "' ) ";
		return getDatas(hql);
	}

	/**
	 * 根据样式名，判断名称是否唯一
	 * 
	 * @param name
	 * @param application
	 * @return 存在返回true,否则返回false
	 * @throws Exception
	 */
	public boolean isStyleNameExist(String id, String name, String application) throws Exception {
		String hql = "from StyleRepositoryVO rp where rp.name=" + "'" + name + "'";
		if (application != null && application.length() > 0) {
			hql += (" and rp.applicationid = '" + application + "' ");
		}

		if (id != null && !id.equals("")) {
			hql += (" and rp.id !='" + id + "' ");
		}

		if (this.getData(hql) != null) {
			throw new Exception("{*[duplicate_name]*}");
		} else
			return false;
	}
}
