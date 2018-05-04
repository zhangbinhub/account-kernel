package OLink.bpm.core.dynaform.dts.excelimport.config.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.dynaform.dts.excelimport.config.ejb.IMPMappingConfigVO;

public class HibernateIMPMappingConfigDAO extends HibernateBaseDAO<IMPMappingConfigVO> implements IMPMappingConfigDAO {
	public HibernateIMPMappingConfigDAO(String voClassName) {
		super(voClassName);
	}

	public Collection<IMPMappingConfigVO> getAllMappingConfig(String application) throws Exception {
		String hql = "from " + _voClazzName;
		ParamsTable params = new ParamsTable();
		params.setParameter("application", application);
		return getDatas(hql, params);
	}

	public Object getMappingExcel(String id) throws Exception {
		String hql = "from " + _voClazzName;
		hql += " where ID='" + id + "'";
		return getData(hql);
	}

}
