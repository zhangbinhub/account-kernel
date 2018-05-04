package OLink.bpm.core.user.ejb;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import OLink.bpm.core.user.dao.UserDefinedDAO;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.util.StringUtil;
import OLink.bpm.base.dao.DAOFactory;
import eWAP.core.Tools;

public class UserDefinedProcessBean extends AbstractDesignTimeProcessBean<UserDefined> implements UserDefinedProcess {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1210735331986765651L;

	@Override
	protected IDesignTimeDAO<UserDefined> getDAO() throws Exception {
		return (UserDefinedDAO) DAOFactory.getDefaultDAO(UserDefined.class.getName());
	}


	public void doUserDefinedUpdate(ValueObject vo) throws Exception {
	}

	public Collection<UserDefined> doViewByApplication(String applicationId) throws Exception {
		return ((UserDefinedDAO) getDAO()).findByApplication(applicationId);
	}

	public int doViewCountByName(String name, String applicationid)
			throws Exception {
		return ((UserDefinedDAO) getDAO()).queryCountByName(name, applicationid);  
	}

	
	public DataPackage<UserDefined> getDatapackage(String hql, ParamsTable params) throws Exception {
		String _pageLines = params.getParameterAsString("_pagelines");
		String _currPage = params.getParameterAsString("_currpage");
		int pageLines = 10;
		int currPage = 1;
		if(!StringUtil.isBlank(_pageLines)){
			try{
				pageLines = Integer.parseInt(_pageLines);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		if(!StringUtil.isBlank(_currPage)){
			try{
				currPage = Integer.parseInt(_currPage);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		return getDAO().getDatapackage(hql, params, currPage, pageLines);
	}
	
	public void doCreate(ValueObject vo) throws Exception {
		UserDefined tmp = null;
		tmp = ((UserDefinedDAO) getDAO()).login(((UserDefined) vo).getName());
		if (tmp != null) {
//			throw new ExistNameException("{*[core.form.exist]*}");
		}
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

}
