package OLink.bpm.core.macro.repository.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import junit.framework.TestCase;
public class RepositoryActionHelperTest extends TestCase {

	RepositoryActionHelper actionHelper=null;
	
	protected void setUp() throws Exception {
		super.setUp();
		actionHelper=new RepositoryActionHelper();
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	//Set a collection   RepositoryActionHelper SetCollectionRepository().
	public void testSetCollectionRepository()throws Exception
	{
		Collection<String> Repotypes = new ArrayList<String>();
		  
		Repotypes.add("hello world");
		Repotypes.add("welcome");
		Repotypes.add("Thank you");
		List<String> repotype=(List<String>)Repotypes;
		actionHelper.setRepotype(repotype);
		Collection<String> col=actionHelper.getRepotype();
		List<String> list=(List<String>)col;
		for(int i=0;i<list.size();i++)
		{
			Object object= list.get(i);
			object.toString();
		}
		
		
	}

}
