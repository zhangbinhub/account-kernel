package OLink.bpm.core.report.crossreport.runtime.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleRow;
import OLink.bpm.core.report.crossreport.runtime.action.CalculationMethod;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataSet;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;


/**
 * AnalyseTable is the object base on the console data, which can format or
 * analyze the data in different dimension (like the excel pivot table).
 */
public class AnalyseTable implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6744014428148992030L;
	/**
	 * The column set of the AnalyseTable
	 */
	private Collection<AnalyseDimension> columnSet = new ArrayList<AnalyseDimension>();
	/**
	 * The row set of the AnalyseTable
	 */
	private Collection<AnalyseDimension> rowSet = new ArrayList<AnalyseDimension>();
	/**
	 * The filter set of the AnalyseTable
	 */
	private Collection<AnalyseFilter> filterSet = new ArrayList<AnalyseFilter>();

	/**
	 * The data of the AnalyseTable.
	 */
	private ConsoleDataSet dataSet;

	/**
	 * The calculation field.
	 */
	private ConsoleMetaData calculationField;

	/**
	 * The calculation method
	 */
	private CalculationMethod calculationMethod;
	
    private boolean isDisplayRow ;
    
    private boolean isDisplayCol;
    
    private boolean isShowRowHead;
    
    private String rowCalMethod;
    
    private String colCalMethod;
    
    private String report;

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public boolean isDisplayRow() {
		return isDisplayRow;
	}

	public void setDisplayRow(boolean isDisplayRow) {
		this.isDisplayRow = isDisplayRow;
	}

	public boolean isDisplayCol() {
		return isDisplayCol;
	}

	public void setDisplayCol(boolean isDisplayCol) {
		this.isDisplayCol = isDisplayCol;
	}
	public String getRowCalMethod() {
		return rowCalMethod;
	}

	public void setRowCalMethod(String rowCalMethod) {
		this.rowCalMethod = rowCalMethod;
	}

	public String getColCalMethod() {
		return colCalMethod;
	}

	public void setColCalMethod(String colCalMethod) {
		this.colCalMethod = colCalMethod;
	}

	/**
	 * The constructor with the columns and rows dimensions, data, and
	 * calculation method
	 * 
	 * @param columns
	 *            The column dimension set.
	 * @param rows
	 *            The row dimension set.
	 * @param datas
	 *            The data set.
	 * @param method
	 *            The calculation method
	 */
	public AnalyseTable(Collection<ConsoleMetaData> columns,
			Collection<ConsoleMetaData> rows, ConsoleMetaData caculationField,
			CalculationMethod method, Collection<ConsoleMetaData> filters,
			Collection<ConsoleData> filtersData, ConsoleDataSet datas) {

		// 1.Construct the filters.
		Object[] filterAry = filters.toArray();
		Object[] filterDataAry = filtersData.toArray();

		for (int i = 0; i < filterAry.length; ++i) {
			if (filterDataAry[i] != null
					&& ((ConsoleData) filterDataAry[i]).getStringValue() != null
					&& ((ConsoleData) filterDataAry[i]).getStringValue()
							.length() > 0)
				filterSet.add(new AnalyseFilter((ConsoleMetaData) filterAry[i],
						(ConsoleData) filterDataAry[i]));
		}

		// 1.Construct & filter the data.
		if (!isEmptyFitler(filterSet))
			this.dataSet = filterData(datas, filterSet);
		else
			this.dataSet = datas;

		// 3.Construct the coluumn dimension.
		AnalyseDimension currentAnalyseColumn = null;

		for (Iterator<ConsoleMetaData> iterator = columns.iterator(); iterator.hasNext();) {
			ConsoleMetaData metaData = iterator.next();
			currentAnalyseColumn = (currentAnalyseColumn == null) ? new AnalyseDimension(
					metaData, this.dataSet)
					: new AnalyseDimension(metaData, currentAnalyseColumn);

			columnSet.add(currentAnalyseColumn);
		}
		// 4.Construct the row dimension.
		AnalyseDimension currentAnalyseRow = null;

		for (Iterator<ConsoleMetaData> iterator = rows.iterator(); iterator.hasNext();) {
			ConsoleMetaData metaData = iterator.next();
			currentAnalyseRow = (currentAnalyseRow == null) ? new AnalyseDimension(
					metaData, this.dataSet)
					: new AnalyseDimension(metaData, currentAnalyseRow);

			rowSet.add(currentAnalyseRow);
		}
		
		// 5.Construct the calculation field and method
		this.calculationField = caculationField;
		this.calculationMethod = method;
	}

	/**
	 * @return the columnSet
	 */
	public Collection<AnalyseDimension> getColumnSet() {
		return columnSet;
	}

	/**
	 * @param columnSet
	 *            the columnSet to set
	 */
	public void setColumnSet(Collection<AnalyseDimension> columnSet) {
		this.columnSet = columnSet;
	}

	/**
	 * @return the rowSet
	 */
	public Collection<AnalyseDimension> getRowSet() {
		return rowSet;
	}

	/**
	 * @param rowSet
	 *            the rowSet to set
	 */
	public void setRowSet(Collection<AnalyseDimension> rowSet) {
		this.rowSet = rowSet;
	}

	/**
	 * @return the dataSet
	 */
	public ConsoleDataSet getDataSet() {
		return dataSet;
	}

	/**
	 * @param dataSet
	 *            the dataSet to set
	 */
	public void setDataSet(ConsoleDataSet dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * @return the calculationMethod
	 */
	public CalculationMethod getCalculationMethod() {
		return calculationMethod;
	}

	/**
	 * @param calculationMethod
	 *            the calculationMethod to set
	 */
	public void setCalculationMethod(CalculationMethod calculationMethod) {
		this.calculationMethod = calculationMethod;
	}

	/**
	 * @return the calculationField
	 */
	public ConsoleMetaData getCalculationField() {
		return calculationField;
	}

	/**
	 * @param calculationField
	 *            the calculationField to set
	 */
	public void setCalculationField(ConsoleMetaData calculationField) {
		this.calculationField = calculationField;
	}

	/**
	 * @return the filterSet
	 */
	public Collection<AnalyseFilter> getFilterSet() {
		return filterSet;
	}

	/**
	 * @param filterSet
	 *            the filterSet to set
	 */
	public void setFilterSet(Collection<AnalyseFilter> filterSet) {
		this.filterSet = filterSet;
	}

	/**
	 * Filter the data
	 * 
	 * @param orginalDataSet
	 *            The original data set.
	 * @param filterSet
	 *            The filter set
	 * @return The data set after filter.
	 */
	private ConsoleDataSet filterData(ConsoleDataSet orginalDataSet,
			Collection<AnalyseFilter> filterSet) {
		ConsoleDataSet dataSet = new ConsoleDataSet(orginalDataSet
				.getMetaDataSet());

		for (Iterator<ConsoleRow> iterator = orginalDataSet.getRows().iterator(); iterator.hasNext();) {
			ConsoleRow row = iterator.next();
			if (meetFilter(row, filterSet))
				dataSet.addRow(row);
		}
		
		return dataSet;
	}

	/**
	 * Check whether the data meet the filter
	 * 
	 * @param row
	 *            The row data
	 * @param filters
	 *            The filters
	 * @return
	 */
	private boolean meetFilter(ConsoleRow row, Collection<AnalyseFilter> filters) {	
		for (Iterator<AnalyseFilter> iterator = filters.iterator(); iterator.hasNext();) {
			AnalyseFilter filter = iterator.next();
			ConsoleData rowData = row.getData(filter.getMetaData());
			
			if (rowData == null || !rowData.equals(filter.getSelectedValue()))
				return false;
		}
		
		return true;
	}

	/**
	 * Check whether the filter is empty.
	 * 
	 * @param filterSet
	 *            The filter set.
	 * @return Whether the filter is empty.
	 */
	private boolean isEmptyFitler(Collection<AnalyseFilter> filterSet) {
		if (filterSet == null || filterSet.size() <= 0)
			return true;

		for (Iterator<AnalyseFilter> iterator = filterSet.iterator(); iterator.hasNext();) {
			AnalyseFilter filter = iterator.next();
			if (filter != null && filter.getSelectedValue() != null)
				return false;
		}
		
		return true;
	}

	public boolean isShowRowHead() {
		return isShowRowHead;
	}

	public void setShowRowHead(boolean isShowRowHead) {
		this.isShowRowHead = isShowRowHead;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}