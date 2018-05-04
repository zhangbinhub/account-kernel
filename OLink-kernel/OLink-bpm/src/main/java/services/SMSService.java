
package services;

import cn.emay.sdk.client.api.Client;
import eWAP.core.SMSReceive;
import java.io.Serializable;

public class SMSService implements eWAP.core.SMSService{

	public int sendMsg(String memberCode, String memberPWD,
			String smsNumber, String smsContent)
			throws Exception {
		try{
			Client myClient = new Client(memberCode,memberPWD);
			int myReg=myClient.registEx(memberPWD);
			int iRet=myClient.sendSMS(new String[] {smsNumber},smsContent, 3);
			return iRet;
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		
	}

	public int sendMsg(String memberCode, String memberPWD,
			String smsNumber, String smsContent,
			String replyCode) throws Exception {
       return 0;
}

	public Receive receiveMsg(
			String memberCode, String memberPWD)
			throws Exception {
         return null;
	}

	public Receive[] receiveMsgs(
			String memberCode, String memberPWD)
			throws Exception {
        return null;
	}
}

 class Receive implements Serializable,SMSReceive{

	private static final long serialVersionUID = -7081405673676624792L;

	private String msgContent;

	private String recvtel;

	private String sentTime;

	private String srctermid;

	/**
	 * Gets the msgContent value for this SimpleReceive.
	 * 
	 * @return msgContent
	 */
	public String getMsgContent() {
		return msgContent;
	}

	/**
	 * Sets the msgContent value for this SimpleReceive.
	 * 
	 * @param msgContent
	 */
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	/**
	 * Gets the recvtel value for this SimpleReceive.
	 * 
	 * @return recvtel
	 */
	public String getRecvtel() {
		return recvtel;
	}

	/**
	 * Sets the recvtel value for this SimpleReceive.
	 * 
	 * @param recvtel
	 */
	public void setRecvtel(String recvtel) {
		this.recvtel = recvtel;
	}

	/**
	 * Gets the sentTime value for this SimpleReceive.
	 * 
	 * @return sentTime
	 */
	public String getSentTime() {
		return sentTime;
	}

	/**
	 * Sets the sentTime value for this SimpleReceive.
	 * 
	 * @param sentTime
	 */
	public void setSentTime(String sentTime) {
		this.sentTime = sentTime;
	}

	/**
	 * Gets the srctermid value for this SimpleReceive.
	 * 
	 * @return srctermid
	 */
	public String getSrctermid() {
		return srctermid;
	}

	/**
	 * Sets the srctermid value for this SimpleReceive.
	 * 
	 * @param srctermid
	 */
	public void setSrctermid(String srctermid) {
		this.srctermid = srctermid;
	}

}
