package OLink.bpm.core.report.crossreport.runtime.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleRow;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataSet;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;

/**
 * The dimension definition of the AnalyseTable, it is the iterative data
 * structure and contain the row data set.
 * 
 */
public class AnalyseDimension {

	public final static String KEY_KEY_SEPARATOR = "!!";

	public final static String DIM_DIM_SEPARATOR = "@@";
	/**
	 * The console meta data of the this analyze column.
	 */
	private ConsoleMetaData metaData;

	/**
	 * The parent AnalyseColumn.
	 */
	private AnalyseDimension parentAnalyseDimension;

	/**
	 * The dimension data of the AnalyseTable.
	 */
	private Map<String, Collection<ConsoleRow>> datas = new LinkedHashMap<String, Collection<ConsoleRow>>();

	/**
	 * The constructor with the console meta data and initialize console data
	 * set , it may use for first level constructor.
	 * 
	 * @param metaData
	 *            The target field meta data.
	 * @param dataSet
	 *            The initialize console data set
	 */
	public AnalyseDimension(ConsoleMetaData metaData, ConsoleDataSet dataSet) {
		this.metaData = metaData;
		setRootDatas(metaData, dataSet);
	}

	/**
	 * The constructor with the console meta data and parentAnalyseRow , it may
	 * use for non-first level constructor.
	 * 
	 * @param metaData
	 *            The target field meta data.
	 * @param parentAnalyseRow
	 *            The parent AnalyseRow
	 */
	public AnalyseDimension(ConsoleMetaData metaData, AnalyseDimension parentAnalyseRow) {

		this.metaData = metaData;
		this.parentAnalyseDimension = parentAnalyseRow;

		Map<String, Collection<ConsoleRow>> parentDatas = parentAnalyseRow.getDatas();

		for (Iterator<Entry<String, Collection<ConsoleRow>>> iterator = parentDatas.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Collection<ConsoleRow>> entry = iterator.next();
			String parentKey = entry.getKey();

			Collection<ConsoleRow> parentData = parentDatas.get(parentKey);

			for (Iterator<ConsoleRow> iterator2 = parentData.iterator(); iterator2.hasNext();) {
				ConsoleRow currentRow = iterator2.next();
                String tempKey =  "";
                if(StringUtil.isBlank(currentRow.getData(metaData).getStringValue()))
                	tempKey = "&nbsp;";
                else
                	tempKey = currentRow.getData(metaData).getStringValue();
				String key = parentKey + KEY_KEY_SEPARATOR + tempKey;

				if (datas.get(key) == null) {
					Collection<ConsoleRow> rows = new ArrayList<ConsoleRow>();

					rows.add(currentRow);
				

					datas.put(key, rows);
				} else {
					datas.get(key).add(currentRow);
				}
			}
		}
	}

	/**
	 * Initialize the first level console data set.
	 * 
	 * @param metaData
	 *            The target field meta data.
	 * @param dataSet
	 *            The initialize console data set.
	 */
	public void setRootDatas(ConsoleMetaData metaData, ConsoleDataSet dataSet) {
		for (Iterator<ConsoleRow> iterator = dataSet.getRows().iterator(); iterator.hasNext();) {
			ConsoleRow currentRow = iterator.next();
			ConsoleData consoleData = currentRow.getData(metaData);

			String key = (consoleData != null && consoleData.getStringValue() != null) ? consoleData.getStringValue()
					: "";

			if (datas.get(key) == null) {
				Collection<ConsoleRow> rows = new ArrayList<ConsoleRow>();

				rows.add(currentRow);

				datas.put(key, rows);
			} else {
				datas.get(key).add(currentRow);
			}
		}
		
		/*for (Iterator<String> iterator = datas.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
		}*/

	}

