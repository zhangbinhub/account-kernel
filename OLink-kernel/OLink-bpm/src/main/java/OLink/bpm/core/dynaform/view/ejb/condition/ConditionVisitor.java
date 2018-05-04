package OLink.bpm.core.dynaform.view.ejb.condition;

public interface ConditionVisitor {
	void visitColumnType(String columnType);

	void visitColumn(String column);

	void visitDateValue(String value, String datePattern);

	void visitStringValue(String value);

	void visitNumberValue(String value);

	void visitOperator(String operator);

	String getConditions();
}
