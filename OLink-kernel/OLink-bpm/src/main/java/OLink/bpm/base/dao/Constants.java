package OLink.bpm.base.dao;

/**
 * The constant value in DAO layer
 */
public class Constants {
	/**
	 * The integer value of ORM_HIBERNATE
	 */
	public static final int ORM_HIBERNATE = 1;

	/**
	 * The integer value of ORM_HIBERNATE
	 */
	public static final int ORM_DEFAULT = ORM_HIBERNATE;

	/**
	 * Get the OR tools description.
	 * 
	 * @param type
	 *            The OR tools.
	 * @return The OR tools description.
	 */
	public static String ormType2Str(int type) {
		switch (type) {
		case ORM_HIBERNATE:
			return "Hibernate";
		default:
			return "";
		}
	}

}
