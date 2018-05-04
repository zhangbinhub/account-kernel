package pers.acp.tools.file.excel.common;

import java.util.HashMap;
import java.util.Map;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;

public enum ExcelType implements IEnumValue {

    EXCEL_TYPE_XLS(".xls", 0),

    EXCEL_TYPE_XLSX(".xlsx", 1);

    private String name;

    private Integer value;

    private static Map<Integer, ExcelType> map;

    static {
        map = new HashMap<>();
        for (ExcelType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    ExcelType(String name, Integer value) {
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

    public static ExcelType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(ExcelType.class, value);
    }

}
