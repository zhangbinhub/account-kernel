/*
 * Created on 2005-3-31
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.workflow.utility;

import OLink.bpm.core.workflow.storage.runtime.ejb.Type;

/**
 * @author ZhouTY
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NameNode {
	private String text;

	// private String name;

	public NameNode(String text) {
		this.text = text;
	}

	// 获取ID（角色或人员）
	public String getId() {
		if (text == null || text.trim().equals("")) {
			return null;
		}
		String[] tmp = CommonUtil.split(text, '|');
		if (tmp != null && tmp.length >= 1) {
			return tmp[0].substring(1);
		} else {
			return null;
		}
	}

	// 获取部门字串
	public String getDept() {
		if (text == null || text.trim().equals("")) {
			return null;
		}
		String[] tmp = CommonUtil.split(text, '|');
		if (tmp != null && tmp.length >= 2) {
			String part2 = tmp[1];
			int pos2 = part2.lastIndexOf("/");
			if (pos2 > 0) {
				return part2.substring(0, pos2);
			}
		}
		return null;
	}

	// 获取角色字串（角色或人员）
	public String getShortName() {
		if (text == null || text.trim().equals("")) {
			return null;
		}
		String[] tmp = CommonUtil.split(text, '|');
		if (tmp != null && tmp.length >= 2) {
			String part = tmp[1];
			int lastpos = part.lastIndexOf("/");
			if (lastpos > 0 && lastpos + 1 <= part.length()) {
				return part.substring(lastpos + 1, part.length());
			} else {
				return part;
			}
		}
		return null;
	}

	/**
	 * @return Returns the name.
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setText(String text) {
		this.text = text;
	}

	public boolean equals(Object obj) {
		if (obj instanceof NameNode) {
			NameNode node = (NameNode) obj;
			String id1 = node.getId();
			String id2 = getId();
			boolean flag = id1 != null && id1.trim().length() > 0
					&& id2 != null && id2.trim().length() > 0
					&& id1.equals(id2);
			return flag;
		}
		return false;
	}

	public int hashCode() {
		return super.hashCode();
	}

	public String toString(boolean onlyId) {
		if (text != null && text.trim().length() > 0) {
			if (onlyId) {
				return getId() + ";";
			} else {
				return text + ";";
			}
		}
		return "";
	}

	public String toString() {
		return toString(false);
	}

	public int getType() {
		if (text != null && text.length() > 1) {
			char t = text.charAt(0);
			switch (t) {
			case 'D':
				return Type.TYPE_DEPARTMENT;
			case 'U':
				return Type.TYPE_USER;
			case 'R':
				return Type.TYPE_ROLE;
			}
		}
		return -1;
	}

	public static void main(String[] args) {
		/*
		 * String s = "A001|a/b/c"; NameNode n = new NameNode(s);
		 */
		/*
		 * n.toString(true); n.getId(); n.getDept(); n.getShortName();
		 */

	}

}
