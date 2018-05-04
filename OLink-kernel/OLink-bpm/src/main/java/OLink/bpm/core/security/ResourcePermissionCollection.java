package OLink.bpm.core.security;

import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The Resource permission collection
 */
public class ResourcePermissionCollection extends PermissionCollection {

	private static final long serialVersionUID = 100765769983827239L;

	private Hashtable<Object, Hashtable<Object, ResourcePermission>> permissions;

	public ResourcePermissionCollection() {
		permissions = new Hashtable<Object, Hashtable<Object, ResourcePermission>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.security.PermissionCollection#elements()
	 */
	public Enumeration<Permission> elements() {
		Hashtable<Object, Permission> list = new Hashtable<Object, Permission>();
		Enumeration<Hashtable<Object, ResourcePermission>> enum11 = permissions
				.elements();
		while (enum11.hasMoreElements()) {
			Hashtable<Object, ResourcePermission> table = enum11.nextElement();
			list.putAll(table);
		}
		return list.elements();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.security.PermissionCollection#implies(java.security.Permission)
	 */
	public boolean implies(Permission permission) {
		// if (true)
		// return true;

		if (!(permission instanceof ResourcePermission))
			throw new IllegalArgumentException("Wrong Permission type");

		ResourcePermission rcsPermission = (ResourcePermission) permission;
		Hashtable<?, ResourcePermission> aggregate = permissions
				.get(rcsPermission.getName());
		if (aggregate == null)
			return false;

		Enumeration<ResourcePermission> enum11 = aggregate.elements();
		while (enum11.hasMoreElements()) {
			ResourcePermission p = enum11.nextElement();
			if (p.implies(permission))
				return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.security.PermissionCollection#add(java.security.Permission)
	 */
	public void add(Permission permission) {
		if (isReadOnly())
			throw new IllegalArgumentException("Read only collection");

	}

	/**
	 * This is called when the same name is added twice to the collection. The
	 * actions are combine and the name is only stored once in the collection.
	 * 
	 * @param a
	 *            The first resouce permission.
	 * @param b
	 *            The first resouce permission.
	 * @return
	 */
	/*
	 * private ResourcePermission merge(ResourcePermission arg0,
	 * ResourcePermission arg1) { return null; }
	 */

}
