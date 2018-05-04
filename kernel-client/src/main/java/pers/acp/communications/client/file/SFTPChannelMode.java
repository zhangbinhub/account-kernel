package pers.acp.communications.client.file;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;
import com.jcraft.jsch.ChannelSftp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangbin on 2016/12/20.
 * sftp 传输模式
 */
public enum SFTPChannelMode implements IEnumValue {

    OVERWRITE("OVERWRITE", ChannelSftp.OVERWRITE),

    RESUME("RESUME", ChannelSftp.RESUME),

    APPEND("APPEND", ChannelSftp.APPEND);

    private String name;

    private Integer value;

    private static Map<Integer, SFTPChannelMode> map;

    static {
        map = new HashMap<>();
        for (SFTPChannelMode type : values()) {
            map.put(type.getValue(), type);
        }
    }

    SFTPChannelMode(String name, Integer value) {
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

    public static SFTPChannelMode getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(SFTPChannelMode.class, value);
    }

}
