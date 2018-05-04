package OLink.bpm.core.formula;

public class MenubarType {
	public static final int MENUBAR_TYPE_RELATION = 0;

	public static final int MENUBAR_TYPE_OPERATOR = 1;

	public static final int MENUBAR_TYPE_COMPARE = 2;

	public static final int MENUBAR_TYPE_FIELDNAME = 3;

	public static final int MENUBAR_TYPE_EXPRESS = 4;

	public static final int MENUBAR_TYPE_NOTSYMBOL = 255;
	
	private static final String[] RELATION_LIST = { "AND", "OR" };

	private static final String[] OPERATOR_LIST = { "+", "-", "*", "/" };

	private static final String[] COMPARE_LIST = { "LIKE", ">", ">=", "<",
			"<=", "=", "IN", "NOT IN" };

	public static final int[] TYPES = { 0, 1, 2, 3, 4 };

	public static final String[] NAMES = { "Relation", "Operator", "Compare",
			"FieldName", "Express" };

	public static String getName(int type) {
		if (type == 0) {
			return null;
		}

		for (int i = 0; i < TYPES.length; i++)
			if (type == (TYPES[i]))
				return NAMES[i];
		return "";
	}

	public static int getType(String name) {
		for (int i = 0; i < RELATION_LIST.length; i++) {
			if (name.trim().equalsIgnoreCase(RELATION_LIST[i])) {
				return MENUBAR_TYPE_RELATION;
			}
		}

		for (int j = 0; j < OPERATOR_LIST.length; j++) {
			if (name.trim().equalsIgnoreCase(OPERATOR_LIST[j])) {
				return MENUBAR_TYPE_OPERATOR;
			}
		}

		for (int n = 0; n < COMPARE_LIST.length; n++) {
			if (name.trim().equalsIgnoreCase(COMPARE_LIST[n])) {
				return MENUBAR_TYPE_COMPARE;
			}
		}

		return Integer.MAX_VALUE;
	}
}
