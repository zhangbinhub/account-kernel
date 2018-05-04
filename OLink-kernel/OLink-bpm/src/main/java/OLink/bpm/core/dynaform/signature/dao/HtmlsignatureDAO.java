package OLink.bpm.core.dynaform.signature.dao;

import java.util.List;

import OLink.bpm.base.dao.IDesignTimeDAO;
import OLink.bpm.core.dynaform.signature.ejb.Htmlsignature;

public interface HtmlsignatureDAO extends IDesignTimeDAO<Htmlsignature> {

	List<Htmlsignature> queryAll() throws Exception;
	List<Htmlsignature> queryById(String SignatureID, String DocumentID, String FormID) throws Exception;
	List<Htmlsignature> queryByDocumentID(String DocumentID, String FormID) throws Exception;
	void updateHtmlsignature(Htmlsignature htmlsignature) throws Exception;
	void createHtmlsignature(String mDocumentID, String mSignatureID, String mSignature, String FormID) throws Exception;
}
