package OLink.bpm.core.xmpp.notification;

import OLink.bpm.core.domain.ejb.DomainVO;
import OLink.bpm.core.xmpp.XMPPNotification;
import OLink.bpm.util.ObjectUtil;

public class SiteMessageIQ extends XMPPNotification {

	public SiteMessageIQ() {
		super();
	}

	private String title;
	private String content;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public DomainVO getDomain() {
		return this.domain;
	}

	public void setDomain(DomainVO domain) {
		this.domain = domain;
	}

	public SiteMessageIQ clone() {
		SiteMessageIQ siteMessageIQ = new SiteMessageIQ();
		try {
			ObjectUtil.copyProperties(siteMessageIQ, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return siteMessageIQ;
	}

	@Override
	public String getInnerXML() {
		StringBuffer childXml = new StringBuffer();
		childXml
				.append("<siteMessage xmlns=\"obpm:iq:notification:siteMessage\">");
		childXml.append("<id>");
		childXml.append(this.id != null ? this.id : "");
		childXml.append("</id>");
		childXml.append("<domainid>");
		childXml
				.append(this.domain != null ? (this.domain.getId() != null ? this.domain
						.getId()
						: "")
						: "");
		childXml.append("</domainid>");
		childXml.append("<title>");
		childXml.append(this.title != null ? this.title : "");
		childXml.append("</title>");

		childXml.append("<content>");
		childXml.append(this.content != null ? this.content : "");
		childXml.append("</content>");
		childXml.append("</siteMessage>");
		return childXml.toString();
	}

}
