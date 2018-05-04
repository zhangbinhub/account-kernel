package OLink.bpm.core.dynaform.view.ejb.condition;

public class Condition {
	private String name;
	private String val;
	private String operator;

	public Condition(String name, String val, String operator) {
		this.name = name;
		this.val = val;
		this.operator = operator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

}
