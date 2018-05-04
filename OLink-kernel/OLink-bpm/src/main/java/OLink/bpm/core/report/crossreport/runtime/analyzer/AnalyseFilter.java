package OLink.bpm.core.report.crossreport.runtime.analyzer;

import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;

/**
 * The Analyze Table Filter.
 */
public class AnalyseFilter {
	/**
	 * The target AnalyseDimension;
	 */
	private ConsoleMetaData metaData;

	/**
	 * The filter value.
	 */
	private ConsoleData selectedValue;

	/**
	 * @return the metaData
	 */
	public ConsoleMetaData getMetaData() {
		return metaData;
	}

	/**
	 * The contructor with console metaData and selected value.
	 * 
	 * @param metaData
	 *            The console metaData
	 * @param selectedValue
	 *            The selected value.
	 */
	public AnalyseFilter(ConsoleMetaData metaData, ConsoleData selectedValue) {
		this.metaData = metaData;
		this.selectedValue = selectedValue;
	}

	/**
	 * @param metaData
	 *            the metaData to set
	 */
	public void setMetaData(ConsoleMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the selectedValue
	 */
	public ConsoleData getSelectedValue() {
		return selectedValue;
	}

	/**
	 * @param selectedValue
	 *            the selectedValue to set
	 */
	public void setSelectedValue(ConsoleData selectedValue) {
		this.selectedValue = selectedValue;
	}
}
