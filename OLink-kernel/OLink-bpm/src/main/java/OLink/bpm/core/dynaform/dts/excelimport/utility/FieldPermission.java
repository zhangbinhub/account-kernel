/*
 * Created on 2005-4-4
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.dts.excelimport.utility;

import OLink.bpm.util.StringUtil;

/**
 *         comment go to Window - Preferences - Java - Code Style - Code
 *         Templates
 */
public class FieldPermission {

	private int permisstionType;
	private String fieldName;

	public FieldPermission() {

	}

	public FieldPermission(String fieldPermStr) {
		if (!StringUtil.isBlank(fieldPermStr)) {
			if (fieldPermStr.indexOf("@") == 0) {
				this.permisstionType = PermissionType.READONLY;
			} else if (fieldPermStr.indexOf("#") == 0) {
				this.permisstionType = PermissionType.MODIFY;
			} else if (fieldPermStr.indexOf("$") == 0) {
				this.permisstionType = PermissionType.HIDDEN;
			}
			this.fieldName = fieldPermStr.substring(1);
		}
	}

	/**
	 * @return Returns the permisstionType.
	 * @uml.property name="permisstionType"
	 */
	public int getPermisstionType() {
		return permisstionType;
	}

	/**
	 * @param permisstionType
	 *            The permisstionType to set.
	 * @uml.property name="permisstionType"
	 */
	public void setPermisstionType(int permisstionType) {
		this.permisstionType = permisstionType;
	}

	/**
	 * @return Returns the fieldName.
	 * @uml.property name="fieldName"
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            The fieldName to set.
	 * @uml.property name="fieldName"
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String toString() {
		String str = "";
		if (this.permisstionType == PermissionType.READONLY) {
			str += "@";
		} else if (this.permisstionType == PermissionType.MODIFY) {
			str = "#";
		} else if (this.permisstionType == PermissionType.HIDDEN) {
			str = "$";
		}
		str += this.fieldName;
		return str;
	}

	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
		FieldPermission fieldPerm = (FieldPermission) obj;
		int type1 = fieldPerm.getPermisstionType();
		int type2 = getPermisstionType();
		String name1 = fieldPerm.getFieldName();
		String name2 = getFieldName();
		boolean flag = (type1 == type2 && name1 != null
				&& name1.trim().length() > 0 && name2 != null
				&& name2.trim().length() > 0 && name1.equals(name2));
		return flag;
	}

	@Override
	public int hashCode() {
		return this.fieldName.hashCode()*31;
	}
	
	
}
