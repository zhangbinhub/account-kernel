package OLink.bpm.core.packager.ejb;

import OLink.bpm.base.dao.ValueObject;

public abstract class AccessElement extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int repository;
	
	private boolean accessRight;

	private int accessType;

	private String path;

	public boolean isAccessRight() {
		return accessRight;
	}

	public void setAccessRight(boolean accessRight) {
		this.accessRight = accessRight;
	}

	public int getAccessType() {
		return accessType;
	}

	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getRepository() {
		return repository;
	}

	public void setRepository(int repository) {
		this.repository = repository;
	}

}
