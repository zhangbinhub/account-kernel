package pers.acp.tools.task.timer.ruletype;

import java.util.HashMap;
import java.util.Map;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;

/**
 * 执行类型
 *
 * @author zhangbin
 */
public enum ExcuteType implements IEnumValue {

    WeekDay("WeekDay", 0),

    Weekend("Weekend", 1),

    All("All", 2);

    private String name;

    private Integer value;

    private static Map<Integer, ExcuteType> map;

    private static Map<String, ExcuteType> nameMap;

    static {
        map = new HashMap<>();
        nameMap = new HashMap<>();
        for (ExcuteType type : values()) {
            map.put(type.getValue(), type);
            nameMap.put(type.getName(), type);
        }
    }

    ExcuteType(String name, Integer value) {
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

    public static ExcuteType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(ExcuteType.class, value);
    }

    public static ExcuteType getEnum(String name) throws EnumValueUndefinedException {
        if (nameMap.containsKey(name.toLowerCase())) {
            return nameMap.get(name.toLowerCase());
        }
        throw new EnumValueUndefinedException(ExcuteType.class, name);
    }

}
