package OLink.bpm.core.workflow.utility;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.TestCase;

public class NameListTest extends TestCase {

	protected void setUp() throws Exception {

		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParse() throws Exception {
		String namelist = "{A001|a;B001|b;}";
		NameList nameList = NameList.parser(namelist);
		Collection<Object> colls = nameList.getData();
		for (Iterator<Object> iter = colls.iterator(); iter.hasNext();) {
			// iter.next() instanceof NameNode;
		}
	}
}
