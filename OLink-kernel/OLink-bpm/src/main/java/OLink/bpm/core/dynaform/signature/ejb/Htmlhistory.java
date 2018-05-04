package OLink.bpm.core.dynaform.signature.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * 
 * @author Alex 日志数据实体
 */

public class Htmlhistory extends ValueObject {
	
	private static final long serialVersionUID = 924636625541020584L;

	public Htmlhistory() {
	}

	/**
	 * 文档编号
	 */

	private String DocumentID;
	/**
	 * 印章编号
	 */

	private String SignatureID;
	/**
	 * 印章名字
	 */

	private String SignatureName;
	/**
	 * 印章单位
	 */

	private String SignatureUnit;
	/**
	 * 印章使用者
	 */

	private String SignatureUser;
	/**
	 * KEY序列号
	 */

	private String KeySN;
	/**
	 * 印章序列号
	 */

	private String SignatureSN;
	/**
	 * 印章全球唯一标识
	 */

	private String SignatureGUID;
	/**
	 * 签章机器IP
	 */

	private String IP;
	/**
	 * 日志标志
	 */

	private String LogType;
	/**
	 * 日志时间
	 */

	private String LogTime;

	/**
	 * 获取文档编号
	 * 
	 * @return
	 */
	public String getDocumentID() {
		return DocumentID;
	}

	/**
	 * 设置文档编号
	 * 
	 * @param documentID
	 */
	public void setDocumentID(String documentID) {
		DocumentID = documentID;
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
	 * 设置印章编号
	 * 
	 * @param signatureID
	 */
	public void setSignatureID(String signatureID) {
		SignatureID = signatureID;
	}

	/**
	 * 获取印章名字
	 * 
	 * @return
	 */
	public String getSignatureName() {
		return SignatureName;
	}

	/**
	 * 设置印章名字
	 * 
	 * @param signatureName
	 */
	public void setSignatureName(String signatureName) {
		SignatureName = signatureName;
	}

	/**
	 * 获取印章单位
	 * 
	 * @return
	 */
	public String getSignatureUnit() {
		return SignatureUnit;
	}

	/**
	 * 设置印章单位
	 * 
	 * @param signatureUnit
	 */
	public void setSignatureUnit(String signatureUnit) {
		SignatureUnit = signatureUnit;
	}

	/**
	 * 获取印章使用者
	 * 
	 * @return
	 */
	public String getSignatureUser() {
		return SignatureUser;
	}

	/**
	 * 设置印章使用者
	 * 
	 * @param signatureUser
	 */
	public void setSignatureUser(String signatureUser) {
		SignatureUser = signatureUser;
	}

	/**
	 * 获取KEY序列号
	 * 
	 * @return
	 */
	public String getKeySN() {
		return KeySN;
	}

	/**
	 * 设置KEY序列号
	 * 
	 * @param keySN
	 */
	public void setKeySN(String keySN) {
		KeySN = keySN;
	}

	/**
	 * 获取印章序列号
	 * 
	 * @return
	 */
	public String getSignatureSN() {
		return SignatureSN;
	}

	/**
	 * 设置印章序列号
	 * 
	 * @param signatureSN
	 */
	public void setSignatureSN(String signatureSN) {
		SignatureSN = signatureSN;
	}

	/**
	 * 获取印章全球唯一标识
	 * 
	 * @return
	 */
	public String getSignatureGUID() {
		return SignatureGUID;
	}

	/**
	 * 设置印章全球唯一标识
	 * 
	 * @param signatureGUID
	 */
	public void setSignatureGUID(String signatureGUID) {
		SignatureGUID = signatureGUID;
	}

	/**
	 * 获取签章机器IP
	 * 
	 * @return
	 */
	public String getIP() {
		return IP;
	}

	/**
	 * 设置签章机器IP
	 * 
	 * @param ip
	 */
	public void setIP(String ip) {
		IP = ip;
	}

	/**
	 * 获取日志标志
	 * 
	 * @return
	 */
	public String getLogType() {
		return LogType;
	}

	/**
	 * 设置日志标志
	 * 
	 * @param logType
	 */
	public void setLogType(String logType) {
		LogType = logType;
	}

	/**
	 * 获取日志时间
	 * 
	 * @return
	 */
	public String getLogTime() {
		return LogTime;
	}

	/**
	 * 设置日志时间
	 * 
	 * @param logTime
	 */
	public void setLogTime(String logTime) {
		LogTime = logTime;
	}

}