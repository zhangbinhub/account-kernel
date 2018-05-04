package pers.acp.tools.file.word;

import java.util.HashMap;
import java.util.Map;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;

public enum WordType implements IEnumValue {

    WORD_TYPE_DOC(".doc", 0),

    WORD_TYPE_DOCX(".docx", 1);

    private String name;

    private Integer value;

    private static Map<Integer, WordType> map;

    static {
        map = new HashMap<>();
        for (WordType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    WordType(String name, Integer value) {
        this.name = name.toLowerCase();
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

    public static WordType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(WordType.class, value);
    }

}
