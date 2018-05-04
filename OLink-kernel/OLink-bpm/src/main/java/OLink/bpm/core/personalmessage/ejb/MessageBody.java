package OLink.bpm.core.personalmessage.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * 站内短信信息体
 * 
 * @author Tom
 * 
 */
public class MessageBody extends ValueObject {

	private static final long serialVersionUID = 1L;

	private String id;
	private String type;
	private String title;
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

}
