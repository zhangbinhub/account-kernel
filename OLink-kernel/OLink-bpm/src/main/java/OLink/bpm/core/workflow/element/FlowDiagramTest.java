package OLink.bpm.core.workflow.element;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.util.ProcessFactory;
import junit.framework.TestCase;
import OLink.bpm.core.workflow.storage.definition.ejb.BillDefiProcess;

public class FlowDiagramTest extends TestCase {
	BillDefiProcess bp;

	protected void setUp() throws Exception {
		bp = (BillDefiProcess) ProcessFactory
				.createProcess(BillDefiProcess.class);
		bp.doQuery(new ParamsTable());
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for
	 * 'FlowDiagram.toJpegImage(File)'
	 */
	public void testToJpegImageFile() throws Exception {
		// SessionSignal signal = PersistenceUtils.getSessionSignal();
		// signal.sessionSignal++;
		/*
		 * Just for test FileInputStream fis = new
		 * FileInputStream("C:\\WORKFLOW.TXT");
		 * 
		 * InputStreamReader isr = new InputStreamReader(fis);
		 * 
		 * BufferedReader br = new BufferedReader(isr);
		 * 
		 * StringBuffer sb = new StringBuffer();
		 * 
		 * while (true) { String s = br.readLine(); if (s == null) { break; }
		 * else { sb.append(s + "\n"); } }
		 * 
		 * // BillDefiVO flowVO = (BillDefiVO)bp.doView("1164598740139000");
		 * WFRunner wfr = new WFRunner(sb.toString(), "aannvv"); FlowDiagram fd
		 * = wfr.getFlowDiagram();
		 * fd.setFlowstatus(FlowType.FLOWSTATUS_OPEN_RUN_RUNNING);
		 * 
		 * 
		 * ImageUtil imageUtil = new ImageUtil(fd); imageUtil.toImage(new
		 * File("c://flowImage.jpg")); imageUtil.toMobileImage(new
		 * File("c://flowMobile.png"));
		 * 
		 * // PersistenceUtils.closeSession();
		 */
	}
}
