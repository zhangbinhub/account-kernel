package OLink.bpm.core.report.crossreport.runtime.action;

import java.util.Collection;

import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseFilter;
import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseTable;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataSet;

/**
 * The interface that all standard implementations of AnalyzerTableWriter must
 * implement.AnalyzerTableWriter can write the analyze data set or other object
 * at special object, like excel file,text file,database...
 */
public interface AnalyseTableWriter {
	/**
	 * Write the console data set to the object which specify object in url.
	 * 
	 * @param url
	 *            The specify object url.
	 * @param dataSet
	 *            The target current analyse table.
	 * @return The content for the AnalyseTable
	 * @throws Exception
	 */
	String getTableHtml(String url, AnalyseTable analyseTable)
			throws Exception;

	/**
	 * Writer the analyze table filter to to the object which specify object in
	 * url.
	 * 
	 * @param url
	 *            he specify object url.
	 * @param filter
	 *            The analyze table filters
	 * @param filterData
	 *            The analyze table filters data         
	 * @param dataset
	 *            The data set.
	 * @return The content for the AnalyseTable
	 * @throws Exception
	 */
	String getFilterHtml(String url, Collection<AnalyseFilter> filters,
						 Collection<ConsoleData> filtersData, ConsoleDataSet dataset)
			throws Exception;

}
