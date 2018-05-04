package OLink.bpm.core.workflow.storage.runtime.ejb;

import java.io.Serializable;
import java.util.Date;

import OLink.bpm.base.dao.ValueObject;
import OLink.bpm.constans.Environment;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;
import OLink.bpm.core.user.ejb.BaseUser;

/**
 * 传阅者
 * @author happy
 *
 */
public class Circulator extends ValueObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -736001366916806969L;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 用户
	 */
	private String userId;
	/**
	 * 文档
	 */
	private String docId;
	
	/**
	 * 节点
	 */
	private String nodertId;
	/**
	 * 流程实例
	 */
	private String flowstatertId;
	/**
	 * 抄送时间
	 */
	private Date ccTime;
	/**
	 * 阅读时间
	 */
	private Date readTime;
	/**
	 * 阅读期限
	 */
	private Date deadline;
	/**
	 * 是否已阅
	 */
	private boolean read = false;
	
	/**
	 * 摘要
	 */
	private String summary;
	
	private String formId;
	
	public String getFormId() {
		return formId;
	}
	public void setFormId(String formId) {
		this.formId = formId;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	/**
	 * 获取名称
	 * @return
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取用户Id
	 * @return
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * 设置用户Id
	 * @param userId
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * 获取文档Id
	 * @return
	 */
	public String getDocId() {
		return docId;
	}
	/**
	 * 设置文档ID
	 * @param docId
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}
	/**
	 * 获取节点Id
	 * @return
	 */
	public String getNodertId() {
		return nodertId;
	}
	/**
	 * 设置节点Id
	 * @param nodertId
	 */
	public void setNodertId(String nodertId) {
		this.nodertId = nodertId;
	}
	/**
	 * 获取流程实例Id
	 * @return
	 */
	public String getFlowstatertId() {
		return flowstatertId;
	}
	/**
	 * 设置流程实例Id
	 * @param flowstatertId
	 */
	public void setFlowstatertId(String flowstatertId) {
		this.flowstatertId = flowstatertId;
	}
	/**
	 * 获取抄送时间
	 * @return
	 */
	public Date getCcTime() {
		return ccTime;
	}
	/**
	 * 设置抄送时间
	 * @param ccTime
	 */
	public void setCcTime(Date ccTime) {
		this.ccTime = ccTime;
	}
	/**
	 * 获取阅读时间
	 * @return
	 */
	public Date getReadTime() {
		return readTime;
	}
	/**
	 * 设置阅读时间
	 * @param readTime
	 */
	public void setReadTime(Date readTime) {
		this.readTime = readTime;
	}
	/**
	 * 获取阅读期限
	 * @return
	 */
	public Date getDeadline() {
		return deadline;
	}
	/**
	 * 设置阅读期限
	 * @param deadline
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	/**
	 * 是否已读
	 * @return
	 */
	public boolean isRead() {
		return read;
	}
	/**
	 * 设置是否已读
	 * @param read
	 */
	public void setRead(boolean read) {
		this.read = read;
	}
	
	
	public Circulator() {
		super();
	}
	/**
	 * @param doc
	 * @param nodeRT
	 * @param userVO
	 * @param deadline
	 */
	public Circulator(Document doc, NodeRT nodeRT, BaseUser userVO, Date deadline) {
		this.setName(userVO.getName());
		this.setUserId(userVO.getId());
		this.setDocId(doc.getId());
		this.setNodertId(nodeRT.getId());
		this.setFlowstatertId(doc.getStateid());
		this.setCcTime(new Date());
		if (deadline != null) {
			this.setDeadline(new java.sql.Timestamp(deadline.getTime()));
		} else {
			this.setDeadline(null);
		}
		this.setDomainid(doc.getDomainid());
		this.setApplicationid(doc.getApplicationid());
	}
	
	public String toHtmlText(WebUser user, String summaryCfgId) throws Exception {
		StringBuffer html = new StringBuffer();
		if (this.isRead()) {
			// html.append("  ").append(this.getSummary());
			html
					.append("<td id=\"pd\" onMouseOver=\"this.className='over'\" onMouseOut=\"this.className='out'\" style=\"margin-top:2px;\">&nbsp;&nbsp;&nbsp;");
			html
					.append("<img src=\""
							+ Environment.getInstance().getContextPath()
							+ "/resource/img/hasRd.gif\" alt=\"已读\"  name=\"isRdImg\"/>");
			html.append("<a style=\"width:85%\" href=\"javaScript:viewDoc('" + this.getDocId()
					+ "', '" + this.getFormId() + "','" + summaryCfgId + "')\">");
			html.append(this.getSummary() + "</a></td>");
		} else {
			html
					.append("<td id=\"pd\" onMouseOver=\"this.className='over'\" onMouseOut=\"this.className='out'\" style=\"\">&nbsp;&nbsp;&nbsp;");
			html
					.append("<img src=\""
							+ Environment.getInstance().getContextPath()
							+ "/resource/img/unRd.gif\" alt=\"未读\"  name=\"isRdImg\"/>");
			html.append("<a style=\"width:85%\" href=\"javaScript:viewDoc('" + this.getDocId()
					+ "', '" + this.getFormId() + "','" + summaryCfgId + "')\">");
			html.append(this.getSummary() + "</a></td>");
		}

		return html.toString();

	}
	
	/**
	 * 拼装手机端待办XML
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public String toMbXMLText(WebUser user, String summaryCfgId,String title) throws Exception {
		StringBuffer html = new StringBuffer();
		if (this.isRead()) {
			html.append("<TR id='"+this.getId()+"' v='"+this.getFormId()+"' n='"+summaryCfgId+"'>");
			html.append("["+title+"](已读)"+this.getSummary()+"</TR>");
		} else {
			html.append("<TR id='"+this.getId()+"' v='"+this.getFormId()+"' n='"+summaryCfgId+"'>");
			html.append("["+title+"](未读)"+this.getSummary()+"</TR>");
		}

		return html.toString();

	}
	

}
