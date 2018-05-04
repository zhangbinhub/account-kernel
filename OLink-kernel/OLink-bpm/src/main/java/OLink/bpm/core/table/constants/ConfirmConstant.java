package OLink.bpm.core.table.constants;

import java.util.HashMap;
import java.util.Map;

public class ConfirmConstant {
	/**
	 * @uml.property name="msgCodeNames"
	 * @uml.associationEnd qualifier="key:java.lang.Object java.lang.String"
	 */
	public static Map<Integer, String> msgCodeNames;

	public static final int FORM_EXIST = 0;

	public static final int FORM_DATA_EXIST = 1;

	public static final int FIELD_EXIST = 2;

	public static final int FIELD_DATA_EXIST = 3;

	public static final int FIELD_TYPE_INCOMPATIBLE = 4;

	public static final int FIELD_DUPLICATE = 5;

	static {
		msgCodeNames = new HashMap<Integer, String>();
		msgCodeNames.put(Integer.valueOf(FORM_EXIST), "table.exist");
		msgCodeNames.put(Integer.valueOf(FORM_DATA_EXIST), "table.data.exist");
		msgCodeNames.put(Integer.valueOf(FIELD_EXIST), "field.exist");
		msgCodeNames.put(Integer.valueOf(FIELD_DATA_EXIST), "field.data.exist");
		msgCodeNames.put(Integer.valueOf(FIELD_TYPE_INCOMPATIBLE), "field.type.incompatible");
		msgCodeNames.put(Integer.valueOf(FIELD_DUPLICATE), "field.duplicate");
	}

	public static String getMsgKeyName(int keyCode) {
		return msgCodeNames.get(Integer.valueOf(keyCode));
	}
}
