package OLink.bpm.core.sysconfig;

public class RootElementNotFoundException extends Exception {
	
	private static final long serialVersionUID = 4827556485417420056L;
	
	public RootElementNotFoundException() {
		super("can not found the root element 'sysConfig'");
	}
}
