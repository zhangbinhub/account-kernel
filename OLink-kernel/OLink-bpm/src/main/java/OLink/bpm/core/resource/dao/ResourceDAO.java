package OLink.bpm.core.resource.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.resource.ejb.ResourceVO;

public interface ResourceDAO extends IDesignTimeDAO<ResourceVO> {

	/**
	 * Get the family tree.
	 * 
	 * @param parent
	 *            The parent resouce.
	 * @return The family tree.
	 * @throws Exception
	 */
	Collection<ResourceVO> getFamilyTree(String parent, String application)
			throws Exception;

	Collection<ResourceVO> getProtectResources(String application) throws Exception;

	Collection<ResourceVO> getTopProtectResources(String application)
			throws Exception;

	Collection<ResourceVO> queryTopResources(String application) throws Exception;

	ResourceVO getResourceByViewId(String viewId, String application)
			throws Exception;

	Collection<ResourceVO> getDatasByParent(String parent) throws Exception;
	/**
	 * 删除所有引用视图对象集合vos中的视图对象的菜单
	 * 
	 * @param vos 视图对象集合
	 * @throws Exception
	 */
	void removeByViewList(Collection<View> vos, String application) throws Exception;
}
