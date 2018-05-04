package OLink.bpm.core.security;

import java.security.Permission;

/**
 * The resouce permission
 */
public class ResourcePermission extends Permission {

	private static final long serialVersionUID = -966377775560618317L;

	/**
	 * @param name
	 *            The resouce permiss name.
	 */
	public ResourcePermission(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.security.Permission#implies(java.security.Permission)
	 */
	public boolean implies(Permission permission) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		ResourcePermission that = (ResourcePermission) obj;
		return this.getName() != null ? this.getName().equals(that.getName()) : that.getName() == null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		StringBuffer value = new StringBuffer(getName());
		return value.toString().hashCode() * 10;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.security.Permission#getActions()
	 */
	public String getActions() {
		return null;
	}
	/**
	 * Checks if the subject owns the resource by comparing all of the Subject's
	 * principals to the resource.getOwner() value.
	 * 
	 * @param user
	 *            Ther user
	 * @param resource
	 *            The resouce.
	 * @return True for the user is the owner of the resource , false otherwise.
	 */
	/*
	 * private boolean isResourceOwner(Subject user, ValueObject resource) {
	 * String owner = resource.getId(); Iterator<Principal> principalIterator =
	 * user.getPrincipals().iterator(); while (principalIterator.hasNext()) {
	 * Principal principal = principalIterator.next(); if
	 * (principal.getName().equals(owner)) return true; } return false; }
	 */

}
