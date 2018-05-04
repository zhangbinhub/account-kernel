package OLink.bpm.core.dynaform.view.ejb.condition;

import OLink.bpm.core.dynaform.document.dql.SQLFunction;
import OLink.bpm.util.DbTypeUtil;
import OLink.bpm.util.StringUtil;

public class DefaultConditionVisitor implements ConditionVisitor {
	StringBuilder conditions = new StringBuilder();

	private SQLFunction function;

	private String currentOperator;

	private String currentColumn;

	public DefaultConditionVisitor(String applicationid) {
		try {
			function = DbTypeUtil.getSQLFunction(applicationid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void visitColumnType(String columnType) {
	}

	public void visitColumn(String column) {
		currentColumn = column;
	}

	public void visitDateValue(String value, String datePattern) {
		if (!StringUtil.isBlank(value)) {
//			conditions.append(" AND " + currentColumn + " " + currentOperator + " ");
			conditions.append(" AND " + function.toChar(currentColumn,datePattern) + " " + currentOperator + " ");

			if (currentOperator.equals("LIKE")) {
				conditions.append("'%").append(value).append("%'");
			} else {
				if (currentOperator.equals("IN") || currentOperator.equals("NOT IN")) {
					if (value instanceof String) {
						String[] values = value.split(",");
						conditions.append("('");
						for (int i = 0; i < values.length; i++) {
//							conditions.append(function.toDate(values[i], datePattern) + ",");
							conditions.append(values[i] + ",");
						}
						conditions.deleteCharAt(conditions.lastIndexOf(","));
						conditions.append("')");
					} else {
						try {
//							conditions.append(function.toDate("'" + value + "'", datePattern));
							conditions.append("'"+value+"'");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
//					conditions.append(function.toDate("'" + value + "'", datePattern));
					conditions.append("'"+value+"'");
				}
			}
		}
	}

	public void visitNumberValue(String value) {
		if (!StringUtil.isBlank(value)) {
			conditions.append(" AND " + currentColumn + " " + currentOperator + " ");
			if (currentOperator.equals("LIKE")) {
				conditions.append("'%").append(value).append("%'");
			} else {
				if (currentOperator.equals("IN") || currentOperator.equals("NOT IN")) {
					String[] values = value.split(",");
					conditions.append("(");
					for (int i = 0; i < values.length; i++) {
						conditions.append(values[i] + ",");
					}
					conditions.deleteCharAt(conditions.lastIndexOf(","));
					conditions.append(")");
				} else {
					conditions.append(value);
				}
			}
		}
	}

	public void visitStringValue(String value) {
		if (!StringUtil.isBlank(value)) {
			String[] values = value.split(",");
			if (values.length > 0) {
				conditions.append(" AND " + currentColumn + " " + currentOperator + " ");
				if (currentOperator.equals("LIKE")) {
					conditions.append("'%").append(values[0]).append("%'");
				} else {
					if (currentOperator.equals("IN") || currentOperator.equals("NOT IN")) {
						conditions.append("(");
						for (int i = 0; i < values.length; i++) {
							conditions.append("'" + values[i] + "',");
						}
						conditions.deleteCharAt(conditions.lastIndexOf(","));
						conditions.append(")");
					} else {
						conditions.append("'").append(values[0]).append("'");
					}
				}
			}
		}
	}

	public void visitOperator(String operator) {
		currentOperator = operator;
	}

	public String getConditions() {
		return conditions.toString();
	}
}
