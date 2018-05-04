package OLink.bpm.util.cache;

public interface IMyCache {
	IMyElement get(Object key);
	void put(IMyElement element);
	void put(Object key, Object value);
}
