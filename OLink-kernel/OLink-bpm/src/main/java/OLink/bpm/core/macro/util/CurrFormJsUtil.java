package OLink.bpm.core.macro.util;

import OLink.bpm.core.dynaform.form.ejb.Form;


public class CurrFormJsUtil {
	private Form _currForm;

	public CurrFormJsUtil(Form currForm) {
		_currForm = currForm;
	}
	
	public Object getCurrForm() {
		return _currForm;
	}

	public static void main(String[] args) {
	}
}
