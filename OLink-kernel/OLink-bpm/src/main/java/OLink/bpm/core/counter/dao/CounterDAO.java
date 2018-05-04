package OLink.bpm.core.counter.dao;

import java.util.Collection;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.counter.ejb.CounterVO;
import OLink.bpm.base.dao.IBaseDAO;
import OLink.bpm.base.dao.IRuntimeDAO;

public interface CounterDAO extends IRuntimeDAO{

	/**
	 * Remove the sequence counter according the tag name.
	 * @param name The sequence tag name
	 * @throws Exception
	 */
	void removeByName(String name, String application, String domainid) throws Exception;

	/**
	 * find the sequence according the tag name.
	 * @param name The sequence tag name.
	 * @return The sequence counter.
	 * @throws Exception
	 */
	CounterVO findByName(String name, String application, String domainid) throws Exception;

	Collection<CounterVO> getDatas(String sql, String domainid) throws Exception;
	/**
	 * 
	 * @see IDesignTimeDAO#create(ValueObject)
	 */
	void create(ValueObject vo) throws Exception;

	/**
	 * (non-Javadoc)
	 * 
	 * @see IBaseDAO#update(ValueObject)
	 */
	void update(ValueObject vo) throws Exception;

}
