package OLink.bpm.core.dynaform.view.ejb;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.util.StringUtil;

public class FilterExpression {
	String left;

	String right;

	String operator;

	public final static String DOCUMENT_CLASSNAME = "Document";

	public final static String ITEM_CLASSNAME = "Item";
   /**
    * 根据视图过滤条件以及参数转变成HQL语句
    * @param text 视图过滤条件
    * @param params 参数
    * @return
    */
	public String parseToHql(String text, ParamsTable params) {
		int fromIndex = 0;

		StringBuffer hql = new StringBuffer();
		hql.append("FROM " + DOCUMENT_CLASSNAME
				+ " WHERE istmp <> true AND id IN(");
		hql.append("SELECT doc.id FROM " + DOCUMENT_CLASSNAME + " doc WHERE ");

		int cdCount = 0;
		while (text.indexOf("{", fromIndex) != -1) {
			int start = text.indexOf("{", fromIndex);
			int end = text.indexOf("}", start);
			String expr = text.substring(start + 1, end);

			String[] mapStrs = expr.split(",");
			if (mapStrs.length > 3) {
				left = mapStrs[0];
				operator = mapStrs[1];
				right = mapStrs[3];

				String varcharvalue = params
						.getParameterAsString(getValue(right));
				if (cdCount > 0 && varcharvalue != null) {
					hql.append(" AND ");
				}

				if (getValue(left).startsWith("$")) {
					String leftVal = getValue(left).replaceAll("[$]", "");
					hql.append("(doc." + leftVal + " ");
					hql.append(getValue(operator) + " '" + getValue(right)
							+ "')");
				} else {
					if (varcharvalue != null) {
						hql.append("(doc.id IN (SELECT items.document FROM "
								+ ITEM_CLASSNAME + " items ");
						hql
								.append("WHERE items.name='" + getValue(left)
										+ "' ");

						hql.append("AND items.varcharvalue ");

						hql.append(getValue(operator) + " '" + varcharvalue
								+ "'))");
					}
				}
				cdCount++;
			}
			fromIndex = end;
		}
		hql.append(")");

		if (cdCount > 0) {
			return hql.toString();
		}

		return "";
	}

	private String getValue(String mapStr) {
		mapStr = StringUtil.dencodeHTML(mapStr);
		int index = mapStr.indexOf(":");
		String value = mapStr.substring(index + 1, mapStr.length());
		value = value.substring(value.indexOf("'") + 1, value.lastIndexOf("'"));
		return value;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}

	public static void main(String[] args) {
//		String text0 = "[{field:'產品代碼ID',operator:'=',type:'00',match:'fsdfsdf'},{field:'標準成本',operator:'LIKE',type:'00',match:'fsdfsdsdf'}]";
//		String text1 = "[{$formname:'WPI-GB/Trade Expense/CCDA Form/fm_wpigb_ccda',operator:'=',type:'00',match:'fsdfsdf'},{field:'標準成本',operator:'LIKE',type:'00',match:'fsdfsdsdf'}]";
//		FilterExpression fe = new FilterExpression();
//		String hql = fe.parseToHql(text0, new ParamsTable());
	}

}
