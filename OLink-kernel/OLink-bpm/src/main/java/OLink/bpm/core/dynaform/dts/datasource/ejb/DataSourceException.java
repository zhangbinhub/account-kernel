package OLink.bpm.core.dynaform.dts.datasource.ejb;

public class DataSourceException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5202252138719735536L;

	public DataSourceException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message. The cause
	 * is not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause}.
	 * 
	 * @param message
	 *            the detail message. The detail message is saved for later
	 *            retrieval by the {@link #getMessage()} method.
	 */
	public DataSourceException(String message) {
		super(message);
	}
}
