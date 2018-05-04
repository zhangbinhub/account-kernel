package OLink.bpm.core.dynaform.form.ejb;

import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * 空组件
 * 
 * 
 */
public class NullField extends FormField {
	
	private static final long serialVersionUID = -8808850990782938908L;

	public String toMbXMLText(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	public String toTemplate() {
		return null;
	}

	public String toHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

	public String toPrintHtmlTxt(Document doc, IRunner runner, WebUser webUser) throws Exception {
		return null;
	}

}
