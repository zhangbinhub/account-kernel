package OLink.bpm.core.expimp.imp.util;

import org.apache.commons.beanutils.DynaBean;

public class DynaBeanPackage {
	private String displayName;

	private String displayValue;

	private DynaBean dynaBean;

	public DynaBeanPackage(String displayName, String displayValue,
			DynaBean dynaBean) {
		setDisplayName(displayName);
		setDisplayValue(displayValue);
		setDynaBean(dynaBean);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public DynaBean getDynaBean() {
		return dynaBean;
	}

	public void setDynaBean(DynaBean dynaBean) {
		this.dynaBean = dynaBean;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}
}
