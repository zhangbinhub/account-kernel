package OLink.bpm.core.dynaform.dts.datasource.ejb;

import java.util.Collection;
import java.util.Map;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface DataSourceProcess extends IDesignTimeProcess<DataSource> {
	/**
	 * excete sql
	 * 
	 * @param dataSouceName
	 * @param sql
	 * @param application
	 * @return
	 * @throws Exception
	 */
	Collection<?> queryDataSourceSQL(String dataSouceName, String sql,
									 String application) throws Exception;

	/**
	 * Insert data
	 * 
	 * @param dataSouceName
	 * @param sql
	 * @param application
	 * @throws Exception
	 */
	void queryInsert(String dataSouceName, String sql, String application)
			throws Exception;

	/**
	 * CreateAndUpdate create table and upadte table
	 * 
	 * @param dataSouceName
	 * @param sql
	 * @param application
	 * @throws Exception
	 */
	void createOrUpdate(String dataSouceName, String sql,
						String application) throws Exception;

	/**
	 * remove
	 * 
	 * @param dataSouceName
	 * @param sql
	 * @param application
	 * @throws Exception
	 */
	void remove(String dataSouceName, String sql, String application)
			throws Exception;

	Map<?, ?> findDataSourceSQL(String dataSouceName, String sql,
								String application) throws Exception;
}
