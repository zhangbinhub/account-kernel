/*
 * Created on 2005-3-31
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.workflow.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import OLink.bpm.core.workflow.storage.runtime.ejb.ActorRT;
import OLink.bpm.core.workflow.storage.runtime.ejb.NodeRT;

/**
 * @author ZhouTY
 * 
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class NameList {
	public final static int OPTION_OR = 1;

	public final static int OPTION_AND = 2;

	private Collection<Object> data = new Vector<Object>();

	private int option;

	public NameList() {
	}

	public NameList(int option) {
		setOption(option);
	}

	/**
	 * @return Returns the option.
	 */
	public int getOption() {
		return option;
	}

	/**
	 * @param option
	 *            The option to set.
	 */
	public void setOption(int option) {
		this.option = option;
	}

	public void add(NameList nl) {
		if (nl != null) {
			data.add(nl);
		}
	}

	public void add(NameNode name) {
		if (name != null) {
			data.add(name);
		}
	}

	public boolean remove(NameNode node) {

		if (data.contains(node)) {
			if (option == NameList.OPTION_OR) {
				data.clear();
			} else {
				data.remove(node);
			}
			return true;
		}

		boolean flag = false;
		Iterator<Object> iter = data.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof NameList) {
				NameList subNameList = (NameList) element;
				flag = subNameList.remove(node);
				if (flag) {
					if (subNameList.option == NameList.OPTION_OR) {
						data.remove(subNameList);
						break;
					} else if (subNameList.data.size() == 0) {
						data.remove(subNameList);
						break;
					} else {
						flag = false;
					}
				}
			}
		}
		return flag;
	}

	public void replaceFirst(NameNode source, NameNode target) {
	}

	public void replaceAll(NameNode source, NameNode target) {
	}

	public String toString() {
		return toString(false);
	}

	public String toString(boolean onlyId) {
		if (data == null || data.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		if (option == OPTION_AND) {
			sb.append("{");
		} else if (option == OPTION_OR) {
			sb.append("(");
		}

		Iterator<Object> iter = data.iterator();
		while (iter.hasNext()) {
			Object em = iter.next();
			if (em instanceof NameList) {
				NameList nl = (NameList) em;
				sb.append(nl.toString(onlyId));
			} else if (em instanceof NameNode) {
				NameNode nn = (NameNode) em;
				sb.append(nn.toString(onlyId));
			}

		}

		if (option == OPTION_OR) {
			sb.append(")");
		} else if (option == OPTION_AND) {
			sb.append("}");
		}

		return sb.toString();
	}

	public Collection<String[]> toCollection() {
		if (data == null || data.isEmpty()) {
			return null;
		}

		Collection<String[]> colls = new ArrayList<String[]>();
		Iterator<Object> iter = data.iterator();
		while (iter.hasNext()) {
			Object em = iter.next();
			if (em instanceof NameList) {
				NameList nl = (NameList) em;
				colls.addAll(nl.toCollection());
			} else if (em instanceof NameNode) {
				NameNode nn = (NameNode) em;
				String[] names = new String[2];
				names[0] = nn.getId();
				names[1] = nn.getShortName();
				colls.add(names);
			}

		}
		return colls;
	}

	public Collection<NameNode> toNameNodeCollection() {
		if (data == null || data.isEmpty()) {
			return null;
		}

		Collection<NameNode> colls = new ArrayList<NameNode>();
		Iterator<Object> iter = data.iterator();
		while (iter.hasNext()) {
			Object em = iter.next();
			if (em instanceof NameList) {
				NameList nl = (NameList) em;
				colls.addAll(nl.toNameNodeCollection());
			} else if (em instanceof NameNode) {
				NameNode nn = (NameNode) em;
				colls.add(nn);
			}

		}
		return colls;
	}

	public static NameList parser(String nameListStr) {
		// "[]"代表或关系，"{}"代表与关系
		NameList nameList = new NameList();
		if (nameListStr == null || nameListStr.trim().length() <= 0) {
			return nameList;
		}

		Stack<NameList> stack = new Stack<NameList>();

		nameListStr = nameListStr.trim();
		char[] charlist = nameListStr.toCharArray();

		if (charlist[0] != '{' && charlist[0] != '[') {
			charlist = ("{" + nameListStr + "}").toCharArray();
		}

		StringBuffer tmp = new StringBuffer();
		NameList rootNameList = null;

		NameList pushNameList = null;
		NameList peekNameList = null;

		for (int i = 0; i < charlist.length; i++) {

			char c = charlist[i];

			switch (c) {
			case '{':
				pushNameList = new NameList(NameList.OPTION_AND);
				if (!stack.isEmpty()) {
					rootNameList = stack.peek();
					rootNameList.add(pushNameList);
				} else {
					rootNameList = pushNameList;
				}
				stack.push(pushNameList);
				break;
			case '(':
				pushNameList = new NameList(NameList.OPTION_OR);
				if (!stack.isEmpty()) {
					rootNameList = stack.peek();
					rootNameList.add(pushNameList);
				} else {
					rootNameList = pushNameList;
				}
				stack.push(pushNameList);
				break;
			case ')':
			case '}':
				if (tmp != null && tmp.length() > 0) {
					peekNameList = stack.peek();
					NameNode node = new NameNode(tmp.toString());
					peekNameList.add(node);
					tmp = new StringBuffer();
				}
				stack.pop();
				break;
			case ';':
				if (tmp != null && tmp.length() > 0) {
					peekNameList = stack.peek();
					NameNode node = new NameNode(tmp.toString());
					peekNameList.add(node);
					tmp = new StringBuffer();
				}
				break;

			default:
				tmp.append(c);
				break;
			}

		}

		if (stack != null && stack.isEmpty()) {
			return rootNameList;
		} else {
			return null;
		}
	}

	public boolean isContains(NameNode node) {
		boolean flag = false;
		Iterator<Object> iter = data.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element instanceof NameList) {
				NameList subNameList = (NameList) element;
				flag = subNameList.isContains(node);
				if (flag) {
					return true;
				}
			} else if (element instanceof NameNode) {
				NameNode nameNode = (NameNode) element;
				flag = nameNode.equals(node);
				if (flag) {
					break;
				}
			}
		}
		return flag;
	}

	public boolean isMatchCondition(NodeRT nodert) throws Exception {
		if (data != null && nodert != null) {
			if (getOption() == OPTION_OR) {
				Iterator<Object> iter = data.iterator();
				while (iter.hasNext()) {
					Object element = iter.next();

					if (element instanceof NameNode) {
						// NameNode nameNode = (NameNode) element;
						Iterator<ActorRT> iter2 = nodert.getActorrts()
								.iterator();
						while (iter2.hasNext()) {
							ActorRT actrt = iter2.next();
							if (actrt.getIsprocessed()) {
								return true;
							}
						}
					} else if (element instanceof NameList) {
						NameList nameList = (NameList) element;
						if (nameList.isMatchCondition(nodert))
							return true;
					}
				}
				return false;
			} else if (getOption() == OPTION_AND) {
				Iterator<Object> iter = data.iterator();
				while (iter.hasNext()) {
					Object element = iter.next();

					if (element instanceof NameNode) {
						// NameNode nameNode = (NameNode) element;
						Iterator<ActorRT> iter2 = nodert.getActorrts()
								.iterator();
						boolean flag = false;
						while (iter2.hasNext()) {
							ActorRT actrt = iter2.next();
							if (!actrt.getIsprocessed()) {
								flag = true;
								break;
							}
						}
						if (!flag)
							return false;
					} else if (element instanceof NameList) {
						NameList nameList = (NameList) element;
						if (!nameList.isMatchCondition(nodert))
							return false;
					}
				}
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean isEmpty() {
		return data == null || data.isEmpty();
	}

	public static void main(String[] args) {
		/*
		 * if (false) { String nlStr =
		 * "{A001|a;B001|b;({C001|c;D001|d};{E001|e;F001|f});(G001|g;H001|h);I001|i}"
		 * ; NameList nl = NameList.parser(nlStr); NameList nl2 =
		 * NameList.parser(nl + ""); NameNode nn = new NameNode("C001");
		 * nl.remove(nn); NameNode nn2 = new NameNode("D001"); nl.remove(nn2);
		 * NameNode nn3 = new NameNode("E001"); nl.remove(nn3); NameNode nn4 =
		 * new NameNode("A001"); nl.remove(nn4); NameNode nn5 = new
		 * NameNode("G001"); nl.remove(nn5); NameNode nn6 = new
		 * NameNode("B001"); nl.remove(nn6); } if (false) { String nlStr =
		 * "{U|amdin;D-05-02|副总师;}"; NameList nl = NameList.parser(nlStr);
		 * NameNode nn = new NameNode("D-05-02"); nl.remove(nn); } if (false) {
		 * NameList nl = new NameList(); nl.setOption(NameList.OPTION_AND);
		 * nl.add(new NameNode("U|admin")); nl.add(new NameNode("D-05-03|总部"));
		 * NameNode nn = new NameNode("U"); nl.remove(nn); } if (false) { //
		 * String nlStr = "{A001|a;(U|u;D-05-02|d;U|u;)(U|u;U-01-02|u2;)}"; //
		 * String nlStr = "{(a;b);(c;d)}"; // NameList nl = new NameList(); //
		 * nl.setOption(NameList.OPTION_OR); // nl.add(NameList.parser(nlStr));
		 * // nl.add(NameList.parser("({U;D-05-02;}U;{U;})")); NameList nl =
		 * NameList.parser("{U;}"); NameNode nn = new NameNode("U"); } if
		 * (false) { NameList nl =
		 * NameList.parser("(U-05-03-002|陈红霞;U-05-03-003|熊光辉;)"); Collection
		 * colls = nl.toCollection(); Iterator iters = colls.iterator(); while
		 * (iters.hasNext()) { String[] names = (String[]) iters.next(); } } if
		 * (true) { // NameList nl = NameList.parser(null); // NameNode nn = new
		 * NameNode("U"); }
		 */
	}

	public Collection<Object> getData() {
		return data;
	}

	public void setData(Collection<Object> data) {
		this.data = data;
	}
}
