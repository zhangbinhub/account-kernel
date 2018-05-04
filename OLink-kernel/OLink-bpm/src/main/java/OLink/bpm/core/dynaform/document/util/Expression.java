/*
 * Created on 2005-4-20
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.document.util;

/**
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class Expression implements Element {

	private String text;

	private String left;

	private String right;

	private String join;

	public Expression() {

	}

	public Expression(String text) throws Exception {
		this.text = text;
		if (text != null && text.trim().length() > 0) {
			if (text.indexOf(">=") > 0) {
				String[] tmp = text.split(">=");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = ">=";
			} else if (text.indexOf("<=") > 0) {
				String[] tmp = text.split("<=");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = "<=";
			} else if (text.indexOf("=>") > 0) {
				String[] tmp = text.split("=>");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = "=>";
			} else if (text.indexOf("=<") > 0) {
				String[] tmp = text.split("=<");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = "=<";
			} else if (text.indexOf("!=") > 0) {
				String[] tmp = text.split("!=");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = "!=";
			} else if (text.indexOf("<>") > 0) {
				String[] tmp = text.split("<>");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = "<>";
			} else if (text.indexOf("=") > 0) {
				String[] tmp = text.split("=");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = "=";
			} else if (text.indexOf(">") > 0) {
				String[] tmp = text.split(">");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = ">";
			} else if (text.indexOf("<") > 0) {
				String[] tmp = text.split("<");
				left = tmp[0].trim();
				right = tmp[1].trim();
				join = "<";
			} else if (text.toLowerCase().indexOf(" like ") > 0) {
				int pos = text.toLowerCase().indexOf(" like ");
				left = text.substring(0, pos).trim();
				right = text.substring(pos + 6, text.length()).trim();
				join = "LIKE";
			} else if (text.toLowerCase().indexOf(" in ") > 0) {
				int pos = text.toLowerCase().indexOf(" in ");
				left = text.substring(0, pos).trim();
				right = "(" + text.substring(pos + 4, text.length()).trim()
						+ ")";
				join = "IN";
			} else if (text.toLowerCase().indexOf(" is ") > 0) {
				int pos = text.toLowerCase().indexOf(" is ");
				left = text.substring(0, pos).trim();
				right = "(" + text.substring(pos + 4, text.length()).trim()
						+ ")";
				join = "IS";
			} else {
				throw new Exception("Expression String Error!");
			}
		} else {
			throw new Exception("Expression String Error!");
		}
	}

	/**
	 * @return Returns the left.
	 * @uml.property name="left"
	 */
	public String getLeft() {
		return left;
	}

	/**
	 * @param left
	 *            The left to set.
	 * @uml.property name="left"
	 */
	public void setLeft(String left) {
		this.left = left;
	}

	/**
	 * @return Returns the right.
	 * @uml.property name="right"
	 */
	public String getRight() {
		return right;
	}

	/**
	 * @param right
	 *            The right to set.
	 * @uml.property name="right"
	 */
	public void setRight(String right) {
		this.right = right;
	}

	/**
	 * @return the text
	 * @uml.property name="text"
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            the text to set
	 * @uml.property name="text"
	 */
	public void setText(String text) {
		this.text = text;
	}

	public static void main(String[] args) {
	}

	/**
	 * @return Returns the join.
	 * @uml.property name="join"
	 */
	public String getJoin() {
		return join;
	}

	/**
	 * @param join
	 *            The join to set.
	 * @uml.property name="join"
	 */
	public void setJoin(String join) {
		this.join = join;
	}

	public String toString() {
		StringBuffer tmp = new StringBuffer();
		tmp.append(left);
		tmp.append(" ");
		tmp.append(join);
		tmp.append(" ");
		tmp.append(right);

		return tmp.toString();
		// return text;
	}

	public String toSqlString() {
		StringBuffer sql = new StringBuffer();
		if (left.trim().startsWith("$")) {// DOC 字段
			if (left.trim().startsWith("$owner")) {
				sql.append("SELECT authdocid id FROM "
						+ Formula._AUTH_CLASSNAME + " WHERE authvalue IN (");
				sql.append(right);
				sql.append(")");
				sql
						.append(" AND authtype='OWNER' AND authfrom = 'T_DYNAFORM_DOCUMENT'");
			} else {
				sql.append("doc.");
				sql.append(left.substring(1, left.length()));
				sql.append(" ");
				sql.append(join);
				sql.append(" ");
				sql.append(right);
			}
		} else {
			if (join.equals("LIKE")) {
				sql.append("doc.items.name='");
				sql.append(left);
				sql.append("' AND ");
				sql.append("doc.items.varcharvalue LIKE ");
				sql.append(right);
			} else {
				sql.append("doc.items.name='");
				sql.append(left);
				sql.append("' AND ");
				sql.append("(");
				sql.append("doc.items.varcharvalue ");
				sql.append(join);
				sql.append(" ");
				sql.append(right);

				sql.append(") ");
			}
		}

		return sql.toString();
	}
}
