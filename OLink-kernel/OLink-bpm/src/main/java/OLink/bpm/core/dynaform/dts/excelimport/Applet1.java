package OLink.bpm.core.dynaform.dts.excelimport;

import java.applet.Applet;

public class Applet1 extends Applet {

	private static final long serialVersionUID = 8334617425090678247L;
	private boolean isStandalone = false;

	// Get a parameter value
	public String getParameter(String key, String def) {
		return isStandalone ? System.getProperty(key, def)
				: (getParameter(key) != null ? getParameter(key) : def);
	}

	// Construct the applet
	public Applet1() {
	}

	// Initialize the applet
	public void init() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Component initialization
	private void jbInit() throws Exception {
	}

	// Get Applet information
	public String getAppletInfo() {
		return "Applet Information";
	}

	// Get parameter info
	public String[][] getParameterInfo() {
		return null;
	}
}
