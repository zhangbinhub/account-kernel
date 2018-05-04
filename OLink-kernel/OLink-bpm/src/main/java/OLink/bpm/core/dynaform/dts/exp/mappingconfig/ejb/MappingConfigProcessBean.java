package OLink.bpm.core.dynaform.dts.exp.mappingconfig.ejb;

import java.util.Collection;
import java.util.Set;

import OLink.bpm.base.dao.DAOFactory;
import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.base.dao.PersistenceUtils;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;
import OLink.bpm.core.dynaform.dts.exp.mappingconfig.dao.MappingConfigDAO;
import OLink.bpm.base.ejb.AbstractDesignTimeProcessBean;
import org.apache.commons.beanutils.PropertyUtils;

public class MappingConfigProcessBean extends AbstractDesignTimeProcessBean<MappingConfig> implements MappingConfigProcess{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6136993206480555544L;

	protected IDesignTimeDAO<MappingConfig> getDAO() throws Exception {
		return (MappingConfigDAO) DAOFactory.getDefaultDAO(MappingConfig.class.getName());
	}
	
	public void doUpdate(ValueObject vo) throws Exception {
		try {
			PersistenceUtils.beginTransaction();

			ValueObject po = getDAO().find(vo.getId());
			if (po!=null) {
				Collection<ColumnMapping> columnmappings=((MappingConfig)po).getColumnMappings();
				PropertyUtils.copyProperties(po,vo);
				((MappingConfig)po).setColumnMappings((Set<ColumnMapping>)columnmappings);
				getDAO().update(po);
			}
			else {
				getDAO().update(vo);
			}
			
			PersistenceUtils.commitTransaction();
		} catch (Exception e) {
			e.printStackTrace();
			PersistenceUtils.rollbackTransaction();
		}
	}

	public Collection<MappingConfig> getMCByTableName(String tableName, String application) throws Exception {
		return ((MappingConfigDAO)getDAO()).getMCByTableName(tableName, application);
	}
}
