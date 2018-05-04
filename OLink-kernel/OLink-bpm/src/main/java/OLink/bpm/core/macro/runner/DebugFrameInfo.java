package OLink.bpm.core.macro.runner;

public class DebugFrameInfo {

	public DebugFrameInfo(Dim.StackFrame frame) {
		source = frame.sourceInfo().source();
		url = frame.getUrl();
		lineNumber = frame.getLineNumber();
	}

	public DebugFrameInfo(String url, String source, int lineNumber) {
		this.source = source;
		this.url = url;
		this.lineNumber = lineNumber;
	}

	/**
	 * The script.
	 */
	private String source;

	/**
	 * The URL of the script.
	 */
	private String url;

	/**
	 * Current line number.
	 */
	private int lineNumber;

	public String source() {
		return this.source;
	}

	public String url() {
		return this.url;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @hibernate.property column="lineNumber"
	 */
	public int getLineNumber() {
		if (url != null && url.equals("baselib")) {
			return lineNumber;
		} else {
			return lineNumber;
		}

	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

}
