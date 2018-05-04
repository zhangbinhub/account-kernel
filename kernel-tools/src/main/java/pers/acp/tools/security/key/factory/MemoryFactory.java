package pers.acp.tools.security.key.factory;

import pers.acp.tools.security.key.KeyEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhang on 2016/7/19.
 * 内存存储
 */
public class MemoryFactory implements IStorageFactory {

    private static final Map<String, KeyEntity> temporaryKey = new ConcurrentHashMap<>();

    @Override
    public KeyEntity readEntity(String traitid) throws Exception {
        KeyEntity entity;
        synchronized (temporaryKey) {
            if (temporaryKey.containsKey(traitid)) {
                entity = temporaryKey.get(traitid);
            } else {
                entity = null;
            }
        }
        return entity;
    }

    @Override
    public void savaEntity(KeyEntity entity) throws Exception {
        synchronized (temporaryKey) {
            temporaryKey.put(entity.getTraitId(), entity);
        }
    }

    @Override
    public void deleteEntity(String traitid) throws Exception {
        synchronized (temporaryKey) {
            if (temporaryKey.containsKey(traitid)) {
                temporaryKey.remove(traitid);
            }
        }
    }

    @Override
    public void clear() throws Exception {
        synchronized (temporaryKey) {
            temporaryKey.clear();
        }
    }

}
