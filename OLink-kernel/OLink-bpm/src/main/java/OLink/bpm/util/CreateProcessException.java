package OLink.bpm.util;

public class CreateProcessException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 595689181324578197L;

	public CreateProcessException() {
	}

	public CreateProcessException(String message) {
		super(message);
	}
	
	public CreateProcessException(Throwable cause) {
		super(cause);
	}
}
