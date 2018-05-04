package OLink.bpm.util;

/**
 * @author Happy
 *
 */
public class WarpException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6458948973965652768L;

	public WarpException() {
	}

	public WarpException(String message) {
		super(message);
	}

	public WarpException(Throwable cause) {
		super(cause);
	}

	public WarpException(String message, Throwable cause) {
		super(message, cause);
	}

}
