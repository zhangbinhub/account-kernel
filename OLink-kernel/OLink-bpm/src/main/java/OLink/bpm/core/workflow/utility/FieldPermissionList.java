/*
 * Created on 2005-4-4
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.workflow.utility;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.form.ejb.FormField;

/**
 * @author Administrator
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class FieldPermissionList implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8890840537780236585L;

	private Collection<FieldPermission> data = new ArrayList<FieldPermission>();

	public FieldPermissionList() {

	}

	public void add(FieldPermission fp) {
		if (fp != null) {
			data.add(fp);
		}
	}

	public static FieldPermissionList parser(String permissionListStr) {
		FieldPermissionList permissionList = new FieldPermissionList();
		if (permissionListStr == null || permissionListStr.trim().length() <= 0) {
			return permissionList;
		}
		FieldPermission tmp = null;
		String[] permissionArray = CommonUtil.split(permissionListStr, ";");
		for (int i = 0; i < permissionArray.length; i++) {
			tmp = new FieldPermission(permissionArray[i]);
			permissionList.add(tmp);
		}

		return permissionList;
	}

	public void remove(FieldPermission fieldPerm) {
		data.remove(fieldPerm);
	}

	public void clear() {
		data.clear();
	}

	public int checkPermission(FormField formField) {
		return this.checkPermission(formField.getName());
	}

	public int checkPermission(String fieldName) {
		if (data == null || data.size() <= 0) {
			return PermissionType.MODIFY;
		}
		Iterator<FieldPermission> iters = data.iterator();
		while (iters.hasNext()) {
			FieldPermission fieldPerm = iters.next();
			if (fieldName != null && fieldName.equals(fieldPerm.getFieldName())) {
				return fieldPerm.getPermisstionType();
			}
		}
		return PermissionType.MODIFY;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Iterator<FieldPermission> iter = data.iterator();
		while (iter.hasNext()) {
			FieldPermission fieldPerm = iter.next();
			sb.append(fieldPerm.toString());
			sb.append(";");
		}
		return sb.toString();
	}

	public static void main(String[] args) {
		String str = "@a;#b;$c;@d;$e;#f";
		FieldPermissionList fieldPermList = FieldPermissionList.parser(str);
		FieldPermission fieldPerm = new FieldPermission("#b");
		fieldPermList.remove(fieldPerm);
	}
}
