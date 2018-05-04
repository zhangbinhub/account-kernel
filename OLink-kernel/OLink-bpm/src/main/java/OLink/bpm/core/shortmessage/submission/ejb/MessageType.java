package OLink.bpm.core.shortmessage.submission.ejb;

public class MessageType {

	public static final int[] TYPES={0,1,2};
	
	public static final String[] NAMES = {"[通知]","[提醒]","[催办]"};
	
	public static int getType(String name){
		if (name == null)
			return 0;

		for (int i = 0; i < NAMES.length; i++)
			if (name.equals(NAMES[i]))
				return TYPES[i];
		return 0;
	}
	
	public static String getName(int type){
		if (type == 0) {
			return NAMES[0];
		}

		for (int i = 0; i < TYPES.length; i++)
			if (type == (TYPES[i]))
				return NAMES[i];
		return "";
	}
}
