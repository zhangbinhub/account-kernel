package OLink.bpm.core.report.wfdashboard.ejb;

import OLink.bpm.base.dao.ValueObject;

public class DashBoardVO extends ValueObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	
	Double value;

	public String getName() {
		return name; 
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getValue() {  
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
	
	

}
