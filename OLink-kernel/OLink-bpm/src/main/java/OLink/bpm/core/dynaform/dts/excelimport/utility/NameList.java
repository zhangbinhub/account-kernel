/*
 * Created on 2005-3-31
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.dts.excelimport.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class NameList {
	public final static int OPTION_OR = 1;
	public final static int OPTION_AND = 2;
	private static final Logger log = Logger.getLogger(NameList.class);
	private Collection<Object> data = new Vector<Object>();
	private int option;

	public NameList() {
	}

	public NameList(int option) {
		setOption(option);
	}

	/**
	 * @return Returns the option.
	 * @uml.property name="option"
	 */
	public int getOption() {
		return option;
	}

	/**
	 * @param option
	 *            The option to set.
	 * @uml.property name="option"
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

	public static NameList parser(String nameListStr) {
		// "[]"代表或关系，"{}"代表与关系
		NameList nameList = new NameList();
		if (nameListStr == null || nameListStr.trim().length() <= 0) {
			return nameList;
		}

		Stack<NameList> stack = new Stack<NameList>();

		nameListStr = nameListStr.trim();
		char[] charlist = nameListStr.toCharArray();
		StringBuffer tmp = new StringBuffer();
		NameList rootNameList = null;

		NameList pushNameList = null;
		// NameList popNameList = null;
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
		if (data.contains(node)) {
			return true;
		}

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
			}
		}
		return flag;
	}

	// public boolean isPerm(WebUser webUser, boolean canAgency){
	// String[] allId = webUser.getAllUserId(canAgency);
	// String[] allGroupid = webUser.getAllUserGroupId(canAgency);
	// String[] allDeptid = webUser.getAllUserDeptId(canAgency);
	// for(int i=0; i<allId.length; i++){
	// NameNode node = new NameNode(allId[i]);
	// if(this.isContains(node)){
	// return true;
	// }
	// }
	// for(int i=0; i<allGroupid.length; i++){
	// NameNode node = new NameNode(allGroupid[i]);
	// if(this.isContains(node)){
	// return true;
	// }
	// }
	// for(int i=0; i<allDeptid.length; i++){
	// NameNode node = new NameNode(allDeptid[i]);
	// if(this.isContains(node)){
	// return true;
	// }
	// }
	// return false;
	// }
	//		
	// public void remove(WebUser webUser, boolean canAgency){
	// String[] allId = webUser.getAllUserId(canAgency);
	// String[] allGroupid = webUser.getAllUserGroupId(canAgency);
	// String[] allDeptid = webUser.getAllUserDeptId(canAgency);
	// for(int i=0; i<allId.length; i++){
	// NameNode nuser = new NameNode(allId[i]);
	// this.remove(nuser);
	// }
	// for(int i=0; i<allGroupid.length; i++){
	// NameNode ngroup = new NameNode(allGroupid[i]);
	// this.remove(ngroup);
	// }
	// for(int i=0; i<allDeptid.length; i++){
	// NameNode ndept = new NameNode(allDeptid[i]);
	// this.remove(ndept);
	// }
	// }

	public boolean isEmpty() {
		return data == null || data.isEmpty();
	}

	public static void main(String[] args) {
		// String nlStr = "(U|admin;D-05-02|副总师;)";
		// NameList nl = NameList.parser(nlStr);
		// NameList nl2 = NameList.parser(nl.toString());
		/*
		 * if (false) { String nlStr =
		 * "{A001|a;B001|b;({C001|c;D001|d};{E001|e;F001|f});(G001|g;H001|h);I001|i}"
		 * ; log.info("NameList->"+nlStr); NameList nl = NameList.parser(nlStr);
		 * log.info("NameList->"+nl); NameList nl2 = NameList.parser(nl+"");
		 * log.info("NameList->"+nl2);
		 * 
		 * NameNode nn = new NameNode("C001"); nl.remove(nn);
		 * log.info("NameList->"+nl);
		 * 
		 * NameNode nn2 = new NameNode("D001"); nl.remove(nn2);
		 * log.info("NameList->"+nl);
		 * 
		 * NameNode nn3 = new NameNode("E001"); nl.remove(nn3);
		 * log.info("NameList->"+nl);
		 * 
		 * NameNode nn4 = new NameNode("A001"); nl.remove(nn4);
		 * log.info("NameList->"+nl);
		 * 
		 * NameNode nn5 = new NameNode("G001"); nl.remove(nn5);
		 * log.info("NameList->"+nl);
		 * 
		 * NameNode nn6 = new NameNode("B001"); nl.remove(nn6);
		 * log.info("NameList->"+nl); } if(false) { String nlStr =
		 * "{U|amdin;D-05-02|副总师;}"; NameList nl = NameList.parser(nlStr);
		 * NameNode nn = new NameNode("D-05-02"); nl.remove(nn); } if (false) {
		 * NameList nl = new NameList(); nl.setOption(NameList.OPTION_AND);
		 * 
		 * nl.add(new NameNode("U|admin")); nl.add(new NameNode("D-05-03|总部"));
		 * 
		 * NameNode nn = new NameNode("U"); nl.remove(nn);
		 * 
		 * } if(false) { // String nlStr =
		 * "{A001|a;(U|u;D-05-02|d;U|u;)(U|u;U-01-02|u2;)}"; //// String nlStr =
		 * "{(a;b);(c;d)}"; // // NameList nl = new NameList(); //
		 * nl.setOption(NameList.OPTION_OR); // //
		 * nl.add(NameList.parser(nlStr)); //
		 * nl.add(NameList.parser("({U;D-05-02;}U;{U;})"));
		 * 
		 * NameList nl = NameList.parser("{U;}"); NameNode nn = new
		 * NameNode("U"); log.info("isPerm->"+nl.isContains(nn)); } if(false) {
		 * NameList nl = NameList.parser("(U-05-03-002|陈红霞;U-05-03-003|熊光辉;)");
		 * log.info("NameList--->"+nl);
		 * log.info("NameList2--->"+NameList.parser(nl.toString())); Collection
		 * colls = nl.toCollection(); Iterator iters = colls.iterator();
		 * while(iters.hasNext()){ String[] names = (String[])iters.next();
		 * log.info("id-->"+names[0]); log.info("name-->"+names[1]); } }
		 */
		// if(true){
		// try{
		// UserProxy userProxy = new UserProxy();
		// UserVO userVO = userProxy.doView("U-06-004");
		// WebUser user = new WebUser(userVO);
		// NameList nl =
		// NameList.parser("({U-05-08-007|甘洪霖;U-05-08-006|江海;}(U-05-03-002|陈红霞;U-05-03-003|熊光辉;)(D-06|测试部;))");
		// NameNode nn = new NameNode("U-06-004");
		// log.info("isPerm->"+ nl.isContains(nn));
		// log.info("isPerm->"+ nl.isPerm(user, true));
		// log.info("NameList2***--->"+nl.toString());
		// }catch(Exception ex){
		// ex.printStackTrace();
		// }
		// }

		if (true) {
			NameList nl = NameList.parser(null);
			log.info("NameList***--->" + nl);
			log.info("NameList2***--->" + NameList.parser(nl.toString()));
			NameNode nn = new NameNode("U");
			log.info("isPerm->" + nl.isContains(nn));
		}

	}
}
