package OLink.bpm.core.multilanguage.ejb;

public class LanguageType {

	public static final int LANGUAGE_TYPE_ENGLISH = 1;

	public static final int LANGUAGE_TYPE_SIMPLE_CHINESE = 2;

	public static final int LANGUAGE_TYPE_TRADITION_CHINESE = 3;

	//public static final int LANGUAGE_TYPE_RUSSIAN = 4;

	public static final int[] TYPES = { 1, 2, 3};

	public static final String[] NAMES = { "English", "简体中文",
		"繁體中文"};

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
		if (name == null)
			return 0;

		for (int i = 0; i < NAMES.length; i++)
			if (name.equals(NAMES[i]))
				return TYPES[i];
		return 0;
	}
}
