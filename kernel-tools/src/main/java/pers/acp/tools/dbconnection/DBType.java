package pers.acp.tools.dbconnection;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/7/21.
 * 数据库类型
 */
public enum DBType implements IEnumValue {

    MySQL("mysql", 1, "MySQLFactory"),

    Oracle("oracle", 2, "OracleFactory"),

    MsSQL("mssql", 3, "MsSQLFactory");

    private String name;

    private Integer value;

    private String factoryName;

    private static Map<String, DBType> map;

    static {
        map = new HashMap<>();
        for (DBType type : values()) {
            map.put(type.getName().toLowerCase(), type);
        }
    }

    DBType(String name, Integer value, String factoryName) {
        this.name = name.toLowerCase();
        this.value = value;
        this.factoryName = factoryName;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getFactoryName() {
        return this.factoryName;
    }

    @Override
    public Boolean equals(Integer value) {
        return this.value.equals(value);
    }

    public static DBType getEnum(String name) throws EnumValueUndefinedException {
        if (map.containsKey(name.toLowerCase())) {
            return map.get(name.toLowerCase());
        }
        throw new EnumValueUndefinedException(DBType.class, name);
    }

}
