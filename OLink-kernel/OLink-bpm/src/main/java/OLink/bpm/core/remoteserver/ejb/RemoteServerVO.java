package OLink.bpm.core.remoteserver.ejb;

import OLink.bpm.base.dao.ValueObject;

/**
 * @hibernate.class
 * table = "T_REMOTESERVER"
 * @author Jenny
 *
 */

public class RemoteServerVO extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3072257022234391043L;

	String  name;
	
	String  url;

	/**
	 * @hibernate.property
	 * column="NAME"
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @hibernate.property
	 * column="URL"
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
