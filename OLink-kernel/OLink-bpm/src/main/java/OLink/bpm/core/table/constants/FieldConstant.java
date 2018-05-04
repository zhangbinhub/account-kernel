package OLink.bpm.core.table.constants;

import java.sql.Types;

import OLink.bpm.core.dynaform.document.ejb.Item;

public class FieldConstant {
	private static int[] typeCodes;

	private static String[] typeNames;

	static {
		typeCodes = new int[6];
		typeCodes[0] = Types.NULL;
		typeCodes[1] = Types.VARCHAR;
		typeCodes[2] = Types.NUMERIC;
		typeCodes[3] = Types.TIMESTAMP;
		typeCodes[4] = Types.CLOB;
		typeCodes[5] = Types.BLOB;

		typeNames = new String[6];
		typeNames[0] = null;
		typeNames[1] = Item.VALUE_TYPE_VARCHAR;
		typeNames[2] = Item.VALUE_TYPE_NUMBER;
		typeNames[3] = Item.VALUE_TYPE_DATE; // 日期类型默认用TimeStamp
		typeNames[4] = Item.VALUE_TYPE_TEXT;
		typeNames[5] = Item.VALUE_TYPE_BLOB;
	}

	public static int getTypeCode(String typeName) {
		int typeCode = Types.NULL;

		for (int i = 1; i < typeNames.length; i++) {
			if (typeNames[i].equals(typeName)) {
				typeCode = typeCodes[i];
			}
		}

		return typeCode;
	}

	public static String getTypeName(int typeCode) {
		String typeName = null;

		for (int i = 0; i < typeCodes.length; i++) {
			if (typeCodes[i] == typeCode) {
				typeName = typeNames[i];
			}
		}

		return typeName;
	}
}
