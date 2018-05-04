package OLink.bpm.core.dynaform.signature.ejb;

import java.util.List;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.base.ejb.IDesignTimeProcess;

/**
 * 
 * @author Alex
 *
 */
public interface HtmlsignatureProcess<E> extends IDesignTimeProcess<E> {

	List<E> queryAll() throws Exception;
	List<E> queryById(String SignatureID, String DocumentID, String FormID) throws Exception;
	List<E> queryByDocumentID(String DocumentID, String FormID) throws Exception;
	void updateHtmlsignature(Htmlsignature htmlsignature) throws Exception;
	void createHtmlsignature(String mDocumentID, String mSignatureID, String mSignature, String FormID) throws Exception;

	void getDocument(ParamsTable params, WebUser user) throws Exception;

	void getBatchDocument(ParamsTable params) throws Exception;

	void saveSignature(ParamsTable params) throws Exception;

	void getNowTime() throws Exception;

	void deleSignature(ParamsTable params) throws Exception;

	void loadSignature(ParamsTable params) throws Exception;

	void showSignature(ParamsTable params) throws Exception;

	void getSignatureData(ParamsTable params) throws Exception;

	void signatureKey(ParamsTable params) throws Exception;

	void putSignatureData(ParamsTable params) throws Exception;

	void saveHistory(ParamsTable params) throws Exception;
}
