package OLink.bpm.core.multilanguage.dao;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.multilanguage.ejb.MultiLanguage;

public interface MultiLanguageDAO extends IDesignTimeDAO<MultiLanguage> {
	/**
	 * 通过语言类型、标签查找
	 */
	MultiLanguage find(int languageType, String label) throws Exception;
	/**
	 * 通过语言类型、标签、域来查找
	 * @param languageType
	 * @param label
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	MultiLanguage find(int languageType, String label, String domain) throws Exception;
	/**
	 * 
	 * @param languageType
	 * @param label
	 * @throws Exception
	 */
	void remove(int languageType, String label) throws Exception;
}
