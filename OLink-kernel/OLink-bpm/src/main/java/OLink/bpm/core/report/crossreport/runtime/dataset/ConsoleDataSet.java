package OLink.bpm.core.report.crossreport.runtime.dataset;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.base.dao.DataPackage;
import OLink.bpm.core.dynaform.form.ejb.ValidateMessage;
import OLink.bpm.core.dynaform.view.ejb.Column;
import OLink.bpm.core.dynaform.view.ejb.View;
import OLink.bpm.core.macro.runner.IRunner;
import OLink.bpm.core.macro.runner.JavaScriptFactory;
import OLink.bpm.core.table.constants.FieldConstant;
import OLink.bpm.core.dynaform.document.ejb.Document;
import OLink.bpm.core.user.action.WebUser;

/**
 * The data set, it include one or more than row data.
 */
public class ConsoleDataSet implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6925441767096301086L;
	/**
	 * The row data set.
	 */
	private Collection<ConsoleRow> rows = new ArrayList<ConsoleRow>();
	/**
	 * The meta data of the dataset.
	 */
	private Map<String , ConsoleMetaData> metaDataSet = new HashMap<String , ConsoleMetaData>();

	/**
	 * @return the rows
	 */
	public Collection<ConsoleRow> getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(Collection<ConsoleRow> rows) {
		this.rows = rows;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return (rows != null) ? rows.size() : 0;
	}

	/**
	 * Append one row.
	 * 
	 * @param row
	 *            The target row.
	 */
	public void addRow(ConsoleRow row) {
		rows.add(row);
	}

	/**
	 * Remove one row
	 * 
	 * @param row
	 *            The target row.
	 */
	public void removeRow(ConsoleRow row) {
		rows.remove(row);
	}

	/**
	 * @return the metaDataSet
	 */
	public Map<String , ConsoleMetaData> getMetaDataSet() {
		return metaDataSet;
	}

	/**
	 * @param metaDataSet
	 *            the metaDataSet to set
	 */
	public void setMetaDataSet(Map<String , ConsoleMetaData> metaDataSet) {
		this.metaDataSet = metaDataSet;
	}

	/**
	 * Get the console meta data set iterator.
	 * 
	 * @return the console meta data set iterator.
	 */
	public Iterator<ConsoleMetaData> getMetaDataSetIterator() {
		if (metaDataSet == null)
			return null;

		List<ConsoleMetaData> list = new ArrayList<ConsoleMetaData>();

		list.addAll(metaDataSet.values());

		Collections.sort(list, new ConsoleMetaDataComparator());

		return list.iterator();
	}

	/**
	 * Construct with console meta data set.
	 * 
	 * @param metaDataSet
	 *            The console meta data set.
	 */
	public ConsoleDataSet(Map<String, ConsoleMetaData> metaDataSet) {
		for (Iterator<Entry<String, ConsoleMetaData>> iterator = metaDataSet.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, ConsoleMetaData> entry = iterator.next();
			String key = entry.getKey();
			ConsoleMetaData value = entry.getValue();

			this.metaDataSet.put(key, value);
		}
	}

	/**
	 * Constructor with the Jdbc RowSet, It'will convert the Jdbc RowSet to
	 * Console DataSet.
	 * 
	 * @param rowSet
	 *            The target Jdbc RowSet.
	 * @throws Exception
	 */
	public ConsoleDataSet(ResultSet rowSet) throws Exception {
		long primaryKey = 0;

		if (rowSet != null) {
			ResultSetMetaData resultSetMetaData = rowSet.getMetaData();//获取此 ResultSet 对象的列的编号、类型和属性。

			// format the console meta data.
			for (int i = 1; i <= resultSetMetaData.getColumnCount(); ++i) {
				ConsoleDataType dataType = ConsoleDataType.toDataType(resultSetMetaData.getColumnType(i));
				String columnName = resultSetMetaData.getColumnName(i);

				metaDataSet.put(columnName, new ConsoleMetaData(dataType, columnName, i));
			}
			// format the row data.
			while (rowSet.next()) {
				ConsoleRow row = new ConsoleRow(primaryKey, rowSet, metaDataSet);

				addRow(row);

				primaryKey++;
			}

		}
	}
	
	public ConsoleDataSet(View view, ParamsTable params, WebUser webUser) throws Exception {
		long primaryKey = 0;
		if (view != null) {
			Document searchDocument = null;
			if (view.getSearchForm() != null) {
				try {
					searchDocument = view.getSearchForm().createDocument(params,webUser);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				searchDocument = new Document();
			}
			for (Iterator<Column> iter = view.getColumns().iterator(); iter.hasNext();) {
				int i =0;
				Column column = iter.next();
				ConsoleDataType dataType = null;
				if(column.getFormField()!=null){
					int typeCode = FieldConstant.getTypeCode(column.getFormField().getFieldtype());
					dataType = ConsoleDataType.toDataType(typeCode);//表单字段模式获取数据类型
				}else{
					dataType = ConsoleDataType.toDataType(ConsoleDataType.String.getValue());//脚本模式默认数据类型为字符
				}

				metaDataSet.put(column.getName(), new ConsoleMetaData(dataType, column.getName(), i));
				i++;
			}
			DataPackage<Document> datas = view.getViewTypeImpl().getViewDatasPage(params, 1, Integer.MAX_VALUE, webUser, searchDocument);
			for (Iterator<Document> iter = datas.datas.iterator(); iter.hasNext();) {
				Document doc = iter.next();
				IRunner runner = JavaScriptFactory.getInstance(params.getSessionid(), view.getApplicationid());
				runner.initBSFManager(doc, params, webUser, new ArrayList<ValidateMessage>());
				ConsoleRow row = new ConsoleRow(primaryKey,runner,doc,webUser,view.getColumns(),metaDataSet);

				addRow(row);

				primaryKey++;
			}
			
		}
		
	}
}
