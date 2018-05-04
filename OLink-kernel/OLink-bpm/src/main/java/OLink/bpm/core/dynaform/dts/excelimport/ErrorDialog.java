package OLink.bpm.core.dynaform.dts.excelimport;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.WindowEvent;

/**
 * <p>
 * Title: excelimport
 * </p>
 * <p>
 * Description: This Applet Is A Work Flow Defing Tooles.We Can use it to defing
 * Work Flow And Stored As XML Format.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Cyberway
 * </p>
 * 
 * @author James Zhou
 * @version 1.0
 */

public class ErrorDialog extends Dialog {

	private static final long serialVersionUID = 6041494598652883639L;
	Panel panel1 = new Panel();
	BorderLayout borderLayout1 = new BorderLayout();

	public ErrorDialog(Frame frame, String title, boolean modal) {
		super(frame, title, modal);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
			add(panel1);
			pack();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public ErrorDialog(Frame frame) {
		this(frame, "", false);
	}

	public ErrorDialog(Frame frame, boolean modal) {
		this(frame, "", modal);
	}

	public ErrorDialog(Frame frame, String title) {
		this(frame, title, false);
	}

	private void jbInit() throws Exception {
		panel1.setLayout(borderLayout1);
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			cancel();
		}
		super.processWindowEvent(e);
	}

	void cancel() {
		dispose();
	}
}
