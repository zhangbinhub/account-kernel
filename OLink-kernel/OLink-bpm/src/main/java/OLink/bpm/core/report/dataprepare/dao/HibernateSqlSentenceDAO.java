package OLink.bpm.core.report.dataprepare.dao;

import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.core.report.dataprepare.ejb.SqlSentence;

public class HibernateSqlSentenceDAO extends HibernateBaseDAO<SqlSentence> implements SqlSentenceDAO{

	public HibernateSqlSentenceDAO(String voClassName) {
		super(voClassName);
	}
	
	public DataPackage<SqlSentence> getSqlSentenceByDataPrepare(String id) throws Exception{
		String hql = "FROM " + _voClazzName +" vo  where vo.dataPrepare.id='"+id+"'";
		   return getDatapackage(hql);
	}
}
