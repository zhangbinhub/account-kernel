package OLink.bpm.core.dynaform.dts.excelimport.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import OLink.bpm.core.dynaform.dts.excelimport.ExcelMappingDiagram;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @author nicholas
 */
public class ImpExcelToDoc {

	private ImportProvider importProvider;
	


	public ImpExcelToDoc(String excelPath, ExcelMappingDiagram mapping) throws Exception {
		File file = new File(excelPath);
		FileInputStream fis = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(fis);

		if (excelPath.toLowerCase().endsWith(".xls")){
			importProvider = new XLSDocumentImprotProvider(new HSSFWorkbook(bis), mapping);
		} else if(excelPath.toLowerCase().endsWith(".xlsx")) {
			importProvider = new XLSXDocumentImprotProvider(new XSSFWorkbook(bis), mapping);
		}

	}
	
	public String creatDocument(WebUser user, ParamsTable params, String applicationid) throws Exception {
		return importProvider.creatDocument(user, params, applicationid);
	}



	public ImportProvider getImportProvider() {
		return importProvider;
	}

	public void setImportProvider(ImportProvider importProvider) {
		this.importProvider = importProvider;
	}




	
}
