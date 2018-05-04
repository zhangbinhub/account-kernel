package pers.acp.tools.exceptions.base;

public abstract class BaseException extends Exception {

	private static final long serialVersionUID = -7545052394584258864L;

	private Integer code = 1;
	private String message;

	public BaseException(String message) {
		super(message);
		this.message = message;
	}

	public BaseException(Exception e) {
		this(e.getMessage());
		this.message = e.getMessage();
	}

	public BaseException(Integer code, String message) {
		this(message);
		this.code = code;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

}
