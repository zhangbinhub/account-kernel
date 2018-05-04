package OLink.bpm.core.dynaform.dts.datasource.dao;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.dynaform.dts.datasource.ejb.DataSource;

public class HibernateDataSourceDAO extends HibernateBaseDAO<DataSource> implements DataSourceDAO {

	public HibernateDataSourceDAO(String voClassName) {
		super(voClassName);
	}

	public DataSource getDataSource(String dataSouceName, String application)
			throws Exception {
		String hql = "FROM " + _voClazzName + " vo where vo.applicationid='"
				+ application + "' ";
		hql += " AND vo.name='" + dataSouceName + "'";
		return (DataSource) getData(hql);
	}
}
