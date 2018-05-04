package OLink.bpm.core.report.crossreport.runtime.analyzer;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The comparator for the analyse data key.
 * 
 */
public class AnalyseDataKeyComparator implements Comparator<AnalyseDataKeyPair>, Serializable {

	private static final long serialVersionUID = 2073917551730063109L;

	public int compare(AnalyseDataKeyPair o1, AnalyseDataKeyPair o2) {
		if(o1 != null && o2 != null){
			return o1.compareTo(o2);
		}
		return 0;
	}
}
