//Source file: D:\\BILLFLOW\\src\\billflow\\BFApplet.java

//Source file: E:\\billflow\\src\\billflow\\BFApplet.java

package OLink.bpm.core.macro.editor;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;

import OLink.bpm.core.macro.editor.text.JSideTextPane;

import netscape.javascript.JSObject;

public class JavaScriptEditorApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5796831404435415355L;

	boolean isStandalone = false;

	Panel bppanel = new Panel();
	
	JSideTextPane fd;
	

	BorderLayout borderLayout1 = new BorderLayout();
	

	JSObject win = null;

	private MediaTracker tracker;

	/**
	 * Construct the applet
	 * 
	 * @roseuid 3E0A6E1602A2
	 */
	public JavaScriptEditorApplet() {

	}

	/**
	 * Get a parameter value
	 * 
	 * @param key
	 * @param def
	 * @return java.lang.String
	 * @roseuid 3E0A6E16028E
	 */
	public String getParameter(String key, String def) {
		return isStandalone ? System.getProperty(key, def)
				: (getParameter(key) != null ? getParameter(key) : def);
	}

	/**
	 * Initialize the applet
	 * 
	 * @roseuid 3E0A6E1602AC
	 */
	public void init() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----------------------------------
	protected void loading(Image image1) {
		// wait for the image to load completely
		synchronized (tracker) {
			tracker.addImage(image1, 0);
			try {
				tracker.waitForID(0);
			} catch (InterruptedException interruptedexception) {
				interruptedexception.printStackTrace();
			}
			tracker.removeImage(image1, 0);
		}
	}
	/**
	 * Component initialization
	 * 
	 * @throws Exception
	 * @roseuid 3E0A6E1602AD
	 */
	private void jbInit() throws Exception {
		tracker = new MediaTracker(this);

		String JavaScriptStr = "";

		if (getParameter("JavaScriptStr") != null
				&& !getParameter("JavaScriptStr").toLowerCase().equals("null")) {
			JavaScriptStr = getParameter("JavaScriptStr");
		}

		this.setLayout(borderLayout1);

		fd = new JSideTextPane();
		
		fd.setBackground(Color.white);
		try {
			win = JSObject.getWindow(this);
		}
		catch(Exception e) {
			
		}

		this.add(bppanel, BorderLayout.CENTER);

		bppanel.setLayout(new BorderLayout());
		bppanel.add(fd,BorderLayout.CENTER);
		fd.setText(JavaScriptStr);
	}

	/**
	 * Get Applet information
	 * 
	 * @return java.lang.String
	 * @roseuid 3E0A6E1602C0
	 */
	public String getAppletInfo() {
		return "Applet Information";
	}

	/**
	 * Get parameter info
	 * 
	 * @return String[][]
	 * @roseuid 3E0A6E1602CA
	 */
	public String[][] getParameterInfo() {
		return null;
	}
	
	public void loadScriptText(String text) {
		if (text!=null) {
			this.fd.setText(text);			
		}
		else {
			this.fd.setText("");
		}
	}
	
	public String getScriptText() {
		String text = fd.getText();
		if (text!=null) {
			return text;
		}
		else {
			return "";			
		}
	}

}
