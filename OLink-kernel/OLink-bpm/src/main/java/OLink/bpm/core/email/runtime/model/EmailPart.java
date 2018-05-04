package OLink.bpm.core.email.runtime.model;

import javax.activation.DataSource;

import OLink.bpm.core.email.util.Utility;

/**
 * @author Tom
 */
public class EmailPart{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1253290214253743384L;
	private int id;
	private Object content;
	private String disposition;
	private String contentType;
	private String contentId;
	private long size;
	private String sizeReadable;
	private String fileName;
	private String shortname;
	
	/**
	 * @SuppressWarnings DataSource不支持序列化
	 */
	private DataSource dataSource;
	
	public static final String TEXT_BODY = "Text Body";
	public static final String BODY = "Body";
	public static final String HTML_BODY = "Html Body";

	public EmailPart() {
		super();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getDisposition() {
		return disposition;
	}

	public void setDisposition(String disposition) {
		this.disposition = disposition;
	}

	public String getContentType() {
		//if (contentType == null) {
		//	contentType = "text/html";
		//}
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
		this.sizeReadable = Utility.sizeToHumanReadable(size);
	}

	/**
	 * @return
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param string
	 */
	public void setFileName(String string) {
		fileName = string;
		
		if (fileName == null) {
			if (getContentType().indexOf("text/html") >= 0) {
				fileName = HTML_BODY;
			} else if (getContentType().indexOf("text/plain") >= 0) {
				fileName = TEXT_BODY;
			} else {
				fileName = BODY;
			}
		}
		//if (fileName.length() > 9) {
		//	shortname = fileName.substring(0,9) + "...";
		//} else {
			shortname = fileName;
		//}
	}

	public boolean isPlainText() {
		return this.contentType != null && this.contentType.indexOf("text/plain") >= 0;
	}

	public boolean isHTMLText() {
		return this.contentType != null && this.contentType.indexOf("text/html") >= 0;
	}

	public boolean isImage() {
		return this.contentType != null && this.contentType.indexOf("image/") >= 0;
	}

	public boolean isAudio() {
		return this.contentType != null && this.contentType.indexOf("audio/") >= 0;
	}
	/**
	 * @return
	 */
	public String getSizeReadable() {
		return sizeReadable;
	}

	/**
	 * @param string
	 */
	public void setSizeReadable(String string) {
		sizeReadable = string;
	}

	/**
	 * @return
	 */
	public String getShortname() {
		return shortname;
	}

	/**
	 * @param string
	 */
	public void setShortname(String string) {
		shortname = string;
	}
	/**
	 * @return
	 */
	public DataSource getDataSource() {
		return dataSource;
	}

	/**
	 * @param source
	 */
	public void setDataSource(DataSource source) {
		dataSource = source;
	}
	
	public void setContent(Object content, String type) {
		this.content = content;
		this.contentType = type;
	}

}
