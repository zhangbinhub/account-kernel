package OLink.bpm.util.excel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExcelWorkBook {
	public HSSFWorkbook workbook = null;

	// 设置当前workbookName
	private String workbookName = null;

	private HSSFSheet sheet = null;

	private FileOutputStream fileOut;

	public ExcelWorkBook() {
		workbook = new HSSFWorkbook();
	}

	public ExcelWorkBook(String workbookName) {
		this();
		setWorkbookName(workbookName);
	}

	public String getWorkbookName() {
		return workbookName;
	}

	public void setWorkbookName(String workbookName) {
		this.workbookName = workbookName;
	}

	/**
	 * @return
	 * @SuppressWarnings("deprecation") 使用了过时的POI API
	 */
	@SuppressWarnings("deprecation")
	public HSSFSheet getSheet() {
		if (sheet == null) {
			sheet = workbook.createSheet();
			workbook.setSheetName(0, workbookName);
		}
		return sheet;
	}

	public HSSFWorkbook getWorkbook(){
		return workbook;
	}
	
	/**
	 * 
	 * @param toDir
	 * @param excelName
	 */
	public void write(String destDir, String excelName) {
		File file = new File(destDir);
		if (!file.exists()) {
			boolean isok = file.mkdirs();
			if(isok)
				writerFileStream(destDir + "/" + excelName);
		}
	}
	
	/**
	 * 输入当前WorkBook为下载临时文件记录
	 * 
	 * @param excelName
	 */
	public void writerFileStream(String excelName) {
		try {
			fileOut = new FileOutputStream(excelName);
			workbook.write(fileOut);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOut.flush();
				fileOut.close();
				if (workbook != null) {
					workbook = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
