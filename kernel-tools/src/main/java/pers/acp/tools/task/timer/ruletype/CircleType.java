package pers.acp.tools.task.timer.ruletype;

import java.util.HashMap;
import java.util.Map;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;

/**
 * 循环周期
 *
 * @author zhangbin
 */
public enum CircleType implements IEnumValue {

    Time("Time", 0),

    Day("Day", 1),

    Week("Week", 2),

    Month("Month", 3),

    Quarter("Quarter", 4),

    Year("Year", 5);

    private String name;

    private Integer value;

    private static Map<Integer, CircleType> map;

    private static Map<String, CircleType> nameMap;

    static {
        map = new HashMap<>();
        nameMap = new HashMap<>();
        for (CircleType type : values()) {
            map.put(type.getValue(), type);
            nameMap.put(type.getName(), type);
        }
    }

    CircleType(String name, Integer value) {
        this.name = name.toLowerCase();
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static CircleType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(CircleType.class, value);
    }

    public static CircleType getEnum(String name) throws EnumValueUndefinedException {
        if (nameMap.containsKey(name.toLowerCase())) {
            return nameMap.get(name.toLowerCase());
        }
        throw new EnumValueUndefinedException(CircleType.class, name);
    }

}
