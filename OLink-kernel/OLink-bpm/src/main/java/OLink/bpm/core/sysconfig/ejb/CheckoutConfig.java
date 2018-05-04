package OLink.bpm.core.sysconfig.ejb;

public class CheckoutConfig implements java.io.Serializable{

	private static final long serialVersionUID = -9153393795140815273L;
	/**
	 * 是否启用
	 */
	public static final String INVOCATION = "INVOCATION";
	
	private boolean invocation;

	public boolean isInvocation() {
		return invocation;
	}

	public void setInvocation(boolean invocation) {
		this.invocation = invocation;
	}
	
	public boolean getInvocation() {
		return invocation;
	}
	
	
}
