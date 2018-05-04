package OLink.bpm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * The string list utility.
 */
public class StringList {
	private ArrayList<String> context = new ArrayList<String>();

	private char split;

	/**
	 * Construct the string list with default split tag ";"
	 * 
	 * @param s
	 *            The string.
	 */
	public StringList(String s) {
		this(s, ';');
	}

	public StringList() {

	}

	/**
	 * Construct the string list the special split tag.
	 * 
	 * @param s
	 *            The string.
	 * @param sp
	 *            The split tag
	 */
	public StringList(String s, char sp) {
		this.split = sp;
		String elm = null;
		int p = -1;

		if (s.length() == 0) {
			context.add("");
		} else {
			while (s.length() > 0) {
				p = s.indexOf(sp);
				if (p >= 0) {
					elm = s.substring(0, p);
					context.add(elm);
					s = s.substring(p + 1);
				} else {
					if (s.length() > 0)
						context.add(s);
					s = "";
				}
			}
		}
	}

	/**
	 * @param s
	 *            The split tag.
	 */
	public void setSplit(char s) {
		this.split = s;
	}

	/**
	 * @return The split tag.
	 */
	public char getSplit() {
		return split;
	}

	/**
	 * Add a string to stringlist.
	 * 
	 * @param s
	 *            The target string.
	 */
	public void add(String s) {
		if (s != null && s.length() >= 0) {
			add(new StringList(s));
		}
	}

	/**
	 * Append another string list .
	 * 
	 * @param sl
	 *            The target string list.
	 */
	public void add(StringList sl) {
		if (sl != null) {
			context.addAll(sl.toCollection());
		}
	}

	/**
	 * Remove the string from list.
	 * 
	 * @param s
	 *            The target list.
	 */
	public void remove(String s) {
		if (s != null && s.length() >= 0) {
			context.remove(s);
		}
	}

	/**
	 * Remove the string from list.
	 * 
	 * @param index
	 *            The string index.
	 */
	public void remove(int index) {
		if (index >= 0 && index < context.size()) {
			context.remove(index);
		}
	}

	/**
	 * Remove the sub string list.
	 * 
	 * @param sl
	 *            The sub string list.
	 */
	public void remove(StringList sl) {
		if (sl != null) {
			context.removeAll(sl.toCollection());
		}
	}

	/**
	 * Replace one string to another string in list
	 * 
	 * @param from
	 *            The target string.
	 * @param to
	 *            The replacement.
	 */
	public void replace(String from, String to) {
		for (int i = 0; i < context.size(); i++) {
			if (from.equals(context.get(i))) {
				context.set(i, to);
			}
		}
	}

	/**
	 * Replace one string to another string in list
	 * 
	 * @param index
	 *            The target string index.
	 * @param to
	 *            The replacement.
	 */
	public void replace(int index, String to) {
		context.set(index, to);
	}

	/**
	 * Sort the string list (descend).
	 */
	public void sort() {
		sort(false);
	}

	/**
	 * Sort the string list
	 * 
	 * @param desc
	 *            True for descend , false for ascend
	 */
	public void sort(boolean desc) {
		int i = 0, j = 1, len = context.size();
		if (len > 1) {
			String strTmp = null;
			for (i = 0; i < len - 1; i++) {
				for (j = i + 1; j < len; j++) {
					if (desc) {
						if (context.get(i)
								.compareTo(context.get(j)) < 0) {
							strTmp = context.get(i);
							context.set(i, context.get(j));
							context.set(j, strTmp);
						}
					} else {
						if (context.get(i)
								.compareTo(context.get(j)) > 0) {
							strTmp = context.get(i);
							context.set(i, context.get(j));
							context.set(j, strTmp);
						}
					}
				}
			}
		}
	}

	/**
	 * Remove the duplicate string in the list.
	 */
	public void unique() {
		ArrayList<String> v = new ArrayList<String>();
		String tmp;
		for (int i = 0; i < context.size(); i++) {
			tmp = context.get(i);
			if (!v.contains(tmp)) {
				v.add(tmp);
			}
		}
		context.clear();
		for (int i = 0; i < v.size(); i++) {
			context.add(v.get(i));
		}
	}

	/**
	 * Remove all the zero-length in the string.
	 */
	public void trim() {
		for (int i = 0; i < context.size(); i++) {
			if (context.get(i).equals("")) {
				context.remove(i);
			}
		}
	}

	/**
	 * Retrieve the position of the special string in the list.
	 * 
	 * @param s
	 *            The special string
	 * @return
	 */
	public int indexOf(String s) {
		return this.context.indexOf(s);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return toString(';');
	}

	/**
	 * Retrieve The whole string with the special split tag.
	 * 
	 * @param sp
	 *            The special split tag.
	 * @return The whole string.
	 */
	public String toString(char sp) {
		StringBuffer rtn = new StringBuffer();
		Iterator<String> iter = context.iterator();
		while (iter.hasNext()) {
			String item = iter.next();
			rtn.append(item);
			rtn.append(sp);
		}
		return rtn.toString();
	}

	/**
	 * Retrieve with string array from the string list.
	 * 
	 * @return The string array
	 */
	public String[] toStringArray() {
		String[] rtn = new String[context.size()];
		for (int i = 0; i < context.size(); i++) {
			rtn[i] = context.get(i);
		}
		return rtn;
	}

	/**
	 * Retrieve with the string collection from the string list.
	 * 
	 * @return The string collection
	 */
	public Collection<String> toCollection() {
		return context;
	}
}
