package OLink.bpm.core.workflow;

public class FlowState {
	public static final int START = 0; // 0

	public static final int BIGIN = 0x00000010; // 16

	// (注:DRAFT一般为256,只在判断时使用268435456)
	public static final int RUNNING = 0x00000100; // 256

	public static final int SUSPEND = 0x00001000; // 4096

	public static final int ABORT = 0x00010000; // 65536

	public static final int DRAFT = 0x10000000; // 268435456

	public static final int TERMINAT = 0x01000000; // 16777216

	public static final int COMPLETE = 0x00100000; // 1048576

	public static final int AUTO = 0x00110000; // 1114112

	public static final int SUBFLOW = 0x00111000; // 1118208

	public static final int[] CODES = { BIGIN, RUNNING, SUSPEND, ABORT, DRAFT,
			TERMINAT, COMPLETE, AUTO, SUBFLOW };

	public static final String[] NAMES = { "Start", "Running", "Suspended",
			"Abort", "Terminate", "Completed", "Draft", "Auto", "SubFlow" };

	public static String getName(int code) {
		if (code == 0) {
			return NAMES[0];
		}

		for (int i = 0; i < CODES.length; i++) {
			if (code == CODES[i]) {
				return NAMES[i];
			}
		}
		return "";
	}

	public static int getCode(String name) {
		if (name == null || name.trim().length() <= 0) {
			return CODES[0];
		}

		for (int i = 0; i < NAMES.length; i++) {
			if (name.equals(NAMES[i])) {
				return CODES[i];
			}
		}

		return 0;
	}

}
