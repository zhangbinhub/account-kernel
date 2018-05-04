package OLink.bpm.core.multilanguage.ejb;

import OLink.bpm.base.ejb.IDesignTimeProcess;

public interface MultiLanguageProcess extends IDesignTimeProcess<MultiLanguage> {
	/**
	 * 
	 * @param languageType 
	 * @param label
	 * @return
	 * @throws Exception
	 */
	MultiLanguage doView(int languageType, String label) throws Exception;
	/**
	 * 条件语言类型、标签、域
	 * @param languageType
	 * @param label
	 * @param domain
	 * @return
	 * @throws Exception
	 */
	MultiLanguage doView(int languageType, String label, String domain) throws Exception;
	/**
	 * @param languageType
	 * @param label
	 * @throws Exception
	 */
	void doRemove(int languageType, String label) throws Exception;
}
