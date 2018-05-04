package OLink.bpm.core.report.crossreport.runtime.action;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The calculation method during the data analyze. it may the aggregate
 * function, (like summarize, count, max...) or data collection.
 */
public class CalculationMethod implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5446957457746459975L;
	public final static CalculationMethod SUM = new CalculationMethod(0);
	public final static CalculationMethod AVERAGE = new CalculationMethod(1);
	public final static CalculationMethod DISTINCT = new CalculationMethod(2);
	public final static CalculationMethod COUNT = new CalculationMethod(3);
	public final static CalculationMethod MAX = new CalculationMethod(4);
	public final static CalculationMethod MIN = new CalculationMethod(5);

	private int value;

	private static Map<String, CalculationMethod> nameMap = new LinkedHashMap<String, CalculationMethod>();

	static {
		nameMap.put("SUM", SUM);
		nameMap.put("AVERAGE", AVERAGE);
		nameMap.put("DISTINCT", DISTINCT);
		nameMap.put("COUNT", COUNT);
		nameMap.put("MAX", MAX);
		nameMap.put("MIN", MIN);

	}

	public CalculationMethod(int value) {
		this.value = value;
	}

	public boolean equals(Object obj) {
		if (obj instanceof CalculationMethod) {
			return this.value == ((CalculationMethod) obj).value;
		}
		return super.equals(obj);
	}

	public int hashCode() {
		return value * 31;
	}

	public static CalculationMethod valueOf(String name) {
		return nameMap.get(name);
	}
}
