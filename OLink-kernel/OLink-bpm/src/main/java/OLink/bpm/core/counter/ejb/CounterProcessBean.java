package OLink.bpm.core.counter.ejb;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.IRuntimeDAO;
import OLink.bpm.base.ejb.AbstractRunTimeProcessBean;
import OLink.bpm.core.counter.dao.CounterDAO;
import OLink.bpm.util.RuntimeDaoManager;
import eWAP.core.Tools;

/**
 * @author nicholas
 */
public class CounterProcessBean extends AbstractRunTimeProcessBean<CounterVO>
		implements CounterProcess {

	private static final Object _lock = new Object();

	private static HashMap<CounterKey, CounterVO> _counters = new HashMap<CounterKey, CounterVO>();

	public CounterProcessBean(String applicationId) {
		super(applicationId);
	}

	private static final long serialVersionUID = -3768074774695007780L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see CounterProcess#doRemoveByName(java.lang.String)
	 */
	public void doRemoveByName(String name, String application, String domainid)
			throws Exception {
		synchronized (_lock) {
			try {
				PersistenceUtils.beginTransaction();

				// 从Cache中清除
				_counters.remove(new CounterKey(name, application, domainid));

				((CounterDAO) getDAO()).removeByName(name, application,
						domainid);
				PersistenceUtils.commitTransaction();
			} catch (Exception ex) {
				PersistenceUtils.rollbackTransaction();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see CounterProcess#getNextValue(java.lang.String)
	 */
	public int getNextValue(String name, String application, String domainid)
			throws Exception {
		synchronized (_lock) {
			try {
				// Session s =
				// PersistenceUtils.getSessionSignal().currentSession;
				PersistenceUtils.beginTransaction();
				// PersistenceUtils.getSessionSignal().currentSession.clear();
				CounterDAO dao = (CounterDAO) getDAO();
				if (name != null) {
					
CounterKey key = new CounterKey(name, application, domainid);

					CounterVO vo = findByName(name, application, domainid);
					if (vo == null) {
						vo = new CounterVO();
						vo.setId(Tools.getSequence());
						vo.setName(name);
						vo.setCounter(1);
						vo.setApplicationid(application);
						vo.setDomainid(domainid);
						
						dao.create(vo);
					} else {
						vo.setCounter(vo.getCounter() + 1);
						dao.update(vo);
					}
					
					//更新Cache
					_counters.put(key, vo);
					
					PersistenceUtils.commitTransaction();

					return vo.getCounter();
				}
			} catch (Exception e) {
				e.printStackTrace();
				PersistenceUtils.rollbackTransaction();
			}
			return 0;
		}
	}

	public int getShortSquence(String name, String application,
			String domainid, int maxValue) throws Exception {
		return getShortSquence(name, application, domainid, maxValue, 1);
	}

	public int getShortSquence(String name, String application,
			String domainid, int maxValue, int defaultValue) throws Exception {

		synchronized (_lock) {
			int value = getNextValue(name, application, domainid);
			if (value < defaultValue || value > maxValue) {
				CounterVO vo = findByName(name,
						application, domainid);
				vo.setCounter(defaultValue);
				getDAO().update(vo);
				
				//更新Cache
				_counters.put(new CounterKey(name, application, domainid), vo);
				
				value = vo.getCounter();
			}
			return value;
		}
	}

	public int getLastValue(String name, String application, String domainid)
			throws Exception {
		if (name != null) {
			CounterVO vo = findByName(name, application, domainid);
			if (vo == null) {
				return 0;
			} else {
				return vo.getCounter();
			}
		}
		return 0;
	}

	public Collection<CounterVO> getDatas(String sql, String domainid)
			throws Exception {
		CounterDAO dao = (CounterDAO) getDAO();
		return dao.getDatas(sql, domainid);

	}

	public CounterVO findByName(String name, String application, String domainid)
			throws Exception {

		CounterKey key = new CounterKey(name, application, domainid);
		CounterVO vo = _counters.get(key);
		if (vo == null) {
			vo = ((CounterDAO) getDAO())
					.findByName(name, application, domainid);
			if (vo != null) {
				_counters.put(key, vo);
			}
		}

		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see OLink.bpm.base.ejb.BaseProcessBean#getDAO()
	 */
	protected IRuntimeDAO getDAO() throws Exception {
		// ApplicationVO app=getApplicationVO(getApplicationId());
		RuntimeDaoManager runtimeDao = new RuntimeDaoManager();
		return runtimeDao.getCounterDAO(getConnection(), getApplicationId());
	}
}

class CounterKey {
	private String name;
	private String applicationid;
	private String domainid;

	CounterKey(String name, String application, String domainid) {
		this.name = name;
		this.applicationid = application;
		this.domainid = domainid;
	}

	public boolean equals(Object object) {
		if (object != null && object instanceof CounterKey) {
			CounterKey ck = (CounterKey) object;
			return ((ck.name == null && this.name == null) || (ck.name != null && ck.name
					.equals(name)))
					&& ((ck.applicationid == null && this.applicationid == null) || (ck.applicationid != null && ck.applicationid
							.equals(applicationid)))
					&& ((ck.domainid == null && this.domainid == null) || (ck.domainid != null && ck.domainid
							.equals(domainid)));
		}
		return false;
	}
}