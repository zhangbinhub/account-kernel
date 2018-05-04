/**
 * SimpleReceive.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OLink.bpm.webservice.model;

import java.io.Serializable;

public class SimpleReceive implements Serializable {

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
