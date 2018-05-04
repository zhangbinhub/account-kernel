package OLink.bpm.core.resource.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import OLink.bpm.core.resource.dao.ResourceDAO;
import OLink.bpm.util.MobileMenuUtil;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.links.dao.LinkDAO;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.links.ejb.LinkVO;
import OLink.bpm.core.permission.ejb.PermissionPackage;
import OLink.bpm.core.permission.ejb.PermissionVO;
import OLink.bpm.util.StringUtil;
import eWAP.core.Tools;
import edu.emory.mathcs.backport.java.util.Arrays;

public class ResourceProcessBean extends
		AbstractDesignTimeProcessBean<ResourceVO> implements ResourceProcess {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5776735707845334604L;

	public void doCreate(ValueObject vo) throws Exception {
		ResourceVO res = (ResourceVO) vo;
		if (ResourceType.RESOURCE_TYPE_MOBILE.equals(res.getType())) {
			// 当手机菜单的上级为null时，如果当前菜单不是最顶级菜单，全部归到顶级菜单下面
			if (res.getSuperior() == null) {
				if (StringUtil.isBlank(res.getDescription())) {
					throw new Exception("{*[page.name.notexist]*}");
				} else if (!res.getDescription().trim().equals(
						MobileMenuUtil.DEFAULT_MENU_DES)) {
					ResourceVO superior = MobileMenuUtil
							.checkOrCreateMobileMenu(res.getApplicationid());
					res.setSuperior(superior);
				}
			}
		}
		super.doCreate(vo);
		PermissionPackage.clearCache();
	}

	public void doRemove(String pk) throws Exception {
		ResourceVO resource = (ResourceVO) getDAO().find(pk);
		Collection<ResourceVO> voList = ((ResourceDAO) getDAO())
				.getDatasByParent(resource.getId());
		if (voList != null && voList.size() > 0) {
			throw new ResourceException("(" + resource.getDescription()
					+ "){*[core.resource.hasuser]*}");
		}

		try {
			PersistenceUtils.beginTransaction();

			Collection<PermissionVO> permissions = resource
					.getRelatedPermissions();
			for (Iterator<PermissionVO> iterator = permissions.iterator(); iterator
					.hasNext();) {
				PermissionVO permission = iterator.next();
				permission.setRoleId("");
			}
			resource.getRelatedPermissions().clear();

			getDAO().remove(pk);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	public void doRemove(String[] pks) throws Exception {
		super.doRemove(pks);
		PermissionPackage.clearCache();
	}

	public void doUpdate(ValueObject vo) throws Exception {
		super.doUpdate(vo);
		PermissionPackage.clearCache();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ResourceProcess#getFamilyTree()
	 */
	public Collection<ResourceVO> getFamilyTree(String application)
			throws Exception {
		return ((ResourceDAO) getDAO()).getFamilyTree("", application);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcessBean#getDAO()
	 */
	// @SuppressWarnings("unchecked")
	protected IDesignTimeDAO<ResourceVO> getDAO() throws Exception {
		IDesignTimeDAO<ResourceVO> dao = (ResourceDAO) DAOFactory
				.getDefaultDAO(ResourceVO.class.getName());
		return dao;
	}

	public Map<String, String> deepSearchMenuTree(Collection<ResourceVO> cols,
			ResourceVO startNode, String excludeNodeId, int deep)
			throws Exception {
		Map<String, String> list = new LinkedHashMap<String, String>();

		list.put("", "{*[NO]*}");

		String prefix = "|------------------------------------------------";
		if (startNode != null) {
			list.put(startNode.getId(), prefix.substring(0, deep * 2)
					+ startNode.getDescription());
		}

		Iterator<ResourceVO> iter = cols.iterator();
		while (iter.hasNext()) {
			ResourceVO vo = iter.next();
			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchMenuTree(cols, vo,
								excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			} else {
				if (vo.getSuperior() != null
						&& vo.getSuperior().getId().equals(startNode.getId())) {
					if (vo.getId() != null && !vo.getId().equals(excludeNodeId)) {
						Map<String, String> tmp = deepSearchMenuTree(cols, vo,
								excludeNodeId, deep + 1);
						list.putAll(tmp);
					}
				}
			}
		}
		return list;
	}

	public Collection<ResourceVO> deepSearchResouece(
			Collection<ResourceVO> colls, ResourceVO startNode,
			String excludeNodeId, int deep) throws Exception {
		Collection<ResourceVO> rtn = new ArrayList<ResourceVO>();

		Iterator<ResourceVO> iter = colls.iterator();
		while (iter.hasNext()) {
			ResourceVO vo = iter.next();
			if (startNode == null) {
				if (vo.getSuperior() == null) {
					if (deep == 1) {
						rtn.add(vo);
					} else if (vo.getId() != null
							&& !vo.getId().equals(excludeNodeId)) {
						rtn.add(vo);
						Collection<ResourceVO> idList = deepSearchResouece(
								colls, vo, excludeNodeId, deep - 1);
						rtn.addAll(idList);
					}
				}
			} else {
				if (vo.getSuperior() != null
						&& vo.getSuperior().getId().equals(startNode.getId())) {
					if (deep == 1) {
						rtn.add(vo);
					} else if (vo.getId() != null
							&& !vo.getId().equals(excludeNodeId)) {
						rtn.add(vo);
						Collection<ResourceVO> idList = deepSearchResouece(
								colls, vo, excludeNodeId, deep - 1);
						rtn.addAll(idList);
					}
				}
			}
		}

		return rtn;
	}

	public Collection<ResourceVO> doSimpleQuery(ParamsTable params,
			String application) throws Exception {
		return super.doSimpleQuery(params, application);
	}

	public Collection<ResourceVO> getProtectResources(String application)
			throws Exception {
		return ((ResourceDAO) getDAO()).getProtectResources(application);
	}

	public Collection<ResourceVO> getTopProtectResources(String application)
			throws Exception {
		return ((ResourceDAO) getDAO()).getTopProtectResources(application);
	}

	public Collection<ResourceVO> getTopResources(String application)
			throws Exception {
		return ((ResourceDAO) getDAO()).queryTopResources(application);
	}

	public ResourceVO getResourceByName(String description, String application)
			throws Exception {
		String hql = "FROM " + ResourceVO.class.getName() + " vo ";
		hql += " WHERE vo.applicationid='" + application + "'";
		hql += " AND vo.description='" + description + "'";
		return (ResourceVO) getDAO().getData(hql);
	}

	public ResourceVO getTopResourceByName(String description,
			String application) throws Exception {
		String hql = "FROM " + ResourceVO.class.getName() + " vo ";
		hql += " WHERE vo.applicationid='" + application + "'";
		hql += " AND vo.superior is null AND vo.description='" + description
				+ "'";
		Collection<ResourceVO> cols = getDAO().getDatas(hql);

		if (cols != null) {
			Iterator<ResourceVO> its = cols.iterator();
			if (its != null) {
				while (its.hasNext()) {
					Object obj = its.next();
					if (obj instanceof ResourceVO)
						return (ResourceVO) obj;
				}
			}
		}
		return null;
	}

	public ResourceVO getResourceByViewId(String viewId, String application)
			throws Exception {
		return ((ResourceDAO) getDAO()).getResourceByViewId(
				viewId, application);
	}

	public void copyResource(String applicationid, String originid,
			String destid) throws Exception {
		ResourceVO origin = (ResourceVO) doView(originid);
		ResourceVO dest = (ResourceVO) doView(destid);
		MobileMenuUtil.createFromResource(origin, dest, applicationid);
	}

	/**
	 * 删除所有引用视图对象集合viewlist中的视图对象的菜单
	 * 
	 * @param viewlist
	 *            视图对象集合
	 * @throws Exception
	 */
	public void doRemoveByViewList(Collection<View> viewlist, String application)
			throws Exception {
		((ResourceDAO) getDAO()).removeByViewList(viewlist, application);
	}

	/**
	 * 获得上级下孩子集合
	 */
	public Collection<ResourceVO> doGetDatasByParent(String parent)
			throws Exception {
		return ((ResourceDAO) getDAO()).getDatasByParent(parent);
	}

	/**
	 * 创建菜单
	 * 
	 * @throws Exception
	 * @throws Exception
	 * @throws Exception
	 */
	public void doCreateMenu(ResourceVO resourceVO) {
		try {
			PersistenceUtils.beginTransaction();
			if (resourceVO != null) {
				LinkVO linkVo = resourceVO.getLink();
				if (linkVo != null) {
					if (linkVo.getId() == null
							|| linkVo.getId().trim().length() == 0) {
						linkVo.setId(Tools.getSequence());
					}
					if (linkVo.getSortId() == null
							|| linkVo.getSortId().trim().length() == 0) {
						linkVo.setSortId(Tools.getTimeSequence());
					}
				}
				if (resourceVO.getId() == null
						|| resourceVO.getId().trim().length() == 0) {
					resourceVO.setId(Tools.getSequence());
				}
				if (resourceVO.getSortId() == null
						|| resourceVO.getSortId().trim().length() == 0) {
					resourceVO.setSortId(Tools.getTimeSequence());
				}
				LinkDAO linkDao = (LinkDAO) DAOFactory
						.getDefaultDAO(LinkVO.class.getName());
				linkDao.create(linkVo);
			}
			getDAO().create(resourceVO);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			try {
				PersistenceUtils.rollbackTransaction();
				throw e;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			try {
				PersistenceUtils.closeSession();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public Collection<ResourceVO> doQueryBySQL(String sql) throws Exception {
		return this.getDAO().getDatasBySQL(sql);
	}

	@SuppressWarnings("unchecked")
	public void copyResources(String applicationid, String[] originids,
			String destid) throws Exception {
		if (originids == null) {
			return;
		}
		if (destid == null) {
			return;
		}
		// List<String> res_ids = Arrays.asList(originids);
		// 将originids转化为List
		final List<String> copies_ids = Arrays.asList(originids);
		// 在originids范围内的所有顶级菜单
		List<String> ids = new ArrayList<String>();
		int size = copies_ids.size();
		ResourceVO dest = (ResourceVO) doView(destid);
		// 初始化ids顶级菜单集
		for (int i = 0; i < size; i++) {
			String topSuperiorId = getTopSuperior(copies_ids.get(i), copies_ids);
			if (!ids.contains(topSuperiorId)) {
				ids.add(topSuperiorId);
			}
		}
		// 创建ids集的mobile菜单
		for (int i = 0; i < ids.size(); i++) {
			ResourceVO origin = (ResourceVO) doView(ids.get(i));
			MobileMenuUtil.createFromResource(origin, dest, applicationid,
					true, copies_ids);
		}
	}

	private String getTopSuperior(final String resId, List<String> all_ids)
			throws Exception {
		ResourceVO rvo = (ResourceVO) doView(resId);
		ResourceVO superior = rvo.getSuperior();
		if (superior == null) {
			return resId;
		} else {
			String superiorId = superior.getId();
			if (!all_ids.contains(superiorId)) {
				return resId;
			} else {
				return getTopSuperior(superiorId, all_ids);
			}
		}
	}
}
