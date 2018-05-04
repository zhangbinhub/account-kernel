package OLink.bpm.base.ejb;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;

/**
 * The base process interface.
 */
public interface IRunTimeProcess<E> extends java.io.Serializable {
	/**
	 * Create a new value object
	 * 
	 * @param vo
	 *            The value object
	 * @throws Exception
	 */
	void doCreate(ValueObject vo) throws Exception;

	/**
	 * Update the value object
	 * 
	 * @param vo
	 *            The value object.
	 * @throws Exception
	 */
	void doUpdate(ValueObject vo) throws Exception;

	/**
	 * Remove the value object
	 * 
	 * @param pk
	 *            The value object's primary key.
	 * @throws Exception
	 */
	void doRemove(String pk) throws Exception;

	/**
	 * @param id
	 * @return
	 * @throws Exception
	 */
	ValueObject doView(String id) throws Exception;

	/**
	 * Begin transaction
	 * 
	 * @throws Exception
	 */
	void beginTransaction() throws Exception;

	/**
	 * Commit the transaction.
	 * 
	 * @throws Exception
	 */
	void commitTransaction() throws Exception;

	/**
	 * Roll back transaction.
	 * 
	 * @throws Exception
	 */
	void rollbackTransaction() throws Exception;

	/**
	 * Close the connection
	 * 
	 * @throws Exception
	 */
	void closeConnection() throws Exception;

	/**
	 * Query the object
	 * 
	 * @param params
	 *            The parameter table
	 * @param user
	 *            The web user.
	 * @return The result data package.
	 * @throws Exception
	 */
	DataPackage<E> doQuery(ParamsTable params, WebUser user)
			throws Exception;

}
