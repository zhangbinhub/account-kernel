package OLink.bpm.core.dynaform.dts.exp.columnmapping.dao;

import java.util.Collection;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.dynaform.dts.exp.columnmapping.ejb.ColumnMapping;

public class HibernateColumnMappingDAO extends HibernateBaseDAO<ColumnMapping> implements ColumnMappingDAO{

	public HibernateColumnMappingDAO(String voClassName) {
		super(voClassName);
	}
	
	public Collection<ColumnMapping> getColMapBytoName(String toName, String application) throws Exception {
	 String hql = "from "+this._voClazzName +" where toName ='"+toName+"'";
	   ParamsTable params=new ParamsTable();
	   params.setParameter("application",application);
	   return getDatas(hql, params);
	}
}
