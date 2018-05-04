package OLink.bpm.core.report.tablecolumn.action;

import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.report.reportconfig.ejb.ReportConfig;
import OLink.bpm.core.report.reportconfig.ejb.ReportConfigProcess;
import OLink.bpm.core.report.tablecolumn.ejb.TableColumn;
import OLink.bpm.util.ProcessFactory;
import junit.framework.TestCase;

public class TableColumnActionTest extends TestCase {

	TableColumnAction action=null;
	//TableColumn vo=null;
	ReportConfig rc=null;
	String _reportConfigid=null;
	String _type=null;
	String _selects[]=null;
	
	private Collection<TableColumn> _fieldList;  //根据_reportConfigid,_type取得
	private String _isSort;
	private String calculateMode[];
    private String _description[];
    private String _width[];
	private String _orderNo[];
	private String _fontSize[];
	private String _backColor[];
	
	protected void setUp() throws Exception {
		super.setUp();
		action=new TableColumnAction();
		//vo=new TableColumn();
		rc=new ReportConfig();
		_type=ReportConfig.Column_Type_Detail;
		_selects=new String[]{"username","sex","age"};
		_isSort="username";
		calculateMode=new String[]{"","",""};
		_description=new String[]{"","",""};
		_width=new String[]{"","",""};
		_orderNo=new String[]{"3","2","1"};
		_fontSize=new String[]{"18","18","18"};
		_backColor=new String[]{"","",""};
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/*
	 * Test method for 'TableColumnAction.doNew()'
	 */
	public void doNew() throws Exception {
		action.doNew();
		_fieldList=action.get_fieldList();
		
		assertEquals(_fieldList.size(),3);
		boolean flag=true;
		
		for (Iterator<TableColumn> iter = _fieldList.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if(!em.getName().equals("username")&&!em.getName().equals("sex")
					&&!em.getName().equals("age"))
				flag=false;
		}
		
		assertTrue(flag);	}

	/*
	 * Test method for 'TableColumnAction.doEdit()'
	 */
	public void doEdit() throws Exception {
		action.doEdit();
		_fieldList=action.get_fieldList();
		
		assertEquals(_fieldList.size(),3);
		
		boolean flag=true;
		
		for (Iterator<TableColumn> iter = _fieldList.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if(!em.getName().equals("username")&&!em.getName().equals("sex")
					&&!em.getName().equals("age"))
				flag=false;
		}
		
		assertTrue(flag);
	}

	/*
	 * Test method for 'TableColumnAction.doSave()'
	 */
	public void testDoSave() throws Exception {
		ReportConfigProcess rp = (ReportConfigProcess) (ProcessFactory
				.createProcess(ReportConfigProcess.class));
		rp.doCreate(rc);
		_reportConfigid=rc.getId();
		action.set_type(_type);
		action.set_backColor(_backColor);
		action.set_description(_description);
		action.set_fontSize(_fontSize);
		action.set_orderNo(_orderNo);
		action.setCalculateMode(calculateMode);
		action.set_selects(_selects);
		action.set_isSort(_isSort);
		action.set_reportConfigid(_reportConfigid);
		action.set_width(_width);
		
		action.doSave();
		doNew();
		doEdit();
		doList();
		doDelete();
		
	}

	/*
	 * Test method for 'TableColumnAction.doList()'
	 */
	public void doList() throws Exception {
		action.doList();
		_fieldList=action.get_fieldList();
		
		assertEquals(_fieldList.size(),3);
		boolean flag=true;
		
		for (Iterator<TableColumn> iter = _fieldList.iterator(); iter.hasNext();) {
			TableColumn em = iter.next();
			if(!em.getName().equals("username")&&!em.getName().equals("sex")
					&&!em.getName().equals("age"))
				flag=false;
		}
		
		assertTrue(flag);	}
	
	public void doDelete() throws Exception {
		ReportConfigProcess rp = (ReportConfigProcess) (ProcessFactory
				.createProcess(ReportConfigProcess.class));
	  rp.doRemove(_reportConfigid);
	  action.doDelete();
		
	}

}
