/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Rhino JavaScript Debugger code, released
 * November 21, 2000.
 *
 * The Initial Developer of the Original Code is
 * SeeBeyond Corporation.
 * Portions created by the Initial Developer are Copyright (C) 2000
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Igor Bukanov
 *   Matt Gould
 *   Cameron McCormack
 *   Christopher Oliver
 *   Hannes Wallnoefer
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU General Public License Version 2 or later (the "GPL"), in which
 * case the provisions of the GPL are applicable instead of those above. If
 * you wish to allow use of your version of this file only under the terms of
 * the GPL and not to allow others to use your version of this file under the
 * MPL, indicate your decision by deleting the provisions above and replacing
 * them with the notice and other provisions required by the GPL. If you do
 * not delete the provisions above, a recipient may use your version of this
 * file under either the MPL or the GPL.
 *
 * ***** END LICENSE BLOCK ***** */
package OLink.bpm.core.macro.runner;

import javax.servlet.http.HttpServletRequest;

/**
 * GUI for the Rhino debugger.
 */
public class DebugGui {

	/**
	 * Serializable magic number.
	 */
	private static final long serialVersionUID = -8217029773456711622L;

	/**
	 * The debugger.
	 */
	Dim dim;

	JavaScriptDebuger debuger;

	private static DebugGui getCurrentDebugGui(HttpServletRequest request)
			throws Exception {
		String sessionid = request.getSession().getId();

		if (JavaScriptFactory.isDebug(sessionid)) {

			JavaScriptDebuger d = JavaScriptDebuger
					.getDebugInstance(sessionid);

			if (d != null) {
				return new DebugGui(d, "JavaScript Debuger");
			}
		}

		// request.getSession().getSessionContext().
		return null;

	}

	public static DebugFrameInfo setDebugModule(boolean debug,
			HttpServletRequest request) throws Exception {
		String sessionid = request.getSession().getId();
		if (debug) {
			JavaScriptFactory.set_debug(sessionid, debug);
		} else {
			JavaScriptFactory.set_debug(sessionid, debug);
//			DebugGui gui = getCurrentDebugGui(request);
			
			JavaScriptDebuger d = JavaScriptDebuger
			.getDebugInstance(sessionid);
			
			if (d != null) {
			
				DebugGui gui = new DebugGui(d, "JavaScript Debuger");
			
//				synchronized (gui.dim.eventThreadMonitor) {
					gui.debuger.clearDebugInfo();

//				}

				String cmd = "Go";

				return actionPerformed(cmd, gui);

			}

		}
		return new DebugFrameInfo("", "", -1);
	}

	public static boolean isDebugModule(HttpServletRequest request) {
		String sessionid = request.getSession().getId();
		return JavaScriptFactory.isDebug(sessionid);
	}

	public static String evalExpr(String expr, HttpServletRequest request) {
		try {
			DebugGui gui = getCurrentDebugGui(request);
			return gui.dim.eval(expr);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static DebugFrameInfo actionPerformed(String cmd,
			HttpServletRequest request) throws Exception {
		DebugGui gui = getCurrentDebugGui(request);

		if (gui != null) {
			gui.debuger.clearDebugInfo();
			return actionPerformed(cmd, gui);
		}

		return new DebugFrameInfo("", "", -1);

	}

	private static DebugFrameInfo actionPerformed(String cmd, DebugGui gui) {
		gui.performed(cmd);

		synchronized (gui.dim.eventThreadMonitor) {
			do {
				try {
					gui.dim.eventThreadMonitor.wait(500);

					synchronized (gui.dim.monitor) {
						gui.dim.monitor.wait(500);
					}
				} catch (InterruptedException exc) {
					return new DebugFrameInfo("", "", -1);
				}
			} while (gui.debuger.getDebugInfo() == null
					&& !gui.debuger.isFinnished());

			if (gui.debuger.getDebugInfo() != null) {
				return gui.debuger.getDebugInfo();
			} else if (gui.dim.currentContextData() != null) {
				return new DebugFrameInfo(gui.dim.currentContextData()
						.getFrame(0));
			}

			return new DebugFrameInfo("", "", -1);
		}
	}

	public static DebugFrameInfo viewDebugFrameInfo(String cmd,
			HttpServletRequest request) throws Exception {
		DebugGui gui = getCurrentDebugGui(request);

		if (gui != null && gui.dim != null
				&& gui.dim.currentContextData() != null) {
			return new DebugFrameInfo(gui.dim.currentContextData().getFrame(0));
		} else {
			return new DebugFrameInfo("", "", -1);
		}

	}

	/**
	 * Creates a new SwingGui.
	 */
	public DebugGui(JavaScriptDebuger debuger, String title) {
		this.dim = debuger.getEngine();
		this.debuger = debuger;
	}

	private void exit() {
		dim.setReturnValue(Dim.EXIT);
	}

	// ActionListener
	public void performed(String cmd) {
		int returnValue = -1;
		if (cmd.equals("Step Over")) {
			returnValue = Dim.STEP_OVER;
		} else if (cmd.equals("Step Into")) {
			returnValue = Dim.STEP_INTO;
		} else if (cmd.equals("Step Out")) {
			returnValue = Dim.STEP_OUT;
		} else if (cmd.equals("Go")) {
			returnValue = Dim.GO;
		} else if (cmd.equals("Break")) {
			dim.setBreak();
		} else if (cmd.equals("Exit")) {
			exit();
		}

		if (returnValue != -1) {
			dim.setReturnValue(returnValue);
		}
	}

}
