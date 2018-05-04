package OLink.bpm.core.helper.ejb;

import java.io.Serializable;

import OLink.bpm.base.dao.ValueObject;

/**
 * @hibernate.class table="T_HELPER"
 * @author Administrator
 * 
 */

public class HelperVO extends ValueObject implements Serializable {
	
	private static final long serialVersionUID = -4362884515151013951L;

	String url;

	String title;

	String context;

	/**
	 * @hibernate.property column="CONTEXT"
	 * type="text"
	 * 
	 * @return
	 */
	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * @hibernate.property column="TITLE"
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @hibernate.property column="URL"
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
