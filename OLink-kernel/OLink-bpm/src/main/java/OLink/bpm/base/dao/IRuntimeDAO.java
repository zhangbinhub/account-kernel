package OLink.bpm.base.dao;


public interface IRuntimeDAO extends IBaseDAO {
	void create(ValueObject vo) throws Exception;

	void remove(String pk) throws Exception;

	void update(ValueObject vo) throws Exception;

	ValueObject find(String id) throws Exception;
}
