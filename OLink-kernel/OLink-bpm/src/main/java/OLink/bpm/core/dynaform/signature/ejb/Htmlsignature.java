package OLink.bpm.core.dynaform.signature.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @author Alex 系统印章数据实体
 */
public class Htmlsignature extends ValueObject {
	
	private static final long serialVersionUID = 5792278579732653555L;

	public Htmlsignature() {
	}

	/**
	 * 文档编号
	 */
	private String DocumentID;
	/**
	 * 表单编号
	 */
	private String FormID;
	/**
	 * 印章
	 */
	private String Signature;
	/**
	 * 印章编号
	 */
	private String SignatureID;

	/**
	 * 设置文档编号
	 * 
	 * @return
	 */
	public String getDocumentID() {
		return DocumentID;
	}

	/**
	 * 获取档编号
	 * 
	 * @param documentID
	 */
	public void setDocumentID(String documentID) {
		DocumentID = documentID;
	}

	/**
	 * 设置印章
	 * 
	 * @return
	 */
	public String getSignature() {
		return Signature;
	}

	/**
	 * 获取印章
	 * 
	 * @param signature
	 */
	public void setSignature(String signature) {
		Signature = signature;
	}

	/**
	 * 设置印章编号
	 * 
	 * @return
	 */
	public String getSignatureID() {
		return SignatureID;
	}

	/**
	 * 获取印章编号
	 * 
	 * @param signatureID
	 */
	public void setSignatureID(String signatureID) {
		SignatureID = signatureID;
	}

	public String getFormID() {
		return FormID;
	}

	public void setFormID(String formID) {
		FormID = formID;
	}

}
