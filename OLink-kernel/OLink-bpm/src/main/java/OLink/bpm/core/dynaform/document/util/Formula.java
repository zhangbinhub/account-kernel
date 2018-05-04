/*
 * Created on 2005-4-20
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package OLink.bpm.core.dynaform.document.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

/**
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class Formula implements Element {
	public final static int NO_COUNT = 1;

	public final static int OPTION_UNDEFINED = 0;

	public final static int OPTION_OR = 1;

	public final static int OPTION_AND = 2;

	public final static String _AUTH_CLASSNAME = "OLink.bpm.core.authority.ejb.Authority";

	public final static String _DOCUMENT_CLASSNAME = "Document";

	private Collection<Element> data = new Vector<Element>();

	private int option;

	public Formula() {
	}

	public Formula(int option) {
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

	public void add(Formula nl) {
		if (nl != null) {
			data.add(nl);
		}
	}

	public void add(Expression name) {
		if (name != null) {
			data.add(name);
		}
	}

	public String toString() {
		if (data == null || data.isEmpty()) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		sb.append("(");

		Iterator<Element> iter = data.iterator();
		while (iter.hasNext()) {
			Object em = iter.next();
			if (sb.length() > 0) {
				char lastChar = sb.charAt(sb.length() - 1);
				if (lastChar != '(') {

					if (data.size() > 1)
						if (getOption() == OPTION_AND) {
							sb.append(" AND ");
						} else if (getOption() == OPTION_OR) {
							sb.append(" OR ");
						}
				}

			}
			sb.append(em.toString());
		}

		sb.append(")");

		return sb.toString();

	}

	// public String toSqlString() {
	// int noA = NO_COUNT ++;
	// int noB = NO_COUNT ++;
	// String causeA = "";
	// String causeB = "";
	// if (data == null || data.isEmpty()) {
	// return "";
	// }
	// // StringBuffer sb = new StringBuffer();
	// // sb.append("(");
	//
	// Iterator iter = data.iterator();
	// if (iter.hasNext()) {
	// Element em = (Element)iter.next();
	// causeA = em.toSqlString();
	// }
	//
	// if (iter.hasNext()) {
	// Element em = (Element)iter.next();
	// causeB = em.toSqlString();
	// }
	//
	// // sb.append(") ");
	//
	// String where = null;
	//		
	// if (getOption() == OPTION_AND) {
	// if (causeA!=null && causeA.length()>0 && causeB!=null &&
	// causeB.length()>0) {
	// where = "SELECT T$_" + noA + ".ID ID FROM (" + causeA + ") T$_" + noA +
	// ", (" + causeB + ") T$_" + noB + " WHERE T$_" + noA + ".ID = T$_" + noB +
	// ".ID";
	// }
	// else if (causeA!=null && causeA.length()>0) {
	// where = "SELECT T$_" + noA + ".ID ID FROM (" + causeA + ") T$_" + noA + "
	// ";
	// }
	// else if (causeB!=null && causeB.length()>0) {
	// where = "SELECT T$_" + noB + ".ID ID FROM (" + causeB + ") T$_" + noB + "
	// ";
	// }
	// }
	// else {
	// if (causeA!=null && causeA.length()>0 && causeB!=null &&
	// causeB.length()>0) {
	// where = causeA + " UNION " + causeB;
	// }
	// else if (causeA!=null && causeA.length()>0) {
	// where = causeA;
	// }
	// else if (causeB!=null && causeB.length()>0) {
	// where = causeB;
	// }
	// }
	//
	//		
	// return where;
	// }

	public String toSqlString() {
		StringBuffer where = new StringBuffer();
		if (data == null || data.isEmpty()) {
			return "";
		}
		String[] cause = new String[data.size()];
		Object obj[] = data.toArray();
		for (int i = 0; i < obj.length; i++) {
			Element em = (Element) obj[i];
			cause[i] = em.toSqlString();
		}

		if (getOption() == OPTION_AND) {
			// String whereEquals = "";
			for (int i = 0; i < cause.length; i++) {
				if (cause[i] != null && cause[i].length() > 0) {
					if (i < cause.length - 1) {
						where.append("(" + cause[i] + ") AND ");
					} else {
						where.append("(" + cause[i] + ") ");
					}
				}
			}
		} else if (getOption() == OPTION_OR) {
			for (int i = 0; i < cause.length; i++) {
				if (cause[i] != null && cause[i].length() > 0) {
					if (i < cause.length - 1) {
						where.append("(" + cause[i] + ") OR ");
					} else {
						where.append("(" + cause[i] + ") ");
					}
				}
			}
		} else {
			for (int i = 0; i < cause.length; i++) {
				if (cause[i] != null && cause[i].length() > 0) {
					where.append("(" + cause[i] + ") ");
				}
			}
		}

		return where.toString();
	}

	public static Formula parser(String formulaStr) throws Exception {
		// "[]"代表或关系，"{}"代表与关系
		Formula rootFormula = new Formula();
		if (formulaStr == null || formulaStr.trim().length() <= 0) {
			return rootFormula;
		}
		formulaStr = "(" + formulaStr + ")";

		Stack<Formula> stack = new Stack<Formula>();
		stack.push(rootFormula);

		formulaStr = formulaStr.trim();
		char[] charlist = formulaStr.toCharArray();
		StringBuffer tmp = new StringBuffer();

		for (int i = 0; i < charlist.length; i++) {

			char c = charlist[i];

			switch (c) {
			case ' ':
				while (charlist[i + 1] == ' ') {
					i++;
				}
				if (charlist.length > i + 3)
					if (charlist[i + 1] == 'A' || charlist[i + 1] == 'a')
						if (charlist[i + 2] == 'N' || charlist[i + 2] == 'n')
							if (charlist[i + 3] == 'D'
									|| charlist[i + 3] == 'd') {
								// 处理AND

								Expression exp = null;
								if (tmp.length() > 0) {
									exp = new Expression(tmp.toString());
								}

								Formula peek = stack.peek();

								if (peek.getOption() == OPTION_AND) {
									if (exp != null) {
										peek.add(exp);
									}

								} else if (peek.getOption() == OPTION_UNDEFINED) {
									peek.setOption(OPTION_AND);
									if (exp != null) {
										peek.add(exp);
									}
								} else {
									Formula fml = new Formula(OPTION_AND);
									if (exp != null) {
										peek.add(exp);
									}
									fml.add(peek);
									stack.pop();
									stack.push(fml);
									rootFormula = fml;
								}

								i += 3;
								tmp = new StringBuffer();
								break;
							}
				if (charlist.length > i + 2)
					if (charlist[i + 1] == 'O' || charlist[i + 1] == 'o')
						if (charlist[i + 2] == 'R' || charlist[i + 2] == 'r') {
							// 处理OR
							Expression exp = null;
							if (tmp.length() > 0) {
								exp = new Expression(tmp.toString());
							}

							Formula peek = stack.peek();

							if (peek.getOption() == OPTION_OR) {
								if (exp != null) {
									peek.add(exp);
								}

							} else if (peek.getOption() == OPTION_UNDEFINED) {
								peek.setOption(OPTION_OR);
								if (exp != null) {
									peek.add(exp);
								}
							} else {
								Formula fml = new Formula(OPTION_OR);
								if (exp != null) {
									peek.add(exp);
								}
								fml.add(peek);
								stack.pop();
								stack.push(fml);
								rootFormula = fml;
							}

							i += 2;
							tmp = new StringBuffer();

							break;
						}
				tmp.append(c);

				break;
			case '(': {
				Formula peek = stack.peek();

				Formula fml = new Formula(OPTION_UNDEFINED);
				peek.add(fml);
				stack.push(fml);
				// rootFormula = peek;
				break;
			}
			case ')': {
				Formula peek = stack.peek();
				Expression exp = null;
				if (tmp.toString().trim().length() > 0) {
					exp = new Expression(tmp.toString());
				}

				if (exp != null) {
					peek.add(exp);
				}
				tmp = new StringBuffer();

				break;
			}
			default:
				tmp.append(c);
				break;
			}

		}
		return rootFormula;
	}

	public boolean isEmpty() {
		return data == null || data.isEmpty();
	}

	public static void main(String[] args) {
		// String cndtn = "((($DOCID=123 AND $FORMNAME='TESTFORM') OR
		// (标题='hello' AND 内容 LIKE '%WORD%') AND 作者<>'zhouty' AND 密级='0')))";
		// String cndtn = "$DOCID=123 AND $FORMNAME='TESTFORM' OR 标题='hello' AND
		// 内容 LIKE '%WORD%' AND 作者<>'zhouty' AND 密级='0'";
		// String cndtn = "$A=1 OR B=2 AND C=3 OR D=4 OR E=5 AND F=7 AND G=7";
		// String cndtn = "(((A=1 AND B=2) OR C=3) AND D=4 OR E=5) OR (F=7 OR
		// G=8) ";//AND H=9 OR I=10";
		// String cndtn = "(A=1 AND B=2) OR C=3";
		// String cndtn = "(($OWNER='D' AND $FORMNAME='TESTFORM') OR (标题='hello'
		// AND 内容 LIKE '%WORD%'))";
		// String cndtn = "( $channelid = 1120196566387000 ) AND ( $formname
		// like 'OilWellZuanJingShengChanRiBao' ) AND ( 井号 like '%花2-4%' ) ";
		// String cndtn = "((商品名称 LIKE '%一%') AND (商品类别='音像'))";
		// String cndtn = "((商品名称 LIKE '%一%') AND (商品类别='音像'))";
		// String cndtn = " ($owner in 'D-07',D-07-02','D-07-03','D-07-02-01'
		// and $auditusers like '%U-07-022%' and $authorid = 'U-07-022' and
		// $state!=0) or ($authorid = 'U-07-022' and $state=0) ";
		// String cndtn = " ($formname = 'D-07' and area like '%U-07-022%' )";
		// String cndtn =
		// "$formname='app01/mod01/mod01-02/order' and $author.departments.id in ('1163050869292000','1163050973674000')";
		// String cndtn =
		// "($formname='WPI-GB/Trade Expense/PWP Form/fm_wpigb_pwp' and region in ('1165461097109000','1165461068958000','1165461077741000','1165461107814000','1165658369312000','1165460750500000','1165658347310000','1165415680832000','1165461090359000'))";
		// String cndtn = "region in ('123')";
		String cndtn = "(($formname='WPI-GB/Trade Expense/CCDA Form/fm_wpigb_ccda') and (($state.state = 256) or ($state.state = 4096)) and ($state.actors.actorid='1165461090359000'))";
		try {
			Formula.parser(cndtn);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
