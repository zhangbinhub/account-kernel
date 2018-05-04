package OLink.bpm.core.report.crossreport.runtime.action;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseDimension;
import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseFilter;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleRow;
import OLink.bpm.core.report.crossreport.runtime.analyzer.AnalyseTable;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.util.StringUtil;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataSet;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataType;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;

/**
 * The html analyse table console writer which implement the AnalyseTableWriter
 * interface, it can export the analyze table to html.
 * 
 */
public class HtmlAnalyseTableWriter implements AnalyseTableWriter {
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.orange.console.AnalyseTableWriter#write(java.lang.String,
	 *      net.orange.console.model.analyzer.AnalyseTable)
	 */
	public String getTableHtml(String url, AnalyseTable analyseTable) throws Exception {
		if(analyseTable == null)
			return "<b>No data found</b>";

		StringBuffer html = new StringBuffer();

		html.append("<div id=\"dataTable\"><table  border='1' cellSpacing='0' borderColor='#e4e4e4' cellPadding='3' width='100%' bgColor='#ffffff'>");
		if(analyseTable.isShowRowHead()){
			html.append(getRowSetHtml(analyseTable,getColumnSetHtml(analyseTable)));
		}else{
			html.append(getColumnSetHtml(analyseTable));
			html.append(getRowSetHtml(analyseTable));
		}
		html.append("</table></div>");
		return html.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.orange.console.AnalyseTableWriter#getFilterHtml(java.lang.String,
	 *      java.util.Collection,
	 *      net.orange.console.model.dataset.ConsoleDataSet)
	 */
	public String getFilterHtml(String url, Collection<AnalyseFilter> filters, Collection<ConsoleData> filtersData, ConsoleDataSet dataset) {
		StringBuffer html = new StringBuffer();

		html.append("<div id=\"searchFormTable\"><table >");

		Object[] filterAry = filters.toArray();
		Object[] filterDataAry = filtersData.toArray();

		for (int i = 0; i < filterAry.length; ++i) {
			AnalyseFilter filter = (AnalyseFilter) filterAry[i];
			ConsoleData filterData = (ConsoleData) filterDataAry[i];
			String columnName = filter.getMetaData().getColumnName().toUpperCase();

			html.append("<tr>");

			html.append("<td class='content-detail-title-right' nowrap> ");
			html.append(columnName.indexOf("ITEM_") >= 0 ? columnName.substring(columnName.indexOf("ITEM_") + 5)
					: columnName);
			html.append("</td>");

			html.append("<td align='left' class='content-detail' nowrap>");
			html.append("<select name='filter_" + filter.getMetaData().getColumnName()
					+ "' width='' onchange='document.forms[0].submit();'>");
			html.append("<option value=''></option>");

			html.append(getFilterOptionHtml(filter, filterData, dataset));

			html.append("</select>");

			html.append("</td>");

			html.append("</tr>");
		}
		html.append("</table></div>");

		return html.toString();
	}

	/**
	 * Get the analyse table column header html
	 * 
	 * @param analyseTable
	 *            The target analyse table.
	 * @return The analyse table column head html.
	 */
	private String getColumnSetHtml(AnalyseTable analyseTable) {

		StringBuffer buffer = new StringBuffer();
		int i = 1;

		
		for (Iterator<AnalyseDimension> iterator = analyseTable.getColumnSet().iterator(); iterator.hasNext();) {
			AnalyseDimension column = iterator.next();
			buffer.append("<tr>");

			List<String> keySet = column.getDatasKeyIterator();

			for (Iterator<AnalyseDimension> iterator2 = analyseTable.getRowSet().iterator(); iterator2.hasNext();) {
				AnalyseDimension deimension = iterator2.next();
				ConsoleMetaData metaData = deimension.getMetaData();
				if(metaData != null){
					buffer.append("<td style='background-color:#EEF0F2' align='center' nowrap>");
					String columnName = metaData.getColumnName().toUpperCase();
					buffer.append(columnName.indexOf("ITEM_") >= 0 ? columnName.substring(columnName.indexOf("ITEM_") + 5)
							: columnName);
					buffer.append("</td>");
				}

			}
			
			for (Iterator<String> iterator2 = keySet.iterator(); iterator2.hasNext();) {
				String key = iterator2.next();
				if(analyseTable.getCalculationMethod()!=null){
				  key = StringUtil.isBlank(key)?"汇总":key;
			      buffer.append("<td style='background-color:#EEF0F2'  align='center' colspan='" + getColumnSpan(key, analyseTable) + "' nowrap >");
				  buffer.append(getKeyLabel(key));
				  buffer.append("</td>");
				}
			}
			
			if (analyseTable.isDisplayCol())
				buffer.append("<td align='center' class=\"tr-total\" >{*[Total]*}</td>");

			buffer.append("</tr>");
			++i;
		}
		return buffer.toString();
	}

	/**
	 * Get the column span.
	 * 
	 * @param analyseColumnKey
	 *            The analyse column key.
	 * @param analyseTable
	 *            The analyse table.
	 * @return The The analyse column span count.
	 */
	private int getColumnSpan(String analyseColumnKey, AnalyseTable analyseTable) {

		Object[] columnSet = analyseTable.getColumnSet().toArray();

		AnalyseDimension lastAnalyseColumn = (AnalyseDimension) columnSet[columnSet.length - 1];

		Collection<String> keySet = lastAnalyseColumn.getDatas().keySet();

		int count = 0;

		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (key.startsWith(analyseColumnKey + AnalyseDimension.KEY_KEY_SEPARATOR))
				count++;
		}

		return count;
	}

	/**
	 * Get the key label.
	 * 
	 * @param key
	 *            The whole key
	 * @return The key label
	 */
	private String getKeyLabel(String key) {
		String[] keys = key.split(AnalyseDimension.KEY_KEY_SEPARATOR);
		return keys[keys.length - 1];
	}
	/**
	 * 每行都有自己的表头
	 */
	private String getRowSetHtml(AnalyseTable analyseTable,String columnSetHtml) {
		StringBuffer buffer = new StringBuffer();
		DecimalFormat df = new DecimalFormat("######0.00");
		

		Object[] rowDimesions = analyseTable.getRowSet().toArray();
		AnalyseDimension lastRowDimension = (AnalyseDimension) rowDimesions[rowDimesions.length - 1];

		Object[] columnDimesions = analyseTable.getColumnSet().toArray();
		AnalyseDimension lastColumnDimension  = null;
		if(columnDimesions == null ||columnDimesions.length == 0)
		{ 
			ConsoleMetaData metaData = new ConsoleMetaData(ConsoleDataType.toDataType(-1),"小计",0);
		    ConsoleDataSet dataSet = new ConsoleDataSet(new HashMap<String, ConsoleMetaData>());
			lastColumnDimension = new AnalyseDimension(metaData,dataSet);
		}else
		{
			lastColumnDimension = (AnalyseDimension) columnDimesions[columnDimesions.length - 1];
		}
		 

		Map<String, Collection<ConsoleRow>> resultDatas = lastRowDimension.getCrossDatas(lastColumnDimension);
		//Collection rowSum = new ArrayList();
		Map<String, Double> map = new LinkedHashMap<String, Double>();
		double countRow = 0;
		String rowKeyNames = RuntimeHelper.getArrayStr(rowDimesions);

		Iterator<AnalyseDimension> item_col = analyseTable.getColumnSet().iterator();
		String columnKey =  "";
		if(item_col.hasNext())
		{
			AnalyseDimension currentAnalyseColumn = item_col.next();
			columnKey = currentAnalyseColumn.getMetaData().getColumnName();
		}

		for (Iterator<String> iterator = lastRowDimension.getDatasKeyIterator().iterator(); iterator.hasNext();) {
	
			String lastRowKey = iterator.next();
			String[] lastrowKeyName = RuntimeHelper.getKeyArray(rowDimesions);
			String onClink = "";
			buffer.append(columnSetHtml);
			buffer.append("<tr class=\"table-tr1\" onmouseover=\"this.className='table-tr-onchange1';\" onmouseout=\"this.className='table-tr1';\">");
			// Output the row deminsions
			String[] keys = lastRowKey.split(AnalyseDimension.KEY_KEY_SEPARATOR);

			for (int i = 0; i < keys.length; ++i) {
				String currentKey = "";

				for (int j = 0; j <= i; ++j) {
					currentKey = (currentKey.length() > 0) ? currentKey + AnalyseDimension.KEY_KEY_SEPARATOR +(StringUtil.isBlank(keys[j])?"null":keys[j])
							: (StringUtil.isBlank(keys[j])?"null":keys[j]);
				}
				
				 onClink = "onclick=ev_onclick(\""+currentKey.replaceAll(" ", "&NBSP;")+"\",\""+RuntimeHelper.getArrayStrFromTo(i,lastrowKeyName)+"\")";
						buffer.append("<td  align='center' class='content-detail-titleccc'><a href=\"#\" "+onClink+" >" + (getKeyLabel(currentKey)).replaceAll("null", "&nbsp;") + "</a></td>");

			}

			// Output the calculation result.
			double colSummarize = 0.00;
			double colValue = 0.00;
			double countColumn = 0;
			for (Iterator<String> iterator2 = lastColumnDimension.getDatasKeyIterator().iterator(); iterator2.hasNext();) {
				String lastColumnKey = iterator2.next();
				String relastRowKey = RuntimeHelper.reOrgLastRowKey(lastRowKey);
				onClink =  "onclick=ev_onclick(\""+relastRowKey.replaceAll("&nbsp;", "null")+lastColumnKey+"\",\""+rowKeyNames+columnKey+";"+"\")";
				String resultKey = lastRowKey + AnalyseDimension.DIM_DIM_SEPARATOR + lastColumnKey;
				Collection<ConsoleRow> collection = resultDatas.get(resultKey);
				Collection<ConsoleRow> datas = collection;
				
				//实现没有汇总的情况
				if(analyseTable.getCalculationMethod()!=null){
					buffer.append(getCalculationHtml(datas, analyseTable.getCalculationField(), analyseTable.getCalculationMethod(),onClink));
				}

				if (analyseTable.isDisplayCol()) {
					colValue = this.getSummarizeTotal(datas, analyseTable.getCalculationField(), analyseTable
							.getCalculationMethod());
					colSummarize += colValue;
				}

				if (analyseTable.isDisplayRow()) {
					double rowValue = this.getSummarizeTotal(datas, analyseTable.getCalculationField(), analyseTable
							.getCalculationMethod());
					if (map.containsKey(lastColumnKey)) {
						double temp = map.get(lastColumnKey).doubleValue();
						map.put(lastColumnKey, new Double(temp + rowValue));
					} else
						map.put(lastColumnKey, new Double(rowValue));
				}
				countColumn++;
			} 
			if (analyseTable.isDisplayCol()) {
				countColumn = countColumn == 0 ? 1 : countColumn;

				if (CalculationMethod.valueOf(analyseTable.getColCalMethod()).equals(CalculationMethod.AVERAGE))
					buffer.append("<td align='right'  class=\"tr-total\" >" + df.format(colSummarize / countColumn) + "</td>");
				else
					buffer.append("<td align='right'  class=\"tr-total\" >" + colSummarize + "</td>");

				if (map.containsKey("Total")) {
					double temp = map.get("Total").doubleValue();
					map.put("Total", new Double(temp + colSummarize));
				} else {
					map.put("Total", new Double(colSummarize));
				}

			}
			countRow++;}
		
		buffer.append("</tr>");
		countRow = countRow == 0 ? 1 : countRow;
		if (analyseTable.isDisplayRow()) {
			buffer
					.append("<tr class=\"tr-total\" >");
			buffer.append("<td colspan=" + analyseTable.getRowSet().size() + ">{*[Grant_Total]*}:</td>");
			for (Iterator<Entry<String, Double>> iterator = map.entrySet().iterator();iterator != null && iterator.hasNext();) {
				//String str = (String) iterator.next();
				Entry<String, Double> entry = iterator.next();
				String str = entry.getKey();
				Double value = entry.getValue();
				String onClink =  "onclick=ev_onclick(\""+str.replaceAll("&nbsp;", "null")+"\",\""+columnKey+";"+"\")";
				buffer.append("<td align='right' ><a href=\"#\" "+onClink+" ><font color=red style=\"font-weight:bold\">");
				if (CalculationMethod.valueOf(analyseTable.getRowCalMethod()) == CalculationMethod.AVERAGE)
					buffer.append(df.format(value / countRow));
				else
					buffer.append(value);
				buffer.append("</font></td></td>");
			}
			buffer.append("</tr>");
		}

		return buffer.toString();
	}

	/**
	 * Get the analyse table row html
	 * 
	 * @param analyseTable
	 *            The target analyse table.
	 * @return The analyse table column head html.
	 */
	private String getRowSetHtml(AnalyseTable analyseTable) {
		StringBuffer buffer = new StringBuffer();
		DecimalFormat df = new DecimalFormat("######0.00");
		

		Object[] rowDimesions = analyseTable.getRowSet().toArray();
		AnalyseDimension lastRowDimension = (AnalyseDimension) rowDimesions[rowDimesions.length - 1];

		Object[] columnDimesions = analyseTable.getColumnSet().toArray();
		AnalyseDimension lastColumnDimension  = null;
		if(columnDimesions == null ||columnDimesions.length == 0)
		{ 
			ConsoleMetaData metaData = new ConsoleMetaData(ConsoleDataType.toDataType(-1),"小计",0);
		    ConsoleDataSet dataSet = new ConsoleDataSet(new HashMap<String, ConsoleMetaData>());
			lastColumnDimension = new AnalyseDimension(metaData,dataSet);
		}else
		{
			lastColumnDimension = (AnalyseDimension) columnDimesions[columnDimesions.length - 1];
		}
		 

		Map<String, Collection<ConsoleRow>> resultDatas = lastRowDimension.getCrossDatas(lastColumnDimension);
		Collection<String> outputValue = new ArrayList<String>();
		//Collection rowSum = new ArrayList();
		Map<String, Double> map = new LinkedHashMap<String, Double>();
		double countRow = 0;
		String rowKeyNames = RuntimeHelper.getArrayStr(rowDimesions);

		Iterator<AnalyseDimension> item_col = analyseTable.getColumnSet().iterator();
		String columnKey =  "";
		if(item_col.hasNext())
		{
			AnalyseDimension currentAnalyseColumn = item_col.next();
			if(currentAnalyseColumn.getMetaData() != null)
				columnKey = currentAnalyseColumn.getMetaData().getColumnName();
		}

		for (Iterator<String> iterator = lastRowDimension.getDatasKeyIterator().iterator(); iterator.hasNext();) {
	
			String lastRowKey = iterator.next();
			String[] lastrowKeyName = RuntimeHelper.getKeyArray(rowDimesions);
			String onClink = "";
			buffer.append("<tr class=\"table-tr1\" onmouseover=\"this.className='table-tr-onchange1';\" onmouseout=\"this.className='table-tr1';\">");
			// Output the row deminsions
			String[] keys = lastRowKey.split(AnalyseDimension.KEY_KEY_SEPARATOR);

			for (int i = 0; i < keys.length; ++i) {
				String currentKey = "";

				for (int j = 0; j <= i; ++j) {
					currentKey = (currentKey.length() > 0) ? currentKey + AnalyseDimension.KEY_KEY_SEPARATOR +(StringUtil.isBlank(keys[j])?"null":keys[j]) 
							: (StringUtil.isBlank(keys[j])?"null":keys[j]);
				}
				
				 onClink = "onclick=ev_onclick(\""+currentKey.replaceAll(" ", "&NBSP;")+"\",\""+RuntimeHelper.getArrayStrFromTo(i,lastrowKeyName)+"\")";
				if (!outputValue.contains(currentKey)) {
						buffer.append("<td align='center' nowrap  class='content-detail-titleccc' rowspan='"
							+ getRowSpan(currentKey, analyseTable) + "'><a href=\"#\" "+onClink+" >" + (getKeyLabel(currentKey)).replaceAll("null", "&nbsp;") + "</a></td>");

					outputValue.add(currentKey);
				}
			}

			// Output the calculation result.
			double colSummarize = 0.0;
			double colValue = 0.0;
			double countColumn = 0;
			for (Iterator<String> iterator2 = lastColumnDimension.getDatasKeyIterator().iterator(); iterator2.hasNext();) {
				String lastColumnKey = iterator2.next();
				String relastRowKey = RuntimeHelper.reOrgLastRowKey(lastRowKey);
				onClink =  "onclick=ev_onclick(\""+relastRowKey.replaceAll("&nbsp;", "null")+lastColumnKey+"\",\""+rowKeyNames+columnKey+";"+"\")";
				String resultKey = lastRowKey + AnalyseDimension.DIM_DIM_SEPARATOR + lastColumnKey;
				Collection<ConsoleRow> collection = resultDatas.get(resultKey);
				Collection<ConsoleRow> datas = collection;
				//实现没有汇总的情况
				if(analyseTable.getCalculationMethod()!=null){
					buffer.append(getCalculationHtml(datas, analyseTable.getCalculationField(), analyseTable.getCalculationMethod(),onClink));
				}

				if (analyseTable.isDisplayCol()) {
					colValue = this.getSummarizeTotal(datas, analyseTable.getCalculationField(), analyseTable
							.getCalculationMethod());
					colSummarize += colValue;
				}

				if (analyseTable.isDisplayRow()) {
					double rowValue = this.getSummarizeTotal(datas, analyseTable.getCalculationField(), analyseTable
							.getCalculationMethod());
					if (map.containsKey(lastColumnKey)) {
						double temp = map.get(lastColumnKey).doubleValue();
						map.put(lastColumnKey, new Double(temp + rowValue));
					} else
						map.put(lastColumnKey, new Double(rowValue));
				}
				countColumn++;
			} 
			if (analyseTable.isDisplayCol()) {
				countColumn = countColumn == 0 ? 1 : countColumn;

				if (CalculationMethod.valueOf(analyseTable.getColCalMethod()).equals(CalculationMethod.AVERAGE))
					buffer.append("<td align='right'  class=\"tr-total\" >" + df.format(colSummarize / countColumn) + "</td>");
				else
					buffer.append("<td align='right'  class=\"tr-total\" >" + colSummarize + "</td>");

				if (map.containsKey("Total")) {
					double temp = map.get("Total").doubleValue();
					map.put("Total", new Double(temp + colSummarize));
				} else {
					map.put("Total", new Double(colSummarize));
				}

			}
			countRow++;}
		
		buffer.append("</tr>");
		countRow = countRow == 0 ? 1 : countRow;
		if (analyseTable.isDisplayRow()) {
			buffer
					.append("<tr class=\"tr-total\" >");
			buffer.append("<td colspan=" + analyseTable.getRowSet().size() + ">{*[Grant_Total]*}:</td>");
			for (Iterator<Entry<String, Double>> iterator = map.entrySet().iterator();iterator != null && iterator.hasNext();) {
				//String str = (String) iterator.next();
				Entry<String, Double> entry = iterator.next();
				String str = entry.getKey();
				Double value = entry.getValue();
				String onClink =  "onclick=ev_onclick(\""+str.replaceAll("&nbsp;", "null")+"\",\""+columnKey+";"+"\")";
				buffer.append("<td align='right' ><a href=\"#\" "+onClink+" ><font color=red style=\"font-weight:bold\">");
				if (CalculationMethod.valueOf(analyseTable.getRowCalMethod()) == CalculationMethod.AVERAGE)
					buffer.append(df.format(value / countRow));
				else
					buffer.append(value);
				buffer.append("</font></td></td>");
			}
			buffer.append("</tr>");
		}

		return buffer.toString();
	}

	/**
	 * Get the column span.
	 * 
	 * @param analyseRowKey
	 *            The analyse column key.
	 * @param analyseTable
	 *            The analyse table.
	 * @return The The analyse column span count.
	 */
	private int getRowSpan(String analyseRowKey, AnalyseTable analyseTable) {

		Object[] rowSet = analyseTable.getRowSet().toArray();

		AnalyseDimension lastAnalyseColumn = (AnalyseDimension) rowSet[rowSet.length - 1];

		Collection<String> keySet = lastAnalyseColumn.getDatas().keySet();

		int count = 0;

		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (key.startsWith(analyseRowKey + AnalyseDimension.KEY_KEY_SEPARATOR))
				count++;
		}

		return count;
	}

	/**
	 * Get the calculation result html
	 * 
	 * @param datas
	 *            The result row
	 * @param calculationField
	 *            The calculation field meta data.
	 * @param method
	 *            The calculation method
	 * @return The result in html
	 */
	private String getCalculationHtml(Collection<ConsoleRow> datas, ConsoleMetaData calculationField, CalculationMethod method,String onClikStr) {
		StringBuffer buffer = new StringBuffer();
		Calculator calculator = new Calculator();

		buffer.append("<td class='content-data'  align='right'>");
		buffer.append("<a  href=\"#\" "+onClikStr+">");

		if (method.equals(CalculationMethod.DISTINCT)) {
			buffer.append("<table>");

			if (datas != null && datas.size() > 0) {
				for (Iterator<ConsoleRow> iterator = datas.iterator(); iterator.hasNext();) {
					ConsoleRow row = iterator.next();
					ConsoleData data = row.getData(calculationField);
					buffer.append("<tr class='content-detail'>");
					buffer.append("<td align='right'>");
					buffer.append(data != null ? data.getStringValue() : "");
					buffer.append("</td>");
					buffer.append("</tr>");
				}

			}

			buffer.append("</table>");
		} else if (method.equals(CalculationMethod.MAX)) {
			if (datas != null && datas.size() > 0)
				buffer.append(calculator.max(datas, calculationField).toString());
			else
				buffer.append("&nbsp;");
		} else if (method.equals(CalculationMethod.MIN)) {
			if (datas != null && datas.size() > 0)
				buffer.append(calculator.min(datas, calculationField).toString());
			else
				buffer.append("&nbsp;");
		} else {
			buffer.append(calculator.compute(datas, method, calculationField));
		}
		buffer.append("</a>");
		buffer.append("</td>");
		return buffer.toString();
	}

	/**
	 * Get the filter option html.
	 * 
	 * @param The
	 *            filter.
	 * @param filterData
	 *            The filter console data.
	 * @param dataset
	 *            The console data set.
	 * @return the filter option html
	 */
	private String getFilterOptionHtml(AnalyseFilter filter, ConsoleData filterData, ConsoleDataSet dataset) {
		List<String> datas = new ArrayList<String>();
		StringBuffer buffer = new StringBuffer();

		if (dataset != null) {
			for (Iterator<ConsoleRow>  iterator = dataset.getRows().iterator(); iterator.hasNext();) {
				ConsoleRow row = iterator.next();
				ConsoleData data = row.getData(filter.getMetaData());

				if (!datas.contains(data.getStringValue()))
					datas.add(data.getStringValue());
			}
		}
		//Comparator cmp = Collator.getInstance(java.util.Locale.CHINA); 
		//Collections.sort(datas,cmp);
		for (Iterator<String> iterator = datas.iterator(); iterator.hasNext();) {
			String option = iterator.next();
			if (option != null) {
				buffer.append("<option value='" + option + "' ");

				if (filterData != null && option.equals(filterData.getStringValue()))
					buffer.append("selected");
				buffer.append(">");

				buffer.append(option);

				buffer.append("</option>");
			}
		}

		return buffer.toString();
	}

	/**
	 * Get the summarize result html
	 * 
	 * @param datas
	 *            The result row
	 * @param calculationField
	 *            The calculation field meta data.
	 * @param method
	 *            The calculation method
	 * @return The result in html
	 */
	private double getSummarizeTotal(Collection<ConsoleRow> datas, ConsoleMetaData calculationField, CalculationMethod method) {
		//StringBuffer buffer = new StringBuffer();
		Calculator calculator = new Calculator();
		return calculator.compute(datas, method, calculationField);
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.orange.console.AnalyseTableWriter#getFilterHtml(java.lang.String,
	 *      java.util.Collection,
	 *      net.orange.console.model.dataset.ConsoleDataSet)
	 */
	public String getDetailDataHtml(Map<Object, Object> metadata,ResultSet rs) {
		StringBuffer html = new StringBuffer();

	/**	html.append("<table >");

		Object[] filterAry = filters.toArray();
		Object[] filterDataAry = filtersData.toArray();

		for (int i = 0; i < filterAry.length; ++i) {
			AnalyseFilter filter = (AnalyseFilter) filterAry[i];
			ConsoleData filterData = (ConsoleData) filterDataAry[i];
			String columnName = filter.getMetaData().getColumnName().toUpperCase();

			html.append("<tr>");

			html.append("<td class='content-detail-title-right' nowrap> ");
			html.append(columnName.indexOf("ITEM_") >= 0 ? columnName.substring(columnName.indexOf("ITEM_") + 5)
					: columnName);
			html.append("</td>");

			html.append("<td align='left' class='content-detail' nowrap>");
			html.append("<select name='filter_" + filter.getMetaData().getColumnName()
					+ "' width='' onchange='document.forms[0].submit();'>");
			html.append("<option value=''></option>");

			html.append(getFilterOptionHtml(filter, filterData, dataset));

			html.append("</select>");

			html.append("</td>");

			html.append("</tr>");
		}**/
		html.append("</table>");

		return html.toString();
	}

}
