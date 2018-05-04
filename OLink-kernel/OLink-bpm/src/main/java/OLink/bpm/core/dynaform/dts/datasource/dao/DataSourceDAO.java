package OLink.bpm.core.dynaform.dts.datasource.dao;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;

public interface DataSourceDAO extends IDesignTimeDAO<DataSource> {

	DataSource getDataSource(String dataSouceName, String application)
			throws Exception;
}
