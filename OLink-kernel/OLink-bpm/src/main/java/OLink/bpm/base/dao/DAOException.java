package OLink.bpm.base.dao;

/**
 * The data access object exception.
 */
public class DAOException extends Exception {

	/**
	 * The serial version uuid.
	 */
	private static final long serialVersionUID = 6995270749893190281L;

	/**
	 * The constructor with erro rmessage.
	 * 
	 * @param s
	 */
	public DAOException(String s) {
		super(s);
	}
}