	/**
	 * @return the metaData
	 */
	public ConsoleMetaData getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData
	 *            the metaData to set
	 */
	public void setMetaData(ConsoleMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the parentAnalyseRow
	 */
	public AnalyseDimension getParentAnalyseDimension() {
		return parentAnalyseDimension;
	}

	/**
	 * @param parentAnalyseRow
	 *            the parentAnalyseRow to set
	 */
	public void setParentAnalyseDimension(AnalyseDimension parentAnalyseRow) {
		this.parentAnalyseDimension = parentAnalyseRow;
	}

	/**
	 * @return the datas
	 */
	public Map<String, Collection<ConsoleRow>> getDatas() {
		return datas;
	}

	/**
	 * Return the datas key which is in order.
	 * 
	 * @return The datas key which is in order.
	 */
	public List<String> getDatasKeyIterator() {

		List<AnalyseDataKeyPair> list = new ArrayList<AnalyseDataKeyPair>();

		Collection<AnalyseDimension> hierarchy = getHierarchy();

		for (Iterator<String> iterator = datas.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			//key = StringUtil.isBlank(key)?null:key;
			AnalyseDataKeyPair pair = new AnalyseDataKeyPair(key, hierarchy);
			list.add(pair);
		}

		//Collections.sort(list, new AnalyseDataKeyComparator());

		List<String> keyList = new ArrayList<String>();

		for (Iterator<AnalyseDataKeyPair> iterator = list.iterator(); iterator.hasNext();) {
			AnalyseDataKeyPair pair = iterator.next();
			keyList.add(pair.getKey());
		}

		//  Comparator cmp = Collator.getInstance(java.util.Locale.CHINA); 
		 // Collections.sort(keyList,cmp);
		return keyList;
	}

	/**
	 * @param datas
	 *            the datas to set
	 */
	public void setDatas(Map<String, Collection<ConsoleRow>> datas) {
		this.datas = datas;
	}

	/**
	 * Get the data divide by the another AnalyseDimension.
	 * 
	 * @param targetDimesion
	 *            The target dimension
	 * @return The cross data.
	 */
	public Map<String, Collection<ConsoleRow>> getCrossDatas(AnalyseDimension targetDimesion) {

		Map<String, Collection<ConsoleRow>> rowDataMap = getDatas();
		Map<String, Collection<ConsoleRow>> columntDataMap = targetDimesion.getDatas();

		Map<String, Collection<ConsoleRow>> resultData = new HashMap<String, Collection<ConsoleRow>>();

		for (Iterator<Entry<String, Collection<ConsoleRow>>> iterator = rowDataMap.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Collection<ConsoleRow>> rowEntry = iterator.next();
			String rowKey = rowEntry.getKey();
			Collection<ConsoleRow> rowData = rowEntry.getValue();

			for (Iterator<ConsoleRow> iterator2 = rowData.iterator(); iterator2.hasNext();) {
				ConsoleRow rowConsoleRow = iterator2.next();
			if(columntDataMap.entrySet().iterator().hasNext()){
				for (Iterator<Entry<String, Collection<ConsoleRow>>> iterator3 = columntDataMap.entrySet().iterator(); iterator3.hasNext();) {
					Entry<String, Collection<ConsoleRow>> columnEntry = iterator3.next();
					String columnKey = columnEntry.getKey();
					Collection<ConsoleRow> columntData = columnEntry.getValue();

					for (Iterator<ConsoleRow> iterator4 = columntData.iterator(); iterator4.hasNext();) {
						ConsoleRow columnConsoleRow = iterator4.next();
						// If the row data match the column data.
						if (rowConsoleRow.getPrimaryKey() == columnConsoleRow.getPrimaryKey()) {

							String newKey = rowKey + DIM_DIM_SEPARATOR + columnKey;

							if (resultData.get(newKey) == null) {
								Collection<ConsoleRow> resultRows = new ArrayList<ConsoleRow>();

								resultRows.add(rowConsoleRow);

								resultData.put(newKey, resultRows);
							} else {
								resultData.get(newKey).add(rowConsoleRow);
							}
						}
					}
				}
				
			 }else{
				 Collection<ConsoleRow> resultRows = new ArrayList<ConsoleRow>();
				 resultRows.add(rowConsoleRow);
				 resultData.put(rowKey, resultRows);
				 //resultData.put(rowKey, rowConsoleRow);
			 }
			}
		}

		return resultData;
	}

	/**
	 * Get the analyse dimension hierarchy.
	 * 
	 * @return The analyse dimension hierarchy array.
	 */
	private Collection<AnalyseDimension> getHierarchy() {
		Collection<AnalyseDimension> hierarchy = new Stack<AnalyseDimension>();

		hierarchy.add(this);

		AnalyseDimension parent = parentAnalyseDimension;

		while (parent != null) {
			hierarchy.add(parent);
			parent = parent.getParentAnalyseDimension();
		}

		return hierarchy;
	}
}
