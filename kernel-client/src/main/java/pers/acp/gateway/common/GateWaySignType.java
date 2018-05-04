package pers.acp.gateway.common;

import java.util.HashMap;
import java.util.Map;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;

public enum GateWaySignType implements IEnumValue {

    MD5("MD5", 1),

    SHA1("SHA1", 2);

    private String name;

    private Integer value;

    private static Map<Integer, GateWaySignType> map;

    static {
        map = new HashMap<>();
        for (GateWaySignType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    GateWaySignType(String name, Integer value) {
        this.name = name.toUpperCase();
        this.value = value;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public Boolean equals(String name) {
        return this.name.equals(name.toUpperCase());
    }

    public static GateWaySignType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(GateWaySignType.class, value);
    }

}
