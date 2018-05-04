package OLink.bpm.core.report.crossreport.runtime.dataset;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * One row of cosole data, it include one or more than console data.
 */
public class ConsoleRow {
	/**
	 * Inner primary key for each row data.
	 */
	private long primaryKey = 0;
	/**
	 * The row data set.
	 */
	private Map<String, ConsoleData> datas = new HashMap<String, ConsoleData>();

	/**
	 * Constructor with the Jdbc rowset and Meta Data Set, it will convert the
	 * current row set data to console row data.
	 * 
	 * @param primaryKey
	 *            The primary key.
	 * @param rowSet
	 *            The Jdbc row set.
	 * @param metaDataSet
	 *            The console meta data set.
	 * @throws Exception
	 */
	public ConsoleRow(long primaryKey, ResultSet rowSet,
			Map<String, ConsoleMetaData> metaDataSet) throws Exception {

		if (metaDataSet != null && rowSet != null) {
			Set<String> set = metaDataSet.keySet();

			if (metaDataSet != null) {
				for (Iterator<String> iter = set.iterator(); iter.hasNext();) {
					String fieldName = iter.next();
					ConsoleMetaData metaData = metaDataSet.get(fieldName);

					if (fieldName != null && fieldName.length() > 0
							&& metaData != null)
						addData(new ConsoleData(rowSet, metaData));
				}
			}
		}
		this.primaryKey =  primaryKey;
	}
	
	public ConsoleRow(long primaryKey, IRunner runner, Document doc, WebUser webUser, Set<Column> columns, Map<String , ConsoleMetaData> metaDataSet) throws Exception {
		Iterator<Column> iter = columns.iterator();
		while (iter.hasNext()) {
			Column col = iter.next();
			String result = col.getTextString(doc, runner, webUser);
			ConsoleMetaData metaData = metaDataSet.get(col.getName());
			addData(new ConsoleData(result,metaData));
		}
		
		this.primaryKey =  primaryKey;
	}

	/**
	 * @return the primaryKey
	 */
	public long getPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey
	 *            the primaryKey to set
	 */
	public void setPrimaryKey(long primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return the datas
	 */
	public List<ConsoleData> getDatas() {
		List<ConsoleData> list = new ArrayList<ConsoleData>();
		list.addAll(datas.values());

		Collections.sort(list, new ConsoleDataComparator());

		return list;
	}

	/**
	 * Append one console data.
	 * 
	 * @param row
	 *            The target row.
	 */
	public void addData(ConsoleData data) {
		datas.put(data.getMetaData().getColumnName(), data);
	}

	/**
	 * Remove one console data
	 * 
	 * @param rowIndex
	 *            The target row.
	 */
	public void removeData(ConsoleData data) {
		datas.remove(data.getMetaData().getColumnName());
	}

	/**
	 * Get the console data according the field meta data.
	 * 
	 * @param metaData
	 *            The field meta data
	 * @return The console data.
	 */
	public ConsoleData getData(ConsoleMetaData metaData) {
		return (datas != null && metaData != null) ? datas.get(metaData
				.getColumnName()) : null;
	}
}
