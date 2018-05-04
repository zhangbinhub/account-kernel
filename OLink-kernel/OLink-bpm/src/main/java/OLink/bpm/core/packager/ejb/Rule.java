package OLink.bpm.core.packager.ejb;

import java.util.Collection;

import OLink.bpm.base.dao.ValueObject;

public class Rule extends ValueObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Collection<AccessElement> accessList;

	public Collection<AccessElement> getAccessList() {
		return accessList;
	}

	public void setAccessList(Collection<AccessElement> accessList) {
		this.accessList = accessList;
	}
}
