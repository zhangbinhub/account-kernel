package OLink.bpm.base.dao;


/**
 * The DAO Factory interface.
 */
public interface IDAOFactory {
	/**
	 * Get the Dao
	 * @param className The value object class name.
	 * @return The relate dao object.
	 */
	IDesignTimeDAO<?> getDAO(String className);
}
