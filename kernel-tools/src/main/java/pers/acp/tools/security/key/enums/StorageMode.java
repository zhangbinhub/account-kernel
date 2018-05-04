package pers.acp.tools.security.key.enums;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/7/18.
 * 密钥存储类型
 */
public enum StorageMode implements IEnumValue {

    Memory("MemoryFactory", 1),

    File("FileFactory", 2),

    DataBase("DataBaseFactory", 3);

    private String name;

    private Integer value;

    private static Map<Integer, StorageMode> map;

    static {
        map = new HashMap<>();
        for (StorageMode type : values()) {
            map.put(type.getValue(), type);
        }
    }

    StorageMode(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static StorageMode getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(StorageMode.class, value);
    }
}
