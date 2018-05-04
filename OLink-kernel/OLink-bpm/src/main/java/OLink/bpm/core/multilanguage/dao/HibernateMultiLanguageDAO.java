package OLink.bpm.core.multilanguage.dao;

import java.util.Collection;
import java.util.HashMap;

import OLink.bpm.base.dao.HibernateBaseDAO;
import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.core.multilanguage.ejb.MultiLanguage;
import OLink.bpm.core.multilanguage.ejb.LanguageType;
import eWAP.core.Tools;

public class HibernateMultiLanguageDAO extends HibernateBaseDAO<MultiLanguage> implements
		MultiLanguageDAO {

	private static HashMap<String, ValueObject> _cache = new HashMap<String, ValueObject>(500);

	public HibernateMultiLanguageDAO(String voClassName) {
		super(voClassName);
	}
	
	/**
	 * 通过语言类型、标签查找
	 */
	public MultiLanguage find(int languageType, String label) throws Exception {
		MultiLanguage ml = (MultiLanguage)_cache.get(languageType+"-"+label); 
		if (ml!=null) {
			return ml;
		}
		
		String hql = "FROM " + _voClazzName + " vo WHERE vo.type='"
				+ languageType + "' and vo.label='" + label + "'";

		Collection<MultiLanguage> colls = super.getDatas(hql, null);
		if (colls != null && colls.size() > 0) {
			ml = colls.iterator().next();
			_cache.put(languageType+"-"+label,ml);
			return ml;
		}
		else {
			ml = new MultiLanguage();
			ml.setId(Tools.getSequence());
			ml.setType(LanguageType.LANGUAGE_TYPE_ENGLISH);
			ml.setLabel(label);
			ml.setText(label);
			_cache.put(languageType+"-"+label,ml);
			return ml;
		}
	}
	
	/**
	 * 通过语言类型、标签、域来查找
	 * @param languageType
	 * @param label
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	public MultiLanguage find(int languageType, String label,String application) throws Exception {
		MultiLanguage ml = (MultiLanguage)_cache.get(languageType+"-"+label); 
		if (ml!=null) {
			return ml;
		}
		
		String hql = "FROM " + _voClazzName + " vo WHERE vo.type='"
				+ languageType + "' and vo.label='" + label + "' and vo.applicationid='"+application+"'";

		Collection<MultiLanguage> colls = super.getDatas(hql, null);
		if (colls != null && colls.size() > 0) {
			ml = colls.iterator().next();
			_cache.put(languageType+"-"+label,ml);
			return ml;
		}
		else {
			return null;
		}
	}

	public void create(ValueObject vo) throws Exception {
		try {
			super.create(vo);
		} catch (Exception e) {
			throw e;
		}
		if (vo!=null) {
			MultiLanguage ml = (MultiLanguage)vo;
			_cache.put(ml.getType()+"-"+ml.getLabel(),vo);
		}
	}

	public void remove(String id) throws Exception {
		MultiLanguage ml = null;
		try {
			ml = (MultiLanguage)super.find(id);
			super.remove(id);
		} catch (Exception e) {
			throw e;
		}
		
		if (ml!=null) {
			_cache.remove(ml.getType()+"-"+ml.getLabel());
		}
	}
	
	public void remove(int languageType, String label) throws Exception {
		_cache.remove(languageType+"-"+label);
	}

	public void update(ValueObject vo) throws Exception {
		try {
			super.update(vo);
		} catch (Exception e) {
			throw e;
		}
		if (vo!=null) {
			MultiLanguage ml = (MultiLanguage)vo;
			_cache.put(ml.getType()+"-"+ml.getLabel(),vo);
		}
		
	}
	
}
