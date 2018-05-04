package OLink.bpm.base.ejb;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.util.StringUtil;
import org.apache.commons.beanutils.PropertyUtils;

import eWAP.core.Tools;

public abstract class AbstractDesignTimeProcessBean<E> implements IDesignTimeProcess<E> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 87703310202408385L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doCreate(ValueObject )
	 */
	public void doCreate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			if (vo.getId() == null || vo.getId().trim().length() == 0) {
				vo.setId(Tools.getSequence());
			}

			if (vo.getSortId() == null || vo.getSortId().trim().length() == 0) {
				vo.setSortId(Tools.getTimeSequence());
			}

			getDAO().create(vo);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}

	public boolean checkExitName(String name, String application) throws Exception {
		try {
			ValueObject po = getDAO().findByName(name, application);
			return po != null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doCreate(ValueObject
	 *      [])
	 */
	public void doCreate(ValueObject[] vos) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			if (vos != null)
				for (int i = 0; i < vos.length; i++) {
					ValueObject vo = vos[i];

					if (vo.getId() == null || vo.getId().trim().length() == 0) {
						vo.setId(Tools.getSequence());
					}

					if (vo.getSortId() == null || vo.getSortId().trim().length() == 0) {
						vo.setSortId(Tools.getTimeSequence());
					}
					getDAO().create(vo);
				}
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doCreate(java.util.Collection)
	 */
	public void doCreate(Collection<ValueObject> vos) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			if (vos != null)
				for (Iterator<ValueObject> iter = vos.iterator(); iter.hasNext();) {
					ValueObject vo = iter.next();
					if (vo.getId() == null || vo.getId().trim().length() == 0) {
						vo.setId(Tools.getSequence());
					}

					if (vo.getSortId() == null || vo.getSortId().trim().length() == 0) {
						vo.setSortId(Tools.getTimeSequence());
					}
					getDAO().create(vo);
				}
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcess#doCreate(ValueObject,
	 *      WebUser)
	 */
	public void doCreate(ValueObject vo, WebUser user) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			if (vo.getId() == null || vo.getId().trim().length() == 0) {
				vo.setId(Tools.getSequence());
			}

			if (vo.getSortId() == null || vo.getSortId().trim().length() == 0) {
				vo.setSortId(Tools.getTimeSequence());
			}
			getDAO().create(vo);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcess#doRemove(java.lang.String)
	 */
	public void doRemove(String pk) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			getDAO().remove(pk);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doRemove(java.lang.String[])
	 */
	public void doRemove(String[] pks) throws Exception {
		try {
			StringBuffer errorMsg = new StringBuffer();
			if (pks != null && pks.length > 0) {
				for (int i = 0; i < pks.length; i++) {
					try {
						doRemove(pks[i]);
					} catch (Exception e) {
						errorMsg.append(e.getMessage() + ";");
					}
				}
				if (errorMsg.lastIndexOf(";") != -1) {
					errorMsg.deleteCharAt(errorMsg.lastIndexOf(";"));
				}
				if (errorMsg.length() > 0) {
					throw new Exception(errorMsg.toString());
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void doRemove(Collection<E> list) throws Exception {
		getDAO().remove(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doUpdate(ValueObject )
	 */
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			ValueObject po = getDAO().find(vo.getId());
			if (po != null) {
				PropertyUtils.copyProperties(po, vo);
				getDAO().update(po);
			} else {
				getDAO().update(vo);
			}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			e.printStackTrace();
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doUpdate(ValueObject
	 *      [])
	 */
	public void doUpdate(ValueObject[] vos) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			if (vos != null)
				for (int i = 0; i < vos.length; i++) {
					ValueObject vo = vos[i];
					ValueObject po = getDAO().find(vo.getId());
					if (po != null) {
						PropertyUtils.copyProperties(po, vo);
						getDAO().update(po);
					} else {
						getDAO().update(vo);
					}
				}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doUpdate(java.util.Collection)
	 */
	public void doUpdate(Collection<ValueObject> vos) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			if (vos != null)
				for (Iterator<ValueObject> iter = vos.iterator(); iter.hasNext();) {
					ValueObject vo = iter.next();
					ValueObject po = getDAO().find(vo.getId());
					if (po != null) {
						PropertyUtils.copyProperties(po, vo);
						getDAO().update(po);
					} else {
						getDAO().update(vo);
					}
				}

			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcess#doUpdate(ValueObject,
	 *      WebUser)
	 */
	public void doUpdate(ValueObject vo, WebUser user) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			ValueObject po = getDAO().find(vo.getId());
			PropertyUtils.copyProperties(po, vo);

			getDAO().update(vo);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
		}
	}
	
	

	/* (non-Javadoc)
	 * @see IDesignTimeProcess#doCreateOrUpdate(ValueObject)
	 */
	public void doCreateOrUpdate(ValueObject vo) throws Exception {
		if (StringUtil.isBlank(vo.getId()))
			doCreate(vo);
		else
			doUpdate(vo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcess#doView(java.lang.String)
	 */
	public ValueObject doView(String pk) throws Exception {
		return getDAO().find(pk);
	}

	public ValueObject doViewByName(String name, String application) throws Exception {
		return getDAO().findByName(name, application);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeOLink.bpm.base.ejb.IDesignTimeProcess#doQuery(OLink.bpm.base.action.
	 * ParamsTable, WebUser)
	 */
	public DataPackage<E> doQuery(ParamsTable params, WebUser user) throws Exception {
		return getDAO().query(params, user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeOLink.bpm.base.ejb.IDesignTimeProcess#doQuery(OLink.bpm.base.action.
	 * ParamsTable)
	 */
	public DataPackage<E> doQuery(ParamsTable params) throws Exception {
		return getDAO().query(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeOLink.bpm.base.ejb.BaseProcess#doSimpleQuery(OLink.bpm.base.action.
	 * ParamsTable)
	 */
	public Collection<E> doSimpleQuery(ParamsTable params) throws Exception {
		return getDAO().simpleQuery(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doSimpleQuery(OLink.bpm.base.action
	 *      .ParamsTable, java.lang.String)
	 */
	public Collection<E> doSimpleQuery(ParamsTable params, String application) throws Exception {
		if (application != null) {
			if (params == null)
				params = new ParamsTable();
			params.setParameter("application", application);
		}

		return getDAO().simpleQuery(params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IDesignTimeProcess#doRemove(ValueObject )
	 */
	public void doRemove(ValueObject obj) throws Exception {
		try {
			PersistenceUtils.beginTransaction();
			getDAO().remove(obj);
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			PersistenceUtils.rollbackTransaction();
			throw e;
		}
	}

	/**
	 * Get the relate data access object.
	 * 
	 * @return The relate data access object.
	 * @throws Exception
	 */
	protected abstract IDesignTimeDAO<E> getDAO() throws Exception;
	
	/**
	 * 通过hql语句获得数据总数
	 * @param hql
	 * @return
	 * @throws Exception
	 */
	public int doGetTotalLines(String hql) throws Exception{
		return getDAO().getTotalLines(hql);
	}

	public Collection<E> doQueryByHQL(String hql,int pageNo,int pageSize) throws Exception {
		return this.getDAO().queryByHQL(hql,pageNo,pageSize);
	}
	
	public void doCheckout(String id, WebUser user) throws Exception{
		PersistenceUtils.beginTransaction();
		this.getDAO().checkout(id, user);
		PersistenceUtils.commitTransaction();
		
	}
	
	public void doCheckin(String id, WebUser user) throws Exception{
		PersistenceUtils.beginTransaction();
		this.getDAO().checkin(id, user);
		PersistenceUtils.commitTransaction();
	}
	
	

}
