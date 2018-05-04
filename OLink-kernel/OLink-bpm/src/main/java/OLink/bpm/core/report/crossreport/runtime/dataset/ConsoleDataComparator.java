package OLink.bpm.core.report.crossreport.runtime.dataset;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The comparator for the console data.
 * 
 */
public class ConsoleDataComparator implements Comparator<ConsoleData>,Serializable {

	private static final long serialVersionUID = 4703390309118430766L;

	public int compare(ConsoleData o1, ConsoleData o2) {
		if(o1 != null && o2 !=null){
			return o1.getMetaData().getOrder() - o1.getMetaData().getOrder();
		}
		return 0;
	}
	
}
