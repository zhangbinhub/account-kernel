package OLink.bpm.core.shortmessage.submission.ejb;

import java.io.Serializable;
import java.util.Date;

import OLink.bpm.base.dao.ValueObject;

public class SubmitMessageVO extends ValueObject implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String docid;

	private String title;

	private String content;

	private Date sendDate;

	private String sender;

	private String receiver;
	
	private String receiverUserID;

	private String replyCode;
	
	private int contentType;

	private boolean submission;

	private boolean failure;

	private boolean reply;
	
	private boolean trash;
	
	private boolean draft;
	
	private boolean needReply;
	
	private boolean mass;

	/**
	 * 是否群发
	 * @return 群发否,true:群发，否则发送给单人
	 */
	public boolean isMass() {
		return mass;
	}

	/**
	 * 设置群发否,true:群发，否则发送给单人
	 * @param mass true|false,标识是否群发
	 */
	public void setMass(boolean mass) {
		this.mass = mass;
	}

	/**
	 * 是否需要回复
	 * @return 是否需要回复,true:需要回复，否则不需要回复
	 */
	public boolean isNeedReply() {
		return needReply;
	}

	/**
	 * 设置是否需要回复,true:需要回复，否则不需要回复
	 * @param needReply true|false,标识是否需要回复
	 */
	public void setNeedReply(boolean needReply) {
		this.needReply = needReply;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getSendDate() {
		return sendDate;
	}

	public void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public boolean getSubmission() {
		return submission;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public void setSubmission(boolean submission) {
		this.submission = submission;
	}

	public String getReplyCode() {
		return replyCode;
	}

	public void setReplyCode(String replyCode) {
		this.replyCode = replyCode;
	}

	public boolean isFailure() {
		return failure;
	}

	public boolean isReply() {
		return reply;
	}

	public boolean isTrash() {
		return trash;
	}

	public boolean isDraft() {
		return draft;
	}

	public void setFailure(boolean failure) {
		this.failure = failure;
	}

	public void setReply(boolean reply) {
		this.reply = reply;
	}

	public void setTrash(boolean trash) {
		this.trash = trash;
	}

	public void setDraft(boolean draft) {
		this.draft = draft;
	}

	public String getDocid() {
		return docid;
	}

	public void setDocid(String docid) {
		this.docid = docid;
	}

	public int getContentType() {
		return contentType;
	}

	public void setContentType(int contentType) {
		this.contentType = contentType;
	}

	public String getReceiverUserID() {
		return receiverUserID;
	}

	public void setReceiverUserID(String receiverUserID) {
		this.receiverUserID = receiverUserID;
	}
	
}
