package pers.acp.tools.dbconnection.entity;

import java.util.HashMap;
import java.util.Map;

import pers.acp.tools.interfaces.IEnumValue;

public enum DBTableFieldType implements IEnumValue {

    /**
     * 字符串
     */
    String("String", 1),

    /**
     * 整数
     */
    Integer("Integer", 2),

    /**
     * 小数
     */
    Decimal("Decimal", 3),

    /**
     * 金额
     */
    Money("Money", 4),

    /**
     * 日期
     */
    Date("Date", 5),

    /**
     * 二进制大字段
     */
    Blob("Blob", 6),

    /**
     * 长文本
     */
    Clob("Clob", 7);

    private String name;

    private Integer value;

    private static Map<Integer, DBTableFieldType> map;

    static {
        map = new HashMap<>();
        for (DBTableFieldType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    DBTableFieldType(String name, Integer value) {
        this.name = name;
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

}
