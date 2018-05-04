package OLink.bpm.core.dynaform.dts.excelimport.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import OLink.bpm.core.dynaform.dts.excelimport.ExcelMappingDiagram;
import OLink.bpm.core.dynaform.dts.excelimport.MasterSheet;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Excel 2007以上版本的Excel导入服务提供者
 * @author Happy.Lau
 *
 */
public class XLSXDocumentImprotProvider extends AbstractImportProvider
		implements ImportProvider {

	public XLSXDocumentImprotProvider(Workbook workBook,
			ExcelMappingDiagram mappingConfig) {
		super(workBook, mappingConfig);
	}
	
	public Map<String, String> getMasterSheetRow(int row) {
		HashMap<String, String> map = new HashMap<String, String>();
		MasterSheet ms = getMappingConfig().getMasterSheet();

		XSSFSheet sheet = (XSSFSheet) getWorkBook().getSheet(ms.name);
		XSSFRow  sheetHeads = sheet.getRow(0);
		XSSFRow  rowCells = sheet.getRow(row);

		boolean flag = false;
		for (short i = 0; i < sheetHeads.getLastCellNum(); i++) {
			if (sheetHeads.getCell(i) != null && getCellStringValue(sheetHeads.getCell(i)) != null) {
				if (!map.containsKey(getCellStringValue(sheetHeads.getCell(i)))) {
					if (rowCells != null && sheetHeads.getCell(i) != null) {
						if (rowCells.getCell(i) != null
								&& rowCells.getCell(i).getCellType() != XSSFCell.CELL_TYPE_BLANK) {
							flag = true;
							map.put(getCellStringValue(sheetHeads.getCell(i)), getCellStringValue(rowCells.getCell(i)));
						} else {
							map.put(getCellStringValue(sheetHeads.getCell(i)), "");
						}

					} else {
						map.put(getCellStringValue(sheetHeads.getCell(i)), null);
					}
				}
			}
		}
		return flag ? map : null;

	}

	public int getMasterSheetRowCount() throws Exception {
		MasterSheet ms = getMappingConfig().getMasterSheet();

		XSSFSheet sheet = (XSSFSheet) getWorkBook().getSheet(ms.name);

		if (sheet == null) {
			throw new Exception(ms.name + " sheet table doesn't exist in your Excel file!");
		}
		return sheet.getLastRowNum() + 1;
	}

	public Map<String, String> getDetailSheetValueList(String sheetName, String columnName, String matchValue) {
		XSSFSheet sheet = (XSSFSheet) getWorkBook().getSheet(sheetName);
		XSSFRow  sheetHeads = sheet.getRow(0);

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		for (short row = 1; row < sheet.getLastRowNum(); row++) {

			XSSFRow  rowCells = sheet.getRow(row);

			for (short i = 0; i < sheetHeads.getLastCellNum(); i++) {
				if (sheetHeads.getCell(i) != null && getCellStringValue(sheetHeads.getCell(i)) != null) {

					if (!columnName.equals(getCellStringValue(sheetHeads.getCell(i))))
						continue;

					if (matchValue != null && matchValue.equals(getCellStringValue(rowCells.getCell(i)))) {

						for (short k = 0; k < sheetHeads.getLastCellNum(); k++) {
							String content = map.get(getCellStringValue(sheetHeads.getCell(k)));

							String value = getCellStringValue(rowCells.getCell(k));
							if (value == null || value.length() <= 0) {
								value = " ";
							}

							if (content != null) {
								content += value + ";";
							} else {
								content = value + ";";
							}

							map.put(getCellStringValue(sheetHeads.getCell(k)), content);

						}
					}

				}
			}

		}
		return map;
	}

	public Collection<LinkedHashMap<String, String>> getDetailSheetRowCollection(String sheetName, String columnName,
			String matchValue) throws Exception {
		ArrayList<LinkedHashMap<String, String>> list = new ArrayList<LinkedHashMap<String, String>>();
		XSSFSheet sheet = (XSSFSheet) getWorkBook().getSheet(sheetName);
		XSSFRow sheetHeads = sheet.getRow(0);

		for (int row = 1; row < sheet.getLastRowNum() + 1; row++) {
			try {
				XSSFRow  rowCells = sheet.getRow(row);

				for (short i = 0; i < sheetHeads.getLastCellNum(); i++) {
					try {
						if (sheetHeads.getCell(i) != null && getCellStringValue(sheetHeads.getCell(i)) != null) {

							if (!columnName.equals(getCellStringValue(sheetHeads.getCell(i))))
								continue;

							XSSFCell cell = null;
							try {
								cell = rowCells.getCell(i);
							} catch (Exception e) {
								continue;
							}
							if (matchValue == null || cell == null || getCellStringValue(cell) == null
									|| getCellStringValue(cell).length() <= 0) {
								continue;

							}

							if (matchValue.trim().equals(getCellStringValue(rowCells.getCell(i)).trim())) {

								LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
								for (short k = 0; k < sheetHeads.getLastCellNum(); k++) {

									String value = getCellStringValue(rowCells.getCell(k));
									if (value == null || value.length() <= 0) {
										value = "";
									}

									map.put(getCellStringValue(sheetHeads.getCell(k)), value);

								}
								list.add(map);
							}

						}

					} catch (Throwable e) {
						e.printStackTrace();
						throw new Exception("第 " + (i + 1) + " 列->" + e.getMessage());
					}
				}

			} catch (Throwable e) {
				throw new Exception(sheetName + " 第 " + (row + 1) + " 行->" + e.getMessage());
			}

		}
		return list;
	}
	

	
	

	/**
	 * 
	 * @SuppressWarnings("deprecation") 使用了过时的API
	 * @param cell
	 * @return
	 */
	public String getCellStringValue(Cell cell) {
		try {
			switch (cell.getCellType()) {
			case XSSFCell.CELL_TYPE_BLANK:
				return "";
			case XSSFCell.CELL_TYPE_BOOLEAN:
				return cell.getBooleanCellValue() + "";
			case XSSFCell.CELL_TYPE_ERROR:
				return "error";
			case XSSFCell.CELL_TYPE_FORMULA:
				return cell.getCellFormula();
			case XSSFCell.CELL_TYPE_NUMERIC:

				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd");
					return formate.format(cell.getDateCellValue());
				} else {
					double d = cell.getNumericCellValue();
					if (((int) d) == d) {
						return ((int) d) + "";
					} else {
						return d + "";
					}
				}

			case XSSFCell.CELL_TYPE_STRING:
				return cell.getStringCellValue().trim();

			default:
				return "";
			}
		} catch (Exception e) {
			return null;
		}
	}

}