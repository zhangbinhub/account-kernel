package OLink.bpm.core.report.crossreport.runtime.action;

import java.io.FileOutputStream;
import java.util.Iterator;

import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleDataSet;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleRow;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleData;
import OLink.bpm.core.report.crossreport.runtime.dataset.ConsoleMetaData;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * The excel console writer which implement the ConsoleWriter interface, it can
 * export the console object(like data set) to excel file.
 * 
 */
public class ExcelWriter {

	private static int MAX_EXCEL_ROW_COUNT = 65535;

	/**
	 * 
	 * @param url
	 * @param dataSet
	 * @SuppressWarnings 使用了过时的POI API
	 * @return
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public long write(String url, ConsoleDataSet dataSet) throws Exception {
		try {
			if (url != null && url.length() > 0 && dataSet != null) {
				// 1.Create the excel work sheet
				HSSFWorkbook workbook = new HSSFWorkbook();
				HSSFSheet sheet = workbook.createSheet("result");

				// 2.Export the title
				Iterator<ConsoleMetaData> metaDataSet = dataSet.getMetaDataSetIterator();
				short columnIndex = 0;

				while (metaDataSet.hasNext()) {
					ConsoleMetaData metaData = metaDataSet.next();

					HSSFRow row = sheet.createRow(0);
					HSSFCell cell = row.createCell(columnIndex);
//					cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);
					cell.setCellValue(metaData.getColumnName());

					columnIndex++;
				}

				// 3.Export the data
				if (dataSet.getRows() != null && dataSet.getRows().size() > 0) {
					short rowIndex = 1;

					for (Iterator<ConsoleRow> iterator = dataSet.getRows().iterator(); iterator.hasNext();) {
						ConsoleRow dataRow = iterator.next();
						columnIndex = 0;

						if (rowIndex > MAX_EXCEL_ROW_COUNT)
							break;

						HSSFRow row = sheet.createRow(rowIndex);

						for (Iterator<ConsoleData> iterator2 = dataRow.getDatas().iterator(); iterator2.hasNext();) {
							ConsoleData data = iterator2.next();
							HSSFCell cell = row.createCell(columnIndex);
//							cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);
							cell.setCellValue(data.getStringValue());

							columnIndex++;
						}
						rowIndex++;
					}
				}

				// 4.Save the file
				FileOutputStream fos = new FileOutputStream(url);
				workbook.write(fos);
				fos.close();
			}

			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
}
