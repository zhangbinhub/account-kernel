package OLink.bpm.core.dynaform.dts.excelimport.config;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import OLink.bpm.base.action.ParamsTable;
import OLink.bpm.core.user.action.WebUser;

/**
 * Excel导入服务提供者 实现Excel文档数据导入动态表单的功能
 * @author Happy.Lau
 * 
 */
public interface ImportProvider {

	int getMasterSheetRowCount() throws Exception;

	Map<String, String> getMasterSheetRow(int row);

	Map<String, String> getDetailSheetValueList(String sheetName,
												String columnName, String matchValue);

	Collection<LinkedHashMap<String, String>> getDetailSheetRowCollection(
			String sheetName, String columnName, String matchValue)
			throws Exception;

	String creatDocument(WebUser user, ParamsTable params,
						 String applicationid) throws Exception;

	String getCellStringValue(Cell cell);

}
