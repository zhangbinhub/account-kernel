package OLink.bpm.core.table.model;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.dynaform.form.ejb.Confirm;

public class NeedConfirmException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Collection<Confirm> confirms;

	public NeedConfirmException(Collection<Confirm> confirms) {
		this.confirms = confirms;
	}

	public Collection<Confirm> getConfirms() {
		return confirms;
	}

	public void setConfirms(Collection<Confirm> confirms) {
		this.confirms = confirms;
	}

	public String getMessage() {
		StringBuffer strbuf = new StringBuffer();

		for (Iterator<Confirm> iter = confirms.iterator(); iter.hasNext();) {
			Confirm confirm = iter.next();
			strbuf.append(confirm.getMsgKeyName() + "\n");
		}
		return strbuf.toString();

	}
}
