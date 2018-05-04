package OLink.bpm.core.report.crossreport.runtime.dataset;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The comparator for the console meta datas.
 * 
 */
public class ConsoleMetaDataComparator implements Comparator<ConsoleMetaData>, Serializable {

	private static final long serialVersionUID = -4796057864016208870L;

	public int compare(ConsoleMetaData o1, ConsoleMetaData o2) {
		if(o1 != null && o2 != null){
			return o1.getOrder() - o2.getOrder();
		}
		return 0;
	}

}
