package OLink.bpm.core.dynaform.document;

import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.document.ejb.Document;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author nicholas
 */
public class DocumentException extends Exception {

	private static final long serialVersionUID = -78321644482335353L;

	/**
	 * @uml.property name="validateErrors"
	 */
	private Collection<ValidateMessage> validateErrors;

	private Document document;

	public DocumentException(Collection<ValidateMessage> validateErrors,
			Document doc) {
		this.validateErrors = validateErrors;
		this.document = doc;
	}

	public DocumentException(String errMsg, Document doc) {
		errMsg = errMsg == null ? "" : errMsg;
		ValidateMessage validateMessage = new ValidateMessage("", errMsg);
		this.validateErrors = new ArrayList<ValidateMessage>();
		validateErrors.add(validateMessage);
		this.document = doc;
	}

	/**
	 * @hibernate.property column="validateErrors"
	 * @uml.property name="validateErrors"
	 */
	public Collection<ValidateMessage> getValidateErrors() {
		return validateErrors;
	}

	/**
	 * @param validateErrors
	 *            the validateErrors to set
	 * @uml.property name="validateErrors"
	 */
	public void setValidateErrors(Collection<ValidateMessage> validateErrors) {
		this.validateErrors = validateErrors;
	}

	/**
	 * @hibernate.property column="document"
	 * @uml.property name="document"
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @param document
	 *            the document to set
	 * @uml.property name="document"
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

}
