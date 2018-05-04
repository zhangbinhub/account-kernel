package pers.acp.tools.file.pdf;

import java.util.HashMap;
import java.util.Map;

import pers.acp.tools.exceptions.EnumValueUndefinedException;
import pers.acp.tools.interfaces.IEnumValue;
import com.itextpdf.text.pdf.PdfWriter;

public enum PermissionType implements IEnumValue {

    ALLOW_ASSEMBLY("ALLOW_ASSEMBLY", PdfWriter.ALLOW_ASSEMBLY),

    ALLOW_COPY("ALLOW_COPY", PdfWriter.ALLOW_COPY),

    ALLOW_DEGRADED_PRINTING("ALLOW_DEGRADED_PRINTING", PdfWriter.ALLOW_DEGRADED_PRINTING),

    ALLOW_FILL_IN("ALLOW_FILL_IN", PdfWriter.ALLOW_FILL_IN),

    ALLOW_MODIFY_ANNOTATIONS("ALLOW_MODIFY_ANNOTATIONS", PdfWriter.ALLOW_MODIFY_ANNOTATIONS),

    ALLOW_MODIFY_CONTENTS("ALLOW_MODIFY_CONTENTS", PdfWriter.ALLOW_MODIFY_CONTENTS),

    ALLOW_PRINTING("ALLOW_PRINTING", PdfWriter.ALLOW_PRINTING),

    ALLOW_SCREENREADERS("ALLOW_SCREENREADERS", PdfWriter.ALLOW_SCREENREADERS);

    private String name;

    private Integer value;

    private static Map<Integer, PermissionType> map;

    static {
        map = new HashMap<>();
        for (PermissionType type : values()) {
            map.put(type.getValue(), type);
        }
    }

    PermissionType(String name, Integer value) {
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

    public static PermissionType getEnum(Integer value) throws EnumValueUndefinedException {
        if (map.containsKey(value)) {
            return map.get(value);
        }
        throw new EnumValueUndefinedException(PermissionType.class, value);
    }
}
