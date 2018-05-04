package OLink.bpm.util.excel;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

public class ExcelSheetRow {
	public ExcelSheetRow() {
	}

	public static HSSFSheet sheet = null;

	/**
	 * 设置当前Sheet名字
	 */
	private static String sheetName = null;

	private static HSSFRow row = null;

	/**
	 * 创建当前标题行
	 * 
	 * @param sheet
	 * @return
	 */
	public static HSSFRow createCurrSheetTitle(ExcelWorkBook work) {
		HSSFSheet sheet = work.getSheet();
		row = sheet.createRow(0);
		return row;
	}

	/**
	 * 创建当前excel记录内容
	 * 
	 * @param sheet
	 * @param i
	 * @return
	 */
	public static HSSFRow createCurrSheetRecord(ExcelWorkBook work, int i) {
		HSSFSheet sheet = work.getSheet();
		row = sheet.createRow(i + 1);
		return row;
	}

	public static String getSheetName() {
		return sheetName;
	}

	public static void setSheetName(String sheetName) {
		ExcelSheetRow.sheetName = sheetName;
	}
}
