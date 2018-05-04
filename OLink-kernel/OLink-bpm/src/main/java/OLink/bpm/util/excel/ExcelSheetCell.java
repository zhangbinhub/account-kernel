package OLink.bpm.util.excel;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;

public class ExcelSheetCell {
	private static HSSFRow row = null;

	private static HSSFCell cell = null;

	/**
	 * 用于产生当前excel标题
	 * 
	 * @param sheet
	 *            [当前工作表单]
	 * @param firstRowValue
	 *            [标题数组]
	 * @param style
	 *            [当前单元格风格]
	 * @SuppressWarnings("deprecation") 使用了过时的POI API           
	 */
	@SuppressWarnings("deprecation")
	public static void createCurrRowTitle(ExcelSheetRow sheetRow,
			ExcelWorkBook work, String[] firstRowValue, HSSFCellStyle style) {
		row = ExcelSheetRow.createCurrSheetTitle(work);
		for (int i = 0; i < firstRowValue.length; i++) {
			cell = row.createCell((short) i);
			cell.setCellStyle(style);
			//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
			cell.setCellValue(firstRowValue[i]);
		}
	}

	public static void createCurrRowTitle(ExcelSheetRow sheetRow,
			ExcelWorkBook work, String[] firstRowValue) {
		createCurrRowTitle(sheetRow, work, firstRowValue, work.getWorkbook()
				.createCellStyle());
	}

	/**
	 * 用于生成excel当前记录内容,标题除外
	 * 
	 * @param sheet
	 *            [当前工作表单]
	 * @param beanList
	 *            [当前数据列表,i=Object[]]
	 * @param style
	 *            [当前单元格风格]
	 */
	public static void createCurrRowRecord(ExcelSheetRow sheetRow,
			ExcelWorkBook work, List<Object[]> beanList, HSSFCellStyle style) {
		Object[] obj = null;
		for (int i = 0; i < beanList.size(); i++) {
			row = ExcelSheetRow.createCurrSheetRecord(work, i);
			obj = beanList.get(i);//
			if (obj != null) {
				createExcelCell(row, obj, style);
			}
		}
	}

	public static void createCurrRowRecord(ExcelSheetRow sheetRow,
			ExcelWorkBook work, List<Object[]> beanList) {
		createCurrRowRecord(sheetRow, work, beanList, work.getWorkbook()
				.createCellStyle());
	}

	/**
	 * 需要以数组的方式提供当前每条记录 通过数组自动判断有多少列,生成当前行
	 * @SuppressWarnings("deprecation") 使用了过时的POI API
	 */
	@SuppressWarnings("deprecation")
	private static void createExcelCell(HSSFRow row, Object[] obj,
			HSSFCellStyle style) {
		try {
			for (int i = 0; i < obj.length; i++) {
				try {
					if (obj[i].toString() != null) {

						cell = row.createCell((short) i);
						cell.setCellStyle(style);
						//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
						cell.setCellValue(obj[i].toString());
					}
				} catch (NullPointerException e) {
					continue;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
